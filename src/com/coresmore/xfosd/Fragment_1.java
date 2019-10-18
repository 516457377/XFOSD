package com.coresmore.xfosd;

import com.coresmore.xfosd.libs.data.BleDevice;
import com.coresmore.xfosd.utils.DataModule;
import com.coresmore.xfosd.utils.SpUtlis;
import com.coresmore.xfosd.utils.TypeEnum;
import com.coresmore.xfosd.utils.Utlis;
import com.coresmore.xfosd.view.CustomView;
import com.coresmore.xfosd.view.CustomView.OnDataChange;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_1 extends Fragment {
    private static final String TAP = "Fragment_1";
    private static final int MSG_Sound_Delay = 0;
    private static final int MSG_Click = 1;
    /**主view*/
    private View rootView;
    /**控制轮*/
    private ImageView imgVolume;
    /**控制轮的触摸事件*/
    private View viewTouch;
    /**控制外的小圆圈*/
    private CustomView customView;
    /**大数字*/
    private TextView tv_qian_horn;
    
    /**以后需要加的单机8此控制轮弹出工厂菜单*/
    private TextView tv_setting;
    private int num;

    private float lastX, lastY;
    private PointF pointCenter, pointA, pointB;
    
    private float volumeValue;
    
    private TextView tv_home_1, tv_home_2, tv_home_3, tv_home_4, tv_home_5, tv_home_6;
    private TextView tv_1,tv_2,tv_3;
    private TypeEnum typeEnum;
    
    private MainPager mainActivity;
    
    private boolean donSend = false;
    
    private BleDevice mBleDevice;
    private DataModule dataModule;
    
    
    public void setData(BleDevice mBleDevice, DataModule dataModule) {
        this.mBleDevice = mBleDevice;
        this.dataModule = dataModule;
    }
    
    public void getBleData(){
        if ((mainActivity.type1_1) > 0) {//输入通道
            switch (mainActivity.data1_1[0]) {
            case 0:
                setBtnTopSetect(tv_1);
                break;
            case 1:
                setBtnTopSetect(tv_2);
                break;
            }
            mainActivity.type1_1 = 0;
        }
        if ((mainActivity.type1_2) > 0) {//音量设置
            customView.setValue((int) mainActivity.data1_2[0]);
            //22-338
            float an = (316f/40f*(float)mainActivity.data1_2[0])+22f;
            tv_qian_horn.setText((int) mainActivity.data1_2[0]+"");
            imgVolume.setRotation(an);
            mainActivity.type1_2 = 0;
            donSend = true;
        }
        if ((mainActivity.type1_3) > 0) {//音效模式
            switch (mainActivity.data1_3[0]) {
            case 0:
                setBtnSetect(tv_home_1);
                mainActivity.setTypeEnum(TypeEnum.EQ1);
                break;
            case 1:
                setBtnSetect(tv_home_2);
                mainActivity.setTypeEnum(TypeEnum.EQ2);
                break;
            case 2:
                setBtnSetect(tv_home_3);
                mainActivity.setTypeEnum(TypeEnum.EQ3);
                break;
            case 3:
                setBtnSetect(tv_home_4);
                mainActivity.setTypeEnum(TypeEnum.EQ4);
                break;
            case 4:
                setBtnSetect(tv_home_5);
                mainActivity.setTypeEnum(TypeEnum.EQ5);
                break;
            case 5:
                setBtnSetect(tv_home_6);
                mainActivity.setTypeEnum(TypeEnum.EQ6);
                break;
            }
            mainActivity.type1_3 = 0;
        }
        
    }
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_Sound_Delay:
                if (null == mBleDevice) {
                    toastOne(getString(R.string.str_main_disconnect));
                    return;
                }
                if (donSend) {
                    donSend = false;
                    return;
                }
                int v = msg.arg1;
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x03,(byte) v}), mBleDevice);
                SpUtlis.setPager1Volume(getActivity(), v);
                break;
            case MSG_Click:
                num = 0;
                break;
            default:
                break;
            }
        };
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment1, container, false);
//        rootView.setBackgroundColor(0xffeeccdd);
        initView();
        initData();
        getBleData();
        return rootView;
    }
    
    @Override
    public void onStop() {
        customView.onStop();
        super.onStop();
    }
    

    private void initView() {
        mainActivity = (MainPager) getActivity();
        imgVolume = (ImageView) rootView.findViewById(R.id.imageValume);
        viewTouch = rootView.findViewById(R.id.viewTouch);
        customView = (CustomView) rootView.findViewById(R.id.custorView);
        tv_qian_horn = (TextView) rootView.findViewById(R.id.tv_qian_horn);
        
        tv_home_1 = (TextView) rootView.findViewById(R.id.tv_home_1);
        tv_home_2 = (TextView) rootView.findViewById(R.id.tv_home_2);
        tv_home_3 = (TextView) rootView.findViewById(R.id.tv_home_3);
        tv_home_4 = (TextView) rootView.findViewById(R.id.tv_home_4);
        tv_home_5 = (TextView) rootView.findViewById(R.id.tv_home_5);
        tv_home_6 = (TextView) rootView.findViewById(R.id.tv_home_6);
        
        tv_1 = (TextView) rootView.findViewById(R.id.tv_1);
        tv_2 = (TextView) rootView.findViewById(R.id.tv_2);
        tv_3 = (TextView) rootView.findViewById(R.id.tv_3);
        
        tv_setting = (TextView) getActivity().findViewById(R.id.tv_setting);
    }
    
    private void initData() {
        imgVolume.setRotation(22);
        viewTouch.setOnTouchListener(onTouchListener);
        viewTouch.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                num++;
                if (num > 7) {
                    num = 0;
                    //TODO 
                    tv_setting.setVisibility(View.VISIBLE);
//                    toastOne("click");
                }
                mHandler.removeMessages(MSG_Click);
                mHandler.sendEmptyMessageDelayed(MSG_Click, 1500);
            }
        });
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        imgVolume.measure(w, h);
        customView.setWidth(imgVolume.getMeasuredWidth(), imgVolume.getMeasuredHeight());
        
        tv_home_1.setOnClickListener(tvClickListener);
        tv_home_2.setOnClickListener(tvClickListener);
        tv_home_3.setOnClickListener(tvClickListener);
        tv_home_4.setOnClickListener(tvClickListener);
        tv_home_5.setOnClickListener(tvClickListener);
        tv_home_6.setOnClickListener(tvClickListener);
        
        setBtnSetect(tv_home_1);
        
        tv_1.setOnClickListener(tvTopClickListener);
        tv_2.setOnClickListener(tvTopClickListener);
        tv_3.setOnClickListener(tvTopClickListener);
        setBtnTopSetect(tv_1);
        
        //初始化值
        int volume = SpUtlis.getPager1Volume(getActivity());
        customView.setValue(volume);
        //22-338
        float an = (316f/40f*volume)+22f;
        tv_qian_horn.setText(volume+"");
        imgVolume.setRotation(an);
