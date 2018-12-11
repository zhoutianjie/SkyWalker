package com.kedacom.truetouch.ok.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.kedacom.truetouch.ok.repository.LoginInfoRespository;

/**
 * Created by zhoutianjie on 2018/11/21.
 */

public class LaunchViewModel extends AndroidViewModel {

    private LiveData<Boolean> directToMainUI;
    private LoginInfoRespository respository;
    public LaunchViewModel(@NonNull Application application) {
        super(application);
        respository = LoginInfoRespository.Instance();
    }

    public LiveData<Boolean> directToMainUI(){
        directToMainUI = respository.directToMainUI();
        return directToMainUI;
    }
}
