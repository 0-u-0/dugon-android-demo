package one.dugon.demo.sdk;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

interface SocketListener {
    void onConnected();
    void onNotification(JSONObject data);

    void onClosed();
}

interface RequestCallback {
    void cb(JSONObject data);
}

public class Socket {

    private static final String TAG = "Socket";
    private static final int TIMEOUT = 1000;

    private String url;
    private JSONObject params;
    private WebSocket ws;
    private OkHttpClient client;

    public SocketListener listener = null;

    Map<Integer, RequestCallback> callbacks = new HashMap<>();

    public Socket(String url, JSONObject params) {
        this.url = url;
        this.params = params;
        this.client = new OkHttpClient();
    }

    public static int randInt(int n) {
        int max = (int) Math.pow(10, n + 1);
        int min = (int) Math.pow(10, n);
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public void request(String event, Map<String, Object> data, RequestCallback callback) {
        int id = randInt(8);

        callbacks.put(id, callback);

        JSONObject requestJson = new JSONObject();
        JSONObject params = new JSONObject();
        JSONObject dataJson = new JSONObject(data);

        try {
            params.put("event", event);
            params.put("data", dataJson);

            requestJson.put("id", id);
            requestJson.put("method", "request");
            requestJson.put("params", params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ws.send(requestJson.toString());
    }

    private String getFullURL() {
        String url = null;
        try {
            Log.i(TAG, this.params.toString());
            String params = encryptBase64(this.params.toString());
            String encode = URLEncoder.encode(params, "UTF-8");

            url = String.format("%s?params=%s", this.url, encode);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    public void connect() {
        try {
            Request request = new Request.Builder().url(getFullURL()).build();
            ws = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    if (listener != null) {
                        listener.onConnected();
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    Log.i(TAG, text);
                    try {
                        JSONObject response = new JSONObject(text);
                        int id = response.getInt("id");
                        String method = response.getString("method");
                        JSONObject params = response.getJSONObject("params");

                        if (method.equals("response")) {
                            RequestCallback callback = callbacks.get(id);
                            if (callback != null) {
                                callback.cb(params);
                                callbacks.remove(id);
                            }
                        } else {
                            if (listener != null) {
                                listener.onNotification(params);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    if (listener != null) {
                        listener.onClosed();
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    Log.e(TAG, "WebSocket Error: " + t.getMessage());
                    if (listener != null) {
                        listener.onClosed();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encryptBase64(String str) {
        String res = null;
        try {
            res = new String(Base64.encode(str.getBytes("UTF-8"), Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }
}
