package com.example.damianmichalak.bluetooth_test.view;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damianmichalak.bluetooth_test.R;
import com.example.damianmichalak.bluetooth_test.bluetooth.ConnectionManager;

public class StartFragment extends BaseFragment implements ConnectionManager.ConnectionListener {

    private MainActivity activity;
    private View progressView;
    private TextView startStop;
    private TextView status;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.start_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
        status = (TextView) view.findViewById(R.id.start_status_text);
        progressView = view.findViewById(R.id.start_progress_view);
        startStop = (TextView) view.findViewById(R.id.start);
        activity.getManager().addConnectionListener(this);

        final ConnectionManager.PiStatus piStatus = activity.getManager().getPiStatus();

        if (piStatus.connected) {
            startStop.setText(R.string.start_disconnect);
        } else {
            startStop.setText(R.string.start_launch);
        }

        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (piStatus.connected) {
                    startStop.setVisibility(View.GONE);
                    progressView.setVisibility(View.VISIBLE);
                    status.setText(R.string.start_disconnecting);
                    activity.getManager().sendOptions().sendDisconnect();
                } else {
                    if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        Toast.makeText(getActivity(), R.string.start_bluetooth_error, Toast.LENGTH_SHORT).show();
                    } else {
                        startStop.setVisibility(View.GONE);
                        progressView.setVisibility(View.VISIBLE);
                        status.setText(R.string.start_searching);
                        activity.getManager().searchForPi();
                        activity.enableDrawer();
                    }
                }
            }
        });
    }

    public static Fragment newInstance() {
        return new StartFragment();
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
                status.setText(R.string.start_connecting);
            }
        });
    }

    @Override
    public void piInvisible() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressView.setVisibility(View.GONE);
                startStop.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void piConnected() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startStop.setText(R.string.start_disconnect);
                Toast.makeText(activity, R.string.start_connecting_success, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void piDisconnected() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressView.setVisibility(View.GONE);
                startStop.setVisibility(View.VISIBLE);
                startStop.setText(R.string.start_launch);
                Toast.makeText(activity, R.string.start_disconnected_success, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
