package com.example.damianmichalak.bluetooth_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

class ConnectThread extends Thread {

    private final BluetoothSocket socket;
    private final BluetoothDevice pi;
    private final MainActivity.SocketListener listener;
    private BluetoothAdapter bluetoothAdapter;
    UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");

    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, MainActivity.SocketListener listener) {
        // Use a temporary object that is later assigned to socket,
        // because socket is final
        this.listener = listener;
        BluetoothSocket tmp = null;
        this.bluetoothAdapter = bluetoothAdapter;
        pi = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) { }
        socket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        bluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            socket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                socket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        listener.socket(socket);
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }
    }
}