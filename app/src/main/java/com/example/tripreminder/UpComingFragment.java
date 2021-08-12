package com.example.tripreminder;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import static com.example.tripreminder.MainActivity.NOTIFICATION_ID;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UpComingFragment extends Fragment {
    private RecyclerView rv;
    private UpComingAdapter adapter;
    private ArrayList<Trip> trips;
    private static int position;
    public static final String TRIP_KEY = null;
    private ProgressDialog pd;

    private Date allDate;


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
        allDate = new Date();
        rv = view.findViewById(R.id.upcoming_rv);
        pd = new ProgressDialog(getContext());
        pd.setMessage("Please Wait....");
        pd.setCancelable(false);
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
            public void onItemClick(int position, Context context) {
                Toast.makeText(getContext(), "Clicked on " + (position + 1), Toast.LENGTH_SHORT).show();
            }

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
                                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).removeValue();
                                trips.remove(getPosition());
                                adapter.notifyItemRemoved(getPosition());
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
                builder.setTitle("Notes")
                        .setMessage(trips.get(position).getNotes())
                        .show();

                /*
                String[] notes = {"ahmed", "mohamed", "zayan"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Notes")
                        .setItems(notes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }

                        }).show();*/
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
                                                allDate.setYear(i);
                                                allDate.setMonth(i1);
                                                allDate.setDate(i2);
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
                                                allDate.setHours(i);
                                                allDate.setMinutes(i1);
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
                //if (getActivity() != null && getContext() != null) {
                try{
                    trips.clear();
                    JobScheduler scheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
                    scheduler.cancelAll();
                    for (DataSnapshot t : snapshot.getChildren()) {
                        Trip trip = t.getValue(Trip.class);
                        if (trip.getState().equals("upcoming")) {

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, trip.getDate().getYear());
                            calendar.set(Calendar.MONTH, trip.getDate().getMonth());
                            calendar.set(Calendar.DAY_OF_MONTH, trip.getDate().getDate());
                            calendar.set(Calendar.HOUR_OF_DAY, trip.getDate().getHours());
                            calendar.set(Calendar.MINUTE, trip.getDate().getMinutes());
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            long time = calendar.getTimeInMillis() - System.currentTimeMillis();

                            if (time > 0) {
                                ComponentName componentName = new ComponentName(getContext(), MyJobService.class);
                                JobInfo info;
                                PersistableBundle bundle = new PersistableBundle();
                                bundle.putString("trip_key", trip.getKey());
                                bundle.putString("trip_repeat", trip.getRepeat());
                                bundle.putInt("job_id", jobID);
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
                                trip.setState("Canceled");
                                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(trip.getKey()).setValue(trip);
                                Toast.makeText(getContext(), "Time Pass For some Trips", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    pd.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("trycatch0123", e.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
/*
Intent notifyIntent = new Intent(getActivity().getBaseContext(), MyReceiver.class);
                            notifyIntent.putExtra("trip_key", trip.getKey());
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.YEAR, trip.getDate().getYear());
                            calendar.set(Calendar.MONTH, trip.getDate().getMonth());
                            calendar.set(Calendar.DAY_OF_MONTH, trip.getDate().getDate());
                            calendar.set(Calendar.HOUR_OF_DAY, trip.getDate().getHours());
                            calendar.set(Calendar.MINUTE, trip.getDate().getMinutes());
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
 */