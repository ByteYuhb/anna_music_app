package com.anna.ft_audio;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.anna.ft_audio.api.AudioHelper;

public class AudioApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Application","AudioApplication");
        ARouter.init(this);
        AudioHelper.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
