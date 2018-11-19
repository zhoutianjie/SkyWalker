/**
 * @(#)MyMtcCallback.java   2014-10-21
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.kedacom.kdv.mt.mtapi;

import android.util.Log;

import com.google.gson.Gson;
import com.kedacom.kdv.mt.mtapi.bean.MtApiHead;
import com.kedacom.kdv.mt.mtapi.bean.TImSn;
import com.kedacom.kdv.mt.mtapi.bean.TMTAudioPriorCfg;
import com.kedacom.kdv.mt.mtapi.calback.cw.ImChatMtcCallback;
import com.kedacom.kdv.mt.mtapi.calback.im.ImChatRoomMtcCallback;
import com.kedacom.kdv.mt.mtapi.calback.im.ImMtcCallback;
import com.kedacom.kdv.mt.mtapi.calback.im.LoginMtcCallback;
import com.kedacom.kdv.mt.mtapi.calback.mt.MeetingScheduleMtcCallback;
import com.kedacom.kdv.mt.mtapi.calback.mt.PlatformMtcCallback;
import com.kedacom.kdv.mt.mtapi.calback.mt.StructureMtcCallback;
import com.kedacom.kdv.mt.mtapi.calback.vconf.DCMtcCallback;
import com.kedacom.kdv.mt.mtapi.calback.vconf.LiveMtcCallback;
import com.kedacom.kdv.mt.mtapi.calback.vconf.RecordMtcCallback;
import com.kedacom.kdv.mt.mtapi.calback.vconf.VconfMtcCallback;
import com.kedacom.kdv.mt.mtapi.calback.vconf.VodMtCallback;
import com.kedacom.kdv.mt.mtapi.jni.model.EmRsp;
import com.kedacom.kdv.mt.mtapi.manager.ConfLibCtrl;
import com.kedacom.kdv.mt.mtapi.manager.ConfigLibCtrl;
import com.kedacom.kdv.mt.mtapi.manager.ImLibCtrl;
import com.kedacom.truetouch.app.KDObjectPool;
import com.kedacom.truetouch.app.TruetouchApplication;
import com.kedacom.truetouch.app.TruetouchGlobal;
import com.kedacom.truetouch.app.constant.EmModle;
import com.kedacom.truetouch.contact.manager.ContactManger;
import com.kedacom.truetouch.content.MtVConfInfo;
import com.kedacom.truetouch.main.SlidingMenuManager;
import com.kedacom.truetouch.main.controller.MainMeeting;
import com.kedacom.truetouch.mtc.BaseCallbackHandler;
import com.kedacom.truetouch.netmanagement.NetworkManageNMS;
import com.kedacom.truetouch.upgrade.UpgradeMtcCallback;
import com.kedacom.truetouch.vconf.bean.ConfDetailInfo;
import com.kedacom.truetouch.vconf.constant.EmMtCallDisReason;
import com.kedacom.truetouch.vconf.dao.ConfDetailInfoDao;
import com.kedacom.truetouch.vconf.manager.VConferenceManager;
import com.pc.app.PcAppStackManager;
import com.pc.app.base.PcActivity;
import com.pc.utils.StringUtils;
import com.pc.utils.log.PcLog;
import com.pc.utils.network.NetWorkUtils;
import com.pc.utils.toast.PcToastUtil;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Mtc Callback
 * @author chenj
 * @date 2014-10-21
 */

public class MyMtcCallback extends MtcCallback implements IMtcCallback {

	public static final int HTTPCODE_SUCCESSID = 200;
	public static final int PLATFORMAPI_SUCCESSID = 1000;

	public static final String KEY_MTAPI = "mtapi";
	public static final String KEY_HEAD = "head";
	public static final String KEY_HEAD_EVENTID = "eventid";
	public static final String KEY_HEAD_EVENTNAME = "eventname";
	public static final String KEY_BODY = "body";
	public static final String KEY_basetype = "basetype";

	public static final String KEY_dwHandle = "dwHandle";
	public static final String KEY_dwErrID = "dwErrID";
	public static final String KEY_dwErrorID = "dwErrorID";
	public static final String KEY_AssParam = "AssParam";
	public static final String KEY_MainParam = "MainParam";
	public final static String KEY_TERRINFO = "tErrInfo";
	public static final String KEY_Reserved = "dwReserved";

	public String JNIHEADER = "com.kedacom.truetouch.mtc.jni.";

	// 停止接收Callback消息
	public boolean stopHanldeJni = false;

	private static MyMtcCallback mMyMtcCallback;

	static {
		mMyMtcCallback = new MyMtcCallback();
	}

	// => For SDM-00037552 added by gaofan_kd7331 2015-10-13 09:56:51
	private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	private static KDObjectPool gsonPool = new KDObjectPool(Gson.class);

	private static ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

	static {
		gsonPool.setIsGrowMode(true); // 设为自动生长模式
	}

	// <= For SDM-00037552 added by gaofan_kd7331 2015-10-13 09:56:51

	// private Handler handler = new Handler();

	public synchronized static MyMtcCallback getInstance() {
		if (null == mMyMtcCallback) {
			mMyMtcCallback = new MyMtcCallback();
		}

		return mMyMtcCallback;
	}

	public static void releaseInstance() {
		mMyMtcCallback = null;
	}

	/**
	 */
	private MyMtcCallback() {
		super();
		stopHanldeJni = false;
	}

	/**
	 * @see com.kedacom.kdv.mt.mtapi.MtcCallback#Callback(String)
	 */
	@Override
	public void Callback(String strMsg) {
		super.Callback(strMsg);

		callback(strMsg);
	}

	// private JSONObject jsonBodyObj;

	/**
	 * { "mtapi":{ "head":{ "eventid":2147, eventname:"RegResultNtf", "SessionID": "1" }, "body":{ "basetype" : true } }
	 * }
	 * @see com.kedacom.truetouch.mtc.IMtcCallback#callback(String)
	 */
	public void callback(String result) {
		if (StringUtils.isNull(result)) {
			return;
		}


//		PcLog.syso("-------------callback 1-----------------");
//		PcLog.syso(result);
//		PcLog.syso("-------------callback 2-----------------");

		String body = "";
		JSONObject jsonBodyObj = null;
		MtApiHead mtApiHead = null;
		try {
			JSONObject jsonObj = new JSONObject(result);
			jsonObj = jsonObj.getJSONObject(KEY_MTAPI);

			body = jsonObj.getString(KEY_BODY);
			jsonBodyObj = jsonObj.getJSONObject(KEY_BODY);

			if (null == jsonBodyObj) { // added by gaofan_kd7331 2016-2-2
				return;
			}

			// => For SDM-00037552 added by gaofan_kd7331 2015-10-13 09:56:51
			Gson gson = (Gson) gsonPool.getObject();
			if (null == gson) {
				System.out.printf("gsonPool.getObject() failed! `poolsize' has reached %d.\n", gsonPool.size());
				return;
			}
			mtApiHead = gson.fromJson(jsonObj.getString(KEY_HEAD), MtApiHead.class);
			gsonPool.returnObject(gson);
			// <= For SDM-00037552 added by gaofan_kd7331 2015-10-13 09:56:51
		} catch (Exception e) {
			if (PcLog.isPrint) e.printStackTrace();
		}

		// String eventType = getMessageChildFirstTag2(result);
		// if (StringUtils.isNull(eventType)) {
		// return;
		// }

		try {
			String eventname = mtApiHead.eventname;

			// @formatter:off 注销/退出停止接受消息（除下列消息之外）
//			if (stopHanldeJni && 
//					(
//						!(EmRsp.SrvStartResultNtf.toString().equalsIgnoreCase(eventname)) 			||
//						!(EmRsp.StackInitResNtf.toString().equalsIgnoreCase(eventname)) 			||
//						!(EmRsp.SetH323PxyCfgNtf.toString().equalsIgnoreCase(eventname)) 			||
//						!(EmRsp.SetAudioPriorCfgNtf.toString().equalsIgnoreCase(eventname)) 		||
//						!(EmRsp.SetAudioPrecedenceCfgNtf.toString().equalsIgnoreCase(eventname)) 	||
//						!(EmRsp.SetXAPListCfgNtf.toString().equalsIgnoreCase(eventname))			||
//						!(EmRsp.ApsLoginResultNtf.toString().equalsIgnoreCase(eventname))			||
//						!(EmRsp.ImLoginRsp.toString().equalsIgnoreCase(eventname))					||
//						!(EmRsp.RegResultNtf.toString().equalsIgnoreCase(eventname))				||
//						!(EmRsp.ImDisconnectedNtf.toString().equals(eventname))					||
//						!(EmRsp.ImUserDisconnectNtf.toString().equals(eventname))					||
//						!(EmRsp.ImNotifySecurityNtf.toString().equalsIgnoreCase(eventname))		||
//						!(EmRsp.SetCSUCfgNtf.toString().equalsIgnoreCase(eventname))				||
//						!(EmRsp.SetLoginPlatformSrvCfgNtf.toString().equalsIgnoreCase(eventname))
//					)
//				) {
//				return;
//			}
			// @formatter:on

			// 启动模块
			if (EmRsp.SrvStartResultNtf.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseSrvStartResult(jsonBodyObj);

			}
			//
			else if (EmRsp.SetLoginPlatformSrvCfgNtf.toString().equalsIgnoreCase(eventname)) {

			}

			// 初始化协议栈
			else if (EmRsp.StackInitResNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) {
					jsonBodyObj.getBoolean(KEY_basetype);
				}
			}
			// 设置H323代理模式
			else if (EmRsp.SetH323PxyCfgNtf.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseSetH323PxyCfgNtf(mtApiHead.sessionId, jsonBodyObj);
			}
			//
			else if (EmRsp.SetAudioPrecedenceCfgNtf.toString().equalsIgnoreCase(eventname)) {
				try {
					boolean isAudioPrecedence = jsonBodyObj.getBoolean(MyMtcCallback.KEY_basetype);
					new MtVConfInfo().putSetAudioPrecedence(isAudioPrecedence);
				} catch (Exception e) {
				}
			}
			// 设置音频优选
			else if (EmRsp.SetAudioPriorCfgNtf.toString().equalsIgnoreCase(eventname)) {
				try {
					TMTAudioPriorCfg audioPriorCfg = new TMTAudioPriorCfg().fromJson(body);
					if (null != audioPriorCfg) {
						new MtVConfInfo().putAudioPriorCfg(audioPriorCfg);
					}
				} catch (Exception e) {
				}
			}
			// 查看端口详情
			else if (EmRsp.DiagnoseGetPortInfo_Rsp.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseDiagnoseGetPortInfo(jsonBodyObj);
			}
			// XAPList配置
			else if (EmRsp.SetXAPListCfgNtf.toString().equalsIgnoreCase(eventname)) {
				// CfgNtfMtcCallback.parseXAPListCfgInfo(jsonBodyObj);
			}
			// APS
			else if (EmRsp.ApsLoginResultNtf.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseApsLoginResultNtf(mtApiHead.sessionId, jsonBodyObj);
			}
			// Im
			// ---------------------------------------------------------------------------
			// Im登录
			else if (EmRsp.ImLoginRsp.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseImLoginRsp(jsonBodyObj);
			}
			// Im Discconnected
			else if (EmRsp.ImDisconnectedNtf.toString().equals(eventname)) {
				LoginMtcCallback.parseImDisconnectedNtf(jsonBodyObj);
			}
			// 断开所有服务器通知
			else if (EmRsp.ImUserDisconnectNtf.toString().equals(eventname)) {
				LoginMtcCallback.parseImUserDisconnectNtf(jsonBodyObj);
			}

			// Im准备好获取数据
			else if (EmRsp.ImMembersDataReadyNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has("dwHandle")) {
					jsonBodyObj.getString("dwHandle");
				}

				// 查询联系人组
				// ImLibCtrl.imQueryGroupInfoReq();
				ImLibCtrl.imQuerySubGroupInfoByGroupSnReq(new TImSn());
			}

