package com.example.damianmichalak.bluetooth_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager implements DevicesListener, ConnectThread.ConnectionStatus, ConnectedThread.MessageReceiver {

    private static ConnectionManager connManager;

    public static ConnectionManager getInstance(){
        if(connManager==null){
            connManager = new ConnectionManager();
        }
        return connManager;
    }

    public interface ConnectionListener {

        void piVisible();

        void piInvisible();

        void piConnected();

        void piDisconnected();

        void pointRecived(LatLng point);
    }

    private final BluetoothAdapter bluetoothAdapter;
    private final MessageParser messageParser;
    private List<ConnectionListener> connectionListeners;
    private BluetoothSocket socket;
    private BluetoothDevice pi;
    private ConnectedThread connectedThread;
    private GPSThread gpsThread;
    private ConnectThread connectThread;

    private boolean onlyPi = false;

    public ConnectionManager(){
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.connectionListeners = new ArrayList<>();
        messageParser = new MessageParser();
        gpsThread = new GPSThread(this);

        if (bluetoothAdapter.isEnabled()) {
            Logger.log("Bluetooth is on");
        } else {
            Logger.log("Bluetooth is off");
        }
    }

    /*public ConnectionManager(BluetoothAdapter bluetoothAdapter, ConnectionListener listener) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.listener = listener;
        messageParser = new MessageParser();
        gpsThread = new GPSThread(this);

        if (bluetoothAdapter.isEnabled()) {
            Logger.log("Bluetooth is on");
        } else {
            Logger.log("Bluetooth is off");
        }
    }*/

    public void addConnectionListener(ConnectionListener listener){
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener){
        connectionListeners.remove(listener);
    }

    public void connectToPi() {
        connectThread = new ConnectThread(pi, bluetoothAdapter, this);
        connectThread.start();
    }

    public void searchForPi(boolean checked) {
        onlyPi = checked;
        bluetoothAdapter.startDiscovery();
    }

    public void startGPS() {
        gpsThread.setRun(true);
        if (!gpsThread.started()) {
            gpsThread.start();
        }
    }

    public void stopGPS() {
        gpsThread.setRun(false);
    }

    public void write(String messageToSend) {
        connectedThread.write(messageToSend);
    }

    @Override
    public void newDevice(BluetoothDevice device) {
        if (!onlyPi) {
            Logger.log("found: " + device.getName() + " # " + device.getAddress());
        }

        if (device.getName() != null && device.getName().equals("raspberrypi")) {
            pi = device;
            ParcelUuid[] temp = pi.getUuids();
            if (temp != null) {
                Logger.log("Pi UUID's");
                for (int i = 0; i < temp.length; i++) {
                    Logger.log(temp[i].getUuid().toString());
                }
            }
            piVisibleBroadcast();
        }
    }

    @Override
    public void connectionFail() {
        piDisconnectedBroadcast();
    }

    @Override
    public void connectionSuccess(BluetoothSocket socket) {
        piConnectedBroadcast();
        this.socket = socket;
        connectedThread = new ConnectedThread(socket, this);
        connectedThread.start();
    }

    @Override
    public void messageReceived(String msg) {
        Logger.log("Message recived: [" + msg + "]");
        LatLng point = messageParser.parse(msg);
        pointRecivedBroadcast(point);
    }

    private void pointRecivedBroadcast(LatLng point) {
        for(int i=0; i<connectionListeners.size(); i++){
            connectionListeners.get(i).pointRecived(point);
        }
    }


    private void piVisibleBroadcast(){
        for(int i=0; i<connectionListeners.size(); i++){
            connectionListeners.get(i).piVisible();
        }
    }

    private void piInvisibleBroadcast(){
        for(int i=0; i<connectionListeners.size(); i++){
            connectionListeners.get(i).piInvisible();
        }
    }

    private void piConnectedBroadcast(){
        for(int i=0; i<connectionListeners.size(); i++){
            connectionListeners.get(i).piConnected();
        }
    }

    private void piDisconnectedBroadcast(){
        for(int i=0; i<connectionListeners.size(); i++){
            connectionListeners.get(i).piDisconnected();
        }
    }
}
