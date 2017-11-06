/**
 * 
 */
package com.onemore.ble.maintain.util;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.R.integer;

import com.onemore.ble.maintain.bean.DeviceInfo;

/**
 * @author wuzhiyi
 * @date 2015-6-29 ����5:43:23
 */
public class ContentUtils {
	public final static boolean flag = true;
	public final static boolean IS_DEBUG = false;
	public final static String TRANSMISSION_HEAD = "\u0002$";
	public final static String TRANSMISSION_END = "\n";
	public final static String TRANSMISSION_DECOLLATOR = "#";
	private final static String TRANSMISSION_COMMAND_TEST = TRANSMISSION_HEAD
			+ "00";
	private final static String TRANSMISSION_COMMAND_TEST_ACK = TRANSMISSION_HEAD
			+ "FF";
	private final static String TRANSMISSION_COMMAND_GET_DEVICE_INFO = TRANSMISSION_HEAD
			+ "01";
	private final static String TRANSMISSION_COMMAND_GET_DEVICE_INFO_ACK = TRANSMISSION_HEAD
			+ "FE";
	private final static String TRANSMISSION_COMMAND_SET_DEVICE_TIME = TRANSMISSION_HEAD
			+ "02";
	private final static String TRANSMISSION_COMMAND_SET_DEVICE_TIME_ACK = TRANSMISSION_HEAD
			+ "FD";
	private final static String TRANSMISSION_COMMAND_SET_SWITCH_STATE = TRANSMISSION_HEAD
			+ "03";
	private final static String TRANSMISSION_COMMAND_SET_SWITCH_STATE_ACK = TRANSMISSION_HEAD
			+ "FC";
	private final static String TRANSMISSION_COMMAND_SET_COLOR_TEMPERATURE = TRANSMISSION_HEAD
			+ "04";
	private final static String TRANSMISSION_COMMAND_SET_COLOR_TEMPERATURE_ACK = TRANSMISSION_HEAD
			+ "FB";
	private final static String TRANSMISSION_COMMAND_SET_DEVICE_NAME = TRANSMISSION_HEAD
			+ "05";
	private final static String TRANSMISSION_COMMAND_SET_DEVICE_NAME_ACK = TRANSMISSION_HEAD
			+ "FA";
	private final static String TRANSMISSION_COMMAND_GET_TIME_FUNCTION = TRANSMISSION_HEAD
			+ "06";
	private final static String TRANSMISSION_COMMAND_GET_TIME_FUNCTION_ACK = TRANSMISSION_HEAD
			+ "F9";
	private final static String TRANSMISSION_COMMAND_MODIFY_TIME_FUNCTION = TRANSMISSION_HEAD
			+ "07";
	private final static String TRANSMISSION_COMMAND_MODIFY_TIME_FUNCTION_ACK = TRANSMISSION_HEAD
			+ "F8";
	private final static String TRANSMISSION_COMMAND_ADD_TIME_FUNCTION = TRANSMISSION_HEAD
			+ "08";
	private final static String TRANSMISSION_COMMAND_ADD_TIME_FUNCTION_ACK = TRANSMISSION_HEAD
			+ "F7";
	private final static String TRANSMISSION_COMMAND_DELETE_TIME_FUNCTION = TRANSMISSION_HEAD
			+ "09";
	private final static String TRANSMISSION_COMMAND_DELETE_TIME_FUNCTION_ACK = TRANSMISSION_HEAD
			+ "F6";
	private final static String TRANSMISSION_COMMAND_SET_RGB = TRANSMISSION_HEAD
			+ "0A";
	private final static String TRANSMISSION_COMMAND_SET_RGB_ACK = TRANSMISSION_HEAD
			+ "F5";
	private final static String TRANSMISSION_COMMAND_SET_LIGHT_BRIGHTNESS = TRANSMISSION_HEAD
			+ "0b";
	private final static String TRANSMISSION_COMMAND_SET_LIGHT_BRIGHTNESS_ACK = TRANSMISSION_HEAD
			+ "F4";

