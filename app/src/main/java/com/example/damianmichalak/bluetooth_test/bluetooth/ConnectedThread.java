package com.example.damianmichalak.bluetooth_test.bluetooth;

import android.bluetooth.BluetoothSocket;

import com.example.damianmichalak.bluetooth_test.activity.Logger;

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
            Logger.getInstance().log(e.getMessage());
        }

        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    public void run() {
        Logger.getInstance().log("connected thread start reading");

        while (true) {
            byte[] buffer = new byte[256];
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
            } catch (IOException e) {
                Logger.getInstance().log("Error during read message cause->" + e.getMessage());
                break;
            }
        }
    }

    public void write(String message) {
        try {
            byte[] bytes = message.getBytes();
            outputStream.write(bytes);
            Logger.getInstance().log("Message sent: [" + message + "]");
        } catch (IOException e) {
            Logger.getInstance().log("Error during write message cause->" + e.getMessage());
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Logger.getInstance().log("Error during cancel cause->" + e.getMessage());
        }
    }

}
