package com.icatch.mobilecam.data.entity;

public class ItemInfo {
	public int uiStringInSetting = 0;
	public String uiStringInSettingString = null;
	public String uiStringInPreview = null;
	public int iconID = 0;
	public ItemInfo(int uiStringInSetting, String uiStringInPreview, int iconID) {
		this.uiStringInSetting = uiStringInSetting;
		this.uiStringInPreview = uiStringInPreview;
		this.iconID = iconID;
	}
	public ItemInfo(String uiStringInSetting, String uiStringInPreview, int iconID) {
		this.uiStringInSettingString = uiStringInSetting;
		this.uiStringInPreview = uiStringInPreview;
		this.iconID = iconID;
	}
}
