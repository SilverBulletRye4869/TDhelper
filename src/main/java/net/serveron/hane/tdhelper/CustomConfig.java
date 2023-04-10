package net.serveron.hane.tdhelper;


import net.serveron.hane.tdhelper.util.UtilSet;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CustomConfig {
    private static Map<String, YamlConfiguration> config = new HashMap<>();
    private static JavaPlugin plugin = TDhelper.getInstance();
    private static HashSet<String> existSet = new HashSet<>();

    public static YamlConfiguration getYmlByID(String name) {
        if(!config.containsKey(name)){
            if(!reloadYmlByID(name))return null;
        }
        return config.get(name);
    }

    public static boolean existYml(String name){
        if(existSet.contains(name))return true;
        if(new File(plugin.getDataFolder(),name+".yml").exists()){existSet.add(name);return true;}
        return false;
    }

    public static YamlConfiguration createYmlByID(String name){
        File file = new File(plugin.getDataFolder(),name+".yml");
        try {
            if(!file.exists()) file.createNewFile();
            else UtilSet.sendConsole(name+".ymlは既に存在していたため作成されませんでした。");
        }catch (IOException e){
            UtilSet.sendConsole(name+".ymlの作成に失敗しました");
            e.printStackTrace();
            return null;
        }
        return getYmlByID(name);
    }

    static boolean deleteYmlByID(String name){
        File file = new File(plugin.getDataFolder(),name+".yml");
        boolean result = file.delete();
        if(result){
            config.remove(name);
            existSet.remove(name);
        }
        return result;
    }

    public static boolean reloadYmlByID(String name){
        File file = new File(plugin.getDataFolder(),name+".yml");
        if(!file.exists())return false;
        YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
        config.put(name,y);
        return true;
    }

    public static void saveYmlByID(String name){
        try{
            config.get(name).save(new File(plugin.getDataFolder(),name + ".yml"));
        }catch (IOException e){
            UtilSet.sendConsole(name+".ymlの保存に失敗しました");
        }
    }
}

