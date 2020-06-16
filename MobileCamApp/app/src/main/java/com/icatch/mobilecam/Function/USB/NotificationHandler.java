package com.icatch.mobilecam.Function.USB;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class NotificationHandler extends Handler {
    private Context context;

    public NotificationHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case USBHost_Feature.HANDLER_USB_DEVICE_ATTACHED_ID: {
                Bundle data = message.getData();
                String info = data.getString(USBHost_Feature.HANDLER_USB_DEVICE_ATTACHED_KEY);
                Toast.makeText(context, "Usb device [" + info + "] attached.", Toast.LENGTH_SHORT).show();
                break;
            }
            case USBHost_Feature.HANDLER_USB_DEVICE_DETACHED_ID: {
                Bundle data = message.getData();
                String info = data.getString(USBHost_Feature.HANDLER_USB_DEVICE_DETACHED_KEY);
                Toast.makeText(context, "Usb device [" + info + "] detached.", Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                break;
        }
    }
}
