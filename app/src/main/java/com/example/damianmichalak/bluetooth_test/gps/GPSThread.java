package com.example.damianmichalak.bluetooth_test.gps;

import com.example.damianmichalak.bluetooth_test.bluetooth.ConnectionManager;
import com.example.damianmichalak.bluetooth_test.view.Logger;

public class GPSThread extends Thread {

    // used to pause and restart
    private boolean run = true;

    //used to turn off thread
    private boolean started = false;

    private final ConnectionManager connectionManager;

    public GPSThread(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void setRun(boolean isRunning) {
        this.run = isRunning;
    }

    @Override
    public void run() {
        started = true;
        while (started) {
            try {

                Thread.sleep(1500);
                if (!run) continue;

                connectionManager.sendOptions().sendGPSRequest();
                Thread.sleep(1500);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isStarted() {
        return started;
    }

    public void cancel() {
        Logger.getInstance().log("GPS thread stopped.");
        run = false;
        started = false;
    }

}
