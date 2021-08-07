package com.example.tripreminder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
        getTrips();
        trips = new ArrayList<>();
        adapter = new UpComingAdapter(getContext(), trips);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
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
                            case R.id.mi_note:
                                Toast.makeText(context, "Note", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.mi_edit:
                                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.mi_delete:
                                Log.i("01230123", getPosition() + "");
                                trips.remove(getPosition());
                                adapter.notifyItemRemoved(getPosition());
                                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.mi_cancel:
                                Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
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
                String[] notes = {"ahmed", "mohamed", "zayan"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Notes")
                        .setItems(notes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }

                        }).show();
            }

            @Override
            public void onStartClick(int position, Context context) {

            }
        });

        return view;
    }

    private ArrayList<Trip> getTrips() {

        ArrayList<Trip> tt = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot t : snapshot.getChildren()) {
                    Toast.makeText(getContext(), t.toString(), Toast.LENGTH_SHORT).show();
                    Trip trip = (Trip) t.getValue();
                    //tt.add((Trip) t.getValue());
                    //Toast.makeText(getContext(), tt.size(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return tt;
    }

}