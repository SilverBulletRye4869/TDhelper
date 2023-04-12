package net.serveron.hane.tdhelper;

import net.serveron.hane.tdhelper.command.Command;
import net.serveron.hane.tdhelper.system.MainSystem;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class TDhelper extends JavaPlugin {
    public static final String NORMAL_DATA = "data";
    public static final String SPECIAL_DATA = "sp_data";

    private static JavaPlugin plugin = null;
    private static Logger log = null;
    private static MainSystem mainSystem = null;
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        log = getLogger();

        this.saveDefaultConfig();
        CustomConfig.createYmlByID(NORMAL_DATA);
        CustomConfig.createYmlByID(SPECIAL_DATA);

        mainSystem = new MainSystem(this);
        new Command(this,mainSystem);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static JavaPlugin getInstance(){return plugin;}
    public static Logger getLog(){return log;}
    public static MainSystem getSystem(){return mainSystem;}
}
