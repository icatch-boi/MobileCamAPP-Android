package com.icatch.mobilecam.ui;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.MyCamera.MyCamera;
import com.icatch.mobilecam.SdkApi.CameraProperties;
import com.icatch.mobilecam.SdkApi.FileOperation;
import com.icatch.mobilecam.data.PropertyId.PropertyId;
import com.icatch.mobilecam.data.entity.MultiPbFileResult;
import com.icatch.mobilecam.data.entity.MultiPbItemInfo;
import com.icatch.mobilecam.data.type.FileType;
import com.icatch.mobilecam.utils.ConvertTools;
import com.icatch.mobilecam.utils.FileFilter;
import com.icatch.mobilecam.utils.PanoramaTools;
import com.icatchtek.control.customer.type.ICatchCamFeatureID;
import com.icatchtek.control.customer.type.ICatchCamListFileFilter;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFileType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author b.jiang
 * @date 2020/1/9
 * @description
 */
public class RemoteFileHelper {
    private String TAG = RemoteFileHelper.class.getSimpleName();
    private static RemoteFileHelper instance;
    public HashMap<Integer, List<MultiPbItemInfo>> listHashMap = new HashMap<>();
    private int curFilterFileType = ICatchCamListFileFilter.ICH_OFC_FILE_TYPE_ALL_MEDIA;
    private FileFilter fileFilter = null;
    private final int MAX_NUM = 30;
    private boolean supportSegmentedLoading = false;
    private boolean supportSetFileListAttribute = false;
    private int sensorsNum = 1;

    //    ICatchCameraProperty
//    ICatchCamFeatureID
//
    public static synchronized RemoteFileHelper getInstance() {
        if (instance == null) {
            instance = new RemoteFileHelper();
        }
        return instance;
    }

    public void initSupportCapabilities() {
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        CameraProperties cameraProperties = null;
        if (camera != null) {
            cameraProperties = camera.getCameraProperties();
        }
        if (cameraProperties != null
                && cameraProperties.hasFuction(PropertyId.CAMERA_PB_LIMIT_NUMBER)
                && cameraProperties.checkCameraCapabilities(ICatchCamFeatureID.ICH_CAM_NEW_PAGINATION_GET_FILE)) {
//        if (cameraProperties != null
//                && cameraProperties.hasFuction(PropertyId.CAMERA_PB_LIMIT_NUMBER)) {
            supportSegmentedLoading = true;
            supportSetFileListAttribute = true;
        } else {
            supportSegmentedLoading = false;
            supportSetFileListAttribute = false;
        }

        if(cameraProperties != null){
            sensorsNum = cameraProperties.getNumberOfSensors();
        }
    }

    public int getSensorsNum() {
        return sensorsNum;
    }

    public boolean isSupportSegmentedLoading() {
        return supportSegmentedLoading;
    }

    public boolean isSupportSetFileListAttribute() {
        return supportSetFileListAttribute;
    }

    public List<MultiPbItemInfo> getRemoteFile(FileOperation fileOperation, FileType fileType) {
        int icatchFileType = ICatchFileType.ICH_FILE_TYPE_IMAGE;
        if (fileType == FileType.FILE_PHOTO) {
            icatchFileType = ICatchFileType.ICH_FILE_TYPE_IMAGE;
        } else if (fileType == FileType.FILE_VIDEO) {
            icatchFileType = ICatchFileType.ICH_FILE_TYPE_VIDEO;
        } else if (fileType == FileType.FILE_EMERGENCY_VIDEO) {
            icatchFileType = ICatchFileType.ICH_FILE_TYPE_VIDEO;
        }
        setFileListAttribute(fileOperation, fileType);
        List<ICatchFile> fileList = fileOperation.getFileList(icatchFileType);
        List<MultiPbItemInfo> tempItemInfos = getList(fileList, fileFilter);
        return tempItemInfos;
    }

