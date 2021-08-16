package com.example.tripreminder;

import static com.example.tripreminder.MainActivity.NOTIFICATION_ID;
import static com.example.tripreminder.MainActivity.flag;
import static com.example.tripreminder.UpComingFragment.jobID;

import android.Manifest;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DialogActivity extends AppCompatActivity {

    private TextView tvName;
    private Button btnCancel;
    private Button btnLater;
    private Button btnStart;
    private Uri notification;
    private Ringtone r;

    private TripTest tr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        if (flag) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            flag = false;
        }

        String key = getIntent().getStringExtra("key");
        getTrip(key);
        try {
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setFinishOnTouchOutside(false);
        tvName = findViewById(R.id.tv_dialog_trip_name);
        btnCancel = findViewById(R.id.btn_dialog_cancel);
        btnLater = findViewById(R.id.btn_dialog_later);
        btnStart = findViewById(R.id.btn_dialog_start);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r.stop();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(NOTIFICATION_ID);
                tr.setState("Canceled");
                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(tr);
                finish();
            }
        });

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r.stop();
                finish();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r.stop();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(NOTIFICATION_ID);
                if (tr.getWay().equals("One way Trip")) {
                    Intent intent = new Intent(getApplicationContext(), BubbleService.class);
                    intent.putStringArrayListExtra("notes", tr.getNotes());
                    startService(intent);

                    if (ActivityCompat.checkSelfPermission(DialogActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(DialogActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                        ActivityCompat.requestPermissions(DialogActivity.this, permissions, 101);
                    }
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + tr.getEnd());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                    tr.setState("Done");
                    FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tr.getKey()).setValue(tr);
                    finish();

                } else {
                    String key = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("goDate", tr.getGoDate());
                    hashMap.put("name", tr.getName());
                    hashMap.put("state", "Done");
                    hashMap.put("start", tr.getStart());
                    hashMap.put("end", tr.getEnd());
                    hashMap.put("key", key);
                    hashMap.put("notes", tr.getNotes());
                    hashMap.put("way", "One way Trip");
                    hashMap.put("repeat", tr.getRepeat());
                    FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(hashMap);

                    String start = tr.getStart();
                    String end = tr.getEnd();
                    tr.setStart(tr.getEnd());
                    tr.setEnd(start);
                    tr.setGoDate(tr.getReturnDate());

                    long time = tr.getGoDate().getTimeInMillis() - System.currentTimeMillis();
                    JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                    ComponentName componentName = new ComponentName(getApplicationContext(), MyJobService.class);
                    JobInfo info;
                    PersistableBundle bundle = new PersistableBundle();
                    bundle.putString("trip_key", tr.getKey());
                    bundle.putString("trip_repeat", tr.getRepeat());
                    bundle.putString("title", tr.getName());
                    bundle.putString("body", "From " + tr.getStart() + " to " + tr.getEnd());
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                        info = new JobInfo.Builder(jobID, componentName)
                                .setPeriodic(time)
                                .setExtras(bundle)
                                .build();
                    } else {
                        info = new JobInfo.Builder(jobID, componentName)
                                .setMinimumLatency(time)
                                .setExtras(bundle)
                                .build();
                    }

                    Log.i("01230123", "Job " + jobID + " added after :  " + time + "  Milliseconds");
                    scheduler.schedule(info);
                    jobID++;

                    tr.setWay("One way Trip");
                    FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tr.getKey()).setValue(tr);
                    if (ActivityCompat.checkSelfPermission(DialogActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(DialogActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                        ActivityCompat.requestPermissions(DialogActivity.this, permissions, 101);
                    }
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + end);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);


                    Intent intent = new Intent(getApplicationContext(), BubbleService.class);
                    intent.putStringArrayListExtra("notes", tr.getNotes());
                    startService(intent);
                    finish();
                }
            }
        });

    }

    private void getTrip(String k) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(k);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String end = (String) snapshot.child("end").getValue();
                String key = (String) snapshot.child("key").getValue();
                String name = (String) snapshot.child("name").getValue();
                ArrayList<String> notes = (ArrayList<String>) snapshot.child("notes").getValue();
                String repeat = (String) snapshot.child("repeat").getValue();
                String start = (String) snapshot.child("start").getValue();
                String state = (String) snapshot.child("state").getValue();
                String way = (String) snapshot.child("way").getValue();
                if (way.equals("One way Trip")) {
                    Calendar goDate = Calendar.getInstance();
                    goDate.set(Calendar.YEAR, snapshot.child("goDate").child("weekYear").getValue(Integer.class));
                    goDate.set(Calendar.MONTH, snapshot.child("goDate").child("time").child("month").getValue(Integer.class));
                    goDate.set(Calendar.DAY_OF_MONTH, snapshot.child("goDate").child("time").child("date").getValue(Integer.class));
                    goDate.set(Calendar.HOUR_OF_DAY, snapshot.child("goDate").child("time").child("hours").getValue(Integer.class));
                    goDate.set(Calendar.MINUTE, snapshot.child("goDate").child("time").child("minutes").getValue(Integer.class));
                    goDate.set(Calendar.SECOND, snapshot.child("goDate").child("time").child("seconds").getValue(Integer.class));

                    tr = new TripTest(goDate, name, state, start, end, key, notes, way, repeat);

                } else {
                    Calendar goDate = Calendar.getInstance();
                    goDate.set(Calendar.YEAR, snapshot.child("goDate").child("weekYear").getValue(Integer.class));
                    goDate.set(Calendar.MONTH, snapshot.child("goDate").child("time").child("month").getValue(Integer.class));
                    goDate.set(Calendar.DAY_OF_MONTH, snapshot.child("goDate").child("time").child("date").getValue(Integer.class));
                    goDate.set(Calendar.HOUR_OF_DAY, snapshot.child("goDate").child("time").child("hours").getValue(Integer.class));
                    goDate.set(Calendar.MINUTE, snapshot.child("goDate").child("time").child("minutes").getValue(Integer.class));
                    goDate.set(Calendar.SECOND, snapshot.child("goDate").child("time").child("seconds").getValue(Integer.class));

                    Calendar returnDate = Calendar.getInstance();
                    returnDate.set(Calendar.YEAR, snapshot.child("returnDate").child("weekYear").getValue(Integer.class));
                    returnDate.set(Calendar.MONTH, snapshot.child("returnDate").child("time").child("month").getValue(Integer.class));
                    returnDate.set(Calendar.DAY_OF_MONTH, snapshot.child("returnDate").child("time").child("date").getValue(Integer.class));
                    returnDate.set(Calendar.HOUR_OF_DAY, snapshot.child("returnDate").child("time").child("hours").getValue(Integer.class));
                    returnDate.set(Calendar.MINUTE, snapshot.child("returnDate").child("time").child("minutes").getValue(Integer.class));
                    returnDate.set(Calendar.SECOND, snapshot.child("returnDate").child("time").child("seconds").getValue(Integer.class));

                    tr = new TripTest(goDate, returnDate, name, state, start, end, key, notes, way, repeat);
                }
                tvName.setText(tr.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}