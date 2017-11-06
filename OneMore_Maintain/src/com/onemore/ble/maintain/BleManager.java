package com.onemore.ble.maintain;

import java.security.cert.LDAPCertStoreParameters;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.UUID;

import com.onemore.ble.maintain.services.IWoodenLampBLL;
import com.onemore.ble.maintain.util.BLEUtils;
import com.onemore.ble.maintain.util.ContentUtils;
import com.onemore.ble.maintain.util.L;

import android.R.integer;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

public class BleManager implements BleExecutorListener {
	private final static String TAG = BleManager.class.getSimpleName();

	public static final int STATE_DISCONNECTED = 0;
	public static final int STATE_CONNECTING = 1;
	public static final int STATE_CONNECTED = 2;

	private final BleGattExecutor executor = BLEUtils.createExecutor(this);
	private BluetoothAdapter adapter;
	private BluetoothGatt gatt;
	private BluetoothGattCharacteristic gattCharacteristic = null;
	private BluetoothGattCharacteristic readGattCharacteristic = null;
	private String deviceAddress;
	private StringBuffer readStringBuffer = null;
	private boolean isReadEnd = false;
	private int connectionState = STATE_DISCONNECTED;
	private Context mContext;
	private String mAddress;
	private BleManagerListener managerListener;
	private String toReWriteContent = null;

	public int getState() {
		return connectionState;
	}

	public String getConnectedDeviceAddress() {
		return deviceAddress;
	}

	public void setManagerListener(BleManagerListener managerListener) {
		this.managerListener = managerListener;
	}

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize(Context context) {
		mContext = context;
		if (adapter == null) {
			adapter = BLEUtils.getBluetoothAdapter(context);
		}
		if (adapter == null || !adapter.isEnabled()) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}
		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	
	public boolean reConnect(){
		if(mContext!=null && mAddress!=null){
			return connect(mContext, mAddress);
		}else{
			return false;
		}
	}
	
