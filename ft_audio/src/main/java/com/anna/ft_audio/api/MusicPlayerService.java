package com.anna.ft_audio.api;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.Nullable;


import com.anna.ft_audio.AudioProvider;
import com.anna.ft_audio.media.core.AudioController;
import com.anna.ft_audio.media.event.AudioFavouriteEvent;
import com.anna.ft_audio.media.event.AudioLoadEvent;
import com.anna.ft_audio.media.event.AudioPauseEvent;
import com.anna.ft_audio.media.event.AudioReleaseEvent;
import com.anna.ft_audio.media.event.AudioStartEvent;
import com.anna.ft_audio.media.model.AudioBean;
import com.anna.ft_audio.media.view.NotificationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MusicPlayerService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AudioProvider.Stub() {

            @Override
            public void setQueue(List<AudioBean> audioList) throws RemoteException {
                AudioController.getInstance().setmQueue(audioList);
                //初始化前台Notification
                NotificationHelper.getInstance().init(new NotificationHelper.NotificationHelperListener() {
                    @Override
                    public void onNotificationInit() {
                        //让服务处于前台，提高优先级，增加保活能力
                        startForeground(NotificationHelper.NOTIFICATION_ID,NotificationHelper.getInstance().getNotification());
                    }
                });
            }

            @Override
            public void addAudio(AudioBean bean) throws RemoteException {
                AudioController.getInstance().addAudio(bean);
            }

            @Override
            public void play() throws RemoteException {
                AudioController.getInstance().play();

            }

            @Override
            public void playNext() throws RemoteException {
                AudioController.getInstance().next();
            }

            @Override
            public void playPre() throws RemoteException {
                AudioController.getInstance().previous();
            }

            @Override
            public void pause() throws RemoteException {
                AudioController.getInstance().pause();
            }

            @Override
            public void resume() throws RemoteException {
                AudioController.getInstance().resume();
            }

            @Override
            public void release() throws RemoteException {
                AudioController.getInstance().release();
            }
        };
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        registerBroadcastReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AudioController.getInstance().release();
        unRegisterBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        if (mReceiver == null) {
            mReceiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(NotificationReceiver.ACTION_STATUS_BAR);
            registerReceiver(mReceiver, filter);
        }
    }

    private NotificationReceiver mReceiver;
    private void unRegisterBroadcastReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新notifacation为load状态
        NotificationHelper.getInstance().showLoadStatus(event.mAudioBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新notifacation为暂停状态
        NotificationHelper.getInstance().showPauseStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新notifacation为播放状态
        NotificationHelper.getInstance().showPlayStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFavouriteEvent(AudioFavouriteEvent event) {
        //更新notifacation收藏状态
        NotificationHelper.getInstance().changeFavouriteStatus(event.isFavourite);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioReleaseEvent(AudioReleaseEvent event) {
        //移除notifacation
    }

    /**
     * 接收Notification发送的广播
     */
    public static class NotificationReceiver extends BroadcastReceiver {
        public static final String ACTION_STATUS_BAR =
                AudioHelper.getmContext().getPackageName() + ".NOTIFICATION_ACTIONS";
        public static final String EXTRA = "extra";
        public static final String EXTRA_PLAY = "play_pause";
        public static final String EXTRA_NEXT = "play_next";
        public static final String EXTRA_PRE = "play_previous";
        public static final String EXTRA_FAV = "play_favourite";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            String extra = intent.getStringExtra(EXTRA);
            switch (extra) {
                case EXTRA_PLAY:
                    //处理播放暂停事件,可以封到AudioController中
                    AudioController.getInstance().playOrPause();
                    break;
                case EXTRA_PRE:
                    AudioController.getInstance().previous(); //不管当前状态，直接播放
                    break;
                case EXTRA_NEXT:
                    AudioController.getInstance().next();
                    break;
                case EXTRA_FAV:
                    AudioController.getInstance().changeFavourite();
                    break;
            }
        }
    }
}
