package com.icatch.mobilecam.Function.streaming;

import android.view.Surface;
import android.view.SurfaceHolder;

import com.icatch.mobilecam.Listener.VideoFramePtsChangedListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.SdkApi.PanoramaVideoPlayback;
import com.icatch.mobilecam.SdkApi.StreamProvider;
import com.icatch.mobilecam.data.Mode.PreviewLaunchMode;
import com.icatch.mobilecam.utils.PanoramaTools;
import com.icatchtek.pancam.customer.exception.IchGLFormatNotSupportedException;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceNotSetException;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.stream.ICatchIStreamProvider;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLSurfaceType;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchVideoFormat;

/**
 * Created by b.jiang on 2017/10/24.
 */

public class VideoStreaming {
    private final String TAG = VideoStreaming.class.getSimpleName();
    private PanoramaVideoPlayback videoPlayback;
    private ICatchIStreamProvider iCatchIStreamProvider;
    private StreamProvider streamProvider;
    private Surface surface;
    private SurfaceHolder holder;
    private boolean isStreaming = false;
    private boolean needRelease = true;
    private H264DecoderThread h264DecoderThread;
    private MjpgDecoderThread mjpgDecoderThread;
    private ICatchVideoFormat videoFormat;
    private int frmW = 0;
    private int frmH = 0;
    private int viewWidth = 0;
    private int viewHeigth = 0;
    private int previewCodec;
    private boolean enableRender;
    private ICatchSurfaceContext iCatchSurfaceContext;
    private VideoFramePtsChangedListener framePtsChangedListener = null;

    public VideoStreaming(PanoramaVideoPlayback videoPlayback) {
        this.videoPlayback = videoPlayback;
    }

    public  void  changePanoramaType(int var1){
        if (enableRender) {
            videoPlayback.changePanoramaType(var1);
        }
    }

    public void setFramePtsChangedListener(VideoFramePtsChangedListener framePtsChangedListener) {
        this.framePtsChangedListener = framePtsChangedListener;
    }

