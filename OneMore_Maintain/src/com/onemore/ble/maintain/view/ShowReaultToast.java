package com.onemore.ble.maintain.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.onemore.ble.maintain.R;

public class ShowReaultToast {
    private static Toast mToast;
    private static TextView contentView = null;

    public static void showToast(Context mContext, String text, int duration) {
        if (mToast != null){
        	contentView.setText(text);
        }
        else{        	
        	initToast(mContext, text, duration);
        }
        mToast.show();
    }

    public static void showToast(Context mContext, int resId, int duration) {
        showToast(mContext, mContext.getResources().getString(resId), duration);
    }

	private static void initToast(Context context, String message,int duration) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View toastView = inflater.inflate(R.layout.toast_text_layout, null);
		TextView titleView = (TextView) toastView
				.findViewById(R.id.tv_popupwindow_title);
		titleView.setText(R.string.toast_title);
		contentView = (TextView) toastView
				.findViewById(R.id.tv_popupwindow_text);
    	contentView.setText(message);
		if (mToast == null) {
			mToast = new Toast(context);
			mToast.setGravity(Gravity.CENTER, 0, 0);
			mToast.setDuration(duration);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.setView(toastView);
	}	
}


