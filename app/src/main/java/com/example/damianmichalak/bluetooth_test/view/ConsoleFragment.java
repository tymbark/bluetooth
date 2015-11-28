package com.example.damianmichalak.bluetooth_test.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.damianmichalak.bluetooth_test.R;

public class ConsoleFragment extends Fragment implements Logger.LoggerListener {

    private MainActivity activity;
    private TextView console;
    private TextView clearConsole;
    private TextView customMessage;
    private ScrollView scroll;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.console_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
        console = (TextView) view.findViewById(R.id.console_output);
        clearConsole = (TextView) view.findViewById(R.id.console_clear);
        customMessage = (TextView) view.findViewById(R.id.console_custom_message);
        console.setMovementMethod(new ScrollingMovementMethod());
        scroll = (ScrollView) view.findViewById(R.id.console_scroll);

        clearConsole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.getInstance().clearLogs();
            }
        });

        customMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomMessageDialog dialog = new CustomMessageDialog(activity);
                dialog.show();
            }
        });

        Logger.getInstance().addListener(this);
    }

    public static Fragment newInstance() {
        return new ConsoleFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.getInstance().removeListener(this);
    }

    @Override
    public void write(final String message) {
        activity.runOnUiThread(new Runnable() {
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

}
