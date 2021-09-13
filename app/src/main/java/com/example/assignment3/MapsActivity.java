package com.example.assignment3;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.assignment3.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    String cityName,strCityLat, strCityLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        Intent getWeatherIntent = getIntent();

        try {
            cityName = getWeatherIntent.getStringExtra("city_name");
            strCityLat = getWeatherIntent.getStringExtra("city_lat");
            strCityLong = getWeatherIntent.getStringExtra("city_long");


        }catch (Exception e){ }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        LatLng city = new LatLng(Double.parseDouble(strCityLat), Double.parseDouble(strCityLong));
        mMap.addMarker(new MarkerOptions().position(city).title(cityName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city,15f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}