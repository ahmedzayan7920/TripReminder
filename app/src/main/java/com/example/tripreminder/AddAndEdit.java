package com.example.tripreminder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    Calendar allDate;
    Trip trip;

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
        allDate = Calendar.getInstance();
        calendar = Calendar.getInstance();
        repeat = findViewById(R.id.repeat);

        String key = getIntent().getStringExtra(UpComingFragment.TRIP_KEY);
        if (key != null) {
            FirebaseDatabase.getInstance().getReference("Trips").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            trip = snapshot.getValue(Trip.class);
                            if (trip != null){
                                etName.setText(trip.getName());
                                etStart.setText(trip.getStart());
                                etEnd.setText(trip.getEnd());
                                if (trip.getWay().contains("One")) {
                                    way.setSelection(0);
                                } else {
                                    way.setSelection(1);
                                }

                                switch (trip.getRepeat()) {
                                    case "Daily":
                                        repeat.setSelection(1);
                                        break;
                                    case "Weekly":
                                        repeat.setSelection(2);
                                        break;
                                    case "Monthly":
                                        repeat.setSelection(3);
                                        break;
                                    default:
                                        repeat.setSelection(0);
                                        break;
                                }
                                tvDate.setText(trip.getDate().get(Calendar.YEAR) + "/" + (trip.getDate().get(Calendar.MONTH) + 1) + "/" + trip.getDate().get(Calendar.DAY_OF_MONTH));
                                allDate.set(Calendar.YEAR, trip.getDate().get(Calendar.YEAR));
                                allDate.set(Calendar.MONTH, trip.getDate().get(Calendar.MONTH));
                                allDate.set(Calendar.DAY_OF_MONTH, trip.getDate().get(Calendar.DAY_OF_MONTH));
                                allDate.set(Calendar.DAY_OF_MONTH, trip.getDate().get(Calendar.DAY_OF_MONTH));
                                allDate.set(Calendar.DAY_OF_MONTH, trip.getDate().get(Calendar.DAY_OF_MONTH));

                                tvTime.setText(trip.getDate().get(Calendar.HOUR_OF_DAY) + ":" + trip.getDate().get(Calendar.MINUTE));
                                allDate.set(Calendar.HOUR_OF_DAY, trip.getDate().get(Calendar.HOUR_OF_DAY));
                                allDate.set(Calendar.MINUTE, trip.getDate().get(Calendar.MINUTE));

                                notes.setText(trip.getNotes());
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
        repeat.setAdapter(dataAdapterRepeat);

        repeat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });


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
                            Log.i("012301230123", i+"");
                            Toast.makeText(getApplicationContext(), i+"", Toast.LENGTH_SHORT).show();
                            allDate.set(Calendar.YEAR, i);
                            allDate.set(Calendar.MONTH, i1);
                            allDate.set(Calendar.DAY_OF_MONTH, i2);
                        }
                    }, year, month, day);
                    d.show();
                } else {

                    DatePickerDialog d = new DatePickerDialog(AddAndEdit.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            tvDate.setText(i + "/" + (i1 + 1) + "/" + i2);
                            Log.i("012301230123", i+"");
                            Toast.makeText(getApplicationContext(), i+"", Toast.LENGTH_SHORT).show();
                            allDate.set(Calendar.YEAR, i);
                            allDate.set(Calendar.MONTH, i1);
                            allDate.set(Calendar.DAY_OF_MONTH, i2);
                        }
                    }, trip.getDate().get(Calendar.YEAR), trip.getDate().get(Calendar.MONTH), trip.getDate().get(Calendar.DAY_OF_MONTH));
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
                            Log.i("01598753", i + " hour" + i1 + " minute");

                            if (i >= 13){
                                if (i1 >= 10){
                                    tvTime.setText((i-12) + ":" + i1 + " pm");
                                }else{
                                    tvTime.setText((i-12) + ":" + "0"+i1 + " pm");
                                }

                            }else {
                                if (i1 >= 10){
                                    tvTime.setText((i) + ":" + i1 + " am");
                                }else{
                                    tvTime.setText((i) + ":" + "0"+i1 + " am");
                                }
                            }

                            allDate.set(Calendar.HOUR_OF_DAY, i);
                            allDate.set(Calendar.MINUTE, i1);
                            allDate.set(Calendar.SECOND, 0);
                            allDate.set(Calendar.MILLISECOND, 0);
                        }
                    }, hour, minute, false);
                    t.show();
                } else {
                    TimePickerDialog t = new TimePickerDialog(AddAndEdit.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {

                            if (i >= 13){
                                if (i1 >= 10){
                                    tvTime.setText((i-12) + ":" + i1 + " pm");
                                }else{
                                    tvTime.setText((i-12) + ":" + "0"+i1 + " pm");
                                }

                            }else {
                                if (i1 >= 10){
                                    tvTime.setText((i) + ":" + i1 + " am");
                                }else{
                                    tvTime.setText((i) + ":" + "0"+i1 + " am");
                                }
                            }
                            allDate.set(Calendar.HOUR, i);
                            allDate.set(Calendar.MINUTE, i1);
                            allDate.set(Calendar.SECOND, 0);
                            allDate.set(Calendar.MILLISECOND, 0);
                        }
                    }, trip.getDate().get(Calendar.HOUR_OF_DAY), trip.getDate().get(Calendar.MINUTE), false);
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
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("date", allDate);
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
                        name = etName.getText().toString();
                        start = etStart.getText().toString();
                        end = etEnd.getText().toString();
                        String w = way.getSelectedItem().toString();
                        String r = repeat.getSelectedItem().toString();
                        String n = notes.getText().toString();

                        Trip t = new Trip(allDate, name, "upcoming", start, end, key, n, w, r);
                        FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(t);
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
