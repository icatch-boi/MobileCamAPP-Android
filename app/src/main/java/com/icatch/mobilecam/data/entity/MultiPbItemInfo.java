package com.icatch.mobilecam.data.entity;


import com.icatch.mobilecam.utils.ConvertTools;
import com.icatchtek.reliant.customer.type.ICatchFile;


public class MultiPbItemInfo {
    private static final String TAG = MultiPbItemInfo.class.getSimpleName();
    public ICatchFile iCatchFile;

    public int section;
    public boolean isItemChecked =false;
    private boolean isPanorama =false;
    private String fileDate;

    public MultiPbItemInfo(ICatchFile file) {
        super();
        this.iCatchFile = file;
        this.isItemChecked = false;
    }
    public MultiPbItemInfo(ICatchFile file, int section) {
        super();
        this.iCatchFile = file;
        this.section =section;
        this.isItemChecked = false;
    }

    public MultiPbItemInfo(ICatchFile iCatchFile, int section, boolean isPanorama) {
        this.iCatchFile = iCatchFile;
        this.section = section;
        this.isPanorama = isPanorama;
    }

    public void setPanorama(boolean panorama) {
        isPanorama = panorama;
    }

    public boolean isPanorama() {
        return isPanorama;
    }

    public void setSection(int section){
        this.section = section;
    }

    public String getFilePath(){
        return iCatchFile.getFilePath();
    }

    public int getFileHandle(){
        return iCatchFile.getFileHandle();
    }

    public String getFileDate(){
        String time = iCatchFile.getFileDate();
        if(time == null || time.isEmpty()){
            time = "unknown";
        }else if(time.contains( "T" ) == false){

        }else {
            int position = time.indexOf( "T" );
            time = time.substring( 0, position );
        }
        return time;
    }

    public String getFileSize(){
        int size = (int)iCatchFile.getFileSize();
        return  ConvertTools.ByteConversionGBMBKB(size);
    }

    public long getFileSizeInteger(){
        long fileSize = iCatchFile.getFileSize();
        return  fileSize;
    }

    public String getFileName(){
        return iCatchFile.getFileName();
    }
    public String getFileDateMMSS(){
        return dateFormatTransform(iCatchFile.getFileDate());
    }

    public String  dateFormatTransform(String value){
        if(value == null){
            return "";
        }
        String date = "";
        String time = "";
        String yy = "";
        String MM = "";
        String dd = "";
        String hh = "";
        String mm = "";
        String ss = "";
        int position = value.indexOf("T");
        date = value.substring(0,position);//20161021
        time = value.substring(position +1);
        yy = date.substring(0,4);
        MM = date.substring(4,6);
        dd = date.substring(6,8);
        hh = time.substring(0,2);
        mm = time.substring(2,4);
        ss = time.substring(4,6);
        date = yy+ "-" + MM + "-" + dd + " " + hh + ":" + mm + ":" + ss;
        return date;
    }
}
