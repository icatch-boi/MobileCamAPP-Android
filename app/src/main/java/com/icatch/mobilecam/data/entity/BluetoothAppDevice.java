package com.icatch.mobilecam.data.entity;

public class BluetoothAppDevice {
    private String bluetoothName;
    private String bluetoothAddr;
    private boolean bluetoothConnect = false;
    private boolean bluetoothExist = false;

    public BluetoothAppDevice(String name, String addr,boolean bluetoothConnect) {
        this.bluetoothName = name;
        this.bluetoothAddr = addr;
        this.bluetoothConnect = bluetoothConnect;
    }
    public BluetoothAppDevice(String name, String addr,boolean bluetoothConnect,boolean bluetoothExist) {
        this.bluetoothName = name;
        this.bluetoothAddr = addr;
        this.bluetoothConnect = bluetoothConnect;
        this.bluetoothExist = bluetoothExist;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public String getBluetoothAddr() {
        return bluetoothAddr;
    }

    public void setBluetoothAddr(String bluetoothAddr) {
        this.bluetoothAddr = bluetoothAddr;
    }

    public boolean getBluetoothConnect() {
        return bluetoothConnect;
    }

    public void setBluetoothConnect(boolean bluetoothConnect) {
        this.bluetoothConnect = bluetoothConnect;
    }

    public boolean getBluetoothExist() {
        return bluetoothExist;
    }

    public void setBluetoothExist(boolean bluetoothExist) {
        this.bluetoothExist = bluetoothExist;
    }
}
