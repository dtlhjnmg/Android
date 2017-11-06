package com.onemore.ble.maintain.applications;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Application;

import com.onemore.ble.maintain.bean.DeviceInfo;
import com.onemore.ble.maintain.services.IWoodenLampBLL;
import com.onemore.ble.maintain.services.WoodenLampBLL;

public class MainApplication extends Application {
	public static MainApplication instance;
	private IWoodenLampBLL woodenLampBLL = null;
	private HashMap<String, DeviceInfo> lightInfoHashMap = new HashMap<String, DeviceInfo>();
	private DeviceInfo currentLightInfo;
	private String currentAddress = null;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public static MainApplication getInstance() {
		if (null == instance)
			instance = new MainApplication();
		return instance;

	}

	public IWoodenLampBLL getWoodenLampBLL() {
		if(woodenLampBLL==null){
			woodenLampBLL=WoodenLampBLL.getInstance();
		}
		return woodenLampBLL;
	}

	public void closeAllDevice(){
		Iterator iter = lightInfoHashMap.entrySet().iterator();  
		while (iter.hasNext()) {  
		    Map.Entry entry = (Map.Entry) iter.next();  
		    Object key = entry.getKey();  
		    Object val = entry.getValue();  
		    DeviceInfo li = (DeviceInfo)val;
		    if(li.getBleManager()!=null){
		    	li.getBleManager().disconnect();
		    	li.getBleManager().close();
		    }
		}
		setCurrentAddress(null);
	}
/*	public LightInfo getCurrentLightInfo() {
		if (currentLightInfo == null && ContentUtils.IS_DEBUG) {
			LightInfo lightInfo = new LightInfo();
			lightInfo.setDeviceName("Smargic");
			lightInfo.setMacAddress("00:11:22:33:44");
			addLightInfo(lightInfo);
			return lightInfo;
		} else {
			if (currentAddress != null) {
				return lightInfoHashMap.get(currentAddress);
			} else {
				return null;
			}
		}
	}*/

	public DeviceInfo getLightInfo(){
		if (currentAddress != null) {
			return lightInfoHashMap.get(currentAddress);
		} else {
			return null;
		}
	}
	public DeviceInfo getLightInfoByAddress(String lightAddress) {
		if (lightAddress != null) {
			return lightInfoHashMap.get(lightAddress);
		} else {
			return null;
		}
	}
	public void clearAllInfo(){
		if(lightInfoHashMap != null){
			lightInfoHashMap.clear();
		}
	}
	public void addLightInfo(DeviceInfo lightInfo) {
		if (!lightInfoHashMap.containsKey(lightInfo.getMacAddress())) {
			lightInfoHashMap.put(lightInfo.getMacAddress(), lightInfo);
		}
	}
	public void updateLightInfo(DeviceInfo lightInfo){
		if(lightInfoHashMap.containsKey(lightInfo.getMacAddress())){
			DeviceInfo li = lightInfoHashMap.get(lightInfo.getMacAddress());
			li.setResponseCode(lightInfo.getResponseCode());
			li.setResult(lightInfo.getResult());
			if(li.getDeviceName()==null||li.getDeviceName().length()==0){
				li.setDeviceName(lightInfo.getDeviceName());
			}
			li.setLightBrightness(lightInfo.getLightBrightness());
			li.setLightState(lightInfo.getLightState());
			li.setColorTemperature(lightInfo.getColorTemperature());
			li.setTime(lightInfo.getTime());
			li.setBColor(lightInfo.getBColor());
			li.setRColor(lightInfo.getRColor());
			li.setGColor(lightInfo.getGColor());
			//li.setTimerArrayList(li.getTimerArrayList());
			for(int id:lightInfo.getTimerArrayList()){
				li.getTimerArrayList().add(id);
			}
		}
	}

	public String getCurrentAddress() {
		return currentAddress;
	}

	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}
}
