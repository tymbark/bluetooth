package com.example.damianmichalak.bluetooth_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class GPSActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionManager.ConnectionListener {

    private ConnectionManager manager = ConnectionManager.getInstance();

    private List<LatLng> route;

    private PolylineOptions polylineOptions;
    private GoogleMap mMap;
    private boolean zoomInited = false;

    public static Intent newIntent(Context context) {
        return new Intent(context, GPSActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        manager.addConnectionListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        route = new ArrayList<>();
        polylineOptions = new PolylineOptions();
        //polylineOptions.addAll(route);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        /*LatLng sydney = new LatLng(52.40065, 16.951059833);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        mMap = map;
        map.addPolyline(polylineOptions);
    }

    @Override
    public void piVisible() {

    }

    @Override
    public void piInvisible() {

    }

    @Override
    public void piConnected() {

    }

    @Override
    public void piDisconnected() {

    }

    @Override
    public void pointRecived(final LatLng point) {
        route.add(point);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMap != null) {
                    mMap.addPolyline(polylineOptions.add(point));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                    if(!zoomInited){
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                        zoomInited = true;
                    }
                }
            }
        });
    }
}
