package com.kedacom.kdv.mt.mtapi;

/***********************************************************************************
 * 终端码流监控图像MtMonitorCtrl相关 JNI接口 (mtmonitorctrl_jni.h)
 ***********************************************************************************/

public class MonitorCtrl {

	/**
	 * 
	 */
	private MonitorCtrl() {

	}

	/**
	* VideoAssStreamCmd
	* @brief   video双流开始/停止命令(主要是mtc客户端使用)
	*
	* @param    [in]   bStart       TRUE:开始 FALSE:停止
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	*
	* @note 响应通知：Ev_MtApi_Vc_AssSndSream_Status_Ntf
	*/
	public static native int VideoAssStreamCmd(boolean bStart, int nSSID);

	// video双流开始/停止命令(主要是一体终端使用)
	public static native int VideoAssStreamCmd(boolean bStart);

	/**
	* StartStreamBroadcastCmd
	* @brief   开始码流广播命令(主要是mtc客户端使用)
	*
	* @param    [in]   nCodecType(EmCodecComponent_Api)  编解码器类型 
	* @param    [in]   nCodecIdx(EmCodecComponentIndex_Api)   某一路
	* @param    [in]   StringBuffer(TNetAddr_Api)     RTP地址信息
	* Json格式如下：
	* {
	*    "emIpType": 1,
	*    "dwIp": 12345,
	*    "achIpV6": "sdffsdf",
	*    "dwPort": 60000
	*}
	* @param    [in]   StringBuffer(TNetAddr_Api)    RTCP地址信息
	* Json格式如下：
	* {
	*    "emIpType": 1,
	*    "dwIp": 12345,
	*    "achIpV6": "sdffsdf",
	*    "dwPort": 60000
	*}
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	*
	* @note
	*/
	public static native int StartStreamBroadcastCmd(int nCodecType, int nCodecIdx, StringBuffer strRtpAddr, StringBuffer strRtcpAddr, int nSSID);

	// 开始码流广播命令(主要是一体终端使用)
	public static native int StartStreamBroadcastCmd(int nCodecType, int nCodecIdx, StringBuffer strRtpAddr, StringBuffer strRtcpAddr);

	/**
	* StopStreamBroadcastCmd
	* @brief   停止码流广播命令(主要是mtc客户端使用)
	*
	* @param    [in]   nCodecType(EmCodecComponent_Api)  编解码器类型 
	* @param    [in]   nCodecIdx(EmCodecComponentIndex_Api)   某一路
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	* @note
	*
	*/
	public static native int StopStreamBroadcastCmd(int nCodecType, int nCodecIdx, int nSSID);

	// 停止码流广播命令(主要是一体终端使用)
	public static native int StopStreamBroadcastCmd(int nCodecType, int nCodecIdx);

	/**
	* StartStreamTransCmd
	* @brief   转发某一路码流命令(主要是mtc客户端使用)
	*
	* @param    [in]   nCodecType(EmCodecComponent_Api)  编解码器类型 
	* @param    [in]   nCodecIdx(EmCodecComponentIndex_Api)   某一路
	* @param    [in]   StringBuffer(TRtpRtcpPair_Api) RTP和RTCP地址信息
	* Json格式如下：
	* {
	*    "tRtpAddr": {
	*        "emIpType": 1,
	*        "dwIp": 12345,
	*        "achIpV6": "sdffsdf",
	*        "dwPort": 60000
	*    },
	*    "tRtcpAddr": {
	*        "emIpType": 1,
	*        "dwIp": 12345,
	*        "achIpV6": "sdffsdf",
	*        "dwPort": 60001
	*    }
	*}
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	* @note
	*
	*/
	public static native int StartStreamTransCmd(int nCodecType, int nCodecIdx, StringBuffer strRtpRtcpAddr, int nSSID);

	// 转发某一路码流命令(主要是一体终端使用)
	public static native int StartStreamTransCmd(int nCodecType, int nCodecIdx, StringBuffer strRtpRtcpAddr);

