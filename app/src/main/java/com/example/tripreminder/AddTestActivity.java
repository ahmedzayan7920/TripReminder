package com.example.tripreminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddTestActivity extends AppCompatActivity {

    private EditText addEtName;
    private EditText addEtStart;
    private EditText addEtEnd;
    private EditText addEtNotes;

    private TextView addTvGoDate;
    private TextView addTvGoTime;
    private TextView addTvReturnDate;
    private TextView addTvReturnTime;

    private Spinner spinnerRepeat;
    private Spinner spinnerWay;

    private Button addBtnAddNotes;
    private Button addBtnSave;

    private TripTest trip;
    private Calendar allGoDate;
    private Calendar allReturnDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);

        addEtName = findViewById(R.id.add_et_name);
        addEtStart = findViewById(R.id.add_et_start);
        addEtEnd = findViewById(R.id.add_et_end);
        addEtNotes = findViewById(R.id.add_et_notes);
        addTvGoDate = findViewById(R.id.add_tv_go_date);
        addTvGoTime = findViewById(R.id.add_tv_go_time);
        addTvReturnDate = findViewById(R.id.add_tv_return_date);
        addTvReturnTime = findViewById(R.id.add_tv_return_time);
        spinnerRepeat = findViewById(R.id.add_spinner_repeat);
        spinnerWay = findViewById(R.id.add_spinner_way);
        addBtnAddNotes = findViewById(R.id.add_btn_add_notes);
        addBtnSave = findViewById(R.id.add_btn_save);

        allGoDate = Calendar.getInstance();
        allReturnDate = Calendar.getInstance();
        String key = getIntent().getStringExtra(UpComingFragment.TRIP_KEY);
        if (key != null) {
            FirebaseDatabase.getInstance().getReference("Trips").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            trip = snapshot.getValue(TripTest.class);
                            if (trip != null){
                                addEtName.setText(trip.getName());
                                addEtStart.setText(trip.getStart());
                                addEtEnd.setText(trip.getEnd());
                                if (trip.getWay().contains("One")) {
                                    spinnerWay.setSelection(0);
                                    addTvGoDate.setText(trip.getGoDate().get(Calendar.YEAR) + "/" + (trip.getGoDate().get(Calendar.MONTH) + 1) + "/" + trip.getGoDate().get(Calendar.DAY_OF_MONTH));
                                    allGoDate.set(Calendar.YEAR, trip.getGoDate().get(Calendar.YEAR));
                                    allGoDate.set(Calendar.MONTH, trip.getGoDate().get(Calendar.MONTH));
                                    allGoDate.set(Calendar.DAY_OF_MONTH, trip.getGoDate().get(Calendar.DAY_OF_MONTH));
                                    allGoDate.set(Calendar.DAY_OF_MONTH, trip.getGoDate().get(Calendar.DAY_OF_MONTH));
                                    allGoDate.set(Calendar.DAY_OF_MONTH, trip.getGoDate().get(Calendar.DAY_OF_MONTH));

                                    addTvGoTime.setText(trip.getGoDate().get(Calendar.HOUR_OF_DAY) + ":" + trip.getGoDate().get(Calendar.MINUTE));
                                    allGoDate.set(Calendar.HOUR_OF_DAY, trip.getGoDate().get(Calendar.HOUR_OF_DAY));
                                    allGoDate.set(Calendar.MINUTE, trip.getGoDate().get(Calendar.MINUTE));
                                } else {
                                    spinnerWay.setSelection(1);
                                    addTvReturnDate.setText(trip.getReturnDate().get(Calendar.YEAR) + "/" + (trip.getReturnDate().get(Calendar.MONTH) + 1) + "/" + trip.getReturnDate().get(Calendar.DAY_OF_MONTH));
                                    allReturnDate.set(Calendar.YEAR, trip.getReturnDate().get(Calendar.YEAR));
                                    allReturnDate.set(Calendar.MONTH, trip.getReturnDate().get(Calendar.MONTH));
                                    allReturnDate.set(Calendar.DAY_OF_MONTH, trip.getReturnDate().get(Calendar.DAY_OF_MONTH));
                                    allReturnDate.set(Calendar.DAY_OF_MONTH, trip.getReturnDate().get(Calendar.DAY_OF_MONTH));
                                    allReturnDate.set(Calendar.DAY_OF_MONTH, trip.getReturnDate().get(Calendar.DAY_OF_MONTH));

                                    addTvReturnTime.setText(trip.getReturnDate().get(Calendar.HOUR_OF_DAY) + ":" + trip.getReturnDate().get(Calendar.MINUTE));
                                    allReturnDate.set(Calendar.HOUR_OF_DAY, trip.getReturnDate().get(Calendar.HOUR_OF_DAY));
                                    allReturnDate.set(Calendar.MINUTE, trip.getReturnDate().get(Calendar.MINUTE));
                                }

                                switch (trip.getRepeat()) {
                                    case "Daily":
                                        spinnerRepeat.setSelection(1);
                                        break;
                                    case "Weekly":
                                        spinnerRepeat.setSelection(2);
                                        break;
                                    case "Monthly":
                                        spinnerRepeat.setSelection(3);
                                        break;
                                    default:
                                        spinnerRepeat.setSelection(0);
                                        break;
                                }

                                addEtNotes.setText(trip.getNotes());
                            }
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
        spinnerRepeat.setAdapter(dataAdapterRepeat);

        List<String> listTrip = new ArrayList<>();
        listTrip.add("One way Trip");
        listTrip.add("Round Trip");
        ArrayAdapter<String> dataAdapterTrip = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listTrip);
        dataAdapterTrip.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWay.setAdapter(dataAdapterTrip);


        addTvGoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (key == null) {
                    Calendar c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR);
                    DatePickerDialog d = new DatePickerDialog(AddTestActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            addTvGoDate.setText(i + "/" + (i1 + 1) + "/" + i2);
                            Log.i("012301230123", i+"");
                            Toast.makeText(getApplicationContext(), i+"", Toast.LENGTH_SHORT).show();
                            allGoDate.set(Calendar.YEAR, i);
                            allGoDate.set(Calendar.MONTH, i1);
                            allGoDate.set(Calendar.DAY_OF_MONTH, i2);
                        }
                    }, year, month, day);
                    d.show();
                } else {

                    DatePickerDialog d = new DatePickerDialog(AddTestActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            addTvGoDate.setText(i + "/" + (i1 + 1) + "/" + i2);
                            Log.i("012301230123", i+"");
                            Toast.makeText(getApplicationContext(), i+"", Toast.LENGTH_SHORT).show();
                            allGoDate.set(Calendar.YEAR, i);
                            allGoDate.set(Calendar.MONTH, i1);
                            allGoDate.set(Calendar.DAY_OF_MONTH, i2);
                        }
                    }, trip.getGoDate().get(Calendar.YEAR), trip.getGoDate().get(Calendar.MONTH), trip.getGoDate().get(Calendar.DAY_OF_MONTH));
                    d.show();
                }


            }
        });


        addTvGoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (key == null) {
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog t = new TimePickerDialog(AddTestActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            Log.i("01598753", i + " hour" + i1 + " minute");

                            if (i >= 13){
                                if (i1 >= 10){
                                    addTvGoTime.setText((i-12) + ":" + i1 + " pm");
                                }else{
                                    addTvGoTime.setText((i-12) + ":" + "0"+i1 + " pm");
                                }

                            }else {
                                if (i1 >= 10){
                                    addTvGoTime.setText((i) + ":" + i1 + " am");
                                }else{
                                    addTvGoTime.setText((i) + ":" + "0"+i1 + " am");
                                }
                            }
                            allGoDate.set(Calendar.HOUR_OF_DAY, i);
                            allGoDate.set(Calendar.MINUTE, i1);
                            allGoDate.set(Calendar.SECOND, 0);
                            allGoDate.set(Calendar.MILLISECOND, 0);
                        }
                    }, hour, minute, false);
                    t.show();
                } else {
                    TimePickerDialog t = new TimePickerDialog(AddTestActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {

                            if (i >= 13){
                                if (i1 >= 10){
                                    addTvGoTime.setText((i-12) + ":" + i1 + " pm");
                                }else{
                                    addTvGoTime.setText((i-12) + ":" + "0"+i1 + " pm");
                                }

                            }else {
                                if (i1 >= 10){
                                    addTvGoTime.setText((i) + ":" + i1 + " am");
                                }else{
                                    addTvGoTime.setText((i) + ":" + "0"+i1 + " am");
                                }
                            }
                            allGoDate.set(Calendar.HOUR, i);
                            allGoDate.set(Calendar.MINUTE, i1);
                            allGoDate.set(Calendar.SECOND, 0);
                            allGoDate.set(Calendar.MILLISECOND, 0);
                        }
                    }, trip.getGoDate().get(Calendar.HOUR_OF_DAY), trip.getGoDate().get(Calendar.MINUTE), false);
                    t.show();
                }

            }
        });

        addTvReturnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (key == null) {
                    Calendar c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR);
                    DatePickerDialog d = new DatePickerDialog(AddTestActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            addTvReturnDate.setText(i + "/" + (i1 + 1) + "/" + i2);
                            Log.i("012301230123", i+"");
                            Toast.makeText(getApplicationContext(), i+"", Toast.LENGTH_SHORT).show();
                            allReturnDate.set(Calendar.YEAR, i);
                            allReturnDate.set(Calendar.MONTH, i1);
                            allReturnDate.set(Calendar.DAY_OF_MONTH, i2);
                        }
                    }, year, month, day);
                    d.show();
                } else {

                    DatePickerDialog d = new DatePickerDialog(AddTestActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            addTvReturnDate.setText(i + "/" + (i1 + 1) + "/" + i2);
                            Log.i("012301230123", i+"");
                            Toast.makeText(getApplicationContext(), i+"", Toast.LENGTH_SHORT).show();
                            allReturnDate.set(Calendar.YEAR, i);
                            allReturnDate.set(Calendar.MONTH, i1);
                            allReturnDate.set(Calendar.DAY_OF_MONTH, i2);
                        }
                    }, trip.getReturnDate().get(Calendar.YEAR), trip.getReturnDate().get(Calendar.MONTH), trip.getReturnDate().get(Calendar.DAY_OF_MONTH));
                    d.show();
                }
            }
        });


        addTvReturnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (key == null) {
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog t = new TimePickerDialog(AddTestActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            Log.i("01598753", i + " hour" + i1 + " minute");

                            if (i >= 13){
                                if (i1 >= 10){
                                    addTvReturnTime.setText((i-12) + ":" + i1 + " pm");
                                }else{
                                    addTvReturnTime.setText((i-12) + ":" + "0"+i1 + " pm");
                                }

                            }else {
                                if (i1 >= 10){
                                    addTvReturnTime.setText((i) + ":" + i1 + " am");
                                }else{
                                    addTvReturnTime.setText((i) + ":" + "0"+i1 + " am");
                                }
                            }
                            allReturnDate.set(Calendar.HOUR_OF_DAY, i);
                            allReturnDate.set(Calendar.MINUTE, i1);
                            allReturnDate.set(Calendar.SECOND, 0);
                            allReturnDate.set(Calendar.MILLISECOND, 0);
                        }
                    }, hour, minute, false);
                    t.show();
                } else {
                    TimePickerDialog t = new TimePickerDialog(AddTestActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {

                            if (i >= 13){
                                if (i1 >= 10){
                                    addTvReturnTime.setText((i-12) + ":" + i1 + " pm");
                                }else{
                                    addTvReturnTime.setText((i-12) + ":" + "0"+i1 + " pm");
                                }

                            }else {
                                if (i1 >= 10){
                                    addTvReturnTime.setText((i) + ":" + i1 + " am");
                                }else{
                                    addTvReturnTime.setText((i) + ":" + "0"+i1 + " am");
                                }
                            }
                            allReturnDate.set(Calendar.HOUR, i);
                            allReturnDate.set(Calendar.MINUTE, i1);
                            allReturnDate.set(Calendar.SECOND, 0);
                            allReturnDate.set(Calendar.MILLISECOND, 0);
                        }
                    }, trip.getReturnDate().get(Calendar.HOUR_OF_DAY), trip.getReturnDate().get(Calendar.MINUTE), false);
                    t.show();
                }
            }
        });

        addBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addEtName.getText().toString().isEmpty() && !addEtStart.getText().toString().isEmpty() && !addEtEnd.getText().toString().isEmpty() &&
                        !addTvGoDate.getText().toString().isEmpty() && !addTvGoTime.getText().toString().isEmpty()) {
                    if (spinnerWay.getSelectedItem().equals("Round Trip")){
                        if (!addTvReturnDate.getText().toString().isEmpty() && !addTvReturnTime.getText().toString().isEmpty()){
                            if (key == null) {
                                String name = addEtName.getText().toString();
                                String start = addEtStart.getText().toString();
                                String end = addEtEnd.getText().toString();
                                String w = spinnerWay.getSelectedItem().toString();
                                String r = spinnerRepeat.getSelectedItem().toString();
                                String n = addEtNotes.getText().toString();
                                String key = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("goDate", allGoDate);
                                hashMap.put("returnDate", allReturnDate);
                                hashMap.put("name", name);
                                hashMap.put("state", "upcoming");
                                hashMap.put("start", start);
                                hashMap.put("end", end);
                                hashMap.put("key", key);
                                hashMap.put("notes", n);
                                hashMap.put("way", w);
                                hashMap.put("repeat", r);
                                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(hashMap);
                                finish();

                            } else {
                                String name = addEtName.getText().toString();
                                String start = addEtStart.getText().toString();
                                String end = addEtEnd.getText().toString();
                                String w = spinnerWay.getSelectedItem().toString();
                                String r = spinnerRepeat.getSelectedItem().toString();
                                String n = addEtNotes.getText().toString();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("goDate", allGoDate);
                                hashMap.put("returnDate", allReturnDate);
                                hashMap.put("name", name);
                                hashMap.put("state", "upcoming");
                                hashMap.put("start", start);
                                hashMap.put("end", end);
                                hashMap.put("key", key);
                                hashMap.put("notes", n);
                                hashMap.put("way", w);
                                hashMap.put("repeat", r);
                                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(hashMap);
                                finish();
                            }
                        }else{
                            if (addTvReturnDate.getText().toString().isEmpty()) {
                                Toast.makeText(AddTestActivity.this, "Please Enter Return Date", Toast.LENGTH_SHORT).show();
                            } else if (addTvReturnTime.getText().toString().isEmpty()) {
                                Toast.makeText(AddTestActivity.this, "Please Enter Return Time", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        if (key == null) {
                            String name = addEtName.getText().toString();
                            String start = addEtStart.getText().toString();
                            String end = addEtEnd.getText().toString();
                            String w = spinnerWay.getSelectedItem().toString();
                            String r = spinnerRepeat.getSelectedItem().toString();
                            String n = addEtNotes.getText().toString();
                            String key = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("goDate", allGoDate);
                            hashMap.put("name", name);
                            hashMap.put("state", "upcoming");
                            hashMap.put("start", start);
                            hashMap.put("end", end);
                            hashMap.put("key", key);
                            hashMap.put("notes", n);
                            hashMap.put("way", w);
                            hashMap.put("repeat", r);
                            FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(hashMap);
                            finish();

                        } else {
                            String name = addEtName.getText().toString();
                            String start = addEtStart.getText().toString();
                            String end = addEtEnd.getText().toString();
                            String w = spinnerWay.getSelectedItem().toString();
                            String r = spinnerRepeat.getSelectedItem().toString();
                            String n = addEtNotes.getText().toString();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("goDate", allGoDate);
                            hashMap.put("name", name);
                            hashMap.put("state", "upcoming");
                            hashMap.put("start", start);
                            hashMap.put("end", end);
                            hashMap.put("key", key);
                            hashMap.put("notes", n);
                            hashMap.put("way", w);
                            hashMap.put("repeat", r);
                            FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(hashMap);
                            finish();
                        }
                    }

                } else {
                    if (addEtName.getText().toString().isEmpty()) {
                        Toast.makeText(AddTestActivity.this, "Please Enter Trip Name", Toast.LENGTH_SHORT).show();
                    } else if (addEtStart.getText().toString().isEmpty()) {
                        Toast.makeText(AddTestActivity.this, "Please Enter Trip Start Point", Toast.LENGTH_SHORT).show();
                    } else if (addEtEnd.getText().toString().isEmpty()) {
                        Toast.makeText(AddTestActivity.this, "Please Enter Trip End Point", Toast.LENGTH_SHORT).show();
                    } else if (addTvGoDate.getText().toString().isEmpty()) {
                        Toast.makeText(AddTestActivity.this, "Please Enter Trip Date", Toast.LENGTH_SHORT).show();
                    } else if (addTvGoTime.getText().toString().isEmpty()) {
                        Toast.makeText(AddTestActivity.this, "Please Enter Trip Time", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }
}