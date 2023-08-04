
package top.maxim.test;

import android.app.Application;

import im.floo.floolib.BMXRTCEngine;
import im.floo.floolib.BMXRTCService;
import top.maxim.rtc.engine.UCloudEngine;

/**
 * Description : RTC Created by Mango on 2018/12/2.
 */
public class RTCManager extends BaseManager {

    private static final String TAG = RTCManager.class.getSimpleName();

    private static final RTCManager sInstance = new RTCManager();

    private BMXRTCService mService;

    public static RTCManager getInstance() {
        return sInstance;
    }

    private RTCManager() {
        mService = bmxClient.getRTCManager();
    }

    public void init(Application application){
        UCloudEngine.init(application, "urtc-h2gklbnp", "bcbc73551dc7b486fd33f799750e2797");
        mService.setupRTCEngine(new UCloudEngine());
    }

    public BMXRTCEngine getRTCEngine(){
        return mService.getRTCEngine();
    }
}
