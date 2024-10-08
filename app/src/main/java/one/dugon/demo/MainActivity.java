package one.dugon.demo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonObject;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import one.dugon.demo.sdk.Dugon;
import one.dugon.demo.sdk.LocalVideoSource;
import one.dugon.demo.sdk.ProtooSocket;
import one.dugon.demo.sdk.Session;
import one.dugon.demo.sdk.Transport;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Session session;
    private Transport transport;
    private SurfaceViewRenderer fullscreenRenderer;
    private LocalVideoSource localVideoSource;
    private ProtooSocket socket;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

    public void soupTest(){
        socket = new ProtooSocket();
        try {
            Dugon.initialize(getApplication());

            var f = socket.connect("ws://192.168.82.107:4443",Map.of("roomId","vm7khrqj","peerId","abc"));
            f.get();
            var r1 = socket.request("getRouterRtpCapabilities");
            JsonObject rr1 = r1.get();
            Dugon.load(rr1);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}