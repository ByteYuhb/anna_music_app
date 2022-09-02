package com.anna.lib_base.module.audio;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.anna.lib_base.module.audio.model.CommonAudioBean;

import java.util.List;

public class AudioImpl {
    private static AudioImpl mAudioImpl = null;

    @Autowired(name = "/audio/audio_service") protected AudioService mAudioService;

    public static AudioImpl getInstance() {
        if (mAudioImpl == null) {
            synchronized (AudioImpl.class) {
                if (mAudioImpl == null) {
                    mAudioImpl = new AudioImpl();
                }
            }
        }
        return mAudioImpl;
    }
    private AudioImpl(){
        ARouter.getInstance().inject(this);
    }
    public void setQueue(List<CommonAudioBean> audioList){
        mAudioService.setQueue(audioList);
    }
    public void addAudio(CommonAudioBean bean){
        mAudioService.addAudio(bean);
    }
    public void play(){
        mAudioService.play();
    }
    public void playNext(){
        mAudioService.playNext();
    }
    public void playPre(){
        mAudioService.playPre();
    }
    public void pause(){
        mAudioService.pause();
    }
    public void resume(){
        mAudioService.resume();
    }
    public void release(){
        mAudioService.release();
    }
    public void initMusicService(Context context){
        if(mAudioService !=null){
            mAudioService.initMusicService(context);
        }

    }

}
