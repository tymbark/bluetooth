package com.example.damianmichalak.bluetooth_test.view;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.damianmichalak.bluetooth_test.R;
import com.example.damianmichalak.bluetooth_test.bluetooth.ConnectionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class GPSFragment extends Fragment implements OnMapReadyCallback, ConnectionManager.ConnectionListener {

    private MainActivity activity;
    private GoogleMap mMap;
    private boolean zoomInited = false;
    private Polyline currentPolyline;

    private int[] colorsList = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
    private int startGpsClickCount = 0;
    private boolean startedGPS = false;
    private TextView startStop;
    private SupportMapFragment mapFragment;

    public static GPSFragment newInstance() {
        return new GPSFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View output = inflater.inflate(R.layout.gps_fragment, container, false);
        mapFragment = new SupportMapFragment() {
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                mMap = mapFragment.getMap();
            }
        };
        getChildFragmentManager().beginTransaction().add(R.id.map_container, mapFragment).commit();
        return output;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.gps_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getManager().clearRoute();
            }
        });

        startStop = (TextView) view.findViewById(R.id.gps_start);
        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getManager().toggleGPS();
                startedGPS = !startedGPS;
                if (startedGPS) {
                    currentPolyline = mMap.addPolyline(new PolylineOptions().color(colorsList[startGpsClickCount%colorsList.length]));
                    startGpsClickCount++;
                    startStop.setText("Stop GPS");
                } else {
                    startStop.setText("Start GPS");
                }
            }
        });

        activity = (MainActivity) getActivity();
        activity.getManager().addConnectionListener(this);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.getManager().removeConnectionListener(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        currentPolyline = mMap.addPolyline(new PolylineOptions());
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
    public void GPSpointReceived(final List<LatLng> route) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (route.size() == 1) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(route.get(0), 12.0f));
                }

                List<LatLng> points = currentPolyline.getPoints();
                points.addAll(route);
                currentPolyline.setPoints(points);


                if (mMap != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(route.get(route.size() - 1)));
                    if (!zoomInited) {
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                        zoomInited = true;
                    }
                }
            }
        });
    }

    @Override
    public void pointReceived(PointF pointF) {

    }

    @Override
    public void time(int timestamp) {

    }

    @Override
    public void searchStarted() {

    }
}
