package com.icatch.mobilecam.utils.imageloader;

import com.icatchtek.reliant.customer.type.ICatchFile;

import java.util.Locale;

/**
 * @author b.jiang
 * @date 2020/1/16
 * @description
 */
public class TutkUriUtil {
    private static final String URI_PREFIX = "tutk://";

    public TutkUriUtil() {
    }

    public static boolean isTutkUri(String uri) {
        return uri != null && uri.length() != 0 ? belongsTo(uri) : false;
    }
    //filehandle会变化，需要移除fileHandle
    public static String getKey(String uri){
        String infoStr = crop(uri);
        int beginIndex = infoStr.indexOf("fileName");
        String temp = infoStr.substring(beginIndex);
        return URI_PREFIX + temp;
    }


    public static String getTutkOriginalUri(ICatchFile iCatchFile) {

        // int fileType, String fileName, long fileSize
        String fileInfoStr = "fileHandle=" + iCatchFile.getFileHandle()  + "&fileName=" + iCatchFile.getFileName() + "&fileSize=" + iCatchFile.getFileSize() + "&original";
        return URI_PREFIX + fileInfoStr;
    }

    public static boolean isOriginalUri(String uri){
        return  (uri != null && uri.contains("original"));
    }

    public static String getTutkThumbnailUri(ICatchFile iCatchFile) {

        // int fileType, String fileName, long fileSize
        String fileInfoStr = "fileHandle=" + iCatchFile.getFileHandle()  + "&fileName=" + iCatchFile.getFileName() + "&fileSize=" + iCatchFile.getFileSize() + "&thumbnail";
        return URI_PREFIX + fileInfoStr;
    }

    public static boolean isThumbnailUri(String uri){
        return  (uri != null && uri.contains("thumbnail"));
    }

    private static boolean belongsTo(String uri) {
        return uri.toLowerCase(Locale.US).startsWith(URI_PREFIX);
    }

    public static String crop(String uri) {
        if (!belongsTo(uri)) {
            throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, URI_PREFIX));
        } else {
            return uri.substring(URI_PREFIX.length());
        }
    }

    public static ICatchFile getInfoOfUri(String uri) {
        String infoStr = crop(uri);
        return getICatchFile(infoStr);
    }

    private static ICatchFile getICatchFile(String infoStr) {
        String[] fileAttrs = infoStr.split("&");
        if (fileAttrs != null && fileAttrs.length >= 3) {
            String[] keyVal1 = fileAttrs[0].split("=");
            if (keyVal1.length == 2) {
                ICatchFile iCatchFile = new ICatchFile(Integer.valueOf(keyVal1[1]));
                return iCatchFile;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
