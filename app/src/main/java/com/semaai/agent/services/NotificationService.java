package com.semaai.agent.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.semaai.agent.R;

public class NotificationService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();

        Intent intent1 = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent1.putExtra(Settings.EXTRA_CHANNEL_ID, "ChannelId1");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification customNotification = new NotificationCompat.Builder(this, "ChannelId1")
                .setSmallIcon(R.drawable.notification_logo)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentTitle("New Alert!")
                .setContentText("You are 500 meters outside of the clock-in area!")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, customNotification);

        return START_STICKY;
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel("ChannelId1",
                    "Foreground notification",
                    NotificationManager.IMPORTANCE_MIN);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