//        if (volume != 0) {
//            donSend = true;
//        }
        int input = SpUtlis.getPager1Input(getActivity());
        switch (input) {
        case 0:
            setBtnTopSetect(tv_1);
            break;
        case 1:
            setBtnTopSetect(tv_2);
            break;
        }
        
        int eq = SpUtlis.getPager1Eq(getActivity());
        switch (eq) {
        case 0:
            setBtnSetect(tv_home_1);
            mainActivity.setTypeEnum(TypeEnum.EQ1);
            break;
        case 1:
            setBtnSetect(tv_home_2);
            mainActivity.setTypeEnum(TypeEnum.EQ2);
            break;
        case 2:
            setBtnSetect(tv_home_3);
            mainActivity.setTypeEnum(TypeEnum.EQ3);
            break;
        case 3:
            setBtnSetect(tv_home_4);
            mainActivity.setTypeEnum(TypeEnum.EQ4);
            break;
        case 4:
            setBtnSetect(tv_home_5);
            mainActivity.setTypeEnum(TypeEnum.EQ5);
            break;
        case 5:
            setBtnSetect(tv_home_6);
            mainActivity.setTypeEnum(TypeEnum.EQ6);
            break;
        }
        
        customView.addDataChange(new OnDataChange() {
            
            @Override
            public void dataChange(int value) {
//                toastOne(value+"");
//                String text = (int)(value/40f*100f)+"";
                tv_qian_horn.setText(value+"");
                mHandler.removeMessages(MSG_Sound_Delay);
                Message msg = mHandler.obtainMessage();
                msg.arg1 = value;
                mHandler.sendMessageDelayed(msg, 500);
            }
        });
        
    }
    
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            typeEnum = ((MainPager)getActivity()).getTypeEnum();
            getBleData();
            switch (typeEnum) {
            case EQ1:
                setBtnSetect(tv_home_1);
                break;
            case EQ2:
                setBtnSetect(tv_home_2);
                break;
            case EQ3:
                setBtnSetect(tv_home_3);
                break;
            case EQ4:
                setBtnSetect(tv_home_4);
                break;
            case EQ5:
                setBtnSetect(tv_home_5);
                break;
            case EQ6:
                setBtnSetect(tv_home_6);
                break;
            default:
                break;
            }
        }
        customView.onStop();
        super.onHiddenChanged(hidden);
    }
    

    private OnTouchListener onTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                pointCenter = new PointF(imgVolume.getWidth() / 2, imgVolume.getHeight() / 2);
                break;
            case MotionEvent.ACTION_MOVE:
                pointA = new PointF(lastX, lastY);
                pointB = new PointF(event.getX(), event.getY());

                float an = Utlis.angle(pointCenter, pointA, pointB) + imgVolume.getRotation();
                // 限制旋转的角度  
                an = an > 338 ? 338 : an;
                an = an < 22 ? 22 : an;
                //22-338
                imgVolume.setRotation(an);
                volumeValue = (an - 22f) / (338f - 22f) * 40;
                
                if ((int)volumeValue != (int)customView.getValue()) {//当值未变化适合不触发，避免发送重复数据
                    customView.setValue((int) volumeValue);
                }
                lastX = event.getX();
                lastY = event.getY();
                break;
            default:
                break;
            }

            return false;
        }
    };

    private OnClickListener tvClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            if (null == mBleDevice) {
                toastOne(getString(R.string.str_main_disconnect));
                return;
            }
            setBtnSetect(v);
            switch (v.getId()) {
            case R.id.tv_home_1:
                ((MainPager)getActivity()).setTypeEnum(TypeEnum.EQ1);
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x07,(byte) 0x00}), mBleDevice);
                SpUtlis.setPager1EQ(getActivity(), 0);
                break;
            case R.id.tv_home_2:
                ((MainPager)getActivity()).setTypeEnum(TypeEnum.EQ2);
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x07,(byte) 0x01}), mBleDevice);
                SpUtlis.setPager1EQ(getActivity(), 1);
                break;
            case R.id.tv_home_3:
                ((MainPager)getActivity()).setTypeEnum(TypeEnum.EQ3);
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x07,(byte) 0x02}), mBleDevice);
                SpUtlis.setPager1EQ(getActivity(), 2);
                break;
            case R.id.tv_home_4:
                ((MainPager)getActivity()).setTypeEnum(TypeEnum.EQ4);
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x07,(byte) 0x03}), mBleDevice);
                SpUtlis.setPager1EQ(getActivity(), 3);
                break;
            case R.id.tv_home_5:
                ((MainPager)getActivity()).setTypeEnum(TypeEnum.EQ5);
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x07,(byte) 0x04}), mBleDevice);
                SpUtlis.setPager1EQ(getActivity(), 4);
                break;
            case R.id.tv_home_6:
                ((MainPager)getActivity()).setTypeEnum(TypeEnum.EQ6);
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x07,(byte) 0x05}), mBleDevice);
                SpUtlis.setPager1EQ(getActivity(), 5);
                
