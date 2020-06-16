package com.icatch.mobilecam.Function.CameraAction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.ui.adapter.DownloadManagerAdapter;
import com.icatch.mobilecam.ui.appdialog.CustomDownloadDialog;
import com.icatch.mobilecam.data.AppInfo.AppInfo;
import com.icatch.mobilecam.data.entity.DownloadInfo;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.Message.AppMessage;
import com.icatch.mobilecam.R;
import com.icatch.mobilecam.SdkApi.FileOperation;
import com.icatch.mobilecam.utils.fileutils.FileTools;
import com.icatch.mobilecam.utils.MediaRefresh;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFileType;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Added by zhangyanhu C01012,2014-6-20
 */
public class PbDownloadManager {
    private static String TAG = "PbDownloadManager";
    private ExecutorService executor;
    public long downloadProgress;
    public FileOperation fileOperation;
    private LinkedList<ICatchFile> downloadTaskList;
    private LinkedList<ICatchFile> downloadChooseList;
    private LinkedList<ICatchFile> downloadProgressList;
    private DownloadManagerAdapter downloadManagerAdapter;
    private AlertDialog.Builder builder;
    private Context context;
    private HashMap<ICatchFile, DownloadInfo> downloadInfoMap = new HashMap<ICatchFile, DownloadInfo>();
    private ICatchFile currentDownloadFile;
    private Timer downloadProgressTimer;
    private int downloadFailed = 0;
    private int downloadSucceed = 0;
    private CustomDownloadDialog customDownloadDialog;
    private String curFilePath = "";

    public PbDownloadManager(Context context, LinkedList<ICatchFile> downloadList) {
        this.context = context;
        this.downloadTaskList = downloadList;
        this.fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();
//        filePath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH;
        downloadChooseList = new LinkedList<>();
        downloadProgressList = new LinkedList<ICatchFile>();
        downloadChooseList.addAll(downloadTaskList);
        for (int ii = 0; ii < downloadChooseList.size(); ii++) {
            DownloadInfo downloadInfo = new DownloadInfo(downloadChooseList.get(ii), downloadChooseList.get(ii).getFileSize(), 0, 0, false);
            downloadInfoMap.put(downloadChooseList.get(ii), downloadInfo);
        }
    }

    public void show() {
        showDownloadManagerDialog();
        executor = Executors.newSingleThreadExecutor();
        if (downloadTaskList.size() > 0) {
            currentDownloadFile = downloadTaskList.getFirst();
            new DownloadAsytask(currentDownloadFile).execute();
            downloadProgressTimer = new Timer();
            downloadProgressTimer.schedule(new DownloadProgressTask(), 0, 100);

        }
    }


