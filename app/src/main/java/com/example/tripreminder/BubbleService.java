package com.example.tripreminder;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.siddharthks.bubbles.FloatingBubbleConfig;
import com.siddharthks.bubbles.FloatingBubbleService;

public class BubbleService extends FloatingBubbleService {
    String notes;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notes = intent.getStringExtra("notes");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected FloatingBubbleConfig getConfig() {
        Context context = getApplicationContext();
        View view = getInflater().inflate(R.layout.activity_bubble_view, null);
        TextView tv = view.findViewById(R.id.tv_bubble_notes);
        tv.setText(notes);
        return new FloatingBubbleConfig.Builder()
                .bubbleIcon(ContextCompat.getDrawable(context, R.drawable.icon))
                .removeBubbleIcon(ContextCompat.getDrawable(context, com.siddharthks.bubbles.R.drawable.close_default_icon))
                .bubbleIconDp(100)
                .expandableView(view)
                .removeBubbleIconDp(100)
                .paddingDp(4)
                .borderRadiusDp(0)
                .physicsEnabled(true)
                .expandableColor(Color.WHITE)
                .triangleColor(0xFF215A64)
                .gravity(Gravity.LEFT)
                .build();
    }
}