	private final static String TRANSMISSION_COMMAND_LIGHT_INFO_CHANGE = TRANSMISSION_HEAD
			+ "0c";
	public final static int RESPONSE_RESULT_CODE_SUCCESS = 0;
	public final static int RESPONSE_RESULT_CODE_FAILTH = 1;
	public final static int RESPONSE_RESULT_CODE_TYPE_ERROR = 2;
	public final static int RESPONSE_RESULT_CODE_FORMAT_ERROR = 3;
	public final static int RESPONSE_RESULT_CODE_ILLEGAL_CONTENT = 4;

	public final static int LIGHT_STATE_OPEN = 1;
	public final static int LIGHT_STATE_CLOSE = 0;

	public final static int COMMAND_GET_DEVICE_TOKEN = 0;
	public final static int COMMAND_GET_DEVICE_INFO = 1;
	public final static int COMMAND_SET_DEVICE_TIME = 2;
	public final static int COMMAND_SET_SWITCH_STATE = 3;
	public final static int COMMAND_SET_COLOR_TEMPERATURE = 4;
	public final static int COMMAND_SET_DEVICE_NAME = 5;
	public final static int COMMAND_GET_TIME_FUNCTION = 6;
	public final static int COMMAND_MODIFY_TIME_FUNCTION = 7;
	public final static int COMMAND_ADD_TIME_FUNCTION = 8;
	public final static int COMMAND_DELETE_TIME_FUNCTION = 9;
	public final static int COMMAND_SET_RGB = 10;
	public final static int COMMAND_SET_LIGHT_BRIGHTNESS = 11;
	public final static int COMMAND_LIGHT_INFO_CHANGE = 12;

	public static byte[] encodeSendContent(int command,DeviceInfo lightInfo){
		ByteBuffer byteBuffer = ByteBuffer.allocate(16);
		switch(command){
			case COMMAND_GET_DEVICE_TOKEN:
				byteBuffer.put((byte)0x06);
				byteBuffer.put((byte)0x01);
				byteBuffer.put((byte)0x01);
				byteBuffer.put((byte)0x01);
			break;
		}
		
		return byteBuffer.array();
		
	}
	public static String encodeContent(int command, DeviceInfo lightInfo) {
		StringBuffer sbBuffer = new StringBuffer();
		switch (command) {
		case COMMAND_GET_DEVICE_TOKEN:
			sbBuffer.append(TRANSMISSION_COMMAND_TEST);
			sbBuffer.append(TRANSMISSION_DECOLLATOR);
			break;
		case COMMAND_GET_DEVICE_INFO:
			sbBuffer.append(TRANSMISSION_COMMAND_GET_DEVICE_INFO);
			sbBuffer.append(TRANSMISSION_DECOLLATOR);
			break;
		case COMMAND_SET_DEVICE_TIME:
			sbBuffer.append(TRANSMISSION_COMMAND_SET_DEVICE_TIME);
			sbBuffer.append(TRANSMISSION_DECOLLATOR);
			sbBuffer.append(lightInfo.getTime());
			sbBuffer.append(TRANSMISSION_DECOLLATOR);
			break;
		case COMMAND_SET_SWITCH_STATE:
			//sbBuffer.append(TRANSMISSION_COMMAND_SET_SWITCH_STATE);
			//sbBuffer.append(TRANSMISSION_DECOLLATOR);
			sbBuffer.append(lightInfo.getLightState() ? "\u0001"
					: "\u0000");
			//sbBuffer.append(TRANSMISSION_DECOLLATOR);
			break;
		case COMMAND_SET_COLOR_TEMPERATURE:
			sbBuffer.append(TRANSMISSION_COMMAND_SET_COLOR_TEMPERATURE);
			sbBuffer.append(TRANSMISSION_DECOLLATOR);
			sbBuffer.append(lightInfo.getColorTemperature());
			sbBuffer.append(TRANSMISSION_DECOLLATOR);
			break;
		case COMMAND_SET_DEVICE_NAME:
			sbBuffer.append(TRANSMISSION_COMMAND_SET_DEVICE_NAME);
			sbBuffer.append(TRANSMISSION_DECOLLATOR);
			try {
				String ss = lightInfo.getDeviceName();// URLEncoder.encode(lightInfo.getDeviceName(),"utf-8");;
				String u8 = new String(ss.getBytes(), "utf-8");
				sbBuffer.append(u8);
			} catch (Exception e) {
				// sbBuffer.append(lightInfo.getDeviceName());
			}
			sbBuffer.append(TRANSMISSION_DECOLLATOR);
			break;
		}
		String string = sbBuffer.toString();
		//sbBuffer.append(Integer.toHexString(getCheckNum(string)));
		//sbBuffer.append(TRANSMISSION_END);
		//L.d("encodeContent=" + sbBuffer.toString());
		return string;//sbBuffer.toString();
	}