    Handler downloadManagerHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg)
            String message;
            switch (msg.what) {
                case AppMessage.UPDATE_LOADING_PROGRESS:
                    ICatchFile icatchFile = ((DownloadInfo) msg.obj).file;
                    downloadInfoMap.put(icatchFile, (DownloadInfo) msg.obj);
                    downloadManagerAdapter.notifyDataSetChanged();
                    break;
                case AppMessage.CANCEL_DOWNLOAD_ALL:
                    AppLog.d(TAG, "receive CANCEL_DOWNLOAD_ALL");
                    alertForQuitDownload();
                    break;
                case AppMessage.MESSAGE_CANCEL_DOWNLOAD_SINGLE:
                    ICatchFile temp = (ICatchFile) msg.obj;
                    AppLog.d(TAG, "1122 receive MESSAGE_CANCEL_DOWNLOAD_SINGLE");
                    if (currentDownloadFile == temp) {
                        if (fileOperation.cancelDownload() == false) {
                            Toast.makeText(context, R.string.dialog_cancel_downloading_failed, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        //ICOM-4224 Begin modify by b.jiang 20170329
//                        String fileName = currentDownloadFile.getFileName();
//                        String filePath = null;
//                        if(currentDownloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_IMAGE) {
//                            filePath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_PHOTO
//                                    + fileName;
//                        }else if(currentDownloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_VIDEO) {
//                            filePath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_VIDEO
//                                    + fileName;
//                        }
                        if (curFilePath != null) {
                            File file = new File(curFilePath);
                            //ICOM-4224 End modify by b.jiang 20170329
                            if (file == null || !file.exists()) {
                                return;
                            }
                            if (file.delete()) {
                                AppLog.d("2222", "delete file success == " + curFilePath);
                            }
                        }


                    }
                    Toast.makeText(context, R.string.dialog_cancel_downloading_succeeded, Toast.LENGTH_SHORT).show();
                    downloadInfoMap.remove(temp);
                    downloadChooseList.remove(temp);
                    downloadTaskList.remove(temp);
                    AppLog.d(TAG, "1122 receive MESSAGE_CANCEL_DOWNLOAD_SINGLE downloadChooseList size=" + downloadChooseList.size() + "downloadInfoMap size=" + downloadInfoMap.size());
                    downloadManagerAdapter = new DownloadManagerAdapter(context, downloadInfoMap, downloadChooseList, downloadManagerHandler);
                    customDownloadDialog.setAdapter(downloadManagerAdapter);
                    downloadManagerAdapter.notifyDataSetChanged();

                    updateDownloadMessage();
                    if (downloadTaskList.size() <= 0) {
                        if (customDownloadDialog != null) {
                            customDownloadDialog.dismissDownloadDialog();
                        }
                    }
                    break;
                case AppMessage.DOWNLOAD_FAILURE:
                    AppLog.d(TAG, "receive DOWNLOAD_FAILURE downloadFailed=" + downloadFailed);
                    downloadFailed++;
                    updateDownloadMessage();
                    break;
                case AppMessage.DOWNLOAD_SUCCEED:
                    downloadSucceed++;
                    updateDownloadMessage();
                    break;
            }
        }
    };


    public void showDownloadManagerDialog() {
        downloadManagerAdapter = new DownloadManagerAdapter(context, downloadInfoMap, downloadChooseList, downloadManagerHandler);
        customDownloadDialog = new CustomDownloadDialog();
        customDownloadDialog.showDownloadDialog(context, downloadManagerAdapter);
        customDownloadDialog.setBackBtnOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                downloadManagerHandler.obtainMessage(AppMessage.CANCEL_DOWNLOAD_ALL).sendToTarget();
            }
        });
        updateDownloadMessage();
    }


    public void alertForQuitDownload() {
        if (builder != null) {
            return;
        }
        builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle(R.string.dialog_btn_exit).setMessage(R.string.downloading_quit);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadTaskList.clear();
                //ICOM-4224 Begin modify by b.jiang 20170329
                if (curFilePath != null) {
                    File file = new File(curFilePath);
                    if (file == null || !file.exists()) {
                        return;
                    }
                    if (file.delete()) {
                        AppLog.d("2222", "alertForQuitDownload file success == " + curFilePath);
                    }
                }
                //ICOM-4224 End modify by b.jiang 20170329

                if (fileOperation.cancelDownload() == false) {
                    Toast.makeText(context, R.string.dialog_cancel_downloading_failed, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    customDownloadDialog.dismissDownloadDialog();
                    if (downloadProgressTimer != null) {
                        downloadProgressTimer.cancel();
                    }
                    Toast.makeText(context, R.string.dialog_cancel_downloading_succeeded, Toast.LENGTH_SHORT).show();
                }
                AppLog.d(TAG, "cancel download task and quit download manager");
            }
        });

        builder.setNegativeButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder = null;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void downloadCompleted() {
        curFilePath = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.download_manager));
        String message = context.getResources().getString(R.string.download_complete_result).replace("$1$", String.valueOf(downloadSucceed))
                .replace("$2$", String.valueOf(downloadFailed));
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    class DownloadProgressTask extends TimerTask {

        @Override
        public void run() {

            if (downloadProgressList.isEmpty()) {
                return;
            }
            String TAG = "DownloadProgressTask";
            final ICatchFile iCatchFile = downloadProgressList.getFirst();
//            String path = null;
//            if(iCatchFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_IMAGE) {
//                path = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_PHOTO
//                        + iCatchFile.getFileName();
//            }else if(iCatchFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_VIDEO) {
//                path = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_VIDEO
//                        + iCatchFile.getFileName();
//            }
            File file = new File(curFilePath);
            AppLog.d(TAG, "filename = " + file);
            if (downloadInfoMap.containsKey(iCatchFile) == false) {
                downloadProgressList.removeFirst();
                return;
            }

            long fileLength = file.length();
            if (file != null) {
                if (fileLength == iCatchFile.getFileSize()) {
                    downloadProgress = 100;
                    downloadProgressList.removeFirst();
                } else {
                    downloadProgress = file.length() * 100 / iCatchFile.getFileSize();
                }
            } else {
                downloadProgress = 0;
            }
            if (downloadInfoMap.containsKey(iCatchFile) == false) {
                return;
            }
            final DownloadInfo downloadInfo = downloadInfoMap.get(iCatchFile);
            downloadInfo.curFileLength = fileLength;
            downloadInfo.progress = (int) downloadProgress;
            AppLog.d(TAG, "downloadProgress = " + downloadProgress);
            downloadManagerHandler.post(new Runnable() {
                @Override
                public void run() {
                    downloadInfoMap.put(iCatchFile, downloadInfo);
                    downloadManagerAdapter.notifyDataSetChanged();
                }
            });
//            downloadManagerHandler.obtainMessage(AppMessage.UPDATE_LOADING_PROGRESS, (int) downloadProgress, 0, downloadInfo).sendToTarget();
        }
    }

    class DownloadAsytask extends AsyncTask<String, Integer, Boolean> {
        private String TAG = "DownloadAsytask";
        ICatchFile downloadFile;
        private String fileName;
        private String fileType = null;
        private String filePath = null;

        public DownloadAsytask(ICatchFile iCatchFile) {
            super();
            downloadFile = iCatchFile;
            downloadProgressList.addLast(downloadFile);
            if (downloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_IMAGE) {
                filePath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_PHOTO;
            } else if (downloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_VIDEO) {
                filePath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_VIDEO;
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            boolean retvalue = false;
            fileName = downloadFile.getFileName();
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            //ICOM-4116 Begin Delete by b.jiang 20170315
//            File tempFile = new File(filePath + fileName);
//            if (tempFile.exists()) {
//                if (tempFile.length() == downloadFile.getFileSize()) {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    downloadTaskList.remove(downloadFile);
//                    return true;
//                }
//            }
            //ICOM-4116 End Delete by b.jiang 20170315

            AppLog.d(TAG, "start downloadFile=" + filePath + fileName);
            //ICOM-4116 Begin Add by b.jiang 20170315
            curFilePath = FileTools.chooseUniqueFilename(filePath + fileName);
            retvalue = fileOperation.downloadFile(downloadFile, curFilePath);
            //ICOM-4116 End Add by b.jiang 2017031
            AppLog.d(TAG, "end downloadFile retvalue =" + retvalue);
            if (retvalue) {
                if (downloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_VIDEO) {
                    AppLog.d(TAG, "fileName = " + fileName);
                    if (fileName.endsWith(".mov") || fileName.endsWith(".MOV")) {
                        fileType = "video/mov";
                    } else {
                        fileType = "video/mp4";
                    }
                    MediaRefresh.addMediaToDB(context, filePath + downloadFile.getFileName(), fileType);
                } else if (downloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_IMAGE) {
                    MediaRefresh.scanFileAsync(context, filePath + downloadFile.getFileName());
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return retvalue;
        }

        protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行
        }

        protected void onPostExecute(Boolean result) {
            //后台任务执行完之后被调用，在ui线程执行
            if (!result) {
                AppLog.d(TAG, "receive DOWNLOAD_FAILURE downloadFailed=" + downloadFailed);
                downloadProgressList.remove(downloadFile);
                downloadFailed++;
                updateDownloadMessage();
            } else {
                downloadSucceed++;
                updateDownloadMessage();
            }
            downloadTaskList.remove(downloadFile);
            if (downloadTaskList.size() > 0) {
                currentDownloadFile = downloadTaskList.getFirst();
                new DownloadAsytask(currentDownloadFile).execute();
            } else {
                if (customDownloadDialog != null) {
                    customDownloadDialog.dismissDownloadDialog();
                }
                if (downloadProgressTimer != null) {
                    downloadProgressTimer.cancel();
                }
                downloadCompleted();
            }
        }
    }

    private void updateDownloadMessage() {
        //JIRA BUG ICOM-3525 Start modify by b.jiang 20160725
        String message = context.getResources().getString(R.string.download_progress).replace("$1$", String.valueOf(downloadSucceed))
                .replace("$2$", String.valueOf(downloadChooseList.size() - downloadSucceed)).replace("$3$", String.valueOf(downloadFailed));
        //JIRA BUG ICOM-3525 End modify by b.jiang 20160725
        customDownloadDialog.setMessage(message);
    }
}
