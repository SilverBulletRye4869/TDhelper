package net.serveron.hane.tdhelper.system;

import net.serveron.hane.tdhelper.CustomConfig;
import net.serveron.hane.tdhelper.util.UtilSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Text;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TDgroup {
    private static final YamlConfiguration YML = CustomConfig.getYmlByID("data");
    private static final double SPACE_SIZE = 0.3;

    private final JavaPlugin plugin;
    private final MainSystem MAIN_SYSTEM;
    private final String ID;
    private List<UUID> uuids;
    private boolean autoSave = true;

    public TDgroup(JavaPlugin plugin, MainSystem mainSystem, String id){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem;
        this.ID = id;
        uuids = YML.getStringList(ID+".uuids").stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public boolean setLine(int index,String text){
        TextDisplay td = getTD(index);
        if(td==null)return false;
        td.setText(text);
        return true;
    }

    public boolean addLine(){return addLine(null);}
    public boolean addLine(String init){
        TextDisplay td = getTD(getSize()-1);
        if(td==null || uuids == null)return false;
        Entity entity = Bukkit.getEntity(uuids.get(uuids.size()-1));

        TextDisplay newer = MAIN_SYSTEM.spawnNew(entity.getLocation().add(0,-SPACE_SIZE,0));
        uuids.add(newer.getUniqueId());
        if(init!=null)newer.setText(init);
        if(autoSave)save();
        return true;
    }

    public boolean insertLine(int index){return insertLine(index,null);}
    public boolean insertLine(int index,String init){
        if(index<0||index >= uuids.size())return false;
        for(int i = index;i<uuids.size();i++) {
            uuids.set(i+1,uuids.get(i));
            tpDown(i);
        }
        Location loc = Bukkit.getEntity(uuids.get(index+1)).getLocation().add(0,SPACE_SIZE,0);
        TextDisplay td = MAIN_SYSTEM.spawnNew(loc);
        if(init!=null)td.setText(init);
        uuids.set(index,td.getUniqueId());
        if(autoSave)save();
        return true;
    }

    public boolean removeLine(){return removeLine(getSize()-1);}
    public boolean removeLine(int index){
        if(uuids.size()<=index || index<0)return false;
        uuids.remove(index);
        for(int i = index;i<uuids.size();i++)tpUp(i);
        if(autoSave)save();
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
        for(UUID uuid : uuids){
            UtilSet.sendPrefixMessage(p,((TextDisplay)Bukkit.getEntity(uuid)).getText());
        }
        UtilSet.sendSuggestMessage(p,"§d[行を指定して編集]","/tdh setline "+ID+" ");
        UtilSet.sendSuggestMessage(p,"§d[行を追加]","/tdh addline "+ID+" ");
        UtilSet.sendSuggestMessage(p,"§d[行を削除]","/tdh removeline "+ID+" ");
        UtilSet.sendSuggestMessage(p,"§d[行を挿入]","/tdh insertline "+ID+" ");
    }

    public void teleport(Location loc){
        for(UUID uuid : uuids){
            Bukkit.getEntity(uuid).teleport(loc);
            loc.add(0,SPACE_SIZE,0);
        }
    }

    public List<UUID> getUUIDs(){
        return uuids;
    }

    public void setAutoSave(boolean status){autoSave = status;}

    public void reload(){
        this.uuids = YML.getStringList(ID+".uuids").stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public void save(){
        YML.set(ID+".uuids",uuids);
        CustomConfig.saveYmlByID("data");
    }


}
