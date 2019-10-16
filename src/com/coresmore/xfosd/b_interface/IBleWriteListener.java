package com.coresmore.xfosd.b_interface;

import com.coresmore.xfosd.libs.exception.BleException;

public interface IBleWriteListener {
    
   public void onWriteSuccess(int current, int total, byte[] justWrite);
   public void onWriteFailure(BleException exception);
}
