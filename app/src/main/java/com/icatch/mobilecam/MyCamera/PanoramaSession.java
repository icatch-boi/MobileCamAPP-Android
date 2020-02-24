package com.icatch.mobilecam.MyCamera;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.icatch.mobilecam.Log.AppLog;
import com.icatchtek.pancam.customer.ICatchPancamSession;
import com.icatchtek.pancam.customer.type.ICatchGLColor;
import com.icatchtek.pancam.customer.type.ICatchGLDisplayPPI;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchTransportException;
import com.icatchtek.reliant.customer.transport.ICatchITransport;

/**
 * Created by zhang yanhu C001012 on 2016/6/22 11:41.
 */
public class PanoramaSession {
    private static final String TAG = PanoramaSession.class.getSimpleName();
    private ICatchPancamSession iCatchPancamSession;

    public boolean prepareSession(ICatchITransport transport) {
        boolean ret = false;
        iCatchPancamSession = ICatchPancamSession.createSession();
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        ICatchGLDisplayPPI displayPPI = new ICatchGLDisplayPPI(displayMetrics.xdpi, displayMetrics.ydpi);
        try {
            ret = iCatchPancamSession.prepareSession(transport, ICatchGLColor.BLACK, displayPPI);
        } catch (IchTransportException e) {
            e.printStackTrace();
        }
        AppLog.d(TAG, "ICatchPancamSession preparePanoramaSession ret=" + ret);
        return ret;
    }

    public ICatchPancamSession getSession() {
        return iCatchPancamSession;
    }

    public boolean destroySession() {
        boolean ret = false;
        if (iCatchPancamSession != null) {
            try {
                ret = iCatchPancamSession.destroySession();
            } catch (IchInvalidSessionException e) {
                e.printStackTrace();
            }
            AppLog.d(TAG, "ICatchPancamSession destroyPanoramaSession ret=" + ret);
        }
        return ret;
    }
}
