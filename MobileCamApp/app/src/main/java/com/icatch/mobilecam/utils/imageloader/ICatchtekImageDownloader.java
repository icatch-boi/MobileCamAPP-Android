package com.icatch.mobilecam.utils.imageloader;

import android.content.Context;
import android.graphics.Bitmap;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraManager;
import com.icatch.mobilecam.SdkApi.FileOperation;
import com.icatch.mobilecam.utils.BitmapTools;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFrameBuffer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sha.liu on 2018/4/12.
 */

public class ICatchtekImageDownloader extends BaseImageDownloader {
    private static String TAG = ICatchtekImageDownloader.class.getSimpleName();
    private static final int HTTP_SOCKET_TIMEOUT_MS = 10000;

    public ICatchtekImageDownloader(Context context) {
        super(context);
    }

    public ICatchtekImageDownloader(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
    }

    @Override
    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        if (TutkUriUtil.isTutkUri(imageUri)) {
            return getStreamFromTUTK(imageUri, extra);
        }
        return super.getStreamFromOtherSource(imageUri, extra);
    }

    private InputStream getStreamFromTUTK(String imageUri, Object extra) throws IOException {
        AppLog.d(TAG, "getStreamFromTUTK imageUri:" + imageUri);
        ICatchFile file = TutkUriUtil.getInfoOfUri(imageUri);
        if (file == null) {
            return null;
        }
        FileOperation fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();
        if (fileOperation == null) {
            return null;
        }
        if (TutkUriUtil.isThumbnailUri(imageUri)) {
            ICatchFrameBuffer buffer = fileOperation.getThumbnail(file);
            if (buffer != null) {
                Bitmap bitmap = BitmapTools.decodeByteArray(buffer.getBuffer(), 200, 200);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                bitmap.recycle();
                byte[] thumbnail = baos.toByteArray();
                if (thumbnail != null && thumbnail.length > 0) {
                    return new ByteArrayInputStream(thumbnail);
                }
            }
        } else if (TutkUriUtil.isOriginalUri(imageUri)) {
            ICatchFrameBuffer buffer = fileOperation.getQuickview(file);
            if (buffer == null || buffer.getFrameSize() <= 0) {
                AppLog.e(TAG, "buffer == null  send _LOAD_BITMAP_FAILED 01");
                buffer = fileOperation.downloadFile(file);
            }
            if (buffer != null) {
                int datalength = buffer.getFrameSize();
                if (datalength > 0) {
                    byte[] thumbnail = buffer.getBuffer();
                    if (thumbnail != null) {
                        return new ByteArrayInputStream(thumbnail);
                    }
                }
            }
        }
        return null;
    }
}
