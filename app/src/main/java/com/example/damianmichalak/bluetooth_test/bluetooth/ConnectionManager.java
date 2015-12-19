package com.example.damianmichalak.bluetooth_test.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.PointF;
import android.os.Handler;
import android.os.ParcelUuid;

import com.example.damianmichalak.bluetooth_test.gps.GPSThread;
import com.example.damianmichalak.bluetooth_test.gps.MessageParser;
import com.example.damianmichalak.bluetooth_test.view.CarControlFragment;
import com.example.damianmichalak.bluetooth_test.view.Logger;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager implements DevicesListener, ConnectThread.ConnectionStatus, ConnectedThread.MessageReceiver {

    private class PiStatus {
        public boolean searching = false;
        public boolean visible = false;
        public boolean connected = false;
        public int timestamp = 0;
    }

    public interface ConnectionListener {

        void piVisible();

        void piInvisible();

        void piConnected();

        void piDisconnected();

        void GPSpointReceived(List<LatLng> route);

        void pointReceived(PointF pointF);

        void time(int timestamp);

        void searchStarted();
    }

    private final BluetoothAdapter bluetoothAdapter;
    private final MessageParser messageParser;
    private List<ConnectionListener> connectionListeners;
    private BluetoothDevice pi;
    private ConnectedThread connectedThread;
    private SearchingThread searchingThread;
    private ConnectThread connectThread;
    private GPSThread gpsThread;
    private PiStatus piStatus = new PiStatus();

    private List<LatLng> route = new ArrayList<>();

    final Handler handler = new Handler();
    final Runnable timerRunnable = new Runnable() {
        public void run() {
            if (piStatus.connected) {
                timeBroadcast();
                piStatus.timestamp++;
                handler.postDelayed(timerRunnable, 1000);
            }
        }
    };

    public ConnectionManager() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.connectionListeners = new ArrayList<>();
        messageParser = new MessageParser();

        if (bluetoothAdapter.isEnabled()) {
            Logger.getInstance().log("Bluetooth is on");
        } else {
            Logger.getInstance().log("Bluetooth is off");
        }
    }

    public void destroy() {
        connectionListeners.clear();
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
        if (piStatus.connected) {
            listener.piConnected();
        } else {
            listener.piDisconnected();
        }

        if (!piStatus.visible) {
            listener.piInvisible();
        }

        if (piStatus.searching) {
            listener.searchStarted();
        }

        if (piStatus.visible) {
            listener.piVisible();
        }

        listener.time(piStatus.timestamp);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    private void connectToPi() {
        connectThread = new ConnectThread(pi, bluetoothAdapter, this);
        connectThread.start();
    }

    public void searchForPi() {
        searchingThread = new SearchingThread(bluetoothAdapter, this);
        searchingThread.start();
        piStatus.searching = true;
    }

    public void toggleGPS() {
        if (gpsThread == null) {
            gpsThread = new GPSThread(this);
        }

        if (!gpsThread.started()) {
            gpsThread.setRun(true);
            gpsThread.start();
        } else {
            gpsThread.setRun(false);
            gpsThread = null;
            clearRoute();
        }
    }

    public void write(String messageToSend) {
        connectedThread.write(messageToSend);
    }

    @Override
    public void newDevice(BluetoothDevice device) {
        Logger.getInstance().log("found: " + device.getName() + " # " + device.getAddress());

        if (device.getName() != null && device.getName().equals("raspberrypi")) {
            pi = device;
            ParcelUuid[] temp = pi.getUuids();
            if (temp != null) {
                Logger.getInstance().log("Pi UUID's");
                for (int i = 0; i < temp.length; i++) {
                    Logger.getInstance().log(temp[i].getUuid().toString());
                }
            }
            piVisibleBroadcast();
            searchingThread.setRaspberryFound(true);
        }
    }

    public void piInvisible() {
        piInvisibleBroadcast();
        piStatus.searching = false;
    }

    @Override
    public void connectionFail() {
        piDisconnectedBroadcast();
    }

    @Override
    public void connectionSuccess(BluetoothSocket socket) {
        piConnectedBroadcast();
        connectedThread = new ConnectedThread(socket, this);
        connectedThread.start();
    }

    @Override
    public void messageReceived(String msg) {
        Logger.getInstance().log("Message received: [" + msg + "]");
        final Object response = messageParser.parse(msg);

        if (response instanceof LatLng) {
            route.add((LatLng) response);
            GPSpointReceivedBroadcast();
        }

        if (response instanceof PointF) {
            pointReceivedBroadcast((PointF) response);
        }

    }

    private void GPSpointReceivedBroadcast() {
        for (int i = 0; i < connectionListeners.size(); i++) {
            connectionListeners.get(i).GPSpointReceived(route);
        }
    }

    private void pointReceivedBroadcast(PointF pointF) {
        for (int i = 0; i < connectionListeners.size(); i++) {
            connectionListeners.get(i).pointReceived(pointF);
        }
    }


    private void piVisibleBroadcast() {
        connectToPi();
        piStatus.visible = true;
        for (int i = 0; i < connectionListeners.size(); i++) {
            connectionListeners.get(i).piVisible();
        }
    }

    private void piInvisibleBroadcast() {
        piStatus.visible = false;
        for (int i = 0; i < connectionListeners.size(); i++) {
            connectionListeners.get(i).piInvisible();
        }
    }

    private void piConnectedBroadcast() {
        handler.post(timerRunnable);
        piStatus.connected = true;
        for (int i = 0; i < connectionListeners.size(); i++) {
            connectionListeners.get(i).piConnected();
        }
    }

    private void piDisconnectedBroadcast() {
        piStatus.connected = false;
        for (int i = 0; i < connectionListeners.size(); i++) {
            connectionListeners.get(i).piDisconnected();
        }
    }

    private void timeBroadcast() {
        for (int i = 0; i < connectionListeners.size(); i++) {
            connectionListeners.get(i).time(piStatus.timestamp);
        }
    }

    public void clearRoute() {
        route.clear();
    }

    public void sendCarDirections(CarControlFragment.CarDirection tempCar) {
        final String message = "pwm " + tempCar.speed + " " + tempCar.dir.toString();
        write(message);
    }

}
