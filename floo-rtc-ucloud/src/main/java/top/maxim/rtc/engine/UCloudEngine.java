package top.maxim.rtc.engine;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.ucloudrtclib.sdkengine.UCloudRtcSdkEngine;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv;
import com.ucloudrtclib.sdkengine.define.UCloudRtcRenderView;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkAudioDevice;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkAuthInfo;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkCaptureMode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkLogLevel;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaServiceStatus;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMediaType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkMode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkNetWorkQuality;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkPushEncode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkRoomType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkScaleType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStats;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamInfo;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamRole;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkStreamType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkTrackType;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkVideoOutputOrientationMode;
import com.ucloudrtclib.sdkengine.define.UCloudRtcSdkVideoProfile;
import com.ucloudrtclib.sdkengine.listener.UCloudRtcSdkEventListener;

import java.util.ArrayList;
import java.util.List;

import im.floo.floolib.BMXAudioProfile;
import im.floo.floolib.BMXErrorCode;
import im.floo.floolib.BMXRTCEngine;
import im.floo.floolib.BMXRTCEngineListener;
import im.floo.floolib.BMXRoomAuth;
import im.floo.floolib.BMXRoomType;
import im.floo.floolib.BMXStream;
import im.floo.floolib.BMXVideoCanvas;
import im.floo.floolib.BMXVideoConfig;
import im.floo.floolib.BMXVideoMediaType;
import im.floo.floolib.BMXVideoProfile;
import top.maxim.rtc.view.BMXRtcRenderView;
import top.maxim.rtc.view.RTCRenderView;


/**
 * Description :Ucloud Engine
 * Created by mango on 5/16/21.
 */
public class UCloudEngine extends BMXRTCEngine {

    private UCloudRtcSdkEngine mEngine;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private List<BMXRTCEngineListener> mListeners = new ArrayList<>();

    private String mRoomId;

