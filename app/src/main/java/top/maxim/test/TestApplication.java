package top.maxim.test;

import androidx.multidex.MultiDexApplication;

public class TestApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        BaseManager.initBMXSDK(this);
    }
}
