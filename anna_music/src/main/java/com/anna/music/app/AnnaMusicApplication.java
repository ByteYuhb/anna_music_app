package com.anna.music.app;

import android.app.Application;

import com.anna.lib_audio_player.api.AudioHelper;
import com.anna.lib_update.app.UpdateHelper;
import com.anna.lib_video.app.VideoHelper;

public class AnnaMusicApplication extends Application {
    private static AnnaMusicApplication mApplication = null;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        //视频SDK初始化
        VideoHelper.init(this);
        //音频SDK初始化
        AudioHelper.init(this);
        //初始化应用更新组件
        UpdateHelper.init(this);
    }
}
