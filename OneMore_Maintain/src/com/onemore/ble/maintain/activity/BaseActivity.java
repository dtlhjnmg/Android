/**
 * 
 */
package com.onemore.ble.maintain.activity;

import android.app.Activity;
import android.os.Handler;

import com.onemore.ble.maintain.R;
import com.onemore.ble.maintain.util.BLEUtils;
import com.onemore.ble.maintain.view.WaitProgressView;

/**
 * @author wuzhiyi
 * @date 2015-11-25 ����10:13:17
 */
public class BaseActivity extends Activity {
	private WaitProgressView waitProgressView = null;
	private Handler waitProgressHandler = new Handler();
	private Runnable waitProgressRunnable = new Runnable() {
		@Override
		public void run() {
			waitProgressHandler.removeCallbacks(waitProgressRunnable);
			handleTimeout();
		}
	};
	public void showWaitMessage() {
		showWaitMessage(R.string.progress_wait);
	}

	public void showWaitMessage(int showMessage) {
		showWaitMessage(showMessage, 0);
	}

	public void showWaitMessage(int showMessage, int waitTime) {
		showWaitMessage(showMessage, R.string.device_offline_error, waitTime);
	}
	
	public void showWaitMessage(int showMessage, int dismissMessage,int waitTime) {
		waitProgressView = new WaitProgressView(this, 0);
		waitProgressView.setShowText(showMessage, dismissMessage);
		waitProgressView.show(waitTime, BLEUtils.TIMEOUT);
	}
	
	public void dismissWaitMessage() {
		if (waitProgressView != null)
			waitProgressView.hide();
	}
	
	public void handleTimeout(){
		
	}
	
}
