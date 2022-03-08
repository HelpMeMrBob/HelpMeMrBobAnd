package com.helpme.helpmemrboband;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Information extends AppCompatActivity {

    String TAG = "SON";
    SupportMapFragment mapFragment;
    GoogleMap map;
    EditText searchField;
    Button myLocation;
    String provider;
    double longitude;
    double latitude;
    ArrayList<InfomationDTO> dtoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Intent intent = getIntent();
        String menu = intent.getStringExtra("menu");

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

                //마커 클릭 이벤트
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Toast.makeText(getApplicationContext(),
                                marker.getTitle(),
                                Toast.LENGTH_SHORT).show();

                        return false;
                    }

                });

                //인포 윈도우 클릭 이벤트
                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {

                        Intent intent = new Intent(getApplicationContext(),
                                DetailView.class);

                        for (int i = 0; i < dtoList.size(); i++) {
                            if (marker.getTitle().equals(dtoList.get(i).getPlace())) {
                                intent.putExtra("menu", dtoList.get(i).getMenu());
                                intent.putExtra("plcNum", dtoList.get(i).getPlcNum());
                                intent.putExtra("operTime", dtoList.get(i).getOperTime());
                                intent.putExtra("price", dtoList.get(i).getPrice());
                            }
                        }

                        intent.putExtra("place", marker.getTitle());
                        intent.putExtra("address", marker.getSnippet());

                        startActivity(intent);
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
        if (menu != null) {
            searchField.setText(menu);
            Map<String, String> map = new HashMap<>();
            map.put("search", menu);

            MapTask task = new MapTask();
            task.execute(map);
        }
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    Map<String, String> map = new HashMap<>();
                    map.put("search", searchField.getText().toString());

                    MapTask task = new MapTask();
                    task.execute(map);
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





    public class MapTask extends AsyncTask<Map, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Map... maps) {

            HttpClient.Builder http = new HttpClient.Builder("POST", OracleConnect.servletURL + "searchMenuList.do");

            http.addAllParameters(maps[0]);

            HttpClient post = http.create();
            post.request();

            int statusCode = post.getHttpStatusCode();

            String body = post.getBody();
            return body;
        }

        @Override
        protected void onPostExecute(String json) {

            Toast.makeText(getApplicationContext(),
                    "검색된 데이터가 많으면\n오래 걸릴 수 있습니다.",
                    Toast.LENGTH_SHORT).show();

            map.clear();
            Log.d(TAG, json);
            dtoList = new ArrayList<>();

            if (json != null) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray keywordArray = jsonObject.getJSONArray("keyword");
                    for (int i = 0; i < keywordArray.length(); i++) {
                        JSONObject keywordObject = keywordArray.getJSONObject(i);
                        InfomationDTO dto = new InfomationDTO();
                        dto.setPlace(keywordObject.getString("place"));
                        dto.setAddress(keywordObject.getString("address"));
                        dto.setMenu(keywordObject.getString("menu"));
                        dto.setPlcNum(keywordObject.getString("plcNum"));
                        dto.setPrice(keywordObject.getString("price"));
                        dto.setOperTime(keywordObject.getString("operTime"));

                        dtoList.add(dto);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "무슨 에러일까..", Toast.LENGTH_SHORT).show();
            }

            ArrayList<String> address = new ArrayList<>();

            for (int i = 0; i < dtoList.size(); i++) {
                address.add(dtoList.get(i).getAddress());
            }

            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> temp;

            try {
                for (int i = 0; i < address.size(); i++) {
                    LatLng point = new LatLng(0, 0);
                    String add = "";

                    temp = geocoder.getFromLocationName(address.get(i), 1);

                    System.out.println("검색 결과 : " + temp.get(0));

                    try {
                        String[] splitStr = temp.get(0).toString().split(",");

                        add = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소

                        String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                        String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도

                        point = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    catch (StringIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    MarkerOptions mOptions = new MarkerOptions();
                    mOptions.title(dtoList.get(i).getPlace());
                    mOptions.snippet(add);
                    mOptions.position(point);
                    map.addMarker(mOptions);
                }

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(37.473339391474006, 126.89325025627143), 15));
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(getApplicationContext(),
                    "검색 완료",
                    Toast.LENGTH_SHORT).show();
        }
    }
}