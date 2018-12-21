package com.kedacom.truetouch.ok.login.bean;

/**
 * 登录进程
 * Created by zhoutianjie on 2018/12/12.
 */

public class LoginProcess {

    //回调接口

    public enum EmLoginState{
        LOGIN_ING,
        LOGIN_SUCCESS,
        LOGIN_FAILE
    }

    public enum EmLoginFailMsg{

    }

    private EmLoginState emLoginState;


    //登录失败的详细Id，对应EmLoginFailMsg这个枚举
    private EmLoginFailMsg emLoginFailMsg;

    public EmLoginState getLoginState() {
        return emLoginState;
    }

    public void setLoginState(EmLoginState emLoginState) {
        this.emLoginState = emLoginState;
    }


    public EmLoginFailMsg getLoginFailMsg() {
        return emLoginFailMsg;
    }

    public void setLoginFailMsg(EmLoginFailMsg emLoginFailMsg) {
        this.emLoginFailMsg = emLoginFailMsg;
    }
}
