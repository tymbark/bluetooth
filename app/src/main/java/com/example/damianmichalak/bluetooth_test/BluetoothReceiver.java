package com.example.damianmichalak.bluetooth_test;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReceiver extends BroadcastReceiver {

    private final DevicesListener devicesListener;

    public BluetoothReceiver(DevicesListener devicesListener) {
        this.devicesListener = devicesListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            devicesListener.newDevice(device);
        }
    }
}
