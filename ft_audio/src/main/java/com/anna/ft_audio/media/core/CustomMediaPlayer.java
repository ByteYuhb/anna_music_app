package com.anna.ft_audio.media.core;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 这个类主要是提供MediaPlayer的各种状态，内部回调
 */
public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    //使用枚举类表示当前播放器的各种状态
    public enum Status{
        IDLE, INITIALIZED, PREPARED,STARTED, PAUSED, STOPPED, COMPLETED
    }

    //当前播放器状态
    private Status mStatus;

    public CustomMediaPlayer(){
        super();
        //默认状态 IDLE
        mStatus = Status.IDLE;
        //设置播放完毕后的回调
        setOnCompletionListener(this);
        //设置数据准备完毕，准备播放的回调
        setOnPreparedListener(this);
        //设置发生错误事件的回调
        setOnErrorListener(this);
    }

    //设置播放完毕后的回调
    @Override
    public void onCompletion(MediaPlayer mp) {
        mStatus = Status.COMPLETED;
        listener.omCompleted();
    }

    //播放器重置：状态设置为IDLE
    @Override
    public void reset() {
        super.reset();
        mStatus = Status.IDLE;
    }

    //设置数据元的回调：状态设置为：INITIALIZED
    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        mStatus = Status.INITIALIZED;
    }

    //数据准备完毕后，开始播放前的状态：PREPARED
    @Override
    public void onPrepared(MediaPlayer mp) {
        mStatus = Status.PREPARED;
        listener.onPrepared();
    }

    //发送错误事件的回调，返回true表示消费掉事件，如果为false则还会回调onComplete接口回调
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        listener.onError();
        return true;
    }

    //开始播放状态
    @Override
    public void start() throws IllegalStateException {
        super.start();
        mStatus = Status.STARTED;
    }

    //停止播放状态
    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mStatus = Status.STOPPED;
    }

    //暂停状态
    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mStatus = Status.PAUSED;
    }

    public void setmStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

    public Status getmStatus() {
        return mStatus;
    }

    private MediaPlayerListener listener;

    public void setListener(MediaPlayerListener listener) {
        this.listener = listener;
    }

    /**
     * 这个Listener用途AudioPlayer的事件回调
     */
    public interface MediaPlayerListener{
        void onPrepared();
        void omCompleted();
        void onError();
    }
}
