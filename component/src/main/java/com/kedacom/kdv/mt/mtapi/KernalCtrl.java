package com.kedacom.kdv.mt.mtapi;

public class KernalCtrl {

	private KernalCtrl() {

	}


	public static native int SetSysWorkPathPrefix(String strPathPrefix);


	public static native int MtStart(int nEmModel, String strModelName, String strSoftwareVer, String strOEMName);


	private static native int MtStop();

}
