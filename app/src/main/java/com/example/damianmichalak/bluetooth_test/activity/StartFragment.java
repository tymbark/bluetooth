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

public class StartFragment extends Fragment implements ConnectionManager.ConnectionListener {

    private MainActivity activity;
    private View progressView;
    private View start;
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
        progressView = view.findViewById(R.id.start_progress_view);
        start = view.findViewById(R.id.start);
        status = (TextView) view.findViewById(R.id.start_status_text);
        activity.getManager().addConnectionListener(this);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setVisibility(View.GONE);
                progressView.setVisibility(View.VISIBLE);
                status.setText("Searching for PI...");
                activity.getManager().searchForPi();
                activity.enableDrawer();
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
                status.setText("Connecting to PI...");
            }
        });
    }

    @Override
    public void piInvisible() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressView.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void piConnected() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.showFragment(StatusFragment.newInstance());
            }
        });
    }

    @Override
    public void piDisconnected() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressView.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void pointReceived(List<LatLng> points) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void time(int timestamp) {

    }

    @Override
    public void searchStarted() {

    }
}
