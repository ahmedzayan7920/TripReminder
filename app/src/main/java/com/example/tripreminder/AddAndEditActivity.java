package com.example.tripreminder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class AddAndEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText addEtName;
    private EditText addEtStart;
    private EditText addEtEnd;
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

    private LinearLayout addLayout;

    public static ArrayList<String> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_edit);

        addEtName = findViewById(R.id.add_et_name);
        addEtStart = findViewById(R.id.add_et_start);
        addEtEnd = findViewById(R.id.add_et_end);
        addTvGoDate = findViewById(R.id.add_tv_go_date);
        addTvGoTime = findViewById(R.id.add_tv_go_time);
        addTvReturnDate = findViewById(R.id.add_tv_return_date);
        addTvReturnTime = findViewById(R.id.add_tv_return_time);
        spinnerRepeat = findViewById(R.id.add_spinner_repeat);
        spinnerWay = findViewById(R.id.add_spinner_way);
        addBtnAddNotes = findViewById(R.id.add_btn_add_notes);
        addBtnSave = findViewById(R.id.add_btn_save);
        addLayout = findViewById(R.id.add_return_layout);

        notes = new ArrayList<>();
        allGoDate = Calendar.getInstance();
        allReturnDate = Calendar.getInstance();
        String key = getIntent().getStringExtra(UpComingFragment.TRIP_KEY);
        if (key != null) {
            FirebaseDatabase.getInstance().getReference("Trips").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String end = (String) snapshot.child("end").getValue();
                            String key = (String) snapshot.child("key").getValue();
                            String name = (String) snapshot.child("name").getValue();
                            notes = (ArrayList<String>) snapshot.child("notes").getValue();
                            String repeat = (String) snapshot.child("repeat").getValue();
                            String start = (String) snapshot.child("start").getValue();
                            String state = (String) snapshot.child("state").getValue();
                            String way = (String) snapshot.child("way").getValue();
                            if (way.equals("One way Trip")){
                                Calendar goDate = Calendar.getInstance();
                                goDate.set(Calendar.YEAR, snapshot.child("goDate").child("weekYear").getValue(Integer.class));
                                goDate.set(Calendar.MONTH, snapshot.child("goDate").child("time").child("month").getValue(Integer.class));
                                goDate.set(Calendar.DAY_OF_MONTH, snapshot.child("goDate").child("time").child("date").getValue(Integer.class));
                                goDate.set(Calendar.HOUR_OF_DAY, snapshot.child("goDate").child("time").child("hours").getValue(Integer.class));
                                goDate.set(Calendar.MINUTE, snapshot.child("goDate").child("time").child("minutes").getValue(Integer.class));
                                goDate.set(Calendar.SECOND, snapshot.child("goDate").child("time").child("seconds").getValue(Integer.class));

                                trip = new TripTest(goDate, name, state, start, end, key, notes, way, repeat);
                            }else {
                                Calendar goDate = Calendar.getInstance();
                                goDate.set(Calendar.YEAR, snapshot.child("goDate").child("weekYear").getValue(Integer.class));
                                goDate.set(Calendar.MONTH, snapshot.child("goDate").child("time").child("month").getValue(Integer.class));
                                goDate.set(Calendar.DAY_OF_MONTH, snapshot.child("goDate").child("time").child("date").getValue(Integer.class));
                                goDate.set(Calendar.HOUR_OF_DAY, snapshot.child("goDate").child("time").child("hours").getValue(Integer.class));
                                goDate.set(Calendar.MINUTE, snapshot.child("goDate").child("time").child("minutes").getValue(Integer.class));
                                goDate.set(Calendar.SECOND, snapshot.child("goDate").child("time").child("seconds").getValue(Integer.class));

                                Calendar returnDate = Calendar.getInstance();
                                returnDate.set(Calendar.YEAR, snapshot.child("goDate").child("weekYear").getValue(Integer.class));
                                returnDate.set(Calendar.MONTH, snapshot.child("goDate").child("time").child("month").getValue(Integer.class));
                                returnDate.set(Calendar.DAY_OF_MONTH, snapshot.child("goDate").child("time").child("date").getValue(Integer.class));
                                returnDate.set(Calendar.HOUR_OF_DAY, snapshot.child("goDate").child("time").child("hours").getValue(Integer.class));
                                returnDate.set(Calendar.MINUTE, snapshot.child("goDate").child("time").child("minutes").getValue(Integer.class));
                                returnDate.set(Calendar.SECOND, snapshot.child("goDate").child("time").child("seconds").getValue(Integer.class));

                                trip = new TripTest(goDate, returnDate, name, state, start, end, key, notes, way, repeat);
                            }

                            if (trip != null) {
                                addEtName.setText(trip.getName());
                                addEtStart.setText(trip.getStart());
                                addEtEnd.setText(trip.getEnd());
                                if (trip.getWay().equals("One way Trip")) {
                                    spinnerWay.setSelection(0);
                                    addTvGoDate.setText(trip.getGoDate().get(Calendar.YEAR) + "/" + (trip.getGoDate().get(Calendar.MONTH) + 1) + "/" + trip.getGoDate().get(Calendar.DAY_OF_MONTH));
                                    allGoDate.set(Calendar.YEAR, trip.getGoDate().get(Calendar.YEAR));
                                    allGoDate.set(Calendar.MONTH, trip.getGoDate().get(Calendar.MONTH));
                                    allGoDate.set(Calendar.DAY_OF_MONTH, trip.getGoDate().get(Calendar.DAY_OF_MONTH));
                                    allGoDate.set(Calendar.HOUR_OF_DAY, trip.getGoDate().get(Calendar.HOUR_OF_DAY));
                                    allGoDate.set(Calendar.MINUTE, trip.getGoDate().get(Calendar.MINUTE));

                                    if (trip.getGoDate().get(Calendar.HOUR_OF_DAY) == 0) {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvGoTime.setText((12) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        } else {
                                            addTvGoTime.setText((12) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        }
                                    } else if (trip.getGoDate().get(Calendar.HOUR_OF_DAY) == 12) {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        } else {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        }

                                    } else if (trip.getGoDate().get(Calendar.HOUR_OF_DAY) >= 13) {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY) - 12) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        } else {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY) - 12) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        }

                                    } else {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        } else {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        }
                                    }
                                    allGoDate.set(Calendar.HOUR_OF_DAY, trip.getGoDate().get(Calendar.HOUR_OF_DAY));
                                    allGoDate.set(Calendar.MINUTE, trip.getGoDate().get(Calendar.MINUTE));
                                } else {
                                    addLayout.setVisibility(View.VISIBLE);
                                    spinnerWay.setSelection(1);
                                    addTvGoDate.setText(trip.getGoDate().get(Calendar.YEAR) + "/" + (trip.getGoDate().get(Calendar.MONTH) + 1) + "/" + trip.getGoDate().get(Calendar.DAY_OF_MONTH));
                                    allGoDate.set(Calendar.YEAR, trip.getGoDate().get(Calendar.YEAR));
                                    allGoDate.set(Calendar.MONTH, trip.getGoDate().get(Calendar.MONTH));
                                    allGoDate.set(Calendar.DAY_OF_MONTH, trip.getGoDate().get(Calendar.DAY_OF_MONTH));
                                    allGoDate.set(Calendar.HOUR_OF_DAY, trip.getGoDate().get(Calendar.HOUR_OF_DAY));
                                    allGoDate.set(Calendar.MINUTE, trip.getGoDate().get(Calendar.MINUTE));


                                    if (trip.getGoDate().get(Calendar.HOUR_OF_DAY) == 0) {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvGoTime.setText((12) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        } else {
                                            addTvGoTime.setText((12) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        }
                                    } else if (trip.getGoDate().get(Calendar.HOUR_OF_DAY) == 12) {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        } else {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        }

                                    } else if (trip.getGoDate().get(Calendar.HOUR_OF_DAY) >= 13) {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY) - 12) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        } else {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY) - 12) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        }

                                    } else {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        } else {
                                            addTvGoTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        }
                                    }
                                    allGoDate.set(Calendar.HOUR_OF_DAY, trip.getGoDate().get(Calendar.HOUR_OF_DAY));
                                    allGoDate.set(Calendar.MINUTE, trip.getGoDate().get(Calendar.MINUTE));

                                    addTvReturnDate.setText(trip.getReturnDate().get(Calendar.YEAR) + "/" + (trip.getReturnDate().get(Calendar.MONTH) + 1) + "/" + trip.getReturnDate().get(Calendar.DAY_OF_MONTH));
                                    allReturnDate.set(Calendar.YEAR, trip.getReturnDate().get(Calendar.YEAR));
                                    allReturnDate.set(Calendar.MONTH, trip.getReturnDate().get(Calendar.MONTH));
                                    allReturnDate.set(Calendar.DAY_OF_MONTH, trip.getReturnDate().get(Calendar.DAY_OF_MONTH));
                                    allReturnDate.set(Calendar.HOUR_OF_DAY, trip.getReturnDate().get(Calendar.HOUR_OF_DAY));
                                    allReturnDate.set(Calendar.MINUTE, trip.getReturnDate().get(Calendar.MINUTE));

                                    addTvReturnTime.setText(trip.getReturnDate().get(Calendar.HOUR_OF_DAY) + ":" + trip.getReturnDate().get(Calendar.MINUTE));

                                    if (trip.getGoDate().get(Calendar.HOUR_OF_DAY) == 0) {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvReturnTime.setText((12) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        } else {
                                            addTvReturnTime.setText((12) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        }
                                    } else if (trip.getGoDate().get(Calendar.HOUR_OF_DAY) == 12) {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvReturnTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        } else {
                                            addTvReturnTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        }

                                    } else if (trip.getGoDate().get(Calendar.HOUR_OF_DAY) >= 13) {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvReturnTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY) - 12) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        } else {
                                            addTvReturnTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY) - 12) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " pm");
                                        }

                                    } else {
                                        if (trip.getGoDate().get(Calendar.MINUTE) >= 10) {
                                            addTvReturnTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        } else {
                                            addTvReturnTime.setText((trip.getGoDate().get(Calendar.HOUR_OF_DAY)) + ":" + "0" + trip.getGoDate().get(Calendar.MINUTE) + " am");
                                        }
                                    }
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
                R.layout.spinner_list, listRepeat);
        dataAdapterRepeat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeat.setAdapter(dataAdapterRepeat);


        List<String> listTrip = new ArrayList<>();
        listTrip.add("One way Trip");
        listTrip.add("Round Trip");
        ArrayAdapter<String> dataAdapterTrip = new ArrayAdapter<>(this,
                R.layout.spinner_list, listTrip);
        dataAdapterTrip.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWay.setAdapter(dataAdapterTrip);
        spinnerWay.setOnItemSelectedListener(this);

        addTvGoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Calendar c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR);
                    DatePickerDialog d = new DatePickerDialog(AddAndEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            addTvGoDate.setText(i + "/" + (i1 + 1) + "/" + i2);
                            allGoDate.set(Calendar.YEAR, i);
                            allGoDate.set(Calendar.MONTH, i1);
                            allGoDate.set(Calendar.DAY_OF_MONTH, i2);
                        }
                    }, year, month, day);
                    d.show();

            }
        });


        addTvGoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog t = new TimePickerDialog(AddAndEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {

                            if (i == 0) {
                                if (i1 >= 10) {
                                    addTvGoTime.setText((12) + ":" + i1 + " am");
                                } else {
                                    addTvGoTime.setText((12) + ":" + "0" + i1 + " am");
                                }
                            } else if (i == 12) {
                                if (i1 >= 10) {
                                    addTvGoTime.setText((i) + ":" + i1 + " pm");
                                } else {
                                    addTvGoTime.setText((i) + ":" + "0" + i1 + " pm");
                                }

                            } else if (i >= 13) {
                                if (i1 >= 10) {
                                    addTvGoTime.setText((i - 12) + ":" + i1 + " pm");
                                } else {
                                    addTvGoTime.setText((i - 12) + ":" + "0" + i1 + " pm");
                                }

                            } else {
                                if (i1 >= 10) {
                                    addTvGoTime.setText((i) + ":" + i1 + " am");
                                } else {
                                    addTvGoTime.setText((i) + ":" + "0" + i1 + " am");
                                }
                            }
                            allGoDate.set(Calendar.HOUR_OF_DAY, i);
                            allGoDate.set(Calendar.MINUTE, i1);
                            allGoDate.set(Calendar.SECOND, 0);
                            allGoDate.set(Calendar.MILLISECOND, 0);
                        }
                    }, hour, minute, false);
                    t.show();

            }
        });

        addTvReturnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Calendar c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR);
                    DatePickerDialog d = new DatePickerDialog(AddAndEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            addTvReturnDate.setText(i + "/" + (i1 + 1) + "/" + i2);
                            allReturnDate.set(Calendar.YEAR, i);
                            allReturnDate.set(Calendar.MONTH, i1);
                            allReturnDate.set(Calendar.DAY_OF_MONTH, i2);
                        }
                    }, year, month, day);
                    d.show();
            }
        });


        addTvReturnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog t = new TimePickerDialog(AddAndEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {

                            if (i == 0) {
                                if (i1 >= 10) {
                                    addTvReturnTime.setText((12) + ":" + i1 + " am");
                                } else {
                                    addTvReturnTime.setText((12) + ":" + "0" + i1 + " am");
                                }
                            } else if (i == 12) {
                                if (i1 >= 10) {
                                    addTvReturnTime.setText((i) + ":" + i1 + " pm");
                                } else {
                                    addTvReturnTime.setText((i) + ":" + "0" + i1 + " pm");
                                }

                            } else if (i >= 13) {
                                if (i1 >= 10) {
                                    addTvReturnTime.setText((i - 12) + ":" + i1 + " pm");
                                } else {
                                    addTvReturnTime.setText((i - 12) + ":" + "0" + i1 + " pm");
                                }

                            } else {
                                if (i1 >= 10) {
                                    addTvReturnTime.setText((i) + ":" + i1 + " am");
                                } else {
                                    addTvReturnTime.setText((i) + ":" + "0" + i1 + " am");
                                }
                            }
                            allReturnDate.set(Calendar.HOUR_OF_DAY, i);
                            allReturnDate.set(Calendar.MINUTE, i1);
                            allReturnDate.set(Calendar.SECOND, 0);
                            allReturnDate.set(Calendar.MILLISECOND, 0);
                        }
                    }, hour, minute, false);
                    t.show();

            }
        });

        addBtnAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddAndEditActivity.this, AddNotesActivity.class);
                intent.putExtra("key", notes);
                startActivity(intent);
            }
        });

        addBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addEtName.getText().toString().isEmpty() && !addEtStart.getText().toString().isEmpty() && !addEtEnd.getText().toString().isEmpty() &&
                        !addTvGoDate.getText().toString().isEmpty() && !addTvGoTime.getText().toString().isEmpty()) {
                    if (spinnerWay.getSelectedItem().equals("Round Trip")) {
                        if (!addTvReturnDate.getText().toString().isEmpty() && !addTvReturnTime.getText().toString().isEmpty()) {
                            String name = addEtName.getText().toString();
                            String start = addEtStart.getText().toString();
                            String end = addEtEnd.getText().toString();
                            String w = spinnerWay.getSelectedItem().toString();
                            String r = spinnerRepeat.getSelectedItem().toString();
                            if (notes.isEmpty()){
                                notes.add("");
                            }
                            if (key == null) {
                                String key = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("goDate", allGoDate);
                                hashMap.put("returnDate", allReturnDate);
                                hashMap.put("name", name);
                                hashMap.put("state", "upcoming");
                                hashMap.put("start", start);
                                hashMap.put("end", end);
                                hashMap.put("key", key);
                                hashMap.put("notes", notes);
                                hashMap.put("way", w);
                                hashMap.put("repeat", r);
                                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(hashMap);

                            } else {

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("goDate", allGoDate);
                                hashMap.put("returnDate", allReturnDate);
                                hashMap.put("name", name);
                                hashMap.put("state", "upcoming");
                                hashMap.put("start", start);
                                hashMap.put("end", end);
                                hashMap.put("key", key);
                                hashMap.put("notes", notes);
                                hashMap.put("way", w);
                                hashMap.put("repeat", r);
                                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(hashMap);
                            }
                            finish();
                        } else {
                            if (addTvReturnDate.getText().toString().isEmpty()) {
                                addTvReturnTime.setError("Please Enter Return Date");
                                Toast.makeText(AddAndEditActivity.this, "Please Enter Return Date", Toast.LENGTH_SHORT).show();
                            } else if (addTvReturnTime.getText().toString().isEmpty()) {
                                addTvReturnTime.setError("Please Enter Return Time");
                                Toast.makeText(AddAndEditActivity.this, "Please Enter Return Time", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        String name = addEtName.getText().toString();
                        String start = addEtStart.getText().toString();
                        String end = addEtEnd.getText().toString();
                        String w = spinnerWay.getSelectedItem().toString();
                        String r = spinnerRepeat.getSelectedItem().toString();
                        if (notes.isEmpty()){
                            notes.add("");
                        }
                        if (key == null) {
                            String key = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("goDate", allGoDate);
                            hashMap.put("name", name);
                            hashMap.put("state", "upcoming");
                            hashMap.put("start", start);
                            hashMap.put("end", end);
                            hashMap.put("key", key);
                            hashMap.put("notes", notes);
                            hashMap.put("way", w);
                            hashMap.put("repeat", r);
                            FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(hashMap);

                        } else {

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("goDate", allGoDate);
                            hashMap.put("name", name);
                            hashMap.put("state", "upcoming");
                            hashMap.put("start", start);
                            hashMap.put("end", end);
                            hashMap.put("key", key);
                            hashMap.put("notes", notes);
                            hashMap.put("way", w);
                            hashMap.put("repeat", r);
                            FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(hashMap);
                        }
                        finish();
                    }

                } else {
                    if (addEtName.getText().toString().isEmpty()) {
                        addEtName.setError("Please Enter Trip Name");
                        Toast.makeText(AddAndEditActivity.this, "Please Enter Trip Name", Toast.LENGTH_SHORT).show();
                    } else if (addEtStart.getText().toString().isEmpty()) {
                        addEtStart.setError("Please Enter Trip Start Point");
                        Toast.makeText(AddAndEditActivity.this, "Please Enter Trip Start Point", Toast.LENGTH_SHORT).show();
                    } else if (addEtEnd.getText().toString().isEmpty()) {
                        addEtEnd.setError("Please Enter Trip End Point");
                        Toast.makeText(AddAndEditActivity.this, "Please Enter Trip End Point", Toast.LENGTH_SHORT).show();
                    } else if (addTvGoDate.getText().toString().isEmpty()) {
                        addTvGoDate.setError("Please Enter Trip Date");
                        Toast.makeText(AddAndEditActivity.this, "Please Enter Trip Date", Toast.LENGTH_SHORT).show();
                    } else if (addTvGoTime.getText().toString().isEmpty()) {
                        addTvGoTime.setError("Please Enter Trip Time");
                        Toast.makeText(AddAndEditActivity.this, "Please Enter Trip Time", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getItemAtPosition(i).equals("Round Trip")) {
            addLayout.setVisibility(View.VISIBLE);
        } else {
            addLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}