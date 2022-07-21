// AudioProvider.aidl
package com.anna.lib_audio_player;

import com.anna.lib_audio_player.media.model.AudioBean;

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