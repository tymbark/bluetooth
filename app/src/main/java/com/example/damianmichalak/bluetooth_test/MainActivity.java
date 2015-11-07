package com.example.damianmichalak.bluetooth_test;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends AppCompatActivity implements DevicesListener, Logger {

    private BluetoothAdapter bluetoothAdapter;
    private TextView console;
    private ScrollView scroll;
    private CheckBox checkbox;
    private String output = "";
    private boolean consoleVisible = true;
    private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
    private BluetoothDevice pi;
    private View connectPI;

    AcceptThread acceptThread;
    ConnectedThread connectedThread;
    ConnectThread connectThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBluetooth();

        console = (TextView) findViewById(R.id.console_output);
        scroll = (ScrollView) findViewById(R.id.console_scroll);
        checkbox = (CheckBox) findViewById(R.id.only_pi);
        console.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.find_devices)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDevices();
                    }
                });
        connectPI = findViewById(R.id.connect_pi);
        connectPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectThread = new ConnectThread(pi, bluetoothAdapter, new SocketListener(), MainActivity.this);
                connectThread.start();
//                tryToConnect(pi);
            }
        });

        writeLine("Hello World!");
        if (bluetoothAdapter.isEnabled()) {
            writeLine("Bluetooth is on");
        } else {
            writeLine("Bluetooth is off");
        }

    }

    private void setupBluetooth() {
        bluetoothReceiver.setDevicesListener(this);
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothReceiver, filter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        acceptThread = new AcceptThread(bluetoothAdapter, this);
    }

    private void getDevices() {
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

    private void writeLine(final String s) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (console == null) return;
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

            case R.id.action_clear_console:
                output = "";
                console.setText("");
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
        if (bluetoothReceiver != null) {
            unregisterReceiver(bluetoothReceiver);
        }
    }

    @Override
    public void newDevice(@NonNull BluetoothDevice device) {
        if (!checkbox.isChecked()) {
            writeLine("found: " + device.getName() + " # " + device.getAddress());
        }

        if (device.getName() != null && device.getName().equals("raspberrypi")) {
            pi = device;
            connectPI.setEnabled(true);
        }
    }

    public void tryToConnect(BluetoothDevice device) {
        BluetoothSocket socket = null;
        try {
            Class<?> clazz = device.getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};

            Method m = clazz.getMethod("createRfcommSocket", paramTypes);
            Object[] params = new Object[]{Integer.valueOf(1)};

            socket = (BluetoothSocket) m.invoke(device, params);
            //socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            writeLine("Socket created!");
        } catch (Exception e) {
            e.printStackTrace();
            writeLine("ERROR!    " + e.getMessage());
        }
        try {
            socket.connect();
            writeLine("Socket connected!");
        } catch (IOException e) {
            e.printStackTrace();
            writeLine("ERROR!    " + e.getMessage());
        }

        try {
            OutputStream os = socket.getOutputStream();
            String s = "Witam";
            os.write(s.getBytes());
            writeLine("Message sent!");
            socket.close();

            writeLine("Socket closed!");
        } catch (IOException e) {
            e.printStackTrace();
            writeLine("ERROR!    " + e.getMessage());
        }
    }

    @Override
    public void log(String message) {
        writeLine(message);
    }

    public class SocketListener {

        public void socket(BluetoothSocket socket) {
            Log.d("CHUJ", "socket");
            connectedThread = new ConnectedThread(socket, MainActivity.this);
            byte[] data = "chuj".getBytes();
            connectedThread.write(data);
        }
    }
}
