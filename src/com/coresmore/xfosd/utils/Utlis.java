package com.coresmore.xfosd.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class Utlis {
    private static final String TAP = "XFUtlis";
    
    
    
    /**获取版本号名称（对应versionName*/
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
    
    /**
     * 根据坐标系中的3点确定夹角的方法（注意：夹角是有正负的）
     * 
     * @param cen
     *            旋转的中心点
     * @param first
     *            起始点
     * @param second
     *            结束点
     * @return 返回旋转的角度具有方向
     */
    public static float angle(PointF cen, PointF first, PointF second) {
        float dx1, dx2, dy1, dy2;

        dx1 = first.x - cen.x;
        dy1 = first.y - cen.y;
        dx2 = second.x - cen.x;
        dy2 = second.y - cen.y;

        // 计算三边的平方
        float ab2 = (second.x - first.x) * (second.x - first.x) + (second.y - first.y) * (second.y - first.y);
        float oa2 = dx1 * dx1 + dy1 * dy1;
        float ob2 = dx2 * dx2 + dy2 * dy2;

        // 根据两向量的叉乘来判断顺逆时针
        boolean isClockwise = ((first.x - cen.x) * (second.y - cen.y) - (first.y - cen.y) * (second.x - cen.x)) > 0;

        // 根据余弦定理计算旋转角的余弦值
        double cosDegree = (oa2 + ob2 - ab2) / (2 * Math.sqrt(oa2) * Math.sqrt(ob2));

        // 异常处理，因为算出来会有误差绝对值可能会超过一，所以需要处理一下
        if (cosDegree > 1) {
            cosDegree = 1;
        } else if (cosDegree < -1) {
            cosDegree = -1;
        }
        // 计算弧度
        double radian = Math.acos(cosDegree);
        // 计算旋转过的角度，顺时针为正，逆时针为负
        return (float) (isClockwise ? Math.toDegrees(radian) : -Math.toDegrees(radian));

    }
    
    /** byte转String */
    public static String byteToString(byte[] bt) {
        String string = null;
        try {
            string = new String(bt, "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }
    
    /**将byte数组拼接*/
    public static <T> byte[] concat(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    
    /** 
     * String的字符串转换成unicode的String 
     * @param String strText 全角字符串 
     * @return String 每个unicode之间无分隔符 
     * @throws Exception 
     */  
    public static String strToUnicode(String strText) throws Exception {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)
                str.append("\\u" + strHex);
            else // 低位在前面补00
                str.append("\\u00" + strHex);
        }
        return str.toString();
    }
    /**
     * 十进制转换16进制
     * @param d 十进制数
     * */
    public static String intToHex(int d) {
        //StringBuffer s = new StringBuffer();
        StringBuilder sb = new StringBuilder(8);
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(d != 0){
            sb = sb.append(b[d%16]);
            d = d/16;            
        }
        a = sb.reverse().toString();
        return a;
    }
    
    /**  
     * 字符串转换成十六进制字符串 
     * @param String str 待转换的ASCII字符串 
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B] 
     */    
    public static String str2HexStr(String str)  
    {    
  
        char[] chars = "0123456789ABCDEF".toCharArray();    
        StringBuilder sb = new StringBuilder("");  
        byte[] bs = str.getBytes();    
        int bit;    
          
        for (int i = 0; i < bs.length; i++)  
        {    
            bit = (bs[i] & 0x0f0) >> 4;    
            sb.append(chars[bit]);    
            bit = bs[i] & 0x0f;    
            sb.append(chars[bit]);  
            sb.append(' ');  
        }    
        return sb.toString().trim();    
    }  
    
    
    /**将16进制转换byte数组*/
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    
    /**
     * 跳转到当前应用对应的设置页面
     * @param context
     */
    public static void gotoSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
    
    /**
     * 验证数组
     * */
    public static boolean checkByte(byte bs[]) {
        if (bs[0] != 0x58) {
            return false;
        }
        
        byte bsAll = 0;
        for (int i = 1; i < bs.length - 1; i++) {
            bsAll += bs[i];
        }
        Log.d(TAP, bsAll + "="+bs[bs.length - 1]);
        if (bsAll == bs[bs.length - 1]) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 增加验证位
     * */
    public static byte[] getCheckByte(byte[] bs) {
        byte bTemp[] = new byte[bs.length + 1];
        for (int i = 0; i < bs.length; i++) {
            bTemp[i] = bs[i];
        }
        
        byte byAll = 0;
        for (int i = 1; i < bs.length; i++) {
            byAll+=bs[i];
            Log.d(TAP, bs[i]+"");
        }
        
        bTemp[bTemp.length-1] = byAll;
        return bTemp;
    }
//    public static void String();
    
    
    /**部分手机可能扫描不到ble问题*/
    public static void refreshBleAppFromSystem(Context context, String packageName) {
        // 6.0以上才有该功能,不是6.0以上就算了
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return;
        }
        if (!adapter.isEnabled()) {
            return;
        }
        try {
            Object mIBluetoothManager = getIBluetoothManager(adapter);
            Method isBleAppPresentM = mIBluetoothManager.getClass().getDeclaredMethod("isBleAppPresent");
            isBleAppPresentM.setAccessible(true);
            boolean isBleAppPresent = (Boolean) isBleAppPresentM.invoke(mIBluetoothManager);
            if (isBleAppPresent) {
                return;
            }
            Field mIBinder = BluetoothAdapter.class.getDeclaredField("mToken");
            mIBinder.setAccessible(true);
            Object mToken = mIBinder.get(adapter);

            // 刷新偶尔系统无故把app视为非 BLE应用 的错误标识 导致无法扫描设备
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 8.0+ (部分手机是7.1.2 也是如此)
                Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount",
                        IBinder.class, boolean.class, String.class);
                updateBleAppCount.setAccessible(true);
                // 关一下 再开
                updateBleAppCount.invoke(mIBluetoothManager, mToken, false, packageName);
                updateBleAppCount.invoke(mIBluetoothManager, mToken, true, packageName);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                try {
                    // 6.0~7.1.1

                    Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount",
                            IBinder.class, boolean.class);
                    updateBleAppCount.setAccessible(true);
                    // 关一下 再开
                    updateBleAppCount.invoke(mIBluetoothManager, mToken, false);
                    updateBleAppCount.invoke(mIBluetoothManager, mToken, true);
                } catch (NoSuchMethodException e) {
                    // 8.0+ (部分手机是7.1.2 也是如此)
                    try {
                        Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount",
                                IBinder.class, boolean.class, String.class);
                        updateBleAppCount.setAccessible(true);
                        // 关一下 再开
                        updateBleAppCount.invoke(mIBluetoothManager, mToken, false, packageName);
                        updateBleAppCount.invoke(mIBluetoothManager, mToken, true, packageName);
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    /**尝试彻底断开ble连接*/
    public static void setLeServiceEnable(boolean isEnable) {
        Object mIBluetooth;
        try {
            Field sService = BluetoothDevice.class.getDeclaredField("sService");
            sService.setAccessible(true);
            mIBluetooth = sService.get(null);
        } catch (Exception e) {
            return;
        }
        if (mIBluetooth == null) return;

        try {
            if (isEnable) {
                Method onLeServiceUp = mIBluetooth.getClass().getDeclaredMethod("onLeServiceUp");
                onLeServiceUp.setAccessible(true);
                onLeServiceUp.invoke(mIBluetooth);
            } else {
                Method onLeServiceUp = mIBluetooth.getClass().getDeclaredMethod("onBrEdrDown");
                onLeServiceUp.setAccessible(true);
                onLeServiceUp.invoke(mIBluetooth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Object getIBluetoothGatt(Object mIBluetoothManager) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getBluetoothGatt = getDeclaredMethod(mIBluetoothManager, "getBluetoothGatt");
        return getBluetoothGatt.invoke(mIBluetoothManager);
    }

    
    public static Object getIBluetoothManager(BluetoothAdapter adapter) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getBluetoothManager = getDeclaredMethod(BluetoothAdapter.class, "getBluetoothManager");
        return getBluetoothManager.invoke(adapter);
    }
    
    public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method declaredMethod = clazz.getDeclaredMethod(name, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }


    public static Field getDeclaredField(Object obj, String name) throws NoSuchFieldException {
        Field declaredField = obj.getClass().getDeclaredField(name);
        declaredField.setAccessible(true);
        return declaredField;
    }


    public static Method getDeclaredMethod(Object obj, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method declaredMethod = obj.getClass().getDeclaredMethod(name, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }

}