	public boolean connect(Context context, String address) {
		mContext = context;
		mAddress = address;
		if (adapter == null || address == null) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device. Try to reconnect.
		/*if (deviceAddress != null && address.equals(deviceAddress)
				&& gatt != null) {
			Log.d(TAG,
					"Trying to use an existing BluetoothGatt for connection.");
			if (gatt.connect()) {
				connectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}*/

		final BluetoothDevice device = adapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.

		gatt = device.connectGatt(context, false, executor);
		Log.d(TAG, "Trying to create a new connection.");
		deviceAddress = address;
		connectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (adapter == null || gatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		gattCharacteristic = null;
		gatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (gatt == null) {
			return;
		}
		gatt.close();
		gatt = null;
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (gatt == null)
			return null;

		return gatt.getServices();
	}

	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status,
			int newState) {
		if (newState == BluetoothProfile.STATE_CONNECTED) {
			connectionState = STATE_CONNECTED;
			Log.i(TAG, "Connected to GATT server.");
			// Attempts to discover services after successful connection.
			if (gattCharacteristic == null) {
				Log.i(TAG,
						"Attempting to start service discovery:"
								+ gatt.discoverServices());

				if (managerListener != null)
					managerListener.onConnected();
			}
		} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
			connectionState = STATE_DISCONNECTED;
			Log.i(TAG, "Disconnected from GATT server.");
			gattCharacteristic = null;
			gatt.disconnect();
			gatt.close();
			gatt = null;
			//if (managerListener != null)
			//	managerListener.onDisconnected(getConnectedDeviceAddress());
		}
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (adapter == null || gatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		gatt.readCharacteristic(characteristic);
	}
	public void read(){
		if(gattCharacteristic != null){
			readCharacteristic(gattCharacteristic);
		}
	}
	public boolean writeString(byte[] contentBytes){
		boolean result = false;
		if (gattCharacteristic != null && gatt != null) {
			int length = 20;
			if (contentBytes.length > 20) {
				int i = 0;
				while (i < contentBytes.length) {
					byte[] d = new byte[length];
					for (int j = 0; j < length; j++) {
						d[j] = contentBytes[j + i];
					}
					i += 20;
					if (i + 20 < contentBytes.length) {
						length = 20;
					} else {
						length = contentBytes.length - i;
					}
					// L.d("write "+new String(d));
					// ����Characteristic��д��֪ͨ,�յ�����ģ������ݺ�ᴥ��mOnDataAvailable.onCharacteristicWrite()
					gatt.setCharacteristicNotification(gattCharacteristic, true);
					// ������������
					gattCharacteristic.setValue(d);
					// ������ģ��д������
					gatt.writeCharacteristic(gattCharacteristic);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				// ����Characteristic��д��֪ͨ,�յ�����ģ������ݺ�ᴥ��mOnDataAvailable.onCharacteristicWrite()
				//gatt.setCharacteristicNotification(gattCharacteristic, true);
				// ������������				
				//gattCharacteristic.setValue(content.getBytes());
				gattCharacteristic.setValue(contentBytes);
				// ������ģ��д������
				gatt.writeCharacteristic(gattCharacteristic);
				//read();
			}
			result = true;
		} else {
			result = false;
		}
		return result;
	}
	public boolean writeString(String content) {
		boolean result = false;
		L.d("gattCharacteristic=" + (gattCharacteristic == null));
		L.d("gatt=" + (gatt == null));
		if (gattCharacteristic == null && gatt == null) {
			connect(mContext, mAddress);
			toReWriteContent = content;
		} else if (gattCharacteristic == null && gatt != null) {
			gatt.discoverServices();
			toReWriteContent = content;
		} else {
			toReWriteContent = null;
		}
		if (gattCharacteristic != null && gatt != null) {
			byte[] data = content.getBytes();
			int length = 20;
			if (data.length > 20) {
				int i = 0;
				while (i < data.length) {
					byte[] d = new byte[length];
					for (int j = 0; j < length; j++) {
						d[j] = data[j + i];
					}
					i += 20;
					if (i + 20 < data.length) {
						length = 20;
					} else {
						length = data.length - i;
					}
					// L.d("write "+new String(d));
					// ����Characteristic��д��֪ͨ,�յ�����ģ������ݺ�ᴥ��mOnDataAvailable.onCharacteristicWrite()
					gatt.setCharacteristicNotification(gattCharacteristic, true);
					// ������������
					gattCharacteristic.setValue(d);
					// ������ģ��д������
					gatt.writeCharacteristic(gattCharacteristic);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				// ����Characteristic��д��֪ͨ,�յ�����ģ������ݺ�ᴥ��mOnDataAvailable.onCharacteristicWrite()
				gatt.setCharacteristicNotification(gattCharacteristic, true);
				// ������������				
				//gattCharacteristic.setValue(content.getBytes());
				gattCharacteristic.setValue(content);
				// ������ģ��д������
				gatt.writeCharacteristic(gattCharacteristic);
				read();
			}
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {

	}

	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		L.d("onServicesDiscovered:" + (gattCharacteristic == null)
				+ ",address=" + deviceAddress);
		if (status == BluetoothGatt.GATT_SUCCESS && gattCharacteristic == null) {
			for (BluetoothGattService gattService : gatt.getServices()) {
				if (gattService.getUuid().toString()
						.equals(BLEUtils.SERVICE_UUID_KEY)) {

					L.d("gattService.getUuid().toString()="
							+ gattService.getUuid().toString());
					List<BluetoothGattCharacteristic> gattCharacteristics = gattService
							.getCharacteristics();
					for (final BluetoothGattCharacteristic gc : gattCharacteristics) {
						if (gc.getUuid().toString()
								.equals(BLEUtils.UUID_KEY_DATA)) {
							gattCharacteristic = gc;
						}else if(gc.getUuid().toString()
								.equals(BLEUtils.UUID_KEY_DATA_READ)){
							gatt.setCharacteristicNotification(gc, true);
							UUID uuid = UUID.fromString(BLEUtils.UUID_KEY_NOTIFY);
							BluetoothGattDescriptor descriptor = gc.getDescriptor(uuid);
					        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
					        gatt.writeDescriptor(descriptor);				        
							 
							break;
						}
					}
					if (gattCharacteristic != null) {
						if (toReWriteContent != null) {
							writeString(toReWriteContent);
						}
						break;
					}
				}
			}
			if (managerListener != null)
				managerListener.onServiceDiscovered();
		} else {
			Log.w(TAG, "onServicesDiscovered received: " + status);
		}
	}

	@Override
	public void onCharacteristicRead(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic, int status) {
		if (status != BluetoothGatt.GATT_SUCCESS)
			return;

		broadcastUpdate(characteristic);
	}

	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic) {
		broadcastUpdate(characteristic);
	}

	private void broadcastUpdate(BluetoothGattCharacteristic characteristic) {
		final String serviceUuid = characteristic.getService().getUuid()
				.toString();
		final String characteristicUuid = characteristic.getUuid().toString();
		final byte[] data;
		
		data = characteristic.getValue();
		
		isReadEnd = true;
		if (isReadEnd) {
			if (managerListener != null) {				
				managerListener.onDataAvailable(deviceAddress,
						null, data);
			}
		}
	}
}
