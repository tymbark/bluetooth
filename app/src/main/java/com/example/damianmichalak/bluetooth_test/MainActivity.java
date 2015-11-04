package com.example.damianmichalak.bluetooth_test;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
@SuppressLint("SimpleDateFormat")
public class MainActivity extends AppCompatActivity implements DevicesListener {

    private BluetoothAdapter bluetoothAdapter;
    private TextView console;
    private ScrollView scroll;
    private String output = "";
    private boolean consoleVisible = true;
    private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console = (TextView) findViewById(R.id.console_output);
        scroll = (ScrollView) findViewById(R.id.console_scroll);
        console.setMovementMethod(new ScrollingMovementMethod());
        final Button devices = (Button) findViewById(R.id.find_devices);
        final Button but2 = (Button) findViewById(R.id.but2);
        writeLine("Hello World!");

        devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDevices();
            }
        });

        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeLine("dupa 2");
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter.isEnabled()) {
            writeLine("bluetooth is on");
        } else {
            writeLine("bluetooth is off");
        }

    }

    private void getDevices() {
        final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    private void getBondedDevices() {
        final Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        writeLine("found " + bondedDevices.size() + " devices");

        for (BluetoothDevice device : bondedDevices) {
            writeLine(device.getName());
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem hide = menu.findItem(R.id.action_hide_console);
        final MenuItem show = menu.findItem(R.id.action_show_console);

        hide.setVisible(consoleVisible);
        show.setVisible(!consoleVisible);

        return true;
    }

    private void writeLine(String s) {
        if (!output.isEmpty()) {
            output += "\n";

        }

        output += new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())) + " " + s;
        console.setText(output);
        scroll.post(new Runnable() {
            public void run() {
                scroll.smoothScrollTo(0, console.getBottom());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_hide_console:
                scroll.setVisibility(View.GONE);
                consoleVisible = !consoleVisible;
                ActivityCompat.invalidateOptionsMenu(this);
                return true;

            case R.id.action_show_console:
                scroll.setVisibility(View.VISIBLE);
                consoleVisible = !consoleVisible;
                ActivityCompat.invalidateOptionsMenu(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

    @Override
    public void newDevice(BluetoothDevice device) {
        writeLine("found device: " + device.getName());
    }
}
