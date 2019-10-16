package com.coresmore.xfosd;

import com.coresmore.xfosd.libs.data.BleDevice;
import com.coresmore.xfosd.utils.DataModule;
import com.coresmore.xfosd.utils.SpUtlis;
import com.coresmore.xfosd.utils.TypeEnum;
import com.coresmore.xfosd.utils.Utlis;
import com.coresmore.xfosd.view.VerticalSeekBar;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_3 extends Fragment implements OnClickListener {
    private static final String TAP = "Fragment_3";
    private static final int Delay_Time = 300;
    
    private static final int MSG_PRO = 0;
    private static final int MSG_D1 = 1;
    private static final int MSG_D2 = 2;
    private static final int MSG_D3 = 3;
    private static final int MSG_D4 = 4;
    private static final int MSG_D5 = 5;
    private static final int MSG_D6 = 6;
    private static final int MSG_D7 = 7;
    private TypeEnum typeEnum;
    private boolean hasChange = false;
    
    private View rootView;
    private TextView tv_p3_1, tv_p3_2, tv_p3_3, tv_p3_4, tv_p3_5, tv_p3_6, tv_p3_7;
    private VerticalSeekBar seek_p3_1, seek_p3_2, seek_p3_3, seek_p3_4, seek_p3_5, seek_p3_6, seek_p3_7;
    private Button bt_retEQ;
    
    private int pro1, pro2, pro3, pro4, pro5, pro6, pro7, pro8, pro9;
    
    private MainPager mainActivity;
    private BleDevice mBleDevice;
    private DataModule dataModule;
    
    private boolean dontSend = false;
    
    public void setData(BleDevice mBleDevice, DataModule dataModule) {
        this.mBleDevice = mBleDevice;
        if (mBleDevice == null) {
            setEnAbled(false);
        }else{
            setEnAbled(true);
        }
        this.dataModule = dataModule;
    }
    
    public void getBleData() {
        if ((mainActivity.type3) > 0) {// 输入通道
            dontSend = true;
            pro1 = mainActivity.data3[0];
            pro2 = mainActivity.data3[1];
            pro3 = mainActivity.data3[2];
            pro4 = mainActivity.data3[3];
            pro5 = mainActivity.data3[4];
            pro6 = mainActivity.data3[5];
            pro7 = mainActivity.data3[6];
            pro8 = mainActivity.data3[7];
            pro9 = mainActivity.data3[8];
            resetEQ(pro1+12, pro2+12, pro3+12, pro4+12, pro5+12, pro6+12, pro7+12, pro8, pro9);
            mainActivity.type3 = 0;
        }
    }
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (null == mBleDevice) {
                toastOne(getString(R.string.str_main_disconnect));
                return;
            }
            switch (msg.what) {
            case MSG_PRO:
                if (!dontSend) {
                    dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,
                            (byte)pro1,(byte)pro2,(byte)pro3,(byte)pro4,
                            (byte)pro5,(byte)pro6,(byte)pro7,(byte)pro8,(byte)pro9}), mBleDevice);
                }
                dontSend = false;
//                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x02,(byte)pro2}), mBleDevice);
//                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x03,(byte)pro3}), mBleDevice);
//                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x04,(byte)pro4}), mBleDevice);
//                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x05,(byte)pro5}), mBleDevice);
//                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x06,(byte)pro6}), mBleDevice);
//                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x07,(byte)pro7}), mBleDevice);
//                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x08,(byte)pro8}), mBleDevice);
//                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x09,(byte)pro9}), mBleDevice);
                if (hasChange) {
                    ((MainPager)getActivity()).setTypeEnum(TypeEnum.EQ6);
                    SpUtlis.setEqValue(getActivity(), pro1,pro2,pro3,pro4,pro5,pro6,pro7);
                    SpUtlis.setPager1EQ(getActivity(), 5);
//                    mHandler.removeMessages(8);
//                    mHandler.sendEmptyMessageDelayed(8, 1000);
                }
                break;
            case MSG_D1:
                int v1 = msg.arg1;
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x01,(byte)v1}), mBleDevice);
                break;
            case MSG_D2:
                int v2 = msg.arg1;
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x02,(byte)v2}), mBleDevice);
                break;
            case MSG_D3:
                int v3 = msg.arg1;
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x03,(byte)v3}), mBleDevice);
                break;
            case MSG_D4:
                int v4 = msg.arg1;
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x04,(byte)v4}), mBleDevice);
                break;
            case MSG_D5:
                int v5 = msg.arg1;
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x05,(byte)v5}), mBleDevice);
                break;
            case MSG_D6:
                int v6 = msg.arg1;
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x06,(byte)v6}), mBleDevice);
                break;
            case MSG_D7:
                int v7 = msg.arg1;
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45,0x0A,0x07,(byte)v7}), mBleDevice);
                break;
            case 8:
                
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x07,(byte) 0x05}), mBleDevice);
                break;
            case 9:
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45, 0x01, 0x01 }), mBleDevice);
                break;

            default:
                break;
            }
        };
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment3, container, false);
        initView();
        initData();