    public void initSurface(boolean enableRender,SurfaceHolder surfaceHolder,long videoWidth ,long videoHeigth) {
        this.enableRender = enableRender;
        this.surface = surfaceHolder.getSurface();
        this.holder = surfaceHolder;
        if (enableRender) {
            iCatchSurfaceContext = new ICatchSurfaceContext(surfaceHolder.getSurface());
            if(PanoramaTools.isPanorama(videoWidth,videoHeigth)){
                videoPlayback.enableGLRender();
                videoPlayback.initPancamGL(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
                videoPlayback.setSurface(ICatchGLSurfaceType.ICH_GL_SURFACE_TYPE_SPHERE, iCatchSurfaceContext);
            }else {
                videoPlayback.enableCommonRender(iCatchSurfaceContext);
            }
        }else {
            this.iCatchIStreamProvider = videoPlayback.disableRender();
            this.streamProvider = new StreamProvider(iCatchIStreamProvider);
        }
    }

    public void setViewParam(int w, int h) {
        viewWidth = w;
        viewHeigth = h;
        if(enableRender){
            setDrawingArea(w,h);
        }

    }

    public void setDrawingArea(int windowW, int windowH) {
        if (enableRender && iCatchSurfaceContext != null) {
            try {
                iCatchSurfaceContext.setViewPort(0, 0, windowW, windowH);
            } catch (IchGLSurfaceNotSetException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean play(ICatchFile iCatchFile, boolean disableAudio, boolean isRemote) throws IchGLFormatNotSupportedException {
        AppLog.d(TAG, "play enableRender=" + enableRender + " iCatchFile=" + iCatchFile + " disableAudio=" + disableAudio + " isRemote=" + isRemote);
        if (surface == null) {
            AppLog.e(TAG, "surface is not set");
            return false;
        }
        if (isStreaming) {
            AppLog.d(TAG, "apv streaming already started");
            return false;
        }
        boolean ret = videoPlayback.openVideoStream(iCatchFile, disableAudio, isRemote);
        AppLog.d(TAG, "start openVideoStream ret =" + ret);
        if (!ret) {
            return false;
        }
        ret = videoPlayback.resumePlayback();
        if (!ret) {
            return false;
        }
        isStreaming = true;
        needRelease = true;
        AppLog.d(TAG, "sdk start streamProvider OK");
        // init decoder
        if (enableRender) {
            return true;
        }
        int times = 0;
        frmW = 0;
        frmH = 0;
        while (frmW <= 0 || frmH <= 0) {
            if (times > 30) {
                break;
            }
            videoFormat = streamProvider.getVideoFormat();
            if (videoFormat != null) {
                frmW = videoFormat.getVideoW();
                frmH = videoFormat.getVideoH();
            }
            AppLog.e(TAG, "getVideoFormat frmW=" + frmW + " frmH=" + frmH);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            times++;
        }

        if (frmW <= 0 || frmH <= 0) {
//            stop();
            AppLog.e(TAG, "get video format err: frmW<0 or frmH < 0");
            return false;
        }
        startDecoderThread(PreviewLaunchMode.RT_PREVIEW_MODE, videoFormat);
        return true;
    }

    private void startDecoderThread(int previewLaunchMode, ICatchVideoFormat videoFormat) {
        AppLog.i(TAG, "start startDecoderThread videoFormat=" + videoFormat);
        if (videoFormat == null) {
            return;
        }
        boolean enableAudio = streamProvider.containsAudioStream();
        previewCodec = videoFormat.getCodec();
        AppLog.i(TAG, "start startDecoderThread previewCodec=" + previewCodec + " enableAudio=" + enableAudio);
        switch (previewCodec) {
            case ICatchCodec.ICH_CODEC_RGBA_8888:
                mjpgDecoderThread = new MjpgDecoderThread(streamProvider, holder, previewLaunchMode, viewWidth, viewHeigth);
                mjpgDecoderThread.setframePtsChangedListener(framePtsChangedListener);
                mjpgDecoderThread.start(enableAudio, true);
                setSurfaceViewArea();
                break;
            case ICatchCodec.ICH_CODEC_H264:
                h264DecoderThread = new H264DecoderThread(streamProvider, surface, previewLaunchMode);
                h264DecoderThread.setframePtsChangedListener(framePtsChangedListener);
                h264DecoderThread.start(enableAudio, true);
                setSurfaceViewArea();
                break;
            default:
                return;
        }
    }

    public boolean stop() {
        AppLog.d(TAG, "stopStreaming enableRender=" + enableRender + " isStreaming = " + isStreaming);
        AppLog.i(TAG, "stopMPreview preview");
        if (!enableRender) {
            if (mjpgDecoderThread != null) {
                mjpgDecoderThread.stop();
                AppLog.i(TAG, "start stopMPreview mjpgDecoderThread.isAlive() =" + mjpgDecoderThread.isAlive());
            }
            if (h264DecoderThread != null) {
                h264DecoderThread.stop();
                AppLog.i(TAG, "start stopMPreview h264DecoderThread.isAlive() =" + h264DecoderThread.isAlive());
            }
            AppLog.i(TAG, "end preview");
        }
        // stopPreview pv streaming
        if (!isStreaming) {
            AppLog.d(TAG, "pv streaming already stoped");
            return true;
        }
        videoPlayback.pausePlayback();
        boolean ret = videoPlayback.stop();
        isStreaming = false;
        return ret;
    }

    public boolean stopForSdRemove() {
        AppLog.d(TAG, "stopStreaming enableRender=" + enableRender + " isStreaming = " + isStreaming);
        AppLog.i(TAG, "stopMPreview preview");
        if (!enableRender) {
            if (mjpgDecoderThread != null) {
                mjpgDecoderThread.stop();
                AppLog.i(TAG, "start stopMPreview mjpgDecoderThread.isAlive() =" + mjpgDecoderThread.isAlive());
            }
            if (h264DecoderThread != null) {
                h264DecoderThread.stop();
                AppLog.i(TAG, "start stopMPreview h264DecoderThread.isAlive() =" + h264DecoderThread.isAlive());
            }
            AppLog.i(TAG, "end preview");
        }
        // stopPreview pv streaming
        if (!isStreaming) {
            AppLog.d(TAG, "pv streaming already stoped");
            return true;
        }
        boolean ret = videoPlayback.stop();
        isStreaming = false;
        return ret;
    }

    public boolean release() {
        AppLog.d(TAG, "pancamGLRelease enableRender=" + enableRender + " needRelease = " + needRelease);
        boolean ret = false;
        if (needRelease) {
            ret = videoPlayback.pancamGLRelease();
            needRelease = false;
        }
        return ret;
    }

    public void removeSurface(int iCatchSphereType) {
        if (iCatchSurfaceContext != null) {
            videoPlayback.removeSurface(iCatchSphereType, iCatchSurfaceContext);
            iCatchSurfaceContext = null;
        }
    }

    public boolean isStreaming() {
        AppLog.d(TAG, "get getStream: " + isStreaming);
        return isStreaming;
    }

    public void setSurfaceViewArea() {
        AppLog.e(TAG, "setSurfaceViewArea enableRender=" + enableRender + " viewWidth=" + viewWidth + " viewHeigth=" + viewHeigth + " frmW=" + frmW + " " +
                "frmH=" + frmH + " previewCodec=" + previewCodec);
        if (enableRender) {
            return;
        }
        if (viewWidth <= 0 || viewHeigth <= 0) {
            return;
        }
        if (holder == null) {
            return;
        }
        if (frmH <= 0 || frmW <= 0) {
            AppLog.e(TAG, "setSurfaceViewArea frmW or frmH <= 0!!!");
            holder.setFixedSize(viewWidth, viewWidth * 9 / 16);
            return;
        }
        if (previewCodec == ICatchCodec.ICH_CODEC_RGBA_8888) {
            if (mjpgDecoderThread != null) {
                mjpgDecoderThread.redrawBitmap(holder, viewWidth, viewHeigth);
            }
        } else if (previewCodec == ICatchCodec.ICH_CODEC_H264) {
            if (viewWidth * frmH / frmW <= viewHeigth) {
                AppLog.d(TAG, "setSurfaceViewArea setFixedSize 01 w=" + viewWidth + " h=" + (viewWidth * frmH / frmW));
                holder.setFixedSize(viewWidth, viewWidth * frmH / frmW);
            } else {
                AppLog.d(TAG, "setSurfaceViewArea setFixedSize 02 w=" + (viewHeigth * frmW / frmH) + " h=" + viewHeigth);
                holder.setFixedSize(viewHeigth * frmW / frmH, viewHeigth);
            }
        }
        AppLog.d(TAG, "end setSurfaceViewArea");
    }
}
