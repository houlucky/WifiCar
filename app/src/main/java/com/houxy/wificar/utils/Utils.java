package com.houxy.wificar.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.houxy.wificar.C;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Houxy on 2016/11/19.
 */

public class Utils {


    public static int saveBitmapToFile(Bitmap mBitmap, String bitName) {
        FileOutputStream fOut = null;
        Log.i("MjpegView", "saveBitmapToFile enter");
        if (null == bitName || bitName.length() <= 4) {
            return C.CAM_RES_FAIL_FILE_NAME_ERROR;
        }

        File f = new File(bitName);
        Log.i("MjpegView", "saveBitmapToFile, fname =" + f);
        try {
            f.createNewFile();
            Log.i("MjpegView", "saveBitmapToFile, createNewFile success, f=" + f);
            fOut = new FileOutputStream(f);
            Log.i("MjpegView", "saveBitmapToFile, FileOutputStream success, fOut=" + fOut);
        } catch (Exception e) {
            Log.i("MjpegView", "exception, err=" + e.getMessage());
            return C.CAM_RES_FAIL_FILE_WRITE_ERROR;
        }

        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

        try {
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return C.CAM_RES_FAIL_BITMAP_ERROR;
        }

        return C.CAM_RES_OK;
    }


    public static String generateFileName() {
        File sdcard;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist)
        {
            sdcard = Environment.getExternalStorageDirectory();////获取跟目录
        } else {
            return null;
        }

        String save2dir = sdcard.toString() + "/" + "WIFI_CAR";

        File fSave2dir  = new File(save2dir);
        ///判断文件夹是否存在,如果不存在则创建文件夹
        if (!fSave2dir.exists()) {
            fSave2dir.mkdir();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());//get current time
        String str = formatter.format(curDate);

        String save2file = save2dir + "/" + str + ".png";

        File fSave2file = new File(save2file);
        if(fSave2file.exists()) {
            return save2dir + "/" + str + System.currentTimeMillis() + ".png";
        }

        return save2file;
    }

}
