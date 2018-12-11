package com.kedacom.baseutil;

import java.io.File;

/**
 * Created by zhoutianjie on 2018/11/19.
 */

public abstract class PathManager {


    // 根目录
    protected final String mRootDir;

    // app根目录,位于根目录下（根目录/app根目录）
    protected final String mAppRootDir;

    /**
     * @param rootDir 根目录
     * @param appRootDir app根目录，根目录的下级
     */
    public PathManager(String rootDir, String appRootDir) {
        mRootDir = rootDir;
        mAppRootDir = appRootDir;

        if (SDcardUtil.existExternalStorageDirectory()) {
            File rDir = FileUtil.createFolder(SDcardUtil.getExternalStorageDirectory(), rootDir);
            if (rDir == null || StringUtil.isNull(rDir.getAbsolutePath())) {
                FileUtil.createFolder(SDcardUtil.getExternalStorageDirectory(), mAppRootDir);
            } else {
                FileUtil.createFolder(rDir.getAbsolutePath(), mAppRootDir);
            }
        }
    }




}
