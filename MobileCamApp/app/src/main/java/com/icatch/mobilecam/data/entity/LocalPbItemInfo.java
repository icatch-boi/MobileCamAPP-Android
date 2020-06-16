package com.icatch.mobilecam.data.entity;

import com.icatch.mobilecam.utils.ConvertTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LocalPbItemInfo {
    public File file;

    public int section;
    public boolean isItemChecked = false;
    private boolean isPanorama = false;

    public LocalPbItemInfo(File file, int section) {
        this.file = file;
        this.section =section;
        this.isItemChecked = false;
    }

    public LocalPbItemInfo(File file, int section, boolean isPanorama) {
        this.file = file;
        this.section = section;
        this.isPanorama = isPanorama;
    }

    public LocalPbItemInfo(File file) {
        super();
        this.file = file;
        this.isItemChecked = false;
    }

    public void setSection(int section){
        this.section = section;
    }

    public String getFilePath(){
        return file.getPath();
    }

    public String getFileDate(){
        long time = file.lastModified();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time));
    }

    public String getFileSize(){
        int size = (int)file.length();
        return  ConvertTools.ByteConversionGBMBKB(size);
    }

    public String getFileName(){
        return file.getName();
    }
    public String getFileDateMMSS(){
        long time = file.lastModified();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }

    public boolean isPanorama() {
        return isPanorama;
    }

    public void setPanorama(boolean panorama) {
        isPanorama = panorama;
    }

    //读取文件创建时间
    public void getCreateTime() {
        String filePath = file.getPath();
        String strTime = null;
        try {
            Process p = Runtime.getRuntime().exec("cmd /C dir " + filePath + "/tc");
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.endsWith(".txt")) {
                    strTime = line.substring(0, 17);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("创建时间    " + strTime);
    }

}
