package com.coresmore.xfosd;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.coresmore.xfosd.adapter.MyAdapter;
import com.coresmore.xfosd.b_interface.IBleConnectListener;
import com.coresmore.xfosd.b_interface.IBleNotifyListener;
import com.coresmore.xfosd.b_interface.IBleWriteListener;
import com.coresmore.xfosd.libs.BleManager;
import com.coresmore.xfosd.libs.callback.BleScanCallback;
import com.coresmore.xfosd.libs.data.BleDevice;
import com.coresmore.xfosd.libs.data.BleScanState;
import com.coresmore.xfosd.libs.exception.BleException;
import com.coresmore.xfosd.libs.scan.BleScanRuleConfig;
import com.coresmore.xfosd.utils.BleSppGattAttributes;
import com.coresmore.xfosd.utils.DataModule;
import com.coresmore.xfosd.utils.SpUtlis;
import com.coresmore.xfosd.utils.TypeEnum;
import com.coresmore.xfosd.utils.Utlis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/** 重构代码 */
@SuppressLint({ "NewApi" })
public class MainPager extends Activity implements IBleWriteListener, IBleNotifyListener {
    protected static final int GPS_REQUEST_CODE = 0;
    protected static final int Factory_REQUEST_CODE = 1;
    private static final int MSG_DIS = 0;
    private static final int MSG_Success = 1;
    private static final int MSG_Fail = 2;
    private static final int MSG_Delay = 3;
    private static final int REQUEST_CODE_ACCESS = 1;
    private static final String TAP = "MainPager1";
    private String[] Mfest = { "android.permission.ACCESS_FINE_LOCATION" };

    protected int connnectNum;
    private DataModule dataModule;
    private Fragment_2 fragment2;
    private Fragment_3 fragment3;
    private Fragment_1 fragmentHome;
    private List<Fragment> fragments;
    private FrameLayout frame_connect;
    private boolean hasConnect = false;
    private LinearLayout lin_load;
    private ListView list_device;
    private BleDevice mBleDevice;
    private BleManager mBleManager;

    private List<BleDevice> mList = new ArrayList();
    protected String mMac;
    Toast mToast = null;
    private FragmentManager manager;
    private MyAdapter myAdapter;
    private TextView tipTextView;
    private FragmentTransaction transaction;
    private TextView tv_connect;
    private TextView tv_setting;
    private ProgressBar progress;
    private int nameSize = 3;
    /** 车型，车号 */
    private int carFactory, carNum;
    
    private String versions;

    private TypeEnum typeEnum = TypeEnum.EQ1;
    
    private boolean clickCombo = false;

    public TypeEnum getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(TypeEnum typeEnum) {
        this.typeEnum = typeEnum;

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_DIS:
                tv_connect.performClick();
                hasConnect = false;
                break;
            case MSG_Success:
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45, 0x01, 0x01 }), mBleDevice);
                break;
            case MSG_Fail:
                dataModule.bleNotifyStop(mBleDevice);
                mBleManager.disconnect(mBleDevice);
                mBleDevice = null;
                mBleManager.disconnectAllDevice();
                tv_connect.setText(R.string.str_main_disconnect);
                tv_connect.setTextColor(getResources().getColorStateList(R.drawable.main_text_selector));
                Drawable drawable = getResources().getDrawable(R.drawable.ic_main_disconnect);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_connect.setCompoundDrawables(null, drawable, null, null);
                break;
            case MSG_Delay:
                clickCombo = false;
                break;
            default:
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        requestWindowFeature(1);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0);
        }
        setContentView(R.layout.layout_pager);
        if (Build.VERSION.SDK_INT < 23) {
            initView();
        } else {
            ArrayList<String> list = new ArrayList<String>(Arrays.asList(Mfest));
            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {// 判断是否具有权限，有权限的剔除，无权限则申请
                String mis = iterator.next();
                if (checkCallingOrSelfPermission(mis) == PackageManager.PERMISSION_GRANTED) {
                    iterator.remove();
                }
            }

            if (list.size() > 0) {// 如果权限组大于0表示还有未通过的权限，则申请权限，否则正常执行
                requestPermissions(list.toArray(new String[list.size()]), REQUEST_CODE_ACCESS);
            } else {
                initView();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            openGPSSEtting();
        }
        if (requestCode == Factory_REQUEST_CODE) {
            // TODO
            carFactory = data.getIntExtra("carFactory", carFactory);
            carNum = data.getIntExtra("carNum", carNum);
            toastOne("选择车号:"+carFactory+"型号:"+carNum);
            
            if (null != mBleDevice) {
                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45, 0x0B, (byte) carFactory ,(byte) carNum}), mBleDevice);
            }
        }
    }

    private View.OnClickListener connectClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.tv_connect:
                if (!mBleManager.isSupportBle()) {// 不支持ble
                    toastOne(getString(R.string.str_fail));
                    return;
                }

                if (tv_connect.getText().equals(getString(R.string.str_main_connect))) {// 断开连接
                    
                    new AlertDialog.Builder(MainPager.this,android.R.style.Theme_DeviceDefault_Dialog_Alert).setTitle("断开连接").setMessage("确定断开连接么？")
                    .setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 关闭dialog
//                            dialogInterface.dismiss();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int is) {
                            frame_connect.setVisibility(View.VISIBLE);
                            lin_load.setVisibility(View.VISIBLE);
                            tipTextView.setText(R.string.str_wait);
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            disConnect();
                                        }
                                    });
                                }
                            }).start();
                        }
                    }).setCancelable(false).show();
                    
