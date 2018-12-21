package com.kedacom.truetouch.ok.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import android.support.annotation.NonNull;
;

import com.kedacom.truetouch.ok.db.entity.LoginInfo;
import com.kedacom.truetouch.ok.login.bean.LoginProcess;

import com.kedacom.truetouch.ok.repository.LoginInfoRespository;


/**
 * Created by zhoutianjie on 2018/12/11.
 */

public class LoginViewModel extends AndroidViewModel{

    /**
     * 所有的LiveData 都放在ViewModel里面,respository 内部不再包含内部LiveData相关的引用
     */
    private LiveData<LoginInfo> loginInfoLiveData;
    private MutableLiveData<LoginProcess> loginProcessLiveData = new MutableLiveData<>();
    private LoginProcess mLoginProcess;


    private LoginInfoRespository respository;
    public LoginViewModel(@NonNull Application application) {
        super(application);
        respository = LoginInfoRespository.Instance();
        mLoginProcess = new LoginProcess();
    }

    public LiveData<LoginInfo> getLastUserLoginInfo(){
        loginInfoLiveData = respository.getLastUserLoginInfo();
        return loginInfoLiveData;
    }

    public LiveData<LoginProcess> getLoginProcess(){

        return loginProcessLiveData;
    }


    public void Login(String address,String account,String pwd){

        mLoginProcess.setLoginState(LoginProcess.EmLoginState.LOGIN_ING);
        loginProcessLiveData.setValue(mLoginProcess);




    }

    public void saveLoginInfo(String address,String account,String pwd){

    }

    public void clearAddress(){

    }

    public void clearAccount(){

    }

    public void clearPassword(){

    }

}
