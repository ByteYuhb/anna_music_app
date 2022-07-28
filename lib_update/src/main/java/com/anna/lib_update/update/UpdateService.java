package com.anna.lib_update.update;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.anna.lib_common_ui.base.constant.Constant;
import com.anna.lib_update.R;
import com.anna.lib_update.app.UpdateHelper;
import com.anna.lib_update.update.constant.Constants;
import com.anna.lib_update.update.model.UpdateModel;
import com.anna.lib_update.update.utils.Utils;

import java.io.File;

public class UpdateService extends Service {
    public static final String UPDATE_FILE_KEY = "apk";
    public static final String CHANNEL_ID = "channel_id_update";
    public static final String CHANNEL_NAME = "channel_name_update";

    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationCompat.Builder notificationBuilder;

    private UpdateReceiver mReceiver = null;
    /**
     * 文件存放路经
     */
    private String filePath;
    /**
     * 文件下载地址
     */
    private String apkUrl;

    /**
     * 服务器固定地址
     */
    private static final String APK_URL_TITLE = Constants.IP_PORT+"/images/anna_music-3.0.apk";

    /**启动更新服务的静态方法
     * @param context
     */
    public static void start(Context context){
        Intent it = new Intent(context, UpdateService.class);
        context.startService(it);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化通知栏
     */
    @Override
    public void onCreate() {
        registerBroadcastReceiver();
        filePath = Environment.getExternalStorageDirectory() + "/imooc/down/imooc.apk";
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(false);
            channel.enableLights(false);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }
        notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.icon_imooc)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_imooc))
                .setContentTitle(getString(R.string.app_name));
    }

    /**1.显示其实通知栏
     * 2.开始执行下载任务
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        apkUrl = APK_URL_TITLE;
        //1.显示其实通知栏
        notifyUser(getString(R.string.update_download_start), getString(R.string.update_download_start),0);
        //2.开始执行下载任务
        startDownLoad();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownLoad() {
        UpdateManager.getInstance().startDownload(apkUrl,filePath, new UpdateDownloadListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onPrepared(long contentLength, String downloadUrl) {

            }

            @Override
            public void onProgressChanged(int progress, String downloadUrl) {
                notifyUser(getString(R.string.update_download_processing),
                        getString(R.string.update_download_processing), progress);
            }

            @Override
            public void onPaused(int progress, int completeSize, String downloadUrl) {
                notifyUser(getString(R.string.update_download_failed),
                        getString(R.string.update_download_failed_msg), 0);
                deleteApkFile();
                stopSelf();// 停掉服务自身
            }

            @Override
            public void onFinished(int completeSize, String downloadUrl) {
                notifyUser(getString(R.string.update_download_finish),
                        getString(R.string.update_download_finish), 100);
                stopSelf();// 停掉服务自身
            }

            @Override
            public void onFailure() {
                notifyUser(getString(R.string.update_download_failed),
                        getString(R.string.update_download_failed_msg), 0);
                deleteApkFile();
                stopSelf();// 停掉服务自身
            }
        });
    }

    private void notifyUser(String tickerMsg,String message,int progress) {
        notificationBuilder.setTicker(tickerMsg);
        if(progress>=0 && progress<100){
            notificationBuilder.setProgress(100,progress,false);
        }else {
            notificationBuilder.setProgress(0,0,false);
            notificationBuilder.setContentText(message);
            //发送广播去安装
            sendInstallBroadcast();
        }
        notification = notificationBuilder.build();
        notificationManager.notify(0,notification);
    }

    private void sendInstallBroadcast() {
        Intent intent = new Intent(UpdateHelper.UPDATE_ACTION);
        intent.putExtra(UPDATE_FILE_KEY, filePath);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    /**
     * 删除无用apk文件
     */
    private boolean deleteApkFile() {
        File apkFile = new File(filePath);
        if (apkFile.exists() && apkFile.isFile()) {
            return apkFile.delete();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadcastReceiver();
    }

    /**
     * 接收Update发送的广播
     */
    public class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //启动安装页面
            context.startActivity(
                    Utils.getInstallApkIntent(context, intent.getStringExtra(UpdateHelper.UPDATE_FILE_KEY)));
        }
    }

    /**
     * 注册广播
     */
    private void registerBroadcastReceiver() {
        if (mReceiver == null) {
            mReceiver = new UpdateReceiver();
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(mReceiver, new IntentFilter(UpdateHelper.UPDATE_ACTION));
        }
    }

    /**
     * 反注册广播
     */
    private void unRegisterBroadcastReceiver() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        }
    }

}
