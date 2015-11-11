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
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends AppCompatActivity implements Logger.LoggerListener, ConnectionManager.ConnectionListener {

    private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
    private ConnectionManager manager;

    private TextView console;
    private ScrollView scroll;
    private CheckBox checkbox;
    private Button connectPI;
    private Button customMsgButton;
    private Button startGPSButton;
    private Button stopGPSButton;

    private boolean consoleVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console = (TextView) findViewById(R.id.console_output);
        scroll = (ScrollView) findViewById(R.id.console_scroll);
        checkbox = (CheckBox) findViewById(R.id.only_pi);
        startGPSButton = (Button) findViewById(R.id.start_gps);
        stopGPSButton = (Button) findViewById(R.id.stop_gps);
        customMsgButton = (Button) findViewById(R.id.custom_msg);
        connectPI = (Button) findViewById(R.id.connect_pi);
        console.setMovementMethod(new ScrollingMovementMethod());
        final CustomMessageDialog dialog = new CustomMessageDialog(MainActivity.this);


        setupLogger();
        setupBluetooth();


        findViewById(R.id.find_devices)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        manager.searchForPi(checkbox.isChecked());
                    }
                });
        connectPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.connectToPi();
            }
        });
        startGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.startGPS();
                stopGPSButton.setEnabled(true);
                startGPSButton.setEnabled(false);
            }
        });
        stopGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.stopGPS();
                stopGPSButton.setEnabled(false);
                startGPSButton.setEnabled(true);
            }
        });
        customMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        findViewById(R.id.open_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(GPSActivity.newIntent(MainActivity.this));
            }
        });

    }

    private void setupLogger() {
        Logger.setListener(this);
        Logger.log("Hello World!");
    }

    private void setupBluetooth() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothReceiver, filter);

        manager = new ConnectionManager(BluetoothAdapter.getDefaultAdapter(), this);
        bluetoothReceiver.setDevicesListener(manager);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem hide = menu.findItem(R.id.action_hide_console);
        final MenuItem show = menu.findItem(R.id.action_show_console);

        hide.setVisible(consoleVisible);
        show.setVisible(!consoleVisible);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                Logger.clear();
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
        Logger.destroy();
        if (bluetoothReceiver != null) {
            unregisterReceiver(bluetoothReceiver);
        }
    }

    @Override
    public void write(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (console == null) return;
                if (scroll == null) return;
                console.setText(message);
                scroll.post(new Runnable() {
                    public void run() {
                        scroll.smoothScrollTo(0, console.getBottom());
                    }
                });
            }
        });
    }

    @Override
    public void piVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectPI.setEnabled(true);
            }
        });
    }

    @Override
    public void piInvisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectPI.setEnabled(false);
            }
        });
    }

    @Override
    public void piConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startGPSButton.setEnabled(true);
                customMsgButton.setEnabled(true);
            }
        });
    }

    @Override
    public void piDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startGPSButton.setEnabled(false);
                customMsgButton.setEnabled(false);
            }
        });
    }

    public void popupResult(final String messageToSend) {
        manager.write(messageToSend);
    }
}