    public MultiPbFileResult getRemoteFile(FileOperation fileOperation, FileType fileType, int fileTotalNum, int startIndex) {
        AppLog.d(TAG, "getRemoteFile fileType:" + fileType + " fileTotalNum:" + fileTotalNum + " startIndex:" + startIndex + " maxNum:" + MAX_NUM);
        if (startIndex > fileTotalNum) {
            MultiPbFileResult result = new MultiPbFileResult();
            result.setLastIndex(startIndex);
            result.setMore(false);
            result.setFileList(null);
            return result;
        }
        int endIndex = MAX_NUM + startIndex - 1;
        if (endIndex >= fileTotalNum) {
            endIndex = fileTotalNum;
        }
        setFileListAttribute(fileOperation, fileType);

        List<ICatchFile> fileList = fileOperation.getFileList(ICatchFileType.ICH_FILE_TYPE_ALL, startIndex, endIndex);
//        List<ICatchFile> fileList = fileOperation.getFileList(icatchFileType, startIndex, endIndex);
        if (fileFilter == null || fileFilter.getTimeFilterType() == FileFilter.TIME_TYPE_ALL_TIME) {
            List<MultiPbItemInfo> pbItemInfos = getList(fileList, null);
            int lastIndex = endIndex + 1;
            boolean isMore = lastIndex < fileTotalNum;
            MultiPbFileResult result = new MultiPbFileResult();
            result.setFileList(pbItemInfos);
            result.setLastIndex(lastIndex);
            result.setMore(isMore);
            return result;
        } else {
            List<MultiPbItemInfo> tempItemInfos = getList(fileList, fileFilter);
            List<MultiPbItemInfo> pbItemInfos = new LinkedList<>();
            if (tempItemInfos != null && tempItemInfos.size() > 0) {
                pbItemInfos.addAll(tempItemInfos);
            }
            int lastIndex = endIndex + 1;
            boolean isMore;
            while (pbItemInfos.size() < MAX_NUM && lastIndex < fileTotalNum) {
                if (fileList != null && fileList.size() > 0 && fileFilter.isLess(fileList.get(fileList.size() - 1))) {
                    break;
                }
                endIndex = MAX_NUM + lastIndex - 1;
                if (endIndex >= fileTotalNum) {
                    endIndex = fileTotalNum;
                }
                fileList = fileOperation.getFileList(ICatchFileType.ICH_FILE_TYPE_ALL, lastIndex, endIndex);
                tempItemInfos = getList(fileList, fileFilter);
                if (tempItemInfos != null && tempItemInfos.size() > 0) {
                    pbItemInfos.addAll(tempItemInfos);
                }
                lastIndex = endIndex + 1;

            }
            if (fileList != null && fileList.size() > 0 && fileFilter.isLess(fileList.get(fileList.size() - 1))) {
                isMore = false;
            } else {
                isMore = lastIndex < fileTotalNum;
            }
            MultiPbFileResult result = new MultiPbFileResult();
            result.setFileList(pbItemInfos);
            result.setLastIndex(lastIndex);
            result.setMore(isMore);
            return result;
        }
    }

