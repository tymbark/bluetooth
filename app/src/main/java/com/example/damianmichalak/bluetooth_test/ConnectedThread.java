package com.example.damianmichalak.bluetooth_test;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class ConnectedThread extends Thread {

    interface MessageReceiver {

        void messageReceived(String msg);

    }

    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private GpsProvider.GpsPointListener gpsPointListener;
    private MessageReceiver receiver;

    public ConnectedThread(BluetoothSocket socket, MessageReceiver receiver) {
        this.socket = socket;
        this.receiver = receiver;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Logger.log(e.getMessage());
        }

        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    public void run() {
        Logger.log("connected thread start reading");

        while (true) {
            byte[] buffer = new byte[1024];
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            try {
                inputStream.read(buffer);

                final String response = new String(buffer);

                receiver.messageReceived(response);
                if (gpsPointListener != null) {
                    gpsPointListener.deliverMessage(response);
                }
            } catch (IOException e) {
                Logger.log("Error during read message    cause->" + e.getMessage());
                break;
            }
        }
    }

    public void write(String message) {
        try {
            byte[] bytes = message.getBytes();
            outputStream.write(bytes);
            Logger.log("Message sent: [" + message + "]");
        } catch (IOException e) {
            Logger.log("Error during write message    cause->" + e.getMessage());
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    public void setGpsPointListener(GpsProvider.GpsPointListener listener) {
        this.gpsPointListener = listener;
    }
}
