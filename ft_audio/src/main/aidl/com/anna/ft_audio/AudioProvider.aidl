// AudioProvider.aidl
package com.anna.ft_audio;

import com.anna.ft_audio.media.model.AudioBean;

// Declare any non-default types here with import statements

interface AudioProvider {
    void setQueue(in List<AudioBean> audioList);
    void addAudio(in AudioBean bean);
    void play();
    void playNext();
    void playPre();
    void pause();
    void resume();
    void release();
}