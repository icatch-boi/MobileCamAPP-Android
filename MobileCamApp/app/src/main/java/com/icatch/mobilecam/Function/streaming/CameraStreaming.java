package com.icatch.mobilecam.Function.streaming;

import android.media.MediaCodec;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.SdkApi.PanoramaPreviewPlayback;
import com.icatch.mobilecam.SdkApi.StreamProvider;
import com.icatch.mobilecam.data.Mode.PreviewLaunchMode;
import com.icatch.mobilecam.data.type.Tristate;
import com.icatchtek.pancam.customer.stream.ICatchIStreamProvider;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchStreamParam;
import com.icatchtek.reliant.customer.type.ICatchVideoFormat;

/**
 * Created by b.jiang on 2017/9/21.
 */

public class CameraStreaming {
    private final String TAG = CameraStreaming.class.getSimpleName();
    private PanoramaPreviewPlayback previewPlayback;
    private ICatchIStreamProvider iCatchIStreamProvider;
    private StreamProvider streamProvider;
    private Surface surface;
    private SurfaceHolder holder;
    private MediaCodec decoder;
    private boolean isStreaming = false;
    private boolean freezeDecoder = false;
    private H264DecoderThread h264DecoderThread;
    private MjpgDecoderThread mjpgDecoderThread;
    private ICatchVideoFormat videoFormat;
    private int frmW = 0;
    private int frmH = 0;
    private int viewWidth = 0;
    private int viewHeigth = 0;
    private int previewCodec;

//    private ICameraObserver frameLoadingObserver = null;
//    private FramePtsChangedListener framePtsChangedListener;

    public CameraStreaming(PanoramaPreviewPlayback previewPlayback) {
        this.previewPlayback = previewPlayback;
    }

    public void setSurface(SurfaceHolder holder) {
        this.surface = holder.getSurface();
        this.holder = holder;
        AppLog.d(TAG, "initSurface: " + surface);
    }

    public void setViewParam(int w, int h) {
        viewWidth = w;
        viewHeigth = h;
    }

    public void disnableRender() {
        this.iCatchIStreamProvider = previewPlayback.disableRender();
        this.streamProvider = new StreamProvider(iCatchIStreamProvider);
    }
//    public void setFrameLoadingObserver(ICameraObserver observer) {
//        this.frameLoadingObserver = observer;
//    }

    //    public void setFramePtsChangedListener(FramePtsChangedListener framePtsChangedListener) {
//        this.framePtsChangedListener = framePtsChangedListener;
//    }

    public Tristate start(ICatchStreamParam param, boolean enableAudio) {
        AppLog.d(TAG, "startStreaming, enableAudio: " + enableAudio);
        if (surface == null) {
            AppLog.e(TAG, "surface is not set");
            return Tristate.FALSE;
        }
        if (isStreaming) {
            AppLog.d(TAG, "apv streaming already started");
            return Tristate.NORMAL;
        }
        Tristate ret = previewPlayback.start(param, enableAudio);
        AppLog.d(TAG, "sdk start streamProvider ret =" + ret);
        if (ret != Tristate.NORMAL) {
            return ret;
        }
        AppLog.d(TAG, "sdk start streamProvider OK");
        // init decoder
        try {
            videoFormat = streamProvider.getVideoFormat();
            if (videoFormat != null) {
                frmW = videoFormat.getVideoW();
                frmH = videoFormat.getVideoH();
            }
            startDecoderThread(PreviewLaunchMode.RT_PREVIEW_MODE, videoFormat);
        } catch (Exception e) {
            AppLog.e(TAG, "get video format err: " + e.getMessage());
            return Tristate.FALSE;
        }
        return Tristate.NORMAL;
    }

    private void startDecoderThread(int previewLaunchMode, ICatchVideoFormat videoFormat) {
        AppLog.i(TAG, "start startDecoderThread");
        if (videoFormat == null) {
            AppLog.i(TAG, "start startDecoderThread videoFormat is null");
            return;
        }
        AppLog.i(TAG, "start startDecoderThread videoFormat:" + videoFormat.toString());
        boolean enableAudio = streamProvider.containsAudioStream();
        previewCodec = videoFormat.getCodec();
        AppLog.i(TAG, "start startDecoderThread previewCodec=" + previewCodec + " enableAudio=" + enableAudio);
        switch (previewCodec) {
            case ICatchCodec.ICH_CODEC_RGBA_8888:
                mjpgDecoderThread = new MjpgDecoderThread(streamProvider, holder, previewLaunchMode, viewWidth, viewHeigth);
                mjpgDecoderThread.start(enableAudio, true);
                setSurfaceViewArea();
                break;
            case ICatchCodec.ICH_CODEC_H264:
                h264DecoderThread = new H264DecoderThread(streamProvider, surface, previewLaunchMode);
                h264DecoderThread.start(enableAudio, true);
                setSurfaceViewArea();
                break;
            default:
                return;
        }
    }

    public boolean stop() {
        AppLog.d(TAG, "stopStreaming isStreaming = " + isStreaming);
        AppLog.i(TAG, "stopMPreview preview");
        if (mjpgDecoderThread != null) {
            mjpgDecoderThread.stop();
            AppLog.i(TAG, "start stopMPreview mjpgDecoderThread.isAlive() =" + mjpgDecoderThread.isAlive());
        }
        if (h264DecoderThread != null) {
            h264DecoderThread.stop();
            AppLog.i(TAG, "start stopMPreview h264DecoderThread.isAlive() =" + h264DecoderThread.isAlive());
        }
        AppLog.i(TAG, "end preview");

        // stopPreview pv streaming
        boolean ret = previewPlayback.stop();
        isStreaming = false;
        //isStreaming = false;
        return ret;
    }

    public boolean isStreaming() {
        AppLog.d(TAG, "get getStream: " + isStreaming);
        return isStreaming;
    }

    public void setSurfaceViewArea() {
        AppLog.e(TAG, "setSurfaceViewArea viewWidth=" + viewWidth + " viewHeigth=" + viewHeigth + " frmW=" + frmW + " " +
                "frmH=" + frmH + " previewCodec=" + previewCodec);
        if (viewWidth <= 0 || viewHeigth <= 0) {
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
                holder.setFixedSize(viewWidth, viewWidth * frmH / frmW);
            } else {
                holder.setFixedSize(viewHeigth * frmW / frmH, viewHeigth);
            }
        }
        AppLog.d(TAG, "end setSurfaceViewArea");
    }
}
