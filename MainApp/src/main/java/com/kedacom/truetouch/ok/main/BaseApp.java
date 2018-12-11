package com.kedacom.truetouch.ok.main;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.kedacom.baseutil.LogUtil;
import com.kedacom.baseutil.StringUtil;
import com.kedacom.truetouch.ok.BuildConfig;
import com.kedacom.truetouch.ok.db.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhoutianjie on 2018/11/20.
 */

public abstract class BaseApp extends Application {

    private static BaseApp instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.v("App","onCreate");
        instance = this;
    }

    public static BaseApp getInstance(){
        return instance;
    }

    public String getRootDirName(){
        String def = "kedacom";
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String rootDir = applicationInfo.metaData.getString("com.privatecustom.publiclibs.root");
            return rootDir;
        } catch (PackageManager.NameNotFoundException e) {
            return def;
        }
    }

    public String getApprootDirName() {
        String def = "Sky";

        try {
            ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String appRootDir = appInfo.metaData.getString("com.kedacom.truetouch.app.root");
            if (StringUtil.isNull(appRootDir)) {
                return def;
            }
            return appRootDir;
        } catch (Exception e) {
            return def;
        }
    }

    public String versionName(String defVersionName){
        PackageManager packageMgr = getPackageManager();
        String packageName = getPackageName();
        if (packageMgr == null || TextUtils.isEmpty(packageName)) {
            return defVersionName;
        }
        try {
            PackageInfo packageInfo = packageMgr.getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        }catch (Exception e){
            return defVersionName;
        }
    }

    public String getApkTimestamp() {
        return getApkTimestamp(new SimpleDateFormat("yyyyMMdd"));
    }

    private String getApkTimestamp(SimpleDateFormat formatter) {
        return formatter.format(new Date(BuildConfig.TIMESTAMP));
    }

    public String getOmeName(){
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("com.kedacom.truetouch.oem.name", "");
        } catch (Exception e) {
        }

        return "";
    }

    public AppDatabase getDataBase(){
        return AppDatabase.Instance(this);
    }
}
