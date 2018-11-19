package com.kedacom.kdv.mt.mtapi;


public class MtcLib {

	private MtcLib() {
	}

	public static native void Start(boolean bIsMtcMode);

	public static native void DisConnect(int nSessionID);

	public static native int GetSessionByIp(StringBuffer strMtIp);


	public static native int GetSessionByIdx(int nIdx);

	public static native int GetMaxSessionCnt();

	public static native int GetUsedSessionCnt();

	public static native void Quit();

	public static native void Setcallback(Object callback);

	public static native String GetDateTime();

}
