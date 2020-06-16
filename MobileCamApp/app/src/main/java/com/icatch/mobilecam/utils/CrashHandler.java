/**
 * Added by zhangyanhu C01012,2014-10-13
 */
package com.icatch.mobilecam.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


import com.icatch.mobilecam.Application.PanoramaApp;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.ui.ExtendComponent.MyToast;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author email
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    private Handler handler = new Handler();
    // CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    // 程序的Context对象
    private Context mContext;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(thread,ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            AppLog.e(TAG, "....default process!");
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                AppLog.e(TAG, e.toString());
            }
            AppLog.e(TAG, "....show custom process!");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PanoramaApp.getContext(), R.string.app_exception, Toast.LENGTH_LONG).show();
                }
            });

//			 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Thread thread ,final Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                MyToast.show(mContext, mContext.getResources().getString(R.string.app_exception) + "\n" + ex.toString());
                Looper.loop();
            }
        }.start();
        String localizedMessage = ex.getLocalizedMessage();
        AppLog.e(TAG, "localizedMessage" + (localizedMessage==null ? "null" : localizedMessage));
        String message = ex.getMessage();
        AppLog.e(TAG, message==null ? "null" : message);
        AppLog.e(TAG, ex.toString());
        getStackTrace(ex);
        getThreadStackTrace(thread);
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        // saveCrashInfo2File(ex);
        return true;
    }

    private void getThreadStackTrace(Thread thread){
        if(thread == null){
            return;
        }
        AppLog.e(TAG,"thread id:" + thread.getId() + " name:" + thread.getName());
        StackTraceElement[] traceElements = thread.getStackTrace();
        if(traceElements != null && traceElements.length > 0){
            for (StackTraceElement temp:traceElements
            ) {
                if(temp != null){
                    String stackString = temp.toString();
                    AppLog.e(TAG, stackString);
                }
            }
        }
    }

    /**
     * 调用栈信息
     * @param ex
     */
    private void getStackTrace(Throwable ex){
        StackTraceElement[] traceElements = ex.getStackTrace();
        if(traceElements != null && traceElements.length > 0){
            for (StackTraceElement temp:traceElements
                 ) {
                if(temp != null){
                    String stackString = temp.toString();
                    AppLog.e(TAG, stackString);
                }
            }
        }
    }
    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            AppLog.d(TAG, e.toString());
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                AppLog.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                AppLog.e(TAG, e.toString());
            }
        }
    }
}
