package com.example.damianmichalak.bluetooth_test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static String output;
    private static Logger logger = null;
    private static LoggerListener listener;

    public Logger() {
        output =  "";
    }

    public static Logger getInstance() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public static void setListener(LoggerListener loggerListener) {
        if (logger == null) {
            logger = new Logger();
        }

        listener = loggerListener;
    }

    public static void log(String msg) {
        if (logger == null) {
            logger = new Logger();
        }

        if (!output.isEmpty()) {
            output += "\n";

        }
        output += new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())) + " " + msg;
        if (listener != null) listener.write(output);
    }

    public static void clear() {
        output = "";
    }

    public static void destroy() {
        clear();
        listener = null;
    }

    interface LoggerListener {

        void write(String output);
    }
}
