/**
 * 
 */
package com.onemore.ble.maintain.services;

import android.os.Handler;

/**
 * @author wuzhiyi
 * @date 2015-10-14 ����5:37:50
 */
public class MessageEntity {
	public int messageId = -1;
	public int comType = 0;
	public IMessageResponseCallBack receiveHandler;
	public Handler sendHandler;
	public long sendTime;	
}
