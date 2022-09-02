package com.anna.ft_audio.media.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;


import com.anna.ft_audio.R;
import com.anna.ft_audio.api.AudioHelper;
import com.anna.ft_audio.api.MusicPlayerService;
import com.anna.ft_audio.media.activity.MusicPlayerActivity;
import com.anna.ft_audio.media.core.AudioController;
import com.anna.ft_audio.media.db.GreenDaoHelper;
import com.anna.ft_audio.media.model.AudioBean;
import com.anna.lib_image_loader.glide.CustomImageLoader;


/**
 * 音乐Notification帮助类
 */
public class NotificationHelper {

  public static final String CHANNEL_ID = "channel_id_audio";
  public static final String CHANNEL_NAME = "channel_name_audio";
  public static final int NOTIFICATION_ID = 0x111;

  //最终的Notification显示类
  private Notification mNotification;
  private RemoteViews mRemoteViews; // 大布局
  private RemoteViews mSmallRemoteViews; //小布局
  private NotificationManager mNotificationManager;
  private NotificationHelperListener mListener;
  private String packageName;
  //当前要播的歌曲Bean
  private AudioBean mAudioBean;

  public static NotificationHelper getInstance() {
    return SingletonHolder.instance;
  }

  private static class SingletonHolder {
    private static NotificationHelper instance = new NotificationHelper();
  }

  public void init(NotificationHelperListener listener) {
    mNotificationManager = (NotificationManager) AudioHelper.getmContext()
        .getSystemService(Context.NOTIFICATION_SERVICE);
    packageName = AudioHelper.getmContext().getPackageName();
    mAudioBean = AudioController.getInstance().getNowPlaying();
    initNotification();
    mListener = listener;
    if (mListener != null) mListener.onNotificationInit();
  }

  /*
   * 创建Notification,
   */
  private void initNotification() {
    if (mNotification == null) {
      //首先创建布局
      initRemoteViews();
      //再构建Notification
      Intent intent = new Intent(AudioHelper.getmContext(), MusicPlayerActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(AudioHelper.getmContext(), 0, intent,
          PendingIntent.FLAG_IMMUTABLE);

      //适配安卓8.0的消息渠道
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channel =
            new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
//        channel.enableLights(false);
//        channel.enableVibration(false);
        mNotificationManager.createNotificationChannel(channel);
      }
      NotificationCompat.Builder builder =
          new NotificationCompat.Builder(AudioHelper.getmContext(), CHANNEL_ID)
                  .setContentIntent(pendingIntent)
                  .setSmallIcon(R.mipmap.ic_launcher)
                  .setCustomBigContentView(mRemoteViews) //大布局
                  .setContent(mSmallRemoteViews); //正常布局，两个布局可以切换
      mNotification = builder.build();

      showLoadStatus(mAudioBean);
    }
  }

  /*
   * 创建Notification的布局,默认布局为Loading状态
   */
  private void initRemoteViews() {
    int layoutId = R.layout.notification_big_layout;
    mRemoteViews = new RemoteViews(packageName, layoutId);
    mRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
    mRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
    if (null != GreenDaoHelper.selectFavourite(mAudioBean)) {
      mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_loved);
    } else {
      mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_love_white);
    }

    int smalllayoutId = R.layout.notification_small_layout;
    mSmallRemoteViews = new RemoteViews(packageName, smalllayoutId);
    mSmallRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
    mSmallRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);

    //点击播放按钮广播
    Intent playIntent = new Intent(MusicPlayerService.NotificationReceiver.ACTION_STATUS_BAR);
    playIntent.putExtra(MusicPlayerService.NotificationReceiver.EXTRA,
            MusicPlayerService.NotificationReceiver.EXTRA_PLAY);
    PendingIntent playPendingIntent = PendingIntent.getBroadcast(AudioHelper.getmContext(),
            1,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    mRemoteViews.setOnClickPendingIntent(R.id.play_view,playPendingIntent);
    mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
    mSmallRemoteViews.setOnClickPendingIntent(R.id.play_view, playPendingIntent);
    mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);

    //点击上一首按钮广播
    Intent previousIntent = new Intent(MusicPlayerService.NotificationReceiver.ACTION_STATUS_BAR);
    previousIntent.putExtra(MusicPlayerService.NotificationReceiver.EXTRA,
            MusicPlayerService.NotificationReceiver.EXTRA_PRE);
    PendingIntent previousPendingIntent =
        PendingIntent.getBroadcast(AudioHelper.getmContext(), 2, previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
    mRemoteViews.setOnClickPendingIntent(R.id.previous_view, previousPendingIntent);
    mRemoteViews.setImageViewResource(R.id.previous_view, R.mipmap.note_btn_pre_white);

    //点击下一首按钮广播
    Intent nextIntent = new Intent(MusicPlayerService.NotificationReceiver.ACTION_STATUS_BAR);
    nextIntent.putExtra(MusicPlayerService.NotificationReceiver.EXTRA,
            MusicPlayerService.NotificationReceiver.EXTRA_PRE);
    PendingIntent nextPendingIntent =
        PendingIntent.getBroadcast(AudioHelper.getmContext(), 3, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
    mRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
    mRemoteViews.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white);
    mSmallRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
    mSmallRemoteViews.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white);

    //点击收藏按钮广播
    Intent favouriteIntent = new Intent(MusicPlayerService.NotificationReceiver.ACTION_STATUS_BAR);
    favouriteIntent.putExtra(MusicPlayerService.NotificationReceiver.EXTRA,
            MusicPlayerService.NotificationReceiver.EXTRA_FAV);
    PendingIntent favouritePendingIntent =
        PendingIntent.getBroadcast(AudioHelper.getmContext(), 4, favouriteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
    mRemoteViews.setOnClickPendingIntent(R.id.favourite_view, favouritePendingIntent);
  }

  public Notification getNotification() {
    return mNotification;
  }

  /**
   * 显示Notification的加载状态
   */
  public void showLoadStatus(AudioBean bean) {
    //防止空指针crash
    mAudioBean = bean;
    if (mRemoteViews != null) {
      mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
      mRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
      mRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
      CustomImageLoader.getInstance()
          .displayImageForNotification(AudioHelper.getmContext(), mRemoteViews, R.id.image_view,
              mNotification, NOTIFICATION_ID, mAudioBean.albumPic);
      //更新收藏view
      if (null != GreenDaoHelper.selectFavourite(mAudioBean)) {
        mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_loved);
      } else {
        mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_love_white);
      }

      //小布局也要更新
      mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
      mSmallRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
      mSmallRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
      CustomImageLoader.getInstance()
          .displayImageForNotification(AudioHelper.getmContext(), mSmallRemoteViews, R.id.image_view,
              mNotification, NOTIFICATION_ID, mAudioBean.albumPic);

      mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }
  }

  public void showPlayStatus() {
    if (mRemoteViews != null) {
      mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
      mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
      mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }
  }

  public void showPauseStatus() {
    if (mRemoteViews != null) {
      mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
      mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
      mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }
  }

  public void changeFavouriteStatus(boolean isFavourite) {
    if (mRemoteViews != null) {
      mRemoteViews.setImageViewResource(R.id.favourite_view,
          isFavourite ? R.mipmap.note_btn_loved : R.mipmap.note_btn_love_white);
      mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }
  }

  /**
   * 与音乐service的回调通信
   */
  public interface NotificationHelperListener {
    void onNotificationInit();
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
