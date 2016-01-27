package com.example.damianmichalak.bluetooth_test.gps;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;

public class MessageParser {

    public static class LogOff {
    }

    public static class Area {
        public float area;

        public Area(float area) {
            this.area = area;
        }
    }


    public Object parse(String msg) {

        final String[] strings = msg.split("\\s+");

        if (strings.length == 0) {
            return null;
        }

        switch (strings[0]) {
            case "gps":

                if (strings.length != 4) return null;

                final double lat = Double.parseDouble(strings[1]);
                final double lng = Double.parseDouble(strings[2]);
                return new LatLng(lat, lng);

            case "point":

                if (strings.length != 3) return null;

                final float x = Float.parseFloat(strings[1]);
                final float y = Float.parseFloat(strings[2]);
                return new PointF(x, y);

            case "off":

                if (strings.length != 2) return null;
                if (strings[1].startsWith("ok")) {
                    return new LogOff();
                } else {
                    return null;
                }

            case "area":
                if (strings.length != 2) return null;
                final float area = Float.parseFloat(strings[1]);
                if (area < 0 || area > 1000000) {
                    return new Area(area);
                } else {
                    return null;
                }

            default:
                return null;
        }

    }
}
