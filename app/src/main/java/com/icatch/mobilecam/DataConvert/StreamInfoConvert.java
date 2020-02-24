package com.icatch.mobilecam.DataConvert;

import com.icatch.mobilecam.data.entity.StreamInfo;
import com.icatch.mobilecam.Log.AppLog;

/**
 * Created by zhang yanhu C001012 on 2015/12/11 17:16.
 */
public class StreamInfoConvert {
    public static StreamInfo convertToStreamInfoBean(String cmd){
        String[] temp;
        StreamInfo streamInfo = new StreamInfo();
        //JIRA IC-395
        if(cmd.contains("FPS")){
            temp = cmd.split("\\?|&");
            streamInfo.mediaCodecType = temp[0];
            temp[1] = temp[1].replace("W=","");
            temp[2] = temp[2].replace("H=","");
            temp[3] = temp[3].replace("BR=","");
            temp[4] = temp[4].replace("FPS=","");
            streamInfo.width = Integer.parseInt(temp[1]);
            streamInfo.height = Integer.parseInt(temp[2]);
            streamInfo.bitrate = Integer.parseInt(temp[3]);
            streamInfo.fps = Integer.parseInt(temp[4]);
        }else {
            temp = cmd.split("\\?|&");
            streamInfo.mediaCodecType = temp[0];
            temp[1] = temp[1].replace("W=","");
            temp[2] = temp[2].replace("H=","");
            temp[3] = temp[3].replace("BR=","");
            streamInfo.width = Integer.parseInt(temp[1]);
            streamInfo.height = Integer.parseInt(temp[2]);
            streamInfo.bitrate = Integer.parseInt(temp[3]);
            streamInfo.fps = 30;
        }

        AppLog.i("1111","streamInfo.width ="+streamInfo.width);
        AppLog.i("1111","streamInfo.heigh ="+streamInfo.height);
        AppLog.i("1111","streamInfo.mediaCodecType ="+streamInfo.mediaCodecType);
        AppLog.i("1111","streamInfo.bitrate ="+streamInfo.bitrate);
        AppLog.i("1111","streamInfo.fps ="+streamInfo.fps);
        return  streamInfo;
    }
}
