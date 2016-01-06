package com.example.damianmichalak.bluetooth_test.bluetooth;

import android.bluetooth.BluetoothSocket;

import com.example.damianmichalak.bluetooth_test.view.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class ConnectedThread extends Thread {

    interface MessageReceiver {

        void messageReceived(String msg);
        void serverCrashed();

    }

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private MessageReceiver receiver;
    private boolean working = true;

    public ConnectedThread(BluetoothSocket socket, MessageReceiver receiver) {
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

        while (working) {
            byte[] buffer = new byte[256];
            try {
                Thread.sleep(100);
                if (!working) return;

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
                receiver.serverCrashed();
                break;
            }
        }

        Logger.getInstance().log("Connected Thread closed.");

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
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        working = false;
        Logger.getInstance().log("Connected Thread closing...");
    }

}
