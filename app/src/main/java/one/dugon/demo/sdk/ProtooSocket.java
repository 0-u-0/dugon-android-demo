package one.dugon.demo.sdk;


import okhttp3.*;
import okio.ByteString;

import java.util.Map;

public class ProtooSocket {

    private OkHttpClient client;
    private WebSocket webSocket;
    private WebSocketListener listener;

    public ProtooSocket(WebSocketListener listener) {
        this.client = new OkHttpClient();
        this.listener = listener;
    }

    public void connect(String url, Map<String, String> queryParams) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        if (queryParams != null) {
            for (Map.Entry<String, String> param : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .header("Sec-WebSocket-Protocol", "protoo");


        Request request = requestBuilder.build();
        this.webSocket = client.newWebSocket(request, listener);
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

    public static void main(String[] args) {
        // 示例用法
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.out.println("WebSocket opened");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                System.out.println("Received message: " + text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                System.out.println("Received bytes: " + bytes.hex());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println("Closing: " + code + " / " + reason);
                webSocket.close(1000, null);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                System.out.println("Closed: " + code + " / " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                t.printStackTrace();
            }
        };

//        CustomWebSocket customWebSocket = new CustomWebSocket(listener);
//
//        customWebSocket.connect(
//                "wss://example.com/socket",
//                Map.of("param1", "value1", "param2", "value2"),
//                Map.of("Custom-Header", "HeaderValue")
//        );
    }
}