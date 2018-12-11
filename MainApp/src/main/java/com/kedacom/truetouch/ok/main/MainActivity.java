package com.kedacom.truetouch.ok.main;



import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kedacom.truetouch.ok.R;
import com.kedacom.truetouch.ok.base.BaseActivity;
import com.kedacom.truetouch.ok.util.constant.AppConstant;

@Route(path = AppConstant.MAINACTIVITY_PATH)
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
