package com.example.damianmichalak.bluetooth_test.bluetooth;

import com.example.damianmichalak.bluetooth_test.view.CarControlFragment;

public class SendingManager {


    private final ConnectedThread connectedThread;

    public SendingManager(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }

    private void write(String messageToSend) {
        connectedThread.write(messageToSend);
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
}
