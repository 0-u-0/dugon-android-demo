package one.dugon.demo.sdk.sdp;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.util.regex.Matcher;

public class Parser {
    private static final String TAG = "Parser";

    public static void parse(String sdp) {
        JsonObject session = new JsonObject();
        JsonObject location = session;
        JsonArray media = new JsonArray();


        String[] sdpLines = sdp.split("\r\n");

        for (String line : sdpLines) {
            char type = line.charAt(0);
            String content = line.substring(2);

            Log.d(TAG, String.format("Type : %c, content: %s", type, content));

            if (type == 'm'){
                // TODO: 2024/10/3
                JsonObject m = new JsonObject();

                m.add("rtp", new JsonArray());
                m.add("fmtp", new JsonArray());

                media.add(m);

                location = media.get(media.size() - 1).getAsJsonObject();
            }

            if (Grammar.data.containsKey(type)) {
                Grammar[] grammars = Grammar.data.get(type);
                for (Grammar g : grammars) {
                    Matcher matcher = g.match(content);
                    if (matcher.find()) {
                        parseRegex(g, matcher ,location);
                        break;
                    }else{
                        Log.d(TAG,"Match failed:"+content);
                    }
                }
            }

        }

        session.add("media",media);

        Log.d(TAG,"result");
        Log.d(TAG,session.toString());
    }

    public static void parseRegex(Grammar g, Matcher matcher,JsonObject location) {
        if (!g.push.isEmpty() && !location.has(g.push)) {
            location.add(g.push, new JsonArray());
        } else if (g.needsBlank() && !location.has(g.name)) {
            location.add(g.name, new JsonObject());
        }
//
        JsonObject object = new JsonObject();
        JsonObject keyLocation = !g.push.isEmpty() ? object: g.needsBlank() ? location.getAsJsonObject(g.name) : location;

        attachProperties(matcher, keyLocation, g.names, g.name);

        if (!g.push.isEmpty()){
            location.getAsJsonArray(g.push).add(keyLocation);
        }
    }

    public static void addProperties(JsonObject location, String key, String input) {
        try {
            int result = Integer.parseInt(input);
            location.addProperty(key, result);
        } catch (NumberFormatException e) {
            location.addProperty(key, input);
        }
    }

    public static void attachProperties(Matcher matcher, JsonObject location, String[] names, String name) {
        Log.d(TAG, "attachProperties:"+name);
        if (!name.isEmpty() && names.length == 0) {
            addProperties(location, name, matcher.group());
        } else {
            for (int i = 0; i < names.length; i += 1) {
                addProperties(location, names[i], matcher.group(i+1));
            }
        }
    }
}
