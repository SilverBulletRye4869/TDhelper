package net.serveron.hane.tdhelper.system;

import net.serveron.hane.tdhelper.CustomConfig;
import net.serveron.hane.tdhelper.TDhelper;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpCharManager {
    public static final Set<String> SP_CHAR_SET = Set.of("players","max_players","ping","motd");
    public static final char ENCLOSE_CHAR = '%';
    public static final Set<Pattern> PATTERN_SET = SP_CHAR_SET.stream()
            .map(g->Pattern.compile(
                    ENCLOSE_CHAR+g+":[^%]*"+ENCLOSE_CHAR
            ))
            .collect(Collectors.toSet());



    public static Map<String, List<String>> checker(String txt){
        Map<String,List<String>> matchedMemo = new HashMap<>();
        YamlConfiguration y = null;
        PATTERN_SET.forEach(pattern->{
            Matcher matcher = pattern.matcher(txt);
            while (matcher.find()){
                String matched = matcher.group();
                int centerPos = matched.indexOf(":");
                String type = matched.substring(1,centerPos);
                if(matchedMemo.containsKey(type)) matchedMemo.get(type).add(matched.substring(centerPos+1,matched.length()-1));
                else matchedMemo.put(type,new ArrayList<>(){{add(matched.substring(centerPos+1,matched.length()-1));}});
            }
        });
        return matchedMemo;
    }

    public static void writeToYml(String key, String defaultTxt, Map<String,List<String>> spStatus){
        YamlConfiguration YML = CustomConfig.getYmlByID(TDhelper.SPECIAL_DATA);
        YML.set(key+".def_text",defaultTxt);
        YML.set(key+".sp_status",spStatus);
        CustomConfig.saveYmlByID(TDhelper.SPECIAL_DATA);
    }

    public static void deleteFromYml(String key){
        CustomConfig.getYmlByID(TDhelper.SPECIAL_DATA).set(key,null);
        CustomConfig.saveYmlByID(TDhelper.SPECIAL_DATA);
    }
}
