/**
 * @(#)TPrsParam.java   2014-12-12
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.kedacom.kdv.mt.mtapi.bean;

/**
  * 丢包重传参数
  * @author weiyunliang 
  * @date 2014-12-12
  */

public class TPrsParam extends TMtApi{

	public boolean bEnable = true;
	public int dwFirstTimeSpan = 0; // 第一个重传检测点(ms)
	public int dwSecondTimeSpan = 0; // 第二个重传检测点(ms)
	public int dwThirdTimeSpan = 0; // 第三个重传检测点(ms)
	public int dwRejectTimeSpan = 0; // 过期丢弃的时间跨度(ms)
	public int dwSendBufTimeSpan = 0; // 发送缓存 default 1000
	public boolean bUseSmoothSend = false; // 启用平滑发送
}
