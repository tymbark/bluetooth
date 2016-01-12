package com.example.damianmichalak.bluetooth_test.view;

import android.graphics.PointF;
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
import com.example.damianmichalak.bluetooth_test.view.widget.DrawingView;
import com.example.damianmichalak.bluetooth_test.view.widget.JoystickListener;
import com.example.damianmichalak.bluetooth_test.view.widget.JoystickView;


public class CarControlFragment extends BaseFragment implements JoystickListener, ConnectionManager.ConnectionListener {

    public enum Direction {
        LEFT, RIGHT, STRAIGHT
    }

    public class CarDirection {
        public int speed = 0;
        public Direction dir = Direction.STRAIGHT;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CarDirection)) return false;
            CarDirection that = (CarDirection) o;
            return speed == that.speed &&
                    dir == that.dir;
        }

    }

    private CarDirection car;
    private TextView speedValue;
    private TextView directionValue;
    private TextView start;
    private TextView pause;
    private JoystickView joystick;
    private MainActivity activity;
    private DrawingView drawingView;
    private boolean sendingPWM = false;
    private boolean toastDisplayed = false;

    public static Fragment newInstance() {
        return new CarControlFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.car_control_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        speedValue = (TextView) view.findViewById(R.id.car_control_speed_value);
        directionValue = (TextView) view.findViewById(R.id.car_control_direction_value);
        joystick = (JoystickView) view.findViewById(R.id.car_control_joystick);
        drawingView = (DrawingView) view.findViewById(R.id.car_control_drawing_view);
        start = (TextView) view.findViewById(R.id.car_control_start);
        pause = (TextView) view.findViewById(R.id.car_control_pause);

        joystick.setJostickListener(this);

        activity = (MainActivity) getActivity();

        start.setEnabled(!activity.getManager().getPiStatus().counts);
        pause.setEnabled(activity.getManager().getPiStatus().counts);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getManager().sendOptions().sendStartPoints();
                activity.getManager().setCountPoints(true);
                start.setEnabled(!activity.getManager().getPiStatus().counts);
                pause.setEnabled(activity.getManager().getPiStatus().counts);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getManager().sendOptions().sendStopPoints();
                activity.getManager().setCountPoints(false);
                start.setEnabled(!activity.getManager().getPiStatus().counts);
                pause.setEnabled(activity.getManager().getPiStatus().counts);
            }
        });


        drawingView.schedulePoints(activity.getManager().getPreviousPoints());
        activity.getManager().addConnectionListener(this);
    }

    @Override
    public void onMoved(int pan, int tilt) {
        final CarDirection tempCar = new CarDirection();

        if (pan <= -4) {
            tempCar.dir = Direction.LEFT;
            directionValue.setText(R.string.car_control_left);
        } else if (pan >= 4) {
            tempCar.dir = Direction.RIGHT;
            directionValue.setText(R.string.car_control_right);
        } else {
            tempCar.dir = Direction.STRAIGHT;
            directionValue.setText(R.string.car_control_straight);
        }

        final int speed = (tilt / 2) * -1;
        speedValue.setText("" + speed);
        tempCar.speed = speed;

        if (!tempCar.equals(car) && sendingPWM) {
            sendCar(tempCar);
        }

        car = tempCar;
    }

    @Override
    public void onStarted() {
        sendingPWM = true;
    }

    @Override
    public void onStopped() {
        sendingPWM = false;
        directionValue.setText(R.string.car_control_straight);
        speedValue.setText("0");
        car.speed = 0;
        car.dir = Direction.STRAIGHT;
        sendCar(car);
    }

    private void sendCar(CarDirection tempCar) {
        if (activity.getManager().sendOptions() != null) {
            activity.getManager().sendOptions().sendCarDirections(tempCar);
        } else {
            if (!toastDisplayed) {
                Toast.makeText(activity, R.string.car_control_error, Toast.LENGTH_SHORT).show();
                toastDisplayed = true;
            }
        }
    }

    @Override
    public void pointReceived(final PointF pointF) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (drawingView != null) {
                    drawingView.addPoint(pointF);
                }
            }
        });
    }

}
