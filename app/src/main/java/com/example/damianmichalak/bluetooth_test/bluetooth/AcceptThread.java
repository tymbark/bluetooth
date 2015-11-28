package com.example.damianmichalak.bluetooth_test.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.example.damianmichalak.bluetooth_test.view.Logger;

import java.io.IOException;
import java.util.UUID;

class AcceptThread extends Thread {
    private final BluetoothServerSocket serverSocket;
    private final BluetoothAdapter bluetoothAdapter;
    private final Logger logger;
    UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");

    public AcceptThread(BluetoothAdapter bluetoothAdapter, Logger logger) {
        // Use a temporary object that is later assigned to serverSocket,
        // because serverSocket is final
        this.logger = logger;
        this.bluetoothAdapter = bluetoothAdapter;
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("chuj", uuid);
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
        serverSocket = tmp;
    }

    public void run() {
        logger.log("accept thread run");
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                logger.log("accept function");
                socket = serverSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)

//                manageConnectedSocket(socket);
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.log(e.getMessage());
                }
                break;
            }
        }
    }

    /**
     * Will cancel the listening socket, and cause the thread to finish
     */
    public void cancel() {
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }
}