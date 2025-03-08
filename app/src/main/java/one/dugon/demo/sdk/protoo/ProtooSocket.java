package one.dugon.demo.sdk.protoo;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.*;
import okhttp3.Request;
import okio.ByteString;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ProtooSocket{
    private static final String TAG = "ProtooSocket";

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private OkHttpClient client;
    private WebSocket webSocket;
    private Gson gson;

    private Map<Integer, CompletableFuture<JsonObject>> pendingRequests;

    public Consumer<JsonObject> onRequest;

    public ProtooSocket() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.pendingRequests = new ConcurrentHashMap<>();
    }

    public CompletableFuture<Void> connect(String url, Map<String, String> queryParams) {
        CompletableFuture<Void> futureConnect = new CompletableFuture<>();

        StringBuilder urlBuilder = new StringBuilder(url);
        if (queryParams != null && !queryParams.isEmpty()) {
            urlBuilder.append("?");
            for (Map.Entry<String, String> param : queryParams.entrySet()) {
                urlBuilder.append(param.getKey()).append("=").append(param.getValue()).append("&");
            }
            urlBuilder.setLength(urlBuilder.length() - 1); // 移除最后一个多余的 '&'
        }

        Log.d(TAG,urlBuilder.toString());

        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                .url(urlBuilder.toString())
                .header("Sec-WebSocket-Protocol", "protoo");


        Request request = requestBuilder.build();

        executor.execute(()->{
            var listener = new WebSocketListener(){

                @Override
                public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
//        super.onOpen(webSocket, response);
                    Log.d(TAG,"onOpen");
                    futureConnect.complete(null);
//                    executor.execute(()-> {
//                        try {
//                            Future<JsonObject> r1 = request("getRouterRtpCapabilities", new JsonObject());
//                            JsonObject rr1 = r1.get();
//                            Log.d(TAG,"ok1");

//            JsonObject joinData = new JsonObject();
//            JsonObject rtpCapabilitiesJson = JsonParser.parseString(rtpCapabilities).getAsJsonObject();
//            JsonObject sctpCapabilitiesJson = JsonParser.parseString(sctpCapabilities).getAsJsonObject();
//            JsonObject device = new JsonObject();
//            device.addProperty("flag", "chrome");
//            device.addProperty("name", "Chrome");
//            device.addProperty("version", "129.0.0.0");

//            joinData.add("device", device);
//            joinData.add("rtpCapabilities", rtpCapabilitiesJson);
//            joinData.add("sctpCapabilities", sctpCapabilitiesJson);
//            joinData.addProperty("displayName", "gg");

//            Future<JsonObject> r2 = request("join", joinData);

//
//                        } catch (ExecutionException | InterruptedException e) {
//                            e.printStackTrace();
////            throw new RuntimeException(e);
//                        }
//                    });

                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
//        super.onMessage(webSocket, bytes);
                    Log.d("W","onMessage:");

                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
//        super.onMessage(webSocket, text);
                    Log.d(TAG,text);
                    JsonObject jsonObject = gson.fromJson(text, JsonObject.class);
                    if(jsonObject.has("response")){
                        ProtooResponse response = gson.fromJson(jsonObject, ProtooResponse.class);
                        var future = pendingRequests.get(response.id);
                        future.complete(response.data);
//            Log.d(TAG,response.toString());
                    } else if(jsonObject.has("notification")){

                    } else if(jsonObject.has("request")){
                        onRequest.accept(jsonObject);
                    }

                }


                @Override
                public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
//        super.onFailure(webSocket, t, response);
                    Log.d("W","onFailure:"+t.toString());
                    futureConnect.completeExceptionally(t);
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
            };
            this.webSocket = client.newWebSocket(request, listener);
        });

        return futureConnect;
    }

    public CompletableFuture<JsonObject> request(String method) {
        return request(method,new JsonObject());
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

        executor.execute(()-> {
            String message = gson.toJson(requestJson);

            Log.d(TAG,message);

            send(message);
        });


        return future;
    }

    public void request2(String method, JsonObject data) {
        int id = new Random().nextInt(Integer.MAX_VALUE);
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("request", true);
        requestJson.addProperty("id", id);
        requestJson.addProperty("method", method);
        requestJson.add("data", data);


        executor.execute(()-> {
            String message = gson.toJson(requestJson);

            Log.d(TAG,message);

            send(message);
        });

    }


    public void response(int id) {

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("id", id);
        responseJson.addProperty("response",true);
        responseJson.addProperty("ok",true);
        responseJson.add("data", new JsonObject());

        String message = gson.toJson(responseJson);

        Log.d(TAG,message);

        send(message);
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
//
//    @Override
//    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
////        super.onOpen(webSocket, response);
//        Log.d(TAG,"onOpen");
//
//        executor.execute(()-> {
//            try {
//                Future<JsonObject> r1 = request("getRouterRtpCapabilities", new JsonObject());
//                JsonObject rr1 = r1.get();
//                Log.d(TAG,"ok1");
//
////            JsonObject joinData = new JsonObject();
////            JsonObject rtpCapabilitiesJson = JsonParser.parseString(rtpCapabilities).getAsJsonObject();
////            JsonObject sctpCapabilitiesJson = JsonParser.parseString(sctpCapabilities).getAsJsonObject();
////            JsonObject device = new JsonObject();
////            device.addProperty("flag", "chrome");
////            device.addProperty("name", "Chrome");
////            device.addProperty("version", "129.0.0.0");
//
////            joinData.add("device", device);
////            joinData.add("rtpCapabilities", rtpCapabilitiesJson);
////            joinData.add("sctpCapabilities", sctpCapabilitiesJson);
////            joinData.addProperty("displayName", "gg");
//
////            Future<JsonObject> r2 = request("join", joinData);
//
////
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
////            throw new RuntimeException(e);
//            }
//        });
//
//    }
//
//    @Override
//    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
////        super.onMessage(webSocket, bytes);
//        Log.d("W","onMessage:");
//
//    }
//
//    @Override
//    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
////        super.onMessage(webSocket, text);
//        Log.d("W",text);
//        JsonObject jsonObject = gson.fromJson(text, JsonObject.class);
//        if(jsonObject.has("response")){
//            ProtooResponse response = gson.fromJson(jsonObject, ProtooResponse.class);
//            var future = pendingRequests.get(response.id);
//            future.complete(response.data);
////            Log.d(TAG,response.toString());
//        } else if(jsonObject.has("notification")){
//
//        }
//
//    }
//
//
//    @Override
//    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
////        super.onFailure(webSocket, t, response);
//        Log.d("W","onFailure:"+t.toString());
//
//    }
//
//    @Override
//    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
////        super.onClosing(webSocket, code, reason);
//        Log.d("W","onClosing:");
//
//    }
//
//    @Override
//    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
////        super.onClosed(webSocket, code, reason);
//        Log.d("W","onClosed:");
//
//    }
}