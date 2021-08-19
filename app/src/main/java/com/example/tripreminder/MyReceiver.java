package com.example.tripreminder;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.tripreminder.MainActivity.CHANNEL_ID;
import static com.example.tripreminder.MainActivity.NOTIFICATION_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MainActivity.mainActivity != null){
            MainActivity.mainActivity.finish();
        }

        String key = intent.getStringExtra("trip_key");
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");

        Intent intent1 = new Intent(context, DialogActivity.class);
        intent1.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent1.addFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
        intent1.putExtra("key", key);
        context.startActivity(intent1);


        Intent resultIntent = new Intent(context, DialogActivity.class);
        resultIntent.putExtra("key", key);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel Description");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_test)
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(NOTIFICATION_ID, builder.build());

    }
}