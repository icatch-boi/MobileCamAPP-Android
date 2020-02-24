package com.icatch.mobilecam.SdkApi;

import com.icatch.mobilecam.Log.AppLog;
import com.icatchtek.pancam.customer.ICatchIPancamVideoPlayback;
import com.icatchtek.pancam.customer.ICatchPancamConfig;
import com.icatchtek.pancam.customer.ICatchPancamSession;
import com.icatchtek.pancam.customer.exception.IchGLFormatNotSupportedException;
import com.icatchtek.pancam.customer.exception.IchGLNotInitedException;
import com.icatchtek.pancam.customer.exception.IchGLPanoramaTypeNotSupportedException;
import com.icatchtek.pancam.customer.gl.ICatchIPancamGL;
import com.icatchtek.pancam.customer.gl.ICatchIPancamGLTransform;
import com.icatchtek.pancam.customer.stream.ICatchIStreamProvider;
import com.icatchtek.pancam.customer.stream.ICatchIStreamStablization;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLPoint;
import com.icatchtek.reliant.customer.exception.IchDeprecatedException;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchNotSupportedException;
import com.icatchtek.reliant.customer.exception.IchPauseFailedException;
import com.icatchtek.reliant.customer.exception.IchResumeFailedException;
import com.icatchtek.reliant.customer.exception.IchSeekFailedException;
import com.icatchtek.reliant.customer.exception.IchStreamNotRunningException;
import com.icatchtek.reliant.customer.exception.IchTransportException;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchFile;

/**
 * Created by zhang yanhu C001012 on 2016/6/27 13:46.
 */
public class PanoramaVideoPlayback {
    private static final String TAG = PanoramaVideoPlayback.class.getSimpleName();
    private ICatchIPancamVideoPlayback videoPlayback;
    private ICatchIPancamGL pancamGL;
    private StreamStablization streamStablization = null;

    public PanoramaVideoPlayback(ICatchPancamSession iCatchPancamSession) {
        videoPlayback = iCatchPancamSession.getVideoPlayback();
        try {
            ICatchIStreamStablization iCatchIStreamStablization = videoPlayback.getStreamStablization();
            streamStablization = new StreamStablization(iCatchIStreamStablization);
        } catch (IchStreamNotRunningException e) {
            e.printStackTrace();
        } catch (IchNotSupportedException e) {
            e.printStackTrace();
        }
    }
    public StreamStablization getStreamStablization(){
            return streamStablization;
    }

