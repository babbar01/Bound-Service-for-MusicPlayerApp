package com.example.bound_service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MusicPlayerService extends Service {

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
        return mybinder;
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
