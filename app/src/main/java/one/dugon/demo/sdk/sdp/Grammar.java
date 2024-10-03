package one.dugon.demo.sdk.sdp;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grammar {
    String name;
    String regex;
    String push;
    String[] names;
    String format;


    public static Map<Character, Grammar[]> data = Map.of(
            'v', new Grammar[]{
                    new Grammar("version", "^(\\d*)$")
            },
            'o', new Grammar[]{
                    new Grammar("origin", "^(\\S*) (\\d*) (\\d*) (\\S*) IP(\\d) (\\S*)",
                            new String[]{
                                    "username", "sessionId", "sessionVersion", "netType", "ipVer", "address"
                            }, "%s %s %d %s IP%d %s")
            },
            's', new Grammar[]{
                    new Grammar("name")
            },
            't', new Grammar[]{
                    new Grammar("timing", "^(\\d*) (\\d*)",
                            new String[]{"start", "stop"},
                            "%d %d")
            }
    );

    public Grammar(String name) {
        this(name, "");
    }

    public Grammar(String name, String regex) {
        this(name, regex, new String[]{});
    }

    public Grammar(String name, String regex, String[] names) {
        this(name, regex, "", names, "");
    }

    public Grammar(String name, String regex, String[] names, String format) {
        this(name, regex, "", names, format);
    }


    public Grammar(String name, String regex, String push, String[] names, String format) {
        this.name = name;
        this.regex = regex;
        this.push = push;
        this.names = names;
        this.format = format;
    }

    public boolean check(String content) {
        Matcher matcher = match(content);
        return matcher.find();
    }

    public Matcher match(String content) {
        String reg;
        if (regex.isEmpty()) {
            reg = content;
        } else {
            reg = regex;
        }
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(content);
    }

    public boolean needsBlank() {
        return !name.isEmpty() && names.length > 0;
    }
}
