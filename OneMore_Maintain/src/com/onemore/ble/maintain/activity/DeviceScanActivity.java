package com.onemore.ble.maintain.activity;

import android.app.Fragment;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onemore.ble.maintain.BleDevicesScanner;
import com.onemore.ble.maintain.BleManager;
import com.onemore.ble.maintain.R;
import com.onemore.ble.maintain.adapter.BleDevicesAdapter;
import com.onemore.ble.maintain.applications.MainApplication;
import com.onemore.ble.maintain.bean.DeviceInfo;
import com.onemore.ble.maintain.interfaces.IBleScanFinish;
import com.onemore.ble.maintain.services.BleServiceListener;
import com.onemore.ble.maintain.services.IWoodenLampBLL;
import com.onemore.ble.maintain.services.WoodenLampBLL;
import com.onemore.ble.maintain.util.BLEUtils;
import com.onemore.ble.maintain.util.ContentUtils;
import com.onemore.ble.maintain.util.L;
import com.onemore.ble.maintain.view.EnableBluetoothDialog;
import com.onemore.ble.maintain.view.ErrorDialog;
import com.onemore.ble.maintain.view.WaitProgressView;

public class DeviceScanActivity extends ListActivity implements
		ErrorDialog.ErrorDialogListener,
		EnableBluetoothDialog.EnableBluetoothDialogListener, IBleScanFinish,
		BleServiceListener {
	private boolean isFlag = false;
	private BluetoothAdapter bluetoothAdapter;
	private BleDevicesAdapter leDeviceListAdapter;
	private BleDevicesScanner scanner;
	private DeviceInfo lightInfo = null;
	private ImageButton titleReturn = null;
	private Button searchButton = null;
	private ProgressBar refreshProgressBar = null;
	private Handler uiThreadHandler = new Handler(Looper.getMainLooper());
	private IWoodenLampBLL woodenLampBLL = null;
	private WaitProgressView waitProgressView = null;
	
	public static byte[] hexStr2Bytes(String src)  
    {  
        int m=0,n=0;  
        int l=src.length()/2;  
        System.out.println(l);  
        byte[] ret = new byte[l];  
        for (int i = 0; i < l; i++)  
        {  
            m=i*2+1;  
            n=m+1;  
            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));  
        }  
        return ret;  
    }  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title_devices);
		setContentView(R.layout.device_scan_layout);
		initWoodenLampBLL();
		titleReturn = (ImageButton) findViewById(R.id.ib_title_return);
		titleReturn.setVisibility(View.GONE);
		searchButton = (Button) findViewById(R.id.ib_title_search);
		refreshProgressBar = (ProgressBar) findViewById(R.id.pb_title_refresh);
		TextView titleTextView = (TextView) findViewById(R.id.tv_title_name);
		titleTextView.setPadding(
				getResources().getDimensionPixelOffset(
						R.dimen.system_margin_left), 0, 0, 0);
		final View emptyView = findViewById(R.id.empty_view);
		getListView().setEmptyView(emptyView);
		final int bleStatus = BLEUtils.getBleStatus(getBaseContext());
		switch (bleStatus) {
		case BLEUtils.STATUS_BLE_NOT_AVAILABLE:
			ErrorDialog.newInstance(R.string.dialog_error_no_ble).show(
					getFragmentManager(), ErrorDialog.TAG);
			return;
		case BLEUtils.STATUS_BLUETOOTH_NOT_AVAILABLE:
			ErrorDialog.newInstance(R.string.dialog_error_no_bluetooth).show(
					getFragmentManager(), ErrorDialog.TAG);
			return;
		default:
			bluetoothAdapter = BLEUtils.getBluetoothAdapter(getBaseContext());
		}

		if (leDeviceListAdapter == null) {
			leDeviceListAdapter = new BleDevicesAdapter(getBaseContext());
			setListAdapter(leDeviceListAdapter);
		}
		if (bluetoothAdapter != null) {
			// initialize scanner
			scanner = new BleDevicesScanner(bluetoothAdapter,
					new BluetoothAdapter.LeScanCallback() {
						@Override
						public void onLeScan(final BluetoothDevice device,
								final int rssi, byte[] scanRecord) {
							//byte[] filter = hexStr2Bytes(device.getAddress().replace(":", ""));
					        //if(scanRecord.length >= filter.length){
					         //String name = new String(scanRecord);
					         //String filterStr = new String(filter);
					         if(device.getName()!=null){
									if ((device.getName()!=null  && (device.getName().contains("XY_T111") ||
											device.getName().contains("XiaoYi")||
											device.getName().contains("Gua Suo")||
											device.getName().contains("Drawer")))) {
										DeviceInfo lightInfo = new DeviceInfo(true);
										int temp = 0;
										byte manu1 = scanRecord[5];
										temp = manu1<<8;
										byte manu2 = scanRecord[6];
										byte power = scanRecord[14];
										byte state = scanRecord[15];
										if(device.getAddress().equals("D0:95:37:D8:9E:40")){
											L.d("sss");
										}
										lightInfo.setManufacturer(temp +manu2);
										lightInfo.setDeviceName(device.getName());
										lightInfo.setRssi(rssi);
										lightInfo.setLightState(state == 0);
										lightInfo.setGColor(power);										
										lightInfo.setMacAddress(device.getAddress());
										lightInfo.getBleManager().initialize(
												DeviceScanActivity.this);
										lightInfo.getBleManager().setManagerListener(
												woodenLampBLL);								
										MainApplication.getInstance().addLightInfo(
												lightInfo);
										leDeviceListAdapter.addDevice(lightInfo, rssi);
										leDeviceListAdapter.notifyDataSetChanged();
									}
					         }
						}
					});
			scanner.setBleScanFinish(this);
			scanner.setScanPeriod(BLEUtils.SCAN_PERIOD);
		}
	}

	private void initWoodenLampBLL() {
		woodenLampBLL = MainApplication.getInstance().getWoodenLampBLL();
	}

	@Override
	protected void onDestroy() {
		MainApplication.getInstance().closeAllDevice();
		System.exit(0);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isFlag) {
			if (woodenLampBLL != null) {
				woodenLampBLL.registeBleServiceListener(this);
				isFlag = true;
			}
		}
		if (ContentUtils.IS_DEBUG) {

		} else {
			if (bluetoothAdapter == null) {
			} else {
				if (!bluetoothAdapter.isEnabled()) {
					final Fragment f = getFragmentManager().findFragmentByTag(
							EnableBluetoothDialog.TAG);
					if (f == null)
						new EnableBluetoothDialog().show(getFragmentManager(),
								EnableBluetoothDialog.TAG);
					return;
				} else if (leDeviceListAdapter.getCount() == 0) {
					init();
				}else{
					if(woodenLampBLL.getShouldReScan()){
						woodenLampBLL.setShouldReScan(false);
						for(int i=0;i<leDeviceListAdapter.getCount();i++){
							if(((DeviceInfo)leDeviceListAdapter.getItem(i)).getMacAddress().equals(MainApplication.getInstance().getLightInfo().getMacAddress())){
								((DeviceInfo)leDeviceListAdapter.getItem(i)).setDeviceName(MainApplication.getInstance().getLightInfo().getDeviceName());
								leDeviceListAdapter.notifyDataSetChanged();
								break;
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (woodenLampBLL != null) {
			woodenLampBLL.unRegisteBleServiceListener(this);
			isFlag = false;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		lightInfo = leDeviceListAdapter.getDevice(position);
		if (scanner != null && scanner.isScanning()){
			scanner.stop();
			updateSearch();
		}
			
		if (lightInfo == null)
			return;
		if (woodenLampBLL != null) {
			MainApplication.getInstance().setCurrentAddress(lightInfo.getMacAddress());
			/*if(lightInfo.isGetInfo()){
				final Intent intent = new Intent(this,
						LauncherActivity.class);
				intent.putExtra(LauncherActivity.EXTRAS_DEVICE_NAME,
						lightInfo.getDeviceName());
				intent.putExtra(LauncherActivity.EXTRAS_DEVICE_ADDRESS,
						lightInfo.getMacAddress());
				startActivity(intent);
			}else{
				showWaitMessage();
				lightInfo.getBleManager().connect(DeviceScanActivity.this, lightInfo.getMacAddress());
			}*/
			showWaitMessage();
			lightInfo.getBleManager().connect(DeviceScanActivity.this, lightInfo.getMacAddress());
		}
			/*if (MainApplication.getInstance().getCurrentAddress() == null) {
				MainApplication.getInstance().setCurrentAddress(lightInfo.getMacAddress());
				showWaitMessage();
				MainApplication.getInstance().getLightInfo().getBleManager().connect(DeviceScanActivity.this, lightInfo.getMacAddress());
				//woodenLampBLL.getDeviceInfo();
			} else if (!MainApplication.getInstance().getCurrentAddress()
					.equals(lightInfo.getMacAddress())) {
				MainApplication.getInstance().setCurrentAddress(
						lightInfo.getMacAddress());
				showWaitMessage();
				MainApplication.getInstance().getLightInfo().getBleManager().connect(DeviceScanActivity.this, lightInfo.getMacAddress());				
			} else if (MainApplication.getInstance().getLightInfoByAddress(
					lightInfo.getMacAddress()) != null
					&& !MainApplication
							.getInstance()
							.getLightInfoByAddress(lightInfo.getMacAddress())
							.isGetInfo()) {
				woodenLampBLL.getDeviceInfo();
			} else {
				final Intent intent = new Intent(this,
						DeviceControlActivity.class);
				intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME,
						lightInfo.getDeviceName());
				intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS,
						lightInfo.getMacAddress());
				startActivity(intent);
			}
		}*/
			
	}

	@Override
	public void onEnableBluetooth(EnableBluetoothDialog f) {
		bluetoothAdapter.enable();
		if (scanner != null) {
			scanner.setFirstScan(true);
		}
		init();
	}

	@Override
	public void onCancel(EnableBluetoothDialog f) {
		finish();
	}

	@Override
	public void onDismiss(ErrorDialog f) {
		finish();
	}

	@Override
	public void onBleScanFinished() {
		if (scanner != null)
			scanner.stop();
		uiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				updateSearch();
			}
		});
	}

	@Override
	public void onConnected(){
		dismissWaitMessage();
		//woodenLampBLL.getDeviceInfo();
		//woodenLampBLL.getLockState();
		if(MainApplication.getInstance().getLightInfo() != null){
			MainApplication.getInstance().getLightInfo().setGetInfo(true);
		}
		final Intent intent = new Intent(this,
				LauncherActivity.class);
		intent.putExtra(LauncherActivity.EXTRAS_DEVICE_NAME,
				lightInfo.getDeviceName());
		intent.putExtra(LauncherActivity.EXTRAS_DEVICE_ADDRESS,
				lightInfo.getMacAddress());
		startActivity(intent);
	}
	
	@Override
	public void onDisconnected(){
		
	}
	@Override
	public void onDataResponse(DeviceInfo lightInfo) {
		
	}

	private void init() {
		if(leDeviceListAdapter != null){
			MainApplication.getInstance().clearAllInfo();
			leDeviceListAdapter.clear();
			leDeviceListAdapter.notifyDataSetChanged();
		}
		scanner.start();
		updateSearch();
	}

	public void titleMenuClick(View view) {
		switch (view.getId()) {
		case R.id.ib_title_search:
			if (ContentUtils.IS_DEBUG) {

			} else {
				init();
			}
			break;
		case R.id.pb_title_refresh:
			if (scanner != null && scanner.isScanning()){
				scanner.stop();
				updateSearch();
			}
			break;
		}
	}

	private void updateSearch() {
		if (scanner == null || !scanner.isScanning()) {
			searchButton.setVisibility(View.VISIBLE);
			refreshProgressBar.setVisibility(View.GONE);
		} else {
			searchButton.setVisibility(View.GONE);
			refreshProgressBar.setVisibility(View.VISIBLE);
		}
	}
	
	private void showWaitMessage(){
		waitProgressView = new WaitProgressView(this, 0);
		waitProgressView.setShowText(R.string.connect_device_wait,R.string.connect_device_error);
		waitProgressView.show(0, 5 * 1000);
	}
	private void dismissWaitMessage(){
		waitProgressView.hide();
	}

	@Override
	public void onDataResponseContent(byte[] content) {
		/*if(content.startsWith("\u0000")){
			MainApplication.getInstance().getLightInfo().setLightState(false);
		}else{
			MainApplication.getInstance().getLightInfo().setLightState(true);
		}*/
		/*
		final Intent intent = new Intent(this,
				LauncherActivity.class);
		intent.putExtra(LauncherActivity.EXTRAS_DEVICE_NAME,
				lightInfo.getDeviceName());
		intent.putExtra(LauncherActivity.EXTRAS_DEVICE_ADDRESS,
				lightInfo.getMacAddress());
		startActivity(intent);*/
	}
}
