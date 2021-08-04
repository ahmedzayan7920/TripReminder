package com.example.tripreminder;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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
        Trip trip = new Trip("11/1/2222", "22:22", "Zagazig University", "upcoming", "Damietta", "Zagazig");
        trips.add(trip);
        trips.add(trip);
        trips.add(trip);
        trips.add(trip);
        trips.add(trip);
        trips.add(trip);

        adapter = new HistoryAdapter(getContext(), trips);
        rv2.setAdapter(adapter);
        RecyclerView.LayoutManager lm = new GridLayoutManager(getContext(), 1);
        rv2.setLayoutManager(lm);
        rv2.setHasFixedSize(true);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onShowNotesClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                trips.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        return view;
    }
}