//        getBleData();
//        rootView.setBackgroundColor(0xffddeecc);
        return rootView;
    }

    private void initView() {
        mainActivity = (MainPager) getActivity();
        tv_p3_1 = (TextView) rootView.findViewById(R.id.tv_p3_1);
        tv_p3_2 = (TextView) rootView.findViewById(R.id.tv_p3_2);
        tv_p3_3 = (TextView) rootView.findViewById(R.id.tv_p3_3);
        tv_p3_4 = (TextView) rootView.findViewById(R.id.tv_p3_4);
        tv_p3_5 = (TextView) rootView.findViewById(R.id.tv_p3_5);
        tv_p3_6 = (TextView) rootView.findViewById(R.id.tv_p3_6);
        tv_p3_7 = (TextView) rootView.findViewById(R.id.tv_p3_7);
        
        seek_p3_1 = (VerticalSeekBar) rootView.findViewById(R.id.seek_p3_1);
        seek_p3_2 = (VerticalSeekBar) rootView.findViewById(R.id.seek_p3_2);
        seek_p3_3 = (VerticalSeekBar) rootView.findViewById(R.id.seek_p3_3);
        seek_p3_4 = (VerticalSeekBar) rootView.findViewById(R.id.seek_p3_4);
        seek_p3_5 = (VerticalSeekBar) rootView.findViewById(R.id.seek_p3_5);
        seek_p3_6 = (VerticalSeekBar) rootView.findViewById(R.id.seek_p3_6);
        seek_p3_7 = (VerticalSeekBar) rootView.findViewById(R.id.seek_p3_7);
        
        bt_retEQ = (Button) rootView.findViewById(R.id.bt_retEQ);
        bt_retEQ.setOnClickListener(this);
    }

    /**
     * 
     */
    private void initData() {
        seek_p3_1.setOnSeekBarChangeListener(seekBarChangeListener);
        seek_p3_2.setOnSeekBarChangeListener(seekBarChangeListener);
        seek_p3_3.setOnSeekBarChangeListener(seekBarChangeListener);
        seek_p3_4.setOnSeekBarChangeListener(seekBarChangeListener);
        seek_p3_5.setOnSeekBarChangeListener(seekBarChangeListener);
        seek_p3_6.setOnSeekBarChangeListener(seekBarChangeListener);
        seek_p3_7.setOnSeekBarChangeListener(seekBarChangeListener);
        setEnAbled(false);
        onHiddenChanged(false);
    }
    
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            typeEnum = ((MainPager)getActivity()).getTypeEnum();
            switch (typeEnum) {
            case EQ1://原声
                resetEQ(12,12,12,12,12,12,12,0,0);
                break;
            case EQ2://摇滚
                resetEQ(20,17,16,15,16,15,14,-3,0);
                break;
            case EQ3://流行
                resetEQ(17,13,12,12,12,12,12,3,5);
                break;
            case EQ4://爵士
                resetEQ(18,16,15,16,17,15,14,1,2);
                break;
            case EQ5://古典
                resetEQ(21,4,4,5,6,7,8,-2,0);
                break;
            case EQ6://用户
               int eqs[] = SpUtlis.getEqValue(getActivity());
               resetEQ(eqs[0]+12,eqs[1]+12,eqs[2]+12,eqs[3]+12,eqs[4]+12,eqs[5]+12,eqs[6]+12,0,0);
                break;

            default:
                break;
            }
            hasChange = false;
            if (TypeEnum.EQ6 == typeEnum) {
                mHandler.sendEmptyMessageDelayed(9, 600);
            } else {
                getBleData();
            }
//            getBleData();
            setEnAbled(mBleDevice == null ? false : true);
        }
    }
    
    private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
        
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            
        }
        
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            
        }
        
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
            case R.id.seek_p3_1:
                tv_p3_1.setText(progress-12+"");
                pro1 = progress-12;
