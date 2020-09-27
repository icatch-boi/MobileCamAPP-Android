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
        int ICH_CAM_BURST_NUMBER_HS = 0;
        int ICH_CAM_BURST_NUMBER_OFF = 1;
        int ICH_CAM_BURST_NUMBER_3 = 2;
        int ICH_CAM_BURST_NUMBER_5 = 3;
        int ICH_CAM_BURST_NUMBER_10 = 4;
        int ICH_CAM_BURST_NUMBER_7 = 5;
        int ICH_CAM_BURST_NUMBER_15 = 6;
        int ICH_CAM_BURST_NUMBER_30 = 7;
        burstMap.put(ICH_CAM_BURST_NUMBER_HS, 0);
        burstMap.put(ICH_CAM_BURST_NUMBER_OFF, 1);
        burstMap.put(ICH_CAM_BURST_NUMBER_3, 3);
        burstMap.put(ICH_CAM_BURST_NUMBER_5, 5);
        burstMap.put(ICH_CAM_BURST_NUMBER_10, 10);
        burstMap.put(ICH_CAM_BURST_NUMBER_7, 7);
        burstMap.put(ICH_CAM_BURST_NUMBER_15, 15);
        burstMap.put(ICH_CAM_BURST_NUMBER_30, 30);
    }

    public int getBurstConverFromFw(int fwValue) {
        if (fwValue >= 0 && fwValue <= 7) {
            return burstMap.get(fwValue);
        }
        return 0;
    }
}
