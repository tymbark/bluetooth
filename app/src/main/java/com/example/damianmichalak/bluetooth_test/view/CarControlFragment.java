package com.example.damianmichalak.bluetooth_test.view;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.damianmichalak.bluetooth_test.R;
import com.example.damianmichalak.bluetooth_test.bluetooth.ConnectionManager;
import com.example.damianmichalak.bluetooth_test.view.widget.DrawingView;
import com.example.damianmichalak.bluetooth_test.view.widget.JoystickMovedListener;
import com.example.damianmichalak.bluetooth_test.view.widget.JoystickView;


public class CarControlFragment extends BaseFragment implements JoystickMovedListener, ConnectionManager.ConnectionListener  {

    @Override
    public void pointReceived(final PointF pointF) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (drawingView != null){
                    drawingView.addPoint(pointF);
                }
            }
        });
    }

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
    private JoystickView joystick;
    private MainActivity activity;
    private DrawingView drawingView;

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

        joystick.setOnJostickMovedListener(this);

        activity = (MainActivity) getActivity();

        view.findViewById(R.id.car_control_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getManager().messageReceived("point " + 0.0f + " " + gety());

            }
        });


        drawingView.schedulePoints(activity.getManager().getPreviousPoints());
        activity.getManager().addConnectionListener(this);
    }

    private int y = 0;

    private float gety() {
        return ++y;
    }

    @Override
    public void OnMoved(int pan, int tilt) {
        final CarDirection tempCar = new CarDirection();

        if (pan <= -4) {
            tempCar.dir = Direction.LEFT;
            directionValue.setText("left");
        } else if (pan >= 4) {
            tempCar.dir = Direction.RIGHT;
            directionValue.setText("right");
        } else {
            tempCar.dir = Direction.STRAIGHT;
            directionValue.setText("straight");
        }

        final int speed = (tilt / 2) * -1;
        speedValue.setText("" + speed);
        tempCar.speed = speed;

        if (!tempCar.equals(car)) {
            sendCar(tempCar);
        }

        car = tempCar;
    }

    private void sendCar(CarDirection tempCar) {
        activity.getManager().sendOptions().sendCarDirections(tempCar);
    }

    @Override
    public void OnReleased() {
        directionValue.setText("straight");
        speedValue.setText("0");
        car.speed = 0;
        car.dir = Direction.STRAIGHT;
        sendCar(car);
    }

    @Override
    public void OnReturnedToCenter() {

    }
}
