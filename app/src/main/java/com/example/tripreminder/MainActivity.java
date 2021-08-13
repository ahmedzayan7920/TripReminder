package com.example.tripreminder;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    public static final String CHANNEL_ID = "aaa";
    public static final int NOTIFICATION_ID = 10;


    private TextView tvUserName;
    private TextView tvUserEmail;
    private ImageView ivUserImage;
    static boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_view);
        View view = navigationView.getHeaderView(0);

        if (flag) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            flag = false;
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("UpComing");
        setSupportActionBar(toolbar);

        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        ivUserImage = view.findViewById(R.id.iv_user_image);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 123123);
            }
        }




        tvUserName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        tvUserEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Picasso.with(this)
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .into(ivUserImage);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UpComingFragment()).commit();
            navigationView.setCheckedItem(R.id.drawer_upcoming);
            toolbar.setTitle("UpComing");
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.drawer_upcoming:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UpComingFragment()).commit();
                        navigationView.setCheckedItem(R.id.drawer_upcoming);
                        toolbar.setTitle("UpComing");
                        toolbar.getMenu().getItem(0).setVisible(true);
                        break;
                    case R.id.drawer_history:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HistoryFragment()).commit();
                        navigationView.setCheckedItem(R.id.drawer_history);
                        toolbar.setTitle("History");
                        toolbar.getMenu().getItem(0).setVisible(false);
                        break;
                    case R.id.drawer_Logout:
                        FirebaseAuth.getInstance().signOut();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123123) {
            if (resultCode == RESULT_OK) {
                if (Settings.canDrawOverlays(this)) {
                    // You have permission

                }

            }
        }
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
