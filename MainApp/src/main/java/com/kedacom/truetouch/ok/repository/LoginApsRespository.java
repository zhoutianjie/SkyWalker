package com.kedacom.truetouch.ok.repository;

/**
 * aps 登录相关仓库
 * Created by zhoutianjie on 2018/12/11.
 */

public class LoginApsRespository {

    private LoginApsRespository() {
    }

    private static class SingleTonHolder{
        private static final LoginApsRespository INSTANCE = new LoginApsRespository();
    }

    public static LoginApsRespository Instance(){
        return SingleTonHolder.INSTANCE;
    }
}