    private UCloudRtcSdkEventListener mUCloudListener = new UCloudRtcSdkEventListener() {
        @Override
        public void onServerDisconnect() {
        }

        @Override
        public void onJoinRoomResult(int code, String msg, String roomId) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onJoinRoom(msg, Long.parseLong(roomId), code == 0 ? BMXErrorCode.NoError : BMXErrorCode.NotFound);
                }
            });
        }

        @Override
        public void onLeaveRoomResult(int code, String msg, String roomId) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onLeaveRoom(msg, Long.parseLong(roomId), code == 0 ? BMXErrorCode.NoError : BMXErrorCode.NotFound, msg);
                }
            });
        }

        @Override
        public void onRejoiningRoom(String roomId) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onRejoining(Long.parseLong(roomId), BMXErrorCode.NoError);
                }
            });
        }

        @Override
        public void onRejoinRoomResult(String roomId) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onReJoinRoom("", Long.parseLong(roomId), BMXErrorCode.NoError);
                }
            });
        }

        @Override
        public void onLocalPublish(int code, String msg, UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onLocalPublish(buildBMXStream(uCloudRtcSdkStreamInfo), msg, code == 0 ? BMXErrorCode.NoError : BMXErrorCode.NotFound);
                }
            });
        }

        @Override
        public void onLocalUnPublish(int code, String msg, UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onLocalUnPublish(buildBMXStream(uCloudRtcSdkStreamInfo), msg, code == 0 ? BMXErrorCode.NoError : BMXErrorCode.NotFound);
                }
            });
        }

        @Override
        public void onLocalUnPublishOnly(int code, String msg, UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo) {

        }

        @Override
        public void onRemoteUserJoin(String uid) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onMemberJoined(Long.parseLong(mRoomId), Long.parseLong(uid));
                }
            });
        }

        @Override
        public void onRemoteUserLeave(String uid, int code) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onMemberExited(Long.parseLong(mRoomId), Long.parseLong(uid), code == 0 ? "" : BMXErrorCode.NotFound.toString());
                }
            });
        }

        @Override
        public void onRemotePublish(UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onRemotePublish(buildBMXStream(uCloudRtcSdkStreamInfo), "", BMXErrorCode.NoError);
                }
            });
        }

        @Override
        public void onRemoteUnPublish(UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onRemoteUnPublish(buildBMXStream(uCloudRtcSdkStreamInfo), "", BMXErrorCode.NoError);
                }
            });
        }

        @Override
        public void onSubscribeResult(int code, String msg, UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onSubscribe(buildBMXStream(uCloudRtcSdkStreamInfo), msg, code == 0 ? BMXErrorCode.NoError : BMXErrorCode.NotFound);
                }
            });
        }

        @Override
        public void onUnSubscribeResult(int code, String msg, UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onUnSubscribe(buildBMXStream(uCloudRtcSdkStreamInfo), msg, code == 0 ? BMXErrorCode.NoError : BMXErrorCode.NotFound);
                }
            });
        }

        @Override
        public void onLocalStreamMuteRsp(int code, String msg, UCloudRtcSdkMediaType uCloudRtcSdkMediaType, UCloudRtcSdkTrackType uCloudRtcSdkTrackType, boolean b) {

        }

        @Override
        public void onRemoteStreamMuteRsp(int code, String msg, String uid, UCloudRtcSdkMediaType uCloudRtcSdkMediaType, UCloudRtcSdkTrackType uCloudRtcSdkTrackType, boolean b) {

        }

        @Override
        public void onRemoteTrackNotify(String uid, UCloudRtcSdkMediaType uCloudRtcSdkMediaType, UCloudRtcSdkTrackType uCloudRtcSdkTrackType, boolean b) {

        }

        @Override
        public void onSendRTCStatus(UCloudRtcSdkStats uCloudRtcSdkStats) {

        }

        @Override
        public void onRemoteRTCStatus(UCloudRtcSdkStats uCloudRtcSdkStats) {

        }

        @Override
        public void onLocalAudioLevel(int volume) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onLocalAudioLevel(volume);
                }
            });
        }

        @Override
        public void onRemoteAudioLevel(String uid, int volume) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onRemoteAudioLevel(Long.parseLong(uid), volume);
                }
            });
        }

        @Override
        public void onKickoff(int code) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onKickoff(code == 0 ? "" : BMXErrorCode.NotFound.toString(), code == 0 ? BMXErrorCode.NoError : BMXErrorCode.NotFound);
                }
            });
        }

        @Override
        public void onWarning(int code) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onWarning(code == 0 ? "" : BMXErrorCode.NotFound.toString(), code == 0 ? BMXErrorCode.NoError : BMXErrorCode.NotFound);
                }
            });
        }

        @Override
        public void onError(int code) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                    listener.onError(code == 0 ? "" : BMXErrorCode.NotFound.toString(), code == 0 ? BMXErrorCode.NoError : BMXErrorCode.NotFound);
                }
            });
        }

        @Override
        public void onAddStreams(int code, String msg) {

        }

        @Override
        public void onDelStreams(int code, String msg) {

        }

        @Override
        public void onLogOffUsers(int code, String msg) {

        }

        @Override
        public void onMsgNotify(int code, String msg) {

        }

        @Override
        public void onLogOffNotify(int code, String uid) {

        }

        @Override
        public void onRecordStart(int code, String fileName) {

        }

        @Override
        public void onRecordStop(int code) {

        }

        @Override
        public void onQueryMix(int code, String msg, int type, String mixId, String fileName) {

        }

        @Override
        public void onRecordStatusNotify(UCloudRtcSdkMediaServiceStatus uCloudRtcSdkMediaServiceStatus, int code, String msg, String uid, String roomId, String mixId, String fileName) {

        }

        @Override
        public void onRelayStatusNotify(UCloudRtcSdkMediaServiceStatus uCloudRtcSdkMediaServiceStatus, int code, String msg, String uid, String roomId, String mixId, String[] pushUrls) {

        }

        @Override
        public void onServerBroadCastMsg(String uid, String msg) {

        }

        @Override
        public void onAudioDeviceChanged(UCloudRtcSdkAudioDevice uCloudRtcSdkAudioDevice) {

        }

        @Override
        public void onPeerLostConnection(int type, UCloudRtcSdkStreamInfo uCloudRtcSdkStreamInfo) {

        }

        @Override
        public void onNetWorkQuality(String uid, UCloudRtcSdkStreamType uCloudRtcSdkStreamType, UCloudRtcSdkMediaType uCloudRtcSdkMediaType, UCloudRtcSdkNetWorkQuality uCloudRtcSdkNetWorkQuality) {
            mHandler.post(() -> {
                for (BMXRTCEngineListener listener :
                        mListeners) {
                }
            });
        }

        @Override
        public void onAudioFileFinish() {

        }
    };

    public UCloudEngine() {
        mEngine = UCloudRtcSdkEngine.createEngine(mUCloudListener);
        mEngine.setAutoPublish(false);
        mEngine.setAutoSubscribe(false);
        mEngine.setStreamRole(UCloudRtcSdkStreamRole.UCLOUD_RTC_SDK_STREAM_ROLE_BOTH);
        BMXVideoConfig config = new BMXVideoConfig();
        config.setProfile(BMXVideoProfile.Profile_640_480);
        setVideoProfile(config);
    }

    private static String APP_ID;
    private static String APP_KEY;

    public static void init(Application application, String appId, String appKey) {
        APP_ID = appId;
        APP_KEY = appKey;
        UCloudRtcSdkEnv.initEnv(application);
        UCloudRtcSdkEnv.setWriteToLogCat(true);
        UCloudRtcSdkEnv.setLogReport(true);
        UCloudRtcSdkEnv.setEncodeMode(UCloudRtcSdkPushEncode.UCLOUD_RTC_PUSH_ENCODE_MODE_H264);
        UCloudRtcSdkEnv.setLogLevel(UCloudRtcSdkLogLevel.UCLOUD_RTC_SDK_LogLevelInfo);
        UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIAL);
        UCloudRtcSdkEnv.setReConnectTimes(60);
        UCloudRtcSdkEnv.setTokenSecKey(APP_KEY);
        //UCloudRtcSdkEnv.setDeviceChannelType(UCloudRtcSdkChannelType.UCLOUD_RTC_SDK_CHANNEL_TYPE_VOICE);
        //推流方向
