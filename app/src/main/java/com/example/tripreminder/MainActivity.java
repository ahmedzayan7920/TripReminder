package com.example.tripreminder;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    public static MainActivity mainActivity;

    private String fullScreenInd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_view);
        View view = navigationView.getHeaderView(0);

        mainActivity = this;
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



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
*/

        tvUserName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        tvUserEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Picasso.with(this)
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .into(ivUserImage);
/*
        fullScreenInd = getIntent().getStringExtra("fullScreenIndicator");
        if ("y".equals(fullScreenInd)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();

            ivUserImage.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            ivUserImage.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            ivUserImage.setAdjustViewBounds(false);
            ivUserImage.setScaleType(ImageView.ScaleType.FIT_XY);
        }*/
        ivUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
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
                        toolbar.getMenu().getItem(1).setVisible(false);
                        break;
                    case R.id.drawer_history:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HistoryFragment()).commit();
                        navigationView.setCheckedItem(R.id.drawer_history);
                        toolbar.setTitle("History");
                        toolbar.getMenu().getItem(0).setVisible(false);
                        toolbar.getMenu().getItem(1).setVisible(true);
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
        menu.findItem(R.id.main_map).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.main_add) {
            Intent intent = new Intent(getBaseContext(), AddAndEditActivity.class);
            startActivity(intent);
            return true;
        }else if (item.getItemId() == R.id.main_map) {
            startActivity(new Intent(getBaseContext() , MapsActivity.class));
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
