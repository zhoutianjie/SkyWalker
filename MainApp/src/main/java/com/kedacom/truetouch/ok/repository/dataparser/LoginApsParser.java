package com.kedacom.truetouch.ok.repository.dataparser;

import com.kedacom.baseutil.LogUtil;
import com.kedacom.kdv.mt.mtapi.bean.ApsLoginResultNtf;

/**
 * Created by zhoutianjie on 2018/12/20.
 */

public class LoginApsParser implements DataParser {

    public interface LoginApsResultListener{
        void acquireResult(ApsLoginResultNtf result);
    }

    private LoginApsResultListener listener;

    //调用功能接口之前 或者 接收通知之前设置对应接口的监听
    public void setListener(LoginApsResultListener listener) {
        this.listener = listener;
    }

    /**
     * 处理LoginAps的响应，并通过回调返回给respository
     * Parser只负责解析，不负责业务逻辑，业务逻辑有respository处理
     * @param msgContent
     */
    @Override
    public void parseMsg(String msgContent) {
        LogUtil.v("LoginApsParser",msgContent);
        ApsLoginResultNtf resultNtf = (ApsLoginResultNtf) new ApsLoginResultNtf().fromJson(msgContent);
        if(listener!=null){
            listener.acquireResult(resultNtf);
        }
    }


}
