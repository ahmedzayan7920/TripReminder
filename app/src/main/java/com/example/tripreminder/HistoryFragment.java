package com.example.tripreminder;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryFragment extends Fragment {
    private RecyclerView rv2;
    private HistoryAdapter adapter;
    private ArrayList<Trip> trips;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        rv2 = view.findViewById(R.id.history_rv);
        trips = new ArrayList<>();
        getTrips();
        adapter = new HistoryAdapter(getContext(), trips);
        rv2.setAdapter(adapter);
        rv2.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onShowNotesClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Notes")
                        .setMessage(trips.get(position).getNotes())
                        .show();
            }

            @Override
            public void onDeleteClick(int position) {
                String key = trips.get(position).getKey();
                trips.remove(position);
                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).removeValue();
                adapter.notifyItemRemoved(position);
            }
        });

        return view;
    }

    private void getTrips() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                trips.clear();
                for (DataSnapshot t : snapshot.getChildren()) {
                    Trip trip = t.getValue(Trip.class);
                    if (trip.getState().equals("upcoming")) {

                    } else {
                        trips.add(trip);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}