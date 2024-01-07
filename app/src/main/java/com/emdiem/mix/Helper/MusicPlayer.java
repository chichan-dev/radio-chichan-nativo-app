package com.emdiem.mix.Helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

public class MusicPlayer extends Service {

    private static SimpleExoPlayer exoPlayer;

    public static SimpleExoPlayer getInstance() {
        return exoPlayer;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (exoPlayer == null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("action");

        if ("play".equals(action)) {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(true);
            }
        } else if ("pause".equals(action)) {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false);
            }
        }

        return 1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Libera tu ExoPlayer cuando ya no lo necesites
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}