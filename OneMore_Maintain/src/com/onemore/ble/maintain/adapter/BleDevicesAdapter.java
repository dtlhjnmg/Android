package com.onemore.ble.maintain.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onemore.ble.maintain.R;
import com.onemore.ble.maintain.bean.DeviceInfo;

public class BleDevicesAdapter extends BaseAdapter {
	private final LayoutInflater inflater;

	private final ArrayList<DeviceInfo> leDevices;
	private final HashMap<String, Integer> rssiMap = new HashMap<String, Integer>();

	public BleDevicesAdapter(Context context) {
		leDevices = new ArrayList<DeviceInfo>();
		inflater = LayoutInflater.from(context);
	}

	public void addDevice(DeviceInfo device, int rssi) {
		boolean flag = false;
		for(int i=0;i<leDevices.size();i++){
			if(leDevices.get(i).getMacAddress().equals(device.getMacAddress())){
				leDevices.get(i).setDeviceName(device.getDeviceName());
				leDevices.get(i).setGColor(device.getGColor());
				leDevices.get(i).setLightState(device.getLightState());
				flag = true;
				break;
			}
		}
		if(!flag){
			leDevices.add(device);
			if (leDevices.size() > 1) {
				Collections.sort(leDevices, new SortComparator());
			}
		}
		rssiMap.put(device.getMacAddress(), rssi);
	}

	public DeviceInfo getDevice(int position) {
		return leDevices.get(position);
	}

	public void clear() {
		leDevices.clear();
	}

	@Override
	public int getCount() {
		return leDevices.size();
	}

	@Override
	public Object getItem(int i) {
		return leDevices.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder;
		// General ListView optimization code.
		if (view == null) {
			view = inflater
					.inflate(R.layout.device_scan_list_item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.deviceIndex = (TextView)view.findViewById(R.id.device_index);
			viewHolder.deviceAddress = (TextView) view
					.findViewById(R.id.device_address);
			viewHolder.deviceName = (TextView) view
					.findViewById(R.id.device_name);
			viewHolder.deviceRssi = (TextView) view
					.findViewById(R.id.device_rssi);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		DeviceInfo lightInfo = leDevices.get(i);
		viewHolder.deviceIndex.setText(String.valueOf(i));
		final String deviceName = lightInfo.getDeviceName();
		if (deviceName != null && deviceName.length() > 0)
			viewHolder.deviceName.setText(deviceName+"("+lightInfo.getGColor()+","+
						(lightInfo.getLightState()?"开":"关")+")");
		else
			viewHolder.deviceName.setText(R.string.unknown_device);
		viewHolder.deviceAddress.setText(lightInfo.getMacAddress());
		viewHolder.deviceRssi.setText("" + rssiMap.get(lightInfo.getMacAddress()) + " dBm");

		return view;
	}

	private static class ViewHolder {
		TextView deviceIndex;
		TextView deviceName;
		TextView deviceAddress;
		TextView deviceRssi;
	}

	class SortComparator implements Comparator {
		@Override
		public int compare(Object obj1, Object obj2) {
			DeviceInfo device1 = (DeviceInfo) obj1;
			DeviceInfo device2 = (DeviceInfo) obj2;
			if(device1.getDeviceName()==null){
				return -1;
			}
			if(device2.getDeviceName()==null){
				return 1;
			}
			return (device2.getRssi() - device1.getRssi());
		}
	}
}
