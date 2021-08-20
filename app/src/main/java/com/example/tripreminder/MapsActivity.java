package com.example.tripreminder;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.example.tripreminder.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ArrayList<String> ends , starts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ends = new ArrayList<>();
        starts = new ArrayList<>();
        getTrips();
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        getTrips();
        mMap = googleMap;

        for (int i = 0; i < ends.size(); i++) {
            int s = (int) (Math.random() * 10);
            LatLng endPoint = getLocationFromAddress(this, ends.get(i));
            LatLng startPoint = getLocationFromAddress(this, starts.get(i));
            if (startPoint != null && endPoint!= null){
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(randomColor(s)))
                        .position(endPoint)
                        .title(ends.get(i)));

                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(randomColor(s)))
                        .position(startPoint)
                        .title(starts.get(i)));

                mMap.addPolyline((new PolylineOptions()).add(startPoint,endPoint)
                        .width(5)
                        .color(roadColor(s))
                        .geodesic(true));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(startPoint));
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

    public int roadColor(int s) {
        int c;
        switch (s) {
            case 0:
                c = Color.YELLOW;
                break;
            case 1:
                c = Color.rgb(0, 127, 255);
                break;
            case 2:
                c = Color.MAGENTA;
                break;
            case 3:
                c = Color.BLUE;
                break;
            case 4:
                c = Color.CYAN;
                break;
            case 5:
                c = Color.RED;
                break;
            case 6:
                c = Color.GREEN;
                break;
            case 7:
                c = Color.rgb(255, 128, 0);
                break;
            case 8:
                c = Color.rgb(255, 0, 128);
                break;
            case 9:
                c = Color.rgb(127, 0, 255);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + s);
        }
        return c;
    }

    private void getTrips() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot t : snapshot.getChildren()) {
                    String start = (String) t.child("start").getValue();
                    starts.add(start);
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