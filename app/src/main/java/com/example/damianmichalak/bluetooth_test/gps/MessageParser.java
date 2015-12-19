package com.example.damianmichalak.bluetooth_test.gps;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MessageParser {

    private final List<LatLng> route = new ArrayList<>();

    public List<LatLng> getRoute() {
        return route;
    }

    public Object parse(String msg) {
        if (msg.startsWith("gps")) {

            final String[] strings = msg.split("\\s+");

            if (strings.length != 3) return null;

            final double lat = Double.parseDouble(strings[1]);
            final double lng = Double.parseDouble(strings[2]);
            return new LatLng(lat, lng);

        } else if (msg.startsWith("point")) {


            final String[] strings = msg.split("\\s+");

            if (strings.length != 3) return null;

            final float x = Float.parseFloat(strings[1]);
            final float y = Float.parseFloat(strings[2]);
            return new PointF(x, y);
        }

        return null;
    }
}
