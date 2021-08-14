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
                    String end = (String) t.child("end").getValue();
                    ends.add(end);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}