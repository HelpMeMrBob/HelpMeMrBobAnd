package com.helpme.helpmemrboband;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    String TAG = "HELP";
    SupportMapFragment mapFragment;
    GoogleMap map;
    EditText searchField;
    Button myLocation;
    String provider;
    double longitude;
    double latitude;

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
                markerOptions.title("내 위치");

                map.addMarker(markerOptions);

                //카메라 이동 및 최초레벨 지정
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(WORLD, 16));
                //지도 유형형
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Toast.makeText(getApplicationContext(),
                                "내 위치",
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


        // 검색어 입력 영역
        searchField = findViewById(R.id.searchField);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(getApplicationContext(),
                            searchField.getText().toString(),
                            Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });



        //내 위치 버튼 영역
        myLocation = findViewById(R.id.myLocation);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(
                                getApplicationContext(),
                                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Information.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }
                else {
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    provider = location.getProvider();
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);
                }
                LatLng myLocation = new LatLng(latitude, longitude);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLocation);
                markerOptions.title("내 위치");

                map.addMarker(markerOptions);
            }
        });
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            provider = location.getProvider();
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    };
}