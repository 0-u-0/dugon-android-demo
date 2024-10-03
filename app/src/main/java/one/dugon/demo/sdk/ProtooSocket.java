package one.dugon.demo.sdk;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.*;
import okio.ByteString;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProtooSocket extends  WebSocketListener{

    private OkHttpClient client;
    private WebSocket webSocket;
    private Gson gson;
    private String sctpCapabilities = "{\n" +
            "            \"numStreams\":\n" +
            "            {\n" +
            "                \"OS\": 1024,\n" +
            "                \"MIS\": 1024\n" +
            "            }\n" +
            "        }";
    private String rtpCapabilities = "{\n" +
            "            \"codecs\":\n" +
            "            [\n" +
            "                {\n" +
            "                    \"mimeType\": \"audio/opus\",\n" +
            "                    \"kind\": \"audio\",\n" +
            "                    \"preferredPayloadType\": 100,\n" +
            "                    \"clockRate\": 48000,\n" +
            "                    \"channels\": 2,\n" +
            "                    \"parameters\":\n" +
            "                    {\n" +
            "                        \"minptime\": 10,\n" +
            "                        \"useinbandfec\": 1\n" +
            "                    },\n" +
            "                    \"rtcpFeedback\":\n" +
            "                    [\n" +
            "                        {\n" +
            "                            \"type\": \"transport-cc\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"nack\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"mimeType\": \"video/VP8\",\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"preferredPayloadType\": 101,\n" +
            "                    \"clockRate\": 90000,\n" +
            "                    \"parameters\":\n" +
            "                    {},\n" +
            "                    \"rtcpFeedback\":\n" +
            "                    [\n" +
            "                        {\n" +
            "                            \"type\": \"goog-remb\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"transport-cc\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"ccm\",\n" +
            "                            \"parameter\": \"fir\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"nack\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"nack\",\n" +
            "                            \"parameter\": \"pli\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"mimeType\": \"video/rtx\",\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"preferredPayloadType\": 102,\n" +
            "                    \"clockRate\": 90000,\n" +
            "                    \"parameters\":\n" +
            "                    {\n" +
            "                        \"apt\": 101\n" +
            "                    },\n" +
            "                    \"rtcpFeedback\":\n" +
            "                    []\n" +
            "                },\n" +
            "                {\n" +
            "                    \"mimeType\": \"video/VP9\",\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"preferredPayloadType\": 103,\n" +
            "                    \"clockRate\": 90000,\n" +
            "                    \"parameters\":\n" +
            "                    {\n" +
            "                        \"profile-id\": 2\n" +
            "                    },\n" +
            "                    \"rtcpFeedback\":\n" +
            "                    [\n" +
            "                        {\n" +
            "                            \"type\": \"goog-remb\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"transport-cc\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"ccm\",\n" +
            "                            \"parameter\": \"fir\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"nack\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"nack\",\n" +
            "                            \"parameter\": \"pli\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"mimeType\": \"video/rtx\",\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"preferredPayloadType\": 104,\n" +
            "                    \"clockRate\": 90000,\n" +
            "                    \"parameters\":\n" +
            "                    {\n" +
            "                        \"apt\": 103\n" +
            "                    },\n" +
            "                    \"rtcpFeedback\":\n" +
            "                    []\n" +
            "                },\n" +
            "                {\n" +
            "                    \"mimeType\": \"video/H264\",\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"preferredPayloadType\": 105,\n" +
            "                    \"clockRate\": 90000,\n" +
            "                    \"parameters\":\n" +
            "                    {\n" +
            "                        \"level-asymmetry-allowed\": 1,\n" +
            "                        \"packetization-mode\": 1,\n" +
            "                        \"profile-level-id\": \"4d001f\"\n" +
            "                    },\n" +
            "                    \"rtcpFeedback\":\n" +
            "                    [\n" +
            "                        {\n" +
            "                            \"type\": \"goog-remb\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"transport-cc\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"ccm\",\n" +
            "                            \"parameter\": \"fir\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"nack\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"nack\",\n" +
            "                            \"parameter\": \"pli\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"mimeType\": \"video/rtx\",\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"preferredPayloadType\": 106,\n" +
            "                    \"clockRate\": 90000,\n" +
            "                    \"parameters\":\n" +
            "                    {\n" +
            "                        \"apt\": 105\n" +
            "                    },\n" +
            "                    \"rtcpFeedback\":\n" +
            "                    []\n" +
            "                },\n" +
            "                {\n" +
            "                    \"mimeType\": \"video/H264\",\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"preferredPayloadType\": 107,\n" +
            "                    \"clockRate\": 90000,\n" +
            "                    \"parameters\":\n" +
            "                    {\n" +
            "                        \"level-asymmetry-allowed\": 1,\n" +
            "                        \"packetization-mode\": 1,\n" +
            "                        \"profile-level-id\": \"42e01f\"\n" +
            "                    },\n" +
            "                    \"rtcpFeedback\":\n" +
            "                    [\n" +
            "                        {\n" +
            "                            \"type\": \"goog-remb\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"transport-cc\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"ccm\",\n" +
            "                            \"parameter\": \"fir\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"nack\",\n" +
            "                            \"parameter\": \"\"\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"type\": \"nack\",\n" +
            "                            \"parameter\": \"pli\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"mimeType\": \"video/rtx\",\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"preferredPayloadType\": 108,\n" +
            "                    \"clockRate\": 90000,\n" +
            "                    \"parameters\":\n" +
            "                    {\n" +
            "                        \"apt\": 107\n" +
            "                    },\n" +
            "                    \"rtcpFeedback\":\n" +
            "                    []\n" +
            "                }\n" +
            "            ],\n" +
            "            \"headerExtensions\":\n" +
            "            [\n" +
            "                {\n" +
            "                    \"kind\": \"audio\",\n" +
            "                    \"uri\": \"urn:ietf:params:rtp-hdrext:sdes:mid\",\n" +
            "                    \"preferredId\": 1,\n" +
            "                    \"preferredEncrypt\": false,\n" +
            "                    \"direction\": \"sendrecv\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"uri\": \"urn:ietf:params:rtp-hdrext:sdes:mid\",\n" +
            "                    \"preferredId\": 1,\n" +
            "                    \"preferredEncrypt\": false,\n" +
            "                    \"direction\": \"sendrecv\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"kind\": \"audio\",\n" +
            "                    \"uri\": \"http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\",\n" +
            "                    \"preferredId\": 4,\n" +
            "                    \"preferredEncrypt\": false,\n" +
            "                    \"direction\": \"sendrecv\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"uri\": \"http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\",\n" +
            "                    \"preferredId\": 4,\n" +
            "                    \"preferredEncrypt\": false,\n" +
            "                    \"direction\": \"sendrecv\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"uri\": \"http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\",\n" +
            "                    \"preferredId\": 5,\n" +
            "                    \"preferredEncrypt\": false,\n" +
            "                    \"direction\": \"sendrecv\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"kind\": \"audio\",\n" +
            "                    \"uri\": \"urn:ietf:params:rtp-hdrext:ssrc-audio-level\",\n" +
            "                    \"preferredId\": 10,\n" +
            "                    \"preferredEncrypt\": false,\n" +
            "                    \"direction\": \"sendrecv\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"uri\": \"urn:3gpp:video-orientation\",\n" +
            "                    \"preferredId\": 11,\n" +
            "                    \"preferredEncrypt\": false,\n" +
            "                    \"direction\": \"sendrecv\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"uri\": \"urn:ietf:params:rtp-hdrext:toffset\",\n" +
            "                    \"preferredId\": 12,\n" +
            "                    \"preferredEncrypt\": false,\n" +
            "                    \"direction\": \"sendrecv\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"kind\": \"video\",\n" +
            "                    \"uri\": \"http://www.webrtc.org/experiments/rtp-hdrext/playout-delay\",\n" +
            "                    \"preferredId\": 14,\n" +
            "                    \"preferredEncrypt\": false,\n" +
            "                    \"direction\": \"sendrecv\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }";

    private Map<Integer, CompletableFuture<JsonObject>> pendingRequests;

    public ProtooSocket() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.pendingRequests = new ConcurrentHashMap<>();

    }

    public void connect(String url, Map<String, String> queryParams) {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (queryParams != null && !queryParams.isEmpty()) {
            urlBuilder.append("?");
            for (Map.Entry<String, String> param : queryParams.entrySet()) {
                urlBuilder.append(param.getKey()).append("=").append(param.getValue()).append("&");
            }
            urlBuilder.setLength(urlBuilder.length() - 1); // 移除最后一个多余的 '&'
        }



        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.toString())
                .header("Sec-WebSocket-Protocol", "protoo");


        Request request = requestBuilder.build();
        this.webSocket = client.newWebSocket(request, this);
    }

    public CompletableFuture<JsonObject> request(String method, JsonObject data) {
        int id = new Random().nextInt(Integer.MAX_VALUE);
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("request", true);
        requestJson.addProperty("id", id);
        requestJson.addProperty("method", method);
        requestJson.add("data", data);

        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        pendingRequests.put(id, future);

        String message = gson.toJson(requestJson);

        Log.d("W",message);

        send(message);

        return future;
    }


    public void send(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    public void send(ByteString byteString) {
        if (webSocket != null) {
            webSocket.send(byteString);
        }
    }

    public void close(int code, String reason) {
        if (webSocket != null) {
            webSocket.close(code, reason);
        }
    }

    public void cancel() {
        if (webSocket != null) {
            webSocket.cancel();
        }
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
//        super.onOpen(webSocket, response);
        Log.d("W","onOpen");
//        try {
            Future<JsonObject> r1 = request("getRouterRtpCapabilities", new JsonObject());
//            JsonObject rr1 = r1.get();

            JsonObject joinData = new JsonObject();
            JsonObject rtpCapabilitiesJson = JsonParser.parseString(rtpCapabilities).getAsJsonObject();
            JsonObject sctpCapabilitiesJson = JsonParser.parseString(sctpCapabilities).getAsJsonObject();
            JsonObject device = new JsonObject();
            device.addProperty("flag", "chrome");
            device.addProperty("name", "Chrome");
            device.addProperty("version", "129.0.0.0");

            joinData.add("device", device);
            joinData.add("rtpCapabilities", rtpCapabilitiesJson);
            joinData.add("sctpCapabilities", sctpCapabilitiesJson);
            joinData.addProperty("displayName", "gg");

            Future<JsonObject> r2 = request("join", joinData);

//
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
////            throw new RuntimeException(e);
//        }
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
//        super.onMessage(webSocket, bytes);
        Log.d("W","onMessage:");

    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
//        super.onMessage(webSocket, text);
        Log.d("W","??????");
    }


    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
//        super.onFailure(webSocket, t, response);
        Log.d("W","onFailure:"+t.toString());

    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
//        super.onClosing(webSocket, code, reason);
        Log.d("W","onClosing:");

    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
//        super.onClosed(webSocket, code, reason);
        Log.d("W","onClosed:");

    }
}