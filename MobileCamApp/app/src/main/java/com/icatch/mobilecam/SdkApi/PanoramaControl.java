package com.icatch.mobilecam.SdkApi;

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

    public PanoramaControl(ICatchPancamSession iCatchPancamSession) {
        this.iCatchIPancamControl = iCatchPancamSession.getControl();

    }

   public void addEventListener(int var1, ICatchIPancamListener var2) {
        if (iCatchIPancamControl == null) {
            return;
        }
        try {
            iCatchIPancamControl.addEventListener(var1, var2);
        } catch (IchListenerExistsException e) {
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }
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
