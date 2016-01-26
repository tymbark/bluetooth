package com.example.damianmichalak.bluetooth_test.bluetooth;

import com.example.damianmichalak.bluetooth_test.view.CarControlFragment;

public class SendingManager {


    private ConnectedThread connectedThread;

    private void write(String messageToSend) {
        if (connectedThread != null) {
            connectedThread.write(messageToSend);
        }
    }

    public void sendCarDirections(CarControlFragment.CarDirection tempCar) {
        final String message = "pwm " + tempCar.speed + " " + tempCar.dir.toString();
        write(message);
    }

    public void sendGPSRequest() {
        write("gps");
    }

    public void sendDisconnect() {
        write("off");
    }

    public void sendRawMessage(String messageToSend) {
        write(messageToSend);
    }

    public void sendStartPoints() {
        write("points on");
    }

    public void sendStopPoints() {
        write("points off");
    }

    public void sendResetPoints() {
        write("points reset");
    }

    public void sendCalculateArea() {
        write("area");
    }

    public void setThread(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }
}
