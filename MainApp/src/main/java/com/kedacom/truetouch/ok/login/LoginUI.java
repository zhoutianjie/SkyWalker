package com.kedacom.truetouch.ok.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;


import com.alibaba.android.arouter.facade.annotation.Route;
import com.kedacom.baseutil.LogUtil;
import com.kedacom.truetouch.ok.R;
import com.kedacom.truetouch.ok.base.BaseActivity;
import com.kedacom.truetouch.ok.databinding.ActivityLoginBinding;
import com.kedacom.truetouch.ok.db.entity.LoginInfo;
import com.kedacom.truetouch.ok.login.bean.LoginProcess;

import com.kedacom.truetouch.ok.login.listener.LoginListener;
import com.kedacom.truetouch.ok.util.constant.AppConstant;
import com.kedacom.truetouch.ok.viewmodel.LoginViewModel;

/**
 * Created by zhoutianjie on 2018/11/21.
 */
@Route(path = AppConstant.LOGINACTIVITY_PATH)
public class LoginUI extends BaseActivity {

    private ActivityLoginBinding mBinding;
    private LoginViewModel mLoginViewModel;
    private LoginListener mListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        subscribeToModel(mLoginViewModel);

        mListener = new LoginListener() {
            @Override
            public void Login() {
                mLoginViewModel.Login("","","");
            }
        };

        mBinding.setListener(mListener);

    }



    private void subscribeToModel(LoginViewModel model) {

        model.getLastUserLoginInfo().observe(this, new Observer<LoginInfo>() {
            @Override
            public void onChanged(@Nullable LoginInfo loginInfo) {
                if (null!=loginInfo){
                    mBinding.addressEdit.setText(loginInfo.getAddress());
                    mBinding.accountEdit.setText(loginInfo.getAccount());
                    mBinding.passwordEdit.setText(loginInfo.getPwd());
                }

            }
        });

        model.getLoginProcess().observe(this, new Observer<LoginProcess>() {
            @Override
            public void onChanged(@Nullable LoginProcess loginProcess) {
                if(null!=loginProcess){
                    switch (loginProcess.getLoginState()){
                        case LOGIN_ING:
                            LogUtil.v(TAG,"Login...");
                            break;
                        case LOGIN_SUCCESS:
                            LogUtil.v(TAG,"Login success");
                            break;
                        case LOGIN_FAILE:
                            LogUtil.v(TAG,"Login fail");
                            break;
                            default:
                                break;
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
