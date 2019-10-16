package com.coresmore.xfosd.libs.callback;


import java.util.List;

import com.coresmore.xfosd.libs.data.BleDevice;

public abstract class BleScanCallback implements BleScanPresenterImp {

    public abstract void onScanFinished(List<BleDevice> scanResultList);

    public void onLeScan(BleDevice bleDevice) {
    }
}
