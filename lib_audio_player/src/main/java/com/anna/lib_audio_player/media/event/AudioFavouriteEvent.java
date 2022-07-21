package com.anna.lib_audio_player.media.event;

/**
 * 收藏/取消收藏事件
 */
public class AudioFavouriteEvent {
  public boolean isFavourite;

  public AudioFavouriteEvent(boolean isFavourite) {
    this.isFavourite = isFavourite;
  }
}
