/**
 * Added by zhangyanhu C01012,2014-6-27
 */
package com.icatch.mobilecam.SdkApi;

import com.icatch.mobilecam.Log.AppLog;
import com.icatchtek.control.customer.ICatchCameraPlayback;
import com.icatchtek.control.customer.exception.IchCameraModeException;
import com.icatchtek.control.customer.exception.IchNoSuchPathException;
import com.icatchtek.control.customer.type.ICatchCamListFileFilter;
import com.icatchtek.reliant.customer.exception.IchBufferTooSmallException;
import com.icatchtek.reliant.customer.exception.IchDeviceException;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchNoSuchFileException;
import com.icatchtek.reliant.customer.exception.IchSocketException;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFileType;
import com.icatchtek.reliant.customer.type.ICatchFrameBuffer;

import java.util.List;

/**
 * Added by zhangyanhu C01012,2014-6-27
 */
public class FileOperation {
    private final String tag = "FileOperation";
    private ICatchCameraPlayback cameraPlayback;

    public FileOperation(ICatchCameraPlayback cameraPlayback) {
        this.cameraPlayback = cameraPlayback;
    }

    public boolean cancelDownload() {
        AppLog.i(tag, "begin cancelDownload");
        if (cameraPlayback == null) {
            return true;
        }
        boolean retValue = false;
        try {
            retValue = cameraPlayback.cancelFileDownload();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDeviceException e) {
            AppLog.e(tag, "IchDeviceException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end cancelDownload retValue =" + retValue);
        return retValue;
    }

    public List<ICatchFile> getFileList(int type, int startIndex, int endIndex) {
        AppLog.d(tag, "begin getFileList type:" + type + " startIndex:" + startIndex + " endIndex:" + endIndex);
        List<ICatchFile> list = null;
        int timeout = 5;//单位s
        try {
            //Log.d("1111", "start listFiles cameraPlayback=" + cameraPlayback);
            list = cameraPlayback.listFiles(type, startIndex, endIndex, timeout);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchNoSuchPathException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchNoSuchPathException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchInvalidSessionException");
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        }

//        if (list != null && list.size() > 0) {
//            for (ICatchFile file : list
//            ) {
//                AppLog.d(tag, "getFileList info=" + file.toString());
//            }
//        }
        AppLog.d(tag, "end getFileList");
        AppLog.i(tag, "end getFileList list size=" + (list != null ? list.size() : -1));
        return list;
    }

    public List<ICatchFile> getFileList(int type) {
        AppLog.i(tag, "begin getFileList type:" + type);
        List<ICatchFile> list = null;
        try {
            //Log.d("1111", "start listFiles cameraPlayback=" + cameraPlayback);
            list = cameraPlayback.listFiles(type);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchNoSuchPathException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchNoSuchPathException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchInvalidSessionException");
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            for (int ii =0; ii<list.size();ii++){
                AppLog.d(tag, "getFileList info=" + list.get(ii).toString());
            }
        }
        AppLog.i(tag, "end getFileList list size=" + (list != null ? list.size() : -1));
        return list;
    }

    public int getFileCount() {
        int fileCount = 0;
        AppLog.i(tag, "begin getFileCount");
        try {
            fileCount = cameraPlayback.getFileCount();
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.i(tag, "begin getFileCount Exception:" + e.getClass().getSimpleName() + " error:" + e.getMessage());
        }
        AppLog.i(tag, "end getFileList fileCount=" + fileCount);
        return fileCount;
    }

    public boolean setFileListAttribute(int filterType) {
        boolean ret = false;
        AppLog.i(tag, "begin setFileListAttribute filterType=" + filterType);
        try {
            ret = cameraPlayback.setFileListAttribute(filterType, ICatchCamListFileFilter.ICH_SORT_TYPE_DESCENDING);
//            ret = cameraPlayback.setFileListAttribute(filterType,ICatchCamListFileFilter.ICH_SORT_TYPE_ASCENDING);
            AppLog.i(tag, "11 setFileListAttribute ret=" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.i(tag, "setFileListAttribute Exception:" + e.getClass().getSimpleName() + " error:" + e.getMessage());
        }
        AppLog.i(tag, "end setFileListAttribute ret=" + ret);
        return ret;
    }

    public boolean setFileListAttribute(int filterType,int sensorsType) {
        boolean ret = false;
        AppLog.i(tag, "begin setFileListAttribute filterType=" + filterType + " sensorsType=" + sensorsType);
        try {
            ret = cameraPlayback.setFileListAttribute(filterType, ICatchCamListFileFilter.ICH_SORT_TYPE_DESCENDING,sensorsType);
//            ret = cameraPlayback.setFileListAttribute(filterType,ICatchCamListFileFilter.ICH_SORT_TYPE_ASCENDING);
            AppLog.i(tag, "11 setFileListAttribute ret=" + ret);
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.i(tag, "setFileListAttribute Exception:" + e.getClass().getSimpleName() + " error:" + e.getMessage());
        }
        AppLog.i(tag, "end setFileListAttribute ret=" + ret);
        return ret;
    }

    public boolean deleteFile(ICatchFile file) {
        AppLog.i(tag, "begin deleteFile filename =" + file.getFileName());
        boolean retValue = false;
        try {
            retValue = cameraPlayback.deleteFile(file);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchInvalidSessionException");
        } catch (IchNoSuchFileException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchNoSuchFileException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchDeviceException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end deleteFile retValue=" + retValue);
        return retValue;
    }

    public boolean downloadFile(ICatchFile file, String path) {
        AppLog.i(tag, "begin downloadFile filename =" + file.getFileName());
        AppLog.i(tag, "begin downloadFile path =" + path);
        boolean retValue = false;
        try {
            retValue = cameraPlayback.downloadFile(file, path);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchInvalidSessionException");
        } catch (IchNoSuchFileException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchNoSuchFileException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchDeviceException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end downloadFile retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-2
     */
    public ICatchFrameBuffer downloadFile(ICatchFile curFile) {
        AppLog.i(tag, "begin downloadFile for buffer filename =" + curFile.getFileName());
        ICatchFrameBuffer buffer = null;
        try {
            buffer = cameraPlayback.downloadFile(curFile);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchInvalidSessionException");
        } catch (IchNoSuchFileException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchNoSuchFileException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchDeviceException");
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchBufferTooSmallException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end downloadFile for buffer, buffer =" + buffer);
        return buffer;
    }

    /**
     * Added by zhangyanhu C01012,2014-10-28
     */

    public ICatchFrameBuffer getQuickview(ICatchFile curFile) {
        AppLog.i(tag, "begin getQuickview for buffer filename =" + curFile.getFileName());
        ICatchFrameBuffer buffer = null;
        try {
            buffer = cameraPlayback.getQuickview(curFile);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchInvalidSessionException");
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchDeviceException");
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            AppLog.e(tag, "IchDeviceException");
            e.printStackTrace();
        } catch (IchNoSuchFileException e) {
            AppLog.e(tag, "IchDeviceException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end getQuickview for buffer, buffer =" + buffer);
        if (buffer != null) {
            AppLog.i(tag, "buffer size =" + buffer.getFrameSize());
        }
        return buffer;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-2
     */
    public ICatchFrameBuffer getThumbnail(ICatchFile file) {
        AppLog.i(tag, "begin getThumbnail file=" + file);
        // TODO Auto-generated method stub
        ICatchFrameBuffer frameBuffer = null;
        try {
            //Log.d("1111", "start cameraPlayback.getThumbnail(file) cameraPlayback=" + cameraPlayback);
            frameBuffer = cameraPlayback.getThumbnail(file);
            //Log.d("1111", "end cameraPlayback.getThumbnail(file)");
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchInvalidSessionException");
        } catch (IchNoSuchFileException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchNoSuchFileException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchDeviceException");
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchBufferTooSmallException");
            e.printStackTrace();
        }

        //AppLog.i(tag, "end getThumbnail frameBuffer=" + frameBuffer);
        return frameBuffer;
    }

    public ICatchFrameBuffer getThumbnail(String filePath) {
        AppLog.d("[Normal] -- FileOperation: ", "begin getThumbnail");
        // TODO Auto-generated method stub
        ICatchFile icathfile = new ICatchFile(33, ICatchFileType.ICH_FILE_TYPE_VIDEO, filePath, "", 0);
        AppLog.d("[Normal] -- FileOperation: ", "begin getThumbnail file=" + filePath);
        AppLog.d("[Normal] -- FileOperation: ", "begin getThumbnail cameraPlayback=" + cameraPlayback);
        ICatchFrameBuffer frameBuffer = null;
        try {
            AppLog.d("test", "start cameraPlayback.getThumbnail(file) cameraPlayback=" + cameraPlayback);
            frameBuffer = cameraPlayback.getThumbnail(icathfile);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchInvalidSessionException");
        } catch (IchNoSuchFileException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchNoSuchFileException");
            e.printStackTrace();
        } catch (IchDeviceException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchDeviceException");
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            // TODO Auto-generated catch block
            AppLog.d("[Error] -- FileOperation: ", "IchBufferTooSmallException");
            e.printStackTrace();
        }
        AppLog.d("[Normal] -- FileOperation: ", "end getThumbnail frameBuffer=" + frameBuffer);
        return frameBuffer;
    }


}
