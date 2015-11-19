package com.example.damianmichalak.bluetooth_test.gps;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MessageParser {

    private final List<LatLng> route = new ArrayList<>();

    public List<LatLng> getRoute() {
        return route;
    }

    public LatLng parse(String msg) {
        if (msg.startsWith("gps")) {

            final String[] strings = msg.split("\\s+");

            final double lat = Double.parseDouble(strings[1]);
            final double lng = Double.parseDouble(strings[2]);
            return new LatLng(lat, lng);
        }
        return null;
    }
}
