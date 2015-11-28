package com.example.damianmichalak.bluetooth_test.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger {

    private String output;
    private static Logger logger = null;
    private final List<LoggerListener> listeners = new ArrayList<>();

    public Logger() {
        output = "";
    }

    public static Logger getInstance() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public void log(String msg) {
        if (!output.isEmpty()) {
            output += "\n";
        }
        output += new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())) + " " + msg;
        for (LoggerListener listener : listeners) {
            if (listener != null) listener.write(output);
        }
    }

    public void destroy() {
        clearLogs();
        listeners.clear();
    }

    public void addListener(LoggerListener listener) {
        listeners.add(listener);
        listener.write(output);
    }

    public void removeListener(LoggerListener listener) {
        listeners.remove(listener);
    }

    public void clearLogs() {
        output = "";
        for (LoggerListener listener : listeners) {
            if (listener != null) listener.write(output);
        }
    }

    interface LoggerListener {

        void write(String output);
    }
}
