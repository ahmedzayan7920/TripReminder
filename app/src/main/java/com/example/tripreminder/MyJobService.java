package com.example.tripreminder;

import static com.example.tripreminder.MainActivity.CHANNEL_ID;
import static com.example.tripreminder.MainActivity.NOTIFICATION_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i("01230123", "started from job");
        Toast.makeText(this, "started", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel Description");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("My Title")
                .setContentText("This is the Body")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setCategory(NotificationCompat.CATEGORY_ALARM);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, builder.build());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("01230123", "stopped");
        Toast.makeText(this, "stopped", Toast.LENGTH_SHORT).show();
        return false;
    }


}
