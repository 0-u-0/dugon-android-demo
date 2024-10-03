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
            },
            'c', new Grammar[]{
                    new Grammar(
                            "connection",
                            "^IN IP(\\d) (\\S*)",
                            new String[]{"version", "ip"},
                            "IN IP%d %s"
                    )
            },
            'm',  new Grammar[]{
                    new Grammar(
                            "",
                            "^(\\w*) (\\d*) ([\\w/]*)(?: (.*))?",
                            new String[]{"type", "port", "protocol", "payloads"},
                            "IN IP%d %s"
                    )
            },
            'a',new Grammar[]{
                    new Grammar(
                            "",
                            "^rtpmap:(\\d*) ([\\w\\-.]*)(?:\\s*\\/(\\d*)(?:\\s*\\/(\\S*))?)?",
                            "rtp",
                            new String[]{"payload", "codec", "rate", "encoding"},
                            "IN IP%d %s"//TODO: fix
                    ),
                    new Grammar(
                            "",
                            "^fmtp:(\\d*) ([\\S| ]*)",
                            "fmtp",
                            new String[]{"payload", "config",},
                            "fmtp:%d %s"//TODO: fix
                    ),
                    new Grammar(
                            "",
                            "^rtcp:(\\d*)(?: (\\S*) IP(\\d) (\\S*))?",
                            "rtcp",
                            new String[]{"port", "netType", "ipVer", "address"},
                            "fmtp:%d %s"//TODO: fix
                    ),
                    new Grammar(
                            "",
                            "^rtcp-fb:(\\*|\\d*) ([\\w-_]*)(?: ([\\w-_]*))?",
                            "rtcpFb",
                            new String[]{"payload", "type", "subtype"},
                            "fmtp:%d %s"//TODO: fix
                    ),
                    new Grammar(
                            "",
                            "^extmap:(\\d+)(?:\\/(\\w+))?(?: (urn:ietf:params:rtp-hdrext:encrypt))? (\\S*)(?: (\\S*))?",
                            "ext",
                            new String[]{"value", "direction", "encrypt-uri", "uri", "config"},
                            "fmtp:%d %s"//TODO: fix
                    )


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
