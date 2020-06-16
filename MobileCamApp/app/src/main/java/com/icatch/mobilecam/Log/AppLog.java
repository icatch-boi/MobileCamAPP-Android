package com.icatch.mobilecam.Log;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.icatch.mobilecam.Application.PanoramaApp;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.utils.MediaRefresh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yh.zhang C001012 on 2015/10/15:15:08.
 * Fucntion:
 */
public class AppLog {
    private static String TAG = AppLog.class.getSimpleName();
    private static String writeFile;
    private static FileOutputStream out = null;
    private static boolean hasConfiguration = false;
    private static File writeLogFile = null;
    private final static long maxFileSize = 1024 * 1024 * 50;
    private static boolean enableLog = false;

    public static void enableAppLog() {
        enableLog = true;
        initConfiguration();
    }

    private static void initConfiguration() {
        File directory = null;
        String fileName = null;
        String path = null;
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        // System.out.println(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.CHINA);
        path = Environment.getExternalStorageDirectory().toString() + AppInfo.APP_LOG_DIRECTORY_PATH;
        if (path != null) {
            directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }

        fileName = sdf.format(date) + ".log";
        File file = new File(directory, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        writeFile = path + fileName;
        writeLogFile = new File(writeFile);
        if (out != null) {
            closeWriteStream();
        }
        try {
            out = new FileOutputStream(writeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        hasConfiguration = true;

        i("", sdf.format(date) + "\n");
        i("", AppInfo.APP_VERSION + "\n");
    }


    public static String getSystemDate() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss	");
        return sdf.format(date);
    }


    public static void e(String tag, String conent) {
        if (enableLog == false) {
            return;
        }
        if (!hasConfiguration) {
            //return;
            initConfiguration();
        }
        if (writeLogFile.length() >= maxFileSize) {
            initConfiguration();
        }
        String temp = "[" + tag + "]" + getSystemDate() + ": " + "AppError:" + conent + "\n";
        Log.i(TAG, temp);
        try {
            if (out != null) {
                out.write(temp.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static void i(String tag, String conent) {
        if (enableLog == false) {
            return;
        }
        if (!hasConfiguration) {
            initConfiguration();
        }
        if (writeLogFile.length() >= maxFileSize) {
            initConfiguration();
        }
        String temp = getSystemDate() + " " + "AppInfo:" + "[" + tag + "]" + conent + "\n";
        Log.i(TAG, temp);
        try {
            if (out != null) {
                out.write(temp.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static void w(String tag, String conent) {
        if (enableLog == false) {
            return;
        }
        if (!hasConfiguration) {
            initConfiguration();
        }
        if (writeLogFile.length() >= maxFileSize) {
            initConfiguration();
        }
        String temp = "[" + tag + "]" + getSystemDate() + ": " + "AppWarning:" + conent + "\n";

        try {
            if (out != null) {
                out.write(temp.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static void d(String tag, String conent) {
        if (enableLog == false) {
            return;
        }
        if (!hasConfiguration) {
            initConfiguration();
        }
        if (writeLogFile.length() >= maxFileSize) {
            initConfiguration();
        }
        String temp = getSystemDate() + "[" + tag + "]" + ":" + "AppDebug:" + conent + "\n";
        Log.i(TAG, temp);
        try {
            if (out != null) {
                out.write(temp.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static void closeWriteStream() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void refreshAppLog() {
        Context context = PanoramaApp.getContext();
        if (context != null && writeFile != null) {
            MediaRefresh.notifySystemToScan(writeFile, context);
        }

    }
}