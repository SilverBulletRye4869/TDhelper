package net.serveron.hane.tdhelper.system;

import net.serveron.hane.tdhelper.CustomConfig;
import net.serveron.hane.tdhelper.TDhelper;
import net.serveron.hane.tdhelper.util.UtilSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TDgroup {
    private static final double SPACE_SIZE = 0.3;

    private final JavaPlugin plugin = TDhelper.getInstance();
    private final MainSystem MAIN_SYSTEM = TDhelper.getSystem();
    private final String ID;
    private List<UUID> uuids;
    private boolean autoSave = true;

    public TDgroup(String id){
        this.ID = id;
        uuids = CustomConfig.getYmlByID(TDhelper.NORMAL_DATA).getStringList(ID+".uuids").stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public String getLine(int index){
        if(index < 0 || index>=uuids.size())return null;
        return ((TextDisplay)Bukkit.getEntity(uuids.get(index))).getText();
    }

    public List<String> getLines(){
        return uuids.stream().map(Bukkit::getEntity).map(e->((TextDisplay)e).getText()).collect(Collectors.toList());
    }

    public boolean setLine(int index,String text){
        return write(uuids.get(index),text.replaceAll("&","§").replaceAll("§§","&"));
    }

    public boolean addLine(){return addLine(null);}
    public boolean addLine(String init){
        TextDisplay td = getTD(getSize()-1);
        if(td==null || uuids == null)return false;
        Entity entity = Bukkit.getEntity(uuids.get(uuids.size()-1));

        TextDisplay newer = MAIN_SYSTEM.spawnNew(entity.getLocation().add(0,-SPACE_SIZE,0));
        uuids.add(newer.getUniqueId());
        write(newer.getUniqueId(),init);
        if(autoSave)save();
        return true;
    }

    public boolean insertLine(int index){return insertLine(index,null);}
    public boolean insertLine(int index,String init){
        if(index<0||index >= uuids.size())return false;
        Location loc = Bukkit.getEntity(uuids.get(index)).getLocation();
        TextDisplay td = MAIN_SYSTEM.spawnNew(loc);
        write(td.getUniqueId(),init);
        for(int i = index;i<uuids.size();i++)tpDown(i);
        uuids.add(index,td.getUniqueId());
        if(autoSave)save();
        return true;
    }

    public boolean removeLine(){return removeLine(getSize()-1);}
    public boolean removeLine(int index){
        if(uuids.size()<=index || index<0)return false;

        if(getSize()==1)MAIN_SYSTEM.delete(ID);
        else {
            Bukkit.getEntity(uuids.get(index)).remove();
            uuids.remove(index);
            for (int i = index; i < uuids.size(); i++) tpUp(i);
            if (autoSave) save();
        }
        SpCharManager.deleteFromYml(uuids.get(index).toString());
        return true;
    }

    public int getSize(){
        return uuids==null ? -1 : uuids.size();
    }

    public TextDisplay getTD(){return getTD(0);}
    public TextDisplay getTD(int index){
        List<UUID> uuids = getUUIDs();
        if(uuids==null || uuids.size()<=index || index<0)return null;
        return Bukkit.getEntity(uuids.get(index)) instanceof TextDisplay ? (TextDisplay) Bukkit.getEntity(uuids.get(index)) : null;
    }

    public boolean tpDown(int index){
        if(index<0 ||index>=uuids.size())return false;
        Entity entity = Bukkit.getEntity(uuids.get(index));
        return entity.teleport(entity.getLocation().add(0,-SPACE_SIZE,0));
    }

    public boolean tpUp(int index){
        if(index<0 ||index>=uuids.size())return false;
        Entity entity = Bukkit.getEntity(uuids.get(index));
        return entity.teleport(entity.getLocation().add(0,SPACE_SIZE,0));
    }

    public void view(Player p){
        UtilSet.sendPrefixMessage(p,"§b-------- "+ID+"の情報 --------");
        UtilSet.sendRunCommandMessage(p,"§f§l"+getLocation_s(),"/tdh warp "+ID);
        UtilSet.sendEmptyMessage(p);

        YamlConfiguration YML_SP = CustomConfig.getYmlByID(TDhelper.SPECIAL_DATA);
        Set<String> spKeys= YML_SP.getKeys(false);
        for(UUID uuid : uuids){
            if(spKeys.contains(uuid.toString()))UtilSet.sendPrefixMessage(p,YML_SP.getString(uuid.toString()+".def_text"));
            else UtilSet.sendPrefixMessage(p,((TextDisplay)Bukkit.getEntity(uuid)).getText());
        }
        UtilSet.sendEmptyMessage(p);
        UtilSet.sendSuggestMessage(p,"§d[行を指定して編集]","/tdh setline "+ID+" ");
        UtilSet.sendSuggestMessage(p,"§d[行を追加]","/tdh addline "+ID+" ");
        UtilSet.sendSuggestMessage(p,"§d[行を削除]","/tdh removeline "+ID+" ");
        UtilSet.sendSuggestMessage(p,"§d[行を挿入]","/tdh insertline "+ID+" ");
        UtilSet.sendRunCommandMessage(p,"§c[現在地に移動]","/tdh movehere "+ID);
    }

    public void teleport(Location loc) {
        for (UUID uuid : uuids) {
            Entity entity = Bukkit.getEntity(uuid);
            entity.teleport(loc);
            entity.setRotation(loc.getYaw(), 0);
            loc.add(0, -SPACE_SIZE, 0);
        }
    }
    public void warp(Player p){
        p.teleport(getLocation());
    }

    public Location getLocation(){return getLocation(0);}
    public Location getLocation(int index){
        if(index < 0 || index>=uuids.size())return null;
        return Bukkit.getEntity(uuids.get(index)).getLocation();
    }

    public String getLocation_s(){return getLocation_s(0);}
    public String getLocation_s(int index){
        Location loc = getLocation(index);
        return loc.getWorld().getName()+" ("+String.format("%.1f",loc.getX())+", "+String.format("%.1f",loc.getY())+", "+String.format("%.1f",loc.getZ())+")";
    }

    public List<UUID> getUUIDs(){
        return uuids;
    }

    public void setAutoSave(boolean status){autoSave = status;}

    public void reload(){
        this.uuids = CustomConfig.getYmlByID(TDhelper.NORMAL_DATA).getStringList(ID+".uuids").stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public void save(){
        CustomConfig.getYmlByID(TDhelper.NORMAL_DATA).set(ID+".uuids",uuids.stream().map(UUID::toString).collect(Collectors.toList()));
        CustomConfig.saveYmlByID(TDhelper.NORMAL_DATA);
    }

    void delete(){
        uuids.stream().forEach(uuid->{
            Bukkit.getEntity(uuid).remove();
            CustomConfig.getYmlByID(TDhelper.SPECIAL_DATA).set(uuid.toString(),null);
        });
        CustomConfig.getYmlByID(TDhelper.NORMAL_DATA).set(ID,null);
        CustomConfig.saveYmlByID(TDhelper.SPECIAL_DATA);
        CustomConfig.saveYmlByID(TDhelper.NORMAL_DATA);
    }


    public static boolean write(UUID uuid,String txt){
        if(txt==null)return false;
        Entity entity = Bukkit.getEntity(uuid);
        if(entity == null || !(entity instanceof TextDisplay))return false;
        ((TextDisplay)entity).setText(txt);
        Map<String, List<String>> spMatched = SpCharManager.checker(txt);
        if(spMatched.size()>0) SpCharManager.writeToYml(uuid.toString(),txt,spMatched);
        else SpCharManager.deleteFromYml(uuid.toString());
        return true;
    }



}
