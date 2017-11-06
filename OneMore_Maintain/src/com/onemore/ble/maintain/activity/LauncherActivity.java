/**
 * 
 */
package com.onemore.ble.maintain.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onemore.ble.maintain.R;
import com.onemore.ble.maintain.applications.MainApplication;
import com.onemore.ble.maintain.bean.DeviceInfo;
import com.onemore.ble.maintain.services.BleServiceListener;
import com.onemore.ble.maintain.services.IWoodenLampBLL;
import com.onemore.ble.maintain.view.PopupDialogView;
import com.onemore.ble.maintain.view.PopupDialogView.OnPopupDialogButtonClick;

/**
 * @author wuzhiyi
 * @date 2015-7-18 ÉÏÎç9:57:49
 */
public class LauncherActivity extends Activity implements BleServiceListener{
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	//private ImageButton lockImageButton = null;
	//private ImageButton unlockImageButton = null;
	private LinearLayout changeAPNLinearLayout;
	private TextView tokenTextView,chipTypeTextView,deviceVersionTextView,deviceTypeTextView;
	private TextView lockStateTextView,batteryTextView;
	private TextView deviceNameTextView;
	private TextView alarmStateTextView;
	private TextView openCountTextView;
	private EditText passwordEditText;
	private EditText newPasswordEditText;
	private EditText aesKeyEditText;
	private EditText newAesKeyEditText;
	private EditText apnEditText;
	private EditText serverEditText;
	private EditText portEditText;
	private boolean alarmState = false;
	private DeviceInfo lightInfo = null;
	private String deviceName;
	private String deviceAddress;
	private int openCount = 0;
	private int currentID = 0;
	private IWoodenLampBLL woodenLampBLL = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = getIntent();
		deviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		setContentView(R.layout.launcher_layout);
		TextView titleTextView = (TextView) findViewById(R.id.tv_title_name);
		titleTextView.setText(deviceAddress);
		woodenLampBLL = MainApplication.getInstance().getWoodenLampBLL();
		Button addTimerFunction = (Button) findViewById(R.id.ib_title_search);
		addTimerFunction.setVisibility(View.GONE);
		/*lockImageButton = (ImageButton)findViewById(R.id.ib_lock_state);
		lockImageButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(!MainApplication.getInstance().getLightInfo().getLightState()){
					woodenLampBLL.openLock();
				}
			}
		});
		unlockImageButton = (ImageButton)findViewById(R.id.ib_unlock_state);*/
		if (woodenLampBLL != null) {
			woodenLampBLL.registeBleServiceListener(this);
		}
		changeAPNLinearLayout = (LinearLayout)findViewById(R.id.ll_change_apn);
		tokenTextView = (TextView)findViewById(R.id.tv_device_token);
		chipTypeTextView = (TextView)findViewById(R.id.tv_chip_type);
		deviceVersionTextView = (TextView)findViewById(R.id.tv_device_version);
		deviceTypeTextView = (TextView)findViewById(R.id.tv_device_type);
		
		lockStateTextView = (TextView)findViewById(R.id.tv_lock_state);
		batteryTextView = (TextView)findViewById(R.id.tv_battery);
		openCountTextView = (TextView)findViewById(R.id.tv_open_count);
		openCountTextView.setText(String.valueOf(openCount));
		deviceNameTextView = (TextView)findViewById(R.id.tv_device_name);
		deviceNameTextView.setText(deviceName);
		
