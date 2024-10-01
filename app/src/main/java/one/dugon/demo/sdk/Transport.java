package one.dugon.demo.sdk;

import android.content.Context;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.Nullable;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CandidatePairChangeEvent;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.IceCandidateErrorEvent;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Transport implements PeerConnection.Observer{

//    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
//
//    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
//
//    private static final String TAG = "Transport";
//
//    private final Context appContext;
//    private final EglBase rootEglBase;
//
//    @Nullable private SurfaceTextureHelper surfaceTextureHelper;
//    @Nullable private VideoSource videoSource;
//    private VideoTrack localVideoTrack;
//
//    private MediaConstraints sdpMediaConstraints;
//
//    @Nullable
//    private VideoSink localRender;
//
//    @Nullable
//    private PeerConnectionFactory factory;
//
//    @Nullable
//    private VideoCapturer videoCapturer;
//
//    private final PCObserver pcObserver = new PCObserver();
//    private final SDPObserver sdpObserver = new SDPObserver();

    @Nullable
    private PeerConnection pc;

    public Transport()  {

    }

    public void start(PeerConnection peerConnection){
        pc = peerConnection;
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {

    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {

    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {

    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

    }

    @Override
    public void onAddStream(MediaStream mediaStream) {

    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {

    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {

    }

    @Override
    public void onRenegotiationNeeded() {

    }
//
//    // Implementation detail: observe ICE & stream changes and react accordingly.
//    private class PCObserver implements PeerConnection.Observer {
//        @Override
//        public void onIceCandidate(final IceCandidate candidate) {
////            executor.execute(() -> events.onIceCandidate(candidate));
//        }
//
//        @Override
//        public void onIceCandidateError(final IceCandidateErrorEvent event) {
//            Log.d(TAG,
//                    "IceCandidateError address: " + event.address + ", port: " + event.port + ", url: "
//                            + event.url + ", errorCode: " + event.errorCode + ", errorText: " + event.errorText);
//        }
//
//        @Override
//        public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
////            executor.execute(() -> events.onIceCandidatesRemoved(candidates));
//        }
//
//        @Override
//        public void onSignalingChange(PeerConnection.SignalingState newState) {
//            Log.d(TAG, "SignalingState: " + newState);
//        }
//
//        @Override
//        public void onIceConnectionChange(final PeerConnection.IceConnectionState newState) {
//            executor.execute(() -> {
//                Log.d(TAG, "IceConnectionState: " + newState);
//                if (newState == PeerConnection.IceConnectionState.CONNECTED) {
////                    events.onIceConnected();
//                } else if (newState == PeerConnection.IceConnectionState.DISCONNECTED) {
////                    events.onIceDisconnected();
//                } else if (newState == PeerConnection.IceConnectionState.FAILED) {
////                    reportError("ICE connection failed.");
//                }
//            });
//        }
//
//        @Override
//        public void onConnectionChange(final PeerConnection.PeerConnectionState newState) {
//            executor.execute(() -> {
//                Log.d(TAG, "PeerConnectionState: " + newState);
//                if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
////                    events.onConnected();
//                } else if (newState == PeerConnection.PeerConnectionState.DISCONNECTED) {
////                    events.onDisconnected();
//                } else if (newState == PeerConnection.PeerConnectionState.FAILED) {
////                    reportError("DTLS connection failed.");
//                }
//            });
//        }
//
//        @Override
//        public void onIceGatheringChange(PeerConnection.IceGatheringState newState) {
//            Log.d(TAG, "IceGatheringState: " + newState);
//        }
//
//        @Override
//        public void onIceConnectionReceivingChange(boolean receiving) {
//            Log.d(TAG, "IceConnectionReceiving changed to " + receiving);
//        }
//
//        @Override
//        public void onSelectedCandidatePairChanged(CandidatePairChangeEvent event) {
//            Log.d(TAG, "Selected candidate pair changed because: " + event);
//        }
//
//        @Override
//        public void onAddStream(final MediaStream stream) {}
//
//        @Override
//        public void onRemoveStream(final MediaStream stream) {}
//
//        @Override
//        public void onDataChannel(final DataChannel dc) {
//            Log.d(TAG, "New Data channel " + dc.label());
//        }
//
//        @Override
//        public void onRenegotiationNeeded() {
//            // No need to do anything; AppRTC follows a pre-agreed-upon
//            // signaling/negotiation protocol.
//        }
//
//        @Override
//        public void onAddTrack(final RtpReceiver receiver, final MediaStream[] mediaStreams) {}
//
//        @Override
//        public void onRemoveTrack(final RtpReceiver receiver) {}
//    }
//
//
//    public void createPeerConnection(final VideoSink localRender, final VideoSink remoteSink
//                                    ) {
//    this.videoCapturer = createVideoCapturer();
//
////        if (peerConnectionParameters.videoCallEnabled && videoCapturer == null) {
////            Log.w(TAG, "Video call enabled but no video capturer provided.");
////        }
////        createPeerConnection(
////                localRender, Collections.singletonList(remoteSink), videoCapturer, signalingParameters);
//    }
////
//    public void createPeerConnection(final VideoSink localRender
////                                     final List<VideoSink> remoteSinks,
//                                    ) {
//
//        this.localRender = localRender;
//        this.videoCapturer = createVideoCapturer();
//
//        executor.execute(() -> {
//            try {
////                createMediaConstraintsInternal();
//                createPeerConnectionInternal();
//            } catch (Exception e) {
//                throw e;
//            }
//        });
//    }
//
//
//    @Nullable
//    private VideoTrack createVideoTrack(VideoCapturer capturer) {
//        surfaceTextureHelper =
//                SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
//        videoSource = factory.createVideoSource(capturer.isScreencast());
//        capturer.initialize(surfaceTextureHelper, appContext, videoSource.getCapturerObserver());
//        capturer.startCapture(720, 1280, 30);
//
//        localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
//        localVideoTrack.setEnabled(true);
//        localVideoTrack.addSink(localRender);
//        return localVideoTrack;
//    }
//
//
//    private void createPeerConnectionInternal() {
//
//        Log.d(TAG, "Create peer connection.");
//
//        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
//
//        PeerConnection.RTCConfiguration rtcConfig =
//                new PeerConnection.RTCConfiguration(iceServers);
//        // TCP candidates are only useful when connecting to a server that supports
//        // ICE-TCP.
//        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
//        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
//        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
//        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
//        // Use ECDSA encryption.
//        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
//        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;
//
//        assert factory != null;
//        peerConnection = factory.createPeerConnection(rtcConfig, pcObserver);
//
//
//        // Set INFO libjingle logging.
//        // NOTE: this _must_ happen while `factory` is alive!
//        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO);
//
//        List<String> mediaStreamLabels = Collections.singletonList("ARDAMS");
//
//        peerConnection.addTrack(createVideoTrack(videoCapturer), mediaStreamLabels);
//
////        peerConnection.addTrack(createAudioTrack(), mediaStreamLabels);
////        if (isVideoCallEnabled()) {
////            findVideoSender();
////        }
//
//        // Create SDP constraints.
//        sdpMediaConstraints = new MediaConstraints();
//        peerConnection.createOffer(sdpObserver, sdpMediaConstraints);
//
//        Log.d(TAG, "Peer connection created.");
//    }
//
//    private class SDPObserver implements SdpObserver {
//        @Override
//        public void onCreateSuccess(final SessionDescription desc) {
//            Log.d(TAG, "onCreateSuccess");
////            if (localDescription != null) {
////                reportError("Multiple SDP create.");
////                return;
////            }
////            String sdp = desc.description;
////            if (preferIsac) {
////                sdp = preferCodec(sdp, AUDIO_CODEC_ISAC, true);
////            }
////            if (isVideoCallEnabled()) {
////                sdp = preferCodec(sdp, getSdpVideoCodecName(peerConnectionParameters), false);
////            }
////            final SessionDescription newDesc = new SessionDescription(desc.type, sdp);
////            localDescription = newDesc;
////            executor.execute(() -> {
////                if (peerConnection != null && !isError) {
////                    Log.d(TAG, "Set local SDP from " + desc.type);
////                    peerConnection.setLocalDescription(sdpObserver, newDesc);
////                }
////            });
//        }
//
//        @Override
//        public void onSetSuccess() {
//            executor.execute(() -> {
////                if (peerConnection == null || isError) {
////                    return;
////                }
////                if (isInitiator) {
////                    // For offering peer connection we first create offer and set
////                    // local SDP, then after receiving answer set remote SDP.
////                    if (peerConnection.getRemoteDescription() == null) {
////                        // We've just set our local SDP so time to send it.
////                        Log.d(TAG, "Local SDP set succesfully");
////                        events.onLocalDescription(localDescription);
////                    } else {
////                        // We've just set remote description, so drain remote
////                        // and send local ICE candidates.
////                        Log.d(TAG, "Remote SDP set succesfully");
////                        drainCandidates();
////                    }
////                } else {
////                    // For answering peer connection we set remote SDP and then
////                    // create answer and set local SDP.
////                    if (peerConnection.getLocalDescription() != null) {
////                        // We've just set our local SDP so time to send it, drain
////                        // remote and send local ICE candidates.
////                        Log.d(TAG, "Local SDP set succesfully");
////                        events.onLocalDescription(localDescription);
////                        drainCandidates();
////                    } else {
////                        // We've just set remote SDP - do nothing for now -
////                        // answer will be created soon.
////                        Log.d(TAG, "Remote SDP set succesfully");
////                    }
////                }
//            });
//        }
//
//        @Override
//        public void onCreateFailure(final String error) {
////            reportError("createSDP error: " + error);
//        }
//
//        @Override
//        public void onSetFailure(final String error) {
////            reportError("setSDP error: " + error);
//        }
//    }

}
