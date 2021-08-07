package com.example.tripreminder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class AddAndEdit extends AppCompatActivity {
    TextView tvTime , tvDate;
    EditText etName, etStart, etEnd;
    MultiAutoCompleteTextView notes;
    Button btn;
    Calendar c;
    Spinner way;
    Spinner repeat;
    ImageView date ,time;
    Calendar calendar;
    String name;
    String start;
    String end;
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
        calendar = Calendar.getInstance();
        repeat = findViewById(R.id.repeat);

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
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                DatePickerDialog d = new DatePickerDialog(AddAndEdit.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        tvDate.setText(i+"/"+(i1+1)+"/"+i2);
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);
                    }
                }, year, month, day);
                d.show();
            }
        });


        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog t = new TimePickerDialog(AddAndEdit.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        tvTime.setText(i+":"+i1);
                        calendar.set(Calendar.HOUR_OF_DAY, i);
                        calendar.set(Calendar.MINUTE, i1);
                    }
                }, hour, minute, true);
                t.show();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                start = etStart.getText().toString();
                end = etEnd.getText().toString();
                String w = way.getSelectedItem().toString();
                String r = repeat.getSelectedItem().toString();
                String n = notes.getText().toString();

                String key =  FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                Trip t = new Trip(calendar, name, "upcoming", start, end, key, n, w, r);
                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(t);
                finish();
            }
        });
    }

}