			// Im Chatroom Service有效
			else if (EmRsp.ImChatroomServiceAvailableNtf.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) ImMtcCallback.parseImChatroomServiceAvailableNtf(jsonBodyObj);
			}

			// 收到通知XMPP已准备好接受组织架构和离线讨论组创建、成员、成员状态等消息推送
			else if (EmRsp.ImSetReadyRsp.toString().equalsIgnoreCase(eventname)) {
			}

			// Im 已准备好配置数据
			else if (EmRsp.ImConfigDataReadyNtf.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImConfigDataReadyNtfNtf(jsonBodyObj);
			}

			// 通知安全登录(同一账号在其他地方登录)
			else if (EmRsp.ImNotifySecurityNtf.toString().equalsIgnoreCase(eventname)) {
				if (PcLog.isPrint) {
					Log.e("Login", result);
				}
				LoginMtcCallback.parseImNotifySecurityNtf(jsonBodyObj);
			}

			// Im 查询组信息
			else if (EmRsp.ImQueryGroupInfoRsp.toString().equalsIgnoreCase(eventname)) {
			}
			// Im 查询组结束
			else if (EmRsp.ImQueryGroupInfo_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
			}
			// 查询组信息
			else if (EmRsp.ImQuerySubGroupInfoByGroupSn_Rsp.toString().equalsIgnoreCase(eventname)) {
				final JSONObject jsonBodyObjs = jsonBodyObj;
				if (!stopHanldeJni) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							ImMtcCallback.parseImQuerySubGroupInfoByGroupSnRsp(jsonBodyObjs);
						}
					}).start();
				}
			}
			// 查询组Finish信息
			else if (EmRsp.ImQuerySubGroupInfoByGroupSn_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				final JSONObject jsonBodyObjs = jsonBodyObj;
				if (!stopHanldeJni) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							ImMtcCallback.parseImQuerySubGroupInfoByGroupSnFinRsp(jsonBodyObjs);
						}
					}).start();
				}
			}
			// 查询某组下的联系人列表信息
			else if (EmRsp.ImQueryMemberInfoByGroupSn_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					final JSONObject jsonBodyObjs = jsonBodyObj;
					// => For SDM-00037552 added by gaofan_kd7331 2015-10-15 21:26:18
					cachedThreadPool.execute(new Runnable() {

						@Override
						public void run() {
							ImMtcCallback.parseImQueryMemberInfoByGroupSnRsp(jsonBodyObjs);
						}
					});
					// <= For SDM-00037552 added by gaofan_kd7331 2015-10-15 21:26:18
				}
			}
			// 查询某组下的联系人列表Finish信息
			else if (EmRsp.ImQueryMemberInfoByGroupSn_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					final JSONObject jsonBodyObjs = jsonBodyObj;

					// => For SDM-00037552 added by gaofan_kd7331 2015-10-15 21:26:18
					cachedThreadPool.execute(new Runnable() {

						@Override
						public void run() {
							boolean finish = ImMtcCallback.parseImQueryMemberInfoByGroupSnFinRsp(jsonBodyObjs);

							if (finish && !stopHanldeJni) {
								SlidingMenuManager.refreshMainContactsView();
								ImLibCtrl.queryMemberOnlineStateByGroupSn();
								// RmtContactLibCtrl.queryUserInfoReq(userNos);
							}
						}
					});
					// <= For SDM-00037552 added by gaofan_kd7331 2015-10-15 21:26:18
				}
			}
			// 查询组成员列表在线状态
			else if (EmRsp.ImQueryOnlineStateByGroupSnExNtf.toString().equalsIgnoreCase(eventname)) {
				final JSONObject jsonBodyObjs = jsonBodyObj;
				// => For SDM-00037552 added by gaofan_kd7331 2015-10-15 21:26:18
				cachedThreadPool.execute(new Runnable() {

					@Override
					public void run() {
						ImMtcCallback.parseImQueryOnlineStateByGroupSnExNtf(jsonBodyObjs);
					}
				});
				// <= For SDM-00037552 added by gaofan_kd7331 2015-10-15 21:26:18
			}
			// 查询组成员列表在线状态结束通知
			else if (EmRsp.ImQueryOnlineStateByGroupSnExFinNtf.toString().equalsIgnoreCase(eventname)) {
				final JSONObject jsonBodyObjs = jsonBodyObj;

				// => For SDM-00037552 added by gaofan_kd7331 2015-10-15 21:26:18
				cachedThreadPool.execute(new Runnable() {

					@Override
					public void run() {
						if (!stopHanldeJni) ImMtcCallback.parseImQueryOnlineStateByGroupSnExFinNtf(jsonBodyObjs);

						// // 请求进入之前保存的固定讨论组
						// ImLibCtrl.queryEnterSavedPersistentChatroomsReq();
						if (!stopHanldeJni) SlidingMenuManager.notifyDataSetChangedMainMessage(false, false);

						if (!stopHanldeJni) ContactManger.queryMemberInfoFromTmpList(TruetouchApplication.getContext());
					}
				});
				// <= For SDM-00037552 added by gaofan_kd7331 2015-10-15 21:26:18

			}

			// 修改自己的状态
			else if (EmRsp.ImModifySelfStateRsp.toString().equals(eventname)) {

			}
			// 获取IM服务器的时间
			else if (EmRsp.ImGetServerTimeRsp.toString().equals(eventname)) {
				ImMtcCallback.parseGetServerTime(jsonBodyObj);
			}

			// Im 状态改变 Member OnlineState
			else if (EmRsp.ImPushMemberStatusNtf.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) ImMtcCallback.parseImPushMemberStatusNtf(jsonBodyObj);
			}
			// 加为临时关注
			else if (EmRsp.ImTempSubscribeRsp.toString().equals(eventname)) {
				ImMtcCallback.parseTempSubscribe(jsonBodyObj);
			}
			// 获取成员状态扩展信息响应
			else if (EmRsp.ImGetUsersStateExRsp.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImGetUsersStateExRsp(jsonBodyObj);
			}
			// 获取成员状态扩展信息通知
			else if (EmRsp.ImGetUsersStateExNtf.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImGetUsersStateExNtf(jsonBodyObj);
			}
			// 获取成员状态扩展信息结束通知
			else if (EmRsp.ImGetUsersStateExFinNtf.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImGetUsersStateExFinNtf(jsonBodyObj);
			}
			// 通知通知修改组
			else if (EmRsp.ImGroupModifyNtf.toString().equals(eventname)) {
				ImMtcCallback.parseImGroupModifyNtf(jsonBodyObj);
			}
			// 修改组
			else if (EmRsp.ImModifyGroupInfoRsp.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImModifyGroupInfoRsp(jsonBodyObj);
			}
			// 通知添加组(用于同步信息处理)
			else if (EmRsp.ImGroupAddNtf.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImGroupAddNtf(jsonBodyObj);
			}
			// 创建组信息应答
			else if (EmRsp.ImAddGroupInfoRsp.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImAddGroupInfoRsp(jsonBodyObj);
			}
			// 通知删除组
			else if (EmRsp.ImGroupDelNtf.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImGroupDelNtf(jsonBodyObj);
			}
			// 删除组
			else if (EmRsp.ImDelGroupInfoRsp.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImDelGroupInfoRsp(jsonBodyObj);
			}
			// 批量添加联系人到组
			else if (EmRsp.ImBatchAddContactsRsp.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImBatchAddContactsRsp(jsonBodyObj);
			}
			// 添加Member
			else if (EmRsp.ImAddMemberInfoRsp.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImAddMemberInfoRsp(jsonBodyObj);
			}
			// 添加Member(失败)
			else if (EmRsp.ImQueryAddMemberInfoRsp.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImQueryAddMemberInfoRsp(jsonBodyObj);
			}
			// 通知添加Member
			else if (EmRsp.ImMemberAddNtf.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImMemberAddNtf(jsonBodyObj);
			}
			// 通知删除Member
			else if (EmRsp.ImMemberDelNtf.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImMemberDelNtf(jsonBodyObj);
			}
			// 删除Member
			else if (EmRsp.ImDelMemberInfoRsp.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImDelMemberInfoRsp(jsonBodyObj);
			}
			// 批量移动联系人到一个组
			else if (EmRsp.ImBatchMoveContactsRsp.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImBatchMoveContactsRsp(jsonBodyObj);
			}
			// 批量删除联系人
			else if (EmRsp.ImBatchDelContactsRsp.toString().equalsIgnoreCase(eventname)) {
				ImMtcCallback.parseImBatchDelContactsRsp(jsonBodyObj);
			}

			// Im
			// ---------------------------------------------------------------------------
			// ChatRoom
			// Im 最近登录保存的所有固定讨论
			else if (EmRsp.ImPersistentChatroomsNtf.toString().equalsIgnoreCase(eventname)) {
				final JSONObject jsonBodyObjs = jsonBodyObj;
				if (!stopHanldeJni) {
					ImChatRoomMtcCallback.parseImPersistentChatroomsNtf(jsonBodyObjs);
				}
				// ExecutorService pool = Executors.newSingleThreadExecutor();
				// pool.execute(new Runnable() {
				//
				// @Override
				// public void run() {
				// ImChatRoomMtcCallback.parseImPersistentChatroomsNtf(jsonBodyObj);
				// }
				// });
				// pool.shutdown();
			}
			// Im 最近登录保存的所有固定讨论结束通知
			else if (EmRsp.ImPersistentChatroomsFinNtf.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					ImChatRoomMtcCallback.parseImPersistentChatroomsFinNtf(jsonBodyObj);

					// 获取已屏蔽的讨论组列表
					ImLibCtrl.imGetScreenedChatromsReq(); // For SDM-00042970(屏蔽讨论组未同步), added by gaofan_kd7331
					// 2015-11-06 11:24:10

					// 通知XMPP已准备好接受组织架构和离线讨论组创建、成员、成员状态等消息推送
					ImCtrl.IMSetReadyReq(TruetouchGlobal.imHandle);
				}
			}

			// For 讨论组成员变动类消息合并， added by gaofan_kd7331 20160308
			else if (EmRsp.ImChatroomMemberBatchNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatRoomMtcCallback.parseImChatroomMemberBatchNtf(jsonBodyObj);
			}
			// 请求进入之前保存的固定讨论组返回信息
			else if (EmRsp.ImQueryEnterSavedPersistentChatroomsRsp.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImQueryEnterSavedPersistentChatroomsRsp(jsonBodyObj);
			}
			// 通知收到固定讨论组信息
			else if (EmRsp.ImEnterPersistentRoomNtf.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImEnterPersistentRoomNtf(jsonBodyObj);
			}
			// 收到固定讨论组消息（无效的讨论组）
			else if (EmRsp.ImEnterPersistentRoomFailNtf.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImEnterPersistentRoomFailNtf(jsonBodyObj);
			}
			// 多人聊天添加成员应答
			else if (EmRsp.ImMulitChatAddMemberRsp.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImMulitChatAddMemberRsp(jsonBodyObj);
			}
			// 创建多人聊天应答
			else if (EmRsp.ImMulitChatCreateRsp.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImMulitChatCreateRsp(jsonBodyObj, false);
			}
			// 通知创建讨论组消息
			else if (EmRsp.ImMulitChatCreateNtf.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImMulitChatCreateNtf(jsonBodyObj);
			}
			// 收到解散讨论消息
			else if (EmRsp.ImMulitChatDestroyRsp.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImMulitChatDestroyRsp(jsonBodyObj);
			}
			// 销毁多人聊天通知
			else if (EmRsp.ImMulitChatDestroyNtf.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImMulitChatDestroyNtf(jsonBodyObj);
			}
			// 退出多人聊天应答
			else if (EmRsp.ImMulitChatQuitRsp.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImMulitChatQuitRsp(jsonBodyObj);
			}
			// 修改Chatroon Name消息
			else if (EmRsp.ImSetChatroomRsp.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImSetChatroomRsp(jsonBodyObj);
			}
			// 通知修改Chatroon消息
			else if (EmRsp.ImNotifyRoomConfigNtf.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImNotifyRoomConfigNtf(jsonBodyObj);
			}
			// 收到多人聊天删除成员应答
			else if (EmRsp.ImMulitChatDelMemberRsp.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImMulitChatDelMemberRsp(jsonBodyObj);
			}
			// 对端用户已经有32个固定讨论组或者32个临时讨论组，在创建讨论组时，邀请该人 会收到此消息
			else if (EmRsp.ImDeclineNtf.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImDeclineNtf(jsonBodyObj);
			}
			// 收到屏蔽讨论组应答信息
			else if (EmRsp.ImScreenChatromRsp.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImScreenChatromRsp(jsonBodyObj);
			}
			// For SDM-00042970(屏蔽讨论组未同步), added by gaofan_kd7331 2015-11-06 11:24:10
			else if (EmRsp.ImGetScreenedChatromsRsp.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImGetScreenedChatromsRsp(jsonBodyObj);
			}

			// 屏蔽讨论组(无流量)应答
			else if (EmRsp.ImScreenChatromRRsp.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImScreenChatromRRsp(jsonBodyObj);
			}
			// 获取讨论组所有不在线成员信息
			else if (EmRsp.ImMulitChatGetRostersRsp.toString().equals(eventname)) {
				ImChatRoomMtcCallback.parseImMulitChatGetRostersRsp(jsonBodyObj);
			}
			// 查询账号详细信息
			else if (EmRsp.ImQueryAccountInfoRsp.toString().equals(eventname)) {
				ImMtcCallback.parseImQueryAccountInfoRsp(jsonBodyObj);
			}
			// 修改成员通知
			else if (EmRsp.ImMemberModifyNtf.toString().equals(eventname)) {
				ImMtcCallback.parseImMemberModifyNtf(jsonBodyObj);
			}

			// 平台
			// ---------------------------------------------------------------------------
			// 批量查询账号详细信息
			else if (EmRsp.RestQueryUserInfo_Rsp.toString().equals(eventname)) {
				PlatformMtcCallback.parseRestQueryUserInfoRsp(jsonBodyObj);
			} else if (EmRsp.RestQueryUserInfo_Fin_Rsp.toString().equals(eventname)) {
				PlatformMtcCallback.parseRestQueryUserInfoFinishRsp(jsonBodyObj);
			}
			// 查询账号详细信息应答
			else if (EmRsp.RestGetAccountInfo_Rsp.toString().equals(eventname)) {
				final JSONObject jsonBodyObjs = jsonBodyObj;
				ExecutorService pool = Executors.newSingleThreadExecutor();
				pool.execute(new Runnable() {

					@Override
					public void run() {
						PlatformMtcCallback.parseRestGetAccountInfoRsp(jsonBodyObjs);
					}
				});
				pool.shutdown();
			}
			// 修改账号详细信息请求应答
			else if (EmRsp.RestUpdateAccountInfo_Rsp.toString().equals(eventname)) {
				PlatformMtcCallback.parseRestUpdateAccountInfoRsp(jsonBodyObj);
			}
			// 修改头像
			else if (EmRsp.RestMotifyUserPortrait_Rsp.toString().equals(eventname)) {
				PlatformMtcCallback.parseRestMotifyUserPortraitRsp(jsonBodyObj);
			}

			// 按姓名模糊查找本企业联系人请求
			else if (EmRsp.RestUserListByStr_Rsp.toString().equals(eventname)) {
				PlatformMtcCallback.parseRestUserListByStrRsp(jsonBodyObj);
			}
			// 按条件查找企业联系人请求
			else if (EmRsp.RestConditionQuery_Rsp.toString().equals(eventname)) {
				PlatformMtcCallback.parseRestConditionQueryRsp(jsonBodyObj);
			}
			// 获取部门下人员信息(不包含子部门)应答（分包）
