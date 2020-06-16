package com.icatch.mobilecam.MyCamera;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.SdkApi.PanoramaControl;
import com.icatch.mobilecam.SdkApi.PanoramaPhotoPlayback;
import com.icatch.mobilecam.SdkApi.PanoramaVideoPlayback;
import com.icatchtek.control.customer.ICatchCameraPlayback;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.transport.ICatchINETTransport;
import com.icatchtek.reliant.customer.transport.ICatchITransport;

/**
 * Created by b.jiang on 2017/9/15.
 */

public class LocalSession {
    private final static String TAG = LocalSession.class.getSimpleName();
    private static LocalSession instance;
    private PanoramaPhotoPlayback panoramaPhotoPlayback;
    private PanoramaVideoPlayback panoramaVideoPlayback;
    private PanoramaControl panoramaControl;
    private PanoramaSession panoramaSession;
    private CommandSession commandSession;
    private ICatchCameraPlayback iCatchCameraPlayback;

    public static synchronized LocalSession getInstance() {
        if (instance == null) {
            instance = new LocalSession();
        }
        return instance;
    }

    private LocalSession() {

    }

    public boolean preparePanoramaSession() {
        boolean ret = false;
        panoramaSession = new PanoramaSession();
        ICatchITransport transport = new ICatchINETTransport("192.168.1.1");
        ret = panoramaSession.prepareSession(transport);
        if (ret) {
            initPanorama();
        }
        return ret;
    }

    public boolean destroyPanoramaSession() {
        boolean ret = false;
        AppLog.d(TAG, "begin destroyPanoramaSession");
        if (panoramaSession != null) {
            ret = panoramaSession.destroySession();
            panoramaSession = null;
        }
        AppLog.d(TAG, "end destroyPanoramaSession ret=" + ret);
        return ret;
    }

    void initPanorama() {
        panoramaVideoPlayback = new PanoramaVideoPlayback(panoramaSession.getSession());
        panoramaPhotoPlayback = new PanoramaPhotoPlayback(panoramaSession.getSession());
        panoramaControl = new PanoramaControl(panoramaSession.getSession());
    }

    public boolean prepareCommandSession() {
        boolean ret = false;
        commandSession = new CommandSession();
        ICatchITransport transport = new ICatchINETTransport("192.168.1.1");
        ret = commandSession.prepareSession(transport, false);
        if (ret) {
            initCommand();
        }
        return ret;
    }

    public boolean destroyCommandSession() {
        boolean ret = false;
        if (commandSession != null) {
            ret = commandSession.destroySession();
            commandSession = null;
        }
        return ret;
    }

    void initCommand() {
        try {
            iCatchCameraPlayback = commandSession.getSDKSession().getPlaybackClient();
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }
    }

    public PanoramaVideoPlayback getPanoramaVideoPlayback() {
        return panoramaVideoPlayback;
    }

    public PanoramaPhotoPlayback getPanoramaPhotoPlayback() {
        return panoramaPhotoPlayback;
    }

    public ICatchCameraPlayback getICatchCameraPlayback() {
        return iCatchCameraPlayback;
    }

    public PanoramaControl getPanoramaControl() {
        return panoramaControl;
    }
}
