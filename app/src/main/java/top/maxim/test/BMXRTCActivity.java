package top.maxim.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;


import im.floo.floolib.BMXErrorCode;
import im.floo.floolib.BMXRTCEngine;
import im.floo.floolib.BMXRTCEngineListener;
import im.floo.floolib.BMXRoomAuth;
import im.floo.floolib.BMXStream;
import im.floo.floolib.BMXVideoCanvas;
import im.floo.floolib.BMXVideoMediaType;
import top.maxim.rtc.view.RTCRenderView;

public class BMXRTCActivity extends AppCompatActivity {

    private FrameLayout mContainer;

    private RTCRenderView mRemoteView;

    private RTCRenderView mLocalView;

    private BMXRTCEngine engine;

    public static void openVideoCall(Context context) {
        context.startActivity(new Intent(context, BMXRTCActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtc);
        mContainer = findViewById(R.id.video_container);
        mLocalView = findViewById(R.id.local_view);
        mLocalView.init();
        engine = RTCManager.getInstance().getRTCEngine();
        engine.addRTCEngineListener(new BMXRTCEngineListener() {

            @Override
            public void onJoinRoom(String info, String roomId, BMXErrorCode error) {
                if (error != null && error.swigValue() == 0) {
                    engine.publish(BMXVideoMediaType.Camera, true, true);
                    Log.e("aaaaaaa", "加入房间成功 开启发布本地流, roomId= " + roomId + "msg = " + info);
                } else {
                    Log.e("aaaaaaa", "加入房间失败 roomId= " + roomId + "msg = " + info);
                }
            }

            @Override
            public void onLeaveRoom(String info, String roomId, BMXErrorCode error, String reason) {
                if (error != null && error.swigValue() == 0) {
                    Log.e("aaaaaaa", "离开房间成功 roomId= " + roomId + "msg = " + reason);
                } else {
                    Log.e("aaaaaaa", "离开房间失败 roomId= " + roomId + "msg = " + reason);
                }
            }

            @Override
            public void onReJoinRoom(String info, String roomId, BMXErrorCode error) {
            }

            @Override
            public void onMemberJoined(String roomId, long usedId) {
                Log.e("aaaaaaa", "远端用户加入 uid= " + usedId);
            }

            @Override
            public void onMemberExited(String roomId, long usedId, String reason) {
                Log.e("aaaaaaa", "远端用户离开 uid= " + usedId);
                mContainer.removeAllViews();
            }

            @Override
            public void onLocalPublish(BMXStream stream, String info, BMXErrorCode error) {
                if (error != null && error.swigValue() == 0) {
                    BMXVideoCanvas canvas = new BMXVideoCanvas();
                    canvas.setMView(mLocalView.getObtainView());
                    canvas.setMStream(stream);
                    engine.startPreview(canvas);
                    Log.e("aaaaaaa", "发布本地流成功 开启预览 msg = " + info);
                } else {
                    Log.e("aaaaaaa", "发布本地流失败 msg = " + info);
                }
            }

            @Override
            public void onLocalUnPublish(BMXStream stream, String info, BMXErrorCode error) {
                if (error != null && error.swigValue() == 0) {
                    Log.e("aaaaaaa", "停止发布本地流成功 msg = " + info);
                } else {
                    Log.e("aaaaaaa", "停止发布本地流失败 msg = " + info);
                }
            }

            @Override
            public void onRemotePublish(BMXStream stream, String info, BMXErrorCode error) {
                engine.subscribe(stream);
                Log.e("aaaaaaa", "远端发布流 开启订阅");
            }

            @Override
            public void onRemoteUnPublish(BMXStream stream, String info, BMXErrorCode error) {
                Log.e("aaaaaaa", "远端取消发布流");
                BMXVideoCanvas canvas = new BMXVideoCanvas();
                canvas.setMView(mRemoteView.getObtainView());
                engine.stopRemoteView(canvas);
                engine.unSubscribe(stream);
            }

            @Override
            public void onSubscribe(BMXStream stream, String info, BMXErrorCode error) {
                if (error != null && error.swigValue() == 0) {
                    mRemoteView = new RTCRenderView(BMXRTCActivity.this);
                    mRemoteView.init();
                    mContainer.addView(mRemoteView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    BMXVideoCanvas canvas = new BMXVideoCanvas();
                    canvas.setMView(mRemoteView.getObtainView());
                    canvas.setMUserId(stream.getMUserId());
                    canvas.setMStream(stream);
                    engine.startRemoteView(canvas);
                    Log.e("aaaaaaa", "订阅远端流成功, 开启预览 msg = " + info);
                } else {
                    Log.e("aaaaaaa", "订阅远端流失败 msg = " + info);
                }
            }

            @Override
            public void onUnSubscribe(BMXStream stream, String info, BMXErrorCode error) {
                if (error != null && error.swigValue() == 0) {
                    Log.e("aaaaaaa", "取消订阅远端流成功, 开启预览 msg = " + info);
                } else {
                    Log.e("aaaaaaa", "取消订阅远端流失败 msg = " + info);
                }
            }

            @Override
            public void onLocalAudioLevel(int volume) {
            }

            @Override
            public void onRemoteAudioLevel(long userId, int volume) {
            }

            @Override
            public void onKickoff(String info, BMXErrorCode error) {
            }

            @Override
            public void onWarning(String info, BMXErrorCode error) {
            }

            @Override
            public void onError(String info, BMXErrorCode error) {
            }

            @Override
            public void onNetworkQuality(BMXStream stream, String info, BMXErrorCode error) {
            }
        });
        BMXRoomAuth auth = new BMXRoomAuth();
        auth.setMUserId(111);
        auth.setMRoomId("1234");
        engine.joinRoom(auth);
    }

    @Override
    protected void onStop() {
        super.onStop();
        engine.leaveRoom();
    }
}