	/**
	* StopStreamTransCmd
	* @brief   停止某一路码流命令(主要是mtc客户端使用)
	*
	* @param    [in]   nCodecType(EmCodecComponent_Api)  编解码器类型 
	* @param    [in]   nCodecIdx(EmCodecComponentIndex_Api)   某一路
	* @param    [in]   StringBuffer(TRtpRtcpPair_Api) RTP和RTCP地址信息
	* Json格式如下：
	* {
	*    "tRtpAddr": {
	*        "emIpType": 1,
	*        "dwIp": 12345,
	*        "achIpV6": "sdffsdf",
	*        "dwPort": 60000
	*    },
	*    "tRtcpAddr": {
	*        "emIpType": 1,
	*        "dwIp": 12345,
	*        "achIpV6": "sdffsdf",
	*        "dwPort": 60001
	*    }
	*}
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	* @note
	*
	*/
	public static native int StopStreamTransCmd(int nCodecType, int nCodecIdx, StringBuffer strRtpRtcpAddr, int nSSID);

	// 停止某一路码流命令(主要是一体终端使用)
	public static native int StopStreamTransCmd(int nCodecType, int nCodecIdx, StringBuffer strRtpRtcpAddr);

	/**
	* StartRecoderCmd
	* @brief   开始某一路录像命令(主要是mtc客户端使用)
	*
	* @param    [in]   nCodecType(EmCodecComponent_Api)  编解码器类型 
	* @param    [in]   nCodecIdx()   某一路
	* @param    [in]   StringBuffer(TRtpRtcpPair_Api) RTP和RTCP地址信息
	* Json格式如下：
	* {
	*    "tRtpAddr": {
	*        "emIpType": 1,
	*        "dwIp": 12345,
	*        "achIpV6": "sdffsdf",
	*        "dwPort": 60000
	*    },
	*    "tRtcpAddr": {
	*        "emIpType": 1,
	*        "dwIp": 12345,
	*        "achIpV6": "sdffsdf",
	*        "dwPort": 60001
	*    }
	*}
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	* @note
	*
	*/
	public static native int StartRecoderCmd(int nCodecType, int nCodecIdx, StringBuffer strRtpRtcpAddr, int nSSID);

	// 开始某一路录像命令(主要是一体终端使用)
	public static native int StartRecoderCmd(int nCodecType, int nCodecIdx, StringBuffer strRtpRtcpAddr);

	/**
	* StopRecoderCmd
	* @brief   停止某一路录像命令(主要是mtc客户端使用)
	*
	* @param    [in]   nCodecType(EmCodecComponent_Api)  编解码器类型 
	* @param    [in]   CodecIdx(EmCodecComponentIndex_Api)   某一路
	* @param    [in]   StringBuffer(TRtpRtcpPair_Api) RTP和RTCP地址信息
	* Json格式如下：
	* {
	*    "tRtpAddr": {
	*        "emIpType": 1,
	*        "dwIp": 12345,
	*        "achIpV6": "sdffsdf",
	*        "dwPort": 60000
	*    },
	*    "tRtcpAddr": {
	*        "emIpType": 1,
	*        "dwIp": 12345,
	*        "achIpV6": "sdffsdf",
	*        "dwPort": 60001
	*    }
	*}
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	* @note
	*
	*/
	public static native int StopRecoderCmd(int nCodecType, int nCodecIdx, StringBuffer strRtpRtcpAddr, int nSSID);

	// 停止某一路录像命令(主要是一体终端使用)
	public static native int StopRecoderCmd(int nCodecType, int nCodecIdx, StringBuffer strRtpRtcpAddr);

	/**
	* GetMonitorParamCmd
	* @brief   获取监控参数命令(主要是mtc客户端使用)
	*
	* @param    [in]   nCodecType(EmCodecComponent_Api)  编解码器类型 
	* @param    [in]   nCodecIdx(EmCodecComponentIndex_Api)   某一路
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	*
	* @note 响应通知：Ev_MtApi_Vc_CodecMonitorParam_Ntf
	*/
	public static native int GetMonitorParamCmd(int nCodecType, int nCodecIdx, int nSSID);

	// 获取监控参数命令(主要是一体终端使用)
	public static native int GetMonitorParamCmd(int nCodecType, int nCodecIdx);

	/**
	* SendFastUpdateCmd
	* @brief   请求某一路码流关键帧命令(主要是mtc客户端使用)
	*
	* @param    [in]   nCodecType(EmCodecComponent_Api)  编解码器类型 
	* @param    [in]   nCodecIdx(EmCodecComponentIndex_Api)   某一路
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	* @note
	*
	*/
	public static native int SendFastUpdateCmd(int nCodecType, int nCodecIdx, int nSSID);

	// 请求某一路码流关键帧命令(主要是一体终端使用)
	public static native int SendFastUpdateCmd(int nCodecType, int nCodecIdx);

