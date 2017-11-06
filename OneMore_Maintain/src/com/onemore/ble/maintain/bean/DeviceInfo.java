package com.onemore.ble.maintain.bean;

import java.util.ArrayList;

import com.onemore.ble.maintain.BleManager;
import com.onemore.ble.maintain.util.ContentUtils;
import com.onemore.ble.maintain.util.L;

import android.R.integer;


public class DeviceInfo{
	private int TIME_FUNCTION_COUNT = 10;
	public final static Boolean LIGHT_STATE_OPEN = true;
	public final static Boolean LIGHT_STATE_CLOSE = false;
	private Boolean lightState;
	private int lightBrightness;
	private int lightIndex;
	private int colorTemperature;
	private long changeTime;
	private int manufacturer;
	private String deviceName;
	private String macAddress;
	private String time;
	private ArrayList<Integer> timerArrayList;
	private int rssi;
	private int RColor;
	private int GColor;
	private int BColor;
	private int result;
	private int responseCode;
	private BleManager bleManager = null;
	private boolean getInfo = false;
	private int keyType = 0;
	public DeviceInfo(){
	}
	
	public DeviceInfo(boolean shouldInit){
		if(shouldInit){
			lightState = false;
			lightBrightness = 10;
			bleManager = new BleManager();
		}
		lightState = false;
	}
	public Boolean getLightState() {
		return lightState;
	}
	public void setLightState(Boolean lightState) {
		this.lightState = lightState;
	}
	public int getLightBrightness() {
		return lightBrightness;
	}
	public void setLightBrightness(int lightBrightness) {
		this.lightBrightness = Math.max(lightBrightness,10);
	}
	public int getLightIndex() {
		return lightIndex;
	}
	public void setLightIndex(int lightIndex) {
		this.lightIndex = lightIndex;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public long getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(long changeTime) {
		this.changeTime = changeTime;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getColorTemperature() {
		return colorTemperature;
	}
	public void setColorTemperature(int colorTemperature) {
		this.colorTemperature = colorTemperature;
	}
	public int getRColor() {
		return RColor;
	}
	public void setRColor(int rColor) {
		RColor = rColor;
	}
	public int getGColor() {
		return GColor;
	}
	public void setGColor(int gColor) {
		GColor = gColor;
	}
	public int getBColor() {
		return BColor;
	}
	public void setBColor(int bColor) {
		BColor = bColor;
	}	
	
	public String getRGBString(){
		return this.RColor+ContentUtils.TRANSMISSION_DECOLLATOR+this.GColor+ContentUtils.TRANSMISSION_DECOLLATOR+
				this.BColor+ContentUtils.TRANSMISSION_DECOLLATOR;
	}
	
	public int getRssi(){
		return rssi;
	}
	
	public void setRssi(int theRssi){
		rssi = theRssi;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public boolean isSuccess(){
		return this.result == ContentUtils.RESPONSE_RESULT_CODE_SUCCESS;
	}
	public boolean isColorWarm(){
		return this.colorTemperature == ContentUtils.LIGHT_STATE_OPEN;
	}
	public ArrayList<Integer> getTimerArrayList() {
		if(timerArrayList==null){
			timerArrayList = new ArrayList<Integer>();
		}
		return timerArrayList;
	}
	public void setTimerArrayList(ArrayList<Integer> timerArrayList) {
		this.timerArrayList = timerArrayList;
	}
	public void removeTimeIndex(int timeIndex){
		for(Integer id:timerArrayList){
			if(id.equals(timeIndex)){
				timerArrayList.remove(id);
				break;
			}
		}
	}

	public BleManager getBleManager() {
		return bleManager;
	}

	public void setBleManager(BleManager bleManager) {
		this.bleManager = bleManager;
	}

	public boolean isGetInfo() {
		return getInfo;
	}

	public void setGetInfo(boolean getInfo) {
		this.getInfo = getInfo;
	}

	public int getKeyType() {
		return keyType;
	}

	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}

	public int getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(int manufacturer) {
		this.manufacturer = manufacturer;
	}
}
