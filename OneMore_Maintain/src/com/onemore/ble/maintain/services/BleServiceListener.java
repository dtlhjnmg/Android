package com.onemore.ble.maintain.services;

import com.onemore.ble.maintain.bean.DeviceInfo;

public interface BleServiceListener {
	public void onConnected();
	public void onDisconnected();
    public void onDataResponse(DeviceInfo lightInfo);
    public void onDataResponseContent(byte[] content);
}
