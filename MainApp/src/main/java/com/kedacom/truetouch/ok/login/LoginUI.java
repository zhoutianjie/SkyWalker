package com.kedacom.truetouch.ok.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kedacom.truetouch.ok.R;
import com.kedacom.truetouch.ok.base.BaseActivity;
import com.kedacom.truetouch.ok.databinding.ActivityLoginBinding;
import com.kedacom.truetouch.ok.db.entity.LoginInfo;
import com.kedacom.truetouch.ok.util.constant.AppConstant;
import com.kedacom.truetouch.ok.viewmodel.LoginViewModel;

/**
 * Created by zhoutianjie on 2018/11/21.
 */
@Route(path = AppConstant.LOGINACTIVITY_PATH)
public class LoginUI extends BaseActivity {

    private ActivityLoginBinding mBinding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        LoginViewModel loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        subscribeToModel(loginViewModel);

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
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
