package net.serveron.hane.tdhelper.system;

import net.serveron.hane.tdhelper.CustomConfig;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainSystem {
    private final JavaPlugin plugin;
    private final Map<String, TDgroup> TDgroupMap = new HashMap<>();
    private final YamlConfiguration YML;

    public MainSystem(JavaPlugin plugin){
        this.plugin = plugin;
        YML = CustomConfig.getYmlByID("data");
    }

    public TDgroup getTDunitByID(String id){
        if(!TDgroupMap.containsKey(id)){
            if(YML.get(id)==null)return null;
            TDgroupMap.put(id,new TDgroup(plugin,this,id));
        }
        return TDgroupMap.get(id);
    }

    public TDgroup createIfNotExists(Location loc, String id){return createIfNotExists(loc,id,null);}
    public TDgroup createIfNotExists(Location loc, String id, String init){
        if(TDgroupMap.containsKey(id))return TDgroupMap.get(id);
        TextDisplay td = this.spawnNew(loc);
        if(init!=null)td.setText(init);
        String uuidStr = td.getUniqueId().toString();
        YML.set(id+".uuids", List.of(uuidStr));
        CustomConfig.saveYmlByID("data");
        return getTDunitByID(id);
    }

    public void delete(String id){
        TDgroupMap.get(id).delete();
        TDgroupMap.remove(id);
    }

    public TextDisplay spawnNew(Location loc){
        TextDisplay td  = (TextDisplay) loc.getWorld().spawnEntity(loc, EntityType.TEXT_DISPLAY);
        td.setRotation(loc.getYaw(),0);
        return td;
    }
}

