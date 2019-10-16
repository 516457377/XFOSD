package com.coresmore.xfosd.libs.callback;


import com.coresmore.xfosd.libs.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback{

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);

}