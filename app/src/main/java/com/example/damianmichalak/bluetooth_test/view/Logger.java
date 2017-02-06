package com.example.damianmichalak.bluetooth_test.view;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger {

    private List<String> data;
    private static Logger logger = null;
    private final List<LoggerListener> listeners = new ArrayList<>();

    public Logger() {
        data = new ArrayList<>();
    }

    public static Logger getInstance() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public void log(String msg) {
        Log.d("LOGGER", "msg");
        data.add(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())) + " " + msg);
        for (LoggerListener listener : listeners) {
            if (listener != null) listener.newData(data);
        }
    }

    public void destroy() {
        clearLogs();
        listeners.clear();
    }

    public void addListener(LoggerListener listener) {
        listeners.add(listener);
        listener.newData(data);
    }

    public void removeListener(LoggerListener listener) {
        listeners.remove(listener);
    }

    public void clearLogs() {
        data.clear();
        for (LoggerListener listener : listeners) {
            if (listener != null) listener.newData(data);
        }
    }

    interface LoggerListener {

        void newData(List<String> data);
    }
}
