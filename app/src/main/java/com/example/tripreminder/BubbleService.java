package com.example.tripreminder;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.siddharthks.bubbles.FloatingBubbleConfig;
import com.siddharthks.bubbles.FloatingBubbleService;

import java.util.ArrayList;

public class BubbleService extends FloatingBubbleService {
    private static ArrayList<String> notes;
    public static BubbleService bubbleService;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notes = new ArrayList<>();
        notes = intent.getStringArrayListExtra("notes");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected FloatingBubbleConfig getConfig() {
        Context context = getApplicationContext();
        View view = getInflater().inflate(R.layout.activity_bubble_view, null);
        ListView list = view.findViewById(R.id.bubble_list_view_test);
        TextView tv = view.findViewById(R.id.no_notes);
        if (notes.size() == 1 && notes.get(0).equals("")){
            notes.remove(0);
            tv.setVisibility(View.VISIBLE);
        }
        NoteAdapter adapter = new NoteAdapter(this , notes);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(BubbleService.this, list.getAdapter().getItem(i).toString(), Toast.LENGTH_SHORT).show();
            }
        });
        bubbleService = this;
        return new FloatingBubbleConfig.Builder()
                .bubbleIcon(ContextCompat.getDrawable(context, R.drawable.icon))
                .removeBubbleIcon(ContextCompat.getDrawable(context, com.siddharthks.bubbles.R.drawable.close_default_icon))
                .bubbleIconDp(100)
                .expandableView(view)
                .removeBubbleIconDp(80)
                .paddingDp(4)
                .borderRadiusDp(0)
                .physicsEnabled(true)
                .expandableColor(Color.WHITE)
                .triangleColor(0xFF215A64)
                .gravity(Gravity.START)
                .build();
    }
}