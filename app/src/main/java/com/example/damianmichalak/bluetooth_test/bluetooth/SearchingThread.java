package com.example.damianmichalak.bluetooth_test.bluetooth;

import android.bluetooth.BluetoothAdapter;

import com.example.damianmichalak.bluetooth_test.view.Logger;

public class SearchingThread extends Thread {

    private final BluetoothAdapter adapter;
    private final ConnectionManager manager;
    private boolean raspberryFound = false;

    public SearchingThread(BluetoothAdapter adapter, ConnectionManager manager) {
        this.adapter = adapter;
        this.manager = manager;
    }

    @Override
    public void run() {
        Logger.getInstance().log("Searching thread: started");

        int i = 0;
        while (!raspberryFound && i < 5) {
            Logger.getInstance().log("Searching thread: loop" + i);
            adapter.startDiscovery();
            try {
                Thread.sleep(5000 * i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            i++;
        }
        manager.piInvisible();
    }

    public void setRaspberryFound(boolean raspberryFound) {
        this.raspberryFound = raspberryFound;
    }

    public boolean isRaspberryFound() {
        return raspberryFound;
    }
}
