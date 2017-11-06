package com.onemore.ble.maintain.services;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.onemore.ble.maintain.BleManager;
import com.onemore.ble.maintain.applications.MainApplication;
import com.onemore.ble.maintain.bean.DeviceInfo;
import com.onemore.ble.maintain.util.AESUtils;
import com.onemore.ble.maintain.util.ContentUtils;
import com.onemore.ble.maintain.util.L;

public class WoodenLampBLL implements IWoodenLampBLL {
	// ��ʼ��ҵ���߼�������
	
	public volatile static IWoodenLampBLL woodenLampBLL;
	private Context mContext = null;
	private HashMap<Integer, MessageEntity> sendCmdHashMap = new HashMap<Integer, MessageEntity>();
	private int keyType = 0;
	private boolean flag = false;
	//private static final byte[] key = {(byte)0x87,0x16,0x7D,(byte)0xAF,(byte)0xBF,(byte)0xC4,0x14,(byte)0xEA,0x3C,0x2F,(byte)0x9B,0x24,(byte)0x8A,0x63,0x28,0x54};
	//private static final byte[] key = {58,96,67,42,92,01,33,31,41,30,15,78,12,19,40,37};
	//private static final byte[] key = {59,97,68,43,93,02,34,32,42,31,16,79,13,20,41,38};
	private static final byte[] key_lixiang = {32,87,47,82,54,75,63,71,48,80,65,88,17,99,45,43};//立享
	private static final byte[] key_xiaoyi = {(byte)0x87, 0x16, 0x7D, (byte)0xAF, (byte)0xBF, (byte)0xC4, 0x14, (byte)0xEA, 0x3C, 0x2F, (byte)0x9B, 0x24, (byte)0x8A, 0x63, 0x28, 0x54};
	private static final byte[] key_oxo = {58,96,67,42,92,01,33,31,41,30,15,78,12,19,40,37};
	private static final byte[] key_T111 = {59,97,68,43,93,02,34,32,42,31,16,79,13,20,41,38};

	private byte[] token = {0x0,0x0,0x0,0x0};
	public static IWoodenLampBLL getInstance() {
		if (null == woodenLampBLL) {
			synchronized (IWoodenLampBLL.class) {
				if (null == woodenLampBLL) {
					woodenLampBLL = new WoodenLampBLL();
				}
			}
		}
		return woodenLampBLL;
	}

	private final static Handler uiThreadHandler = new Handler(
			Looper.getMainLooper());
	private Boolean shouldReScan = false;
	private CopyOnWriteArrayList<BleServiceListener> bleServiceListeners = new CopyOnWriteArrayList<BleServiceListener>();

	public void registeBleServiceListener(BleServiceListener listener) {
		bleServiceListeners.add(listener);
	}

	public void unRegisteBleServiceListener(BleServiceListener listener) {
		bleServiceListeners.remove(listener);
	}

