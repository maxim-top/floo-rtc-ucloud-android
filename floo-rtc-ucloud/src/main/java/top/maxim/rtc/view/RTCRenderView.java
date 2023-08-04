package top.maxim.rtc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ucloudrtclib.sdkengine.define.UCloudRtcRenderView;

/**
 * Description : RTC view
 * Created by mango on 5/18/21.
 */
public class RTCRenderView extends BMXRtcRenderView {

    private UCloudRtcRenderView mRenderView;

    public RTCRenderView(@NonNull Context context) {
        this(context, null);
    }

    public RTCRenderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RTCRenderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    View obtainView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mRenderView = new UCloudRtcRenderView(context, attrs);
        return mRenderView;
    }

    @Override
    public void init() {
        super.init();
        mRenderView.init();
        mRenderView.setZOrderMediaOverlay(false);
        setMirror(true);
    }

    @Override
    public SurfaceView getSurfaceView() {
        return mRenderView;
    }

    @Override
    public void release() {
        mRenderView.release();
    }

    @Override
    public void setEnableHardwareScaler(boolean enable) {
        mRenderView.setEnableHardwareScaler(enable);
    }

    @Override
    public void setMirror(boolean mirror) {
        mRenderView.setMirror(mirror);
    }
}
