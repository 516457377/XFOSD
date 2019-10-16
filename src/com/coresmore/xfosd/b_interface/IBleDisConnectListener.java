package com.coresmore.xfosd.b_interface;

import com.coresmore.xfosd.libs.data.BleDevice;

import android.bluetooth.BluetoothGatt;

public interface IBleDisConnectListener {
    void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status);
}
