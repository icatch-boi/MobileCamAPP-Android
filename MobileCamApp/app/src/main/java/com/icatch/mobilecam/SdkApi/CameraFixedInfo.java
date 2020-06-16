/**
 * Added by zhangyanhu C01012,2014-6-27
 */
package com.icatch.mobilecam.SdkApi;

import com.icatch.mobilecam.Log.AppLog;
import com.icatchtek.control.customer.ICatchCameraInfo;
import com.icatchtek.reliant.customer.exception.IchInvalidSessionException;

/**
 * Added by zhangyanhu C01012,2014-6-27
 */
public class CameraFixedInfo {
	private final String tag = "CameraFixedInfo";
	private ICatchCameraInfo cameraInfo;

	public CameraFixedInfo(ICatchCameraInfo cameraInfo) {
		this.cameraInfo = cameraInfo;
	}

	public String getCameraName() {
		AppLog.i(tag, "begin getCameraName");
		String name = "";
		try {
			name = cameraInfo.getCameraProductName();
		} catch (IchInvalidSessionException e) {
			e.printStackTrace();
		}
		AppLog.i(tag, "end getCameraName name =" + name);
		return name;
	}

	public String getCameraVersion() {
		AppLog.i(tag, "begin getCameraVersion");
		String version = "";
		try {
			version = cameraInfo.getCameraFWVersion();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end getCameraVersion version =" + version);
		return version;
	}
}
