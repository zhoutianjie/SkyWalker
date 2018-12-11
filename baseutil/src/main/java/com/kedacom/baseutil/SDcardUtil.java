package com.kedacom.baseutil;

import android.os.Environment;

import java.io.File;

/**
 * Created by zhoutianjie on 2018/11/19.
 */

public class SDcardUtil {

    /**
     * 获取外部存储目录
     *
     * @return
     */
    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 外部存储目录是否存在且可写
     *
     * @return
     */
    public static boolean existExternalStorageDirectory() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && Environment.getExternalStorageDirectory().canWrite()) {
            return true;
        }
        return false;
    }

    /**
     * SD 卡是否可用
     * @return
     */
    public static boolean isCanUseSD(){
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static File createDir(String dir){
        if(!isCanUseSD()){
            return null;
        }
        return FileUtil.createFolder(getExternalStorageDirectory() + File.separator + dir);
    }
}
