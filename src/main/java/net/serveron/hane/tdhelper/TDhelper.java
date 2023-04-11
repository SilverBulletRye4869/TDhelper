package net.serveron.hane.tdhelper;

import net.serveron.hane.tdhelper.command.Command;
import net.serveron.hane.tdhelper.system.MainSystem;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class TDhelper extends JavaPlugin {
    private static JavaPlugin plugin = null;
    private static Logger log = null;
    private static MainSystem mainSystem = null;
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        log = getLogger();

        this.saveDefaultConfig();
        CustomConfig.createYmlByID("data");

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