	/**
	* MovePIPCmd
	* @brief   移动画中画命令 (仅Embed)(主要是mtc客户端使用)
	*
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	* @note
	*
	*/
	public static native int MovePIPCmd(int nSSID);

	// 移动画中画命令 (仅Embed)(主要是一体终端使用)
	public static native int MovePIPCmd();

	/**
	* SwitchPIPCmd
	* @brief   切换画中画命令(仅Embed)
	*
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	* @note
	*
	*/
	public static native int SwitchPIPCmd(int nSSID);

	// 切换画中画命令(仅Embed)
	public static native int SwitchPIPCmd();

	/**
	* SetPIPReq
	* @brief   设置多画面风格请求
	*
	* @param    [in]   nPiPMode 多画面风格       MT会话ID 
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	* @note
	*
	*/
	public static native int SetPIPReq(int nPiPMode, int nSSID);

	public static native int SetPIPReq(int nPiPMode);

	public static native int GetLocalVideoViewReq(int nSSID);

	public static native int GetLocalVideoViewReq();

	/**
	* QueryPIPReq
	* @brief   查询多画面风格请求
	*
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	* @note
	*
	*/
	public static native int QueryPIPReq(int nSSID);

	public static native int QueryPIPReq();

	/**
	* SelectLocalVideoViewCmd
	* @brief   设置摄像头控制是远端，还是本地
	*
	* @param    [in]   nEncoder_id        本地还是远端
	* @param    [in]   nSSID       MT会话ID 
	* @return   u32    请求命令发送结果
	* @note
	*
	*/
	public static native int SelectLocalVideoViewCmd(int nEncoder_id, int nSSID);

	public static native int SelectLocalVideoViewCmd(int nEncoder_id);

	/**
	* IsStreamLostPack
	* @brief   5S内当前是否丢包(主要是mtc客户端使用)
	*
	* @param    [in]   nSSID       MT会话ID 
	* @return   boolean    TRUE:丢包 FALSE:未丢包
	*
	* @note 更新通知：Ev_MtApi_Vc_CodecLostPack_Ntf
	*/
	public static native boolean IsStreamLostPack(int nSSID);

	// 5S内当前是否丢包(主要是一体终端使用)
	public static native boolean IsStreamLostPack();

	/**
	* GetStremParamList
	* @brief 当前码流监控参数获取
	*	
	* @param    [in] SSID 会话id
	* @return	监控码流的参数
	*[
	*    {
	*        "emCodecType": 2,
	*        "emCodecIdx": 2,
	*        "byMediaType": 106,
	*        "byPlayLoad": 106,
	*        "tEncryptKey": {
	*            "byLen": 20,
	*            "byKey": "adbdfdlf"
	*        },
	*        "bIsg7221Reverse": true,
	*        "emAacChanNum": 1,
	*        "emAacSampFreq": 1
	*    }
	*]
	* @note 
	*/
	public static native String GetStremParamList(int nSSID);

	public static native String GetStremParamList();

	/**
	* SetAddLogoIntoEncParamCmd
	* @brief   设置台标加入码流(仅win32)
	*
	* @param    [in]  bAddIntoStream
	* @param    [in]  strLogoParam
	{
		"achBmpBuf" : "",
		"dwBmpBufSize" : 0,
		"tLogoParam" : {
			"fXPosRate" : 0.00,
			"fYPosRate" : 0.00,
			"fLogoWidthRate" : 0.00,
			"fLogoHeightRate" : 0.00,
			"tBackBGDColor" : 	{
				"dwRColor" : 0,
				"dwGColor" : 0,
				"dwBColor" : 0,
				"dwTransparency" : 0
			}
		}
	}
	* @param    [in]   nSSID       MT会话ID 
	* @return    u32    请求命令发送结果
	*/
	public static native int SetAddLogoIntoEncParamCmd(boolean bAddIntoStream, StringBuffer strLogoParam, int nSSID);

	public static native int SetAddLogoIntoEncParamCmd(boolean bAddIntoStream, StringBuffer strLogoParam);

