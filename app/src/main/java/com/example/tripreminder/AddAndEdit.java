package com.example.tripreminder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class AddAndEdit extends AppCompatActivity {
    TextView tvTime, tvDate;
    EditText etName, etStart, etEnd;
    MultiAutoCompleteTextView notes;
    Button btn;
    Calendar c;
    Spinner way;
    Spinner repeat;
    ImageView date, time;
    Calendar calendar;
    String name;
    String start;
    String end;
    Date allDate;
    Trip trip;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_edit);

        tvTime = findViewById(R.id.add_tv_time);
        tvDate = findViewById(R.id.add_tv_date);
        way = findViewById(R.id.trip);
        date = findViewById(R.id.iv_date);
        time = findViewById(R.id.iv_time);
        btn = findViewById(R.id.addTrip);
        notes = findViewById(R.id.note);
        etName = findViewById(R.id.et_name);
        etStart = findViewById(R.id.et_start);
        etEnd = findViewById(R.id.et_end);
        allDate = new Date();
        calendar = Calendar.getInstance();
        repeat = findViewById(R.id.repeat);

        String key = getIntent().getStringExtra(UpComingFragment.TRIP_KEY);
        if (key == null) {

        } else {
            FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    trip = snapshot.getValue(Trip.class);
                    etName.setText(trip.getName());
                    etStart.setText(trip.getStart());
                    etEnd.setText(trip.getEnd());
                    if (trip.getWay().contains("One")) {
                        way.setSelection(0);
                    } else {
                        way.setSelection(1);
                    }

                    if (trip.getRepeat().equals("Daily")) {
                        repeat.setSelection(1);
                    } else if (trip.getRepeat().equals("Weekly")) {
                        repeat.setSelection(2);
                    } else if (trip.getRepeat().equals("Monthly")) {
                        repeat.setSelection(3);
                    } else {
                        repeat.setSelection(0);
                    }
                    tvDate.setText(trip.getDate().getYear() + "/" + (trip.getDate().getMonth() + 1) + "/" + trip.getDate().getDate());
                    allDate.setYear(trip.getDate().getYear());
                    allDate.setMonth(trip.getDate().getMonth());
                    allDate.setDate(trip.getDate().getDate());

                    tvTime.setText(trip.getDate().getHours() + ":" + trip.getDate().getMinutes());
                    allDate.setHours(trip.getDate().getHours());
                    allDate.setMinutes(trip.getDate().getMinutes());

                    notes.setText(trip.getNotes());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        List<String> listRepeat = new ArrayList<>();
        listRepeat.add("No Repeated");
        listRepeat.add("Daily");
        listRepeat.add("Weekly");
        listRepeat.add("Monthly");
        ArrayAdapter<String> dataAdapterRepeat = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listRepeat);
        dataAdapterRepeat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeat.setAdapter(dataAdapterRepeat);


        List<String> listTrip = new ArrayList<>();
        listTrip.add("One way Trip");
        listTrip.add("Round Trip");
        ArrayAdapter<String> dataAdapterTrip = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listTrip);
        dataAdapterTrip.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        way.setAdapter(dataAdapterTrip);


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (key == null) {
                    c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR);

                    DatePickerDialog d = new DatePickerDialog(AddAndEdit.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            tvDate.setText(i + "/" + (i1 + 1) + "/" + i2);
                            allDate.setYear(i);
                            allDate.setMonth(i1);
                            allDate.setDate(i2);
                        }
                    }, year, month, day);
                    d.show();
                } else {

                    DatePickerDialog d = new DatePickerDialog(AddAndEdit.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            tvDate.setText(i + "/" + (i1 + 1) + "/" + i2);
                            allDate.setYear(i);
                            allDate.setMonth(i1);
                            allDate.setDate(i2);
                        }
                    }, trip.getDate().getYear(), trip.getDate().getMonth(), trip.getDate().getDate());
                    d.show();
                }


            }
        });


        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (key == null) {
                    c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog t = new TimePickerDialog(AddAndEdit.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            tvTime.setText(i + ":" + i1);
                            allDate.setHours(i);
                            allDate.setMinutes(i1);
                        }
                    }, hour, minute, true);
                    t.show();
                } else {
                    TimePickerDialog t = new TimePickerDialog(AddAndEdit.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            tvTime.setText(i + ":" + i1);
                            allDate.setHours(i);
                            allDate.setMinutes(i1);
                        }
                    }, trip.getDate().getHours(), trip.getDate().getMinutes(), true);
                    t.show();
                }

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etName.getText().toString().isEmpty() && !etStart.getText().toString().isEmpty() && !etEnd.getText().toString().isEmpty() &&
                        !tvDate.getText().toString().isEmpty() && !tvTime.getText().toString().isEmpty()) {
                    if (key == null) {
                        name = etName.getText().toString();
                        start = etStart.getText().toString();
                        end = etEnd.getText().toString();
                        String w = way.getSelectedItem().toString();
                        String r = repeat.getSelectedItem().toString();
                        String n = notes.getText().toString();

                        String key = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                        Trip t = new Trip(allDate, name, "upcoming", start, end, key, n, w, r);
                        FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(t);
/*
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, t.getDate().getYear());
                        calendar.set(Calendar.MONTH, t.getDate().getMonth());
                        calendar.set(Calendar.DAY_OF_MONTH, t.getDate().getDate());
                        calendar.set(Calendar.HOUR_OF_DAY, t.getDate().getHours());
                        calendar.set(Calendar.MINUTE, t.getDate().getMinutes());
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        long time = calendar.getTimeInMillis() - System.currentTimeMillis();
                        Log.i("01230123", "Job added after :  "+time+"  Milliseconds");
                        ComponentName componentName = new ComponentName(getBaseContext(), MyJobService.class);
                        JobInfo info;
                        PersistableBundle bundle = new PersistableBundle();
                        bundle.putString("trip_key", key);
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N){
                            info = new JobInfo.Builder(10,componentName)
                                    .setPeriodic(time)
                                    .setExtras(bundle)
                                    .build();
                        }else{
                            info = new JobInfo.Builder(10,componentName)
                                    .setMinimumLatency(time)
                                    .setExtras(bundle)
                                    .build();
                        }

                        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                        scheduler.schedule(info);

*/
                        finish();
                    } else {
                        name = etName.getText().toString();
                        start = etStart.getText().toString();
                        end = etEnd.getText().toString();
                        String w = way.getSelectedItem().toString();
                        String r = repeat.getSelectedItem().toString();
                        String n = notes.getText().toString();

                        Trip t = new Trip(allDate, name, "upcoming", start, end, key, n, w, r);
                        FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(t);
/*
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, t.getDate().getYear());
                        calendar.set(Calendar.MONTH, t.getDate().getMonth());
                        calendar.set(Calendar.DAY_OF_MONTH, t.getDate().getDate());
                        calendar.set(Calendar.HOUR_OF_DAY, t.getDate().getHours());
                        calendar.set(Calendar.MINUTE, t.getDate().getMinutes());
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        long time = calendar.getTimeInMillis() - System.currentTimeMillis();
                        Log.i("01230123", "Job added after :  "+time+"  Milliseconds");
                        ComponentName componentName = new ComponentName(getBaseContext(), MyJobService.class);
                        JobInfo info;
                        PersistableBundle bundle = new PersistableBundle();
                        bundle.putString("trip_key", key);
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N){
                            info = new JobInfo.Builder(10,componentName)
                                    .setPeriodic(time)
                                    .setExtras(bundle)
                                    .build();
                        }else{
                            info = new JobInfo.Builder(10,componentName)
                                    .setMinimumLatency(time)
                                    .setExtras(bundle)
                                    .build();
                        }

                        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                        scheduler.schedule(info);*/
                        finish();
                    }
                } else {
                    if (etName.getText().toString().isEmpty()) {
                        Toast.makeText(AddAndEdit.this, "Please Enter Trip Name", Toast.LENGTH_SHORT).show();
                    } else if (etStart.getText().toString().isEmpty()) {
                        Toast.makeText(AddAndEdit.this, "Please Enter Trip Start Point", Toast.LENGTH_SHORT).show();
                    } else if (etEnd.getText().toString().isEmpty()) {
                        Toast.makeText(AddAndEdit.this, "Please Enter Trip End Point", Toast.LENGTH_SHORT).show();
                    } else if (tvDate.getText().toString().isEmpty()) {
                        Toast.makeText(AddAndEdit.this, "Please Enter Trip Date", Toast.LENGTH_SHORT).show();
                    } else if (tvTime.getText().toString().isEmpty()) {
                        Toast.makeText(AddAndEdit.this, "Please Enter Trip Time", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

}
