package com.kedacom.truetouch.ok.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kedacom.truetouch.ok.base.BaseActivity;
import com.kedacom.truetouch.ok.util.constant.AppConstant;

/**
 * Created by zhoutianjie on 2018/11/21.
 */
@Route(path = AppConstant.LOGINACTIVITY_PATH)
public class LoginUI extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