	/**
	* SetMobilAddLogoIntoEncParamCmd (mobil)
	* @brief    设置台标加入码流
	*
	* @param    [in]   bAddIntoStream           是否加入
	* @param    [in]   strLogoParamList       台标参数 数组[]
	{
		"atLogoParamList":[
		{
		"achBmpPath" : "",
		"tLogoParam" : {
			"fXPosRate" : 0.00,
			"fYPosRate" : 0.00,
			"fLogoWidthRate" : 0.00,
			"fLogoHeightRate" : 0.00,
			"tBackBGDColor" : 	{
				"dwRColor" : 0,
				"dwGColor" : 0,
				"dwBColor" : 0,
				"dwTransparency" : 0
			}
		}
	}
	],
		"dwCount" : 1
	}
	* @param    [in]   nSSID          MT会话ID
	* @return   u32    0：成功        其他错误码
	*/
	public static native int SetMobilAddLogoIntoEncParamCmd( boolean bAddIntoStream, StringBuffer strLogoParamList, int nSSID);

	public static native int SetMobilAddLogoIntoEncParamCmd( boolean bAddIntoStream, StringBuffer strLogoParamList);

	// //设置,更新，呼叫能力
	/**
	* SetCallCapCmd
	* @brief  设置,更新，呼叫能力
	*
	* @param    [in]  strSendCallCap(TMtSendCap_Api)
		{
	"atAss_aud_send_cap" : [
	{
	"atAudList" : [
	{
	"dwPack_time" : 0,
	"emChnl_num" : 0,
	"emFormat" : 0
	}
	],
	"dwCount" : 1
	}
	],
	"atAss_vid_send_cap" : [
	{
	"atVidList" : [
	{
	"dwItemCount" : 0,
	"emFormat" : 0,
	"emH264Modes" : 0,
	"emH264Profile" : 0,
	"emH265Profile" : 0
	}
	],
	"dwCount" : 1,
	"emVidLab" : 0
	}
	],
	"atMain_aud_send_cap" : [
	{
	"atAudList" : [
	{
	"dwPack_time" : 0,
	"emChnl_num" : 0,
	"emFormat" : 0
	}
	],
	"dwCount" : 1
	}
	],
	"atMain_vid_send_cap" : [
	{
	"atVidList" : [
	{
	"dwItemCount" : 0,
	"emFormat" : 0,
	"emH264Modes" : 0,
	"emH264Profile" : 0,
	"emH265Profile" : 0
	}
	],
	"dwCount" : 1,
	"emVidLab" : 0
	}
	],
	"dwAssAudSndCount" : 1,
	"dwAssVidSndCount" : 1,
	"dwMainAudSndCount" : 1,
	"dwMainVidSndCount" : 1
	}
	* @param    [in]  strRecvCallCap(TMtRecvCap_Api)
	{
	"atAss_aud_recv_cap" : [
	{
	"atAudList" : [
	{
	"dwPack_time" : 0,
	"emChnl_num" : 0,
	"emFormat" : 0
	}
	],
	"dwCount" : 1
	}
	],
	"atAss_vid_recv_cap" : [
	{
	"atVidList" : [
	{
	"dwItemCount" : 0,
	"emFormat" : 0,
	"emH264Modes" : 0,
	"emH264Profile" : 0,
	"emH265Profile" : 0
	}
	],
	"dwCount" : 1,
	"emVidLab" : 0
	}
	],
	"atMain_aud_recv_cap" : [
	{
	"atAudList" : [
	{
	"dwPack_time" : 0,
	"emChnl_num" : 0,
	"emFormat" : 0
	}
	],
	"dwCount" : 1
	}
	],
	"atMain_vid_recv_cap" : [
	{
	"atVidList" : [
	{
	"dwItemCount" : 0,
	"emFormat" : 0,
	"emH264Modes" : 0,
	"emH264Profile" : 0,
	"emH265Profile" : 0
	}
	],
	"dwCount" : 1,
	"emVidLab" : 0
	}
	],
	"dwAssAudRcvCount" : 1,
	"dwAssVidRcvCount" : 1,
	"dwMainAudRcvCount" : 1,
	"dwMainVidRcvCount" : 1
	}
	* @param    [in]  nPro(EmConfProtocol_Api)
	
	* @param    [in]   nSSID       MT会话ID 
	* @return    u32    请求命令发送结果
	*/
	public static native int SetCallCapCmd(StringBuffer strSendCallCap, StringBuffer strRecvCallCap, int nPro, int nSSID);

	public static native int SetCallCapCmd(StringBuffer strSendCallCap, StringBuffer strRecvCallCap, int nPro);

