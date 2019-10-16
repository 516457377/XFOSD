package com.coresmore.xfosd.libs.callback;

import com.coresmore.xfosd.libs.data.BleDevice;

public interface BleScanPresenterImp {

    void onScanStarted(boolean success);

    void onScanning(BleDevice bleDevice);

}
