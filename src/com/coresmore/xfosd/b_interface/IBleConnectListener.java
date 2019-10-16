package com.coresmore.xfosd.b_interface;

import com.coresmore.xfosd.libs.data.BleDevice;
import com.coresmore.xfosd.libs.exception.BleException;

import android.bluetooth.BluetoothGatt;

public interface IBleConnectListener {
    void onStartConnect();
    void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status);
    void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status);
    void onConnectFail(BleDevice bleDevice, BleException exception);
}

