package com.example.tripreminder;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.tripreminder.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ArrayList<String> ends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ends = new ArrayList<>();
        getTrips();
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getTrips();
        mMap = googleMap;

        for (int i = 0; i < ends.size(); i++) {
            int s = (int) (Math.random() * 10);
            LatLng Aswan = getLocationFromAddress(this, ends.get(i));
            if (Aswan != null){
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(randomColor(s)))
                        .position(Aswan)
                        .title(ends.get(i)));
            }
        }
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng latLng = null;
        try {
            address = coder.getFromLocationName(strAddress, 1);
            if (address != null && !address.isEmpty()) {
                Address location = address.get(0);
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return latLng;
    }

    public float randomColor(int s) {
        float hue;
//        this.s = 0;
        switch (s) {
            case 0:
                hue = BitmapDescriptorFactory.HUE_YELLOW;
                break;
            case 1:
                hue = BitmapDescriptorFactory.HUE_AZURE;
                break;
            case 2:
                hue = BitmapDescriptorFactory.HUE_MAGENTA;
                break;
            case 3:
                hue = BitmapDescriptorFactory.HUE_BLUE;
                break;
            case 4:
                hue = BitmapDescriptorFactory.HUE_CYAN;
                break;
            case 5:
                hue = BitmapDescriptorFactory.HUE_RED;
                break;
            case 6:
                hue = BitmapDescriptorFactory.HUE_GREEN;
                break;
            case 7:
                hue = BitmapDescriptorFactory.HUE_ORANGE;
                break;
            case 8:
                hue = BitmapDescriptorFactory.HUE_ROSE;
                break;
            case 9:
                hue = BitmapDescriptorFactory.HUE_VIOLET;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + s);
        }
        return hue;
    }

    private void getTrips() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot t : snapshot.getChildren()) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, t.child("date").child("weekYear").getValue(Integer.class));
                    c.set(Calendar.MONTH, t.child("date").child("time").child("month").getValue(Integer.class));
                    c.set(Calendar.DAY_OF_MONTH, t.child("date").child("time").child("date").getValue(Integer.class));
                    c.set(Calendar.HOUR_OF_DAY, t.child("date").child("time").child("hours").getValue(Integer.class));
                    c.set(Calendar.MINUTE, t.child("date").child("time").child("minutes").getValue(Integer.class));
                    c.set(Calendar.SECOND, t.child("date").child("time").child("seconds").getValue(Integer.class));
                    String end = (String) t.child("end").getValue();
                    String key = (String) t.child("key").getValue();
                    String name = (String) t.child("name").getValue();
                    String notes = (String) t.child("notes").getValue();
                    String repeat = (String) t.child("repeat").getValue();
                    String start = (String) t.child("start").getValue();
                    String state = (String) t.child("state").getValue();
                    String way = (String) t.child("way").getValue();
                    Trip trip = new Trip(c, name, state, start, end, key, notes, way, repeat);
                    ends.add(trip.getEnd());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}