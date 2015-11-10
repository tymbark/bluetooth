package com.example.damianmichalak.bluetooth_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

class ConnectThread extends Thread {

    interface ConnectionStatus {
        void connectionFail();
        void connectionSuccess(BluetoothSocket socket);
    }

    private BluetoothSocket socket;
    private ConnectionStatus statusListener;
    private final BluetoothDevice pi;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothConnector.BluetoothSocketWrapper wrapper;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, ConnectionStatus statusListener) {
        pi = device;
        this.statusListener = statusListener;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void run() {
        Logger.log("Connect thread run");


        BluetoothConnector bluetoothConnector = new BluetoothConnector(pi, false, bluetoothAdapter, null);
        try {
            wrapper = bluetoothConnector.connect();
        } catch (IOException e) {
            statusListener.connectionFail();
            Logger.log("fail!");
            Logger.log(e.getMessage());
        }

        socket = wrapper.getUnderlyingSocket();
        statusListener.connectionSuccess(socket);

        Logger.log("success!");
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Logger.log(e.getMessage());
        }
    }
}