package com.example.damianmichalak.bluetooth_test;

/**
 * Created by Kamil on 2015-11-10.
 */
public class GpsProvider {
private static final String GPS_REQUEST_TAG = "gps";
    private static ConnectedThread connectedThread;

    public GpsProvider(ConnectedThread connectedThread){
        this.connectedThread = connectedThread;
    }

    public static void setGpsPointRequestListener(GpsPointListener listener){
        connectedThread.setGpsPointListener(listener);
    }

    public static void requestGpsPoint(GpsPointListener listener){
        connectedThread.write("gps");
    }

    public interface GpsPointListener{
        void deliverPoint(float x, float y);
        void deliverMessage(String msg);
    }
}
