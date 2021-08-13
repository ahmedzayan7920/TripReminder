package com.example.tripreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.TripViewHolder> {
    private Context context;
    private ArrayList<Trip> trips;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onShowNotesClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvTime;
        TextView tvName;
        TextView tvState;
        TextView tvStart;
        TextView tvDestination;
        TextView tvExpand;
        Button btnShowNotes;
        Button btnDelete;
        CardView cv;
        LinearLayout ll;

        public TripViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date2);
            tvTime = itemView.findViewById(R.id.tv_time2);
            tvName = itemView.findViewById(R.id.tv_name2);
            tvState = itemView.findViewById(R.id.tv_state2);
            tvStart = itemView.findViewById(R.id.tv_start2);
            tvDestination = itemView.findViewById(R.id.tv_destination2);
            tvExpand = itemView.findViewById(R.id.tv_expand);
            cv = itemView.findViewById(R.id.card_view);
            ll = itemView.findViewById(R.id.expand);

            btnShowNotes = itemView.findViewById(R.id.btn_show_notes);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            btnShowNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onShowNotesClick(position);
                        }
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Trip trip = trips.get(getAdapterPosition());
                    trip.setExpand(!trip.isExpand());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }

    public HistoryAdapter(Context context, ArrayList<Trip> trips) {
        this.context = context;
        this.trips = trips;
    }

    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.history_custom_trip, parent, false);
        TripViewHolder viewHolder = new TripViewHolder(v, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip t = trips.get(position);
        int year = t.getDate().get(Calendar.YEAR);
        int month = t.getDate().get(Calendar.MONTH);
        int day = t.getDate().get(Calendar.DAY_OF_MONTH);
        int hour = t.getDate().get(Calendar.HOUR_OF_DAY);
        int minute = t.getDate().get(Calendar.MINUTE);
        holder.tvDate.setText(day+"/"+(month+1)+"/"+year);
        if (hour >= 13){
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

        boolean expand = trips.get(position).isExpand();
        if (expand) {
            holder.ll.setVisibility(View.VISIBLE);
            holder.tvExpand.setText("UnExpand");
        } else {
            holder.ll.setVisibility(View.GONE);
            holder.tvExpand.setText("Expand");
        }
    }

    public int getItemCount() {
        return trips.size();
    }

}
