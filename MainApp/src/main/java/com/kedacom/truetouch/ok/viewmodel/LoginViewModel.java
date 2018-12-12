package com.kedacom.truetouch.ok.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.BaseObservable;
import android.support.annotation.NonNull;

import com.kedacom.truetouch.ok.db.entity.LoginInfo;
import com.kedacom.truetouch.ok.repository.LoginApsRespository;
import com.kedacom.truetouch.ok.repository.LoginInfoRespository;


/**
 * Created by zhoutianjie on 2018/12/11.
 */

public class LoginViewModel extends AndroidViewModel{

    private LiveData<LoginInfo> loginInfoLiveData;



    private LoginInfoRespository respository;
    public LoginViewModel(@NonNull Application application) {
        super(application);
        respository = LoginInfoRespository.Instance();
    }

    public LiveData<LoginInfo> getLastUserLoginInfo(){
        loginInfoLiveData = respository.getLastUserLoginInfo();
        return loginInfoLiveData;
    }




}
