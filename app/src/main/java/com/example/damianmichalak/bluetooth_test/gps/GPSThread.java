package com.example.damianmichalak.bluetooth_test.gps;

import com.example.damianmichalak.bluetooth_test.bluetooth.ConnectionManager;

public class GPSThread extends Thread {

    private boolean run = false;
    private final ConnectionManager connectionManager;
    private boolean started = false;

    public GPSThread(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void setRun(boolean isRunning) {
        this.run = isRunning;
    }

    @Override
    public void run() {
        started = true;
        while (true) {
            if (!run) break;
            connectionManager.write("gps");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean started() {
        return started;
    }
}
