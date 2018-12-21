package com.kedacom.truetouch.ok.repository.jnicallback;

import android.os.Build;
import android.util.ArrayMap;

import com.kedacom.truetouch.ok.repository.dataparser.DataParser;
import com.kedacom.truetouch.ok.repository.dataparser.LoginApsParser;
import com.kedacom.truetouch.ok.repository.msg.EmMsg;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局的组件回调对象
 * Created by zhoutianjie on 2018/12/20.
 */

public class CompomentCallback implements MtapiCallback{

    private Map<String,DataParser> Observers;
    private DataParser dataParser;

    private CompomentCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Observers = new ArrayMap<>();
        }else {
            Observers = new HashMap<>();
        }
        register();
    }

    private static class SingleTonHolder{
        private static final CompomentCallback INSTANCE = new CompomentCallback();
    }

    public static CompomentCallback Instance(){
        return SingleTonHolder.INSTANCE;
    }

    //订阅
    private void register(){
        Observers.put(EmMsg.ApsLoginResultNtf.toString(),new LoginApsParser());
    }



    @Override
    public void callback(String msgId, String msgContent) {

        if((dataParser = Observers.get(msgId))!=null){
            dataParser.parseMsg(msgContent);
        }
    }

    public DataParser getDataParser(String msgId){
        return Observers.get(msgId);
    }





}