	public static DeviceInfo decodeResponse(String response) {
		L.d("decodeResponse:" + response);
		DeviceInfo lightInfo = null;
		if (response.startsWith(TRANSMISSION_HEAD)
				&& response.endsWith(TRANSMISSION_END)) {
			lightInfo = new DeviceInfo();
			String lightInfoString = response.substring(
					TRANSMISSION_HEAD.length(), response.length() - 1);
			String[] infos = lightInfoString.split(TRANSMISSION_DECOLLATOR);
			if (infos.length >= 2) {
				int result = Integer.valueOf(infos[1]);// check result
				lightInfo.setResult(result);
				int ack = 255 - Integer.valueOf(infos[0], 16);
				lightInfo.setResponseCode(ack);
				switch (ack) {
				case COMMAND_GET_DEVICE_INFO:
					if (result == RESPONSE_RESULT_CODE_SUCCESS) {
						// \u0002$FE#0#SmargicTest#150630154040#1#057#1#000#000#000#3#7a\n
						if (infos.length >= 12) {
							try {
								lightInfo.setDeviceName(URLDecoder.decode(
										infos[2], "UTF-8"));
							} catch (Exception e) {
							}
							lightInfo.setTime(infos[3]);
							lightInfo
									.setLightState(Integer.valueOf(infos[4]) == LIGHT_STATE_OPEN);
							lightInfo.setLightBrightness(Integer
									.valueOf(infos[5]));
							lightInfo.setColorTemperature(Integer
									.valueOf(infos[6]));
							lightInfo.setRColor(Integer.valueOf(infos[7]));
							lightInfo.setGColor(Integer.valueOf(infos[8]));
							lightInfo.setBColor(Integer.valueOf(infos[9]));
							int timer = Integer.parseInt(infos[10], 16);
							int i = 0;
							while (timer > 0) {
								if ((timer & 1) == 1) {
									lightInfo.getTimerArrayList().add(i);
								}
								i++;
								timer >>= 1;
							}
						}
					}
					break;
				case COMMAND_LIGHT_INFO_CHANGE:
					if (result == RESPONSE_RESULT_CODE_SUCCESS) {
						if (infos.length >= 7) {
							lightInfo
									.setLightState(Integer.valueOf(infos[2]) == LIGHT_STATE_OPEN);
							lightInfo.setLightBrightness(Integer
									.valueOf(infos[3]));
							lightInfo.setColorTemperature(Integer
									.valueOf(infos[4]));
							lightInfo.setChangeTime(Long.valueOf(infos[5]));
						}
					}
					break;
				}
			}
		}
		return lightInfo;
	}

	private static int getCheckNum(String sendContent) {
		long checkNum = 0;
		byte[] ch = sendContent.getBytes();
		for(int i=0;i<ch.length;i++){
			int v = ch[i] & 0xFF;  
			checkNum+=v;
			//L.d("t="+checkNum+",ch="+ch[i]);
		}
		 //L.d("checkNum="+checkNum);
		// L.d("after %:"+checkNum%256);
		return (int) (checkNum % 256);
	}

	public static String binary(byte[] bytes, int radix) {
		return new BigInteger(1, bytes).toString(radix);// �����1��������
	}

}
