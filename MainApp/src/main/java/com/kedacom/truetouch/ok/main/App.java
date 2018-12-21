package com.kedacom.truetouch.ok.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.util.DebugUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.kedacom.baseutil.AppExecutors;
import com.kedacom.kdv.mt.mtapi.CommonCtrl;
import com.kedacom.kdv.mt.mtapi.ConfigCtrl;
import com.kedacom.kdv.mt.mtapi.KernalCtrl;
import com.kedacom.kdv.mt.mtapi.MonitorCtrl;
import com.kedacom.kdv.mt.mtapi.MtServiceCfgCtrl;
import com.kedacom.kdv.mt.mtapi.MtcLib;
import com.kedacom.kdv.mt.mtapi.bean.BaseBooleantype;
import com.kedacom.kdv.mt.mtapi.bean.Basetype;
import com.kedacom.kdv.mt.mtapi.bean.TMTFecInfo;
import com.kedacom.kdv.mt.mtapi.bean.TPrsParam;
import com.kedacom.kdv.mt.mtapi.bean.TagTNetUsedInfoApi;
import com.kedacom.kdv.mt.mtapi.emun.EmConfProtocol;
import com.kedacom.kdv.mt.mtapi.emun.EmEncryptArithmetic;
import com.kedacom.kdv.mt.mtapi.emun.EmMtModel;
import com.kedacom.truetouch.audio.AudioDeviceAndroid;
import com.kedacom.truetouch.ok.BuildConfig;
import com.kedacom.truetouch.ok.R;
import com.kedacom.truetouch.ok.repository.jnicallback.CompomentCallback;
import com.kedacom.truetouch.ok.service.NetWorkListenerService;
import com.kedacom.truetouch.ok.util.AppPathManager;
import com.kedacom.truetouch.ok.util.NetWorkManager;
import com.kedacom.truetouch.ok.util.VconfManager;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by zhoutianjie on 2018/11/19.
 */

public class App extends BaseApp {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initArouter();
        initComponent();
        initNetWorkListen();
    }

    /**
     * 初始化Arouter
     */
    private void initArouter(){
        if(BuildConfig.DEBUG){
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    /**
     * 初始化业务组件
     */
    private void initComponent(){
        KernalCtrl.SetSysWorkPathPrefix(AppPathManager.Instance().getMTCfgMediaLibDir());
        AppExecutors.Instance().getCacheThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                int nEmModel = EmMtModel.emSkyAndroidPhone.value;
                String strModelName = "SKY for Android Phone";
                String versionName = versionName("5.0.0.0.0");
                String compileTime = getApkTimestamp();
                String omeName = getOmeName();
                KernalCtrl.MtStart(nEmModel,strModelName,getString(R.string.android_version, versionName, compileTime),omeName);
                MtcLib.Start(false);
                //设置回调  所有消息从这里拿到
                MtcLib.Setcallback(CompomentCallback.Instance());
                //初始化 能力集
                int callRate = 384;
                MonitorCtrl.SetCallCapPlusCmd(VconfManager.Instance().getSendResolutionByCallRate(callRate)
                                            ,VconfManager.Instance().getRecResolutionByCallRate(callRate)
                                            , EmConfProtocol.emsip.ordinal());
                MonitorCtrl.SetCallCapPlusCmd(VconfManager.Instance().getSendResolutionByCallRate(callRate)
                                            ,VconfManager.Instance().getRecResolutionByCallRate(callRate)
                                            , EmConfProtocol.em323.ordinal());

                TagTNetUsedInfoApi userdNetInfo = NetWorkManager.getUserdNetInfo(getInstance());

                CommonCtrl.SendUsedNetInfoNtf(new StringBuffer(userdNetInfo.toJson()));

                BaseBooleantype booleantype = new BaseBooleantype();
                booleantype.basetype = true;

                ConfigCtrl.SetLogCfgCmd(new StringBuffer(booleantype.toJson()));

                AudioDeviceAndroid.initialize(getApplicationContext());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
                MtServiceCfgCtrl.sysStartService();
                TPrsParam prsParam = new TPrsParam();
                Log.e("TAG",prsParam.toJson());
                ConfigCtrl.SetLostPktResendCfgCmd(new StringBuffer(prsParam.toJson()));

                Basetype basetype = new Basetype();
                basetype.basetype = EmEncryptArithmetic.emEncryptNone.ordinal();
                ConfigCtrl.SetEncryptTypeCfgCmd(new StringBuffer(basetype.toJson()));

                TMTFecInfo tMTFecInfo  = new TMTFecInfo(true);
                ConfigCtrl.SetFECCfgCmd(new StringBuffer(tMTFecInfo.toJson()));

            }
        });

    }

    /**
     * 初始化网络监听
     */
    private void initNetWorkListen(){
        Intent intent = new Intent();
        intent.setClass(this,NetWorkListenerService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        },Context.BIND_AUTO_CREATE);
    }




}