    private List<MultiPbItemInfo> getList(List<ICatchFile> fileList, FileFilter fileFilter) {
        List<MultiPbItemInfo> multiPbItemInfoList = new LinkedList<>();
        if (fileList == null) {
            return null;
        }
        String fileDate;
        String fileSize;
        String fileTime;
        String fileDuration;
        boolean isPanorama;
        for (int ii = 0; ii < fileList.size(); ii++) {
            ICatchFile iCatchFile = fileList.get(ii);
            fileDate = ConvertTools.getTimeByfileDate(iCatchFile.getFileDate());
            fileSize = ConvertTools.ByteConversionGBMBKB(iCatchFile.getFileSize());;
            fileTime = ConvertTools.getDateTimeString(iCatchFile.getFileDate());
            fileDuration = ConvertTools.millisecondsToMinuteOrHours(iCatchFile.getFileDuration());
            isPanorama = PanoramaTools.isPanorama(iCatchFile.getFileWidth(), iCatchFile.getFileHeight());
            if (fileFilter != null) {
                if (fileFilter.isMatch(iCatchFile)) {
                    MultiPbItemInfo mGridItem = new MultiPbItemInfo(iCatchFile, 0,isPanorama ,fileSize,fileTime,fileDate,fileDuration);
                    multiPbItemInfoList.add(mGridItem);
                }
            } else {
                MultiPbItemInfo mGridItem = new MultiPbItemInfo(iCatchFile, 0, isPanorama,fileSize,fileTime,fileDate,fileDuration);
                multiPbItemInfoList.add(mGridItem);
            }
        }
        return multiPbItemInfoList;
    }


    public void setFileListAttribute(FileOperation fileOperation, FileType fileType) {
        if (!supportSetFileListAttribute) {
            return;
        }
        int filterFileType = ICatchCamListFileFilter.ICH_OFC_TYPE_IMAGE;
        if (fileType == FileType.FILE_PHOTO) {
            filterFileType = ICatchCamListFileFilter.ICH_OFC_TYPE_IMAGE;
        } else if (fileType == FileType.FILE_VIDEO) {
            filterFileType = ICatchCamListFileFilter.ICH_OFC_TYPE_VIDEO;
        } else if (fileType == FileType.FILE_EMERGENCY_VIDEO) {
            filterFileType = ICatchCamListFileFilter.ICH_OFC_TYPE_EMERGENCY_VIDEO;
        }
//        if (curFilterFileType != filterFileType) {
//            fileOperation.setFileListAttribute(filterFileType);
//            curFilterFileType = filterFileType;
//        } else {
//            AppLog.d(TAG, "Current is already fileType:" + fileType);
//        }
        if (fileFilter != null) {
            fileOperation.setFileListAttribute(filterFileType, fileFilter.getSensorType());
        } else {
            fileOperation.setFileListAttribute(filterFileType, ICatchCamListFileFilter.ICH_TAKEN_BY_ALL_SENSORS);
        }

    }

    public int getFileCount(FileOperation fileOperation, FileType fileType) {
        setFileListAttribute(fileOperation, fileType);
        int fileCount = fileOperation.getFileCount();
        AppLog.d(TAG, "fileCount:" + fileCount);
        return fileCount;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public FileFilter getFileFilter() {
        return fileFilter;
    }

    public void setLocalFileList(List<MultiPbItemInfo> pbItemInfoList, FileType fileType) {
        if(pbItemInfoList == null){
            return;
        }
        if (listHashMap.containsKey(fileType.ordinal())) {
            listHashMap.remove(fileType.ordinal());
        }
        List<MultiPbItemInfo> temp = new LinkedList<>();
        temp.addAll(pbItemInfoList);
        listHashMap.put(fileType.ordinal(), temp);
    }

    public List<MultiPbItemInfo> getLocalFileList(FileType fileType) {
        return listHashMap.get(fileType.ordinal());
    }

    public void clearFileList(FileType fileType) {
        if (listHashMap.containsKey(fileType.ordinal())) {
            listHashMap.remove(fileType.ordinal());
        }
    }

    public void remove(MultiPbItemInfo file, FileType fileType) {
        List<MultiPbItemInfo> multiPbItemInfos = getLocalFileList(fileType);
        if (multiPbItemInfos != null) {
            multiPbItemInfos.remove(file);
        }
    }

    public void clearAllFileList() {
        listHashMap.clear();
    }

    public boolean needFilter() {
        if (fileFilter != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean needFilterMoreFile(ICatchFile lastFile) {
        if (fileFilter != null) {
            return !fileFilter.isLess(lastFile);
        } else {
            return true;
        }
    }
}