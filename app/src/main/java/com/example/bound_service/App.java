package com.example.bound_service;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.ArrayList;

public class App extends Application {
    public static final String CHANNEL_ONE = "mychannelone";
    public static final String CHANNEL_TWO = "mychanneltwo";

    @Override
    public void onCreate() {
        super.onCreate();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ONE,"channelone",
                    NotificationManager.IMPORTANCE_HIGH);

            channel1.setDescription("channel for high IMportant notification");


            NotificationChannel channel2 = new NotificationChannel(CHANNEL_TWO,"channeltwo",
                    NotificationManager.IMPORTANCE_LOW);

            channel2.setDescription("channel for high low notification");

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            ArrayList<NotificationChannel> arrayList = new ArrayList();
            arrayList.add(channel1);
            arrayList.add(channel2);

            manager.createNotificationChannels(arrayList);
        }


    }
}
