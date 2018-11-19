package com.kedacom.kdv.mt.mtapi;

import android.util.Log;

import com.google.gson.Gson;
import com.kedacom.kdv.mt.mtapi.bean.TMtH323PxyCfg;
import com.kedacom.kdv.mt.mtapi.manager.ConfigLibCtrl;
import com.kedacom.kdv.mt.mtapi.manager.MonitorLibCtrl;
import com.kedacom.truetouch.app.TruetouchApplication;
import com.kedacom.truetouch.app.TruetouchGlobal;
import com.kedacom.truetouch.mtc.constant.EmMtModel;
import com.kedacom.truetouch.netmanagement.NetworkManageNMS;
import com.kedacom.truetouch.path.addr.LoginSettingsBean;
import com.kedacom.truetouch.path.addr.LoginSettingsFile;
import com.kedacom.truetouch.vconf.constant.EmConfProtocol;
import com.kedacom.truetouch.vconf.manager.VConferenceManager;
import com.pc.utils.StringUtils;
import com.pc.utils.android.sys.TerminalUtils;
import com.pc.utils.log.PcLog;

import org.json.JSONException;
import org.json.JSONObject;

public class KernalCtrl {

	static {
		try {
			// System.loadLibrary("c");
			// System.loadLibrary("dl");
			System.loadLibrary("gnustl_shared");
			System.loadLibrary("osp");
			System.loadLibrary("kprop");
			System.loadLibrary("kdvlog");

			System.loadLibrary("kdcrypto");//needed library 'libkdcrypto.so' for 'libkdssl.so'
			System.loadLibrary("kdssl");
			System.loadLibrary("pfc");
			System.loadLibrary("bfcp"); //needed library 'libkdssl.so' & 'libpfc.so' for 'libbfcp.so'

			System.loadLibrary("kdvsrtp");// needed library 'libkdvcrypto.so' for 'libkdvsrtp.so' // kdvmedianet依赖该库@张亦欢（网络组）
			System.loadLibrary("kdvdatanet");//needed library 'libkdvsrtp.so' for 'libkdvdatanet.so'
			System.loadLibrary("kdvmedianet");//needed library 'libkdvdatanet.so' for 'libkdvsipadapter2.so'
			System.loadLibrary("kdvsipstack2");
			System.loadLibrary("kdvprotocommon");//be depended on 'kdvsdp','kdv323adapter','kdvsipadapter2'
			System.loadLibrary("kdvsdp");
			System.loadLibrary("kdvsipadapter2");
			System.loadLibrary("kdvsipmodule2");
			System.loadLibrary("mediasdk");
			System.loadLibrary("kdv323stack");

			System.loadLibrary("kdv323adapter");
			System.loadLibrary("kdvsecbiz");
			System.loadLibrary("interface");
		} catch (SecurityException se) {
			Log.e("KernalCtrl", "SecurityException", se);
		} catch (UnsatisfiedLinkError ue) {
			Log.e("KernalCtrl", "UnsatisfiedLinkError", ue);
		} catch (NullPointerException ue) {
			Log.e("KernalCtrl", "NullPointerException", ue);
		} catch (RuntimeException e) {
			Log.e("KernalCtrl", "RuntimeException", e);
		} catch (Exception e) {
			Log.e("KernalCtrl", "Exception", e);
		}
	}

	private KernalCtrl() {

	}


	public static native int SetSysWorkPathPrefix(String strPathPrefix);


	public static native int MtStart(int nEmModel, String strModelName, String strSoftwareVer, String strOEMName);


	private static native int MtStop();

}
