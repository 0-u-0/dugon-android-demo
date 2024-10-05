package one.dugon.demo.sdk.sdp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grammar {
    String name;
    String regex;
    String push;
    String[] names;
    //    String format;
    public Function<JsonElement, String> format;

    public static Map<Character, Grammar[]> data = Map.of(
            'v', new Grammar[]{
                    new Grammar("version", "^(\\d*)$")
            },
            'o', new Grammar[]{
                    new Grammar("origin", "^(\\S*) (\\d*) (\\d*) (\\S*) IP(\\d) (\\S*)",
                            new String[]{
                                    "username", "sessionId", "sessionVersion", "netType", "ipVer", "address"
                            }, x -> "%s %s %d %s IP%d %s")
            },
            's', new Grammar[]{
                    new Grammar("name")
            },
            't', new Grammar[]{
                    new Grammar("timing", "^(\\d*) (\\d*)",
                            new String[]{"start", "stop" },
                            x -> "%d %d")
            },

            'c', new Grammar[]{
                    new Grammar(
                            "connection",
                            "^IN IP(\\d) (\\S*)",
                            new String[]{"version", "ip" },
                            x -> "IN IP%d %s"
                    )
            },
            'b', new Grammar[]{
                    new Grammar("",
                            "^(TIAS|AS|CT|RR|RS):(\\d*)",
                            "bandwidth",
                            new String[]{"type", "limit" },
                            x -> "%s:%s")
            },
            'm', new Grammar[]{
                    new Grammar(
                            "",
                            "^(\\w*) (\\d*) ([\\w/]*)(?: (.*))?",
                            new String[]{"type", "port", "protocol", "payloads" },
                            x -> "%s %d %s %s"
                    )
            },
            'a', new Grammar[]{
                    new Grammar(
                            "",
                            "^rtpmap:(\\d*) ([\\w\\-.]*)(?:\\s*\\/(\\d*)(?:\\s*\\/(\\S*))?)?",
                            "rtp",
                            new String[]{"payload", "codec", "rate", "encoding" },
                            x -> x.getAsJsonObject().has("encoding")
                                    ? "rtpmap:%d %s/%s/%s"
                                    : x.getAsJsonObject().has("rate")
                                    ? "rtpmap:%d %s/%s"
                                    : "rtpmap:%d %s"
                    ),
                    new Grammar(
                            "",
                            "^fmtp:(\\d*) ([\\S| ]*)",
                            "fmtp",
                            new String[]{"payload", "config",},
                            x-> "fmtp:%d %s"
                    ),
                    new Grammar(
                            "",
                            "^rtcp:(\\d*)(?: (\\S*) IP(\\d) (\\S*))?",
                            "rtcp",
                            new String[]{"port", "netType", "ipVer", "address" },
                            x-> (x.getAsJsonObject().has("address") && !x.getAsJsonObject().get("addresss").getAsString().isEmpty())
                                    ? "rtcp:%d %s IP%d %s"
                                    : "rtcp:%d"
                    ),
                    new Grammar(
                            "",
                            "^rtcp-fb:(\\*|\\d*) ([\\w-_]*)(?: ([\\w-_]*))?",
                            "rtcpFb",
                            new String[]{"payload", "type", "subtype" },
                            x->"fmtp:%d %s"//TODO: fix
                    ),
                    new Grammar(
                            "",
                            "^extmap:(\\d+)(?:\\/(\\w+))?(?: (urn:ietf:params:rtp-hdrext:encrypt))? (\\S*)(?: (\\S*))?",
                            "ext",
                            new String[]{"value", "direction", "encrypt-uri", "uri", "config" },
                            x->"fmtp:%d %s"//TODO: fix
                    )


            }
    );

    public Grammar(String name) {
        this(name, "(.*)");
    }

    public Grammar(String name, String regex) {
        this(name, regex, new String[]{});
    }

    public Grammar(String name, String regex, String[] names) {
        this(name, regex, "", names, x -> "%s");
    }

    public Grammar(String name, String regex, String[] names, Function<JsonElement, String> format) {
        this(name, regex, "", names, format);
    }


    public Grammar(String name, String regex, String push, String[] names, Function<JsonElement, String> format) {
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
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(content);
    }

    public boolean needsBlank() {
        return !name.isEmpty() && names.length > 0;
    }
}
