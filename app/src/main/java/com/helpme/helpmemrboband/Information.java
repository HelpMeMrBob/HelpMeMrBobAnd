package com.helpme.helpmemrboband;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Information extends AppCompatActivity {

    private static final String TAG = "HELP";
    SupportMapFragment mapFragment;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        mapFragment = (SupportMapFragment)getSupportFragmentManager().
                        findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                map = googleMap;

                //대표위치 월드메르디앙2차
                LatLng WORLD = new LatLng(37.4785147, 126.8787445);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(WORLD);
                markerOptions.title("Here i am");
                markerOptions.snippet("우왕 지도당");

                map.addMarker(markerOptions);

                //카메라 이동 및 최초레벨 지정
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(WORLD, 16));
                //지도 유형형
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Toast.makeText(getApplicationContext(),
                                "Marker클릭 : " + marker.getTitle() + "\n" +
                                marker.getPosition(),
                                Toast.LENGTH_SHORT).show();

                        return false;
                    }
                });

                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {
                        Toast.makeText(getApplicationContext(),
                                "InfoWindow클릭 : " + marker.getTitle() + "\n" +
                                marker.getPosition(),
                                Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        try {
            MapsInitializer.initialize(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}