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
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        if (MainActivity.mainActivity != null){
            MainActivity.mainActivity.finish();
        }

        String key = getInputData().getString("trip_key");
        String title = getInputData().getString("title");
        String body = getInputData().getString("body");

        Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra("key", key);
        getApplicationContext().startActivity(intent);


        Intent resultIntent = new Intent(getApplicationContext(), DialogActivity.class);
        resultIntent.putExtra("key", key);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel Description");
            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_test)
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        managerCompat.notify(NOTIFICATION_ID, builder.build());

        return Result.success();
    }
}
