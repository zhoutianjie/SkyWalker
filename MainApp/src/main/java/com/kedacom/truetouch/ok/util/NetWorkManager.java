package com.kedacom.truetouch.ok.util;

import android.app.Application;
import android.text.TextUtils;

import com.kedacom.baseutil.FormatTransfer;
import com.kedacom.baseutil.NetWorkUtils;
import com.kedacom.kdv.mt.mtapi.bean.TagTNetUsedInfoApi;
import com.kedacom.kdv.mt.mtapi.emun.EmNetAdapterWorkType;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by zhoutianjie on 2018/11/20.
 */

public class NetWorkManager {

    public static TagTNetUsedInfoApi getUserdNetInfo(Application application) {

        String ip = NetWorkUtils.getIpAddr(application.getApplicationContext(), true);

        TagTNetUsedInfoApi netInfo = new TagTNetUsedInfoApi();
        netInfo.emUsedType = EmNetAdapterWorkType.emNetAdapterWorkType_Wifi_Api;
        // netInfo.dwIp = NetWorkUtils.getFirstWiFiIpAddres(TruetouchApplication.getContext());
        try {
            netInfo.dwIp = FormatTransfer.lBytesToLong(InetAddress.getByName(ip).getAddress());
        } catch (Exception e) {
            netInfo.dwIp = FormatTransfer.reverseInt((int) NetWorkUtils.ip2int(ip));
        }
        if (NetWorkUtils.isMobile(application.getApplicationContext())) {
            netInfo.emUsedType = EmNetAdapterWorkType.emNetAdapterWorkType_MobileData_Api;
        }
        String dns = NetWorkUtils.getDns(application.getApplicationContext());
        try {
            if (!TextUtils.isEmpty(dns.trim())) {
                netInfo.dwDns = FormatTransfer.lBytesToLong(InetAddress.getByName(dns).getAddress());
            } else {
                netInfo.dwDns = 0;
            }
        } catch (UnknownHostException e) {
        }
        return netInfo;
    }
}
