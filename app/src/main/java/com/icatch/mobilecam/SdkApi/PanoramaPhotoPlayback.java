package com.icatch.mobilecam.SdkApi;

import com.icatch.mobilecam.Log.AppLog;
import com.icatchtek.pancam.customer.ICatchIPancamImage;
import com.icatchtek.pancam.customer.ICatchPancamSession;
import com.icatchtek.pancam.customer.exception.IchGLAlreadyInitedException;
import com.icatchtek.pancam.customer.exception.IchGLNotInitedException;
import com.icatchtek.pancam.customer.exception.IchGLPanoramaTypeNotSupportedException;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceAlreadySetException;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceNotSetException;
import com.icatchtek.pancam.customer.gl.ICatchIPancamGL;
import com.icatchtek.pancam.customer.gl.ICatchIPancamGLTransform;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.type.ICatchGLImage;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLPoint;
import com.icatchtek.reliant.customer.exception.IchDeprecatedException;
import com.icatchtek.reliant.customer.exception.IchInvalidArgumentException;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchPermissionDeniedException;

/**
 * Created by zhang yanhu C001012 on 2016/6/27 13:46.
 */
public class PanoramaPhotoPlayback {
    private static final String TAG = PanoramaPhotoPlayback.class.getSimpleName();
    private ICatchIPancamImage photoPlayback;
    private ICatchIPancamGL pancamGL;

    public PanoramaPhotoPlayback(ICatchPancamSession iCatchPancamSession) {
        photoPlayback = iCatchPancamSession.getImage();
        try {
            pancamGL = photoPlayback.enableGLRender(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE );
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        } catch (IchPermissionDeniedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "photoPlayback = " + photoPlayback);
    }

//    ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE 1 全景
//    ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID 4 小行星
//    ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R 6 VR效果
    public ICatchIPancamGL enableGLRender(int panoramaType){
        AppLog.d(TAG,"start enableGLRender panoramaType:" + panoramaType);
        try {
            pancamGL = photoPlayback.enableGLRender(panoramaType);
        } catch (Exception e) {
            AppLog.e(TAG,"Exception e:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG,"end enableGLRender pancamGL:" + pancamGL);
        return pancamGL;
    }

    public ICatchIPancamImage getPhotoPlayback() {
        return photoPlayback;
    }

    public boolean clear() {
        AppLog.d(TAG, "start clear ");
        boolean retValue = false;
        retValue = photoPlayback.clear();
        AppLog.d(TAG, "end stop retValue =" + retValue);
        return retValue;
    }

    public boolean release() {
        AppLog.d(TAG, "start pancamGLRelease ");
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

    public boolean update(ICatchGLImage image) {
        AppLog.d(TAG, "start update ");
        boolean ret = false;
        try {
            ret = photoPlayback.update(image);
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end update ret=" + ret);
        return ret;
    }

    public boolean setSurface(int ichSurfaceIdSphere, ICatchSurfaceContext iCatchSurfaceContext) {
        AppLog.d(TAG, "start setSurface ");
        boolean ret = false;
        if (pancamGL == null) {
            return false;
        }
        try {
            ret = pancamGL.setSurface(ichSurfaceIdSphere, iCatchSurfaceContext);
        } catch (IchGLNotInitedException e) {
            e.printStackTrace();
        } catch (IchGLSurfaceAlreadySetException e) {
            e.printStackTrace();
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end setSurface ret=" + ret);
        return ret;
    }

    public boolean removeSurface(int iCatchSphereType, ICatchSurfaceContext iCatchSurfaceContext) {
        AppLog.d(TAG, "start removeSurface ");
        boolean ret = false;
        if (pancamGL == null) {
            return false;
        }
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

    public ICatchIPancamGL getPancamGL() {
        return pancamGL;
    }

    public  boolean changePanoramaType(int panoramaType){
        AppLog.d(TAG, "start changePanoramaType");
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

    public boolean pancamGLInit() {
        AppLog.d(TAG, "start pancamGLInit");
        boolean ret = false;
        if (pancamGL == null) {
            return false;
        }
        try {
            ret = pancamGL.init();
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        } catch (IchGLPanoramaTypeNotSupportedException e) {
            e.printStackTrace();
        } catch (IchGLAlreadyInitedException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end pancamGLInit ret=" + ret);
        return ret;
    }

    public boolean pancamGLSetFormat(int sphereType, int width, int height) {
        AppLog.d(TAG, "start pancamGLSetFormat type=" + sphereType + " width=" + width + " height=" + height);
        boolean ret = false;
        if (pancamGL == null) {
            return false;
        }
        try {
            ret = pancamGL.setFormat(sphereType, width, height);
        } catch (Exception e) {
            AppLog.d(TAG, "end pancamGLSetFormat Exception e:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end pancamGLSetFormat ret=" + ret);
        return ret;
    }

    public boolean pancamGLClearFormat() {
        AppLog.d(TAG, "start pancamGLClearFormat");
        boolean ret = false;
        if (pancamGL == null) {
            return false;
        }
        try {
            ret = pancamGL.clearFormat();
        } catch (Exception e) {
            AppLog.d(TAG, "end pancamGLClearFormat Exception e:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        AppLog.d(TAG, "end pancamGLClearFormat ret=" + ret);
        return ret;
    }

    private ICatchIPancamGLTransform getPancamGLTransform() {
//        AppLog.d(TAG, "start getPancamGLTransform");
        if (pancamGL == null) {
            return null;
        }
        ICatchIPancamGLTransform glTransform = null;
        try {
            glTransform = pancamGL.getPancamGLTransform();
        } catch (IchDeprecatedException e) {
            AppLog.d(TAG, "Exception:" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
//        AppLog.d(TAG, "end getPancamGLTransform glTransform=" + glTransform);
        return glTransform;
    }

    //通过陀螺仪旋转角度
    public boolean pancamGLTransformRotate(int var1, float var2, float var3, float var4, long var5) {
//        AppLog.d(TAG, "start pancamGLTransformRotate ");
        boolean ret = false;
        ICatchIPancamGLTransform glTransform = getPancamGLTransform();
        if (glTransform == null) {
            return false;
        }
        try {
            ret = glTransform.rotate(var1, var2, var3, var4, var5);
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        }
//        AppLog.d(TAG, "end pancamGLTransformRotate ret=" + ret);
        return ret;
    }

    //拖动时调整视角
    public boolean pancamGLTransformRotate(ICatchGLPoint var1, ICatchGLPoint var2) {
//        AppLog.d(TAG, "start pancamGLTransformRotate 02");
        boolean ret = false;
        ICatchIPancamGLTransform glTransform = getPancamGLTransform();
        if (glTransform == null) {
            return false;
        }
        try {
            ret = glTransform.rotate(var1, var2);
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        }
//        AppLog.d(TAG, "end pancamGLTransformRotate 02 ret=" + ret);
        return ret;
    }

    //缩放
    public boolean pancamGLTransLocate(float var1) {
        AppLog.d(TAG, "start pancamGLTransLocate ");
        boolean ret = false;
        ICatchIPancamGLTransform glTransform = getPancamGLTransform();
        if (glTransform == null) {
            return false;
        }
        try {
            ret = glTransform.locate(var1);
        } catch (IchDeprecatedException e) {
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "end pancamGLTransLocate ret=" + ret);
        return ret;
    }
}
