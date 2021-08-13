package com.example.tripreminder;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class UpComingFragment extends Fragment {
    private RecyclerView rv;
    private UpComingAdapter adapter;
    private ArrayList<Trip> trips;
    private static int position;
    public static final String TRIP_KEY = null;
    private ProgressDialog pd;

    private Calendar allDate;


    public static int jobID = 0;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_up_coming, container, false);
        allDate = Calendar.getInstance();
        rv = view.findViewById(R.id.upcoming_rv);
        pd = new ProgressDialog(getContext());
        pd.setMessage("Please Wait....");
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
        trips = new ArrayList<>();
        getTrips();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            adapter = new UpComingAdapter(getContext(), trips);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            adapter = new UpComingAdapter(getContext(), trips);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new UpComingAdapter.OnItemClickListener() {
            @Override
            public void onMenuClick(int position, View v, Context context) {
                setPosition(position);
                PopupMenu menu = new PopupMenu(context, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mi_edit:
                                String tripKey = trips.get(position).getKey();
                                Intent intent = new Intent(context, AddAndEdit.class);
                                intent.putExtra(TRIP_KEY, tripKey);
                                startActivity(intent);
                                return true;
                            case R.id.mi_delete:
                                String key = trips.get(getPosition()).getKey();
                                AlertDialog.Builder alert = new AlertDialog.Builder(
                                        getContext());
                                alert.setTitle("Deleting Alert!!");
                                alert.setMessage("Are you sure !!!!");
                                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).removeValue();
                                        trips.remove(getPosition());
                                        adapter.notifyItemRemoved(getPosition());
                                        dialog.dismiss();
                                    }
                                });
                                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();

                                return true;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.setting_menu);
                menu.show();
            }

            @Override
            public void onNoteClick(int position, Context context) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                if (!trips.get(position).getNotes().isEmpty()){
                    builder.setTitle("Notes")
                            .setMessage(trips.get(position).getNotes())
                            .show();
                }else{
                    builder.setTitle("Notes")
                            .setMessage("No Notes for This Trip")
                            .show();
                }
            }

            @Override
            public void onStartClick(int position, Context context) {
                Trip t1 = trips.get(position);

                if (t1.getWay().equals("One way Trip")){
                    Intent intent = new Intent(context, BubbleService.class);
                    intent.putExtra("notes", t1.getNotes());
                    context.startService(intent);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                        ActivityCompat.requestPermissions(getActivity(), permissions,101);
                    }
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+t1.getEnd());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                    t1.setState("Done");
                    FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(t1.getKey()).setValue(t1);
                    trips.remove(position);
                    adapter.notifyItemRemoved(position);
                    getActivity().finish();
                }else {
                    String[] notes = {"set Date", "Set Time"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Set Round Time")
                            .setItems(notes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0){
                                        Calendar c = Calendar.getInstance();
                                        int day = c.get(Calendar.DAY_OF_MONTH);
                                        int month = c.get(Calendar.MONTH);
                                        int year = c.get(Calendar.YEAR);

                                        DatePickerDialog d = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                                allDate.set(Calendar.YEAR, i);
                                                allDate.set(Calendar.MONTH, i1);
                                                allDate.set(Calendar.DAY_OF_MONTH, i2);
                                                notes[0] += "     Done";
                                                builder.show();
                                            }
                                        }, year, month, day);
                                        d.show();
                                    } else if (i == 1){
                                        Calendar c = Calendar.getInstance();
                                        int hour = c.get(Calendar.HOUR_OF_DAY);
                                        int minute = c.get(Calendar.MINUTE);
                                        TimePickerDialog t = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                allDate.set(Calendar.HOUR_OF_DAY, i);
                                                allDate.set(Calendar.MINUTE, i1);
                                                notes[1] += "     Done";
                                                builder.show();
                                            }
                                        }, hour, minute, true);
                                        t.show();
                                    }
                                }

                            })
                            .setPositiveButton("Save and Start", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String start = t1.getStart();
                                    String end = t1.getEnd();
                                    t1.setStart(t1.getEnd());
                                    t1.setEnd(start);
                                    t1.setDate(allDate);
                                    t1.setWay("One way Trip");
                                    FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(t1.getKey()).setValue(t1);
                                    adapter.notifyItemChanged(position);
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                                        ActivityCompat.requestPermissions(getActivity(), permissions,101);
                                    }
                                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+end);
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    startActivity(mapIntent);
                                    Intent intent = new Intent(context, BubbleService.class);
                                    intent.putExtra("notes", t1.getNotes());
                                    context.startService(intent);
                                    getActivity().finish();
                                }
                            }).show();
                }

            }
        });

        return view;
    }

    private void getTrips() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    trips.clear();
                    JobScheduler scheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
                    scheduler.cancelAll();
                    for (DataSnapshot t : snapshot.getChildren()) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, t.child("date").child("weekYear").getValue(Integer.class));
                        c.set(Calendar.MONTH, t.child("date").child("time").child("month").getValue(Integer.class));
                        c.set(Calendar.DAY_OF_MONTH, t.child("date").child("time").child("date").getValue(Integer.class));
                        c.set(Calendar.HOUR_OF_DAY, t.child("date").child("time").child("hours").getValue(Integer.class));
                        c.set(Calendar.MINUTE, t.child("date").child("time").child("minutes").getValue(Integer.class));
                        c.set(Calendar.SECOND, t.child("date").child("time").child("seconds").getValue(Integer.class));
                        c.set(Calendar.MILLISECOND, 0);
                        String end = (String) t.child("end").getValue();
                        String key = (String) t.child("key").getValue();
                        String name = (String) t.child("name").getValue();
                        String notes = (String) t.child("notes").getValue();
                        String repeat = (String) t.child("repeat").getValue();
                        String start = (String) t.child("start").getValue();
                        String state = (String) t.child("state").getValue();
                        String way = (String) t.child("way").getValue();
                        Trip trip = new Trip(c, name, state, start, end, key, notes, way, repeat);
                        if (trip.getState().equals("upcoming")) {
                            long time = c.getTimeInMillis() - System.currentTimeMillis();
                            if (time > 0) {
                                ComponentName componentName = new ComponentName(getContext(), MyJobService.class);
                                JobInfo info;
                                PersistableBundle bundle = new PersistableBundle();
                                bundle.putString("trip_key", trip.getKey());
                                bundle.putString("trip_repeat", trip.getRepeat());
                                bundle.putString("title", trip.getName());
                                bundle.putString("body", "From " + trip.getStart() + " to " + trip.getEnd());
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

                                trips.add(trip);
                                adapter.notifyDataSetChanged();
                            } else {
                                trip.setState("Time passed");
                                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(trip.getKey()).setValue(trip);
                                Toast.makeText(getContext(), "Time Pass For some Trips", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    pd.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}