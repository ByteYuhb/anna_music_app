package com.anna.lib_audio_player.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.anna.lib_audio_player.AudioProvider;
import com.anna.lib_audio_player.media.db.GreenDaoHelper;

public class AudioHelper {
    //SDK全局Context, 供子模块用
    public static Context mContext;
    //全局服务
    public static AudioProvider provider;

    /**设置全局context且bind音乐服务
     * @param context
     */
    public static void init(Context context) {
        mContext = context;
        GreenDaoHelper.initDatabase();
        bindAudioService();
    }

    /**
     * 释放音乐服务
     */
    public static void release(){
        try {
            provider.release();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mContext.unbindService(serviceConnection);
        mContext = null;
    }
    public static Context getmContext() {
        return mContext;
    }

    /**
     * 绑定音乐服务
     */
    private static void bindAudioService() {
        Intent it = new Intent("com.anna.media.audio.action");
        it.setPackage(mContext.getPackageName());
        mContext.bindService(it,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("TAG","服务绑定成功");
            provider = AudioProvider.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            provider = null;
            Log.d("TAG","服务解绑成功");
        }
    };
}
