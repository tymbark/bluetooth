package com.example.damianmichalak.bluetooth_test.view;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.damianmichalak.bluetooth_test.R;
import com.example.damianmichalak.bluetooth_test.bluetooth.BluetoothReceiver;
import com.example.damianmichalak.bluetooth_test.bluetooth.ConnectionManager;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends AppCompatActivity {

    private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
    private ConnectionManager manager = new ConnectionManager();
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private View console;
    private View home;
    private View connection;
    private View googleMaps;
    private View carControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        showFragment(StartFragment.newInstance());
        setupViews();
        setupBluetooth();

        disableDrawer();
    }

    private void setupViews() {
        console = findViewById(R.id.drawer_console);
        connection = findViewById(R.id.drawer_connection);
        home = findViewById(R.id.drawer_home);
        googleMaps = findViewById(R.id.drawer_google);
        carControl = findViewById(R.id.drawer_car_control);

        clearSelection();
        home.setBackgroundResource(R.color.default_selector_color);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                clearSelection();
                home.setBackgroundResource(R.color.default_selector_color);
                showFragment(StartFragment.newInstance());
            }
        });

        console.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                clearSelection();
                console.setBackgroundResource(R.color.default_selector_color);
                showFragment(ConsoleFragment.newInstance());
            }
        });

        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                clearSelection();
                connection.setBackgroundResource(R.color.default_selector_color);
                showFragment(StatusFragment.newInstance());
            }
        });

        googleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                clearSelection();
                googleMaps.setBackgroundResource(R.color.default_selector_color);
                showFragment(GPSFragment.newInstance());
            }
        });

        carControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                clearSelection();
                carControl.setBackgroundResource(R.color.default_selector_color);
                showFragment(CarControlFragment.newInstance());
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.gps_stop, R.string.gps_start) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawer.setDrawerListener(drawerToggle);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void clearSelection() {
        home.setBackgroundResource(android.R.color.transparent);
        console.setBackgroundResource(android.R.color.transparent);
        connection.setBackgroundResource(android.R.color.transparent);
        googleMaps.setBackgroundResource(android.R.color.transparent);
        carControl.setBackgroundResource(android.R.color.transparent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
        manager.sendOptions().sendRawMessage(messageToSend);
    }

    public void enableDrawer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                console.setEnabled(true);
                connection.setEnabled(true);
                googleMaps.setEnabled(true);
//                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            }
        });
    }

    public void disableDrawer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                console.setEnabled(false);
                connection.setEnabled(false);
                googleMaps.setEnabled(false);
//                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        });
    }
}
