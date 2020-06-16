package com.icatch.mobilecam.utils;

import android.graphics.Rect;

public class ScaleTool {
	public static Rect getScaledPosition(int frmW, int frmH, int wndW, int wndH) {
		int rectLeft = 0;
		int rectRigth = 0;
		int rectTop = 0;
		int rectBottom = 0;
		Rect rect = new Rect();

		if (wndW * frmH < wndH * frmW) {
			// full filled with width
			rectLeft = 0;
			rectRigth = wndW;
			rectTop = (wndH - wndW * frmH / frmW) / 2;
			rectBottom = wndH - rectTop;

		} else if (wndW * frmH > wndH * frmW) {
			// full filled with height
			rectLeft = (wndW - wndH * frmW / frmH) / 2;
			rectRigth = wndW - rectLeft;
			rectTop = 0;
			rectBottom = wndH;
		} else {
			// full filled with width and height
			rectLeft = 0;
			rectRigth = wndW;
			rectTop = 0;
			rectBottom = wndH;
		}

		rect = new Rect(rectLeft, rectTop, rectRigth, rectBottom);
		return rect;
	}
}
