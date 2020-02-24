package com.icatch.mobilecam.Function.USB;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class USBHost_Feature {
    public static final int USB_VENDOR_ID = 0x2AAD;
    public static final int USB_PRODUCT_ID = 0x6353;

    public static final int USB_UAC_FREQUENCY = 48000;
    public static final int USB_UAC_SAMPLEBIT = 16;
    public static final int USB_UAC_NCHANNELS = 2;

    public static final int HANDLER_USB_DEVICE_ATTACHED_ID = 1;
    public static final String HANDLER_USB_DEVICE_ATTACHED_KEY = "HANDLER_USB_DEVICE_ATTACHED";

    public static final int HANDLER_USB_DEVICE_DETACHED_ID = 2;
    public static final String HANDLER_USB_DEVICE_DETACHED_KEY = "HANDLER_USB_DEVICE_DETACHED";

    private static final String ACTION_USB_PERMISSION_BASE = "com.example.USB_PERMISSION.";
    private final String ACTION_USB_PERMISSION = ACTION_USB_PERMISSION_BASE + hashCode();

    private int mVendorID;
    private int mProductID;

//    private Handler handler;
    private Context context;
    private PendingIntent mPermissionIntent;

    private UsbDevice usbDevice;
    private UsbManager usbManager;
    private UsbDeviceConnection usbDeviceConnection;

    public USBHost_Feature(Context context) {
        this.mVendorID = USB_VENDOR_ID;
        this.mProductID = USB_PRODUCT_ID;

//        Toast.makeText(context, String.format(Locale.getDefault(),
//                "UsbDevice use default [Vendor:0x%04x, Product:0x%04x]", this.mVendorID, this.mProductID),
//                Toast.LENGTH_SHORT).show();

        this.context = context;
//        this.handler = handler;
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public void setUsbDevice(int vendorID, int productID) {
        this.mVendorID = vendorID;
        this.mProductID = productID;
//        Toast.makeText(context, String.format(Locale.getDefault(),
//                "UsbDevice changed to [Vendor:0x%04x, Product:0x%04x]", this.mVendorID, this.mProductID),
//                Toast.LENGTH_SHORT).show();
    }

    public boolean hasPermission() {
        usbDevice = getSpecifiedDevice(mVendorID, mProductID);
        return usbDevice != null && usbManager.hasPermission(usbDevice);
    }

    public void requestPermission() {
        usbDevice = getSpecifiedDevice(mVendorID, mProductID);
        if (usbDevice == null) {
//            Toast.makeText(this.context,
//                    String.format(Locale.getDefault(), "No matching Device [VID: %04X, PID: %04X] found.",
//                    this.mVendorID, this.mProductID), Toast.LENGTH_SHORT).show();

            return;
        }

        if (hasPermission()) {
            Log.i("__lib_uvc__", "has permission");
//            Toast.makeText(this.context,
//                    String.format(Locale.getDefault(), "Device [VID: %04X, PID: %04X] already has permission.",
//                            this.mVendorID, this.mProductID), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i("__lib_uvc__", "request permission");
//        Toast.makeText(this.context,
//                String.format(Locale.getDefault(), "Device [VID: %04X, PID: %04X] request permission now.",
//                        this.mVendorID, this.mProductID), Toast.LENGTH_SHORT).show();
        usbManager.requestPermission(usbDevice, mPermissionIntent);
    }

    public UsbDevice getUsbDevice() {
        usbDevice = getSpecifiedDevice(mVendorID, mProductID);
        if (usbDevice == null) {
            return null;
        }
        return this.usbDevice;
    }

    public int getProductID() {
        return mProductID;
    }

    public int getVendorID() {
        return mVendorID;
    }

    public int getFileDescriptor() {
        usbDeviceConnection = getUsbDeviceConnection();
        if (usbDeviceConnection == null) {
            return -1;
        }
        return usbDeviceConnection.getFileDescriptor();
    }


    public UsbDeviceConnection getUsbDeviceConnection() {
        if (usbDeviceConnection == null) {
            usbDevice = getSpecifiedDevice(mVendorID, mProductID);
            if (usbDevice == null) {
                return null;
            }
            usbDeviceConnection = usbManager.openDevice(usbDevice);
        }
        return usbDeviceConnection;
    }

    public void register() {
        if (mPermissionIntent == null) {
            mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION),PendingIntent.FLAG_ONE_SHOT);
            final IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            context.registerReceiver(usbReceiver, filter);
        }
    }

    public void unregister() {
        if (mPermissionIntent != null) {
            context.unregisterReceiver(usbReceiver);
            mPermissionIntent = null;
        }
    }

    public static String generateNameForUsbDevice(UsbDevice usbDevice) {
        if (usbDevice == null) {
            return "";
        }
        return generateNameForUsbDevice(usbDevice.getVendorId(), usbDevice.getProductId());
    }

    public static String generateNameForUsbDevice(int vendorID, int productID) {
        return String.format(Locale.getDefault(),
                "USB[VID:0x%04x PID:0x%04x]", vendorID, productID);
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {

            final String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                final UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    Log.i("__lib_uvc__", "ACTION_USB_PERMISSION, USBDevice: " + device);
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                final UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    Message message = new Message();
                    message.what = HANDLER_USB_DEVICE_ATTACHED_ID;
                    Bundle data = new Bundle();
                    data.putString(HANDLER_USB_DEVICE_ATTACHED_KEY, generateNameForUsbDevice(device));
                    message.setData(data);
//                    handler.sendMessage(message);
                    Log.i("__lib_uvc__", "ACTION_USB_DEVICE_ATTACHED, USBDevice: " + device);
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                final UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    Message message = new Message();
                    message.what = HANDLER_USB_DEVICE_DETACHED_ID;
                    Bundle data = new Bundle();
                    data.putString(HANDLER_USB_DEVICE_DETACHED_KEY, generateNameForUsbDevice(device));
                    message.setData(data);
//                    handler.sendMessage(message);
                    Log.i("__lib_uvc__", "ACTION_USB_DEVICE_DETACHED, USBDevice: " + device);
                }
            }
        }
    };

    private UsbDevice getSpecifiedDevice(int mVendorId, int mProductId) {

        final HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        if (deviceList != null) {
            final Iterator<UsbDevice> iterator = deviceList.values().iterator();
            UsbDevice device;
            while (iterator.hasNext()) {
                device = iterator.next();
                Log.i("__lib_uvc__", "getDeviceList, USBDevice: " + device.getDeviceName());
                Log.i("__lib_uvc__", "getDeviceList, USBDevice: " + device.getVendorId());
                Log.i("__lib_uvc__", "getDeviceList, USBDevice: " + device.getProductId());
//                Toast.makeText(this.context,
//                        String.format(Locale.getDefault(),
//                                "find usb device [VID: %04X, PID: %04X], we want [VID: %04X, PID: %04X].",
//                                device.getVendorId(), device.getProductId(), this.mVendorID, this.mProductID), Toast.LENGTH_SHORT).show();
                if (device.getVendorId() == mVendorId && device.getProductId() == mProductId) {
                    return device;
                }
            }
        }
        return null;
    }

    public List<UsbDevice> getExistsUsbDevices() {

        final HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        List<UsbDevice> usbDevices = new LinkedList<>();
        if (deviceList != null) {
            final Iterator<UsbDevice> iterator = deviceList.values().iterator();
            UsbDevice device;
            while (iterator.hasNext()) {
                device = iterator.next();
                Log.i("__lib_uvc__", "getDeviceList, USBDevice: " + device.getDeviceName());
                Log.i("__lib_uvc__", "getDeviceList, USBDevice: " + device.getVendorId());
                Log.i("__lib_uvc__", "getDeviceList, USBDevice: " + device.getProductId());
                usbDevices.add(device);
            }
            return usbDevices;
        }
        return null;
    }
}
