package com.anna.lib_audio_player.media.core;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;

import com.anna.lib_audio_player.api.AudioHelper;
import com.anna.lib_audio_player.media.event.AudioCompleteEvent;
import com.anna.lib_audio_player.media.event.AudioErrorEvent;
import com.anna.lib_audio_player.media.event.AudioLoadEvent;
import com.anna.lib_audio_player.media.event.AudioPauseEvent;
import com.anna.lib_audio_player.media.event.AudioProgressEvent;
import com.anna.lib_audio_player.media.event.AudioReleaseEvent;
import com.anna.lib_audio_player.media.event.AudioStartEvent;
import com.anna.lib_audio_player.media.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *播放器事件源
 */
public class AudioPlayer implements CustomMediaPlayer.MediaPlayerListener {
    private CustomMediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private static final String TAG = "AudioPlayer";
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INVAL = 100;
    //是否是因为系统引起的焦点问题
    private boolean isPausedByFocusLossTransient;
    //音频焦点监听器
    private AudioFocusManager mAudioFocusManager;

    public AudioPlayer() {
        isPausedByFocusLossTransient = false;
        mMediaPlayer = new CustomMediaPlayer();
        init();
    }

    /**
     * 初始化mMediaPlayer的事件，如：使用唤醒锁，设置音频格式，设置wifilock，设置mAudioFocusManager
     *
     */
    private void init() {
        //使用唤醒锁
        mMediaPlayer.setWakeMode(AudioHelper.getmContext(), PowerManager.PARTIAL_WAKE_LOCK);
        //设置音频格式
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //设置数据准备完毕回调
        mMediaPlayer.setListener(this);
        //设置wifilock，可以让在后台也可以运行wifi下载加载数据
        mWifiLock = ((WifiManager) AudioHelper.getmContext()
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "AudioPlayer");
        //设置Audio管理回调器，在一些场景下会回调，如中途遇到打电话，其他应用占用音频端口
        mAudioFocusManager = new AudioFocusManager(AudioHelper.getmContext(),audioFocusListener);
    }

    //播放进度更新handler
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    //暂停也要更新进度，防止UI不同步，只不过进度一直一样
                    if (getStatus() == CustomMediaPlayer.Status.STARTED
                            || getStatus() == CustomMediaPlayer.Status.PAUSED) {
                        //UI类型处理事件
                        EventBus.getDefault()
                                .post(new AudioProgressEvent(getStatus(), getCurrentPosition(), getDuration()));
                        sendEmptyMessageDelayed(TIME_MSG, TIME_INVAL);
                    }
                    break;
            }
        }
    };

    AudioFocusManager.AudioFocusListener audioFocusListener = new AudioFocusManager.AudioFocusListener() {
        // 重新获得焦点
        @Override
        public void audioFocusGrant() {
            setVolumn(1.0f,1.0f);
            if(isPausedByFocusLossTransient){
                resume();
            }
            isPausedByFocusLossTransient = false;
        }

        // 永久丢失焦点，如被其他播放器抢占
        @Override
        public void audioFocusLoss() {
            if(mMediaPlayer != null) {
                pause();
            }
            isPausedByFocusLossTransient = true;
        }
        // 短暂丢失焦点，如来电
        @Override
        public void audioFocusLossTransient() {
            if (mMediaPlayer != null) pause();
            isPausedByFocusLossTransient = true;
        }
        // 瞬间丢失焦点，如通知
        @Override
        public void audioFocusLossDuck() {
            //瞬间失去焦点,
            setVolumn(0.5f, 0.5f);
        }
    };

    /**
     * 开始启动播放音乐,由异步onPrepared回调调用
     */
    private void start(){
        mMediaPlayer.setmStatus(CustomMediaPlayer.Status.PREPARED);
        mMediaPlayer.start();
        // 启用wifi锁
        mWifiLock.acquire();
        //更新进度
        mHandler.sendEmptyMessage(TIME_MSG);
        //发送start事件，UI类型处理事件
        EventBus.getDefault().post(new AudioStartEvent());
    }

    /**
     * 对外提供的加载音频的方法
     */
    public void load(AudioBean audioBean) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audioBean.mUrl);
            mMediaPlayer.prepareAsync();
            //发送加载音频事件，UI类型处理事件
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }
    /**
     * 对外提供的播放方法
     */
    public void resume() {
        if(getStatus() == CustomMediaPlayer.Status.PAUSED){
            start();
        }
    }
    /**
     * 对外暴露pause方法
     */
    public void pause() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED) {
            mMediaPlayer.pause();
            //关注wifi锁
            if(mWifiLock.isHeld()){
                mWifiLock.release();
            }
            // 取消音频焦点
            if (mAudioFocusManager != null) {
                mAudioFocusManager.abandonAudioFocus();
            }
            //发送暂停事件,UI类型事件
            EventBus.getDefault().post(new AudioPauseEvent());
        }
    }
    /**
     * 销毁唯一mediaplayer实例,只有在退出app时使用
     */
    public void release() {
        if(mMediaPlayer == null){
            return;
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
        // 关闭wifi锁
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        mWifiLock = null;
        // 取消音频焦点
        if (mAudioFocusManager != null) {
            mAudioFocusManager.abandonAudioFocus();
        }
        mAudioFocusManager = null;
        //发送销毁播放器事件,清除通知等
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    /**获取当前状态
     * @return
     */
    public CustomMediaPlayer.Status getStatus(){
        return mMediaPlayer.getmStatus();
    }

    /**
     * 数据元准备完毕的回调，可以直接启动start开始播放
     */
    @Override
    public void onPrepared() {
        start();
    }

    /**
     * 播放完成事件
     */
    @Override
    public void omCompleted() {
        //发送播放完成事件,逻辑类型事件
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public void onError() {
        //发送当次播放实败事件,逻辑类型事件
        EventBus.getDefault().post(new AudioErrorEvent());
    }

    /**
     * 获取当前音乐总时长,更新进度用
     */
    private int getDuration() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED
                || getStatus() == CustomMediaPlayer.Status.PAUSED) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /**获取当前的播放进度
     * @return
     */
    public int getCurrentPosition() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED
                || getStatus() == CustomMediaPlayer.Status.PAUSED) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**设置音量，
     * @param left 左声道 0-1.0f
     * @param right 右声道 0-1.0f
     */
    private void setVolumn(float left, float right) {
        if (mMediaPlayer != null) mMediaPlayer.setVolume(left, right);
    }
}


