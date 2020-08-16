package com.example.bound_service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MusicPlayerService extends Service {

    private static final String MYTAG = "debug";
    public IBinder mybinder = new Mybinder();
    MediaPlayer mediaPlayer = null;
    private boolean mediaStatus = false;


    public MusicPlayerService() {
    }

    public class Mybinder extends Binder{

        public MusicPlayerService getServiceReference() {
            return MusicPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(MYTAG, "onBind: ");
        return mybinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(MYTAG, "onUnbind: ");
        return true;
        // because we returned true here therefore onRebind will be called if we bind the app again
        // from background i.e if we maximise the app again after minimising

    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(MYTAG, "onRebind: ");
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction())
        {
            case constants.NOTIFICATION_ACTION_PLAY:{
                play();
                break;
            }
            case constants.NOTIFICATION_ACTION_PAUSE:{
                pause();
                break;
            }
            case constants.NOTIFICATION_ACTION_STOP:{
                stop();
                break;
            }
            case constants.NOTIFICATION_ACTION_START:{
                showNotification();
                break;
            }
            default:{
                stopSelf();
            }
        }
        Log.d(MYTAG, "onStartCommand: ");
        return START_NOT_STICKY;
    }

    private void  showNotification() {

        Intent playIntent = new Intent(this,MusicPlayerService.class);
        playIntent.setAction(constants.NOTIFICATION_ACTION_PLAY);

        PendingIntent playPendingIntent = PendingIntent.getService(this,0,playIntent,0);


        Intent pauseIntent = new Intent(this,MusicPlayerService.class);
        pauseIntent.setAction(constants.NOTIFICATION_ACTION_PAUSE);

        PendingIntent pausePendingIntent = PendingIntent.getService(this,0,pauseIntent,0);

        Intent stopIntent = new Intent(this,MusicPlayerService.class);
        stopIntent.setAction(constants.NOTIFICATION_ACTION_STOP);

        PendingIntent stopPendingIntent = PendingIntent.getService(this,0,stopIntent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (this,App.CHANNEL_TWO)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setChannelId(App.CHANNEL_TWO)
                .addAction(R.mipmap.ic_launcher,"PLAY",playPendingIntent)
                .addAction(R.mipmap.ic_launcher,"PAUSE",pausePendingIntent)
                .addAction(R.mipmap.ic_launcher,"STOP",stopPendingIntent)
                .setContentTitle("Music Running")
                .setContentText("currently music is playing in background");

        startForeground(1,builder.build());


    }

    boolean isPlaying()
    {
        return mediaStatus;
    }

    void play()
    {
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this,R.raw.sample);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaStatus = false;
                    Intent intent = new Intent("MusicCompletion");
                    intent.putExtra("result","done");

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    stopForeground(true);

                    stopSelf();
                }
            });
        }

        mediaPlayer.start();
        mediaStatus = true;
    }

    void pause()
    {
        if(mediaPlayer!=null) {
            mediaPlayer.pause();
            mediaStatus = false;
        }
    }

    void stop(){
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            mediaStatus = false;
            stopForeground(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(MYTAG, "onDestroy: ");



        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
