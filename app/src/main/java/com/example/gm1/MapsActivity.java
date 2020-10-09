package com.example.gm1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.icu.text.CaseMap.Title;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
import java.util.Date;

import static android.os.Build.VERSION_CODES.M;
import static java.util.Collections.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocListenerInterface {

    private GoogleMap mMap;
    private int flagMap = 1;
    private int Marker_poz, delMarker_poz = 0;
    private Marker mCurrentMark;
    Circle myCirclePoz;
    final String TAG = "myLogs";
    private ArrayList<Marker> mMarkerArrayList = new ArrayList<Marker>();
    private PolylineOptions polylineOptions = new PolylineOptions();
    private Polyline polylineMarshrut;

    //**************************
    private LocationManager locationManager;
    private TextView tvDistance,tvVelocity,txttemp;
    private Location lastLocation;
    private MyLocListener myLocListener;
    private int distance;
    //*****************************


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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

    //**************************************

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] ==RESULT_OK)
        {
            checkPermissions();
        }
    }

    private void checkPermissions()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 100 );
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,1,myLocListener);
        }
    }
    //**************************************

    private void init() {

        //**********************************
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocListener = new MyLocListener();
        tvDistance = findViewById(R.id.tvDistance);
        tvVelocity = findViewById(R.id.tvVelocity);
        txttemp = findViewById(R.id.txttemp);
        myLocListener.setLocListenerInterface(this);
        checkPermissions();
        //**********************************

        mMap.setOnMarkerDragListener(new OnMarkerDragListener() {
           private int del_poz = -1;
            @Override
            public void onMarkerDragStart(com.google.android.gms.maps.model.Marker marker) {
                mCurrentMark =marker;
                del_poz = mMarkerArrayList.indexOf(marker);
                txttemp.setText(String.valueOf(mMarkerArrayList.size()));
                //mMarkerArrayList.remove(marker);
            }

            @Override
            public void onMarkerDrag(com.google.android.gms.maps.model.Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(com.google.android.gms.maps.model.Marker marker) {
                marker.setSnippet(String.valueOf(marker.getPosition()));
                mCurrentMark.setPosition(marker.getPosition());
                if (del_poz!=-1)   {
                    mMarkerArrayList.set(del_poz,mCurrentMark);
                    del_poz = -1;
                }
                txttemp.setText(String.valueOf(mMarkerArrayList.size()));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                TextView mTv = findViewById(R.id.textView2);
                mTv.setTextColor(Color.RED);
                mTv.setText("Удалить маркер " + marker.getTitle());
                delMarker_poz = mMarkerArrayList.indexOf(marker);
                txttemp.setText(String.valueOf(mMarkerArrayList.size()));
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
                txttemp.setText(String.valueOf(mMarkerArrayList.size()));

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
               // myCoordArrayList.add(latLng);
                mMarkerArrayList.add(mCurrentMark);
                txttemp.setText(String.valueOf(mMarkerArrayList.size()));
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positions, 5));      //Добавляем маркер с местоположением на Крещатике
       // mMap.addMarker(new MarkerOptions().position(new LatLng(50.450137, 30.524180)).title("Крещатик"));
        mMap.addMarker(new MarkerOptions().position(positions).title("Крещатик"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
           mCurrentMark = mMarkerArrayList.get(delMarker_poz);
           mMarkerArrayList.remove(delMarker_poz);
           mCurrentMark.remove();
           delMarker_poz= -1;
           TextView mTv = findViewById(R.id.textView2);
           mTv.setTextColor(Color.GRAY);
           mTv.setText("Кнопка");
       }
    }

    public void onClickRoute(View view) {
        if(!mMarkerArrayList.isEmpty()) {
            //mMarkerArrayList.sort(Marker);
            //Collections.sort(mMarkerArrayList);
            ArrayList<LatLng> mCoordArrayList = new ArrayList<LatLng>();
            for (Marker i: mMarkerArrayList)
            {
                mCoordArrayList.add(i.getPosition());
            }
            // Create polyline options with existing LatLng ArrayList

            polylineOptions.addAll(mCoordArrayList);
            polylineOptions
                    .width(5)
                    .color(Color.RED);
           //if (polylineMarshrut!=null)
        //   {
               polylineMarshrut.remove();
         //  }
           polylineMarshrut = mMap.addPolyline(polylineOptions);
           mCoordArrayList.clear();
        }

        //************************************************

        //Получаем контекст для запросов, mapsApiKey хранит в себе String с ключом для карт
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(mapsApiKey)
                .build();

//Здесь будет наш итоговый путь состоящий из набора точек
        DirectionsResult result = null;
        try {
            result = DirectionsApi.newRequest(geoApiContext)
                    .origin(places.get(0))//Место старта
                    .destination(places.get(places.size() - 1))//Пункт назначения
                    .waypoints(places.get(1), places.get(2)).await();//Промежуточные точки. Да, не очень красиво, можно через цикл, но зато наглядно
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//Преобразование итогового пути в набор точек
        List<com.google.maps.model.LatLng> path = result.routes[0].overviewPolyline.decodePath();

//Линия которую будем рисовать
        PolylineOptions line = new PolylineOptions();

        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

//Проходимся по всем точкам, добавляем их в Polyline и в LanLngBounds.Builder
        for (int i = 0; i < path.size(); i++) {
            line.add(new com.google.android.gms.maps.model.LatLng(path.get(i).lat, path.get(i).lng));
            latLngBuilder.include(new com.google.android.gms.maps.model.LatLng(path.get(i).lat, path.get(i).lng));
        }

//Делаем линию более менее симпатичное
        line.width(16f).color(R.color.colorPrimary);

//Добавляем линию на карту
        googleMap.addPolyline(line);

//Выставляем камеру на нужную нам позицию
        LatLngBounds latLngBounds = latLngBuilder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, width, width, 25);//width это размер нашего экрана
        googleMap.moveCamera(track);

        //************************************************
    }

    @Override
    public void OnLocationChanged(Location loc) {
        if(loc.hasSpeed() && lastLocation != null)
        {
            distance += lastLocation.distanceTo(loc);
        }
        lastLocation = loc;
        tvDistance.setText(String.valueOf(distance));
        tvVelocity.setText(String.valueOf(loc.getSpeed()));

        LatLng myPoz = new LatLng(loc.getLatitude(),loc.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPoz, 17));
       // mMap.addMarker(new MarkerOptions().position(myPoz).title("МОЕ"));
        CircleOptions circleOptions = new CircleOptions()
                .center(myPoz).radius(3)
                .fillColor(Color.BLUE).strokeColor(Color.WHITE)
                .strokeWidth(5);

        Circle nowCircle =  mMap.addCircle(circleOptions);

        if (myCirclePoz!=null) {
            myCirclePoz.remove();
        }
        myCirclePoz = nowCircle;
        txttemp.setText(String.valueOf(mMarkerArrayList.size()));
    }
}