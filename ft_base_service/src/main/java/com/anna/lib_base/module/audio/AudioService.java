package com.anna.lib_base.module.audio;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.anna.lib_base.module.audio.model.CommonAudioBean;

import java.util.List;

public interface AudioService extends IProvider {
    void setQueue(List<CommonAudioBean> audioList);
    void addAudio(CommonAudioBean bean);
    void play();
    void playNext();
    void playPre();
    void pause();
    void resume();
    void release();
    void initMusicService(Context context);
}