//                    dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45, 0x01, 0x00 }), mBleDevice);
                } else {// 打开蓝牙搜索
                    if (!checkGpsIsOpen()) {
                        openGPSSEtting();
                        return;
                    }

                    frame_connect.setVisibility(View.VISIBLE);
                    lin_load.setVisibility(View.VISIBLE);
                    tipTextView.setText(R.string.str_wait);

                    if (mBleManager.isBlueEnable()) {
                        scanBleDevice();
                        return;
                    }
                    // 打开蓝牙
                    if (BleManager.getInstance().enableBluetooth()) {
                        openBle();
                        return;
                    }
                    finish();
                    Toast.makeText(MainPager.this, R.string.str_blue_hit, 0).show();
                }
                break;

            case R.id.tv_setting:
                tv_setting.setVisibility(View.GONE);
                Intent intent = new Intent(MainPager.this, FactoryActivity.class);
                intent.putExtra("carFactory", carFactory);
                intent.putExtra("carNum", carNum);
                intent.putExtra("versions", versions);
                startActivityForResult(intent, MainPager.Factory_REQUEST_CODE);
                break;
            }
        }
    };
    
    /**断开连接*/
    private void disConnect(){
        for (int i = 0; i < fragments.size(); i++) {
            switch (i) {
            case 0:
                ((Fragment_1) fragments.get(i)).setData(null, dataModule);
                break;
            case 1:
                if ((fragments.get(i) instanceof Fragment_2)) {
                    ((Fragment_2) fragments.get(i)).setData(null, dataModule);
                } else
                    ((Fragment_3) fragments.get(i)).setData(null, dataModule);
                break;
            case 2:
                if ((fragments.get(i) instanceof Fragment_3)) {
                    ((Fragment_3) fragments.get(i)).setData(null, dataModule);
                } else {
                    ((Fragment_2) fragments.get(i)).setData(null, dataModule);
                }
                break;
            }
        }
        // mBleManager.disconnectAllDevice();//
        mHandler.removeMessages(MSG_Fail);
        mHandler.sendEmptyMessageDelayed(MSG_Fail, 200);
        toastOne(getString(R.string.str_disconnect));
        frame_connect.setVisibility(View.GONE);
    }

    private void openBle() {
        new Thread(new Runnable() {
            public void run() {
                while (!mBleManager.isBlueEnable()) {// 循环到蓝牙彻底打开为止
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        scanBleDevice();
                    }
                });
            }
        }).start();
    }

    private boolean checkGpsIsOpen() {
        return ((LocationManager) getSystemService("location")).isProviderEnabled("gps");
    }
    
    /**尝试配对*/
    private void TestBond(final String mac){
        lin_load.setVisibility(View.VISIBLE);
        list_device.setVisibility(View.GONE);
         
        new Thread(new Runnable() {
            public void run() {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
                if (device!=null) {
                    device.createBond();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        connectBleByMac(mac);
                    }
                });
            }
        }).start();
    }

    /** 连接 */
    private void connectBleByMac(String mac) {
        

//        Method method = BluetoothDevice.class.getMethod("createBond");
//        Log.e(getPackageName(), "开始配对");
//        method.invoke(listdevice.get(position));
//        BluetoothDevice device = new 
        
        
        mBleManager.connect(mac, new IBleConnectListener() {
            //连接失败
            public void onConnectFail(BleDevice paramAnonymousBleDevice, BleException paramAnonymousBleException) {
                connnectNum += 1;
                Log.d("#########!", paramAnonymousBleException.toString());
                if (connnectNum > 1) {
                    toastOne(getString(R.string.str_connect_fail));
                    //TODO
                    frame_connect.setVisibility(View.GONE);
                    BleManager.getInstance().disconnectAllDevice();
                    BleManager.getInstance().disableBluetooth();
//                    BluetoothAdapter.getDefaultAdapter().disable();
                    return;
                }
                connectBleByMac(mMac);
            }

            // 连接成功
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt paramAnonymousBluetoothGatt,
                    int paramAnonymousInt) {

                frame_connect.setVisibility(8);
                toastOne(getString(R.string.str_connect_ok));
                hasConnect = true;
                MainPager.this.mBleDevice = bleDevice;
                tv_connect.setTextColor(getResources().getColor(R.color.cl_text_white));
                tv_connect.setText(R.string.str_main_connect);

                Drawable drawable = getResources().getDrawable(R.drawable.ic_main_connect);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_connect.setCompoundDrawables(null, drawable, null, null);

                mHandler.removeMessages(MSG_Success);
                mHandler.sendEmptyMessageDelayed(MSG_Success, 300);
                dataModule.bleNotifyOpen(mBleDevice);

                for (int i = 0; i < fragments.size(); i++) {
                    switch (i) {
                    case 0:
                        ((Fragment_1) fragments.get(i)).setData(mBleDevice, dataModule);
                        break;
                    case 1:
                        if ((fragments.get(i) instanceof Fragment_2)) {
                            ((Fragment_2) fragments.get(i)).setData(mBleDevice, dataModule);
                        } else
                            ((Fragment_3) fragments.get(i)).setData(mBleDevice, dataModule);
                        break;
                    case 2:
                        if ((fragments.get(i) instanceof Fragment_3)) {
                            ((Fragment_3) fragments.get(i)).setData(mBleDevice, dataModule);
                        } else {
                            ((Fragment_2) fragments.get(i)).setData(mBleDevice, dataModule);
                        }
                        break;
                    }
                }
            }

            // 监听连接断开
            public void onDisConnected(boolean paramAnonymousBoolean, BleDevice paramAnonymousBleDevice,
                    BluetoothGatt paramAnonymousBluetoothGatt, int paramAnonymousInt) {
                if (mBleDevice == null) {
                    return;
                }
//                dataModule.bleWriteToString(Utlis.getCheckByte(new byte[] { 0x45, 0x01, 0x00 }), mBleDevice);
                for (int i = 0; i < fragments.size(); i++) {
                    switch (i) {
                    case 0:
                        ((Fragment_1) fragments.get(i)).setData(null, dataModule);
                        break;
                    case 1:
                        if ((fragments.get(i) instanceof Fragment_2)) {
                            ((Fragment_2) fragments.get(i)).setData(null, dataModule);
                        } else
                            ((Fragment_3) fragments.get(i)).setData(null, dataModule);
                        break;
                    case 2:
                        if ((fragments.get(i) instanceof Fragment_3)) {
                            ((Fragment_3) fragments.get(i)).setData(null, dataModule);
                        } else {
                            ((Fragment_2) fragments.get(i)).setData(null, dataModule);
                        }
                        break;
                    }
                }
                mHandler.removeMessages(MSG_Fail);
                mHandler.sendEmptyMessageDelayed(MSG_Fail, 200);
                // mBleManager.disconnect(mBleDevice);
                // mBleManager.disconnectAllDevice();
                // mBleDevice = null;
                // tv_connect.setText(R.string.str_main_disconnect);
                toastOne(getString(R.string.str_disconnect));
            }

            public void onStartConnect() {
                lin_load.setVisibility(0);
                list_device.setVisibility(8);
                mBleManager.cancelScan();
                tipTextView.setText(getString(R.string.str_connecting));
            }
        });
    }

    private void hideOthersFragment(Fragment paramFragment, boolean paramBoolean) {
        transaction = manager.beginTransaction();
        if (paramBoolean) {
            transaction.add(R.id.fragment, paramFragment);
        }
        Iterator localIterator = fragments.iterator();
        for (;;) {
            if (!localIterator.hasNext()) {
                transaction.commit();
                return;
            }
            Fragment localFragment = (Fragment) localIterator.next();
            if (paramFragment.equals(localFragment)) {
                transaction.show(localFragment);
            } else {
                transaction.hide(localFragment);
            }
        }
    }

    private void initView() {
        fragments = new ArrayList();
        manager = getFragmentManager();
        fragmentHome = new Fragment_1();
        fragments.add(fragmentHome);
        hideOthersFragment(fragmentHome, true);
        findViewById(R.id.btn_1).setSelected(true);
        tv_connect = ((TextView) findViewById(R.id.tv_connect));
        tv_setting = ((TextView) findViewById(R.id.tv_setting));

        frame_connect = ((FrameLayout) findViewById(R.id.frame_connect));
        tipTextView = ((TextView) findViewById(R.id.tipTextView));
        lin_load = ((LinearLayout) findViewById(R.id.lin_load));
        list_device = ((ListView) findViewById(R.id.list_device));
        progress = findViewById(R.id.progress);
        myAdapter = new MyAdapter(mList, this);
        list_device.setAdapter(myAdapter);
        list_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView,
                    int paramAnonymousInt, long paramAnonymousLong) {
                toastOne(((BleDevice) mList.get(paramAnonymousInt)).getName());
                mBleManager.cancelScan();
                connnectNum = 0;
                mMac = ((BleDevice) mList.get(paramAnonymousInt)).getMac();
//                connectBleByMac(mMac);
                TestBond(mMac);
            }
        });
        dataModule = new DataModule();
        dataModule.setWriteListener(this);
        dataModule.setNotifyListener(this);
        tv_connect.setOnClickListener(connectClickListener);
        tv_setting.setOnClickListener(connectClickListener);
        mBleManager = BleManager.getInstance();
        mBleManager.init(getApplication());
        mBleManager.enableLog(true).setReConnectCount(1, 3000L).setConnectOverTime(7000L).setOperateTimeout(5000);
    }

    private void openGPSSEtting() {
        if (checkGpsIsOpen()) {
            toastOne(getString(R.string.str_GPS_hint));
            return;
        } else {
            new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Dialog_Alert).setTitle(R.string.str_openGPS).setMessage(R.string.str_openMSG)
                    .setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 关闭dialog
                            dialogInterface.dismiss();
                        }
                    }).setPositiveButton(R.string.str_gosetting, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 跳转到手机原生设置页面
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, GPS_REQUEST_CODE);
                        }
                    }).setCancelable(false).show();
        }
    }
    
    /** 搜索设备 */
    private void scanBleDevice() {
        clickCombo = true;
        mHandler.removeMessages(MSG_Delay);
        mHandler.sendEmptyMessageDelayed(MSG_Delay, 5000);
        Utlis.refreshBleAppFromSystem(this, getPackageName());
        mList.clear();
        UUID.fromString(BleSppGattAttributes.BLE_SPP_Service);
        nameSize = 4;
        BleScanRuleConfig localBleScanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceName(true, new String[] { "BLE", "t" }).setAutoConnect(false).setScanTimeOut(5000L).build();
        mBleManager.initScanRule(localBleScanRuleConfig);
        if (BleScanState.STATE_SCANNING == mBleManager.getScanSate()) {
            mBleManager.cancelScan();
        }
        mBleManager.scan(new BleScanCallback() {
            public void onScanFinished(List<BleDevice> paramAnonymousList) {
                if (paramAnonymousList.size() < 1) {
                    toastOne(getString(R.string.str_scan_noDevice));
                    frame_connect.setVisibility(8);
                } else {
                    boolean has = false;
                    for (int i = 0; i < paramAnonymousList.size(); i++) {
                        if (paramAnonymousList.get(i).getName().length() <= nameSize) {
                            has = true;
                        }
                    }
                    if (!has) {
                        toastOne(getString(R.string.str_scan_noDevice));
                        frame_connect.setVisibility(8);
                    }}
                progress.setVisibility(View.GONE);
            }

            public void onScanStarted(boolean paramAnonymousBoolean) {
                if (!paramAnonymousBoolean) {
                    toastOne(getString(R.string.str_scanFail));
                    frame_connect.setVisibility(8);
                }
            }

            public void onScanning(BleDevice paramAnonymousBleDevice) {
                if (lin_load.getVisibility() == 0) {
                    lin_load.setVisibility(8);
                    list_device.setVisibility(0);
                    progress.setVisibility(View.VISIBLE);
                }
                if (paramAnonymousBleDevice.getName().length() <= nameSize) {
                    mList.add(paramAnonymousBleDevice);
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setBtnSetect(View paramView) {
        findViewById(R.id.btn_1).setSelected(false);
        findViewById(R.id.btn_2).setSelected(false);
        findViewById(R.id.btn_3).setSelected(false);
        findViewById(R.id.tv_setting).setVisibility(8);
        paramView.setSelected(true);
    }

    public void onBackPressed() {
        if (frame_connect.getVisibility() == 0) {
            if (!clickCombo) {
                frame_connect.setVisibility(8);
            }
            return;
        }
        super.onBackPressed();
    }

    public void onClick(View paramView) {
        setBtnSetect(paramView);
        switch (paramView.getId()) {
        case R.id.btn_1:
            hideOthersFragment(fragmentHome, false);
            break;
        case R.id.btn_2:
            if (fragment2 == null) {
                fragment2 = new Fragment_2();
                fragment2.setData(mBleDevice, dataModule);
                fragments.add(fragment2);
                hideOthersFragment(fragment2, true);
                return;
            }
            hideOthersFragment(fragment2, false);
            break;

        case R.id.btn_3:
            if (fragment3 == null) {
                fragment3 = new Fragment_3();
                fragment3.setData(mBleDevice, dataModule);
                fragments.add(fragment3);
                hideOthersFragment(fragment3, true);
                return;
            }
            hideOthersFragment(fragment3, false);
            break;
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_ACCESS) {
            boolean allOK = true;
            for (int i = 0; i < grantResults.length; i++) {// 判断返回权限组的返回请求结果
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    allOK = false;// 如果有一个不通过表示不可执行
                }
            }
            if (allOK) {
                initView();
            } else {// 拒绝位置信息
                Toast.makeText(this, getString(R.string.str_access_hint), Toast.LENGTH_LONG).show();
                finish();
                Utlis.gotoSetting(this);
            }
        }

    }

    protected void onDestroy() {
        if (mBleDevice != null) {
            mBleManager.disconnectAllDevice();
            mBleDevice = null;
        }
        dataModule.onDestroy();
        mBleManager.cancelScan();
        BleManager.getInstance().destroy();
        super.onDestroy();
    }

    private void toastOne(String str) {
        if (mToast != null) {
            mToast.setText(str);
        } else {
            mToast = Toast.makeText(MainPager.this, str, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void onWriteFailure(BleException paramBleException) {

        runOnUiThread(new Runnable() {
            public void run() {

                toastOne("数据写入失败");
                if (hasConnect) {
                    mHandler.removeMessages(MSG_DIS);
                    mHandler.sendEmptyMessageDelayed(MSG_DIS, 1000L);
                }

            }
        });
    }

    public void onWriteSuccess(int paramInt1, int paramInt2, final byte[] paramArrayOfByte) {
        // https://doupocangqiong1.com/127632/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(MainPager.this,"String"+paramArrayOfByte[1],Toast.LENGTH_SHORT).show();
                // TODO 写入成功
            }
        });
    }

    @Override
    public void onNotifySuccess() {

    }

    @Override
    public void onNotifyFailure(BleException exception) {

    }

    public int type1_1, type1_2, type1_3, type2_1, type2_2, type3;
    /**
     * @data1 01
     * @data2 0,1,1,1,0,0
     * @data3 1,2,3,4,5,6,7,8,9
     */
    public int data1_1[], data1_2[], data1_3[], data2_1[], data2_2[], data3[];

    @Override
    public void onCharacteristicChanged(byte[] data) {
        // 接收到数据
        // StringBuffer buffer = new StringBuffer();
        // for (byte b : data) {
        // buffer.append((byte)b+",");
        // }
        boolean lengthErr = false;
        boolean lengthErr2 = false;
        /*
         * 长度15 参数1：输入通道切换， 参数2：主音量设置， 参数3：音效模式， 参数4-9：相位设置， 参数10-15：延时设置，
         */
        if (data[0] == 0x58 && data[1] == 0x20) {
            // toastOne(data.length+"__");
//            Toast.makeText(MainPager.this, data[4]+"__", Toast.LENGTH_SHORT).show();
            if (Utlis.checkByte(data)) {
                try {
                    if (data.length != 18) {
                        toastOne("数据长度异常。");
                        return;
                    }
                    type1_1 = 1;
                    data1_1 = new int[] { data[2] };

                    type1_2 = 1;
                    data1_2 = new int[] { data[3] };

                    type1_3 = 1;
                    data1_3 = new int[] { data[4] };

                    type2_1 = 1;
                    data2_1 = new int[] { data[5], data[6], data[7], data[8], data[9], data[10] };

                    type2_2 = 1;
                    data2_2 = new int[] { data[11], data[12], data[13], data[14], data[15], data[16] };
                    if (null != fragmentHome) {
                        fragmentHome.getBleData();
                    }
                    if (null != fragment2) {
                        fragment2.getBleData();
                    }
                } catch (Exception e) {
                    toastOne("数据异常,请联系开发人员。");
                }
            } else {
                toastOne("数据校验失败。");
            }
            return;
        }

        /*
         * 长度13 参数1-9：GEQ调节， 参数10-11：车型选择， 参数12-13：版本号，
         */
        if (data[0] == 0x58 && data[1] == 0x21) {
//             toastOne(data.length+"__"+ data[2]+ "_"+data[3]+"_"+ 
//                     data[4]+"_"+ data[5]+"_"+data[6]+"_"+ data[7]+"_"+
//                     data[8]+ "_"+data[9]+"_"+data[10]);
//            toastOne(data[13]+"_"+data[14]);
             SpUtlis.setEqValue(this, data[2],data[3],data[4],data[5],data[6],data[7],data[8]);
            if (Utlis.checkByte(data)) {
                try {
                    if (data.length != 16) {
                        toastOne("数据长度异常。");
                        return;
                    }
                    if (data1_3[0] == 5) {
                        type3 = 1;
                        data3 = new int[] { data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9],
                                data[10] };
                    }
                    versions = data[13]+"."+data[14];
                    // TODO
                } catch (Exception e) {
                    toastOne("数据异常,请联系开发人员。");
                }
                if (null != fragment3) {
                    //不需要EQ页了。
//                    fragment3.getBleData();
//                    fragment3.onHiddenChanged(false);
                }else{
//                    fragment3 = new Fragment_3();
//                    fragment3.setData(mBleDevice, dataModule);
//                    fragments.add(fragment3);
//                    hideOthersFragment(fragment3, true);
//                    fragment3.getBleData();
                }
            } else {
                toastOne("数据校验失败。");
            }
            return;
        }

//        if (Utlis.checkByte(data)) {
//            try {
//                switch (data[1]) {
//                case 0x02:// 1
//                    if (data.length != 4) {
//                        lengthErr = true;
//                        break;
//                    }
//                    type1_1 = data[1];
//                    data1_1 = new int[] { data[2] };
//                    if (null != fragmentHome) {
//                        fragmentHome.getBleData();
//                    }
//                    break;
//                case 0x03:// 1
//                    if (data.length != 4) {
//                        lengthErr = true;
//                        break;
//                    }
//                    type1_2 = data[1];
//                    data1_2 = new int[] { data[2] };
//                    if (null != fragmentHome) {
//                        fragmentHome.getBleData();
//                    }
//                    break;
//                case 0x07:// 1
//                    if (data.length != 4) {
//                        lengthErr = true;
//                        break;
//                    }
//                    type1_3 = data[1];
//                    data1_3 = new int[] { data[2] };
//                    if (null != fragmentHome) {
//                        fragmentHome.getBleData();
//                    }
//                    break;
//                case 0x08:// 2
//                    if (data.length != 9) {
//                        lengthErr = true;
//                        break;
//                    }
//                    type2_1 = data[1];
//                    data2_1 = new int[] { data[2], data[3], data[4], data[5], data[6], data[7] };
//                    if (null != fragment2) {
//                        fragment2.getBleData();
//                    }
//                    break;
//                case 0x09:// 2
//                    if (data.length != 9) {
//                        lengthErr = true;
//                        break;
//                    }
//                    type2_2 = data[1];
//                    data2_2 = new int[] { data[2], data[3], data[4], data[5], data[6], data[7] };
//                    if (null != fragment2) {
//                        fragment2.getBleData();
//                    }
//                    break;
//                case 0x0A:// 3
//                    if (data.length != 12) {
//                        lengthErr = true;
//                        break;
//                    }
//                    type3 = data[1];
//                    data3 = new int[] { data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9],
//                            data[10] };
//                    if (null != fragment3) {
//                        fragment3.getBleData();
//                    }
//                    type3 = data[1];
//                    break;
//                default:
//                    toastOne("不存在此命令。");
//                    break;
//                }
//            } catch (Exception e) {
//                toastOne("数据异常,请联系开发人员。");
//            }
//            if (lengthErr) {
//                toastOne("数据长度异常。");
//            }
//
//        } else {
//            toastOne("数据校验失败。");
//        }
    }
}
