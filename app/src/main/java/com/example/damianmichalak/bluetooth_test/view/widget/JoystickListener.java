package com.example.damianmichalak.bluetooth_test.view.widget;

public interface JoystickListener {
    void onMoved(int pan, int tilt);
    void onStarted();
    void onStopped();
}