package net.serveron.hane.tdhelper.command;

import net.serveron.hane.tdhelper.CustomConfig;
import net.serveron.hane.tdhelper.TDhelper;
import net.serveron.hane.tdhelper.system.MainSystem;
import net.serveron.hane.tdhelper.system.TDgroup;
import net.serveron.hane.tdhelper.util.UtilSet;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TdhCommand implements CommandExecutor, TabCompleter {
    private final MainSystem MAIN_SYSTEM;

    public TdhCommand(JavaPlugin plugin, MainSystem mainSystem){
        this.MAIN_SYSTEM = mainSystem;
        plugin.getCommand("tdh").setExecutor(this);
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if(!(sender instanceof Player p) || !sender.hasPermission("tdh.admin"))return true;
        if(args.length==0){
            sendHelp(p);
            return true;
        }

        String id = args.length >=2 ? args[1] : null;
        TDgroup tdg = id == null ? null : MAIN_SYSTEM.getTDgroupByID(id);
        switch (args[0]) {
            case "create" -> {
                if (id == null) return true;
                YamlConfiguration dataYml = CustomConfig.getYmlByID(TDhelper.NORMAL_DATA);
                if (dataYml.get(id) != null) {
                    UtilSet.sendPrefixMessage(p, "§cそのidのTextDisplayは既に存在します");
                    return true;
                }
                MAIN_SYSTEM.createIfNotExists(p.getLocation(), args[1], args.length > 2 ? UtilSet.connectStringWithSpace(args, 2) : ("TextDisplay (" + id + ")"));
                UtilSet.sendPrefixMessage(p, "§aTextDisplay『§d" + args[1] + "§a』を作成しました");
            }
            case "addline" -> {
                if (tdg == null) {
                    UtilSet.sendPrefixMessage(p, "§cそのidのTextDisplayが見つかりませんでした");
                    return true;
                }
                tdg.addLine(args.length < 3 ? null : UtilSet.connectStringWithSpace(args, 2));
            }
            case "setline" -> {
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
                boolean res = tdg.setLine(Integer.parseInt(args[2]), UtilSet.connectStringWithSpace(args, 3));
                if (res) UtilSet.sendPrefixMessage(p, "§a正常に編集できました");
                else UtilSet.sendPrefixMessage(p, "§c編集に失敗しました。行数等を再度確認の上実行してください");
            }
            case "removeline" -> {
                if (tdg == null) {
                    UtilSet.sendPrefixMessage(p, "§cそのidのTextDisplayが見つかりませんでした");
                    return true;
                }
                if (args.length < 3) tdg.removeLine();
                else {
                    if (!args[2].matches("\\d+")) {
                        UtilSet.sendPrefixMessage(p, "§c行数は整数で入力してください");
                        return true;
                    }
                    tdg.removeLine(Integer.parseInt(args[2]));
                }
                UtilSet.sendPrefixMessage(p, "§a正常に削除しました");
            }
            case "insertline" -> {
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

                boolean res = tdg.insertLine(Integer.parseInt(args[2]), UtilSet.connectStringWithSpace(args, 3));
                if (res) UtilSet.sendPrefixMessage(p, "§a正常に編集できました");
                else UtilSet.sendPrefixMessage(p, "§c編集に失敗しました。行数等を再度確認の上実行してください");
            }
            case "view", "info" -> tdg.view(p);
            case "movehere" -> {
                tdg.teleport(p.getLocation());
                UtilSet.sendPrefixMessage(p, "§aid:§d" + id + "§aのtdgを現在地に移動しました");
            }
            case "delete" -> {
                if (id == null) {
                    UtilSet.sendPrefixMessage(p, "§cそのidのTextDisplayは存在しません");
                    return true;
                }
                MAIN_SYSTEM.delete(id);
                UtilSet.sendPrefixMessage(p, "§a正常に削除しました");
            }
            default -> sendHelp(p);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String command, String[] args) {
        switch (args.length) {
            case 1 -> {
                return Stream.of("create", "addline", "setline", "insertline", "removeline", "view", "movehere")
                        .filter(g -> g.matches("^" + args[0] + ".*"))
                        .collect(Collectors.toList());
            }
            case 2 -> {
                return switch (args[0]) {
                    case "addline", "setline", "insertline", "removeline", "view", "movehere" ->
                            CustomConfig.getYmlByID(TDhelper.NORMAL_DATA).getKeys(false).stream()
                                    .filter(g -> g.matches("^" + args[1] + ".*"))
                                    .collect(Collectors.toList());
                    default -> List.of("");
                };
            }
            case 3 -> {
                switch (args[0]) {
                    case "addline" -> {
                        if (args[2].equals("")) return List.of("<テキスト>");
                        return null;
                    }
                    case "setline", "insertline", "removeline" -> {
                        List<String> res = new ArrayList<>();
                        int size = MAIN_SYSTEM.getTDgroupByID(args[1]).getSize() - 1;
                        while (size >= 0) res.add(String.valueOf(size--));
                        return res;
                    }
                    default -> {
                        return List.of("");
                    }
                }
            }
            case 4 -> {
                switch (args[0]) {
                    case "setline", "insertline", "removeline" -> {
                        if (args[3].equals("")) return List.of("<テキスト>");
                        return null;
                    }
                }
            }
        }
        return List.of("");
    }


    private void sendHelp(Player p){
        UtilSet.sendPrefixMessage(p,"§b------- TDhelper使い方 -------");
        UtilSet.sendSuggestMessage(p,"§6/tdh create <id> §a:指定したidのtdg作成", "/tdh create ");
        UtilSet.sendSuggestMessage(p,"§6/tdh addline <id> <text> §a:指定したtdgに1行追加","/tdh addline ");
        UtilSet.sendSuggestMessage(p,"§6/tdh setline <id> <line-num> <text> §a:指定した行の編集", "/tdh setline ");
        UtilSet.sendSuggestMessage(p,"§6/tdh removeline <id> [<line-num>] §a:指定した行または末尾の削除", "/tdh removeline");
        UtilSet.sendSuggestMessage(p,"§6/tdh insertline <id> <line-num> <text> §a:指定した行を挿入","/tdh insertline ");
        UtilSet.sendSuggestMessage(p,"§6/tdh movehere <id> §a:指定したtdgを現在地に移動","/tdh movehere ");
        UtilSet.sendSuggestMessage(p,"§6/tdh view <id> §a:指定したtdgの詳細表示","/tdh view ");
    }
}
