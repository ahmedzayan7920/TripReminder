package com.example.tripreminder;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.tripreminder.MainActivity.CHANNEL_ID;
import static com.example.tripreminder.MainActivity.NOTIFICATION_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyJobService extends JobService {
    private Trip trip;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        if (MainActivity.mainActivity != null){
            MainActivity.mainActivity.finish();
        }

        String key = jobParameters.getExtras().getString("trip_key");
        String title = jobParameters.getExtras().getString("title");
        String body = jobParameters.getExtras().getString("body");
        String repeat = jobParameters.getExtras().getString("trip_repeat");

        Intent intent = new Intent(this, DialogActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("key", key);
        startActivity(intent);


        Intent resultIntent = new Intent(this, DialogActivity.class);
        resultIntent.putExtra("key", key);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Name", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel Description");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_test)
                .setAutoCancel(false)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_ALARM);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, builder.build());
/*
        if (repeat.equals("Daily")){
            getTrip(key);
        }*/
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
/*
    private void getTrip(String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot t : snapshot.getChildren()) {
                    trip = t.getValue(Trip.class);
                    String k = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                    trip.setKey(k);
                    int i = trip.getDate().getDate();
                    Calendar d = new Date();
                    d = trip.getDate();
                    d.setDate(i+1);
                    trip.setDate(d);
                    FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(k).setValue(trip);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

}