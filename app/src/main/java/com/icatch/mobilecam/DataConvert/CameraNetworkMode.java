package com.icatch.mobilecam.DataConvert;

import android.util.SparseArray;

/**
 * Created by zhang yanhu C001012 on 2015/11/24 16:23.
 */
public final class  CameraNetworkMode {
    public static final int STATION = 0;
    public static final int AP = 1;
    public static final int ETHERNET = 2;
    private static SparseArray<String> modeMap = new SparseArray<String>();


    public static String getModeConvert(int mode) {
        if(modeMap.size() == 0) {
            initNetworkModeMap();
        }
        return modeMap.get(mode);
    }

    private static void initNetworkModeMap() {
        // TODO Auto-generated method stub
        modeMap.put(0, "Station");
        modeMap.put(1, "AP");
        modeMap.put(2, "Ethernet");
    }
}


