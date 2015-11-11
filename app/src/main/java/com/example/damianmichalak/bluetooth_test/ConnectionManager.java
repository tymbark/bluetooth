package com.example.damianmichalak.bluetooth_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;

public class ConnectionManager implements DevicesListener, ConnectThread.ConnectionStatus, ConnectedThread.MessageReceiver {

    public interface ConnectionListener {

        void piVisible();

        void piInvisible();

        void piConnected();

        void piDisconnected();

    }

    private final BluetoothAdapter bluetoothAdapter;
    private final MessageParser messageParser;
    private final ConnectionListener listener;
    private BluetoothSocket socket;
    private BluetoothDevice pi;
    private ConnectedThread connectedThread;
    private GPSThread gpsThread;
    private ConnectThread connectThread;

    private boolean onlyPi = false;

    public ConnectionManager(BluetoothAdapter bluetoothAdapter, ConnectionListener listener) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.listener = listener;
        messageParser = new MessageParser();
        gpsThread = new GPSThread(this);

        if (bluetoothAdapter.isEnabled()) {
            Logger.log("Bluetooth is on");
        } else {
            Logger.log("Bluetooth is off");
        }
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
            listener.piVisible();
        }
    }

    @Override
    public void connectionFail() {
        listener.piDisconnected();
    }

    @Override
    public void connectionSuccess(BluetoothSocket socket) {
        listener.piConnected();
        this.socket = socket;
        connectedThread = new ConnectedThread(socket, this);
        connectedThread.start();
    }

    @Override
    public void messageReceived(String msg) {
        Logger.log("Message recived: [" + msg + "]");
        messageParser.parse(msg);
    }

}
