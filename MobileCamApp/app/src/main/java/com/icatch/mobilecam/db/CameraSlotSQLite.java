package com.icatch.mobilecam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.MyCamera.CameraType;
import com.icatch.mobilecam.data.GlobalApp.GlobalInfo;
import com.icatch.mobilecam.data.SystemInfo.MWifiManager;
import com.icatch.mobilecam.data.entity.CameraSlot;

import java.util.ArrayList;

public class CameraSlotSQLite {
    private SQLiteDatabase db;
    private String TAG = "CameraSlotSQLite";
    private static CameraSlotSQLite instance;
    private ArrayList<CameraSlot> camSlotList;
    private Context context;

    public static CameraSlotSQLite getInstance() {
        if (instance == null) {
            instance = new CameraSlotSQLite();
        }
        return instance;
    }

    private CameraSlotSQLite() {
        creatTable(GlobalInfo.getInstance().getAppContext());
    }

    private void creatTable(Context context) {
        AppLog.i(TAG, "start creatTable");
        this.context = context;
        CameraSlotSQLiteHelper dbHelper = new CameraSlotSQLiteHelper(context);
        this.db = dbHelper.getWritableDatabase();
        AppLog.i(TAG, "end creatTable");

    }

    //插入数据
    public boolean insert(CameraSlot camSlot) {
        AppLog.i(TAG, "start insert isOccupied=" + switchBoolToInt(camSlot.isOccupied));
        //实例化常量值
        ContentValues cValue = new ContentValues();
        cValue.put("isOccupied", switchBoolToInt(camSlot.isOccupied));
        cValue.put("cameraName", camSlot.cameraName);
        cValue.put("imageBuffer", camSlot.cameraPhoto);
        cValue.put("cameraType", camSlot.cameraType);

        //调用insert()方法插入数据
        if (db.insert(CameraSlotSQLiteHelper.DATABASE_TABLE, null, cValue) == -1) {
            AppLog.i(TAG, "failed to insert!");
            return false;
        }
        ;
        AppLog.i(TAG, "end: insert success");
        return true;
    }

    //更新数据
    public void update(CameraSlot camSlot) {
        AppLog.i(TAG, "start update slotPosition=" + camSlot.slotPosition);
        ContentValues values = new ContentValues();
        //在values中添加内容
        values.put("isOccupied", camSlot.isOccupied);
        if(camSlot.cameraPhoto != null && camSlot.cameraPhoto.length > 0) {
            values.put("imageBuffer", camSlot.cameraPhoto);
        }
        values.put("cameraName", camSlot.cameraName);
        values.put("cameraType", camSlot.cameraType);

        //修改条件
        String whereClause = "_id=?";
        //修改添加参数
        //slot position is 0~2,but _id is 1~3

        String[] whereArgs = {String.valueOf(camSlot.slotPosition + 1)};
        //修改
        db.update(CameraSlotSQLiteHelper.DATABASE_TABLE, values, whereClause, whereArgs);
        AppLog.i(TAG, "end update");
    }

    //删除数据
    public void deleteByPosition(int slotPosition) {
        AppLog.i(TAG, "start delete slotPosition=" + slotPosition);
        update(new CameraSlot(slotPosition, false, null, CameraType.UNDEFIND_CAMERA, null, false));
        AppLog.i(TAG, "end delete");
    }

    public ArrayList<CameraSlot> getAllCameraSlotFormDb() {
        AppLog.i(TAG, "start getAllCameraSlotFormDb");
        camSlotList = new ArrayList<CameraSlot>();
        Cursor cursor = db.rawQuery("select * from " + CameraSlotSQLiteHelper.DATABASE_TABLE, null);
        AppLog.i(TAG, "end rawQuery =" + cursor.getCount());
        String wifiSsid = null;

        wifiSsid = MWifiManager.getSsid(context);

        //判断游标是否为空
        while (cursor.moveToNext()) {
            //遍历游标
            AppLog.i(TAG, "cursor.getInt(cursor.getColumnIndex =" + cursor.getInt(cursor.getColumnIndex("_id")));
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            int isUsed = cursor.getInt(cursor.getColumnIndex("isOccupied"));
            String camName = cursor.getString(cursor.getColumnIndex("cameraName"));
            byte[] imageBuf = cursor.getBlob(cursor.getColumnIndex("imageBuffer"));
            int cameraType = cursor.getInt(cursor.getColumnIndex("cameraType"));
            boolean isReady = false;
            if (wifiSsid != null && wifiSsid.equals(camName)) {
                isReady = true;
            }
            camSlotList.add(new CameraSlot(id, switchIntToBool(isUsed), camName, cameraType, imageBuf, isReady));
            AppLog.i(TAG, " switchIntToBool(isUsed) =" + switchIntToBool(isUsed));
            AppLog.i(TAG, "_id=" + id + " isOccupied=" + isUsed + " camName=" + camName + " cameraType=" + cameraType);
        }
        AppLog.i(TAG, "end query all cameraSlot");
        if (cursor != null) {
            cursor.close();
        }
        if (camSlotList.size() == 0) {
            for (int ii = 0; ii < 3; ii++) {
                if (insert(new CameraSlot(ii, false, null, null))) {
                    camSlotList.add(new CameraSlot(ii, false, null, CameraType.UNDEFIND_CAMERA, null, false));
                }
            }
        }
        AppLog.i(TAG, "end getAllCameraSlotFormDb");
        return camSlotList;
    }

    // 更新图片数据
    public void updateImage(Bitmap bitmap) {
//        AppLog.d(TAG, "start updateImage curSlotPosition=" + curSlotPosition);
//        AppLog.i(TAG, "start updateImage curWifiSsid=" + curWifiSsid);
////        ArrayList<CameraSlot>  slotList = getAllCameraSlotFormDb();
////        for (CameraSlot temp : slotList
////                ) {
////            AppLog.d(TAG, "start updateImage temp.slotPosition=" +temp.slotPosition);
////            if(temp.slotPosition == curSlotPosition){
////                camSlot = temp;
////            }
////        }
//
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        if (bitmap != null) {
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//            update(new CameraSlot(curSlotPosition, true, curWifiSsid, os.toByteArray(), true));
//        }
    }

    public void closeDB() {
        db.close();
    }

    private int switchBoolToInt(Boolean value) {
        if (value) {
            return 1;
        }
        return 0;
    }

    private Boolean switchIntToBool(int value) {
        if (value == 1) {
            return true;
        } else {
            return false;
        }
    }

}
