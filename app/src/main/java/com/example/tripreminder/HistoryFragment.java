package com.example.tripreminder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private RecyclerView rv2;
    private HistoryAdapter adapter;
    private ArrayList<Trip> trips;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        fab = view.findViewById(R.id.fab_map);
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
                if (!trips.get(position).getNotes().isEmpty()){
                    builder.setTitle("Notes")
                            .setMessage(trips.get(position).getNotes())
                            .show();
                }else{
                    builder.setTitle("Notes")
                            .setMessage("No Notes for This Trip")
                            .show();
                }
            }

            @Override
            public void onDeleteClick(int position) {
                String key = trips.get(position).getKey();
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getContext());
                alert.setTitle("Deleting Alert!!");
                alert.setMessage("Are you sure !!!!");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        trips.remove(position);
                        FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).removeValue();
                        adapter.notifyItemRemoved(position);
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext() , MapsActivity.class));
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