    public void enableGLRender() {
        try {
            pancamGL = videoPlayback.enableGLRender(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
        } catch (Exception e) {
            AppLog.e(TAG, "Exception : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "enableGLRender pancamGL=" + pancamGL);
    }

    public boolean enableCommonRender(ICatchSurfaceContext iCatchSurfaceContext) {
        boolean ret = false;
        ICatchPancamConfig.getInstance().setOutputCodec(ICatchCodec.ICH_CODEC_JPEG, ICatchCodec.ICH_CODEC_YUV_NV12);
        try {
            ret = videoPlayback.enableRender(iCatchSurfaceContext);
        } catch (Exception e) {
            AppLog.e(TAG, "Exception : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        return ret;
    }


    public void enableGLRender(int var1) {
        AppLog.d(TAG, "begin enableGLRender var1=" + var1);
        try {
            pancamGL = videoPlayback.enableGLRender(var1);
        } catch (Exception e) {
            AppLog.e(TAG, "Exception : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "enableGLRender pancamGL=" + pancamGL);
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
            streamProvider = videoPlayback.disableRender();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        return streamProvider;
    }

    public boolean openVideoStream(ICatchFile iCatchFile, boolean disableAudio, boolean isRemote) throws IchGLFormatNotSupportedException {
        AppLog.d(TAG, "begin play iCatchFile=" + iCatchFile + " disableAudio=" + disableAudio + " isRemote=" + isRemote);
        boolean retValue = false;
        try {
            retValue = videoPlayback.play(iCatchFile, disableAudio, isRemote);
        } catch (Exception e) {
            AppLog.d(TAG, "Exception：" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end play retValue =" + retValue);
        return retValue;
    }

    public int getLength() {
        AppLog.d(TAG, "start getLength ");
        double videoLength = 0;
        try {
            videoLength = videoPlayback.getLength();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception：" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end getLength = " + videoLength);
        return (new Double(videoLength * 100).intValue());
    }

    public boolean stop() {
        AppLog.d(TAG, "start stop ");
        boolean retValue = false;
        try {
            retValue = videoPlayback.stop();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception：" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end stop retValue=" + retValue);
        return retValue;
    }

    public boolean resumePlayback() {
        AppLog.d(TAG, "start resume ");
        boolean ret = false;
        try {
            ret = videoPlayback.resume();
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        } catch (IchTransportException e) {
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            e.printStackTrace();
        } catch (IchResumeFailedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end resume ret=" + ret);
        return true;
    }

    public boolean pausePlayback() {
        AppLog.d(TAG, "start pausePlayback ");
        boolean ret = false;
        try {
            ret = videoPlayback.pause();
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        } catch (IchTransportException e) {
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            e.printStackTrace();
        } catch (IchPauseFailedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end pausePlayback ret=" + ret);
        return ret;
    }

    public boolean videoSeek(double position) {
        AppLog.d(TAG, "start videoSeek ");
        boolean ret = false;
        try {
            ret = videoPlayback.seek(position);
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        } catch (IchTransportException e) {
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            e.printStackTrace();
        } catch (IchSeekFailedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end videoSeek ret=" + ret);
        return ret;
    }

    public boolean initPancamGL(int varl) {
        AppLog.d(TAG, "start initPancamGL ");
        boolean ret = false;
        if (pancamGL == null) {
            AppLog.d(TAG, "pancamGL is null ");
            return false;
        }
        try {
            ret = pancamGL.init();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end initPancamGL ret=" + ret);
        return ret;

    }

    public boolean pancamGLRelease() {
        AppLog.d(TAG, "start pancamGLRelease");
        boolean ret = false;
        if (pancamGL == null) {
            AppLog.d(TAG, "pancamGL is null ");
            return false;
        }
        try {
            ret = pancamGL.release();
        } catch (Exception e) {
            AppLog.e(TAG, "Exception:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end pancamGLRelease ret=" + ret);
        return ret;
    }

    public boolean setSurface(int ichSurfaceIdSphere, ICatchSurfaceContext iCatchSurfaceContext) {
        AppLog.d(TAG, "start initSurface ");
        if (pancamGL == null) {
            AppLog.d(TAG, "pancamGL is null ");
            return false;
        }
        boolean ret = false;
        try {
            ret = pancamGL.setSurface(ichSurfaceIdSphere, iCatchSurfaceContext);
        } catch (Exception e) {
            AppLog.e(TAG, "Exception:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end initSurface ret=" + ret);
        return ret;
    }

    public boolean removeSurface(int iCatchSphereType, ICatchSurfaceContext iCatchSurfaceContext) {
        AppLog.d(TAG, "start removeSurface ");
        if (pancamGL == null) {
            AppLog.d(TAG, "pancamGL is null ");
            return false;
        }
        boolean ret = false;
        try {
            ret = pancamGL.removeSurface(iCatchSphereType, iCatchSurfaceContext);
        } catch (Exception e) {
            AppLog.e(TAG, "Exception:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end removeSurface ret=" + ret);
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

    public boolean setFormat(int type) {
//        if (pancamGL == null) {
//            return false;
//        }
//        boolean ret = false;
//        try {
//            ret = pancamGL.setFormat(type);
//        } catch (IchGLFormatNotSupportedException e) {
//            e.printStackTrace();
//        } catch (IchGLNotInitedException e) {
//            e.printStackTrace();
//        } catch (IchDeprecatedException e) {
//            e.printStackTrace();
//        }
//        return ret;

        return true;
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
}
