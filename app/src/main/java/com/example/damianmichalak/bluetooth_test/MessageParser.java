package com.example.damianmichalak.bluetooth_test;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MessageParser {

    private final List<LatLng> route = new ArrayList<>();

    public void parse(String msg) {
        if (msg.startsWith("gps")) {
            final String[] strings = msg.split("\\s+");
            assert strings.length == 3;

            final double lat = Double.parseDouble(strings[1]);
            final double lng = Double.parseDouble(strings[2]);
            route.add(new LatLng(lat, lng));
        }
    }
}
