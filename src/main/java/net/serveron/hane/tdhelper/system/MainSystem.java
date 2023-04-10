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
    private static final YamlConfiguration YML = CustomConfig.getYmlByID("data");

    private final JavaPlugin plugin;
    private final Map<String, TDgroup> TDgroupMap = new HashMap<>();

    public MainSystem(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public TDgroup getTDunitByID(String id){
        if(!TDgroupMap.containsKey(id)){
            if(YML.get(id)==null)return null;
            TDgroupMap.put(id,new TDgroup(plugin,this,id));
        }
        return TDgroupMap.get(id);
    }

    public TDgroup createIfNotExists(Location loc, String id){
        if(TDgroupMap.containsKey(id))return TDgroupMap.get(id);
        String uuidStr = this.spawnNew(loc).getUniqueId().toString();
        YML.set(id+".uuids", List.of(uuidStr));
        CustomConfig.saveYmlByID("data");
        return getTDunitByID(id);
    }

    public TextDisplay spawnNew(Location loc){
        return (TextDisplay) loc.getWorld().spawnEntity(loc, EntityType.TEXT_DISPLAY);
    }
}

