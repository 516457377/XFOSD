package com.coresmore.xfosd.utils;

import com.coresmore.xfosd.b_interface.IBleNotifyListener;
import com.coresmore.xfosd.b_interface.IBleWriteListener;
import com.coresmore.xfosd.libs.BleManager;
import com.coresmore.xfosd.libs.callback.BleNotifyCallback;
import com.coresmore.xfosd.libs.data.BleDevice;
import com.coresmore.xfosd.libs.exception.BleException;
import com.coresmore.xfosd.server.MyQueue;

import android.os.Handler;

/** 收发数据模块 */
public class DataModule {

    private IBleWriteListener bleWriteListener;

    private IBleNotifyListener bleNotifyListener;
    private static final int MSG = 0;
    
    private boolean hasSend = true;
    private byte[] mData = null;
    
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG) {
                if (bleNotifyListener != null) {
                    bleNotifyListener.onCharacteristicChanged(mData);
                    hasSend = true;
                }
            }
        };
    };
    
    /**设置发送消息监听*/
    public void setWriteListener(IBleWriteListener bleWriteListener) {
        this.bleWriteListener = bleWriteListener;
    }
    /**设置接收消息监听*/
    public void setNotifyListener(IBleNotifyListener bleNotifyListener) {
        this.bleNotifyListener = bleNotifyListener;
    }

    MyQueue myQueue;
    
    /**写数据
     * @param data  new byte[] { 121, -121, 124 };*/
    public void bleWriteToString(byte[] data, BleDevice bleDevice){
        
        if (null == myQueue || !myQueue.isAlive()) {
            myQueue = new MyQueue(bleDevice, bleWriteListener);
        }
        
        myQueue.add(data, bleDevice);
        if (!myQueue.isAlive()) {
            myQueue.start();
        }
        
//        BleManager.getInstance().write(bleDevice, BleSppGattAttributes.BLE_SPP_Service,
//                BleSppGattAttributes.BLE_SPP_Write_Characteristic, data, new BleWriteCallback() {
//
//                    @Override
//                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                        if (bleWriteListener != null) {
//                            bleWriteListener.onWriteSuccess(current, total, justWrite);
//                        }
//                    }
//
//                    @Override
//                    public void onWriteFailure(BleException exception) {
//                        if (bleWriteListener != null) {
//                            bleWriteListener.onWriteFailure(exception);
//                        }
//                    }
//                });
    }
    
    /** 打开消息监听 */
    public void bleNotifyOpen(BleDevice bleDevice) {
        BleManager.getInstance().notify(bleDevice, BleSppGattAttributes.BLE_SPP_Service,
                BleSppGattAttributes.BLE_SPP_Notify_Characteristic, new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {
                        // 打开通知操作成功
                        if (bleNotifyListener != null) {
                            bleNotifyListener.onNotifySuccess();
                        }
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        // 打开通知操作失败
                        if (bleNotifyListener != null) {
                            bleNotifyListener.onNotifyFailure(exception);
                        }
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现
//                        handler.removeMessages(MSG);
//                        if (hasSend) {
//                            mData = data;
//                            hasSend = false;
//                        }else{
//                            mData = Utlis.concat(mData, data);
//                        }
//                        handler.sendEmptyMessageDelayed(MSG, 200);
                        
                        if (bleNotifyListener != null) {
                            bleNotifyListener.onCharacteristicChanged(data);
                        }
                    }
                });
    }
    
    /** 关闭消息监听 */
    public void bleNotifyStop(BleDevice bleDevice) {
        BleManager.getInstance().stopNotify(bleDevice, BleSppGattAttributes.BLE_SPP_Service,
                BleSppGattAttributes.BLE_SPP_Notify_Characteristic);
    }
    
    public void onDestroy() {
        MyQueue.interrupted();
    }
}
