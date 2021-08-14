package com.example.tripreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class UpComingAdapter extends RecyclerView.Adapter<UpComingAdapter.TripViewHolder> {
    private Context context;
    private ArrayList<TripTest> trips;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onMenuClick(int position, View v, Context context);
        void onNoteClick(int position, Context context);
        void onStartClick(int position, Context context);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    class TripViewHolder extends RecyclerView.ViewHolder{
        TextView tvDate;
        TextView tvTime;
        TextView tvName;
        TextView tvState;
        TextView tvStart;
        TextView tvDestination;
        ImageView ivMenu;
        ImageView ivNote;
        Button btnStart;

        public TripViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvName = itemView.findViewById(R.id.tv_name);
            tvState = itemView.findViewById(R.id.tv_state);
            tvStart = itemView.findViewById(R.id.tv_start);
            tvDestination = itemView.findViewById(R.id.tv_destination);
            ivMenu = itemView.findViewById(R.id.iv_menu);
            ivNote = itemView.findViewById(R.id.iv_note);
            btnStart = itemView.findViewById(R.id.btn_start);


            ivMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onMenuClick(position, v, context);
                        }
                    }
                }
            });

            ivNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onNoteClick(position, context);
                        }
                    }
                }
            });

            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onStartClick(position, context);
                        }
                    }
                }
            });
        }
    }

    public UpComingAdapter(Context context, ArrayList<TripTest> trips) {
        this.context = context;
        this.trips = trips;
    }

    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.up_coming_custom_trip, parent, false);
        TripViewHolder viewHolder = new TripViewHolder(v, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        TripTest t = trips.get(position);
        int year = t.getGoDate().get(Calendar.YEAR);
        int month = t.getGoDate().get(Calendar.MONTH);
        int day = t.getGoDate().get(Calendar.DAY_OF_MONTH);
        int hour = t.getGoDate().get(Calendar.HOUR_OF_DAY);
        int minute = t.getGoDate().get(Calendar.MINUTE);
        holder.tvDate.setText(day+"/"+(month+1)+"/"+year);
        if (hour == 0) {
            if (minute >= 10) {
                holder.tvTime.setText((12) + ":" + minute + " am");
            } else {
                holder.tvTime.setText((12) + ":" + "0" + minute + " am");
            }
        } else if (hour == 12) {
            if (minute >= 10) {
                holder.tvTime.setText((hour) + ":" + minute + " pm");
            } else {
                holder.tvTime.setText((hour) + ":" + "0" + minute + " pm");
            }

        } else if (hour >= 13){
            if (minute >= 10){
                holder.tvTime.setText((hour-12) + ":" + minute + " pm");
            }else{
                holder.tvTime.setText((hour-12) + ":" + "0"+minute + " pm");
            }

        }else {
            if (minute >= 10){
                holder.tvTime.setText((hour) + ":" + minute + " am");
            }else{
                holder.tvTime.setText((hour) + ":" + "0"+minute + " am");
            }
        }
        holder.tvName.setText(t.getName());
        holder.tvState.setText(t.getState());
        holder.tvStart.setText(t.getStart());
        holder.tvDestination.setText(t.getEnd());
    }

    public int getItemCount() {
        return trips.size();
    }

}
