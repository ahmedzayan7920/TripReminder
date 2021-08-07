package com.example.tripreminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    public static final String CHANNEL_ID = "aaa";
    public static final int NOTIFICATION_ID = 10;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("UpComing");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_view);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UpComingFragment()).commit();
            navigationView.setCheckedItem(R.id.drawer_upcoming);

        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.drawer_upcoming:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UpComingFragment()).commit();
                        navigationView.setCheckedItem(R.id.drawer_upcoming);
                        toolbar.setTitle("UpComing");
                        toolbar.getMenu().getItem(1).setVisible(true);
                        break;
                    case R.id.drawer_history:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HistoryFragment()).commit();
                        navigationView.setCheckedItem(R.id.drawer_history);
                        toolbar.setTitle("History");
                        toolbar.getMenu().getItem(1).setVisible(false);
                        break;
                    case R.id.drawer_sync:
                        Toast.makeText(getApplicationContext(), "Sync Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.drawer_Logout:
                        firebaseAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.main_add);
        SearchView searchView = (SearchView) menu.findItem(R.id.main_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_add:
                Intent intent = new Intent(getBaseContext(), AddAndEdit.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

}


    /*
    Intent notifyIntent = new Intent(this, MyReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.YEAR, 2021);
                calendar.set(Calendar.MONTH, Calendar.AUGUST);
                calendar.set(Calendar.DAY_OF_MONTH, 7);
                calendar.set(Calendar.HOUR_OF_DAY, 19);
                calendar.set(Calendar.MINUTE, 25);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                */
/*--------------------------------------------------------------------------------------*/
/*
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.YEAR, 2021);
                calendar.set(Calendar.MONTH, Calendar.AUGUST);
                calendar.set(Calendar.DAY_OF_MONTH, 7);
                calendar.set(Calendar.HOUR_OF_DAY, 20);
                calendar.set(Calendar.MINUTE, 4);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long t = calendar.getTimeInMillis() - System.currentTimeMillis();
                Log.i("time", t+"");
                ComponentName componentName = new ComponentName(getBaseContext(), MyJobService.class);
        JobInfo info;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N){
        info = new JobInfo.Builder(10,componentName)
        .setPeriodic(t)
        .build();
        }else{
        info = new JobInfo.Builder(10,componentName)
        .setMinimumLatency(t)
        .build();
        }

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);
        */
