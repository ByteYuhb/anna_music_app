package com.anna.lib_audio_player.media.exception;

/**
 * 播放队列为空异常
 */
public class AudioQueueEmptyException extends RuntimeException {

  public AudioQueueEmptyException(String error) {
    super(error);
  }
}
