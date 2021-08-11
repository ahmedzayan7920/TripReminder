package com.example.tripreminder;

import static com.example.tripreminder.MainActivity.NOTIFICATION_ID;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class DialogActivity extends AppCompatActivity {

    private TextView tvName;
    private Button btnCancel;
    private Button btnLater;
    private Button btnStart;
    private Uri notification;
    private Ringtone r;

    private Trip tr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        Log.i("01230123", "Dialog Shown");

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
                Intent intent = new Intent(getApplicationContext(), BubbleService.class);
                intent.putExtra("notes", tr.getNotes());
                startService(intent);
                tr.setState("Done");
                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tr.getKey()).setValue(tr);
                finish();
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
}