package com.coresmore.xfosd.server;

import java.util.ArrayList;
import java.util.List;

import com.coresmore.xfosd.b_interface.IBleWriteListener;
import com.coresmore.xfosd.libs.BleManager;
import com.coresmore.xfosd.libs.callback.BleWriteCallback;
import com.coresmore.xfosd.libs.data.BleDevice;
import com.coresmore.xfosd.libs.exception.BleException;
import com.coresmore.xfosd.utils.BleSppGattAttributes;

public class MyQueue extends Thread{
    
    private List<byte[]> mList = new ArrayList<byte[]>();
    
    private BleDevice bleDevice;
    
    private IBleWriteListener bleWriteListener;
    
    public MyQueue(BleDevice bleDevice, IBleWriteListener bleWriteListener) {
        super();
        this.bleDevice = bleDevice;
        this.bleWriteListener = bleWriteListener;
    }

    public void add(byte[] bs, BleDevice bleDevice){
        this.bleDevice = bleDevice;
        mList.add(bs);
    }
    
    @Override
    public synchronized void start() {
        
        super.start();
    }
    
    @Override
    public void run() {
//        for (Iterator iterator = mList.iterator(); iterator.hasNext();) {
//            byte[] data = (byte[]) iterator.next();
//
//            BleManager.getInstance().write(bleDevice, BleSppGattAttributes.BLE_SPP_Service,
//                    BleSppGattAttributes.BLE_SPP_Write_Characteristic, data, new BleWriteCallback() {
//
//                        @Override
//                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                            if (bleWriteListener != null) {
//                                bleWriteListener.onWriteSuccess(current, total, justWrite);
//                            }
//                        }
//
//                        @Override
//                        public void onWriteFailure(BleException exception) {
//                            if (bleWriteListener != null) {
//                                bleWriteListener.onWriteFailure(exception);
//                            }
//                        }
//                    });
//        }
        
        while(!isInterrupted()){
            if (mList.size() > 0) {
                byte[] data = mList.get(0);
                
                BleManager.getInstance().write(bleDevice, BleSppGattAttributes.BLE_SPP_Service,
                        BleSppGattAttributes.BLE_SPP_Write_Characteristic, data, new BleWriteCallback() {

                            @Override
                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                if (bleWriteListener != null) {
                                    bleWriteListener.onWriteSuccess(current, total, justWrite);
                                }
                            }

                            @Override
                            public void onWriteFailure(BleException exception) {
                                if (bleWriteListener != null) {
                                    bleWriteListener.onWriteFailure(exception);
                                }
                            }
                        });
                
                mList.remove(0);
            }
            
            
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
        }

            

    }
    
    

}
