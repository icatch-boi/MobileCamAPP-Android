package com.icatch.mobilecam.utils.fileutils;

import com.icatch.mobilecam.Log.AppLog;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/19 10:50.
 */
public class MFileTools extends FileTools {
    private final static String TAG = MFileTools.class.getSimpleName();

    public static String getNewestPhotoFromDirectory(String directoryPath) {
        List<File> files = getPhotosOrderByDate(directoryPath);
        if (files == null || files.isEmpty() || files.size() < 0) {
            AppLog.i(TAG, "getNewestPhotoFromDirectory = null");
            return null;
        }
        AppLog.i(TAG, "getNewestPhotoFromDirectory path =" + files.get(0).getPath());
        return files.get(0).getPath();
    }

    public static String getNewestVideoFromDirectory(String directoryPath) {
        List<File> files = getVideosOrderByDate(directoryPath);
        if (files == null || files.isEmpty() || files.size() < 0) {
            return null;
        }
        AppLog.i(TAG, "getNewestVideoFromDirectory path =" + files.get(0).getPath());
        return files.get(0).getPath();
    }

    public static List<File> getPhotosOrderByDate(String directoryPath) {
        AppLog.i(TAG, "start getPhotosOrderByDate");
        List<File> files = getFilesOrderByDate(directoryPath);
        if (files == null || files.isEmpty() || files.size() < 0) {
            return null;
        }
        List<File> tempFiles = new LinkedList<File>();
        for (File f : files) {
            String fileName = f.getName();
            if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".PNG") || fileName.endsWith(".JPG")) {
                tempFiles.add(f);
            } else {
                // files.remove(f);
            }
        }
        AppLog.i(TAG, "end getPhotosOrderByDate");
        return tempFiles;
    }

    public static List<File> getVideosOrderByDate(String directoryPath) {
        List<File> files = getFilesOrderByDate(directoryPath);
        if (files == null || files.isEmpty() || files.size() < 0) {
            return null;
        }
        List<File> tempFiles = new LinkedList<File>();
        for (File f : files) {
            String fileName = f.getName();
            if (fileName.endsWith(".MP4") || fileName.endsWith(".wmv") || fileName.endsWith(".mp4") || fileName.endsWith(".3gp")
                    || fileName.endsWith(".MOV") || fileName.endsWith(".mov") || fileName.endsWith(".AVI") || fileName.endsWith(".avi")) {
                tempFiles.add(f);
            } else {
                //files.remove(f);
            }
        }
        return tempFiles;
    }

    public static int getPhotosSize(String directoryPath) {
        AppLog.i(TAG, "Start getPhotosSize filePath=" + directoryPath);
        int fileSize = 0;
        List<File> files = getPhotosOrderByDate(directoryPath);
        if (files == null || files.isEmpty() || files.size() < 0) {
            fileSize = 0;
        } else {
            fileSize = files.size();
        }
        AppLog.i(TAG, "End getPhotosSize size=" + fileSize);
        return fileSize;
    }

    public static int getVideosSize(String directoryPath) {
        AppLog.i(TAG, "Start getVideosSize filePath=" + directoryPath);
        int fileSize = 0;
        List<File> files = getVideosOrderByDate(directoryPath);
        if (files == null || files.isEmpty() || files.size() < 0) {
            fileSize = 0;
        } else {
            fileSize = files.size();
        }
        AppLog.i(TAG, "End getVideosSize size=" + fileSize);
        return fileSize;
    }
}
