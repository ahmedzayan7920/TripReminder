package com.example.tripreminder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UpComingFragment extends Fragment {
    private RecyclerView rv;
    private UpComingAdapter adapter;
    private ArrayList<Trip> trips;
    private static int position;
    public static final String TRIP_KEY = null;
    private ProgressDialog pd;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_up_coming, container, false);
        rv = view.findViewById(R.id.upcoming_rv);
        pd = new ProgressDialog(getContext());
        pd.setMessage("Please Wait....");
        pd.setCancelable(false);
        pd.show();
        trips = new ArrayList<>();
        getTrips();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            adapter = new UpComingAdapter(getContext(), trips);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));

        }else {
            adapter = new UpComingAdapter(getContext(), trips);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new UpComingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Context context) {
                Toast.makeText(getContext(), "Clicked on " + (position + 1), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMenuClick(int position, View v, Context context) {
                setPosition(position);
                PopupMenu menu = new PopupMenu(context, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mi_edit:
                                String tripKey = trips.get(position).getKey();
                                Intent intent = new Intent(context, AddAndEdit.class);
                                intent.putExtra(TRIP_KEY, tripKey);
                                startActivity(intent);
                                return true;
                            case R.id.mi_delete:
                                String key = trips.get(getPosition()).getKey();
                                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).removeValue();
                                trips.remove(getPosition());
                                adapter.notifyItemRemoved(getPosition());
                                return true;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.setting_menu);
                menu.show();
            }

            @Override
            public void onNoteClick(int position, Context context) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Notes")
                        .setMessage(trips.get(position).getNotes())
                        .show();

                /*
                String[] notes = {"ahmed", "mohamed", "zayan"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Notes")
                        .setItems(notes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }

                        }).show();*/
            }

            @Override
            public void onStartClick(int position, Context context) {
                Trip t = trips.get(position);
                t.setState("Done");
                FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(t.getKey()).setValue(t);
                trips.remove(position);
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
                    if (trip.getState().equals("upcoming")){
                        trips.add(trip);
                        adapter.notifyDataSetChanged();
                    }
                }
                pd.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}