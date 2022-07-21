package com.anna.music;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.anna.lib_audio_player.AudioProvider;
import com.anna.lib_audio_player.api.AudioHelper;
import com.anna.lib_audio_player.media.db.GreenDaoHelper;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AudioHelper.init(this);
    }
}
