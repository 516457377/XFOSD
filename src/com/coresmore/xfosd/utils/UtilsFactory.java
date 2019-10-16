package com.coresmore.xfosd.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.coresmore.xfosd.R;

import android.content.Context;

public class UtilsFactory {

    public static List<Integer> getDate(Context context, int carFactory, int catMun, String name) {
        int arrayid = 0;
        switch (carFactory) {
        case 0:// 阿尔法
            switch (catMun) {
            case 0:arrayid = R.array.item0_1;break;
            case 1:arrayid = R.array.item0_2;break;
            default:break;
            }
            break;

        case 1:
            switch (catMun) {
            case 0:arrayid = R.array.item1_1;break;
            case 1:arrayid = R.array.item1_2;break;
            default:break;
            }
            break;
        case 2:
            
            break;
        case 3:
            break;
        case 4:
            break;
        case 5:
            break;
        case 6:
            break;
        case 7:
            break;
        case 8:
            break;
        case 9:
            break;
        case 10:
            break;
        case 12:
            break;
        case 13:
            break;
        case 14:
            break;
        case 15:
            break;
        case 16:
            break;
        case 17:
            break;
        case 18:
            break;
        case 19:
            break;
        case 20:
            break;
        default:
            break;
        }

        if (arrayid == 0) {
            return null;
        }
        int[] is = context.getResources().getIntArray(arrayid);
        List<Integer> baseDate = new ArrayList<>();
        for (int i : is) {
            baseDate.add(i); 
        }
        return baseDate;

    }

}
