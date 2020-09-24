package com.example.gm1;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.icu.text.CaseMap.Title;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static android.os.Build.VERSION_CODES.M;
import static java.util.Collections.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int flagMap = 1;
    private int Marker_poz, delMarker_poz = 0;
    private Marker mCurrentMark;
    final String TAG = "myLogs";
    private ArrayList<Marker> mMarkerArrayList = new ArrayList<Marker>();
    private ArrayList<LatLng> myCoordArrayList = new ArrayList<LatLng>();
    private PolylineOptions polylineOptions = new PolylineOptions();
    private Polyline polylineMarshrut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void init() {

        mMap.setOnMarkerDragListener(new OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(com.google.android.gms.maps.model.Marker marker) {
                myCoordArrayList.remove(marker.getPosition());

            }

            @Override
            public void onMarkerDrag(com.google.android.gms.maps.model.Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(com.google.android.gms.maps.model.Marker marker) {
                marker.setSnippet(String.valueOf(marker.getPosition()));
                myCoordArrayList.add(marker.getPosition());

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                TextView mTv = findViewById(R.id.textView2);
                mTv.setTextColor(Color.RED);
                mTv.setText("Удалить маркер " + marker.getTitle());
                delMarker_poz = mMarkerArrayList.indexOf(marker);
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick: " + latLng.latitude + "," + latLng.longitude);
                TextView mTv = findViewById(R.id.textView2);
                mTv.setTextColor(Color.GRAY);
                mTv.setText("Кнопка");
                delMarker_poz= -1;

                /*Toast toast = Toast.makeText(getApplicationContext(),
                        "32e23e23e23e2e23!",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();*/
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Marker_poz = Marker_poz + 1;
                // String s;
                // s= latLng.latitude + ", " + latLng.longitude;
                Log.d(TAG, "onMapLongClick: " + latLng.latitude + "," + latLng.longitude);

                MarkerOptions marker_onclick = new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                        .title((Integer.toString(Marker_poz))).snippet(String.valueOf(latLng)).draggable(true);

                mCurrentMark = mMap.addMarker(marker_onclick);
                myCoordArrayList.add(latLng);
               // mMap.addMarker(marker_onclick);
                mMarkerArrayList.add(mCurrentMark);
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition camera) {
                Log.d(TAG, "onCameraChange: " + camera.target.latitude + "," + camera.target.longitude);
            }
        });

    }
    public void onClickTest(View view) {
        flagMap=flagMap+1;
        TextView myTextView;
        myTextView = (TextView) findViewById(R.id.textView);
        switch (flagMap){
            case (0):
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                myTextView.setText("Карта выключена");
                break;
            case (1):
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                myTextView.setText("Обычный режим");
                break;
            case (2):
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                myTextView.setText("Снимки со спутника");
                break;
            case (3):
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                myTextView.setText("Карта рельефа местности");
                break;
            case (4):
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                myTextView.setText("Снимки со спутника + инфа о улицах и транспорте");
                flagMap=-1;
                break;
        }
    }


    private void setUpMap() {      //создаем координаты для позиции камеры с центром в городе Киев
        LatLng positions = new LatLng(50.452842, 30.524418);      //перемещаем камеру и оттдаляем ее что мы можно было увидеть город
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positions, 10));      //Добавляем маркер с местоположением на Крещатике
        mMap.addMarker(new MarkerOptions().position(new LatLng(50.450137, 30.524180)).title("Крещатик"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
       /* mTxtView = (TextView) findViewById(R.id.textView);
        ViewGroup.LayoutParams params = mTxtView.getLayoutParams();
        params.width = params.width /3 *2;
        mTxtView.setLayoutParams(params);*/
        setUpMap();
        init();
        polylineMarshrut = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));

    }

    public void DelMarker(View view) {
       if (delMarker_poz != -1) {

        //   mCurrentMark = mMarkerArrayList.get(delMarker);
           mMarkerArrayList.remove(delMarker_poz);
           mCurrentMark.remove();
           delMarker_poz= -1;
           TextView mTv = findViewById(R.id.textView2);
           mTv.setTextColor(Color.GRAY);
           mTv.setText("Кнопка");

       }

    }

    public void onClickRoute(View view) {
       // mMarkerArrayList.sort(Comparator<Marker.>);


        if(!myCoordArrayList.isEmpty()) {
            // Create polyline options with existing LatLng ArrayList
            polylineOptions.addAll(myCoordArrayList);
            polylineOptions
                    .width(5)
                    .color(Color.RED);

// Adding multiple points in map using polyline and arraylist
            mMap.addPolyline(polylineOptions);
        }
    }
}