package com.kedacom.kdv.mt.mtapi;


public class MtcLib {

	// 启动
	/**
	* Start
	* @brief 对整个终端进行初始化
	*
	* @param	[in] bIsMtcMode  是否是mtc终端
	* @return	void
	* @note 
	*/
	public static native void Start(boolean bIsMtcMode);

	/**
	* Connect
	* @brief 连接终端（这个只在start参数bIsMtcMode为true时使用，即只有mtc模式的时候使用）
	*
	* @param	[out]   StringBuffer(u32)      返回创建的会话ID(json格式：“{"basetype":u32}”)
	* @param    [in]    strIp                  要连接的终端IP ( emMtLocMode_Api模式无效 )
	* @param    [in]    nPort(u16)             终端监听Port   ( emMtLocMode_Api模式无效 )
	* @param    [in]    strUsrName             要登录终端的用户名 ( emMtLocMode_Api模式无效 )
	* @param    [in]    strUsrPwd              要登录终端的密码  ( emMtLocMode_Api模式无效 )
	* @return	u32  0: 创建成功， 非0: 创建失败
	* @note DisConnect
	*/
	public static native int Connect(StringBuffer strSessionID, StringBuffer strIp, int nPort, StringBuffer strUsrName, StringBuffer strUsrPwd);

	// 断开终端（与connnect对应）
	public static native void DisConnect(int nSessionID);

	/**
	* KdvMt_GetSessionByIp
	* @brief  通过终端IP查找已创建的Session
	*
	* @param  [in]    strMtIp  要查找的终端IP 
	* @return  SessionID  返回对应的SessionID, 不存在为INVALID_SESSIONID
	*
	*
	*/
	public static native int GetSessionByIp(StringBuffer strMtIp);

	/**
	* GetSessionByIdx
	* @brief  通过索引查找对应的SessionID
	*
	* @param  [in]    dwInIdxdx  会话列表索引 
	* @return  SessionID  返回对应的SessionID, 不存在为INVALID_SESSIONID
	*
	*
	*/
	public static native int GetSessionByIdx(int nIdx);

	/**
	* GetMaxSessionCnt
	* @brief  获取最大支持的会话数量
	*
	* @param  [in]   void 
	* @return  int   返回最大支持的session数量
	*
	*/
	public static native int GetMaxSessionCnt();

	/**
	* GetUsedSessionCnt
	* @brief  获取已经存在的会话数量
	*
	* @param  [in]   void 
	* @return  int   返回已经存在的会话数量
	*
	*/
	public static native int GetUsedSessionCnt();

	/**
	* Quit
	* @brief 对整个终端退处理
	*
	* @param	void
	* @return	void
	* @note 
	*/
	public static native void Quit();

	// 设置回调对象
	public static native void Setcallback(Object callback);

	// 获取编译日期
	public static native String GetDateTime();

}
