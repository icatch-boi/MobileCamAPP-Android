package com.icatch.mobilecam.SdkApi;

import com.icatch.mobilecam.Log.AppLog;
import com.icatchtek.pancam.customer.exception.IchGLNotInitedException;
import com.icatchtek.pancam.customer.stream.ICatchIStreamStablization;

/**
 * Created by b.jiang on 2018/5/18.
 */

public class StreamStablization {
    private final String TAG =StreamStablization.class.getSimpleName();
    private ICatchIStreamStablization streamStablization;
    public StreamStablization(ICatchIStreamStablization streamStablization){
        this.streamStablization = streamStablization;
    }

    public  boolean enableStablization(){
        AppLog.d(TAG,"enableStablization ");
        if(streamStablization == null){
            return false;
        }

        boolean ret = false;
        try {
            ret = streamStablization.enableStablization();
        } catch (IchGLNotInitedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG,"end enableStablization ret=" + ret);
        return ret;
    }

    public boolean disableStablization(){
        AppLog.d(TAG,"disableStablization ");
        if(streamStablization == null){
            return false;
        }
        boolean ret = false;
        try {
            ret = streamStablization.disableStablization();
        } catch (IchGLNotInitedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG,"end disableStablization ret=" + ret);
        return ret;

    }
}
