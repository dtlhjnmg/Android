package com.onemore.ble.maintain.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.onemore.ble.maintain.R;

public class PopupwindowCheckBoxListAdapter extends BaseAdapter {
	private ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String,Object>>();
	private Context mContext = null;
	private ArrayList<Boolean> selectedIndex = null;
	private HashMap<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

	public PopupwindowCheckBoxListAdapter(Context context,
			ArrayList<String> list, ArrayList<Boolean> defaultSelecteds) {
		mContext = context;
		selectedIndex = defaultSelecteds;
		if (selectedIndex == null) {
			selectedIndex = new ArrayList<Boolean>();
			for (int i = 0; i < list.size(); i++) {
				selectedIndex.add(false);
			}
		}
		for (int i = 0; i < list.size(); i++) {
			if (selectedIndex.get(i)) {
				isCheckMap.put(i, true);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("radioid", i);
            map.put("textview", list.get(i));
            mList.add(map);

		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		AppItem appItem;
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.popupwindow_list_checkout_item_layout, null);
			appItem = new AppItem();
			appItem.checkBox = (CheckBox) v.findViewById(R.id.cb_popupwindow);
			v.setTag(appItem);
			convertView = v;
		} else {
			appItem = (AppItem) convertView.getTag();
		}
		appItem.checkBox.setText(mList.get(position).get("textview").toString());
		appItem.checkBox.setTag(mList.get(position).get("radioid").toString());

		if (isCheckMap != null && isCheckMap.containsKey(position)) {
			appItem.checkBox.setChecked(isCheckMap.get(position));
		} else {
			appItem.checkBox.setChecked(false);
		}
		
		appItem.checkBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						int radiaoId = Integer.parseInt(buttonView.getTag()
								.toString());
						selectedIndex.set(radiaoId, isChecked);
						if (isChecked) {
							// ��ѡ�еķ���hashmap��
							isCheckMap.put(radiaoId, isChecked);
						} else {
							// ȡ��ѡ�е����޳�
							isCheckMap.remove(radiaoId);
						}

					}
				});
		return convertView;
	}

	public ArrayList<Boolean> getSelectedItem() {
		return selectedIndex;
	}

	class AppItem {
		CheckBox checkBox;
	}
}
