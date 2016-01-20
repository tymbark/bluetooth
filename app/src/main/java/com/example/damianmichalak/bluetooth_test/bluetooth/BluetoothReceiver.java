package com.example.damianmichalak.bluetooth_test.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.damianmichalak.bluetooth_test.view.Logger;

public class BluetoothReceiver extends BroadcastReceiver {

    private DevicesListener devicesListener;

    public void setDevicesListener(DevicesListener devicesListener) {
        this.devicesListener = devicesListener;
    }

    public BluetoothReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        int state;
        final BluetoothDevice device;

        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        writeLine("Bluetooth is on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        writeLine("Bluetooth is turning on...");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(context, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show();
                        writeLine("Bluetooth is off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        writeLine("Bluetooth is turning off...");
                        break;
                }
                break;

            case BluetoothDevice.ACTION_FOUND:
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (devicesListener != null && device != null) {
                    devicesListener.newDevice(device);
                }
                break;
        }
    }

    void writeLine(String msg) {
        if (devicesListener != null)
            Logger.getInstance().log(msg);
    }
}
