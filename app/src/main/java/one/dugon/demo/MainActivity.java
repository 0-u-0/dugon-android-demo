package one.dugon.demo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.webrtc.RendererCommon;
import org.webrtc.RtpParameters;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import one.dugon.demo.sdk.Dugon;
import one.dugon.demo.sdk.LocalVideoSource;
import one.dugon.demo.sdk.RecvTransport;
import one.dugon.demo.sdk.protoo.ProtooSocket;
import one.dugon.demo.sdk.SendTransport;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SurfaceViewRenderer fullscreenRenderer;
    private SurfaceViewRenderer remoteRenderer;

    private LocalVideoSource localVideoSource;
    private ProtooSocket socket;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;


    private static SendTransport sendTransport;
    private static RecvTransport recvTransport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // 权限已经授予，继续你的操作
//            openCamera();
        }

//        Dugon.initialize(getApplication());
//        Dugon.getRtpCapabilities();
//
//        localVideoSource = Dugon.createVideoSource();
//        fullscreenRenderer = findViewById(R.id.fullscreen_video_view);
//        fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
//
//        Dugon.initView(fullscreenRenderer);
//        localVideoSource.play(fullscreenRenderer);
        Dugon.initialize(getApplication());
        //
        localVideoSource = Dugon.createVideoSource();
        fullscreenRenderer = findViewById(R.id.fullscreen_video_view);
        fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);

        remoteRenderer = findViewById(R.id.remote_video_view);
        remoteRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);

        remoteRenderer.setZOrderMediaOverlay(true);
        remoteRenderer.setEnableHardwareScaler(true /* enabled */);

        Dugon.initView(fullscreenRenderer);
        Dugon.initView(remoteRenderer);

        localVideoSource.play(fullscreenRenderer);

        soupTest();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意了权限请求
//                openCamera();
            } else {
                // 用户拒绝了权限请求
                Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void soupTest() {
        socket = new ProtooSocket();

        try {

            socket.onRequest = (requestData)->{
                var requestMethod = requestData.get("method").getAsString();
                if (Objects.equals(requestMethod, "newConsumer")) {
                    Log.d(TAG,"newConsumer");
                    var id = requestData.get("id").getAsInt();
                    var data = requestData.get("data").getAsJsonObject();
                    var kind = data.get("kind").getAsString();
                    var receiverId = data.get("id").getAsString();
                    var rtpParameters= data.get("rtpParameters").getAsJsonObject();

                    Dugon.executor.execute(()->{
                        var transceiver = recvTransport.receive(receiverId,kind,rtpParameters);
                        if(kind.equals("video")){
                            Log.d(TAG,"video!");
                            var track = transceiver.getReceiver().track();
                            var videotrack =  (VideoTrack)track;
                            videotrack.setEnabled(true);
                            videotrack.addSink(remoteRenderer);
                        }
                        socket.response(id);
                    });



                }
            };
            var f = socket.connect("ws://198.18.0.1:4443", Map.of("roomId", "dev", "peerId", "abc"));
//                var f = socket.connect("ws://192.168.1.103:4443", Map.of("roomId", "dev", "peerId", "abc"));
            f.get();
            var getRouterRtpCapabilitiesResponse = socket.request("getRouterRtpCapabilities");
            //-----
            Dugon.load(getRouterRtpCapabilitiesResponse);


            // for sender
            JsonObject senderCreateData = new JsonObject();
            senderCreateData.addProperty("consuming", false);
            senderCreateData.addProperty("forceTcp", false);
            senderCreateData.addProperty("producing", true);

            var rr3 = socket.request("createWebRtcTransport", senderCreateData);
            Log.d(TAG, rr3.toString());


            // for receiver
            JsonObject receiverCreateData = new JsonObject();
            receiverCreateData.addProperty("consuming", true);
            receiverCreateData.addProperty("forceTcp", false);
            receiverCreateData.addProperty("producing", false);

            JsonObject receiverResponseJson = socket.request("createWebRtcTransport", receiverCreateData);
            // recvTransport
            String recvId = receiverResponseJson.get("id").getAsString();
            JsonObject iceParameters2 = receiverResponseJson.getAsJsonObject("iceParameters");
            JsonArray iceCandidates2 = receiverResponseJson.getAsJsonArray("iceCandidates");
            JsonObject dtlsParameters2 = receiverResponseJson.getAsJsonObject("dtlsParameters");

            recvTransport = Dugon.createRecvTransport(recvId,iceParameters2,iceCandidates2,dtlsParameters2);
            recvTransport.onConnect = (JsonObject dtls)->{
                Log.d(TAG,"recvTransport dtls:"+dtls.toString());
                var connectData = new JsonObject();
                connectData.addProperty("transportId",recvId);
                connectData.add("dtlsParameters",dtls);
                var rr4 = socket.request("connectWebRtcTransport", connectData);
                try {
                    Log.d(TAG,"rr4 ok");
                } catch (Exception e) {
                    Log.d(TAG,"rr4 " + e.toString());

                    throw new RuntimeException(e);
                }
            };


            // join
            JsonObject joinData = new JsonObject();
            var rtpCapabilitiesJson = Dugon.rtpCapabilities;
            var sctpCapabilitiesJson = Dugon.sctpCapabilities;

            JsonObject device = new JsonObject();
            device.addProperty("flag", "chrome");
            device.addProperty("name", "Chrome");
            device.addProperty("version", "129.0.0.0");

            joinData.add("device", device);
            joinData.add("rtpCapabilities", rtpCapabilitiesJson);
            joinData.add("sctpCapabilities", sctpCapabilitiesJson);
            joinData.addProperty("displayName", "gg");

            var r2 = socket.request("join", joinData);

            // create sender
            String sendId = rr3.get("id").getAsString();
            JsonObject iceParameters = rr3.getAsJsonObject("iceParameters");
            JsonArray iceCandidates = rr3.getAsJsonArray("iceCandidates");
            JsonObject dtlsParameters = rr3.getAsJsonObject("dtlsParameters");

            sendTransport = Dugon.createSendTransport(sendId, iceParameters, iceCandidates, dtlsParameters);
            List<RtpParameters.Encoding> encodings = new ArrayList<>();

            sendTransport.onConnect = (JsonObject dtls)->{
                Log.d(TAG,"dtls:"+dtls.toString());
                var connectData = new JsonObject();
                connectData.addProperty("transportId",sendId);
                connectData.add("dtlsParameters",dtls);
                var r4 = socket.request("connectWebRtcTransport", connectData);

            };

            sendTransport.onProduce = (JsonObject pData)->{
                pData.addProperty("transportId",sendId);
                var produceResponse = socket.request("produce", pData);
                return produceResponse.get("id").getAsString();

            };
            sendTransport.send(localVideoSource.track, encodings);


        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}