package com.kedacom.baseutil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by zhoutianjie on 2018/11/20.
 */

public class NetWorkUtils {

    public static String getIpAddr(Context context, boolean judeWifi) {

        String vpnIp = getVPNIpAddress();
        if (!TextUtils.isEmpty(vpnIp)) {
            return vpnIp;
        }

        String ipaddr = NetWorkUtils.getLocalIpAddress();
        if (null == ipaddr) ipaddr = "";

        if (!judeWifi) {
            return ipaddr;
        }
        // 小米2S(MI 2S)通过NetWorkUtils.getLocalIpAddress()获取的IP始终为：10.0.2.15
        // 对应2S这种情况,可对WiFi单点进行判断
        if (context != null && (TextUtils.isEmpty(ipaddr) || "0".equals(ipaddr) || "0.0.0.0".equals(ipaddr) || judeWifi) && isWiFi(context)) {
            String wifiIpaddr = NetWorkUtils.getNormalWiFiIpAddres(context);
            Log.i("ip", "wifi地址  = " + wifiIpaddr);
            if (!TextUtils.isEmpty(wifiIpaddr.trim()) && !(wifiIpaddr.equals("0")) && !(wifiIpaddr.equals("0.0.0.0"))) {
                return wifiIpaddr;
            }
        }
        return ipaddr;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        Log.e("VPNTEST", "Inet4Address地址  = " + inetAddress.getHostAddress().toString());
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }

        } catch (SocketException ex) {
            Log.e("Wifi IpAddress", ex.toString());
            return "";
        }
        return "";
    }

    public static String getNormalWiFiIpAddres(Context context) {
        if (null == context) {
            return "";
        }

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        int inIP = info.getIpAddress();

        return (inIP & 0xFF) + "." + ((inIP >> 8) & 0xFF) + "." + ((inIP >> 16) & 0xFF) + "." + (inIP >> 24 & 0xFF);
    }

    public static String getVPNIpAddress() {
        NetworkInterface vpnNet = getVNPNet();
        if (null != vpnNet) {
            for (Enumeration<InetAddress> enumIpAddr = vpnNet.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    Log.e("VPNTEST", "vpnNet 地址  = " + inetAddress.getHostAddress().toString());
                    return inetAddress.getHostAddress().toString();
                }
            }
        }
        return null;
    }

    public static NetworkInterface getVNPNet() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    Log.d("VPNTEST", "isVpnUsed() NetworkInterface Name: " + intf.getName());
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return intf; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static long ip2int(String ip) {
        String[] items = ip.split("\\.");
        return Long.valueOf(items[0]) << 24 | Long.valueOf(items[1]) << 16 | Long.valueOf(items[2]) << 8 | Long.valueOf(items[3]);
    }

    public static boolean isMobile(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return false;
        }
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

        if (null == netInfo) {
            return false;
        }

        // 网络可用
        if (isAvailable(netInfo)) {
            int type = netInfo.getType(); // 网络类型
            if (ConnectivityManager.TYPE_MOBILE == type) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAvailable(NetworkInfo netInfo) {
        if (null == netInfo) {
            return false;
        }

        if (netInfo.isAvailable() && netInfo.isConnected()) {
            return true;
        }

        return false;
    }

    public static String getDns(Context context) {
        if (context == null) {
            return "";
        }

        WifiManager my_wifiManager = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE));
        if (my_wifiManager == null) {
            return null;
        }
        DhcpInfo dhcpInfo = my_wifiManager.getDhcpInfo();

        if (dhcpInfo == null) {
            return null;
        }
        String dns = intToIp(dhcpInfo.dns1);

        return dns;
    }

    public static String intToIp(int intIp) {
        return (intIp & 0xFF) + "." +

                ((intIp >> 8) & 0xFF) + "." +

                ((intIp >> 16) & 0xFF) + "." +

                (intIp >> 24 & 0xFF);
    }


    public static boolean isWiFi(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return false;
        }
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

        if (null == netInfo) {
            return false;
        }

        // 网络可用
        if (isAvailable(netInfo)) {
            int type = netInfo.getType(); // 网络类型
            if (ConnectivityManager.TYPE_WIFI == type) {
                return true;
            }
        }

        return false;
    }
}
