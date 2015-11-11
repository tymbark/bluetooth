package com.example.damianmichalak.bluetooth_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Kamil on 2015-11-11.
 */
public abstract class BaseActivityWithConnection extends AppCompatActivity implements ConnectionManager.ConnectionListener {
    protected ConnectionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = ConnectionManager.getInstance();
        manager.addConnectionListener(this);
    }

    @Override
    protected void onDestroy() {
        manager.removeConnectionListener(this);
        super.onDestroy();
    }
}
