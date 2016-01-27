package com.example.damianmichalak.bluetooth_test.view;

import android.graphics.PointF;
import android.support.v4.app.Fragment;

import com.example.damianmichalak.bluetooth_test.bluetooth.ConnectionManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class BaseFragment extends Fragment implements ConnectionManager.ConnectionListener {
    @Override
    public void piVisible() {

    }

    @Override
    public void piInvisible() {

    }

    @Override
    public void piConnected() {

    }

    @Override
    public void piDisconnected() {

    }

    @Override
    public void GPSpointReceived(List<LatLng> route) {

    }

    @Override
    public void pointReceived(PointF pointF) {

    }

    @Override
    public void searchStarted() {

    }

    @Override
    public void areaCalculated(float area) {

    }
}
