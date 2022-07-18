package com.anna.lib_keepalive.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.anna.lib_keepalive.forground.ForgroundNF;

public class CancelNotifyervice extends Service {
    ForgroundNF _mForgroundNF;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG","onCreate--");
        _mForgroundNF = new ForgroundNF(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG","onStartCommand--");
        _mForgroundNF.startForegroundNotification();
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("TAG","onDestroy--");
        super.onDestroy();
        _mForgroundNF.stopForegroundNotification();
    }
}
