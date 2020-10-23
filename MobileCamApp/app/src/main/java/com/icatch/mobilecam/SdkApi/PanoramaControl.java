package com.icatch.mobilecam.SdkApi;

import com.icatch.mobilecam.Log.AppLog;
import com.icatchtek.pancam.customer.ICatchIPancamControl;
import com.icatchtek.pancam.customer.ICatchIPancamListener;
import com.icatchtek.pancam.customer.ICatchPancamSession;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;
import com.icatchtek.reliant.customer.exception.IchListenerExistsException;
import com.icatchtek.reliant.customer.exception.IchListenerNotExistsException;

/**
 * Created by b.jiang on 2017/9/15.
 */

public class PanoramaControl {
    private ICatchIPancamControl iCatchIPancamControl;
    private String TAG = PanoramaControl.class.getSimpleName();
    public PanoramaControl(ICatchPancamSession iCatchPancamSession) {
        this.iCatchIPancamControl = iCatchPancamSession.getControl();

    }

   public void addEventListener(int var1, ICatchIPancamListener var2) {
        if (iCatchIPancamControl == null) {
            return;
        }
       AppLog.d(TAG,"addEventListener var1:" + var1);
        try {
            iCatchIPancamControl.addEventListener(var1, var2);
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.e(TAG,"addEventListener e:" + e.getClass().getSimpleName());
        }
       AppLog.d(TAG,"addEventListener ret:" + var1);
    }

    public void removeEventListener(int var1, ICatchIPancamListener var2) {
        if (iCatchIPancamControl == null) {
            return;
        }
        try {
            iCatchIPancamControl.removeEventListener(var1, var2);
        } catch (IchListenerNotExistsException e) {
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }

    }
}
