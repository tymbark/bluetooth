package com.example.damianmichalak.bluetooth_test.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.damianmichalak.bluetooth_test.R;
import com.example.damianmichalak.bluetooth_test.bluetooth.ConnectionManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;


public class StatusFragment extends Fragment implements ConnectionManager.ConnectionListener {

    private MainActivity activity;
    private TextView visibility;
    private TextView connection;
    private TextView time;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.status_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        visibility = (TextView) view.findViewById(R.id.status_visibility);
        connection = (TextView) view.findViewById(R.id.status_connection);
        time = (TextView) view.findViewById(R.id.status_time);

        activity = (MainActivity) getActivity();
        activity.getManager().addConnectionListener(this);
    }

    public static StatusFragment newInstance() {
        return new StatusFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.getManager().removeConnectionListener(this);
    }

    @Override
    public void piVisible() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                visibility.setText("Visible");
            }
        });
    }

    @Override
    public void piInvisible() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                visibility.setText("Invisible");
            }
        });
    }

    @Override
    public void piConnected() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connection.setText("Connected");
            }
        });
    }

    @Override
    public void piDisconnected() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connection.setText("Disconnected");
            }
        });
    }

    @Override
    public void pointReceived(List<LatLng> points) {

    }

    @Override
    public void time(final int timestamp) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                time.setText(timestamp + "");
            }
        });
    }
}