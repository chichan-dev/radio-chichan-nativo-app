package com.emdiem.mix.Service;

import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_NEXT;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PAUSE;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PLAY;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.emdiem.mix.Helper.MusicPlayer;
import com.emdiem.mix.Helper.NotificationActionService;
import com.emdiem.mix.MainActivity.MainActivity;
import com.github.skykai.stickercamera.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import android.os.Handler;
import android.os.Looper;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PlaybackService extends Service {

    public static ExoPlayer exoPlayer;
    public Boolean isRunning = false;
    public Messenger mMessenger;
    public Integer mStationId = 100;
    public Boolean justStopped = false;
    public TelephonyManager mgr;
    public PhoneStateListener mPhoneStateListener;
    public AudioManager mAudioManager;
    public AudioManager.OnAudioFocusChangeListener mAudioFocusListener;
    public String mSongUrl;
    private static final int FOREGROUND_SERVICE_ID = 10212;
    private MediaSessionCompat mediaSessionCompat;

    public IBinder onBind(Intent arg0) {

        mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                if (state == TelephonyManager.CALL_STATE_RINGING)
                    stop();
                else if(state == TelephonyManager.CALL_STATE_OFFHOOK)
                    stop();

                super.onCallStateChanged(state, incomingNumber);
            }
        };
        mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if(mgr != null)
            mgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAudioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    // Pause playback mAudioManager.abandonAudioFocus(mAudioFocusListener);
                    stop();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                    justStopped = true;
                } /**else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                 // Resume playback
                 } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                 // Stop playback
                 mAudioManager.abandonAudioFocus(mAudioFocusListener);
                 stop();
                 NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                 notificationManager.cancel(1);
                 justStopped = true;
                 }**/
            }
        };


        exoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        exoPlayer.addListener(new ExoPlayer.EventListener(){

            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(!exoPlayer.getPlayWhenReady() && playbackState == ExoPlayer.STATE_READY) {
                    exoPlayer.setPlayWhenReady(true);
                    Toast.makeText(getApplicationContext(), "Conectado", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

    }

    public void stop(){
        if(exoPlayer != null && exoPlayer.getPlayWhenReady()){
            exoPlayer.stop();
            justStopped = true;
        }
    }

    private void initializeMediaPlayer(String audio_url) {
        Uri uri = Uri.parse(audio_url);
        Log.i("ulr", audio_url);
        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return  new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    public int onStartCommand(Intent intent, final int flags, int startId) {
        if(intent == null || intent.getAction() == null )
            return 0;

        if(intent.getAction().equals("play")) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.get("handler") != null) // Attach handler
                    mMessenger = (Messenger) extras.get("handler");

                if ((intent.getIntExtra("stationId", 100) == mStationId) && exoPlayer.getPlayWhenReady())
                    return 0;

                mStationId = intent.getIntExtra("stationId", 0);
            }

                AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isRunning)
                            return;
                        isRunning = true;

                        startForeground(FOREGROUND_SERVICE_ID, getNotification("PLAY Radio"));
                        initializeMediaPlayer("https://playerservices.streamtheworld.com/api/livestream-redirect/PLAY_PANAMAAAC.aac?dist=lakyPanaWeb?dist=PlayPanaWeb");
                        isRunning = false;
                        shareState(true);

                        Log.d("Service Log", "I'm here");
                    }
                });
        }

        if(intent.getAction().equals("play-track")){
            Bundle extras = intent.getExtras();

            if(intent.getExtras().get("handler") != null) // Attach handler
                mMessenger = (Messenger)intent.getExtras().get("handler");

            justStopped = false;

            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(mSongUrl == intent.getExtras().getString("track")) {
                            stop();
                            return;
                        }
                        mSongUrl = intent.getExtras().getString("track");
                        System.out.println("Track: " + mSongUrl);
                        if(mSongUrl != null) {
                            if (isRunning)
                                return;

                            try {

                                isRunning = true;
                                exoPlayer.seekTo(0);
                                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), "yourApplicationName"));
                                //MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(mSongUrl));
                                //exoPlayer.prepare(videoSource);
                                exoPlayer.setPlayWhenReady(true);
                                startForeground(FOREGROUND_SERVICE_ID, getNotification("Play"));

                            }catch (IllegalStateException e){

                            }
                        }
                        /*
                        exoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {

                                Bundle mData = new Bundle();
                                mData.putBoolean("finished", true);
                                mData.putString("track", mSongUrl);

                                Message mMessage = new Message();
                                mMessage.setData(mData);

                                try {

                                    if (mMessenger != null)
                                        mMessenger.send(mMessage);

                                } catch (RemoteException exception) {

                                }

                            }
                        });
*/
                    }catch (Exception exception2){

                    }

                    isRunning = false;
                    shareState(true);

                }
            });
        }

        if(intent.getAction().equals("pause")) {
            exoPlayer.setPlayWhenReady(false);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
            justStopped = true;
        }

        if(intent.getAction().equals("stop")) {
            exoPlayer.stop();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
            justStopped = true;
        }

        if(intent.getAction().equals("force-stop")) {
            this.forceStop();
        }

        if(intent.getAction().equals("start") && !isRunning) {
            exoPlayer.setPlayWhenReady(true);
        }

        if(intent.getAction().equals("get-progress")){
            broadcastTrackProgress();
        }

        if(intent.getAction().equals("attach")) {
            mMessenger = (Messenger) intent.getExtras().get("handler");

            /**SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
            int stationId = sharedPreferences.getInt("stationId", 100);

            Bundle mData = new Bundle();

            mData.putBoolean("playing", false);
            mData.putInt("stationId", stationId);
            mData.putBoolean("running", false);
            mData.putBoolean("justPlaying", false);

            Message mMessage = new Message();
            mMessage.setData(mData);

            try {
                if (mMessenger != null) {
                    mMessenger.send(mMessage);
                }
            } catch (RemoteException exception) {
                Log.d("Exception", "RemoteException thrown");
            }**/

            shareState(false);

            return 1;

        }

        // shareState(false);

        return 1;
    }

    public void forceStop(){
        stopForeground(true);
        stopSelf();

        if(exoPlayer != null && exoPlayer.getPlayWhenReady()){
            exoPlayer.stop();

            Bundle mData = new Bundle();

            mData.putBoolean("force-stop", true);
            mData.putBoolean("just-stopped", true);

            Message mMessage = new Message();
            mMessage.setData(mData);


            justStopped = true;

            try {
                if (mMessenger != null) {
                    mMessenger.send(mMessage);
                }
            } catch (RemoteException exception) {
                Log.d("Exception", "RemoteException thrown");
            }

        }
    }

    public void broadcastTrackProgress(){

        Bundle mData = new Bundle();

        if(mSongUrl != null && exoPlayer.getPlayWhenReady() && !mSongUrl.equals("")) {

            Log.d("Current", exoPlayer.getCurrentPosition() + "ms");
            Log.d("Current", exoPlayer.getDuration() + "ms");

            mData.putInt("progress", (int) Math.round((((double) exoPlayer.getCurrentPosition() / (double) exoPlayer.getDuration()) * 100)));

        }

        Message mMessage = new Message();
        mMessage.setData(mData);

        try {
            if(mMessenger != null) {
                mMessenger.send(mMessage);
            }
        }catch (RemoteException exception){
            Log.d("Exception", exception.getLocalizedMessage());
        }


    }

    public void shareState(Boolean justPlaying){
        // Share the state of the player
        Bundle mData = new Bundle();

        try {
            mData.putBoolean("playing", exoPlayer.getPlayWhenReady());
        }catch (IllegalStateException e){
            mData.putBoolean("playing", false);
        }

        mData.putInt("stationId", mStationId);
        mData.putBoolean("running", isRunning);
        mData.putBoolean("justPlaying", justPlaying);
        mData.putBoolean("just-stopped", justStopped);

        /**if(mPlayer != null)
            mData.putBoolean("playing", mPlayer.isPlaying());**/

        /**if (mTrack != null)
            mData.putString("track", mTrack.getRawData().toString());**/

        Message mMessage = new Message();
        mMessage.setData(mData);

        try {
            if (mMessenger != null) {
                mMessenger.send(mMessage);
            }
        } catch (RemoteException exception) {
            Log.d("Exception", "RemoteException thrown");
        }
    }

    public Notification getNotification(String songName){
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel);
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_view);
        notificationView.setTextViewText(R.id.name, songName);

        Intent mIntent = new Intent(getApplicationContext(), PlaybackService.class);
        mIntent.setAction("force-stop");

        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getService(
                getApplicationContext(),
                0,
                mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        notificationView.setOnClickPendingIntent(R.id.stop, pendingIntent);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        @SuppressLint("WrongConstant") PendingIntent pendingNotificationIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );


