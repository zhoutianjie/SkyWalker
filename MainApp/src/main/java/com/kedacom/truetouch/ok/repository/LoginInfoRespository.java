package com.kedacom.truetouch.ok.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;

import com.kedacom.baseutil.AppExecutors;
import com.kedacom.baseutil.StringUtil;
import com.kedacom.truetouch.ok.db.AppDatabase;
import com.kedacom.truetouch.ok.db.AppDatabase_Impl;
import com.kedacom.truetouch.ok.db.entity.LoginInfo;
import com.kedacom.truetouch.ok.main.App;

import java.util.Collections;
import java.util.List;

/**
 * Created by zhoutianjie on 2018/11/21.
 */

public class LoginInfoRespository {

    private AppDatabase database;
    private MutableLiveData<LoginInfo> loginInfoLiveData;
    private MutableLiveData<Boolean> booleanMutableLiveData;
    private LoginInfoRespository(){
        database = App.getInstance().getDataBase();
        booleanMutableLiveData = new MediatorLiveData<>();
        loginInfoLiveData = new MediatorLiveData<>();
    }

    private static class SingleTonHolder{
        private static final LoginInfoRespository INSTANCE = new LoginInfoRespository();
    }

    public static LoginInfoRespository Instance(){
        return SingleTonHolder.INSTANCE;
    }

    /**
     * 获取上一次登录用户的登录信息
     */
    public LiveData<LoginInfo> getLastUserLoginInfo(){
        AppExecutors.Instance().getSingleThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                List<LoginInfo> loginInfos = database.getLoginInfoDao().queryAlluser();
                if(loginInfos!=null){
                    Collections.sort(loginInfos);
                    loginInfoLiveData.postValue(loginInfos.get(0));
                }
            }
        });
        return loginInfoLiveData;
    }

    public LiveData<Boolean> directToMainUI(){
        AppExecutors.Instance().getSingleThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                List<LoginInfo> loginInfos = database.getLoginInfoDao().queryAlluser();
                if(loginInfos == null || loginInfos.isEmpty()){
                    booleanMutableLiveData.postValue(false);
                }else {
                    Collections.sort(loginInfos);
                    LoginInfo loginInfo = loginInfos.get(0);
                    String account = loginInfo.getAccount();
                    String pwd = loginInfo.getPwd();
                    boolean lastState = loginInfo.isMain_state();

                    if(StringUtil.isNull(account) || StringUtil.isNull(pwd)){
                        booleanMutableLiveData.postValue(false);
                    }else {
                        booleanMutableLiveData.postValue(lastState);
                    }
                }
            }
        });

        return booleanMutableLiveData;
    }
}
