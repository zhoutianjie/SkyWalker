package com.kedacom.truetouch.ok.repository.jnicallback;

/**
 * JNI层对应组件的全局回调
 * Created by zhoutianjie on 2018/12/20.
 */

public interface MtapiCallback {

    void callback(String msgId,String msgContent);
}
