package com.icatch.mobilecam.SdkApi;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.data.type.Tristate;
import com.icatchtek.pancam.customer.ICatchIPancamPreview;
import com.icatchtek.pancam.customer.ICatchPancamConfig;
import com.icatchtek.pancam.customer.ICatchPancamSession;
import com.icatchtek.pancam.customer.exception.IchGLAlreadyInitedException;
import com.icatchtek.pancam.customer.exception.IchGLNotInitedException;
import com.icatchtek.pancam.customer.exception.IchGLPanoramaTypeNotSupportedException;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceAlreadySetException;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceNotSetException;
import com.icatchtek.pancam.customer.gl.ICatchIPancamGL;
import com.icatchtek.pancam.customer.gl.ICatchIPancamGLTransform;
import com.icatchtek.pancam.customer.stream.ICatchIStreamControl;
import com.icatchtek.pancam.customer.stream.ICatchIStreamProvider;
import com.icatchtek.pancam.customer.stream.ICatchIStreamPublish;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.type.ICatchGLCredential;
import com.icatchtek.pancam.customer.type.ICatchGLPoint;
import com.icatchtek.reliant.customer.exception.IchDeprecatedException;
import com.icatchtek.reliant.customer.exception.IchImageSizeNotSpecifiedException;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchMuxerAlreadyStartedException;
import com.icatchtek.reliant.customer.exception.IchMuxerNotStartedException;
import com.icatchtek.reliant.customer.exception.IchMuxerStartFailedException;
import com.icatchtek.reliant.customer.exception.IchNotSupportedException;
import com.icatchtek.reliant.customer.exception.IchStreamNotRunningException;
import com.icatchtek.reliant.customer.exception.IchStreamNotSupportException;
import com.icatchtek.reliant.customer.exception.IchTransportException;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchFrameBuffer;
import com.icatchtek.reliant.customer.type.ICatchImageSize;
import com.icatchtek.reliant.customer.type.ICatchStreamParam;

import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2016/8/26 13:46.
 */
public class PanoramaPreviewPlayback {
    private static final String TAG = PanoramaPreviewPlayback.class.getSimpleName();
    private ICatchIPancamPreview previewPlayback;
    private ICatchIPancamGL pancamGL = null;
    private List<ICatchImageSize> imageSizelist = null;
    private ICatchImageSize curImageSize = null;

    public PanoramaPreviewPlayback(ICatchPancamSession iCatchPancamSession) {
        previewPlayback = iCatchPancamSession.getPreview();
//        try {
//            pancamGL = previewPlayback.enableGLRender();
//        } catch (IchStreamAlreadyStartedException e) {
//            e.printStackTrace();
//        }
    }