		passwordEditText = (EditText)findViewById(R.id.edt_open_password);
		newPasswordEditText = (EditText)findViewById(R.id.edt_new_open_password);
		aesKeyEditText = (EditText)findViewById(R.id.edt_aes_key);
		newAesKeyEditText = (EditText)findViewById(R.id.edt_new_aes_key);
		apnEditText = (EditText)findViewById(R.id.edt_apn);
		serverEditText = (EditText)findViewById(R.id.edt_server);
		portEditText = (EditText)findViewById(R.id.edt_port);
		if(deviceName.contains("105")){
			changeAPNLinearLayout.setVisibility(View.VISIBLE);
		}else{
			changeAPNLinearLayout.setVisibility(View.GONE);
		}
	}
	
	public void onResume(){
		super.onResume();
		if (MainApplication.getInstance().getLightInfo() != null) {
			//woodenLampBLL.read();
			lightInfo = MainApplication.getInstance().getLightInfo();			
		}
	}
	
	public void onDestroy(){
		super.onDestroy();
		if(woodenLampBLL != null){
			woodenLampBLL.sendBack();
		}
		MainApplication.getInstance().closeAllDevice();
		
	}
	private OnPopupDialogButtonClick renameButtonClick = new OnPopupDialogButtonClick() {
		@Override
		public void onButtonClick(Object arg1, Object arg2) {
			switch(currentID){
			case R.id.btn_change_password:	
				String oldPassword = passwordEditText.getText().toString();
				String newPassword = newPasswordEditText.getText().toString();
				if(oldPassword.length() >0 && newPassword.length() >0){
					woodenLampBLL.changePassword(oldPassword,newPassword);
				}
				break;
				case R.id.btn_change_aes_key:
					String newKeyString = newAesKeyEditText.getText().toString().trim();
					if(newKeyString.length() >0){
						woodenLampBLL.changeAesKey(null,newKeyString);
					}
					break;
				case R.id.btn_recover:
					woodenLampBLL.recover();
					break;
				case R.id.btn_dfu:
					woodenLampBLL.dfu();
					break;
				case R.id.btn_change_apn:
					String apnString = apnEditText.getText().toString().trim();
					if(apnString.length() > 0){
						woodenLampBLL.changeAPN(apnString);
					}
					break;
				case R.id.btn_change_server:
					String serverString = serverEditText.getText().toString().trim();
					if(serverString.length() > 0){
						woodenLampBLL.changeServer(serverString);
					}
					break;
				case R.id.btn_change_port:
					String portString = portEditText.getText().toString().trim();			
					if(portString.length() >0){
						int port = Integer.valueOf(portString);
						if(port < 65536){
							woodenLampBLL.changePort(Integer.valueOf(portString));
						}
					}
					break;
				case R.id.btn_transportation_mode:
					woodenLampBLL.transportationMode();
					break;
				case R.id.btn_test_acce:
					woodenLampBLL.testACCE();
					break;
				case R.id.btn_test_gprs:
					woodenLampBLL.testGPRS();
					break;
				}
			}
	};
	public void titleMenuClick(View v) {
		switch (v.getId()) {
		case R.id.ib_title_return:
			this.finish();
			break;
		case R.id.btn_set_time:			
			woodenLampBLL.setDeviceTime();
			break;
		case R.id.btn_get_battery:			
			woodenLampBLL.getDeviceBattery();
			break;
		case R.id.btn_get_token:
			woodenLampBLL.getToken();			
			break;
		case R.id.btn_open_lock:{		
			String openPassword = passwordEditText.getText().toString().trim();
			woodenLampBLL.openLock(openPassword);
		}
			break;
		case R.id.btn_close_lock:
			woodenLampBLL.closeLock();
			break;
		case R.id.btn_find_device:
			woodenLampBLL.findDevice();
			break;
		case R.id.btn_change_password:
		case R.id.btn_change_aes_key:
		case R.id.btn_recover:
		case R.id.btn_dfu:
		case R.id.btn_change_apn:
		case R.id.btn_change_server:
		case R.id.btn_change_port:
		case R.id.btn_transportation_mode:
		case R.id.btn_test_acce:
		case R.id.btn_test_gprs:
			currentID = v.getId();
			PopupDialogView editDeviceName = new PopupDialogView(LauncherActivity.this);
			editDeviceName.setDialogType(PopupDialogView.POPUPWINDOW_TEXT);
			editDeviceName.setTitleText("提示信息");
			editDeviceName.setRightButtonClick(renameButtonClick);
			editDeviceName.setMessageContent("确定要执行此操作吗?");
			editDeviceName.show();
			break;
		case R.id.btn_back:
				woodenLampBLL.sendBack();
			break;
		}
	}
	
	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onDataResponse(DeviceInfo lightInfo) {
				
	}

	@Override
	public void onDataResponseContent(byte[] content) {
		byte[] bytes = content;
		if(bytes.length>0){	
			if(bytes[0]== 0x01){
				
			}else if(bytes[0]== 0x02){
				if(bytes[1] == 0x02 && bytes[2] == 0x01){
					if(bytes[3] != 0xFF){						
						((TextView)findViewById(R.id.tv_battery)).setText(bytes[3]+"%");
					}else{
						((TextView)findViewById(R.id.tv_battery)).setText("error");
					}
				}
			}else if(bytes[0]== 0x03){
				if(bytes[1]== 0x02){
					if(bytes[3] == 0x0){
						Toast.makeText(LauncherActivity.this, "发送升级指令成功", Toast.LENGTH_SHORT).show();
						MainApplication.getInstance().closeAllDevice();
						LauncherActivity.this.finish();
					}else{
						
					}
				}else if(bytes[1] == 0x04){
					if(bytes[3] == 0x0){
						Toast.makeText(LauncherActivity.this, "发送查找指令成功", Toast.LENGTH_SHORT).show();
					}
				}else if(bytes[1] == 0x06){
					if(bytes[3] == 0x0){
						Toast.makeText(LauncherActivity.this, "发送清除指令成功", Toast.LENGTH_SHORT).show();
					}
				}
			}else if(bytes[0]== 0x04){
				if(bytes[1] == 0x02 && bytes[2] == 0x01){
					if(bytes[3] == 0x0){						
						deviceNameTextView.setText("XiaoYi");
					}else{
						deviceNameTextView.setText("error");
					}
				}
			}else if(bytes[0] == 0x05){
				if(bytes[1] == 0x02){
					if(bytes[3] == 0){
						lockStateTextView.setText("开启");
						MainApplication.getInstance().getLightInfo().setLightState(true);
						openCount+=1;
						openCountTextView.setText(String.valueOf(openCount));
					}else{
						Toast.makeText(LauncherActivity.this, "开锁失败", Toast.LENGTH_SHORT).show();
					}
				}else if(bytes[1] == 0x05){
					if(bytes[3] == 0){
						Toast.makeText(LauncherActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
						passwordEditText.setText(newPasswordEditText.getText().toString());
					}else{
						Toast.makeText(LauncherActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
					}
				}else if(bytes[1] == 0x07){
					if(bytes[3] == 0){
						alarmState = !alarmState;
						alarmStateTextView.setText(alarmState?"开启":"关闭");						
					}
				}else if(bytes[1] == 0x08){
					if(bytes[3] == 0){
						lockStateTextView.setText("关闭");
						MainApplication.getInstance().getLightInfo().setLightState(false);
					}
				}else if(bytes[1] == 0x0d){
					if(bytes[2] == 1 && bytes[3] == 0){
						lockStateTextView.setText("关闭");
						MainApplication.getInstance().getLightInfo().setLightState(false);
					}
				}else if(bytes[1] == 0x0f){
					if(bytes[3] == 0){
						lockStateTextView.setText("开启");
						MainApplication.getInstance().getLightInfo().setLightState(true);
					}else{
						lockStateTextView.setText("关闭");
						MainApplication.getInstance().getLightInfo().setLightState(false);
					}
				}else if(bytes[1] == 0x17){
					if(bytes[3] == 0){
						Toast.makeText(LauncherActivity.this, "发送还车指令成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(LauncherActivity.this, "发送还车指令失败", Toast.LENGTH_SHORT).show();
					}
				}
			}else if(bytes[0] == 0x06){
				displayDeviceToken(bytes);
			}else if(bytes[0] == 0x07){
				if(bytes[1] == 0x03){
					if(bytes[3] == 0x00){
						//String aesKeyString = newAesKeyEditText.getText().toString().trim();
						//woodenLampBLL.setKey(aesKeyString);
						//aesKeyEditText.setText(aesKeyString);
						Toast.makeText(LauncherActivity.this, "修改密钥成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(LauncherActivity.this, "修改密钥失败", Toast.LENGTH_SHORT).show();
					}
				}
			}else if(bytes[0] == 0x08){
				if(bytes[1] == 0x02){
					if(bytes[3] == 0x00){
						Toast.makeText(LauncherActivity.this, "修改APN成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(LauncherActivity.this, "修改APN失败", Toast.LENGTH_SHORT).show();
					}
				}else if(bytes[1] == 0x04){
					if(bytes[3] == 0x00){
						Toast.makeText(LauncherActivity.this, "修改服务器地址成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(LauncherActivity.this, "修改服务器地址失败", Toast.LENGTH_SHORT).show();
					}
				}else if(bytes[1] == 0x06){
					if(bytes[3] == 0x00){
						Toast.makeText(LauncherActivity.this, "修改端口成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(LauncherActivity.this, "修改端口失败", Toast.LENGTH_SHORT).show();
					}
				}else if(bytes[1] == 0x08){
					if(bytes[3] == 0x00){
						Toast.makeText(LauncherActivity.this, "设置运输模式成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(LauncherActivity.this, "设置运输模式失败", Toast.LENGTH_SHORT).show();
					}
				}else if(bytes[1] == 0x0a){
					if(bytes[3] == 0x00){
						Toast.makeText(LauncherActivity.this, "测试ACCE返回正常", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(LauncherActivity.this, "测试ACCE返回错误", Toast.LENGTH_SHORT).show();
					}
				}else if(bytes[1] == 0x0c){
					byte result = bytes[3];
					String resultString = String.format("模块:[%s] SIM卡:[%s] GPRS:[%s] " +
							"TCP连接:[%s] MQTT连接:[%s] GPRS模块:[%s] GPS定位:[%s]", 
							(((result & 0x01) == 0x01)?"正常":"错误"),
							(((result & 0x02) == 0x02)?"正常":"错误"),
							(((result & 0x04) == 0x04)?"正常":"错误"),
							(((result & 0x08) == 0x08)?"正常":"错误"),
							(((result & 0x10) == 0x10)?"正常":"错误"),
							(((result & 0x20) == 0x20)?"正常":"错误"),
							(((result & 0x40) == 0x40)?"正常":"错误"),
							(((result & 0x80) == 0x80)?"正常":"错误"));
					new AlertDialog.Builder(LauncherActivity.this)
					 .setTitle("测试GPRS") 
					 .setMessage(resultString)
					  	.setPositiveButton("确定", null)
					  	.show();
				}
			}
		}
	}
	
	private void displayDeviceToken(byte[] deviceToken){
		if(deviceToken.length >=16){
			switch(deviceToken[1]){
			case 0x02:{
				woodenLampBLL.getLockState();
				tokenTextView.setText(String.format("0x%02x%02x%02x%02x",deviceToken[3],deviceToken[4],deviceToken[5],deviceToken[6]));
				chipTypeTextView.setText((deviceToken[7]==0x02)?"Nordic":"未知");
				deviceVersionTextView.setText(deviceToken[8]+"."+deviceToken[9]);
				deviceTypeTextView.setText((deviceToken[10]==0x01)?"蓝牙单机版":"GPS通信版");
			}
				break;
			}
		}
	}
}