//                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45, 0x01, 0x01 }), mBleDevice);
                break;
            default:
                break;
            }
        }
    };
    
    private OnClickListener tvTopClickListener = new OnClickListener() {
      public void onClick(View v) {
          if (null == mBleDevice) {
              toastOne(getString(R.string.str_main_disconnect));
              return;
          }
          setBtnTopSetect(v);
          switch (v.getId()) {
        case R.id.tv_1:
            dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x02,(byte) 0x00}), mBleDevice);
            SpUtlis.setPager1Input(getActivity(), 0);
            break;
        case R.id.tv_2:
            dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x02,(byte) 0x01}), mBleDevice);
            SpUtlis.setPager1Input(getActivity(), 1);
            break;
        case R.id.tv_3:
            dataModule.bleWriteToString(Utlis.getCheckByte(new byte[]{0x45,0x02,(byte) 0x02}), mBleDevice);
            SpUtlis.setPager1Input(getActivity(), 2);
            break;
        }
      };  
    };
    
    private void setBtnTopSetect(View v) {
        tv_1.setSelected(false);
        tv_2.setSelected(false);
        tv_3.setSelected(false);
        v.setSelected(true);
    }
    
    private void setBtnSetect(View v) {
        tv_home_1.setSelected(false);
        tv_home_2.setSelected(false);
        tv_home_3.setSelected(false);
        tv_home_4.setSelected(false);
        tv_home_5.setSelected(false);
        tv_home_6.setSelected(false);
        v.setSelected(true);
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

}