    public boolean enableCommonRender(ICatchSurfaceContext iCatchSurfaceContext) {
        boolean ret = false;
//        ICatchPancamConfig.getInstance().setOutputCodec(ICatchCodec.ICH_CODEC_JPEG, ICatchCodec.ICH_CODEC_YUV_I420);
        ICatchPancamConfig.getInstance().setOutputCodec(ICatchCodec.ICH_CODEC_JPEG, ICatchCodec.ICH_CODEC_YUV_NV12);
        try {
            ret = previewPlayback.enableRender(iCatchSurfaceContext);
        } catch (Exception e) {
            AppLog.e(TAG, "Exception : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        return ret;
    }

    public boolean enableGLRender() {
        AppLog.d(TAG, "enableGLRender");
        ICatchPancamConfig.getInstance().setOutputCodec(ICatchCodec.ICH_CODEC_JPEG, ICatchCodec.ICH_CODEC_YUV_NV12);
        try {
            pancamGL = previewPlayback.enableGLRender();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end enableGLRender");
        return true;
    }

    public  boolean changePanoramaType(int panoramaType){
        AppLog.d(TAG, "start changePanoramaType panoramaType=" + panoramaType);
        boolean ret = false;
        if (pancamGL == null) {
            return false;
        }
        try {
            ret = pancamGL.changePanoramaType(panoramaType);
        } catch (IchGLPanoramaTypeNotSupportedException e) {
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        } catch (IchGLNotInitedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end changePanoramaType ret=" + ret);
        return ret;
    }

    public ICatchIStreamProvider disableRender() {
        ICatchIStreamProvider streamProvider = null;
        try {
            streamProvider = previewPlayback.disableRender();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        //默认输出codec为YUN_I420，需手动配置为RGBA
        ICatchPancamConfig.getInstance().setOutputCodec(ICatchCodec.ICH_CODEC_JPEG, ICatchCodec.ICH_CODEC_RGBA_8888);
        return streamProvider;
    }


    //    private ICatchIPancamGL getPancamGl() {
//        ICatchIPancamGL pancamGL = null;
//        try {
//            pancamGL = previewPlayback.enableGLRender();
//        } catch (IchStreamAlreadyStartedException e) {
//            AppLog.d(TAG, "enableGLRender IchStreamAlreadyStartedException");
//            e.printStackTrace();
//        }
//        return pancamGL;
//    }
    private ICatchIStreamControl getStreamControl() {
        ICatchIStreamControl streamControl = null;
        if (previewPlayback == null) {
            return null;
        }
        try {
            streamControl = previewPlayback.getStreamControl();
        } catch (IchStreamNotRunningException e) {
            AppLog.d(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        } catch (IchNotSupportedException e) {
            AppLog.d(TAG, "IchNotSupportedException");
            e.printStackTrace();
        }
        return streamControl;
    }

    private ICatchIStreamPublish getStreamPublish() {
        AppLog.d(TAG, "getStreamPublish");
        ICatchIStreamPublish streamPublish = null;
        if (previewPlayback == null) {
            return null;
        }
        try {
            streamPublish = previewPlayback.getStreamPublish();
        } catch (IchStreamNotRunningException e) {
            AppLog.d(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        } catch (IchNotSupportedException e) {
            AppLog.d(TAG, "IchNotSupportedException");
            e.printStackTrace();
        }
        return streamPublish;
    }

    public Tristate start(ICatchStreamParam var1, boolean enableAudio) {
        AppLog.d(TAG, "start Stream ICatchStreamParam=" + var1 + " enableAudio=" + enableAudio);
        Tristate retValue = Tristate.FALSE;
        boolean ret = false;
        try {
            ret = previewPlayback.start(var1, enableAudio);
        } catch (IchStreamNotSupportException e) {
            AppLog.d(TAG, "Exception : IchStreamNotSupportException");
            retValue = Tristate.ABNORMAL;
            e.printStackTrace();
        } catch (Exception e) {
            AppLog.d(TAG, "Exception : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        if (ret == true) {
            retValue = Tristate.NORMAL;
        }
        AppLog.d(TAG, "end Stream ret =" + ret);
        AppLog.d(TAG, "end Stream retValue =" + retValue);
        return retValue;
    }

    public boolean stop() {
        AppLog.d(TAG, "start stop ");
        boolean retValue = false;
        try {
            retValue = previewPlayback.stop();
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        } catch (IchTransportException e) {
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end stop retValue=" + retValue);
        return retValue;
    }

    public boolean init(int panoramaType) {
        AppLog.d(TAG, "start init ");
        if(pancamGL == null){
            return false;
        }
        boolean ret = false;
        try {
            ret = pancamGL.init(panoramaType);
        } catch (IchGLPanoramaTypeNotSupportedException e) {
            AppLog.d(TAG, "IchGLPanoramaTypeNotSupportedException");
            e.printStackTrace();
        } catch (IchGLAlreadyInitedException e) {
            AppLog.d(TAG, "IchGLAlreadyInitedException");
            e.printStackTrace();
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end init ret=" + ret);
        return ret;
    }

    public boolean release() {
        AppLog.d(TAG, "start pancamGLRelease ");
        if(pancamGL == null){
            return false;
        }
        boolean ret = false;
        try {
            ret = pancamGL.release();
        } catch (IchGLNotInitedException e) {
            e.printStackTrace();
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end pancamGLRelease ret=" + ret);
        return ret;
    }

    public boolean setSurface(int ichSurfaceIdSphere, ICatchSurfaceContext iCatchSurfaceContext) {
        AppLog.d(TAG, "start initSurface ");
        if(pancamGL == null){
            return false;
        }
        boolean ret = false;
        try {
            ret = pancamGL.setSurface(ichSurfaceIdSphere, iCatchSurfaceContext);
        } catch (IchGLNotInitedException e) {
            AppLog.d(TAG, "IchGLNotInitedException");
            e.printStackTrace();
        } catch (IchGLSurfaceAlreadySetException e) {
            AppLog.d(TAG, "IchGLSurfaceAlreadySetException");
            e.printStackTrace();
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end initSurface ret=" + ret);
        return ret;
    }

    public boolean removeSurface(int iCatchSphereType, ICatchSurfaceContext iCatchSurfaceContext) {
        AppLog.d(TAG, "start removeSurface ");
        if(pancamGL == null){
            return false;
        }
        boolean ret = false;
        try {
            ret = pancamGL.removeSurface(iCatchSphereType, iCatchSurfaceContext);
        } catch (IchGLNotInitedException e) {
            e.printStackTrace();
        } catch (IchGLSurfaceNotSetException e) {
            e.printStackTrace();
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end removeSurface ret=" + ret);
        return ret;
    }

    public boolean isStreamSupportPublish() {
        AppLog.d(TAG, "start isStreamSupportPublish ");
        boolean ret = false;
        ICatchIStreamPublish streamPublish = getStreamPublish();
        if (streamPublish == null) {
            AppLog.d(TAG, "streamPublish is null");
            return false;
        }
        try {
            ret = streamPublish.isStreamSupportPublish();
        } catch (IchStreamNotRunningException e) {
            AppLog.d(TAG, "IchGLStreamNotRunningException");
            e.printStackTrace();
        } catch (IchStreamNotSupportException e) {
            AppLog.d(TAG, "IchGLStreamNotSupportException");
            e.printStackTrace();
        }
        AppLog.d(TAG, "End isStreamSupportPublish=" + ret);
        return ret;
    }

    public boolean startPublishStreaming(String rtmpUrl) {
        AppLog.d(TAG, "start startPublishStreaming ");
        boolean ret = false;
        ICatchIStreamPublish streamPublish = getStreamPublish();
        if (streamPublish == null) {
            AppLog.d(TAG, "streamPublish is null");
            return false;
        }
        try {
            ret = streamPublish.startPublishStreaming(rtmpUrl);
        } catch (Exception e) {
            AppLog.d(TAG, "Exception e:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end startPublishStreaming ret=" + ret);
        return ret;
    }

    public boolean stopPublishStreaming() {
        AppLog.d(TAG, "start stopPublishStreaming ");
        boolean ret = false;
        ICatchIStreamPublish streamPublish = getStreamPublish();
        if (streamPublish == null) {
            AppLog.d(TAG, "streamPublish is null");
            return false;
        }
        try {
            ret = streamPublish.stopPublishStreaming();
        } catch (Exception e) {
            AppLog.d(TAG, "Exception e:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end stopPublishStreaming ret=" + ret);
        return ret;
    }

    private ICatchIPancamGLTransform getPancamGLTransform() {
        if (pancamGL == null) {
            return null;
        }
        ICatchIPancamGLTransform glTransform = null;
        try {
            glTransform = pancamGL.getPancamGLTransform();
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        }
        return glTransform;
    }


    public boolean locate(float var1) {
        ICatchIPancamGLTransform glTransform = getPancamGLTransform();
        if (glTransform == null) {
            return false;
        }
        boolean ret = false;
        try {
            ret = glTransform.locate(var1);
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean rotate(int var1, float var2, float var3, float var4, long var5) {
//        AppLog.d(TAG,"start rotate var1=" + var1 + " var2=" + var2 + " var3="+ var3 + " var4=" + var4+ " var5=" + var5);
        ICatchIPancamGLTransform glTransform = getPancamGLTransform();
        if (glTransform == null) {
            return false;
        }
        boolean ret = false;
        try {
            ret = glTransform.rotate(var1, var2, var3, var4, var5);
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        }
//        AppLog.d(TAG,"end rotate ret=" + ret);
        return ret;
    }

    public boolean rotate(ICatchGLPoint var1, ICatchGLPoint var2) {
//        AppLog.d(TAG,"start rotate var1=" + var1 + " var2=" + var2 );
        ICatchIPancamGLTransform glTransform = getPancamGLTransform();
        if (glTransform == null) {
            return false;
        }
        boolean ret = false;
        try {
            ret = glTransform.rotate(var1, var2);
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        }
//        AppLog.d(TAG,"end rotate ret=" + ret);
        return ret;
    }

    public boolean startMovieRecord(String var1, boolean var2) {
        //var2 false：表示录影到手机
        //true  录影到相机
        AppLog.d(TAG, "start startMovieRecord ");
        ICatchIStreamControl streamControl = getStreamControl();
        if (streamControl == null) {
            AppLog.d(TAG, "streamControl is null");
            return false;
        }
        boolean ret = false;
        try {
            ret = streamControl.startMovieRecord(var1, var2);
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            e.printStackTrace();
        } catch (IchTransportException e) {
            AppLog.e(TAG, "IchTransportException");
            e.printStackTrace();
        } catch (IchMuxerAlreadyStartedException e) {
            AppLog.e(TAG, "IchMuxerAlreadyStartedException");
            e.printStackTrace();
        } catch (IchMuxerStartFailedException e) {
            AppLog.e(TAG, "IchMuxerStartFailedException");
            e.printStackTrace();
        }
        AppLog.d(TAG, "End startMovieRecord ret=" + ret);
        return ret;
    }

    public boolean stopMovieRecord() {
        AppLog.d(TAG, "start stopMovieRecord ");
        ICatchIStreamControl streamControl = getStreamControl();
        if (streamControl == null) {
            AppLog.d(TAG, "streamControl is null");
            return false;
        }
        boolean ret = false;
        try {
            ret = streamControl.stopMovieRecord();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            e.printStackTrace();
        } catch (IchTransportException e) {
            AppLog.e(TAG, "IchTransportException");
            e.printStackTrace();
        } catch (IchMuxerNotStartedException e) {
            AppLog.e(TAG, "IchMuxerNotStartedException");
            e.printStackTrace();
        }
        AppLog.d(TAG, "End stopMovieRecord ret=" + ret);
        return ret;
    }

    public List<ICatchImageSize> getSupportedImageSize() {
        AppLog.d(TAG, "start getSupportedImageSize ");
        if (imageSizelist != null) {
            return imageSizelist;
        }
        ICatchIStreamControl streamControl = getStreamControl();
        if (streamControl == null) {
            AppLog.d(TAG, "streamControl is null");
            return null;
        }
        try {
            imageSizelist = streamControl.getSupportedImageSize();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        } catch (IchNotSupportedException e) {
            e.printStackTrace();
        } catch (IchTransportException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "End getSupportedImageSize list=" + imageSizelist);
        return imageSizelist;
    }

    public boolean setImageSize(ICatchImageSize var1) {
        AppLog.d(TAG, "start setImageSize var1=" + var1);
        boolean ret = false;
        ICatchIStreamControl streamControl = getStreamControl();
        if (streamControl == null) {
            AppLog.d(TAG, "streamControl is null");
            return false;
        }
        try {
            ret = streamControl.setImageSize(var1);
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        } catch (IchNotSupportedException e) {
            e.printStackTrace();
        } catch (IchTransportException e) {
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        }
        if (ret) {
            curImageSize = var1;
        }
        AppLog.d(TAG, "End setImageSize ret=" + ret);
        return ret;
    }

    public ICatchImageSize getCurImageSize() {
        AppLog.d(TAG, "start getCurImageSize curImageSize=" + curImageSize);
        return curImageSize;
    }

    public boolean snapImage(ICatchFrameBuffer var1, int var2) {
        AppLog.d(TAG, "start snapImage ");
        boolean ret = false;
        ICatchIStreamControl streamControl = getStreamControl();
        if (streamControl == null) {
            AppLog.d(TAG, "streamControl is null");
            return false;
        }
        try {
            ret = streamControl.snapImage(var1, var2);
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            e.printStackTrace();
        } catch (IchNotSupportedException e) {
            AppLog.e(TAG, "IchNotSupportedException");
            e.printStackTrace();
        } catch (IchTransportException e) {
            AppLog.e(TAG, "IchTransportException");
            e.printStackTrace();
        } catch (IchImageSizeNotSpecifiedException e) {
            AppLog.e(TAG, "IchImageSizeNotSpecifiedException");
            e.printStackTrace();
        }
        AppLog.d(TAG, "End snapImage ret=" + ret);
        return ret;
    }

    public String createChannel(ICatchGLCredential var1, String var2, String var3, boolean var4) {
        AppLog.d(TAG, "start createChannel ");
        String ret = "";
        ICatchIStreamPublish streamPublish = getStreamPublish();
        if (streamPublish == null) {
            AppLog.d(TAG, "streamPublish is null");
            return null;
        }
        ret = streamPublish.createChannel(var1, var2, var3, var4);
        AppLog.d(TAG, "End createChannel=" + ret);
        return ret;
    }

    public void deleteChannel() {
        AppLog.d(TAG, "start deleteChannel ");
        ICatchIStreamPublish streamPublish = getStreamPublish();
        if (streamPublish == null) {
            AppLog.d(TAG, "streamPublish is null");
            return;
        }
        streamPublish.deleteChannel();
        AppLog.d(TAG, "End deleteChannel");
    }

    public String startLive() {
        AppLog.d(TAG, "start startLive ");
        String ret = "";
        ICatchIStreamPublish streamPublish = getStreamPublish();
        if (streamPublish == null) {
            AppLog.d(TAG, "streamPublish is null");
            return null;
        }
        ret = streamPublish.startLive();
        AppLog.d(TAG, "End startLive=" + ret);
        return ret;
    }

    public void stopLive() {
        AppLog.d(TAG, "start stopLive");
        ICatchIStreamPublish streamPublish = getStreamPublish();
        if (streamPublish == null) {
            AppLog.d(TAG, "streamPublish is null");
            return;
        }
        streamPublish.stopLive();
        AppLog.d(TAG, "End stopLive");
    }
}
