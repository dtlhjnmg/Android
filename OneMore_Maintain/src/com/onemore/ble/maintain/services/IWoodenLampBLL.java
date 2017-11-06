package com.onemore.ble.maintain.services;

import com.onemore.ble.maintain.BleManagerListener;

public interface IWoodenLampBLL extends BleManagerListener{
	public void registeBleServiceListener(BleServiceListener listener);

	public void unRegisteBleServiceListener(BleServiceListener listener);
	public Boolean getShouldReScan();
	public void setShouldReScan(Boolean shouldReScan);
	public void getDeviceInfo();
	
	public void setDeviceTime();
	public void getDeviceBattery();
	public void setLightState(boolean lightState);
	
	public void setLightBrightness(int brightness);
	public void setLightColorTemp(boolean colorTemp);
	public void changeDeviceName(String deviceName);
	public void setRGB(int rColor, int gColor, int bColor);
	public void read();
	public void getLockState();
	
	public void getToken();
	public void openLock(String openPassword);
	public void closeLock();
	public void switchAlarmState(boolean alarmState);
	public void changePassword(String oldPassword,String newPassword);
	public void changeAesKey(String aesKey,String newKey);
	public void setKey(String key);
	public void dfu();
	public void findDevice();
	public void recover();
	public void changeAPN(String apn);
	public void changeServer(String server);
	public void changePort(int port);
	public void sendBack();
	public void transportationMode();
	public void testACCE();
	public void testGPRS();
}
