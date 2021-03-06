package com.example.damianmichalak.bluetooth_test.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.damianmichalak.bluetooth_test.view.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothConnector {

    private static final String SERVER_UUID = "94f39d29-7d6d-437d-973b-fba39e49d4ee";

    private BluetoothSocketWrapper socket;
    private BluetoothDevice device;
    private boolean secure;
    private BluetoothAdapter adapter;
    private List<UUID> uuidList;
    private int candidate;

    public BluetoothConnector(BluetoothDevice device, boolean secure, BluetoothAdapter adapter,
                              List<UUID> uuidList) {
        this.device = device;
        this.secure = secure;
        this.adapter = adapter;
        this.uuidList = uuidList;

        if (this.uuidList == null || this.uuidList.isEmpty()) {
            this.uuidList = new ArrayList<UUID>();
            this.uuidList.add(UUID.fromString(SERVER_UUID));
        }
    }

    public BluetoothSocketWrapper connect() throws IOException {
        boolean success = false;
        while (selectSocket()) {
            adapter.cancelDiscovery();

            try {
                socket.connect();
                success = true;
                break;
            } catch (IOException e) {
                //try the fallback
                try {
                    socket = new FallbackBluetoothSocket(socket.getUnderlyingSocket());
                    Thread.sleep(500);
                    socket.connect();
                    success = true;
                    break;
                } catch (FallbackException e1) {
                    Logger.getInstance().log("Could not initialize FallbackBluetoothSocket classes." + e.getMessage());
                } catch (InterruptedException e1) {
                    Logger.getInstance().log(e1.getMessage());
                } catch (IOException e1) {
                    Logger.getInstance().log("Fallback failed. Cancelling." + e1.getMessage());
                }
            }
        }

        if (!success) {
            throw new IOException("Could not connect to device: " + device.getAddress());
        }

        return socket;
    }

    private boolean selectSocket() throws IOException {
        if (candidate >= uuidList.size()) {
            return false;
        }

        BluetoothSocket tmp;
        UUID uuid = uuidList.get(candidate++);

        Logger.getInstance().log("Attempting to connect to Protocol: " + uuid);
        if (secure) {
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } else {
            tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
        }
        socket = new NativeBluetoothSocket(tmp);

        return true;
    }

    public static interface BluetoothSocketWrapper {

        InputStream getInputStream() throws IOException;

        OutputStream getOutputStream() throws IOException;

        String getRemoteDeviceName();

        void connect() throws IOException;

        String getRemoteDeviceAddress();

        void close() throws IOException;

        BluetoothSocket getUnderlyingSocket();

    }


    public static class NativeBluetoothSocket implements BluetoothSocketWrapper {

        private BluetoothSocket socket;

        public NativeBluetoothSocket(BluetoothSocket tmp) {
            this.socket = tmp;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return socket.getOutputStream();
        }

        @Override
        public String getRemoteDeviceName() {
            return socket.getRemoteDevice().getName();
        }

        @Override
        public void connect() throws IOException {
            socket.connect();
        }

        @Override
        public String getRemoteDeviceAddress() {
            return socket.getRemoteDevice().getAddress();
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }

        @Override
        public BluetoothSocket getUnderlyingSocket() {
            return socket;
        }

    }

    public class FallbackBluetoothSocket extends NativeBluetoothSocket {

        private BluetoothSocket fallbackSocket;

        public FallbackBluetoothSocket(BluetoothSocket tmp) throws FallbackException {
            super(tmp);
            try {
                Class<?> clazz = tmp.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                fallbackSocket = (BluetoothSocket) m.invoke(tmp.getRemoteDevice(), params);
            } catch (Exception e) {
                throw new FallbackException(e);
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return fallbackSocket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return fallbackSocket.getOutputStream();
        }


        @Override
        public void connect() throws IOException {
            fallbackSocket.connect();
        }


        @Override
        public void close() throws IOException {
            fallbackSocket.close();
        }

    }

    public static class FallbackException extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public FallbackException(Exception e) {
            super(e);
        }

    }
}
