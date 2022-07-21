package com.anna.lib_audio_player.media.event;


import com.anna.lib_audio_player.media.core.AudioController;

/**
 * 播放模式切换事件
 */
public class AudioPlayModeEvent {
  public AudioController.PlayMode mPlayMode;

  public AudioPlayModeEvent(AudioController.PlayMode playMode) {
    this.mPlayMode = playMode;
  }
}
