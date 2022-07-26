package com.anna.lib_video;

/**
 * 与应用层通信接口
 */
public interface VideoContextInterface {

    void onVideoSuccess();

    void onVideoFailed();

    void onVideoComplete();
}
