package com.icatch.mobilecam.Function.USB;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.SparseArray;

import com.icatch.mobilecam.Log.AppLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class USBMonitor {

    private static final boolean DEBUG = true;    // TODO set false on production
    private static final String TAG = "USBMonitor";

    private static final String ACTION_USB_PERMISSION_BASE = "com.serenegiant.USB_PERMISSION.";
//    private final String ACTION_USB_PERMISSION = ACTION_USB_PERMISSION_BASE + hashCode();

    private final String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";
//    public static final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

    private final ConcurrentHashMap<UsbDevice, UsbControlBlock> mCtrlBlocks = new ConcurrentHashMap<UsbDevice, UsbControlBlock>();
    private final WeakReference<Context> mWeakContext;
    private final UsbManager mUsbManager;
    private final OnDeviceConnectListener mOnDeviceConnectListener;
    //    private PendingIntent mPermissionIntent = null;
    private List<DeviceFilter> mDeviceFilters = new ArrayList<DeviceFilter>();

    private final Handler mHandler = new Handler();
    private IntentFilter filter;

    public interface OnDeviceConnectListener {
        /**
         * called when device attached
         *
         * @param device
         */
        public void onAttach(UsbDevice device);

        /**
         * called when device dettach(after onDisconnect)
         *
         * @param device
         */
        public void onDettach(UsbDevice device);

        /**
         * called after device opend
         *
         * @param device
         * @param createNew
         */
        public void onConnect(UsbDevice device, UsbControlBlock ctrlBlock, boolean createNew);

        /**
         * called when USB device removed or its power off (this callback is called after device closing)
         *
         * @param device
         * @param ctrlBlock
         */
        public void onDisconnect(UsbDevice device, UsbControlBlock ctrlBlock);

        /**
         * called when canceled or could not get permission from user
         */
        public void onCancel();
    }

    public USBMonitor(final Context context, final OnDeviceConnectListener listener) {
        if (DEBUG) AppLog.d(TAG, "USBMonitor:Constructor");
/*		if (listener == null)
            throw new IllegalArgumentException("OnDeviceConnectListener should not null."); */
        mWeakContext = new WeakReference<Context>(context);
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mOnDeviceConnectListener = listener;
        if (DEBUG) AppLog.d(TAG, "USBMonitor:mUsbManager=" + mUsbManager);
    }

    public void destroy() {
        if (DEBUG) AppLog.i(TAG, "destroy:");
        unregister();
        final Set<UsbDevice> keys = mCtrlBlocks.keySet();
        if (keys != null) {
            UsbControlBlock ctrlBlock;
            try {
                for (final UsbDevice key : keys) {
                    ctrlBlock = mCtrlBlocks.remove(key);
                    ctrlBlock.close();
                }
            } catch (final Exception e) {
                AppLog.e(TAG, "destroy:" + e);
            }
            mCtrlBlocks.clear();
        }
    }


    /**
     * register BroadcastReceiver to monitor USB events
     */
    public synchronized void register() {
        if (filter == null) {
            final Context context = mWeakContext.get();
            if (context != null) {
                filter = new IntentFilter(ACTION_USB_PERMISSION);
                filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                context.registerReceiver(mUsbReceiver, filter);
            }
            mDeviceCounts = 0;
            mHandler.postDelayed(mDeviceCheckRunnable, 1000);
        }
    }

    /**
     * unregisterUSB BroadcastReceiver
     */
    public synchronized void unregister() {
        if (filter != null) {
            if (DEBUG) AppLog.i(TAG, "unregisterUSB:");
            final Context context = mWeakContext.get();
            if (context != null) {
                context.unregisterReceiver(mUsbReceiver);
            }
            filter = null;
        }
        mDeviceCounts = 0;
        mHandler.removeCallbacks(mDeviceCheckRunnable);
    }

    public synchronized boolean isRegistered() {
        return filter != null;
    }

    /**
     * set device filter
     *
     * @param filter
     */
    public void setDeviceFilter(final DeviceFilter filter) {
        mDeviceFilters.clear();
        mDeviceFilters.add(filter);
    }

    /**
     * set device filters
     *
     * @param filters
     */
    public void setDeviceFilter(final List<DeviceFilter> filters) {
        mDeviceFilters.clear();
        mDeviceFilters.addAll(filters);
    }

    /**
     * return the number of connected USB devices that matched device filter
     *
     * @return
     */
    public int getDeviceCount() {
        return getDeviceList().size();
    }

    /**
     * return device list, return empty list if no device matched
     *
     * @return
     */
    public List<UsbDevice> getDeviceList() {
        return getDeviceList(mDeviceFilters);
    }

    /**
     * return device list, return empty list if no device matched
     *
     * @param filters
     * @return
     */
    public List<UsbDevice> getDeviceList(final List<DeviceFilter> filters) {
        final HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        final List<UsbDevice> result = new ArrayList<UsbDevice>();
        if (deviceList != null) {
            for (final DeviceFilter filter : filters) {
                final Iterator<UsbDevice> iterator = deviceList.values().iterator();
                UsbDevice device;
                while (iterator.hasNext()) {
                    device = iterator.next();
                    if ((filter == null) || (filter.matches(device))) {
                        result.add(device);
                    }
                }
            }
        }
        return result;
    }

    /**
     * return device list, return empty list if no device matched
     *
     * @param filter
     * @return
     */
    public List<UsbDevice> getDeviceList(final DeviceFilter filter) {

        final HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        final List<UsbDevice> result = new ArrayList<UsbDevice>();
        AppLog.d(TAG, "getDeviceList size is: " + deviceList.size());
//        AppLog.d(TAG,"the deviceList.isEmpty() is: "+deviceList.isEmpty());

        if (deviceList != null) {
            final Iterator<UsbDevice> iterator = deviceList.values().iterator();
            UsbDevice device;
            while (iterator.hasNext()) {
                device = iterator.next();
                AppLog.d(TAG, "the device is: " + device);
                if ((filter == null) || (filter.matches(device))) {
                    result.add(device);
                }
            }
        }

        AppLog.d(TAG, "getDeviceList result size is: " + result.size());
        return result;
    }

    /**
     * output device list to LogCat
     */
    public final void dumpDevices() {
        final HashMap<String, UsbDevice> list = mUsbManager.getDeviceList();
        if (list != null) {
            final Set<String> keys = list.keySet();
            if (keys != null && keys.size() > 0) {
                final StringBuilder sb = new StringBuilder();
                for (final String key : keys) {
                    final UsbDevice device = list.get(key);
                    final int num_interface = device != null ? device.getInterfaceCount() : 0;
                    sb.setLength(0);
                    for (int i = 0; i < num_interface; i++) {
                        sb.append(String.format("interface%d:%s", i, device.getInterface(i).toString()));
                    }
                    AppLog.i(TAG, "key=" + key + ":" + device + ":" + sb.toString());
                }
            } else {
                AppLog.i(TAG, "no device");
            }
        } else {
            AppLog.i(TAG, "no device");
        }
    }

    /**
     * return whether the specific Usb device has permission
     *
     * @param device
     * @return
     */
    public boolean hasPermission(final UsbDevice device) {
        return device != null && mUsbManager.hasPermission(device);
    }

    /**
     * request permission to access to USB device
     *
     * @param device
     */

    public synchronized void requestPermission(final UsbDevice device) {
        if (DEBUG) AppLog.d(TAG, "requestPermission:device=" + device);
        if (filter != null) {
            if (device != null) {
                AppLog.d(TAG, "mUsbManager.hasPermission( device )=" + mUsbManager.hasPermission(device));
                if (mUsbManager.hasPermission(device)) {
                    AppLog.d(TAG, "processConnect not from broadcaster");
                    processConnect(device);

                } else {
                    AppLog.d(TAG, "do not has permission,so  requestPermission");
                    PendingIntent permissionIntent = PendingIntent.getBroadcast(mWeakContext.get(), 0, new Intent(
                            ACTION_USB_PERMISSION), 0);
                    mUsbManager.requestPermission(device, permissionIntent);
                }
            } else {
                processCancel(device);
            }
        } else {
            processCancel(device);
        }
    }

    /**
     * BroadcastReceiver for USB permission
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                AppLog.d(TAG, "ACTION_USB_PERMISSION.");
                synchronized (USBMonitor.this) {
                    final UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        AppLog.d(TAG, "onReceive....EXTRA_PERMISSION_GRANTED!!.");
                        if (device != null) {
                            processConnect(device);
                        }
                    } else {
                        processCancel(device);
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                AppLog.d(TAG, "ACTION_USB_DEVICE_ATTACHED");
                final UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                processAttach(device);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                final UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    UsbControlBlock ctrlBlock = null;
                    ctrlBlock = mCtrlBlocks.remove(device);
                    if (ctrlBlock != null) {
                        ctrlBlock.close();
                    }
                    mDeviceCounts = 0;
                    processDettach(device);
                }
            }
        }
    };

    private volatile int mDeviceCounts = 0;

    private final Runnable mDeviceCheckRunnable = new Runnable() {
        @Override
        public void run() {
            final int n = getDeviceCount();
            if (n != mDeviceCounts) {
                if (n > mDeviceCounts) {
                    mDeviceCounts = n;
                    if (mOnDeviceConnectListener != null) mOnDeviceConnectListener.onAttach(null);
                }
            }
            mHandler.postDelayed(this, 2000);    // confirm every 2 seconds
        }
    };

    private final void processConnect(final UsbDevice device) {
        if (DEBUG) AppLog.d(TAG, "processConnect:");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                UsbControlBlock ctrlBlock;
                final boolean createNew;
                ctrlBlock = mCtrlBlocks.get(device);
                if (ctrlBlock == null) {
                    ctrlBlock = new UsbControlBlock(USBMonitor.this, device);
                    mCtrlBlocks.put(device, ctrlBlock);
                    createNew = true;
                } else {
                    createNew = false;
                }
                if (mOnDeviceConnectListener != null) {
                    final UsbControlBlock ctrlB = ctrlBlock;
                    mOnDeviceConnectListener.onConnect(device, ctrlB, createNew);
                }
            }
        });
    }

    private final void processCancel(final UsbDevice device) {
        if (DEBUG) AppLog.d(TAG, "processCancel:");
        if (mOnDeviceConnectListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOnDeviceConnectListener.onCancel();
                }
            });
        }
    }

    private final void processAttach(final UsbDevice device) {
        if (DEBUG) AppLog.d(TAG, "processAttach:");
        if (mOnDeviceConnectListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOnDeviceConnectListener.onAttach(device);
                }
            });
        }
    }

    private final void processDettach(final UsbDevice device) {
        if (DEBUG) AppLog.d(TAG, "processDettach:");
        if (mOnDeviceConnectListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOnDeviceConnectListener.onDettach(device);
                }
            });
        }
    }

    public static final class UsbControlBlock {
        private final WeakReference<USBMonitor> mWeakMonitor;
        private final WeakReference<UsbDevice> mWeakDevice;
        protected UsbDeviceConnection mConnection;
        private final SparseArray<UsbInterface> mInterfaces = new SparseArray<UsbInterface>();

        /**
         * this class needs permission to access USB device before constructing
         *
         * @param monitor
         * @param device
         */
        public UsbControlBlock(final USBMonitor monitor, final UsbDevice device) {
            if (DEBUG) AppLog.i(TAG, "UsbControlBlock:constructor");
            mWeakMonitor = new WeakReference<USBMonitor>(monitor);
            mWeakDevice = new WeakReference<UsbDevice>(device);
            mConnection = monitor.mUsbManager.openDevice(device);
            final String name = device.getDeviceName();
            if (mConnection != null) {
                if (DEBUG) {
                    final int desc = mConnection.getFileDescriptor();
                    final byte[] rawDesc = mConnection.getRawDescriptors();
                    AppLog.i(TAG, "UsbControlBlock:name=" + name + ", desc=" + desc + ", rawDesc=" + rawDesc);
                }
            } else {
                AppLog.e(TAG, "could not connect to device " + name);
            }
        }

        public UsbDevice getDevice() {
            return mWeakDevice.get();
        }

        public String getDeviceName() {
            final UsbDevice device = mWeakDevice.get();
            return device != null ? device.getDeviceName() : "";
        }

        public UsbDeviceConnection getUsbDeviceConnection() {
            return mConnection;
        }

        public synchronized int getFileDescriptor() {
            return mConnection != null ? mConnection.getFileDescriptor() : -1;
        }

        public byte[] getRawDescriptors() {
            return mConnection != null ? mConnection.getRawDescriptors() : null;
        }

        public int getVenderId() {
            final UsbDevice device = mWeakDevice.get();
            return device != null ? device.getVendorId() : 0;
        }

        public int getProductId() {
            final UsbDevice device = mWeakDevice.get();
            return device != null ? device.getProductId() : 0;
        }

        public synchronized String getSerial() {
            return mConnection != null ? mConnection.getSerial() : null;
        }

        /**
         * open specific interface
         *
         * @param interfaceIndex
         * @return
         */
        public synchronized UsbInterface open(final int interfaceIndex) {
            if (DEBUG) AppLog.i(TAG, "UsbControlBlock#open:" + interfaceIndex);
            final UsbDevice device = mWeakDevice.get();
            UsbInterface intf = null;
            intf = mInterfaces.get(interfaceIndex);
            if (intf == null) {
                intf = device.getInterface(interfaceIndex);
                if (intf != null) {
                    synchronized (mInterfaces) {
                        mInterfaces.append(interfaceIndex, intf);
                    }
                }
            }
            return intf;
        }

        /**
         * close specified interface. USB device itself still keep open.
         *
         * @param interfaceIndex
         */
        public void close(final int interfaceIndex) {
            UsbInterface intf = null;
            synchronized (mInterfaces) {
                intf = mInterfaces.get(interfaceIndex);
                if (intf != null) {
                    mInterfaces.delete(interfaceIndex);
                    mConnection.releaseInterface(intf);
                }
            }
        }

        /**
         * close specified interface. USB device itself still keep open.
         */
        public synchronized void close() {
            if (DEBUG) AppLog.i(TAG, "UsbControlBlock#close:");
            if (mConnection != null) {
                final int n = mInterfaces.size();
                int key;
                UsbInterface intf;
                for (int i = 0; i < n; i++) {
                    key = mInterfaces.keyAt(i);
                    intf = mInterfaces.get(key);
                    mConnection.releaseInterface(intf);
                }
                mConnection.close();
                mConnection = null;
                final USBMonitor monitor = mWeakMonitor.get();
                if (monitor != null) {
                    if (monitor.mOnDeviceConnectListener != null) {
                        final UsbDevice device = mWeakDevice.get();
                        monitor.mOnDeviceConnectListener.onDisconnect(device, this);
                    }
                    monitor.mCtrlBlocks.remove(getDevice());
                }
            }
        }
    }

}
