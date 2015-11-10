package com.example.damianmichalak.bluetooth_test;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class ConnectedThread extends Thread {
    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Logger logger;

    public ConnectedThread(BluetoothSocket socket, Logger logger) {
        this.socket = socket;
        this.logger = logger;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    public void run() {
        logger.log("connected thread run");


        // Keep listening to the InputStream until an exception occurs
        while (true) {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            try {
                // Read from the InputStream
                bytes = inputStream.read(buffer);
                // Send the obtained bytes to the UI activity
                //logger.log("something came");
                String s = new String(buffer);
                logger.log("Message recived: [" + s + "]");
            } catch (IOException e) {
                logger.log("Error during read message    cause->" + e.getMessage());
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String message) {
        try {
            byte[] bytes = message.getBytes();
            outputStream.write(bytes);
            logger.log("Message sent: [" + message + "]");
        } catch (IOException e) {
            logger.log("Error during write message    cause->" + e.getMessage());
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
