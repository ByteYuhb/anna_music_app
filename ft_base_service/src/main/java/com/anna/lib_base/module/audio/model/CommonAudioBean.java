package com.anna.lib_base.module.audio.model;

import java.io.Serializable;

/**
 * 音乐播放模块所用的AudioBean
 */
public class CommonAudioBean implements Serializable {

  public String id;
  //地址
  public String mUrl;

  //歌名
  public String name;

  //作者
  public String author;

  //所属专辑
  public String album;

  public String albumInfo;

  //专辑封面
  public String albumPic;

  //时长
  public String totalTime;

  public CommonAudioBean(){

  }
  public CommonAudioBean(String id, String mUrl, String name, String author, String album,
                         String albumInfo, String albumPic, String totalTime) {
    this.id = id;
    this.mUrl = mUrl;
    this.name = name;
    this.author = author;
    this.album = album;
    this.albumInfo = albumInfo;
    this.albumPic = albumPic;
    this.totalTime = totalTime;
  }
}
