package com.kedacom.baseutil;

import java.io.File;

/**
 * Created by zhoutianjie on 2018/11/19.
 */

public class FileUtil {

    public static File createFolder(String dir){
        if(StringUtil.isNull(dir)){
            return null;
        }
        File file = new File(dir);
        if(!file.exists()){
            file.mkdirs();
        }
        return file;
    }

    public static File createFolder(String dir, String folder) {
        if (StringUtil.isNull(dir) && StringUtil.isNull(folder)) {
            return null;
        }

        if (StringUtil.isNull(dir)) {
            return createFolder(folder);
        }

        if (StringUtil.isNull(folder)) {
            return createFolder(dir);
        }

        File file = new File(dir, folder);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }
}
