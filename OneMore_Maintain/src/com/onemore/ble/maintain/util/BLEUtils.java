package com.onemore.ble.maintain.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.onemore.ble.maintain.BleExecutorListener;
import com.onemore.ble.maintain.BleGattExecutor;

/**
 * @author wuzhiyi
 * @date 2015-6-25 ����7:52:51
 */
public class BLEUtils {
	//public final static String SERVICE_UUID_KEY = "0000fff0-0000-1000-8000-00805f9b34fb";
	//public final static String UUID_KEY_DATA = "0000fff1-0000-1000-8000-00805f9b34fb";
	public final static String SERVICE_UUID_KEY = "0000fee7-0000-1000-8000-00805f9b34fb";
	public final static String UUID_KEY_DATA = "000036f5-0000-1000-8000-00805f9b34fb";
	//public final static String SERVICE_UUID_KEY = "ffff1523-ff01-64ff-0000-000000000101";
	//public final static String UUID_KEY_DATA = "ffff1525-ff01-64ff-0000-000000000101";
	
	public final static String UUID_KEY_DATA_READ = "000036f6-0000-1000-8000-00805f9b34fb";
	public final static String UUID_KEY_NOTIFY = "00002902-0000-1000-8000-00805f9b34fb";
	public final static String UUID_KEY_DFU = "f000ffc0-0451-4000-b000-000000000000";
	public final static String UUID_KEY_CC = "f000ccc0-0451-4000-b000-000000000000";
	public static final long SCAN_PERIOD = 10*1000;
	public static final int TIMEOUT = 15*1000;

    public static final int STATUS_BLE_ENABLED = 0;
    public static final int STATUS_BLUETOOTH_NOT_AVAILABLE = 1;
    public static final int STATUS_BLE_NOT_AVAILABLE = 2;
    public static final int STATUS_BLUETOOTH_DISABLED = 3;

	public static final int WEEK_SUNDAY = 1;
	public static final int WEEK_MONDAY = 2;
	public static final int WEEK_TUESDAY = 4;
	public static final int WEEK_WEDNESDAY = 8;
	public static final int WEEK_THURSDAY = 16;
	public static final int WEEK_FRIDAY = 32;
	public static final int WEEK_SATURDAY = 64;
	public static final int WEEK_WORKDAY = WEEK_MONDAY+WEEK_TUESDAY+WEEK_WEDNESDAY+WEEK_THURSDAY+WEEK_FRIDAY;
	public static final int WEEK_WORKEND = WEEK_SATURDAY + WEEK_SUNDAY;
	public static final int WEEK_ALL = WEEK_WORKDAY+WEEK_WORKEND;
	public static final int[] WEEK_VALUES = new int[]{WEEK_SUNDAY,WEEK_MONDAY,WEEK_TUESDAY,WEEK_WEDNESDAY,WEEK_THURSDAY,WEEK_FRIDAY,WEEK_SATURDAY};
	public static final String REGULAREX = "#";
	public static final String WEEK_REGULAREX = "��";
	public static final String TIMING_FUNCTION_REGULAREX = ",";
    
    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null)
            return null;
        return bluetoothManager.getAdapter();
    }

    public static int getBleStatus(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return STATUS_BLE_NOT_AVAILABLE;
        }

        final BluetoothAdapter adapter = getBluetoothAdapter(context);
        // Checks if Bluetooth is supported on the device.
        if (adapter == null) {
            return STATUS_BLUETOOTH_NOT_AVAILABLE;
        }

        if (adapter.isEnabled())
            return STATUS_BLUETOOTH_DISABLED;

        return STATUS_BLE_ENABLED;
    }
    
    public static BleGattExecutor createExecutor(final BleExecutorListener listener) {
        return new BleGattExecutor() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                listener.onConnectionStateChange(gatt, status, newState);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                listener.onServicesDiscovered(gatt, status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                listener.onCharacteristicRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt,
                                                BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                listener.onCharacteristicChanged(gatt, characteristic);
            }
        };
    }
    
    public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.smargic.yomu.bt100", 0).versionCode;
		} catch (NameNotFoundException e) {
			L.e(e.getMessage());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = null;
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.smargic.yomu.bt100", 0).versionName;
		} catch (NameNotFoundException e) {
			L.e(e.getMessage());
		}
		return verName;
	}
}