	// [for android mobile]
	/** 设置呼叫、接听支持的最大分辨率
	
	*	enum EmMtResolution_Api
	*	{
	*		emMtResAuto_Api             = 0,   ///<自适应
	*		emMtSQCIF_Api               = 1,   ///<SQCIF 88x72
	*		emMtQCIF_Api                = 2,   ///<QCIF 176x144
	*		emMtCIF_Api                 = 3,   ///<CIF 352x288
	*		emMt2CIF_Api                = 4,   ///<2CIF 352x576
	*		emMt4CIF_Api                = 5,   ///<4CIF 704x576
	*		emMt16CIF_Api               = 6,   ///<16CIF 1408x1152
	*		emMtVGA352x240_Api          = 7,   ///<352x240  对应平台SIF
	*		emMt2SIF_Api                = 8,   ///<对应平台2SIF，具体不知道多少*多少                  
	*		emMtVGA704x480_Api          = 9,   ///<704x480  对应平台4SIF
	*		emMtVGA640x480_Api          = 10,  ///<VGA 640x480                   
	*		emMtVGA800x600_Api          = 11,  ///<SVGA 800x600                     
	*		emMtVGA1024x768_Api         = 12,  ///<XGA 1024x768                     
	*		emMtVWCIF_Api               = 13,  ///<WCIF 512*288
	*		emMtVSQCIF112x96_Api        = 14,  ///<SQCIF(112*96)
	*		emMtVSQCIF96x80_Api         = 15,  ///<SQCIF(96*80) 
	*		emMtVW4CIF_Api              = 16,  ///<Wide 4CIF(1024*576)
	*		emMtHD720p1280x720_Api      = 17,  ///<720p 1280x720
	*		emMtVGA1280x1024_Api        = 18,  ///<SXGA 1280x1024
	*		emMtVGA1600x1200_Api        = 19,  ///<UXGA 1600x1200
	*		emMtHD1080i1920x1080_Api    = 20,  ///<1080i 1920x1080
	*		emMtHD1080p1920x1080_Api    = 21,  ///<1080p 1920x1080          
	*		emMtVGA1280x800_Api         = 22,  ///<WXGA 1280x800
	*		emMtVGA1440x900_Api         = 23,  ///<WSXGA 1440x900                     
	*		emMtVGA1280x960_Api         = 24,  ///<XVGA  1280x960                    
	*		emMtV1440x816_Api           = 25,  ///<1440×816(3/4)
	*		emMt1280x720_Api            = 26,  ///<1280×720(2/3)
	*		emMtV960x544_Api            = 27,  ///<960×544(1/2)
	*		emMtV640x368_Api            = 28,  ///<640×368(1/3)
	*		emMtV480x272_Api            = 29,  ///<480×272(1/4)
	*		emMt384x272_Api             = 30,  ///<384×272(1/5)
	*		emMt640x544_Api             = 31,  ///<640x544                       
	*		emMt320x272_Api             = 32,  ///<320x272
	*		emMt_720_960x544_Api        = 33, ///<960×544(3/4)
	*		emMt_720_864x480_Api        = 34, ///<864×480(2/3)
	*		emMt_720_640x368_Api        = 35, ///<640×368(1/2)
	*		emMt_720_432x240_Api        = 36, ///<432×240(1/3)
	*		emMt_720_320x192_Api        = 37, ///<320×192(1/4)
	*		emMtVGA480x352_Api          = 38,     ///<480×352, iPad专用
	*		emMtHD480i720x480_Api       = 39,   ///<480i720x480
	*		emMtHD480p720x480_Api       = 40,   ///<480p720x480
	*		emMtHD576i720x576_Api       = 41,   ///<576i720x576
	*		emMtHD576p720x576_Api       = 42,   ///<576p720x576              
	*		emMtVGA1280x768_Api         = 43,   ///<WXGA1280x768
	*		emMtVGA1366x768_Api         = 44,   ///<WXGA1366x768
	*		emMtVGA1280x854_Api         = 45,   ///<WSXGA1280x854
	*		emMtVGA1680x1050_Api        = 46,   ///<WSXGA+1680x1050
	*		emMtVGA1920x1200_Api        = 47,   ///<WUXGA1920x1200
	*		emMtV3840x2160_Api          = 48,   ///<4Kx2K3840x2160
	
	*		emMtVResEnd_Api             = 100,	  
	*	};
	*
	*	enum EmConfProtocol_Api
	*	{
	*		emProtocolBegin_Api = 0,
	*		em323_Api,    ///<H323
	*		emsip_Api,    ///<SIP
	*		emsat_Api,    ///<SAT
	*		emtip_Api,    ///<TIP
	*	};
	*
	*/
	public static native int SetCallCapPlusCmd(int nSendMaxResolution, int nRecvMaxResolution, int nPro);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
