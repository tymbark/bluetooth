package com.example.damianmichalak.bluetooth_test.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.damianmichalak.bluetooth_test.R;
import com.example.damianmichalak.bluetooth_test.bluetooth.BluetoothReceiver;
import com.example.damianmichalak.bluetooth_test.bluetooth.ConnectionManager;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends AppCompatActivity {

    private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
    private ConnectionManager manager = new ConnectionManager();
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        showFragment(StartFragment.newInstance());

        findViewById(R.id.drawer_console).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                showFragment(ConsoleFragment.newInstance());
            }
        });

        findViewById(R.id.drawer_connection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                showFragment(StatusFragment.newInstance());
            }
        });

        findViewById(R.id.drawer_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                showFragment(GPSFragment.newInstance());
            }
        });

        setupBluetooth();
    }

    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void setupBluetooth() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothReceiver, filter);

        bluetoothReceiver.setDevicesListener(manager);
    }

    public ConnectionManager getManager() {
        return manager;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.getInstance().destroy();
        if (manager != null) {
            manager.destroy();
        }
        if (bluetoothReceiver != null) {
            unregisterReceiver(bluetoothReceiver);
        }
    }

    public void popupResult(final String messageToSend) {
        manager.write(messageToSend);
    }

}
