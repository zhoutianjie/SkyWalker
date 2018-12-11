package com.kedacom.kdv.mt.mtapi;


import android.util.Log;

public class KernalCtrl {

	static {
		try {
			System.loadLibrary("gnustl_shared");
			System.loadLibrary("osp");
			System.loadLibrary("kprop");
			System.loadLibrary("kdvlog");
			System.loadLibrary("kdcrypto");
			System.loadLibrary("kdssl");
			System.loadLibrary("pfc");
			System.loadLibrary("bfcp");
			System.loadLibrary("kdvsrtp");
			System.loadLibrary("kdvdatanet");
			System.loadLibrary("kdvmedianet");
			System.loadLibrary("kdvsipstack2");
			System.loadLibrary("kdvprotocommon");
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
