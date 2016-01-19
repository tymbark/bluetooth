package com.example.damianmichalak.bluetooth_test.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.damianmichalak.bluetooth_test.R;

import java.util.ArrayList;
import java.util.List;

public class ConsoleFragment extends Fragment implements Logger.LoggerListener {

    private MainActivity activity;
    private TextView clearConsole;
    private TextView customMessage;
    private List<String> data = new ArrayList<>();
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.console_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
        clearConsole = (TextView) view.findViewById(R.id.console_clear);
        customMessage = (TextView) view.findViewById(R.id.console_custom_message);
        listView = (ListView) view.findViewById(R.id.console_list_view);

        adapter = new ArrayAdapter<>(activity, R.layout.console_element, R.id.console_output, data);
        listView.setAdapter(adapter);


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
    public void newData(final List<String> messages) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listView == null) return;
                if (adapter == null) return;
                data.clear();
                data.addAll(messages);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);

                listView.post(new Runnable() {
                    public void run() {
                        listView.smoothScrollToPosition(data.size() - 1);
                    }
                });
            }
        });
    }

}