//			else if (EmRsp.RestGetDepartmentUser_Rsp.toString().equals(eventname)) {
//				// PlatformMtcCallback.parseRestGetDepartmentUser_Rsp(jsonBodyObj);
//			}
			// 获得公司组织架构和所有人员信息应答（分多包）
//			else if (EmRsp.RestGetDepartmentAll_Rsp.toString().equals(eventname)) {
//				PlatformMtcCallback.parseRestGetDepartmentAllRsp(jsonBodyObj);
//			}
			// 忘记密码后的应答
			else if (EmRsp.RestPassWordByMailRsp.toString().equalsIgnoreCase(eventname)) {
				PlatformMtcCallback.parseRestPassWordByMailRsp(jsonBodyObj);
			}
			// 修改登录（平台的）密码响应
			else if (EmRsp.RestUpdatePassword_Rsp.toString().equalsIgnoreCase(eventname)) {
				PlatformMtcCallback.parseRestUpdatePasswordRsp(jsonBodyObj);
			}

			// 会议
			// ---------------------------------------------------------------------------
			// P2P结束
			else if (EmRsp.P2PEndedNtf.toString().equalsIgnoreCase(eventname)) {
				int reason = 0;
				if (jsonBodyObj.has(KEY_basetype)) {
					reason = jsonBodyObj.getInt(KEY_basetype);
				}
				if(reason == EmMtCallDisReason.emDisConnect_SecEncTypeError.value){
					PcToastUtil.Instance().showCustomShortToast(R.string.vconf_emReason_47);
				}
				VConferenceManager.quitConfAction(true, false);
			}
			// 结束会议(多点)通知
			else if (EmRsp.MulConfEndedNtf.toString().equalsIgnoreCase(eventname)) {
				int reason = 0;
				if (jsonBodyObj.has(KEY_basetype)) {
					reason = jsonBodyObj.getInt(KEY_basetype);
				}
				if (reason == EmMtCallDisReason.emDisconnect_Exceedmaxinconfmtnum.value) {
					PcToastUtil.Instance().showCustomShortToast(R.string.vconf_emReason_23);
				}
				VConferenceManager.quitConfAction(true, false);
			}
			// 取消呼叫 BODY(EmMtCallDisReason_Api, N/A)
			else if (EmRsp.ConfCanceledNtf.toString().equalsIgnoreCase(eventname)) {

				// 判断是否需要 电话入会 ==电话入会，先挂断h323
				if (VConferenceManager.isJoinConfByPhone) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							String confName = "";
							ConfDetailInfo confDetail = new ConfDetailInfoDao().queryByE164(VConferenceManager.mCallPeerE164Num);
							if (confDetail != null) {
								confName = confDetail.getConfName();
							}
							final PcActivity pcCurrActivity = (PcActivity) PcAppStackManager.Instance().currentActivity();
							VConferenceManager.joinConfByPhone(pcCurrActivity, confName, VConferenceManager.mCallPeerE164Num);
							VConferenceManager.quitConfAction(VConferenceManager.currTMtCallLinkSate.isVConf(), true);
						}
					}).start();
					return;
				}
				int reson = -1;
				if (jsonBodyObj.has(KEY_basetype)) {
					reson = jsonBodyObj.getInt(KEY_basetype);
					// 检测是否网络异常
					if (!NetWorkUtils.isAvailable(TruetouchApplication.getContext())) {
						PcToastUtil.Instance().showCustomShortToast(R.string.network_fail_vconf);
					} else {
						VConferenceManager.failedReson(reson);
					}
				}

				// 修改短会原因
				if (reson != -1 && null != VConferenceManager.currTMtCallLinkSate) {
					EmMtCallDisReason emDisReason = EmMtCallDisReason.toEmMtCallDisReason(reson, VConferenceManager.currTMtCallLinkSate.emCallDisReason);
					VConferenceManager.currTMtCallLinkSate.emCallDisReason = emDisReason;
				}

				if (null != VConferenceManager.currTMtCallLinkSate) {
					VConferenceManager.quitConfAction(VConferenceManager.currTMtCallLinkSate.isVConf(), false);
				} else {
					VConferenceManager.quitConfAction(false, false);
				}
			}
			// 会议15分钟提醒(主席权限)
			else if (EmRsp.ConfWillEndNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseConfWillEndNtf(jsonBodyObj);
			}
			// 会议延长通知, 单位为分钟
			else if (EmRsp.ConfDelayNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseConfDelayNtf(jsonBodyObj);
			}
			// 延长会议时间结果(主席延长会议)
			else if (EmRsp.ProlongResultNtf.toString().equalsIgnoreCase(eventname)) {
				if (VConferenceManager.isChairMan()) {
					if (jsonBodyObj.has(KEY_basetype) && jsonBodyObj.getBoolean(KEY_basetype)) {
						PcToastUtil.Instance().showCustomShortToast(R.string.vconf_delay_success);
					} else {
						PcToastUtil.Instance().showCustomShortToast(R.string.vconf_delay_fail);
					}
				}
			}
			// 被叫
			else if (EmRsp.ConfInComingNtf.toString().equals(eventname)) {
				VConferenceManager.isCallIncomVconf = true;
				VconfMtcCallback.parseCallLinkSate(body);
			}
			// 状态通知 呼叫连接中
			else if (EmRsp.ConfCallingNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseCallLinkSate(body);
			}
			// callMissed
			else if (EmRsp.CallMissedNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseCallMissed(body);
			}
			// 关闭第一路通道
			else if (EmRsp.PrimoVideoOff_Ntf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parsePrimoVideoOff(body);
			}
			// 开始点对点会议
			else if (EmRsp.P2PStartedNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseCallLinkSate(body);
				ImLibCtrl.imModifySelfStateReq();
			}
			// 开始多点会议
			else if (EmRsp.MulConfStartedNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseCallLinkSate(body);
				ImLibCtrl.imModifySelfStateReq();

				if (PcLog.isPrint) {
					Log.d("Test", "入会成功，查询会议详情:" + VConferenceManager.mCallPeerE164Num);
				}

				if (TruetouchApplication.getApplication().currLoginModle() != EmModle.customH323) {
					ConfLibCtrl.confGetConfDetailCmd(VConferenceManager.mCallPeerE164Num);
					ConfLibCtrl.confSetSubMtListCmd(true);
				}
			}
			// 获取会议信息
			else if (EmRsp.ConfInfoNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseConfInfo(body);
			}
			// 多点会议，设置本地终端信息
			else if (EmRsp.TerLabelNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.setTerLable(body);
			}
			// 当前会议所有在线终端通知
			else if (EmRsp.OnLineTerListNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseOnLineTerList(jsonBodyObj);
			}
			// 主席位置
			else if (EmRsp.ChairPosNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.setChairPos(body);
			}
			// 向本终端申请主席（本端为主席）
			else if (EmRsp.ApplyChairNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.applyChair(body);
			}
			// 向本终端申请发言（本端为主席）
			else if (EmRsp.ApplySpeakNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.applySpeakPos(body);
			}

			// 当前看的视频源通知
			else if (EmRsp.YouAreSeeingNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseYouAreSing(body);
			}
			else if (EmRsp.BroadcastResultNtf.toString().equalsIgnoreCase(eventname)) {
				//VconfMtcCallback.parseYouAreSing(body);
			}
			else if (EmRsp.CancelSeenByAllNtf.toString().equalsIgnoreCase(eventname)) {
				//VconfMtcCallback.parseYouAreSing(body);
			}
			//混音参数
			else if (EmRsp.MixParamNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseMixParamNtf(body);
			}
			//选看参数上报
			else if (EmRsp.ViewMtParam_Ntf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseViewMtParamNtf(body);
			}
			// 选看结果
			else if (EmRsp.SendThisTerResultNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseSendThisTerResultNtf(jsonBodyObj);
			}
			//开启自主画面合成 结果
			else if (EmRsp.StartCustomVMPResultNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseStartCustomVMPResultNtf(jsonBodyObj);
			}
			//设置自主画面合成（已经开启） 结果
			else if (EmRsp.SetCustomVMPResultNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseSetCustomVMPResultNtf(jsonBodyObj);
			}
			//停止自主画面合成 结果
			else if (EmRsp.StopCustomVMPResultNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseStopCustomVMPResultNtf(jsonBodyObj);
			}
			//开启画面合成 结果
			else if (EmRsp.StartConfVMPResultNtf.toString().equalsIgnoreCase(eventname)){
				VconfMtcCallback.parseStartConfVMPResultNtf(jsonBodyObj);
			}
			// 取消画面合成 结果
			else if (EmRsp.StopConfVMPResultNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseStopConfVMPResultNtf(jsonBodyObj);
			}
			//  获得终端状态通知
			else if (EmRsp.GetTerStatusNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseGetTerStatusNtf(body);
			}
			// 会议中 有人加入会议通知
			else if (EmRsp.TerJoin_Ntf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.pareseTerJoinVconf(body);
			}
			// 会议中 有人退出会议通知
			else if (EmRsp.TerLeft_Ntf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.pareseTerLeftVconf(body);
			}
			// 申请主席 返回通知 获得主席令牌
			else if (EmRsp.ChairTokenGetNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) { // true表示申请成功，false表示申请失败
					// VconfMtcCallback.parseChairToken(jsonBodyObj.getBoolean(basetype));//
					// 设置本终端为主席终端
				}
			}
			// 申请主讲 返回通知
			else if (EmRsp.SeenByAllNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) {
					// VconfMtcCallback.parseSeenByAll();// 设置本终端为主讲终端
				}
			}
			// 轮询状态上报
			else if (EmRsp.PollState_Ntf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseTMtPollInfo(body);
			}
			// 通知当前会议类型
			else if (EmRsp.NotifyConfTypeNtf.toString().equalsIgnoreCase(eventname)) {

			}
			// 画面合成参数
			else if (EmRsp.GetConfVMPResultNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseTMtVmpParam(body);
			}
			// simple 会议消息
			else if (EmRsp.SimpleConfInfo_Ntf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseSimpleConfInfo(body);
			}
			// 当前会议是否加密通知
			else if (EmRsp.CallEncryptNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) {
					VConferenceManager.isCallEncryptNtf = jsonBodyObj.getBoolean(KEY_basetype);
				}
			}
			// 辅流发送状态通知
			else if (EmRsp.AssSndSreamStatusNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseAssSndSreamStatusNtf(jsonBodyObj);
			}
			// 辅流接收状态通知
			else if (EmRsp.AssRcvSreamStatusNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseAssRcvSreamStatusNtf(jsonBodyObj);
			}
			// 多点会议中 双流发送者
			else if (EmRsp.AssStreamSender_Ntf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseAssSreamSender(jsonBodyObj);
			}
			// 注册GK RegResultNtf --- EmRegFailedReason
			else if (EmRsp.RegResultNtf.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseGKRegResultNtf(jsonBodyObj);
			}
			// 注册授权认证
			else if (EmRsp.RegAuthResultNtf.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseRegAuthResult(jsonBodyObj);
			}
			// CSU服务器配置
			else if (EmRsp.SetCSUCfgNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseCSUCfg(body);
			}
			// CSU服务器配置
			else if (EmRsp.SetSipSrvCfgNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseSipCfg(body);
			}
			// 静音
			else if (EmRsp.CodecQuietNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) {
					VconfMtcCallback.parseCodecQuiet(jsonBodyObj.getBoolean(KEY_basetype));
				}
			}
			// 哑音
			else if (EmRsp.CodecMuteNtf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_basetype)) {
					VconfMtcCallback.parseCodecMute(jsonBodyObj.getBoolean(KEY_basetype));
				}
			}
			// 会议列表信息
			else if (EmRsp.ConfListRsp.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseConfList(jsonBodyObj);
			}
			// 会议详情解析
			else if (EmRsp.GetConfDetailInfoNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseConfDetailInfo(jsonBodyObj);
			}
			//会议详情解析，对应新接口，这里只拿一个会议的加密字段
			else if(EmRsp.RestGetInstantConfInfoByID_Rsp.toString().equalsIgnoreCase(eventname)){
				VconfMtcCallback.praseInstantConfInfoByID(jsonBodyObj);
			}
			// 会议列表信息（新接口获取）
			else if (EmRsp.RestGetConferenceList_Rsp.toString().equals(eventname)) {
				VconfMtcCallback.parseConferenceList(jsonBodyObj);
			}
			else if (EmRsp.RestGetConferenceList_Fin_Rsp.toString().equals(eventname)) {
				VconfMtcCallback.parseConferenceListFin(jsonBodyObj);
			}
			// 预约会议列表信息
			else if (EmRsp.RestGetBookConferenceList_Rsp.toString().equals(eventname)) {
				VconfMtcCallback.parseBookConferenceList(jsonBodyObj);
			}
			else if (EmRsp.RestGetBookConferenceList_Fin_Rsp.toString().equals(eventname)) {

			}
			// H323在线终端列表
			else if (EmRsp.OnLineTerListRsp.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseOnLineTerListRsp(jsonBodyObj);
			}
			// 创建会议是否成功(预加入会议结果通知)
			else if (EmRsp.PreCreateConfResultNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.paresJoinCreateConfResult(jsonBodyObj);
			}
			// 加入会议是否成功
			else if (EmRsp.PreJoinConfResultNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.paresJoinCreateConfResult(jsonBodyObj);
			}
			//创建会议是否成功(对应的是新接口)
			else if(EmRsp.RestCreateConference_Rsp.toString().equalsIgnoreCase(eventname)){
				//创会是否成功
				VconfMtcCallback.paresCreateConferenceRsp(jsonBodyObj);
			}
			// 会议加密 弹框
			else if (EmRsp.McReqTerPwdNtf.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.paresReqTerPwdNtf(jsonBodyObj);
			}
			// 音视频编解码信息
			else if (EmRsp.DiagnoseCodecGetStatistic_Rsp.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.parseCodecStatic(body);
			}
			// 网管登陆
			else if (EmRsp.AgentLogInResultNtf.toString().equalsIgnoreCase(eventname)) {
				NetworkManageNMS.parseAgentLogInResultNtf(jsonBodyObj);
			}
			// 检测升级是否成功
			else if (EmRsp.CheckUpgradeResultNtf.toString().equalsIgnoreCase(eventname)) {
				UpgradeMtcCallback.parseCheckUpgradeResultNtf(jsonBodyObj);
			}
			// 如果成功则会有后面这条响应，否则没有
			else if (EmRsp.UpgradeVersionInfoNtf.toString().equalsIgnoreCase(eventname)) {
				UpgradeMtcCallback.parseUpgradeVersionInfoNtf(jsonBodyObj);
			}
			// 下载文件的信息
			else if (EmRsp.UpgradeFileDownloadInfoNtf.toString().equalsIgnoreCase(eventname)) {
				UpgradeMtcCallback.parseUpgradeFileDownloadInfoNtf(jsonBodyObj);
			}
			// 下载完成
			else if (EmRsp.UpgradeFileDownloadOkNtf.toString().equalsIgnoreCase(eventname)) {
				UpgradeMtcCallback.parseUpgradeFileDownloadOkNtf(jsonBodyObj);
			}

			// ---------------------------------- 虚拟会议室 ----------------------------
			else if (EmRsp.RestGetVConfList_Rsp.toString().equalsIgnoreCase(eventname)){
				VconfMtcCallback.paresVirtualVconfList(jsonBodyObj);
			}
			else if (EmRsp.RestGetVConfList_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				VconfMtcCallback.paresVirtualVconfListFinishRsp(jsonBodyObj);
			}

			// 直播
			// ---------------------------------------------------------------------------
			//设置vrs配置
			else if(EmRsp.SetVRSCfgNtf.toString().equalsIgnoreCase(eventname)){
				LiveMtcCallback.vodVrsLogin(body);
			}
			// 登陆VRS后 直播登录结果 0 成功 1 失败 EmVodErrReason
			else if(EmRsp.VodLogin_Ntf.toString().equalsIgnoreCase(eventname)){
				LiveMtcCallback.vodVrsLoginResult(jsonBodyObj);
			}
			// 注销VRS后EmVodErrReason
			else if(EmRsp.VodLogout_Ntf.toString().equalsIgnoreCase(eventname)){
//				LiveMtcCallback.vodVrsLoginResult(jsonBodyObj);
			}
			// 返回账号权限  EmVodRightMask
			else if(EmRsp.VrsRightMask_Ntf.toString().equalsIgnoreCase(eventname)) {
				//此回调是Vrs登录后回调，有时收不到 权限问题在RestGetAccountInfo_Rsp消息中处理
				LiveMtcCallback.parseRightMask(jsonBodyObj); // 暂时使用此回调取代bEnableUnicat（平台V5.2才支持）来判断点播权限。
			}
			//登录VRS后 取回cookie值
			else if(EmRsp.VrsUsrInfo_Ntf.toString().equalsIgnoreCase(eventname)){
				LiveMtcCallback.parseVrsUsrInfo(jsonBodyObj);
			}
			//  获取直播列表
			else if(EmRsp.VodGetRoomListInfo_Ntf.toString().equalsIgnoreCase(eventname)){
				LiveMtcCallback.parseVrsRoomList(jsonBodyObj);
			}
			// 直播登录结果 0 成功 1 失败
			else if(EmRsp.VodGetRoomListInfo_Fin_Ntf.toString().equalsIgnoreCase(eventname)){
				LiveMtcCallback.parseVrsRoomListFinish(jsonBodyObj);
			}

			// 点播
			// ---------------------------------------------------------------------------
			//获取点播列表文件夹
			else if (EmRsp.VodGetFolderInfo_Ntf.toString().equalsIgnoreCase(eventname)){
				VodMtCallback.parseVodGetFolderInfo(jsonBodyObj);
			}
			//获取点播列表文件夹结束
			else if(EmRsp.VodGetFolderInfo_Fin_Ntf.toString().equalsIgnoreCase(eventname)){
				VodMtCallback.parseVodGetFolderInfoFin(jsonBodyObj);
			}
			else if (EmRsp.VodGetPrgsDetailInfo_Ntf.toString().equalsIgnoreCase(eventname)){

			}
			//获取点播基本信息列表
			else if (EmRsp.VodGetPrgsInfo_Ntf.toString().equalsIgnoreCase(eventname)){
				VodMtCallback.parseVodGetPrgsInfo(jsonBodyObj);
			}

			// 录像
			// ---------------------------------------------------------------------------
			else if (EmRsp.RestStartRecord_Rsp.toString().equalsIgnoreCase(eventname)) {
				RecordMtcCallback.parseRestStartRecord(jsonBodyObj);
			} else if (EmRsp.RestStopRecord_Rsp.toString().equalsIgnoreCase(eventname)) {
				RecordMtcCallback.parseRestStopRecord(jsonBodyObj);
			} else if (EmRsp.RestGetRecordState_Rsp.toString().equalsIgnoreCase(eventname)) {
				RecordMtcCallback.parseRestGetRecordState(jsonBodyObj);
			} else if (EmRsp.RestModifyRecordState_Rsp.toString().equalsIgnoreCase(eventname)) {
				RecordMtcCallback.parseRestModifyRecordState(jsonBodyObj);
			} else if (EmRsp.RestGetRecordList_Rsp.toString().equalsIgnoreCase(eventname)) {
				RecordMtcCallback.parseRestGetRecordList(jsonBodyObj);
			} else if (EmRsp.RestGetRecordList_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				RecordMtcCallback.parseRestGetRecordListFin(jsonBodyObj);
			}

			// ----------------------------------数据协作（DC）----------------------------
			else if (EmRsp.DcsLoginResult_Ntf.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsLoginResult(jsonBodyObj);
			} else if (EmRsp.DcsConfResult_Ntf.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsConfResult(jsonBodyObj);
			} else if (EmRsp.DcsCreateConf_Rsp.toString().equalsIgnoreCase(eventname)) {	// 注意：其它终端创建白板也会收到此响应。它相当Ntf。
				DCMtcCallback.parseDcsCreateConf(body);
			} else if (EmRsp.DcsCurrentWhiteBoard_Ntf.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsCurrentWhiteBoard(body);
			} else if (EmRsp.DcsNewWhiteBoard_Ntf.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsNewWhiteBoard(body);
			} else if (EmRsp.DcsSwitch_Ntf.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsSwitch(body);
			} else if (EmRsp.DcsElementOperBegin_Ntf.toString().equalsIgnoreCase(eventname)) {
//				DCMtcCallback.parseDcsElementOperBegin(jsonBodyObj);
			} else if (EmRsp.DcsOperLineOperInfo_Ntf.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DcsOperCircleOperInfo_Ntf.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DcsOperRectangleOperInfo_Ntf.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DcsOperPencilOperInfo_Ntf.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DcsOperColorPenOperInfo_Ntf.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DcsOperInsertPic_Ntf.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DcsOperPitchPicDrag_Ntf.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DcsOperPitchPicDel_Ntf.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DcsOperEraseOperInfo_Ntf.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DcsOperFullScreen_Ntf.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DcsOperUndo_Ntf.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DcsOperRedo_Ntf.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsOperPrimitiveOperInfo(jsonBodyObj);
			} else if (EmRsp.DcsOperClearScreen_Ntf.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsOperClearScreen(body);
			} else if (EmRsp.DcsElementOperFinal_Ntf.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsElementOperFinal(body);
			} else if (EmRsp.DcsDownloadImage_Rsp.toString().equalsIgnoreCase(eventname) ||
					EmRsp.DownloadImage_Ntf.toString().equalsIgnoreCase(eventname)) {
				if (jsonBodyObj.has(KEY_MainParam)) {
					if (jsonBodyObj.getJSONObject(KEY_MainParam).getBoolean("bSucces")) {
						DCMtcCallback.parseDcsDownloadImage(jsonBodyObj.getString(KEY_AssParam));
					}
				} else {
					DCMtcCallback.parseDcsDownloadImage(body);
				}
			} else if (EmRsp.DcsDownloadFile_Rsp.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsDownloadFile(body);
			} else if (EmRsp.DcsDelWhiteBoard_Ntf.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsDelWhiteBoard(jsonBodyObj);
			} else if (EmRsp.DcsQuitConf_Rsp.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsQuitConf(body);
			} else if (EmRsp.DcsReleaseConf_Ntf.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsReleaseConf(jsonBodyObj);
			} else if (EmRsp.DcsUserApplyOper_Ntf.toString().equalsIgnoreCase(eventname)) {
				DCMtcCallback.parseDcsUserApplyOper(body);
			}

			// IMChat
			// ---------------------------------------------------------------------------
			// 联系人键盘输入状态通知
			else if (EmRsp.ImCharStateNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImCharStateNtf(jsonBodyObj);
			}
			// 离线消息超过100 条提示用户
			else if (EmRsp.ImNotifyOfflineMsgOverflowNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImNotifyOfflineMsgOverflowNtf(jsonBodyObj);
			}
			// 告知对方自己的键盘输入状态应答(根据位置)应答
			else if (EmRsp.ImSendCharStateRsp.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImSendCharStateRsp(jsonBodyObj);
			}
			// 接收文件响应 一个完整的文件共享会经历以下5个步骤
			// 1.接收文件通知 ImSendP2PMessageNtf
			// 2.文件共享通知 ImFileShareOfferNtf
			// 3.接收文件共享通知 ImAcceptFileRsp
			// 4.开始文件共享通知 ImFileShareStartNtf
			// 5.文件共享进度通知(多个) ImFileShareProgressNtf
			// 6.文件共享结束通知 ImFileShareCompleteNtf
			// p2p 发送点对点消息应答通知 (接收消息)
			else if (EmRsp.ImSendP2PMessageNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImSendP2PMessageNtf(jsonBodyObj);
			}
			// P2P 发送点对点消息应答通知
			// else if (EmRsp.ImSendP2PMessageInstantRsp.toString().equals(eventname)) {
			// ImChatMtcCallback.parseImSendP2PMessageInstantRsp(jsonBodyObj);
			// }

			// P2P 消息 成功/失败
			else if (EmRsp.ImSendP2PMessageRsp.toString().equals(eventname)) {
				ImChatMtcCallback.parseImSendP2PMessageRsp(jsonBodyObj);
			}
			// 发送文件响应 一个完整的文件共享会经历以下5个步骤
			// 1.发送文件响应 ImSendFileRsp
			// 2.文件共享通知 ImFileShareOfferNtf
			// 3.开始文件共享通知 ImFileShareStartNtf
			// 4.文件共享进度通知(多个) ImFileShareProgressNtf
			// 5.文件共享结束通知 ImFileShareCompleteNtf
			else if (EmRsp.ImSendFileRsp.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImSendFileRsp(jsonBodyObj);
			}
			// 文件共享通知
			else if (EmRsp.ImFileShareOfferNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImFileShareOfferNtf(jsonBodyObj);
			}
			// 开始文件共享通知
			else if (EmRsp.ImFileShareStartNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImFileShareStartNtf(jsonBodyObj);
			}
			// 文件共享进度通知(多个)
			else if (EmRsp.ImFileShareProgressNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImFileShareProgressNtf(jsonBodyObj);
			}
			// 文件共享结束通知
			else if (EmRsp.ImFileShareCompleteNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImFileShareCompleteNtf(jsonBodyObj);
			}
			// 文件共享失败通知
			else if (EmRsp.ImFileShareFailureNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImFileShareFailureNtf(jsonBodyObj);
			}
			// 文件共享取消
			else if (EmRsp.ImFileShareCancelNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImFileShareCancelNtf(jsonBodyObj);
			}
			// 接收图片
			else if (EmRsp.ImAcceptFileRsp.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImAcceptFileRsp(jsonBodyObj);
			}
			/**
			 * 讨论组接收图片 1:收到多人聊天消息通知 ImSendMulitSMSNtf 2:开始传输讨论组图片 ImChatroomPictureOpenNtf 3:讨论组图片传输进度
			 * ImChatroomPictureDataNtf 4:讨论组图片传输成功 ImChatroomPictureCloseNtf 5:讨论组图片传输失败 ImChatroomPictureFailNtf
			 */
			// 多人聊天
			else if (EmRsp.ImSendMulitSMSRsp.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImSendMulitSMSRsp(jsonBodyObj);
			}
			// 发送点消息通知（收到多人聊天消息）
			else if (EmRsp.ImSendMulitSMSNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImSendMulitSMSNtf(jsonBodyObj);
			}
			// 取消文件的发送
			else if (EmRsp.ImCancelFileRsp.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImCancelFileRsp(jsonBodyObj);
			}
			/**
			 * 讨论组发送图片 1:发送讨论组图片 ImSendGroupChatFileRsp 2:开始传输讨论组图片 ImChatroomPictureOpenNtf 3:讨论组图片传输进度
			 * ImChatroomPictureDataNtf 4:讨论组图片传输成功 ImChatroomPictureCloseNtf 5:讨论组图片传输失败 ImChatroomPictureFailNtf
			 */
			else if (EmRsp.ImSendGroupChatFileRsp.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImSendGroupChatFileRsp(jsonBodyObj);
			}
			// 开始传输讨论组图片
			else if (EmRsp.ImChatroomPictureOpenNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImChatroomPictureOpenNtf(jsonBodyObj);
			}
			// 讨论组图片传输进度
			else if (EmRsp.ImChatroomPictureDataNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImChatroomPictureDataNtf(jsonBodyObj);
			}
			// 讨论组图片传输成功
			else if (EmRsp.ImChatroomPictureCloseNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImChatroomPictureCloseNtf(jsonBodyObj);
			}
			// 讨论组图片传输失败
			else if (EmRsp.ImChatroomPictureFailNtf.toString().equalsIgnoreCase(eventname)) {
				ImChatMtcCallback.parseImChatroomPictureFailNtf(jsonBodyObj);
			}
			// 会议日程
			// ---------------------------------------------------------------------------
			// 平台Token响应
			else if (EmRsp.RestGetPlatformAccountTokenRsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) LoginMtcCallback.parseRestGetPlatformAccountTokenRsp(body);
				LoginMtcCallback.parsePlatformAccountToken4PwdSearch(body);
			}
			// 平台登录响应
			else if (EmRsp.RestPlatformAPILoginRsp.toString().equalsIgnoreCase(eventname)) {
				LoginMtcCallback.parseRestPlatformAPILoginRsp(jsonBodyObj);
			}
			// 用户Token失效,rest正在重新登录，告之界面进行提示
			else if (EmRsp.RestUserTokenExpired_Ntf.toString().equalsIgnoreCase(eventname)) {
				// @尚海龙，下层会重连
				// LoginMtcCallback.parseRestUserTokenExpiredNtf(jsonBodyObj);
			}
			// 用户Session失效，rest正在重新登录，告之界面进行提示
			else if (EmRsp.RestUserSessionExpired_Ntf.toString().equalsIgnoreCase(eventname)) {
				// @尚海龙，下层会重连
				// LoginMtcCallback.parseRestUserSessionExpiredNtf(jsonBodyObj);
			}
			// 获取 Meeting DeadLine
			else if (EmRsp.RestGetMeetingDeadLineRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseGetMeetingDeadLineRsp(jsonBodyObj);
			}
			// 会议通知TMTCometdMessageList_Api
			else if (EmRsp.GetCometdMessage_Ntf.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseGetCometdMessageNtf(jsonBodyObj);
			}
			// 获取所有通知消息请求
			else if (EmRsp.RestAppGetAllNotifyRsp.toString().equalsIgnoreCase(eventname)) {
				if (PcLog.isPrint) {
					Log.w("Test", "-------------callback 1-----------------\n" + result + "-------------callback 2-----------------");
				}
				MeetingScheduleMtcCallback.parseRestAppGetAllNotifyRsp(jsonBodyObj);
			}
			// 根据ID获取通知应答
			else if (EmRsp.RestGetNotifyByIdRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetNotifyByIdRsp(jsonBodyObj);
			}
			// 根据通知类型清除通知应答
			else if (EmRsp.RestAppClearNotifyRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestAppClearNotifyRsp(jsonBodyObj);
			}
			// 清除会议所有通知应答
			else if (EmRsp.RestAppClearAllMeetingNotifyRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestAppClearAllMeetingNotifyRsp(jsonBodyObj);
			}
			// 获取 Meeting ShortRoom
			else if (EmRsp.RestGetMeetingShortRoomNtf.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetMeetingShortRoom(jsonBodyObj);
			}
			// 获取 Meeting RoomId
			else if (EmRsp.RestGetMeetingRoomIdNtf.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetMeetingRoomId(jsonBodyObj);
			}
			// 获取Short Meeting
			else if (EmRsp.RestGetShortMeetingNtf.toString().equalsIgnoreCase(eventname)) {
				// MeetingScheduleMtcCallback.parseRestGetShortMeetingNtf(jsonBodyObj);
				MeetingScheduleMtcCallback.parseRestGetShortMeetingNtf(body);
			}
			// 获取会议列表结束通知
			else if (EmRsp.RestGetMeetingList_FinNtf.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetMeetingListFinNtf(jsonBodyObj);
			}
			// 会议反馈
			else if (EmRsp.RestMeetingFeedRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestMeetingFeedRsp(jsonBodyObj);
			}
			// 例会反馈
			else if (EmRsp.RestMeetingRegularFeedRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestMeetingRegularFeedRsp(jsonBodyObj);
			}
			// 结束会议 (new)
			else if (EmRsp.RestStopConference_Rsp.toString().equalsIgnoreCase(eventname)) {
				MainMeeting mainMeeting = SlidingMenuManager.getMainMeeting(true);
				if (null != mainMeeting) {
					mainMeeting.forceRefreshList();
				}
			}
			// 结束会议(old)
			else if (EmRsp.RestEndMeetingRsp.toString().equalsIgnoreCase(eventname)) {
				MainMeeting mainMeeting = SlidingMenuManager.getMainMeeting(true);
				if (null != mainMeeting) {
					mainMeeting.forceRefreshList();
				}
			}
			// 获取Meeting Info(new)
			else if (EmRsp.RestGetBookConferenceInfoByID_Rsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetMeetingInfoRsp(jsonBodyObj);
			}
			// 获取Meeting Info(old)
			else if (EmRsp.RestGetMeetingInfoRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetMeetingInfoRsp(jsonBodyObj);
			}
			// 获取 Regular Info
			else if (EmRsp.RestGetRegularRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetRegularRsp(jsonBodyObj);
			}
			// 获取会议参与人信息通知
			else if (EmRsp.RestGetParticipants_Rsp.toString().equalsIgnoreCase(eventname)) {
				// MeetingScheduleMtcCallback.parseRestGetParticipantsNtf(jsonBodyObj);
				MeetingScheduleMtcCallback.parseRestGetParticipantsNtf(body);
			}
			// 获取会议参与人信息结束通知
			else if (EmRsp.RestGetParticipants_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				// MeetingScheduleMtcCallback.parseRestGetParticipantsNtf(jsonBodyObj);
				//				MeetingScheduleMtcCallback.parseRestGetParticipantsNtf(body);
			}
			// 获取空闲会议室应答
			else if (EmRsp.RestQueryFreeRoomsRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestQueryFreeRoomsRsp(jsonBodyObj);
			}
			// 获取空闲会议室结束标志位
			else if (EmRsp.RestQueryFreeRoomsFinRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestQueryFreeRoomsFinNtf(jsonBodyObj);
			}
			// 获取用户所在企业所有可用的会议室区域应答
			else if (EmRsp.RestQueryMeetingRegionsRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestQueryMeetingRegionsRsp(jsonBodyObj);
			}
			// 获取区域结束标志位
			else if (EmRsp.RestQueryMeetingRegionsFinRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestQueryMeetingRegionsFinNtf(jsonBodyObj);
			}
			// 会议室批量加锁应答
			else if (EmRsp.RestLockMeetingRoomsRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.paseRestLockMeetingRoomsRsp(jsonBodyObj);
			}
			// 锁定会议室结束标志位
			else if (EmRsp.RestLockMeetingRoomsFinRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.paseRestLockMeetingRoomsFinNtf(jsonBodyObj);
			}
			// 会议解锁应答
			else if (EmRsp.RestMeetingUnlockRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.paseRestMeetingUnlockRsp(jsonBodyObj);
			}
			// 判断是否有锁定的会议室。
			else if (EmRsp.IfExistLockByFormkeyRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.paseIfExistLockByFormkeyRsp(jsonBodyObj);
			}
			// 正常召开会议
			else if (EmRsp.RestStartMeetingRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.paseRestStartMeetingRsp(jsonBodyObj);
			}
			// 修改会议
			else if (EmRsp.RestModifyMeetingRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.paseRestModifyMeetingRsp(jsonBodyObj);
			}
			// 删除会议
			else if (EmRsp.DeleteMeetingRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.paseDeleteMeetingRsp(jsonBodyObj);
			}
			// 创建会会议成功
			else if (EmRsp.RestAddMeetingRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.paseRestAddMeetingRsp(jsonBodyObj);
			}
			// 个人模板(new)
			else if (EmRsp.RestGetPersonalTemplatesList_Rsp.toString().equalsIgnoreCase(eventname)) {
				//MeetingScheduleMtcCallback.parseRestGetPersonalTemplateListRsp(jsonBodyObj);
				MeetingScheduleMtcCallback.parseRestGetNewPersonalTemplateListRsp(jsonBodyObj);
			}
			// 个人模板(old)
			else if (EmRsp.RestGetPersonalTemplateListRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetPersonalTemplateListRsp(jsonBodyObj);
			}
			// 个人模板finish(new)
			else if (EmRsp.RestGetPersonalTemplatesList_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetPersonalTemplateListFinRsp(jsonBodyObj);
			}
			// 个人模板finish(old)
			else if (EmRsp.RestGetPersonalTemplateListFinRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetPersonalTemplateListFinRsp(jsonBodyObj);
			}
			// 公共模板列表(new)
			else if (EmRsp.RestGetCommonTemplateList_Rsp.toString().equalsIgnoreCase(eventname)) {
				//MeetingScheduleMtcCallback.parseRestGetPublicTemplateListRsp(jsonBodyObj);
				MeetingScheduleMtcCallback.parseRestGetNewPublicTemplateListRsp(jsonBodyObj);
			}
			// 公共模板列表(old)
			else if (EmRsp.RestGetPublicTemplateListRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetPublicTemplateListRsp(jsonBodyObj);
			}
			// 公共模板finish(new)
			else if (EmRsp.RestGetCommonTemplateList_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetPublicTemplateListFinRsp(jsonBodyObj);
			}
			// 公共模板finish(old)
			else if (EmRsp.RestGetPublicTemplateListFinRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetPublicTemplateListFinRsp(jsonBodyObj);
			}
			// 个人模板 详情(new)
			else if (EmRsp.RestGetPerTemplateByID_Rsp.toString().equalsIgnoreCase(eventname)) {
				//MeetingScheduleMtcCallback.parseRestGetTemplateByIDRsp(jsonBodyObj, true);
				Log.e("PerTemplate",result);
				MeetingScheduleMtcCallback.parseRestGetNewTemplateByIDRsp(jsonBodyObj,true);
			}
			// 个人模板 详情(old)
			else if (EmRsp.RestGetPersonalTemplateByIDRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetTemplateByIDRsp(jsonBodyObj, true);
			}
			// 公共模板 详情(new)
			else if (EmRsp.RestGetCommonTemplateByID_Rsp.toString().equalsIgnoreCase(eventname)) {
				//MeetingScheduleMtcCallback.parseRestGetTemplateByIDRsp(jsonBodyObj, false);
				Log.e("CommonTemplate",result);
				MeetingScheduleMtcCallback.parseRestGetNewTemplateByIDRsp(jsonBodyObj,false);
			}
			// 公共模板 详情(old)
			else if (EmRsp.RestGetPublicTemplateByIDRsp.toString().equalsIgnoreCase(eventname)) {
				MeetingScheduleMtcCallback.parseRestGetTemplateByIDRsp(jsonBodyObj, false);
			}
			// 组织架构（采用新接口，与老接口区别：有集团、公共群组、用户域概念）
			// ---------------------------------------------------------------------------
			// 获取集团信息
			else if (EmRsp.RestGetGroup_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					StructureMtcCallback.parseRestGetGroupRsp(jsonBodyObj);
				}
			}
			// 获取集团公共群组
			else if (EmRsp.RestGetPublicGroup_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					StructureMtcCallback.parseRestGetPublicGroupRsp(jsonBodyObj);
				}
			}
			// 获取集团公共群组结束
			else if (EmRsp.RestGetPublicGroup_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					StructureMtcCallback.parseRestGetPublicGroupFinRsp(jsonBodyObj);
				}
			}
			// 获取集团公共群组版本号
			else if (EmRsp.RestGetPublicGroupVersion_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					final JSONObject tmpJsonBodyObj = jsonBodyObj;
					// 不堵塞回调线程
					cachedThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							StructureMtcCallback.parseRestGetPublicGroupVersionRsp(tmpJsonBodyObj);
						}
					});
				}
			}
			// 获取集团公共群组所有人员信息
			else if (EmRsp.RestGetPublicGroupUser_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					StructureMtcCallback.parseRestGetPublicGroupUserRsp(jsonBodyObj);
				}
			}
			// 获取集团公共群组所有人员信息结束
			else if (EmRsp.RestGetPublicGroupUser_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					final JSONObject tmpJsonBodyObj = jsonBodyObj;
					// 不堵塞回调线程
					cachedThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							StructureMtcCallback.parseRestGetPublicGroupUserFinRsp(tmpJsonBodyObj);
						}
					});
				}
			}
			// 获取用户域信息
			else if (EmRsp.RestGetUserDomain_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					StructureMtcCallback.parseRestGetUserDomainRsp(jsonBodyObj);
				}
			}
			// 获取用户域信息结束
			else if (EmRsp.RestGetUserDomain_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					// 不堵塞回调线程
					final JSONObject tmpJsonBodyObj = jsonBodyObj;
					cachedThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							StructureMtcCallback.parseRestGetUserDomainFinRsp(tmpJsonBodyObj);
						}
					});
				}
			}
			// 获得指定用户域组织架构人数
			else if (EmRsp.RestGetCompanyEmployeeCount_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					// 不堵塞回调线程
					final JSONObject tmpJsonBodyObj = jsonBodyObj;
					cachedThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							StructureMtcCallback.parseRestGetCompanyEmployeeCountRsp(tmpJsonBodyObj);
						}
					});
				}
			}
			// 获取指定用户域组织架构信息
			else if (EmRsp.RestGetUserDomainDepartments_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					StructureMtcCallback.parseRestGetUserDomainDepartmentsRsp(jsonBodyObj);
				}
			}
			// 获取指定用户域组织架构信息结束
			else if (EmRsp.RestGetUserDomainDepartments_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					// 不堵塞回调线程
					final JSONObject tmpJsonBodyObj = jsonBodyObj;
					cachedThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							StructureMtcCallback.parseRestGetUserDomainDepartmentsFinRsp(tmpJsonBodyObj);
						}
					});
				}
			}
			// 获取用户域所有人员信息
			else if (EmRsp.RestGetAllUserDomainUser_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					StructureMtcCallback.parseRestGetAllUserDomainUserRsp(jsonBodyObj);
				}
			}
			// 获取用户域所有人员信息结束
			else if (EmRsp.RestGetAllUserDomainUser_Fin_Rsp.toString().equalsIgnoreCase(eventname)) {
				if (!stopHanldeJni) {
					// 不堵塞回调线程
					final JSONObject tmpJsonBodyObj = jsonBodyObj;
					cachedThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							StructureMtcCallback.parseRestGetAllUserDomainUserFinRsp(tmpJsonBodyObj);
						}
					});
				}
			}
			//收到上报日志消息
			else if(EmRsp.AgentPackLog_Ntf.toString().equalsIgnoreCase(eventname)){
				if(!stopHanldeJni){
					//这里只是上报文件信息
					singleThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							ConfigLibCtrl.uploadLogInfo();
						}
					});
				}
			}
			//设置所上报日志的文件信息对应的响应
			else if(EmRsp.AgentSetPackLogInfo_Ntf.toString().equalsIgnoreCase(eventname)){

			}
			//日志打包完成通知网管对应的响应
			else if(EmRsp.AgentStartUploadLog_Rsp.toString().equalsIgnoreCase(eventname)){

			}
			else if (EmRsp.PushSrvLogin_Rsp.toString().equalsIgnoreCase(eventname)){
				LoginMtcCallback.parsePushSrvLoginRsp(jsonBodyObj);
			}

			/**
			 * ProductFlavorsCallback 对应的是不同版本的回调处理。如果是通用版本的消息处理
			 * else if 语句要放在SpecficiVersionCallback.prase 的上面处理
			 */
			else{
				ProductFlavorsCallback.prase(eventname,result,jsonBodyObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// String eventTypeLower = eventType.toLowerCase();
		//
		// // @formatter:off
		// if (stopHanldeJni
		// && !eventTypeLower.startsWith("disconnect")
		// && !eventTypeLower.startsWith("disconnected")
		// && !eventTypeLower.equals("getplatformaccounttoken")
		// && !eventTypeLower.equals("setplatformaccounttoken")
		// && !eventTypeLower.equals("forgetpswbyemailweibo")
		// && !"connectsus".equals(eventTypeLower)
		// && !"getrecommend".equals(eventTypeLower)
		// && !"receiveloadfileschedule".equals(eventTypeLower)
		// && !"isranportused".equals(eventTypeLower)
		// && !"ranportvalue".equals(eventTypeLower)
		// && !"cfgcommonbaseport".equals(eventTypeLower)
		// && !"snmpregresult".equals(eventTypeLower)) {
		//
		// return;
		// }
		// // @formatter:on
		//
		// callback(createClassname(eventType), result);
	}

	/**
	 * callback
	 * @param className
	 * @param xmlStr
	 */
	public void callback(String className, String xmlStr) {
		if (StringUtils.isNull(className) || StringUtils.isNull(xmlStr)) {
			return;
		}

		BaseCallbackHandler baseCallback = null;
		try {
			Class<?> c = Class.forName(className);
			if (c != null) {
				baseCallback = (BaseCallbackHandler) c.newInstance();
			}
		} catch (ClassNotFoundException e) {
			PcLog.e(getClass().getSimpleName(), "ClassNotFoundException ", e);
		} catch (IllegalAccessException e) {
			PcLog.e(getClass().getSimpleName(), "IllegalAccessException ", e);
		} catch (InstantiationException e) {
			PcLog.e(getClass().getSimpleName(), "InstantiationException ", e);
		} catch (Exception e) {
			PcLog.e(getClass().getSimpleName(), "Exception ", e);
		}

		if (baseCallback != null) {
			baseCallback.doAction(xmlStr);
		}
	}

	/**
	 * 创建一个完全路径的ClassName
	 * @param eventType
	 * @return
	 */
	protected String createClassname(String eventType) {
		if (StringUtils.isNull(eventType)) {
			return "";
		}

		StringBuffer classNameSB = new StringBuffer(JNIHEADER);

		// 微博相关接口
		if (eventType.endsWith("Weibo")) {
			classNameSB.append("weibo.");
		}
		// 会议日程相关
		else if (eventType.endsWith("Meeting")) {
			classNameSB.append("meeting.");
		}
		// 视频会议
		else if (eventType.endsWith("VConf")) {
			classNameSB.append("vconf.");
		} else {
			// classNameSB = new StringBuffer(JNIHEADER);
		}

		return classNameSB.append(eventType).append("Callback").toString();
	}

	/**
	 * message标签下的第一个子标签
	 * @param xmlStr
	 * @return
	 */
	@SuppressWarnings({
			"unchecked", "unused"
	})
	protected String getMessageChildFirstTag(String xmlStr) {
		if (StringUtils.isNull(xmlStr)) return "";
		String resultChildFirstTag = "";

		try {
			Document doc = DocumentHelper.parseText(xmlStr);
			if (doc == null) {
				return null;
			}
			Element rootElt = doc.getRootElement(); // 根节点<TrueTouchAndroid>
			if (rootElt == null) {
				return null;
			}

			String eventId = rootElt.elementTextTrim("EventID"); // EventID的Value
			String rootTag = rootElt.getName();

			if (rootTag == null || rootTag.length() == 0) {
				return resultChildFirstTag;
			}

			Element msgElt = (Element) rootElt.element("Message");// 根节点下的<Message>
			if (msgElt == null || msgElt.nodeCount() < 1) {
				return resultChildFirstTag;
			}

			List<Element> list = msgElt.elements();
			if (list == null || list.isEmpty()) {
				return resultChildFirstTag;
			}

			resultChildFirstTag = list.get(0).getName();

			return resultChildFirstTag;
		} catch (Exception e) {
			return resultChildFirstTag;
		}
	}

	/**
	 * message标签下的第一个子标签
	 * @param xml
	 * @return
	 */
	protected String getMessageChildFirstTag2(String xml) {
		String resultChildFirstTag = "";
		try {
			String subXML = xml.substring(xml.indexOf("<Message>") + "<Message>".length());
			if (StringUtils.isNull(subXML)) {
				return null;
			}

			subXML = subXML.substring(subXML.indexOf("<") + 1);
			if (StringUtils.isNull(subXML)) {
				return null;
			}

			resultChildFirstTag = subXML.substring(0, subXML.indexOf(">"));
		} catch (Exception e) {
			Log.e("ParseXML", "getMessageChildFirstTag2", e);
		}

		return resultChildFirstTag;
	}

}
