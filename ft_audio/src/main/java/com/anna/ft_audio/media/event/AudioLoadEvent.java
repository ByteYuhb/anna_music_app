package com.anna.ft_audio.media.event;


import com.anna.ft_audio.media.model.AudioBean;

public class AudioLoadEvent {
  public AudioBean mAudioBean;

  public AudioLoadEvent(AudioBean audioBean) {
    this.mAudioBean = audioBean;
  }
}