//        UCloudRtcSdkEnv.setPushOrientation(UCloudRtcSdkPushOrentation.UCLOUD_RTC_PUSH_PORTRAIT_MODE);
        //视频输出模式
        UCloudRtcSdkEnv.setVideoOutputOrientation(UCloudRtcSdkVideoOutputOrientationMode.UCLOUD_RTC_VIDEO_OUTPUT_FIXED_PORTRAIT_MODE);
        //私有化部署
//        UCloudRtcSdkEnv.setPrivateDeploy(true);
//        UCloudRtcSdkEnv.setPrivateDeployRoomURL("wss://xxx:5005/ws");
        //无限重连
//        UCloudRtcSdkEnv.setReConnectTimes(-1);
        //默认vp8编码，可以改成h264
//        UCloudRtcSdkEnv.setEncodeMode(UcloudRtcSdkPushEncode.UCLOUD_RTC_PUSH_ENCODE_MODE_H264);
    }

    @Override
    public void addRTCEngineListener(BMXRTCEngineListener listener) {
        if (listener != null) {
            mListeners.add(listener);
        }
    }

    @Override
    public void removeRTCEngineListener(BMXRTCEngineListener listener) {
        if (listener != null) {
            mListeners.remove(listener);
        }
    }

    @Override
    public BMXErrorCode setRoomType(BMXRoomType type) {
        UCloudRtcSdkRoomType uCloudRtcSdkRoomType;
        switch (type){
            case Communication:
                uCloudRtcSdkRoomType = UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_SMALL;
                break;
            case Broadcast:
            default:
                uCloudRtcSdkRoomType = UCloudRtcSdkRoomType.UCLOUD_RTC_SDK_ROOM_LARGE;
                break;
        }
        mEngine.setClassType(uCloudRtcSdkRoomType);
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode setVideoProfile(BMXVideoConfig videoConfig) {
        mEngine.setVideoProfile(buildVideoProfile(videoConfig));
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode setAudioProfile(BMXAudioProfile profile) {
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode joinRoom(BMXRoomAuth auth) {
        isOnCall = true;
        UCloudRtcSdkAuthInfo info = new UCloudRtcSdkAuthInfo();
        info.setAppId(APP_ID);
        info.setToken("testToken");
        info.setRoomId(String.valueOf(auth.getMRoomId()));
        info.setUId(auth.getMUserId() + "");
        UCloudRtcSdkEnv.setCaptureMode(
                UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_LOCAL);
        mRoomId = String.valueOf(auth.getMRoomId());
        // 加入房间
        mEngine.joinChannel(info);
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode leaveRoom() {
        isOnCall = false;
        mEngine.leaveChannel();
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode publish(BMXVideoMediaType type, boolean hasVideo, boolean hasAudio) {
        mEngine.configLocalCameraPublish(hasVideo);
        mEngine.configLocalAudioPublish(hasAudio);
        mEngine.muteLocalVideo(hasVideo, buildUCloudMediaType(type));
        mEngine.publish(buildUCloudMediaType(type), true, true).getErrorCode();
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode unPublish(BMXVideoMediaType type) {
        mEngine.unPublish(buildUCloudMediaType(type));
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode subscribe(BMXStream stream) {
        mEngine.subscribe(buildUCloudStreamInfo(stream));
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode unSubscribe(BMXStream stream) {
        mEngine.unSubscribe(buildUCloudStreamInfo(stream));
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode startPreview(BMXVideoCanvas canvas) {
        RTCRenderView rtcRenderView = (RTCRenderView) canvas.getMView();
        BMXRtcRenderView.ScalingType scalingType = rtcRenderView.getScalingType();
        mEngine.renderLocalView(buildUCloudStreamInfo(canvas.getMStream()), rtcRenderView.getObtainView(), UCloudRtcSdkScaleType.values()[scalingType.ordinal()], null);
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode stopPreview(BMXVideoCanvas canvas) {
        mEngine.stopPreview(buildUCloudMediaType(canvas.getMStream().getMMediaType()));
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode startRemoteView(BMXVideoCanvas canvas) {
        RTCRenderView rtcRenderView = (RTCRenderView) canvas.getMView();
        BMXRtcRenderView.ScalingType scalingType = rtcRenderView.getScalingType();
        mEngine.startRemoteView(buildUCloudStreamInfo(canvas.getMStream()), rtcRenderView.getObtainView(), UCloudRtcSdkScaleType.values()[scalingType.ordinal()], null);
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode stopRemoteView(BMXVideoCanvas canvas) {
        mEngine.stopRemoteView(buildUCloudStreamInfo(canvas.getMStream()));
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode muteLocalAudio(boolean mute) {
        mEngine.muteLocalMic(mute);
        mEngine.configLocalAudioPublish(!mute);
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode muteLocalVideo(BMXVideoMediaType type, boolean mute) {
        mEngine.muteLocalVideo(mute, buildUCloudMediaType(type));
        mEngine.configLocalCameraPublish(!mute);
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode muteRemoteAudio(BMXStream stream, boolean mute) {
        mEngine.muteRemoteAudio(stream.getMUserId() + "", mute);
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode muteRemoteVideo(BMXStream stream, boolean mute) {
        mEngine.muteRemoteVideo(stream.getMUserId() + "", mute);
        return BMXErrorCode.NoError;
    }

    @Override
    public BMXErrorCode switchCamera() {
        mEngine.switchCamera();
        return BMXErrorCode.NoError;
    }

    private BMXStream buildBMXStream(UCloudRtcSdkStreamInfo info) {
        BMXStream streamInfo = new BMXStream();
        if (info != null) {
            streamInfo.setMUserId(Long.parseLong(info.getUId()));
            streamInfo.setMEnableVideo(info.isHasVideo());
            streamInfo.setMEnableAudio(info.isHasAudio());
            streamInfo.setMEnableData(info.isHasData());
            streamInfo.setMMuteVideo(info.isMuteVideo());
            streamInfo.setMMuteAudio(info.isMuteAudio());
        }
        return streamInfo;
    }

    private UCloudRtcSdkStreamInfo buildUCloudStreamInfo(BMXStream stream) {
        UCloudRtcSdkStreamInfo streamInfo = new UCloudRtcSdkStreamInfo();
        if (stream != null) {
            streamInfo.setUid(stream.getMUserId()+"");
            streamInfo.setHasVideo(stream.getMEnableVideo());
            streamInfo.setHasAudio(stream.getMEnableAudio());
            streamInfo.setHasData(stream.getMEnableData());
            streamInfo.setMuteVideo(stream.getMEnableVideo());
            streamInfo.setMuteAudio(stream.getMEnableAudio());
            streamInfo.setMediaType(buildUCloudMediaType(stream.getMMediaType()));
        }
        return streamInfo;
    }

    private UCloudRtcSdkMediaType buildUCloudMediaType(BMXVideoMediaType type) {
        UCloudRtcSdkMediaType uCloudRtcSdkMediaType;
        switch (type){
            case Screen:
                uCloudRtcSdkMediaType = UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_SCREEN;
                break;
            case Camera:
            default:
                uCloudRtcSdkMediaType = UCloudRtcSdkMediaType.UCLOUD_RTC_SDK_MEDIA_TYPE_VIDEO;
                break;
        }
        return uCloudRtcSdkMediaType;
    }

    private UCloudRtcSdkVideoProfile buildVideoProfile(BMXVideoConfig bmxVideoConfig){
        if(bmxVideoConfig == null){
            return UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_640_480;
        }
        UCloudRtcSdkVideoProfile profile;
        switch (bmxVideoConfig.getProfile()){
            case Profile_320_180:
                profile = UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_320_180;
                break;
            case Profile_480_360:
                profile = UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_480_360;
                break;
            case Profile_640_360:
                profile = UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_640_360;
                break;
            case Profile_1280_720:
                profile = UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_1280_720;
                break;
            case Profile_1920_1080:
                profile = UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_1920_1080;
                break;
            case Profile_640_480:
            default:
                profile = UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_640_480;
                break;
        }
        return profile;
    }
}
