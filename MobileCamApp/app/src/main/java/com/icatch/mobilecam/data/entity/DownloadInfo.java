package com.icatch.mobilecam.data.entity;


import com.icatchtek.reliant.customer.type.ICatchFile;

public class DownloadInfo {
    public ICatchFile file = null;
    public long fileSize = 0;
    public long curFileLength = 0;
    public int progress = 0;
    public boolean done = false;

    public DownloadInfo(ICatchFile file, long fileSize, long curFileLength, int progress, boolean done) {
        this.file = file;
        this.fileSize = fileSize;
        this.curFileLength = curFileLength;
        this.progress = progress;
        this.done = done;
    }
}
