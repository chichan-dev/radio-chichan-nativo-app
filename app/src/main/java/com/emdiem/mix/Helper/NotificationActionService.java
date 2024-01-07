package com.emdiem.mix.Helper;

import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PAUSE;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PLAY;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;



public class NotificationActionService extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        context.sendBroadcast(new Intent("TRACKS_TRACKS")
                .putExtra("actionname", action));

        Intent serviceIntent = new Intent(context, MusicPlayer.class);

        if (ACTION_PLAY.equals(action)) {
            // Aquí, reproduce tu música
            serviceIntent.putExtra("action", "play");
            context.startService(serviceIntent);
            Log.d("BroadcastReceiverTEST", "Playing music");

        }
        if (ACTION_PAUSE.equals(action)) {
            // Aquí, pausa tu música
            serviceIntent.putExtra("action", "pause");
            context.startService(serviceIntent);
            Log.d("BroadcastReceiverTEST", "Pausing music");

        }
    }
}