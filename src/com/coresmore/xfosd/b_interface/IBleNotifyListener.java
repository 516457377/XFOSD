package com.coresmore.xfosd.b_interface;

import com.coresmore.xfosd.libs.exception.BleException;

public interface IBleNotifyListener {
    /**打开通知操作成功*/
    void onNotifySuccess();
    /**打开通知操作失败*/
    void onNotifyFailure(BleException exception);
    /**打开通知后，设备发过来的数据将在这里出现*/
    void onCharacteristicChanged(byte[] data);
}
