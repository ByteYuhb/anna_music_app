package com.anna.music.app;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

import com.anna.ft_audio.api.AudioHelper;
import com.anna.lib_base.module.audio.AudioImpl;
import com.anna.lib_update.app.UpdateHelper;
import com.anna.lib_video.app.VideoHelper;

public class AnnaMusicApplication extends Application {
    private static AnnaMusicApplication mApplication = null;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        //ARouter初始化
        ARouter.init(this);
        AudioImpl.getInstance().initMusicService(this);
        //视频SDK初始化
        VideoHelper.init(this);
        //初始化应用更新组件
        UpdateHelper.init(this);

    }
}
