package com.anna.music;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.anna.lib_audio_player.api.AudioHelper;
import com.anna.lib_audio_player.utils.NotificationSetUtil;

public class AudioActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_bottom_view);
        AudioHelper.mContext = this;

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            AudioHelper.provider.setQueue(getAudioList());
            AudioHelper.provider.play();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        NotificationSetUtil.OpenNotificationSetting(this, new NotificationSetUtil.OnNextListener() {
            @Override
            public void onNext() {
//                createNotificationForNormal();
            }
        });

    }

    private void createNotificationForNormal() {
        // 适配8.0及以上 创建渠道
        String CHANNEL_ID = "id101";
        String CHANNEL_NAME = "name101";
        int mNormalNotificationId = 101;
        NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =  new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            mManager.createNotificationChannel(channel);
        }
        // 点击意图 // setDeleteIntent 移除意图
        Intent intent = new Intent(this, BaseActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        // 构建配置
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("普通通知") // 标题
                .setContentText("普通通知内容") // 文本
                .setSmallIcon(R.mipmap.ic_launcher) // 小图标
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 7.0 设置优先级
                .setContentIntent(pendingIntent) // 跳转配置
                .setAutoCancel(true); // 是否自动消失（点击）or mManager.cancel(mNormalNotificationId)、cancelAll、setTimeoutAfter()
        // 发起通知
        mManager.notify(mNormalNotificationId, mBuilder.build());
    }
}
