package net.serveron.hane.tdhelper.util;


import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.serveron.hane.tdhelper.TDhelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class UtilSet {
    private static final JavaPlugin plugin = TDhelper.getInstance();
    public static final String PREFIX = plugin.getConfig().getString("prefix","§e§l[§b§lSystem§e§l]");
    public static final ItemStack GUI_BG = createItem(Material.BLUE_STAINED_GLASS_PANE,"§r");
    public static final ItemStack NULL_BG = createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE,"§r");
    private static final Logger log = TDhelper.getLog();


    public static ItemStack createItem(Material m,String name){return createItem(m,name,null,0,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore){return createItem(m,name,lore,0,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore, Map<Enchantment,Integer> ench){return createItem(m,name,lore,0,ench);}
    public static ItemStack createItem(Material m, String name, List<String> lore, int model){return createItem(m,name,lore,model,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore, int model, Map<Enchantment,Integer> ench){
        ItemStack item = new ItemStack(m);
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta!=null){
            itemMeta.setDisplayName(name);
            if(lore!=null)itemMeta.setLore(lore);
            itemMeta.setCustomModelData(model);
            item.setItemMeta(itemMeta);
        }
        if(ench!=null)item.addUnsafeEnchantments(ench);
        return item;
    }

    public static void invFill(Inventory inv){invFill(inv,GUI_BG,false);}
    public static void invFill(Inventory inv,ItemStack item){invFill(inv,item,false);}
    public static void invFill(Inventory inv,ItemStack item,boolean isAppend){
        int size = inv.getSize();
        for(int i = 0;i<size;i++){
            if(isAppend && inv.getItem(i).getType() != Material.AIR)continue;
            inv.setItem(i,item);
        }
    }

    public static int[] getRectSlotPlaces(int start,int w,int h){
        int[] slotPlaces = new int[w*h];
        for(int i = 0;i<slotPlaces.length;i++)slotPlaces[i] = start + i % w + 9 * (i/w);
        return slotPlaces;
    }

    public static void sendPrefixMessage(Player p, String msg) {
        p.sendMessage(PREFIX + "§r" + msg);
    }

    public enum MessageType{INFO,WARNING,ERROR}
    public static void sendConsole(String msg){sendConsole(msg,MessageType.ERROR);}
    public static void sendConsole(String msg, MessageType type){
        switch (type) {
            case INFO:
                log.info(String.format("[%s] " + msg, plugin.getDescription().getName()));
                break;
            case WARNING:
                log.warning(String.format("[%s] " + msg, plugin.getDescription().getName()));
                break;
            case ERROR:
                log.severe(String.format("[%s] " + msg, plugin.getDescription().getName()));
        }
    }

    //サジェストメッセージ送信
    public static void sendSuggestMessage(Player p, String text, String command){
        TextComponent msg = new TextComponent(PREFIX + text);
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,command));
        p.spigot().sendMessage(msg);
    }

    //ﾗﾝコマンドメッセージを送信
    public static void sendRunCommandMessage(Player p, String text, String command){
        TextComponent msg = new TextComponent(PREFIX + text);
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        p.spigot().sendMessage(msg);
    }

    //アクションバーに表示
    public static void sendActionBar(Player p,String text){
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    public static void broadcast(String s){
        plugin.getServer().broadcastMessage(s);
    }

    /*
    public static int getNBT(String key, Entity e,EquipmentSlot type){
        return (int)getNBT_lf(key,e,type,-1);
    }
    public static int getNBT(String key, Entity e, EquipmentSlot type, int defaultNum){return (int)getNBT_lf(key,e,type,defaultNum);}

    public static double getNBT_lf(String key, Entity e,EquipmentSlot type, double defaultNum){
        ItemStack item = ((LivingEntity)e).getEquipment().getItem(type);
        if(item == null || item.getType()== Material.AIR) return defaultNum;
        NBTItem nbtItem = new NBTItem(item);
        if(nbtItem.hasKey(key))return nbtItem.getDouble(key);
        else return defaultNum;
    }

    public static String getNBT_s(String key, Entity e, EquipmentSlot type, String defaultStr){
        ItemStack item = ((LivingEntity)e).getEquipment().getItem(type);
        if(item == null || item.getType()== Material.AIR) return defaultStr;
        NBTItem nbtItem = new NBTItem(item);
        if(nbtItem.hasKey(key))return nbtItem.getString(key);
        else return defaultStr;
    }*/


    public static boolean ChanceOf(double chance){
        double r = Math.random() * 100;
        return r<chance;
    }

    public static Location LocationCPY(Location loc){
        return new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ(),loc.getYaw(),loc.getPitch());
    }

    //e1から見たe2の相対位置角度（極座標θ部分）を取得
    public static double getRelativeAngle(Entity e1, Entity e2){
        Location e1loc = e1.getLocation();
        Location e2loc = e2.getLocation();
        double theta = Math.abs(-Math.atan2(e2loc.getX() - e1loc.getX(),e2loc.getZ() - e1loc.getZ()) - e1loc.getYaw() / 180*Math.PI);
        return Math.min(2*Math.PI-theta,theta);
    }

    public static void sendEmptyMessage(Player p){sendEmptyMessage(p,1);}
    public static void sendEmptyMessage(Player p,int cnt){
        while (cnt-->0)UtilSet.sendPrefixMessage(p,"");
    }

    public static String connectStringWithSpace(String[] strings, int startIndex){return connectStringWithSpace(strings,startIndex,strings.length);}
    public static String connectStringWithSpace(String[] strings, int startIndex, int endIndex){
        return String.join(" ",Arrays.copyOfRange(strings,Math.min(startIndex,strings.length),Math.min(endIndex,strings.length)));
    }


}
