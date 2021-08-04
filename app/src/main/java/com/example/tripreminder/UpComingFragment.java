package com.example.tripreminder;

import android.app.Dialog;
import android.os.Bundle;

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

import java.util.ArrayList;

public class UpComingFragment extends Fragment{
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

        trips = new ArrayList<>();
        Trip trip = new Trip("11/1/2222", "22:22", "Zagazig University", "upcoming", "Damietta", "Zagazig");
        trips.add(trip);
        trips.add(trip);
        trips.add(trip);
        trips.add(trip);
        trips.add(trip);
        trips.add(trip);

        adapter = new UpComingAdapter(getContext(), trips);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new UpComingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), "Clicked on " + (position + 1), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMenuClick(int position, View v) {
                setPosition(position);
                PopupMenu menu = new PopupMenu(getContext(), v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mi_note:
                                Toast.makeText(getContext(), "Note", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.mi_edit:
                                Toast.makeText(getContext(), "Edit", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.mi_delete:
                                Log.i("01230123", getPosition() + "");
                                trips.remove(getPosition());
                                adapter.notifyItemRemoved(getPosition());
                                Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.mi_cancel:
                                Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.setting_menu);
                menu.show();
            }

            @Override
            public void onNoteClick(int position) {
                Dialog d = new Dialog(getContext());
                d.show();
            }

            @Override
            public void onStartClick(int position) {

            }
        });

        return view;
    }

}