package com.anna.ft_audio.service;

import android.content.Context;
import android.os.RemoteException;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.anna.ft_audio.api.AudioHelper;
import com.anna.ft_audio.media.model.AudioBean;
import com.anna.lib_base.module.audio.AudioImpl;
import com.anna.lib_base.module.audio.AudioService;
import com.anna.lib_base.module.audio.model.CommonAudioBean;

import java.util.ArrayList;
import java.util.List;

@Route(path = "/audio/audio_service")
public class AudioServiceImpl implements AudioService {
    @Override
    public void init(Context context) {

    }

    @Override
    public void setQueue(List<CommonAudioBean> audioList) {
        List<AudioBean> list = new ArrayList<>();
        for(CommonAudioBean bean:audioList){
            AudioBean ab = new AudioBean();
            ab.album = bean.album;
            ab.albumInfo = bean.albumInfo;
            ab.albumPic = bean.albumPic;
            ab.author = bean.author;
            ab.id = bean.id;
            ab.mUrl = bean.mUrl;
            ab.name = bean.name;
            ab.totalTime = bean.totalTime;
            list.add(ab);
        }
        try {
            if(AudioHelper.provider == null){
                return;
            }
            AudioHelper.provider.setQueue(list);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAudio(CommonAudioBean bean) {
        AudioBean ab = new AudioBean();
        ab.album = bean.album;
        ab.albumInfo = bean.albumInfo;
        ab.albumPic = bean.albumPic;
        ab.author = bean.author;
        ab.id = bean.id;
        ab.mUrl = bean.mUrl;
        ab.name = bean.name;
        ab.totalTime = bean.totalTime;
        try {
            AudioHelper.provider.addAudio(ab);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void play() {
        try {
            AudioHelper.provider.play();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playNext() {
        try {
            AudioHelper.provider.playNext();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playPre() {
        try {
            AudioHelper.provider.playPre();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        try {
            AudioHelper.provider.pause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resume() {
        try {
            AudioHelper.provider.resume();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        try {
            AudioHelper.provider.release();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initMusicService(Context context) {
        AudioHelper.init(context);
    }
}
