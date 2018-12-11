package com.kedacom.truetouch.ok.util;

import com.kedacom.baseutil.TerminalUtils;
import com.kedacom.kdv.mt.mtapi.emun.EmMtResolution;

/**
 * Created by zhoutianjie on 2018/11/20.
 */

public class VconfManager {

    private VconfManager(){

    }

    private static class SingleTonHolder{
        private static final VconfManager INSTANCE = new VconfManager();
    }

    public static VconfManager Instance(){
        return SingleTonHolder.INSTANCE;
    }

    public int getSendResolutionByCallRate(int callRate){
        if (callRate <= 320) {
            return EmMtResolution.emMtCIF_Api.ordinal();
        } else if (callRate <= 512) {
            return EmMtResolution.emMt4CIF_Api.ordinal();
        } else {
            // android 4核 1.2G及以上
            if (TerminalUtils.getNumCores() >= 4 && TerminalUtils.getCPUFrequencyMax() > 1.2 * 1000000) {
                return EmMtResolution.emMtHD720p1280x720_Api.ordinal();
            } else {
                return EmMtResolution.emMt4CIF_Api.ordinal();
            }
        }
    }

    public int getRecResolutionByCallRate(int callRate) {
        if (callRate <= 320) {
            return EmMtResolution.emMtCIF_Api.ordinal();
        } else if (callRate <= 512) {
            return EmMtResolution.emMt4CIF_Api.ordinal();
        } else {
            return EmMtResolution.emMtHD720p1280x720_Api.ordinal();
        }
    }




}
