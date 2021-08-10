package com.example.tripreminder;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class WidgetService extends Service {
    private WindowManager windowManager;
    private View chatheadView;
    int LAYOUT_FLAG;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    @Override
    public void onCreate() {
        super.onCreate();

        chatheadView= LayoutInflater.from(this).inflate(R.layout.layout_widget,null);
        WindowManager.LayoutParams params=new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY ,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity= Gravity.TOP |Gravity.LEFT;
        params.x=0;
        params.y=100;
        windowManager=(WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(chatheadView,params);

        ImageView chatheadImage=chatheadView.findViewById(R.id.chathead_image);
        chatheadImage.setOnTouchListener(
                new View.OnTouchListener() {
                    private int initialx;
                    private int initialy;
                    private float touchx;
                    private float touchy;
                    private int lastAction;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            initialx = params.x;
                            initialy = params.y;
                            touchx = event.getRawX();
                            touchy = event.getRawY();
                            lastAction = event.getAction();
                            return true;
                        }

                      if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (lastAction == MotionEvent.ACTION_DOWN) {

                               // Button button = new Button(WidgetService.this);
                               // button.setText("close");

/*                               RelativeLayout layout = chatheadView.findViewById(R.id.chathead_bubble);
                                layout.addView(button);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        stopSelf();
                                        //openDialog();
                                        AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());
                                        builder.setTitle("Notes").setMessage("This is a dialog").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).show();

                                        Toast.makeText(WidgetService.this, "Done!!!", Toast.LENGTH_SHORT).show();
                                    }
                                });*/
                            }

                            lastAction = event.getAction();
                            return true;
                        }
                        if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            params.x=initialx+(int) (event.getRawX()-touchx);
                            params.y=initialy+(int) (event.getRawY()-touchy);
                            windowManager.updateViewLayout(chatheadView,params);
                            lastAction=event.getAction();
                            return true;

                        }
                        return false;
                    }
                }

        );
       chatheadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Done!!!", Toast.LENGTH_SHORT).show();
                Log.i("0123654789", "hoshfoiguwshfiuow");
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(chatheadView !=null){
            windowManager.removeView(chatheadView);
        }
    }
}



