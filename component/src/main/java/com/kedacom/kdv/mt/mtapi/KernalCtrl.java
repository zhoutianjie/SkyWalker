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

	/**
	 * 启动mt终端
	 */
	public static void mtStart() {
		// String mediaLibDir = new TTPathManager().getMediaLibDir();
		// SetSysWorkPathPrefix(mediaLibDir);

		//默认值 nEmModel skyAndroidPhone,strModelName MTINFO_SKYWALKER
		int nEmModel = EmMtModel.emSkyAndroidPhone.value;
		String strModelName = TruetouchGlobal.MTINFO_SKYWALKER;

		if (TruetouchApplication.getApplication().isMovisionPlatform()) {
			if (TruetouchApplication.getApplication().isTablet()) {
				nEmModel = EmMtModel.emTrueTouchAndroidPad.value;
			} else {
				nEmModel = EmMtModel.emTrueTouchAndroidPhone.value;
			}
			strModelName = TruetouchGlobal.MTINFO_TRUETOUCH;
		} else {
			String packageName = TruetouchApplication.getApplication().getPackageName();
			if(StringUtils.equalsStr(packageName,"com.kedacom.truetouch.sky",false)){
				if (TruetouchApplication.getApplication().isTablet()) {
					nEmModel = EmMtModel.emSkyAndroidPad.value;
					strModelName = TruetouchGlobal.MTINFO_SKYWALKER_PAD;
				} else {
					nEmModel = EmMtModel.emSkyAndroidPhone.value;
					strModelName = TruetouchGlobal.MTINFO_SKYWALKER;
				}

			}else if(StringUtils.equalsStr(packageName,"com.kedacom.truetouch.encrypt",false)){
				if (TruetouchApplication.getApplication().isTablet()) {
					nEmModel = EmMtModel.emSkyAndroidPad_s_Api.value;
				} else {
					nEmModel = EmMtModel.emSkyAndroidPhone_s_Api.value;
				}
				strModelName = TruetouchGlobal.MTINFO_SKYWALKER;
			}

		}

		String visionName = TerminalUtils.versionName(TruetouchApplication.getContext(), "5.0.0.0.0");
		String compileTime = TruetouchApplication.getApplication().getApkTimestamp();

		// 摩云平台 启动终端时间较长，3s
		MtStart(nEmModel, strModelName, TruetouchApplication.getContext().getString(R.string.android_version, visionName, compileTime), TruetouchApplication.getApplication().getOEMName());
		MtcLib.start();
		// 检测服务器保存的数据是否是h323，是的话设置成非h323
		// isMtInitH323();
		// MtServiceCfgCtrl.sysStartService();
		setCallCapInitCmd();

		NetworkManageNMS.setUserdNetInfo();//设置网络信息，修复走代理时会议中无法接受码率问题
	}

	/**
	 * 第一次设置能力集 SDM-00093364 2017/11/02（第一次启动设置能力集 by 许成磊）
	 */
	private static void setCallCapInitCmd() {
		short callRate = (short) VConferenceManager.confCallRete(null);
		//SDM-00096010 begin 分别初始化sip和323能力集 by 许成磊
		MonitorLibCtrl.setCallCapPlusCmd(VConferenceManager.getSendResolutionByCallRate(callRate), VConferenceManager.getRecResolutionByCallRate(callRate), EmConfProtocol.emsip.ordinal());
		MonitorLibCtrl.setCallCapPlusCmd(VConferenceManager.getSendResolutionByCallRate(callRate), VConferenceManager.getRecResolutionByCallRate(callRate), EmConfProtocol.em323.ordinal());
		//SDM-00096010 end
	}

	// 设置是否启用业务层的日志功能（libinterface.so里面默认启用了该模块liblogclient.a）。
	public static void enableLogclient(boolean isEnable){
		JSONObject jobj = new JSONObject();
		try {
			jobj.put("basetype", isEnable);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ConfigLibCtrl.setLogCfgCmd(jobj.toString());
	}

	@Deprecated
	private static void isMtInitH323() {
		LoginSettingsBean loginSettingsBean = new LoginSettingsFile().getLoginSettingsBeanInfo();
		if (null != loginSettingsBean) {
			// 从数据库获取当前 是否注册了代理
			StringBuffer H323PxyStringBuf = new StringBuffer();
			ConfigLibCtrl.getH323PxyCfg(H323PxyStringBuf);
			String h323Pxy = H323PxyStringBuf.toString();
			TMtH323PxyCfg tmtH323Pxy = new Gson().fromJson(h323Pxy, TMtH323PxyCfg.class);
			// { "achNumber" : "", "achPassword" : "", "bEnable" : true, "dwSrvIp" : 1917977712, "dwSrvPort" : 2776 }
			if (null != tmtH323Pxy) {
				boolean bEnable = tmtH323Pxy.bEnable;
				if (!loginSettingsBean.isH323() && bEnable) {
					if (PcLog.isPrint) {
						Log.i("KernalCtrl", "数据库是否代理bEnable = " + bEnable);
					}
					ConfigLibCtrl.setAudioPriorCfgCmd(false);
					// 取消代理
					ConfigLibCtrl.setH323PxyCfgCmd(false, false, 0);
				}
			}
		}
	}

	/**
	 * 退出mt终端
	 */
	public static void mtStop() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				MtcLib.Quit();
				MtStop();
			}
		}).start();
	}

	/**
	 * 
	 */
	private KernalCtrl() {

	}

	/**
	 * SetSysWorkPathPrefix
	 * @brief 设置配置文件保存路径前缀
	 * @param [in] String 路径前缀字串
	 * @return int 返回启动结果
	 * @note
	 */
	public static native int SetSysWorkPathPrefix(String strPathPrefix);

	/**
	 * MtStart
	 * @brief 启动一个mt终端
	 * @param [in] int nEmModel 终端型号
	 * @param [in] String strModelName 终端型号名称
	 * @param [in] String strSoftwareVer 软件版本
	 * @return int 返回启动结果
	 * @note MtStop enum EmMtModel_Api { emModelBegin_Api = 0, emTrueLink_Api = 1, ///<致邻 emIPad_Api = 2, ///<iPad
	 *       emIPhone_Api = 3, ///<iPhone emIPhone4s_Api = 4, ///<iPhone4S emIPhone5_Api = 5, ///<iPhone5
	 *       emAndroidPad_Api = 6, ///<android平板 emAndroidPhone_Api = 7, ///<android手机 emX500_Api = 8, ///<X500 };
	 */
	public static native int MtStart(int nEmModel, String strModelName, String strSoftwareVer, String strOEMName);

	/**
	 * MtStop
	 * @brief 停止一个mt终端
	 * @param [in] void
	 * @return int 返回启动结果
	 * @note MtStart
	 */
	private static native int MtStop();

	/**
	 * 使用国密sm3算法计算摘要信息。
	 * @param path 待计算的文件路径
	 * @return 计算出来的摘要信息
	 * */
	public static native String SM3(String path);

	/**
	 * 使用SM4加密
	 * @param plaintTextStr
	 * @return
	 */
	public static native byte[] SM4Encrypt(String plaintTextStr);

	/**
	 * 使用SM4解密
	 * @param bytes
	 * @param length
	 * @return
	 */
	public static native String SM4Dncrypt(byte[] bytes,int length);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// MtStart(EmMtModel.emAndroidPhone.ordinal(),
		// TruetouchGlobal.MTINFO_SKYWALKER,
		// TerminalUtils.versionName(TruetouchApplication.getContext(),
		// "5.0.0"));

		// MtStop();
	}
}
