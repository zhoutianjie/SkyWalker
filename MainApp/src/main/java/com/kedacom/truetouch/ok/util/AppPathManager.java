package com.kedacom.truetouch.ok.util;

import android.content.Context;

import com.kedacom.baseutil.PathManager;
import com.kedacom.truetouch.ok.main.App;

import java.io.File;

/**
 * Created by zhoutianjie on 2018/11/19.
 */

public class AppPathManager extends PathManager {


    protected final String MEDIA_Lib_DIR = "mediaLib";

    private AppPathManager() {
        super(App.getInstance().getRootDirName(), App.getInstance().getApprootDirName());
    }

    private static class SingleTonHolder{
        private static final AppPathManager INSTANCE = new AppPathManager();
    }

    public static AppPathManager Instance(){
        return SingleTonHolder.INSTANCE;
    }


    public String getMTCfgMediaLibDir(){
        return App.getInstance().getApplicationContext().getDir(MEDIA_Lib_DIR, Context.MODE_PRIVATE).getAbsoluteFile() + File.separator;
    }

}
