package com.onemore.ble.maintain.services;

import com.onemore.ble.maintain.bean.DeviceInfo;

public interface IMessageResponseCallBack {
    public void onDataResponse(DeviceInfo lightInfo);
}
