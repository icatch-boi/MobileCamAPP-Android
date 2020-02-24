package com.icatch.mobilecam.DataConvert;

import android.annotation.SuppressLint;
import android.util.SparseArray;

/**
 * Created by zhang yanhu C001012 on 2015/11/18 13:46.
 */
public class BurstConvert {
    @SuppressLint("UseSparseArrays")
    //private HashMap<Integer, Integer> burstMap = new HashMap<Integer, Integer>();
    private  SparseArray<Integer> burstMap = new SparseArray<Integer>();

    private static BurstConvert burstConvert;

    public static BurstConvert getInstance() {
        if (burstConvert == null) {
            burstConvert = new BurstConvert();
        }
        return burstConvert;
    }

    public BurstConvert() {
        initBurstMap();
    }

    private void initBurstMap() {
        // TODO Auto-generated method stub
        burstMap.put(0, 0);
        burstMap.put(1, 1);
        burstMap.put(2, 3);
        burstMap.put(3, 5);
        burstMap.put(4, 10);
    }

    public int getBurstConverFromFw(int fwValue) {
        if (fwValue >= 0 && fwValue <= 4) {
            return burstMap.get(fwValue);
        }
        return 0;
    }
}
