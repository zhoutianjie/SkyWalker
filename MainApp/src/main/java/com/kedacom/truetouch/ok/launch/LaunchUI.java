package com.kedacom.truetouch.ok.launch;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.kedacom.truetouch.ok.R;
import com.kedacom.truetouch.ok.base.BaseActivity;
import com.kedacom.truetouch.ok.databinding.ActivityLauncherBinding;
import com.kedacom.truetouch.ok.util.constant.AppConstant;
import com.kedacom.truetouch.ok.viewmodel.LaunchViewModel;


/**
 * Created by zhoutianjie on 2018/11/20.
 */

public class LaunchUI extends BaseActivity {


    private ActivityLauncherBinding binding;
    private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_launcher);
        imageView = binding.logoImg;

        LaunchViewModel launchViewModel = ViewModelProviders.of(this).get(LaunchViewModel.class);
        subscribeToModel(launchViewModel);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void subscribeToModel(LaunchViewModel model) {
        model.directToMainUI().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean aBoolean) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(imageView,"alpha",0.3f,1.0f);
                animator.setDuration(2000);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(aBoolean){
                            //进入主界面 跳转
                            startActivity(AppConstant.MAINACTIVITY_PATH);
                            finish();

                        }else {
                            //进入登录界面
                            startActivity(AppConstant.LOGINACTIVITY_PATH);
                            finish();
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                animator.start();

            }
        });
    }
}
