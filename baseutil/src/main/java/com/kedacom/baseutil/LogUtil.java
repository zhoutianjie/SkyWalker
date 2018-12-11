package com.kedacom.baseutil;

import android.util.Log;

/**
 * Created by zhoutianjie on 2018/12/11.
 */

public class LogUtil {

    public static boolean isPrint = true;

    public static void e(String TAG,String content){
        if(isPrint){
            Log.e(TAG,content);
        }
    }

    public static void v(String TAG,String content){
        if (isPrint){
            Log.v(TAG,content);
        }
    }
}