//        Intent intentPlay = new Intent(this, PlaybackService.class)
//                .setAction(ACTION_PLAY);
//        @SuppressLint("WrongConstant") PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(this, 0,
//                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        Intent intentPause = new Intent(this, PlaybackService.class)
//                .setAction(ACTION_PAUSE);
//        @SuppressLint("WrongConstant") PendingIntent pendingIntentPause = PendingIntent.getBroadcast(this, 0,
//                intentPause, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent intentPlay = new Intent(this, PlaybackService.class)
                .setAction("play");

        // Añade datos al Intent
        intentPlay.putExtra("handler", mMessenger);
        intentPlay.putExtra("stationId", mStationId);

        PendingIntent pendingIntentPlay = PendingIntent.getService(this, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPause = new Intent(this, PlaybackService.class)
                .setAction("stop");
        PendingIntent pendingIntentPause = PendingIntent.getService(this, 0,
                intentPause, PendingIntent.FLAG_UPDATE_CURRENT);



        mBuilder.setContentTitle("Playback Service")
                .setContentText(songName)
                .setSmallIcon(R.mipmap.ic_launcher) // Reemplaza esto con tu propio ícono
                .setContentIntent(pendingNotificationIntent)
                .setOngoing(true);


        // Configurar el estilo de la notificación para mostrar las acciones en la vista compacta
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mediaSessionCompat = new MediaSessionCompat(PlaybackService.this, "Tag");

                mBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                );
            }
        });



        // Agregar las acciones a la notificación
        mBuilder.addAction(R.drawable.ic_play_arrow_24dp, "Play", pendingIntentPlay);
        mBuilder.addAction(R.drawable.ic_stop_blue_24dp, "Pause", pendingIntentPause);

        Notification notification = mBuilder
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        return notification;
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "MyChannel";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel("MyChannel", name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "MyChannel";
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {


        if(mgr != null) {
            mgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    @Override
    public void onDestroy() {
        exoPlayer.stop();
        exoPlayer.release();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    @Override
    public void onLowMemory() {
        Log.d("Low memory", "LOWWWWW MEMORYYYYY");
    }

    private class getXML extends AsyncTask<Object, Object, Void> {
        @Override
        protected Void doInBackground(Object... params) {
                initializeMediaPlayer("https://playerservices.streamtheworld.com/api/livestream-redirect/PLAY_PANAMAAAC.aac?dist=lakyPanaWeb?dist=PlayPanaWeb");
                isRunning = false;
                shareState(true);

                Log.d("Service Log", "I'm here");
            return null;
        }
    }
}
