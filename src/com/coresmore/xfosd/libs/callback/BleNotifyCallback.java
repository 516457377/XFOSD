package com.coresmore.xfosd.libs.callback;


import com.coresmore.xfosd.libs.exception.BleException;

public abstract class BleNotifyCallback extends BleBaseCallback {

    public abstract void onNotifySuccess();

    public abstract void onNotifyFailure(BleException exception);

    public abstract void onCharacteristicChanged(byte[] data);

}