//                mHandler.removeMessages(MSG_D1);
//                Message msg1 = mHandler.obtainMessage();
//                msg1.what = MSG_D1;
//                msg1.arg1 = progress-12;
//                mHandler.sendMessageDelayed(msg1, Delay_Time);
                break;
            case R.id.seek_p3_2:
                tv_p3_2.setText(progress-12+"");
                pro2 = progress-12;
//                mHandler.removeMessages(MSG_D2);
//                Message msg2 = mHandler.obtainMessage();
//                msg2.what = MSG_D2;
//                msg2.arg1 = progress-12;
//                mHandler.sendMessageDelayed(msg2, Delay_Time);
                break;
            case R.id.seek_p3_3:
                tv_p3_3.setText(progress-12+"");
                pro3 = progress-12;
//                mHandler.removeMessages(MSG_D3);
//                Message msg3 = mHandler.obtainMessage();
//                msg3.what = MSG_D3;
//                msg3.arg1 = progress-12;
//                mHandler.sendMessageDelayed(msg3, Delay_Time);
                break;
            case R.id.seek_p3_4:
                tv_p3_4.setText(progress-12+"");
                pro4 = progress-12;
//                mHandler.removeMessages(MSG_D4);
//                Message msg4 = mHandler.obtainMessage();
//                msg4.what = MSG_D4;
//                msg4.arg1 = progress-12;
//                mHandler.sendMessageDelayed(msg4, Delay_Time);
                break;
            case R.id.seek_p3_5:
                tv_p3_5.setText(progress-12+"");
                pro5 = progress-12;
//                mHandler.removeMessages(MSG_D5);
//                Message msg5 = mHandler.obtainMessage();
//                msg5.what = MSG_D5;
//                msg5.arg1 = progress-12;
//                mHandler.sendMessageDelayed(msg5, Delay_Time);
                break;
            case R.id.seek_p3_6:
                tv_p3_6.setText(progress-12+"");
                pro6 = progress-12;
//                mHandler.removeMessages(MSG_D6);
//                Message msg6 = mHandler.obtainMessage();
//                msg6.what = MSG_D6;
//                msg6.arg1 = progress-12;
//                mHandler.sendMessageDelayed(msg6, Delay_Time);
                break;
            case R.id.seek_p3_7:
                tv_p3_7.setText(progress-12+"");
                pro7 = progress-12;
//                mHandler.removeMessages(MSG_D7);
//                Message msg7 = mHandler.obtainMessage();
//                msg7.what = MSG_D7;
//                msg7.arg1 = progress-12;
//                mHandler.sendMessageDelayed(msg7, Delay_Time);
                break;

            default:
                break;
            }
            
            mHandler.removeMessages(MSG_PRO);
            mHandler.sendEmptyMessageDelayed(MSG_PRO, Delay_Time);
            hasChange = true;
            
        }
    };
    
    public void resetEQ(int e1,int e2,int e3,int e4,int e5,int e6,int e7,int e8,int e9) {
        seek_p3_1.setProgress(e1);
        seek_p3_2.setProgress(e2);
        seek_p3_3.setProgress(e3);
        seek_p3_4.setProgress(e4);
        seek_p3_5.setProgress(e5);
        seek_p3_6.setProgress(e6);
        seek_p3_7.setProgress(e7);
        pro8 = e8;
        pro9 = e9;
    }
    
    public void setEnAbled(boolean boo){
        if (seek_p3_1 == null) {
            return;
        }
        seek_p3_1.setEnabled(boo);
        seek_p3_2.setEnabled(boo);
        seek_p3_3.setEnabled(boo);
        seek_p3_4.setEnabled(boo);
        seek_p3_5.setEnabled(boo);
        seek_p3_6.setEnabled(boo);
        seek_p3_7.setEnabled(boo);
    }
    
    Toast mToast = null;//将此作为全局变量

    private void toastOne(String msg) {
        if (mToast != null) {
            mToast.setText(msg);
        } else {
            mToast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bt_retEQ:
            if (null == mBleDevice) {
                toastOne(getString(R.string.str_main_disconnect));
                return;
            }
            resetEQ(12,12,12,12,12,12,12,0,0);
            hasChange = false;
            dontSend = true;
            ((MainPager)getActivity()).setTypeEnum(TypeEnum.EQ1);
            dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x07,(byte) 0x00}), mBleDevice);
            SpUtlis.setPager1EQ(getActivity(), 0);
            break;

        default:
            break;
        }
    }
}
