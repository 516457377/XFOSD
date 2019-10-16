package com.coresmore.xfosd.libs.callback;


import com.coresmore.xfosd.libs.exception.BleException;

public abstract class BleReadCallback extends BleBaseCallback {

    public abstract void onReadSuccess(byte[] data);

    public abstract void onReadFailure(BleException exception);

}
