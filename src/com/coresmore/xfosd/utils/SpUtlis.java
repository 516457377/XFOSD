package com.coresmore.xfosd.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SpUtlis {
    private static final String TAP = "SpUtlis";
    
    /**保存EQ值*/
    public static void setEqValue(Context context,int ... value){
        if (value.length != 7) {
            Log.d(TAP, "setEqValue value err");
            return;
        }
        SharedPreferences sp = context.getSharedPreferences("eq", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt("eq1", value[0]);
        editor.putInt("eq2", value[1]);
        editor.putInt("eq3", value[2]);
        editor.putInt("eq4", value[3]);
        editor.putInt("eq5", value[4]);
        editor.putInt("eq6", value[5]);
        editor.putInt("eq7", value[6]);
        editor.commit();
    }
    
    public static int[] getEqValue(Context context) {
        SharedPreferences sp = context.getSharedPreferences("eq", Context.MODE_PRIVATE);
        int eqs[] = new int[7];
        for (int i = 0; i < eqs.length; i++) {
            switch (i) {
            case 0:
                eqs[i] = sp.getInt("eq1", 12);
                break;
            case 1:
                eqs[i] = sp.getInt("eq2", 12);
                break;
            case 2:
                eqs[i] = sp.getInt("eq3", 12);
                break;
            case 3:
                eqs[i] = sp.getInt("eq4", 12);
                break;
            case 4:
                eqs[i] = sp.getInt("eq5", 12);
                break;
            case 5:
                eqs[i] = sp.getInt("eq6", 0);
                break;
            case 6:
                eqs[i] = sp.getInt("eq7", 0);
                break;

            default:
                break;
            }

        }
        return eqs;
    }
    
    public static void setPager1Input(Context context,int input){
        SharedPreferences sp = context.getSharedPreferences("eq", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt("input", input);
        editor.commit();
    }
    
    public static void setPager1EQ(Context context,int Eq){
        SharedPreferences sp = context.getSharedPreferences("eq", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt("veq", Eq);
        editor.commit();
    }
    
    public static void setPager1Volume(Context context,int volume){
        SharedPreferences sp = context.getSharedPreferences("eq", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt("volume", volume);
        editor.commit();
    }
    
    public static int getPager1Input(Context context){
        SharedPreferences sp = context.getSharedPreferences("eq", Context.MODE_PRIVATE);
        return sp.getInt("input", 0);
    }
    
    public static int getPager1Eq(Context context){
        SharedPreferences sp = context.getSharedPreferences("eq", Context.MODE_PRIVATE);
        return sp.getInt("veq", 0);
    }
    
    public static int getPager1Volume(Context context){
        SharedPreferences sp = context.getSharedPreferences("eq", Context.MODE_PRIVATE);
        return sp.getInt("volume", 0);
    }

}
