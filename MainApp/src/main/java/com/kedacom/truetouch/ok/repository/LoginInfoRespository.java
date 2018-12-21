package com.kedacom.truetouch.ok.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;


import com.kedacom.baseutil.AppExecutors;
import com.kedacom.baseutil.StringUtil;
import com.kedacom.truetouch.ok.db.AppDatabase;

import com.kedacom.truetouch.ok.db.entity.LoginInfo;

import com.kedacom.truetouch.ok.main.App;

import java.util.Collections;
import java.util.List;

/**
 * Created by zhoutianjie on 2018/11/21.
 */

public class LoginInfoRespository {

    private AppDatabase database;
    private final MutableLiveData<LoginInfo> loginInfoLiveData;
    private final MutableLiveData<Boolean> booleanMutableLiveData;

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
                if(loginInfos!=null && loginInfos.size()>0){

                    Collections.sort(loginInfos);
                    LoginInfo loginInfo = loginInfos.get(0);
                    if(loginInfo!=null){
                        loginInfoLiveData.postValue(loginInfo);
                    }
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

    //登录
    public void login(final String address, final String account, final String pwd){

        AppExecutors.Instance().getSingleThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //保存的登录信息
                LoginInfo loginInfo = new LoginInfo();
                if(!TextUtils.isEmpty(address)){
                    loginInfo.setAddress(address.trim());
                }
                if(!TextUtils.isEmpty(account)){
                    loginInfo.setAccount(account.trim());
                }
                if(!TextUtils.isEmpty(pwd)){
                    loginInfo.setPwd(pwd.trim());
                }

                if(TextUtils.isEmpty(address)){

                }


            }
        });




    }
}
