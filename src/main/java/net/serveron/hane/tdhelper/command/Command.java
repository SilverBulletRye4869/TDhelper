package net.serveron.hane.tdhelper.command;

import net.serveron.hane.tdhelper.CustomConfig;
import net.serveron.hane.tdhelper.TDhelper;
import net.serveron.hane.tdhelper.system.MainSystem;
import net.serveron.hane.tdhelper.system.TDgroup;
import net.serveron.hane.tdhelper.util.UtilSet;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Command implements CommandExecutor {
    private final JavaPlugin plugin;
    private final MainSystem MAIN_SYSTEM;

    public Command(JavaPlugin plugin,MainSystem mainSystem){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem;
        plugin.getCommand("tdh").setExecutor(this);
        plugin.getCommand("tdh").setTabCompleter(new Tab());
    }
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player) || !sender.hasPermission("tdh.admin"))return true;
        Player p = (Player) sender;
        if(args.length==0){
            //help

            return true;
        }

        String id = args.length >=2 ? args[1] : null;
        TDgroup tdg = id == null ? null : MAIN_SYSTEM.getTDunitByID(id);
        switch (args[0]){
            case "create": {
                if (id==null) return true;
                YamlConfiguration dataYml = CustomConfig.getYmlByID(TDhelper.NORMAL_DATA);
                if (dataYml.get(id) != null) {
                    UtilSet.sendPrefixMessage(p, "§cそのidのTextDisplayは既に存在します");
                    return true;
                }
                MAIN_SYSTEM.createIfNotExists(p.getLocation(),args[1],args.length>2 ? args[2] : ("TextDisplay ("+id+")"));
                UtilSet.sendPrefixMessage(p,"§aTextDisplay『§d"+args[1]+"§a』を作成しました");
                break;
            }

            case "addline":
                if (tdg == null) {
                    UtilSet.sendPrefixMessage(p, "§cそのidのTextDisplayが見つかりませんでした");
                    return true;
                }
                tdg.addLine(args.length < 3 ? null : args[2]);
                break;

            case "setline": {
                if (tdg == null) {
                    UtilSet.sendPrefixMessage(p, "§cそのidのTextDisplayが見つかりませんでした");
                    return true;
                }
                if (args.length < 4) {
                    UtilSet.sendPrefixMessage(p, "§c引数が足りません");
                    return true;
                }
                if (!args[2].matches("\\d+")) {
                    UtilSet.sendPrefixMessage(p, "§c行は整数で入力してください");
                    return true;
                }
                boolean res = tdg.setLine(Integer.parseInt(args[2]), args[3]);
                if (res) UtilSet.sendPrefixMessage(p, "§a正常に編集できました");
                else UtilSet.sendPrefixMessage(p, "§c編集に失敗しました。行数等を再度確認の上実行してください");
                break;
            }

            case "removeline":
                if (tdg == null) {
                    UtilSet.sendPrefixMessage(p, "§cそのidのTextDisplayが見つかりませんでした");
                    return true;
                }
                if(args.length<3)tdg.removeLine();
                else{
                    if(!args[2].matches("\\d+")){
                        UtilSet.sendPrefixMessage(p,"§c行数は整数で入力してください");
                        return true;
                    }
                    tdg.removeLine(Integer.parseInt(args[2]));
                }
                UtilSet.sendPrefixMessage(p,"§a正常に削除しました");
                break;

            case "insertline": {
                if (tdg == null) {
                    UtilSet.sendPrefixMessage(p, "§cそのidのTextDisplayが見つかりませんでした");
                    return true;
                }
                if (args.length < 4) {
                    UtilSet.sendPrefixMessage(p, "§c引数が足りません");
                    return true;
                }
                if (!args[2].matches("\\d+")) {
                    UtilSet.sendPrefixMessage(p, "§c行は整数で入力してください");
                    return true;
                }

                boolean res = tdg.insertLine(Integer.parseInt(args[2]), args[3]);
                if (res) UtilSet.sendPrefixMessage(p, "§a正常に編集できました");
                else UtilSet.sendPrefixMessage(p, "§c編集に失敗しました。行数等を再度確認の上実行してください");
                break;
            }

            case "view":
            case "info":
                tdg.view(p);
                break;

            case "movehere":
                tdg.teleport(p.getLocation());
                break;
            case "test":{
                YamlConfiguration dataYml = CustomConfig.getYmlByID(TDhelper.NORMAL_DATA);
                TextDisplay td = (TextDisplay) Bukkit.getEntity(UUID.fromString(dataYml.getString(args[1])));
                td.setText(args[2]);
            }


        }
        return true;
    }


    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            switch (args.length){
                case 1:
                    return Stream.of("create","addline","setline","insertline","removeline","view","movehere")
                            .filter(g->g.matches("^"+args[0]+".*"))
                            .collect(Collectors.toList());

                case 2:
                    switch (args[0]){
                        case "addline":
                        case "setline":
                        case "insertline":
                        case "removeline":
                        case "view":
                        case "movehere":
                            return CustomConfig.getYmlByID(TDhelper.NORMAL_DATA).getKeys(false).stream()
                                    .filter(g->g.matches("^"+args[1]+".*"))
                                    .collect(Collectors.toList());

                        default:
                            return List.of("");
                    }

                case 3:
                    switch (args[0]){
                        case "addline":
                            if(args[2].equals(""))return List.of("<テキスト>");
                            return null;
                        case "setline":
                        case "insertline":
                        case "removeline":
                            List<String> res = new ArrayList<>();
                            int size = MAIN_SYSTEM.getTDunitByID(args[1]).getSize()-1;
                            while(size>=0)res.add(String.valueOf(size--));
                            return res;
                        default:
                            return List.of("");
                    }

                case 4:
                    switch (args[0]){
                        case "setline":
                        case "insertline":
                        case "removeline":
                            if(args[3].equals(""))return List.of("<テキスト>");
                            return null;
                    }
            }
            return List.of("");
        }
    }
}
