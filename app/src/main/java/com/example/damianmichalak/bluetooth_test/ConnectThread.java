package com.example.damianmichalak.bluetooth_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

class ConnectThread extends Thread {

    private BluetoothSocket socket;
    private final BluetoothDevice pi;
    private final MainActivity.SocketListener listener;
    private final Logger logger;
    private BluetoothAdapter bluetoothAdapter;
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothConnector.BluetoothSocketWrapper wrapper;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, MainActivity.SocketListener listener, Logger logger) {
        this.logger = logger;
        this.listener = listener;
        pi = device;
        this.bluetoothAdapter = bluetoothAdapter;
//        BluetoothSocket tmp = null;
//        logger.log("create connect thread");

        // Get a BluetoothSocket to connect with the given BluetoothDevice
//        try {
        // MY_UUID is the app's UUID string, also used by the server code
//            tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
//        } catch (IOException e) { }
//        socket = tmp;


    }

    public void run() {
        logger.log("Connect thread run");


        BluetoothConnector bluetoothConnector = new BluetoothConnector(logger, pi, false, bluetoothAdapter, null);
        try {
            wrapper = bluetoothConnector.connect();
        } catch (IOException e) {
            logger.log("fail!");
            logger.log(e.getMessage());
        }
        listener.socket(wrapper.getUnderlyingSocket());
        logger.log("success!");

        OutputStream outputStream = null;
        try {
            outputStream = wrapper.getOutputStream();
        } catch (IOException e) {
            logger.log(e.getMessage());
        }

        byte[] data = "KURWA MAC TO GUWNO DZIALA".getBytes();
        try {
            outputStream.write(data);
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
    }
}