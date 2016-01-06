package com.example.damianmichalak.bluetooth_test.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.damianmichalak.bluetooth_test.view.Logger;

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
        Logger.getInstance().log("Connect thread run");


        BluetoothConnector bluetoothConnector = new BluetoothConnector(pi, false, bluetoothAdapter, null);
        try {
            wrapper = bluetoothConnector.connect();
        } catch (IOException e) {
            statusListener.connectionFail();
            Logger.getInstance().log("fail!");
            Logger.getInstance().log(e.getMessage());
        }

        if (wrapper == null) {
            Logger.getInstance().log("fail!");
        } else {
            socket = wrapper.getUnderlyingSocket();
            statusListener.connectionSuccess(socket);

            Logger.getInstance().log("success!");
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Logger.getInstance().log("Error during closing socket: " + e.getMessage());
        }
    }
}