	@Override
	public void onConnected() {
		uiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				for (Iterator<BleServiceListener> it2 = bleServiceListeners
						.iterator(); it2.hasNext();) {
					BleServiceListener serviceListener = (BleServiceListener) it2
							.next();
					serviceListener.onConnected();
				}
			}
		});
	}

	@Override
	public void onDisconnected(final String serviceAddress) {
		uiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				DeviceInfo lightInfo = MainApplication.getInstance()
						.getLightInfoByAddress(serviceAddress);
				int count = 0;
				while (count++ < 4) {
					if (lightInfo.getBleManager().connect(mContext,
							lightInfo.getMacAddress())) {
						break;
					}
				}
				if (count >= 4) {
					for (Iterator<BleServiceListener> it2 = bleServiceListeners
							.iterator(); it2.hasNext();) {
						BleServiceListener serviceListener = (BleServiceListener) it2
								.next();
						serviceListener.onConnected();
					}
				}
			}
		});
	}

	@Override
	public void onServiceDiscovered() {
		uiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	@Override
	public void onDataAvailable(String deviceAddress, final String text,
			final byte[] data) {
		handleResponse(deviceAddress, data);
	}

	private void handleResponse(String deviceAddress, final byte[] response) {
		uiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				//byte[] key = {58,96,67,42,92,01,33,31,41,30,15,78,12,19,40,37};
				try {
					if(response.length >=16){
						byte[] temp = new byte[16];
						for(int i=0;i<16;i++){
							temp[i]= response[i]; 
						}
						/*for(int i=0;i<16;i++){
						L.d(String.format("0x%02X",temp[i]));
						}*/
						byte[] encryptingCode = AESUtils.decrypt(getKey(),temp);
						/*for(int i=0;i<16;i++){
						L.d(String.format("0x%02X",encryptingCode[i]));
						}*/
						if(encryptingCode[0] == 0x06 && encryptingCode.length >=16){
							token[0] = encryptingCode[3];
							token[1] = encryptingCode[4];
							token[2] = encryptingCode[5];
							token[3] = encryptingCode[6];
						}
						for (Iterator<BleServiceListener> it2 = bleServiceListeners
								.iterator(); it2.hasNext();) {
							BleServiceListener serviceListener = (BleServiceListener) it2
									.next();
							serviceListener.onDataResponseContent(encryptingCode);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
					
			}
		});
	}

	private BleManager getCurrentBleManage() {
		BleManager bleManager = null;
		if (MainApplication.getInstance().getLightInfo() != null) {
			bleManager = MainApplication.getInstance().getLightInfo()
					.getBleManager();
			
		}
		return bleManager;
	}

	public void read(){		
		getCurrentBleManage().read();
	}
	private void sendMessageToDevice(final int command,
			final DeviceInfo sendLightInfo) {
		if (getCurrentBleManage() != null) {
				IMessageResponseCallBack messageResponseCallBack = new IMessageResponseCallBack() {
					@Override
					public void onDataResponse(DeviceInfo responseLightInfo) {
						DeviceInfo currentLightInfo = MainApplication
								.getInstance().getLightInfoByAddress(
										responseLightInfo.getMacAddress());

						switch (responseLightInfo.getResponseCode()) {
						case ContentUtils.COMMAND_GET_DEVICE_INFO:
							MainApplication.getInstance().updateLightInfo(
									responseLightInfo);
							break;
						case ContentUtils.COMMAND_SET_SWITCH_STATE:
							if (responseLightInfo.isSuccess()) {
								boolean lightState = !currentLightInfo
										.getLightState();
								currentLightInfo.setLightState(lightState);
								responseLightInfo.setLightState(lightState);
							}
							break;
						case ContentUtils.COMMAND_SET_LIGHT_BRIGHTNESS:
							if (responseLightInfo.isSuccess()) {
								currentLightInfo
										.setLightBrightness(sendLightInfo
												.getLightBrightness());
							}
							break;
						case ContentUtils.COMMAND_SET_COLOR_TEMPERATURE:
							if (responseLightInfo.isSuccess()) {
								int colorTemp = ContentUtils.LIGHT_STATE_OPEN
										- currentLightInfo
												.getColorTemperature();
								currentLightInfo.setColorTemperature(colorTemp);
								responseLightInfo
										.setColorTemperature(colorTemp);
							} else {
								responseLightInfo
										.setColorTemperature(currentLightInfo
												.getColorTemperature());
							}
							break;
						case ContentUtils.COMMAND_SET_DEVICE_NAME:
							if (responseLightInfo.isSuccess()) {
								currentLightInfo.setDeviceName(sendLightInfo
										.getDeviceName());
								responseLightInfo.setDeviceName(sendLightInfo
										.getDeviceName());
								currentLightInfo.getBleManager().disconnect();
								currentLightInfo.getBleManager().close();
								currentLightInfo.getBleManager().connect(mContext, currentLightInfo.getMacAddress());
								currentLightInfo.setGetInfo(false);
								setShouldReScan(true);
							}
							break;
						case ContentUtils.COMMAND_LIGHT_INFO_CHANGE:
							if (responseLightInfo.isSuccess()) {
								currentLightInfo
										.setLightState(responseLightInfo
												.getLightState());
								currentLightInfo
										.setLightBrightness(responseLightInfo
												.getLightBrightness());
								currentLightInfo
										.setColorTemperature(responseLightInfo
												.getColorTemperature());
							}
							break;
						}
					}
				};
				MessageEntity messageEntity = new MessageEntity();
				messageEntity.messageId = command;
				messageEntity.receiveHandler = messageResponseCallBack;
				messageEntity.sendTime = System.currentTimeMillis();
				sendCmdHashMap.put(command, messageEntity);		
				getCurrentBleManage().writeString(
						ContentUtils.encodeSendContent(command, sendLightInfo));
		}
	}

	// �����豸ʱ��
	public void setDeviceTime() {
		long currentTime = System.currentTimeMillis()/1000;
		ByteBuffer buffer = ByteBuffer.allocate(8);   
		buffer.putLong(currentTime);
		int length = buffer.capacity();
		for(int i=0;i<buffer.capacity();i++){
			if(buffer.get(i)>0){				
				break;
			}
			length-=1;
		}
		ByteBuffer sendBuffer = ByteBuffer.allocate(16);
		sendBuffer.put((byte)0x01);
		sendBuffer.put((byte)0x01);
		sendBuffer.put((byte)length);
		for(int j=0;j<length;j++){
			sendBuffer.put(buffer.get(buffer.capacity()-length+j));
		}
		for(int j=0;j<4;j++){
			sendBuffer.put(token[j]);
		}
		byte[] sendContent = sendBuffer.array();
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),sendContent);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//������
	public void getDeviceBattery(){
		byte[] openCommand={0x02,0x01,0x01,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

		for(int j=0;j<4;j++){
			openCommand[4+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// �����豸������
	public void changeDeviceName(String deviceName) {
		ByteBuffer sendBuffer = ByteBuffer.allocate(16);
		sendBuffer.put((byte)0x04);
		sendBuffer.put((byte)0x01);
		byte[] name = deviceName.getBytes();
		int length = name.length;
		sendBuffer.put((byte)length);
		for(int j=0;j<length;j++){
			sendBuffer.put(name[j]);
		}
		for(int j=0;j<4;j++){
			sendBuffer.put(token[j]);
		}
		try {
			byte[] sendContent = sendBuffer.array();
			byte[] encryptingCode = AESUtils.encrypt(getKey(),sendContent);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//��ȡToken
	public void getToken(){
		byte[] openCommand={0x06,0x01,0x01,0x01,0x2C,0x2C,0x62,0x58,0x26,0x67,0x42,0x66,0x01,0x33,0x31,0x41};

		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			/*for(int i=0;i<16;i++){
				L.d(String.format("0x%02X",encryptingCode[i]));
			}*/
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//����
	public void openLock(String openPassword){
		byte[] passwordBytes = openPassword.getBytes();
		byte[] openCommand={0x05,0x01,0x06,0x31,0x32,0x33,0x34,0x35,0x36,0x00,0x00,0x00,0x00,0x5E,0x26,0x36};

		for(int i=0;i<passwordBytes.length;i++){
			openCommand[3+i] = passwordBytes[i];
		}
		for(int j=0;j<4;j++){
			openCommand[9+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);

			/*L.d(String.format("0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X " +
					"0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X 0x%02X ",encryptingCode[0],encryptingCode[1],
					encryptingCode[2],encryptingCode[3],encryptingCode[4],encryptingCode[5],encryptingCode[6],encryptingCode[7],
					encryptingCode[8],encryptingCode[9],encryptingCode[10],encryptingCode[11],
					encryptingCode[12],encryptingCode[13],encryptingCode[14],encryptingCode[15]));*/
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//����
	public void closeLock(){
		byte[] openCommand={0x05,0x0C,0x01,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

		for(int j=0;j<4;j++){
			openCommand[4+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//�豸��������
	public void switchAlarmState(boolean alarmState){
		byte[] openCommand={0x05,0x06,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
		if(alarmState){
			openCommand[3] = (byte)0x01;
		}
		for(int j=0;j<4;j++){
			openCommand[4+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//��������
	public void changePassword(String oldPassword,String newPassword){
		byte[] oldBytes = oldPassword.getBytes();
		byte[] newBytes = newPassword.getBytes();
		byte[] oldPasswordContent={0x05,0x03,0x06,0x30,0x30,0x30,0x30,0x30,0x30,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
		byte[] newPasswordContent={0x05,0x04,0x06,0x31,0x32,0x33,0x34,0x35,0x36,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

		for(int i=0;i<oldBytes.length;i++){
			oldPasswordContent[3+i] = oldBytes[i];
		}
		for(int i=0;i<newBytes.length;i++){
			newPasswordContent[3+i] = newBytes[i];
		}
		for(int j=0;j<4;j++){
			oldPasswordContent[9+j] = token[j];
		}
		for(int j=0;j<4;j++){
			newPasswordContent[9+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),oldPasswordContent);
			getCurrentBleManage().writeString(encryptingCode);
			Thread.sleep(200);
			byte[] newCode = AESUtils.encrypt(getKey(),newPasswordContent);
			getCurrentBleManage().writeString(newCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
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
	//������Կ
	public void changeAesKey(String aesKey,String newKey){
		//byte[] aseKey = hexStr2Bytes(aesKey);
		byte[] newKeyBytes = {58,96,67,42,92,01,33,31,41,30,15,78,12,19,40,37};//hexStr2Bytes(newKey);
		//setKey(aesKey);
		byte[] key1={0x07,0x01,0x08,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x00,0x00,0x00,0x00,0x00};
		byte[] key2={0x07,0x02,0x08,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x00,0x00,0x00,0x00,0x00};
		
		for(int i=0;i<8;i++){
			key1[3+i] = newKeyBytes[i];
		}for(int i=0;i<8;i++){
			key2[3+i] = newKeyBytes[8+i];
		}
		
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),key1);
			getCurrentBleManage().writeString(encryptingCode);
			Thread.sleep(200);
			byte[] newCode = AESUtils.encrypt(getKey(),key2);
			getCurrentBleManage().writeString(newCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setKey(String newKey){
		byte[] newKeyBytes = hexStr2Bytes(newKey);
		for(int i=0;i<16;i++){
			//key[i] = newKeyBytes[i];
		}
	}
	

	public void dfu(){
		byte[] openCommand={0x03,0x01,0x01,0x01,0x32,0x33,0x34,0x35,0x36,0x25,0x26,0x27,0x00,0x5E,0x26,0x36};

		for(int j=0;j<4;j++){
			openCommand[4+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void findDevice(){
		byte[] openCommand={0x03,0x03,0x01,0x01,0x32,0x33,0x34,0x35,0x36,0x25,0x26,0x27,0x00,0x5E,0x26,0x36};

		for(int j=0;j<4;j++){
			openCommand[4+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void recover(){
		byte[] openCommand={0x03,0x05,0x01,0x01,0x32,0x33,0x34,0x35,0x36,0x25,0x26,0x27,0x00,0x5E,0x26,0x36};

		for(int j=0;j<4;j++){
			openCommand[4+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void changeAPN(String apn){
		byte[] sendCommand={0x08,0x01,0x02,0x01,0x32,0x33,0x34,0x35,0x36,0x25,0x26,0x27,0x00,0x5E,0x26,0x36};
		int length = apn.length()+1;
		String lengthHex = String.format("%04x", length);
		byte[] lengthByte = hexStr2Bytes(lengthHex);
		
		for(int i=0;i<2;i++){
			sendCommand[3+i] = lengthByte[i];
		}
		for(int j=0;j<4;j++){
			sendCommand[5+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),sendCommand);
			getCurrentBleManage().writeString(encryptingCode);
			Thread.sleep(200);
			byte[] content = apn.getBytes();
			int count = length/12;
			if(length % 12 > 0){
				count +=1;
			}
			byte checkSum = 0x00;
			for(int i=0;i<count;i++){
				byte[] sendCommand1={0x08,0x01,0x01,0x01,0x32,0x33,0x34,0x35,0x36,0x25,0x26,0x27,0x00,0x5E,0x26,0x36};
				sendCommand1[2] = (byte)i;
				sendCommand1[3] = 12;
				for(int j=0;j<12;j++){
					if((j+i*12)==(length-1)){
						sendCommand1[3] = (byte)(j+1);
						sendCommand1[4+j] = checkSum;
						break;
					}else{
						checkSum^= content[j+i*12];
						sendCommand1[4+j] = content[j+i*12];
					}
				}
				byte[] newCode = AESUtils.encrypt(getKey(),sendCommand1);
				getCurrentBleManage().writeString(newCode);
				Thread.sleep(200);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void changeServer(String server){
		byte[] sendCommand={0x08,0x03,0x02,0x01,0x32,0x33,0x34,0x35,0x36,0x25,0x26,0x27,0x00,0x5E,0x26,0x36};
		int length = server.length()+1;
		String lengthHex = String.format("%04x", length);
		byte[] lengthByte = hexStr2Bytes(lengthHex);
		
		for(int i=0;i<2;i++){
			sendCommand[3+i] = lengthByte[i];
		}
		for(int j=0;j<4;j++){
			sendCommand[5+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),sendCommand);
			getCurrentBleManage().writeString(encryptingCode);
			Thread.sleep(200);
			byte[] content = server.getBytes();
			int count = length/12;
			if(length % 12 > 0){
				count +=1;
			}
			byte checkSum = 0x00;
			for(int i=0;i<count;i++){
				byte[] sendCommand1={0x08,0x03,0x01,0x01,0x32,0x33,0x34,0x35,0x36,0x25,0x26,0x27,0x00,0x5E,0x26,0x36};
				sendCommand1[2] = (byte)i;
				sendCommand1[3] = 12;
				for(int j=0;j<12;j++){
					if((j+i*12)==(length-1)){
						sendCommand1[3] = (byte)(j+1);
						sendCommand1[4+j] = checkSum;
						break;
					}else{
						checkSum^= content[j+i*12];
						sendCommand1[4+j] = content[j+i*12];
					}
				}
				byte[] newCode = AESUtils.encrypt(getKey(),sendCommand1);
				getCurrentBleManage().writeString(newCode);
				Thread.sleep(200);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void changePort(int port){
		byte[] sendCommand={0x08,0x05,0x02,0x01,0x32,0x33,0x34,0x35,0x36,0x25,0x26,0x27,0x00,0x5E,0x26,0x36};
		String portHex = String.format("%04x", port);
		byte[] portByte = hexStr2Bytes(portHex);
		
		for(int i=0;i<2;i++){
			sendCommand[3+i] = portByte[i];
		}
		
		for(int j=0;j<4;j++){
			sendCommand[5+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),sendCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendBack(){
		byte[] openCommand={0x05,0x16,0x01,0x01,0x32,0x33,0x34,0x35,0x36,0x25,0x26,0x27,0x00,0x5E,0x26,0x36};

		for(int j=0;j<4;j++){
			openCommand[4+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getDeviceInfo() {
		sendMessageToDevice(ContentUtils.COMMAND_GET_DEVICE_INFO, null);
	}

	// ���ĵ�״̬
	public void setLightState(boolean lightState) {
		DeviceInfo lightInfo = new DeviceInfo();
		lightInfo.setLightState(lightState);
		sendMessageToDevice(ContentUtils.COMMAND_SET_SWITCH_STATE, lightInfo);
	}

	// ���ĵ�����
	public void setLightBrightness(int brightness) {
		DeviceInfo lightInfo = new DeviceInfo();
		lightInfo.setLightBrightness(brightness);
		sendMessageToDevice(ContentUtils.COMMAND_SET_LIGHT_BRIGHTNESS,
				lightInfo);
	}

	// ���ĵƵ���ůɫ
	public void setLightColorTemp(boolean colorTemp) {
		DeviceInfo lightInfo = new DeviceInfo();
		lightInfo.setColorTemperature(colorTemp ? 1 : 0);
		sendMessageToDevice(ContentUtils.COMMAND_SET_COLOR_TEMPERATURE,
				lightInfo);
	}


	// ����RGBֵ
	public void setRGB(int rColor, int gColor, int bColor) {
		DeviceInfo lightInfo = new DeviceInfo();
		lightInfo.setRColor(rColor);
		lightInfo.setGColor(gColor);
		lightInfo.setBColor(bColor);
		sendMessageToDevice(ContentUtils.COMMAND_SET_RGB, lightInfo);
	}

	public Boolean getShouldReScan() {
		return shouldReScan;
	}

	public void setShouldReScan(Boolean shouldReScan) {
		this.shouldReScan = shouldReScan;
	}
	
	public void getLockState(){
		byte[] openCommand={0x05,0x0e,0x01,0x01,0x32,0x33,0x34,0x35,0x36,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
		for(int j=0;j<4;j++){
			openCommand[4+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getCurrentBleManage().writeString(openCommand);
	}
	
	private byte[] getKey(){
		if (MainApplication.getInstance().getLightInfo() != null) {
			DeviceInfo lInfo= MainApplication.getInstance().getLightInfo();
			if(lInfo.getDeviceName().contains("T103")){
				if(lInfo.getManufacturer() == 0x0102){
					return key_lixiang;
				}else{
					return key_xiaoyi;
				}
			}else if(lInfo.getDeviceName().contains("T111") || 
					lInfo.getDeviceName().contains("Gua Suo")){
					return key_T111;
			}else if(lInfo.getDeviceName().contains("T105")){
				return key_xiaoyi;
			}else if(lInfo.getDeviceName().contains("XiaoYi")){
				//return key_T111;
				return key_oxo;
			}
		}
		return key_xiaoyi;
	}

	@Override
	public void transportationMode() {
		byte[] openCommand={0x08,0x07,0x01,0x01,0x32,0x33,0x34,0x35,0x36,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
		for(int j=0;j<4;j++){
			openCommand[2+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	@Override
	public void testACCE(){
		byte[] openCommand={0x08,0x09,0x01,0x01,0x32,0x33,0x34,0x35,0x36,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
		for(int j=0;j<4;j++){
			openCommand[2+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void testGPRS(){
		byte[] openCommand={0x08,0x0B,0x01,0x01,0x32,0x33,0x34,0x35,0x36,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
		for(int j=0;j<4;j++){
			openCommand[2+j] = token[j];
		}
		try {
			byte[] encryptingCode = AESUtils.encrypt(getKey(),openCommand);
			getCurrentBleManage().writeString(encryptingCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
