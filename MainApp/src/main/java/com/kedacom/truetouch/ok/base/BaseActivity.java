package com.kedacom.truetouch.ok.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.kedacom.baseutil.LogUtil;

/**
 * Created by zhoutianjie on 2018/12/11.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.v(TAG,"onCreate");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.v(TAG,"onConfigurationChanged");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        LogUtil.v(TAG,"onSaveInstanceState");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.v(TAG,"onNewIntent");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.v(TAG,"onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.v(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v(TAG,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.v(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.v(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.v(TAG,"onDestroy");
    }


    /**
     * 启动界面
     * @param path
     */
    protected void startActivity(String path){
        ARouter.getInstance().build(path).navigation();
    }
}
