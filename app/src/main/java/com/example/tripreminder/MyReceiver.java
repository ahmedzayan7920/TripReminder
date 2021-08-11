package com.example.tripreminder;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.tripreminder.MainActivity.CHANNEL_ID;
import static com.example.tripreminder.MainActivity.NOTIFICATION_ID;

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
    public void onReceive(Context context, Intent intent1) {
        String key = intent1.getStringExtra("trip_key");

        Intent resultIntent = new Intent(context, DialogActivity.class);
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
                .setContentTitle("My Title")
                .setContentText("This is the Body")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_ALARM);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(NOTIFICATION_ID, builder.build());

        Intent intent = new Intent(context, DialogActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("key", key);
        context.startActivity(intent);
    }
}