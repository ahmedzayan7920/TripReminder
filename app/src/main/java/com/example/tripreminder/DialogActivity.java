package com.example.tripreminder;

import static com.example.tripreminder.MainActivity.NOTIFICATION_ID;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class DialogActivity extends AppCompatActivity {

    private TextView tvName;
    private Button btnCancel;
    private Button btnLater;
    private Button btnStart;
    private Uri notification;
    private Ringtone r;

    private Trip tr;

    private Date allDate;

    static boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        Log.i("0174469630", "create");
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            if (flag) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                flag = false;
            }
        } else {
            if (flag) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                flag = false;
            }
        }


        allDate = new Date();
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
                    intent.putExtra("notes", tr.getNotes());
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
                    String[] notes = {"set Date", "Set Time"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(DialogActivity.this);
                    builder.setTitle("Set Round Time")
                            .setItems(notes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        Calendar c = Calendar.getInstance();
                                        int day = c.get(Calendar.DAY_OF_MONTH);
                                        int month = c.get(Calendar.MONTH);
                                        int year = c.get(Calendar.YEAR);

                                        DatePickerDialog d = new DatePickerDialog(DialogActivity.this, new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                                allDate.setYear(i);
                                                allDate.setMonth(i1);
                                                allDate.setDate(i2);
                                                notes[0] += "     Done";
                                                builder.show();
                                            }
                                        }, year, month, day);
                                        d.show();
                                    } else if (i == 1) {
                                        Calendar c = Calendar.getInstance();
                                        int hour = c.get(Calendar.HOUR_OF_DAY);
                                        int minute = c.get(Calendar.MINUTE);
                                        TimePickerDialog t = new TimePickerDialog(DialogActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                allDate.setHours(i);
                                                allDate.setMinutes(i1);
                                                notes[1] += "     Done";
                                                builder.show();
                                            }
                                        }, hour, minute, true);
                                        t.show();
                                    }
                                }

                            }).setPositiveButton("Save and Start", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String start = tr.getStart();
                            String end = tr.getEnd();
                            tr.setStart(tr.getEnd());
                            tr.setEnd(start);
                            tr.setDate(allDate);
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
                            intent.putExtra("notes", tr.getNotes());
                            startService(intent);
                            finish();
                        }
                    }).show();
                }
            }
        });

    }

    private void getTrip(String key) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot t : snapshot.getChildren()) {
                    Trip trip = t.getValue(Trip.class);
                    if (trip.getKey().equals(key)) {
                        tr = trip;
                        tvName.setText(tr.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("0174469630", "pause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("0174469630", "start");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("0174469630", "stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("0174469630", "destroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("0174469630", "resume");
    }
}