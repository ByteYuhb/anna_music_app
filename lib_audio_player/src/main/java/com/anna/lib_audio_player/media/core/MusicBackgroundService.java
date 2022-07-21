package com.anna.lib_audio_player.media.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.anna.lib_audio_player.api.AudioHelper;

public class MusicBackgroundService extends Service {
    private static final String MUSIC_START_ACTION = "music_action_start";
    private static final String MUSIC_DATA_AUDIOS = "music_data_audios";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}
