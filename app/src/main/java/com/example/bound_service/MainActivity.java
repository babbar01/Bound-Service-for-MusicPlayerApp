package com.example.bound_service;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btnStop,btnPlay;
    private TextView tvStatus;
    private MusicPlayerService musicPlayerService;
    private ServiceConnection mserviceConnection;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
              if(intent.getStringExtra("result") == "done"){
                  btnPlay.setText("Play");
                  tvStatus.setText("SONG COMPLETED");
              }
        }
    };

    boolean mbound;
    private String TAG = "debug";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mserviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                MusicPlayerService.Mybinder mybinder = (MusicPlayerService.Mybinder) binder;
                musicPlayerService = mybinder.getServiceReference();

                mbound = true;
                Log.d(TAG, "onServiceConnected: ");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mbound = false;
                Log.d(TAG, "onServiceDisconnected: ");
            }
        };

        btnStop = findViewById(R.id.btnStop);
        btnPlay = findViewById(R.id.btnPlay);
        tvStatus = findViewById(R.id.tvStatus);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onbtnPlayClicked();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onbtnStopClicked();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        Intent intent = new Intent(this,MusicPlayerService.class);
        bindService(intent,mserviceConnection,BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver,
                new IntentFilter("MusicCompletion"));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mbound) {
            unbindService(mserviceConnection);
            Log.d(TAG, "onStop: unbindService called");
            mbound = false;
        }


    }

    private void onbtnPlayClicked() {
        if(!musicPlayerService.isPlaying()){

            Intent intent = new Intent(MainActivity.this,MusicPlayerService.class);
            startService(intent);

            musicPlayerService.play();
            btnPlay.setText("pause");
            tvStatus.setText("PLAYING");
        }
        else{
            musicPlayerService.pause();
            btnPlay.setText("play");
            tvStatus.setText("PAUSED");
        }

    }

    private void onbtnStopClicked() {
        musicPlayerService.stop();
        btnPlay.setText("play");
        tvStatus.setText("STOPPED");

    }


}
