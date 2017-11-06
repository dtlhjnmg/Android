package com.onemore.ble.maintain.view;

import java.util.ArrayList;
import java.util.Calendar;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.onemore.ble.maintain.R;
import com.onemore.ble.maintain.adapter.PopupwindowCheckBoxListAdapter;
import com.onemore.ble.maintain.util.L;
import com.wheel.OnWheelChangedListener;
import com.wheel.StrericWheelAdapter;
import com.wheel.WheelView;

public class PopupDialogView extends AlertDialog {
	public static final int POPUPWINDOW_SELECTBUTTON_LIST = 1;
	public static final int POPUPWINDOW_TIME_PICKER = 2;
	public static final int POPUPWINDOW_NET_ERROR = 3;
	public static final int POPUPWINDOW_TEXT_LIST = 4;
	public static final int POPUPWINDOW_TEXT = 5;
	public static final int POPUPWINDOW_AUTO_BILLING = 6;
	public static final int POPUPWINDOW_RADIOBUTTON_LIST = 7;
	public static final int POPUPWINDOW_EDITTEXT = 8;
	public static final int POPUPWINDOW_TEXT_ONE_BUTTON = 9;
	public static final int POPUPWINDOW_LINK_PASSWORD = 10;
	public static final int POPUPWINDOW_DATETIME_PICKER = 11;
	public static final int POPUPWINDOW_MENU = 12;

	public static final int POPUPWINDOW_BUTTON_NONE = 0;
	public static final int POPUPWINDOW_BUTTON_ONE = 1;
	public static final int POPUPWINDOW_BUTTON_TWO = 2;
	private int YEAR_START = 2010;
	private LinearLayout showLinearLayout = null;
	private OnPopupDialogButtonClick leftButtonClick = null;
	private OnPopupDialogButtonClick rightButtonClick = null;
	private TextView titleTextView = null;
	private Button confirmButton, cancelButton;
	private WheelView yearWheel, monthWheel, dayWheel, hourWheel, minutesWheel;
	private int yearIndex, monthIndex, dayIndex, ampmIndex, hourIndex,
			minuteIndex;
	private CheckBox useSystemTimeBox = null;
	private boolean isUseSystemTime = false;
	private BaseAdapter popupAdapter = null;
	private int dialogLayout = -1;
	private ArrayList<Boolean> selecteItems = null;
	private Context mContext = null;
	private int dialogType = -1;
	private String titleText = null;
	private Object arg1, arg2;
	private boolean hasTitle = true;
	private int buttonCount = POPUPWINDOW_BUTTON_TWO;
	private int layoutWidth = 0, layoutHeight = 0;
	private String messageContent = null;
	private EditText editText = null;
	private View.OnClickListener menuItemClickListener = null;

	public PopupDialogView(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		{
			if (getDialogLayout() != -1) {
				setContentView(getDialogLayout());
			} else {
				setContentView(R.layout.popupwindow_layout);
			}
			this.setCanceledOnTouchOutside(false);
			titleTextView = (TextView) findViewById(R.id.tv_popupwindow_title);
			if (titleText != null) {
				titleTextView.setText(titleText);
			}
			if (!isHasTitle()) {
				LinearLayout titleLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_title);
				if (titleLayout != null)
					titleLayout.setVisibility(View.GONE);
			}
			if (getButtonCount() == POPUPWINDOW_BUTTON_NONE) {
				LinearLayout buttonLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_button);
				if (buttonLinearLayout != null)
					buttonLinearLayout.setVisibility(View.GONE);
			} else if (getButtonCount() == POPUPWINDOW_BUTTON_ONE) {
				LinearLayout oneButtonLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_one_button);
				if (oneButtonLinearLayout != null) {
					oneButtonLinearLayout.setVisibility(View.VISIBLE);
					confirmButton = (Button) findViewById(R.id.btn_popupwindow_one);
				}
				LinearLayout buttonLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_button);
				if (buttonLinearLayout != null) {
					buttonLinearLayout.setVisibility(View.GONE);
				}
			} else if (getButtonCount() == POPUPWINDOW_BUTTON_TWO) {
				confirmButton = (Button) findViewById(R.id.btn_popupwindow_right);
			}
			switch (dialogType) {
			case POPUPWINDOW_TEXT:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_text);
				showLinearLayout.setVisibility(View.VISIBLE);
				TextView messageContentTextView = (TextView) findViewById(R.id.tv_popupwindow_text);
				if (getMessageContent() != null)
					messageContentTextView.setText(getMessageContent());
				confirmButton.setText(R.string.button_confirm);
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (getRightButtonClick() != null) {
							getRightButtonClick().onButtonClick(null, null);
						}
					}
				});
				break;
			case POPUPWINDOW_SELECTBUTTON_LIST:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_list);
				showLinearLayout.setVisibility(View.VISIBLE);
				ListView listView = (ListView) findViewById(R.id.lv_popupwindow);
				if (listView != null && popupAdapter != null) {
					listView.setAdapter(popupAdapter);
					popupAdapter.notifyDataSetChanged();
				}
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (rightButtonClick != null) {
							if (popupAdapter != null) {
								rightButtonClick
										.onButtonClick(
												((PopupwindowCheckBoxListAdapter) popupAdapter)
														.getSelectedItem(),
												null);
							}
						}
					}
				});
				break;
			case POPUPWINDOW_TIME_PICKER:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_time_picker);
				showLinearLayout.setVisibility(View.VISIBLE);
				initTimePicker();
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (rightButtonClick != null) {
							rightButtonClick.onButtonClick(hourIndex,
									minuteIndex);
						}
					}
				});
				break;
			case POPUPWINDOW_EDITTEXT:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_edittext);
				showLinearLayout.setVisibility(View.VISIBLE);
				editText = (EditText) findViewById(R.id.edt_socket_name);
				editText.setText(getMessageContent());
				editText.requestFocus();
				InputFilter[] filters = {new AdnNameLengthFilter()};  
				editText.setFilters(filters);
				Button editTextButton = (Button) getWindow().findViewById(
						R.id.btn_popupwindow_right);
				editTextButton
						.setOnClickListener(new android.view.View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if (rightButtonClick != null) {
									rightButtonClick.onButtonClick(editText
											.getText().toString().trim(), null);
								}
							}
						});
				break;
			case POPUPWINDOW_MENU:
				this.setCanceledOnTouchOutside(true);
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_menu);
				showLinearLayout.setVisibility(View.VISIBLE);
				Button upButton = (Button) findViewById(R.id.btn_menu_up);
				if (getArg1() != null) {
					upButton.setText(getArg1().toString());
				}
				upButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (getMenuItemClickListener() != null)
							getMenuItemClickListener().onClick(v);
					}
				});
				Button downButton = (Button) findViewById(R.id.btn_menu_down);
				downButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (getMenuItemClickListener() != null)
							getMenuItemClickListener().onClick(v);
					}
				});
				break;
			case POPUPWINDOW_DATETIME_PICKER:
				showLinearLayout = (LinearLayout) findViewById(R.id.ll_popupwindow_datetime_picker);
				showLinearLayout.setVisibility(View.VISIBLE);
				isUseSystemTime = false;
				useSystemTimeBox = (CheckBox) findViewById(R.id.cb_use_system_time);
				useSystemTimeBox
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(CompoundButton arg0,
									boolean arg1) {
								if (arg1 && !isUseSystemTime) {
									isUseSystemTime = true;
									yearWheel.setCurrentItem(Calendar
											.getInstance().get(Calendar.YEAR)
											- YEAR_START);
									monthWheel.setCurrentItem(Calendar
											.getInstance().get(Calendar.MONTH));
									dayWheel.setCurrentItem(Calendar
											.getInstance().get(
													Calendar.DAY_OF_MONTH)-1);
									hourWheel.setCurrentItem(Calendar
											.getInstance().get(
													Calendar.HOUR_OF_DAY));
									minutesWheel
											.setCurrentItem(Calendar
													.getInstance().get(
															Calendar.MINUTE));
									isUseSystemTime = false;
								}
							}
						});
				initDateTimePicker();
				confirmButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (rightButtonClick != null) {
							Calendar settingCalendar = Calendar.getInstance();
							settingCalendar.set(Calendar.YEAR, yearIndex);
							settingCalendar.set(Calendar.MONTH, monthIndex);
							settingCalendar
									.set(Calendar.DAY_OF_MONTH, dayIndex);
							settingCalendar
									.set(Calendar.HOUR_OF_DAY, hourIndex);
							settingCalendar.set(Calendar.MINUTE, minuteIndex);
							rightButtonClick.onButtonClick(settingCalendar,
									null);
						}
					}
				});
				break;
			}
			if (getButtonCount() == POPUPWINDOW_BUTTON_TWO) {
				Button cancelButton = (Button) getWindow().findViewById(
						R.id.btn_popupwindow_left);
				cancelButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						PopupDialogView.this.dismiss();
						if (getLeftButtonClick() != null) {
							leftButtonClick.onButtonClick(null, null);
						}
					}
				});
			}
		}
	}

	public EditText getEditText() {
		return editText;
	}

	public void showInput() {
		if (editText != null) {
			editText.requestFocus();
			InputMethodManager inputManager = (InputMethodManager) editText
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(editText, 0);
		}
	}

	private void initTimePicker() {
		Calendar currentDate = null;
		if (getArg1() != null && getArg1() instanceof Calendar) {
			currentDate = (Calendar) getArg1();
		} else {
			currentDate = Calendar.getInstance();
		}
		hourIndex = currentDate.get(Calendar.HOUR_OF_DAY);
		// dayIndex = hourIndex / 12;
		minuteIndex = currentDate.get(Calendar.MINUTE);
		ArrayList<String> minutesList = new ArrayList<String>();
		ArrayList<String> hourList = new ArrayList<String>();
		// ArrayList<String> dayList = new ArrayList<String>();

		for (int j = 0; j < 60; j++) {
			minutesList.add(String.format("%02d", j));
			if (j < 24)
				hourList.add(String.format("%02d", j));
		}

		/*
		 * String am = mContext.getResources().getString(R.string.am); String pm
		 * = mContext.getResources().getString(R.string.pm); dayList.add(am);
		 * dayList.add(pm); dayWheel = (WheelView) findViewById(R.id.daywheel);
		 * dayWheel.setAdapter(new StrericWheelAdapter(dayList));
		 * dayWheel.setCurrentItem(dayIndex); dayWheel.setCyclic(false);
		 * dayWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
		 * R.dimen.title_text_size)); dayWheel.setInterpolator(new
		 * AnticipateOvershootInterpolator());
		 * 
		 * dayWheel.addChangingListener(new OnWheelChangedListener() {
		 * 
		 * @Override public void onChanged(WheelView wheel, int oldValue, int
		 * newValue) { dayIndex = newValue; } });
		 */
		hourWheel = (WheelView) findViewById(R.id.hourwheel);
		hourWheel.setAdapter(new StrericWheelAdapter(hourList));
		hourWheel.setCurrentItem(hourIndex);
		hourWheel.setCyclic(true);
		hourWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
				R.dimen.normal_text_size));
		hourWheel.setInterpolator(new AnticipateOvershootInterpolator());
		hourWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int lastValue = oldValue % 24;
				int nextValue = newValue % 24;
				/*
				 * if ((lastValue == 0 && nextValue == 11) || (lastValue == 11
				 * && nextValue == 0)) { dayIndex = (dayIndex == 0) ? 1 : 0;
				 * dayWheel.setCurrentItem(dayIndex); }
				 */
				hourIndex = newValue;
			}
		});

		minutesWheel = (WheelView) findViewById(R.id.minuteswheel);
		minutesWheel.setAdapter(new StrericWheelAdapter(minutesList));
		minutesWheel.setCurrentItem(minuteIndex);
		minutesWheel.setCyclic(true);
		minutesWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
				R.dimen.normal_text_size));
		minutesWheel.setInterpolator(new AnticipateOvershootInterpolator());
		minutesWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				minuteIndex = newValue % 60;
			}
		});
	}

	private void initDateTimePicker() {
		Calendar currentDate = null;
		if (getArg1() != null && getArg1() instanceof Calendar) {
			currentDate = (Calendar) getArg1();
		} else {
			currentDate = Calendar.getInstance();
		}
		yearIndex = currentDate.get(Calendar.YEAR);
		monthIndex = currentDate.get(Calendar.MONTH);
		dayIndex = currentDate.get(Calendar.DAY_OF_MONTH);
		hourIndex = currentDate.get(Calendar.HOUR_OF_DAY);
		ampmIndex = hourIndex / 12;
		minuteIndex = currentDate.get(Calendar.MINUTE);
		ArrayList<String> minutesList = new ArrayList<String>();
		ArrayList<String> hourList = new ArrayList<String>();
		ArrayList<String> ampmList = new ArrayList<String>();
		ArrayList<String> dayList = new ArrayList<String>();
		ArrayList<String> monthList = new ArrayList<String>();
		ArrayList<String> yearList = new ArrayList<String>();
		int dayNumberOfMonth = currentDate
				.getActualMaximum(Calendar.DAY_OF_MONTH);
		for (int j = 0; j < 60; j++) {
			minutesList.add(String.format("%02d", j));
			if (j < 12) {
				monthList.add(String.format("%02d", (j + 1)));
			}
			if (j < 24) {
				hourList.add(String.format("%02d", j));
			}
			if (j < dayNumberOfMonth) {
				dayList.add(String.format("%02d", (j + 1)));
			}
			yearList.add(String.valueOf(YEAR_START + j));
		}
		yearWheel = (WheelView) findViewById(R.id.datetime_yearwheel);
		yearWheel.setAdapter(new StrericWheelAdapter(yearList));
		yearWheel.setCurrentItem(yearIndex - YEAR_START);
		yearWheel.setLabel(mContext.getResources().getString(
				R.string.datetime_year));
		yearWheel.setCyclic(false);
		yearWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
				R.dimen.normal_text_size));
		yearWheel.setInterpolator(new AnticipateOvershootInterpolator());
		yearWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				yearIndex = YEAR_START + newValue;
				updateDayWheel(yearIndex, monthIndex);
				if (!isUseSystemTime) {
					useSystemTimeBox.setChecked(false);
				}
			}
		});

		monthWheel = (WheelView) findViewById(R.id.datetime_monthwheel);
		monthWheel.setAdapter(new StrericWheelAdapter(monthList));
		monthWheel.setLabel(mContext.getResources().getString(
				R.string.datetime_month));
		monthWheel.setCurrentItem(monthIndex);
		monthWheel.setCyclic(true);
		monthWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
				R.dimen.normal_text_size));
		monthWheel.setInterpolator(new AnticipateOvershootInterpolator());
		monthWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				monthIndex = newValue % 12;
				updateDayWheel(yearIndex, monthIndex);
				if (!isUseSystemTime) {
					useSystemTimeBox.setChecked(false);
				}
			}
		});

		dayWheel = (WheelView) findViewById(R.id.datetime_daywheel);
		dayWheel.setAdapter(new StrericWheelAdapter(dayList));
		dayWheel.setLabel(mContext.getResources().getString(
				R.string.datetime_day));
		dayWheel.setCurrentItem(dayIndex - 1);
		dayWheel.setCyclic(true);
		dayWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
				R.dimen.normal_text_size));
		dayWheel.setInterpolator(new AnticipateOvershootInterpolator());
		dayWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				dayIndex = newValue + 1;
				if (!isUseSystemTime) {
					useSystemTimeBox.setChecked(false);
				}
			}
		});

		String am = mContext.getResources().getString(R.string.am);
		String pm = mContext.getResources().getString(R.string.pm);
		ampmList.add(am);
		ampmList.add(pm);
		/*
		 * ampmWheel = (WheelView) findViewById(R.id.datetime_ampmwheel);
		 * ampmWheel.setAdapter(new StrericWheelAdapter(ampmList));
		 * ampmWheel.setCurrentItem(ampmIndex); ampmWheel.setCyclic(false);
		 * ampmWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
		 * R.dimen.title_text_size)); ampmWheel.setInterpolator(new
		 * AnticipateOvershootInterpolator());
		 * 
		 * ampmWheel.addChangingListener(new OnWheelChangedListener() {
		 * 
		 * @Override public void onChanged(WheelView wheel, int oldValue, int
		 * newValue) { ampmIndex = newValue; } });
		 */

		hourWheel = (WheelView) findViewById(R.id.datetime_hourwheel);
		hourWheel.setAdapter(new StrericWheelAdapter(hourList));
		hourWheel.setLabel(mContext.getResources().getString(
				R.string.datetime_hour));
		hourWheel.setCurrentItem(hourIndex % 24);
		hourWheel.setCyclic(true);
		hourWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
				R.dimen.normal_text_size));
		hourWheel.setInterpolator(new AnticipateOvershootInterpolator());
		hourWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int lastValue = oldValue % 12;
				int nextValue = newValue % 12;
				if ((lastValue == 0 && nextValue == 11)
						|| (lastValue == 11 && nextValue == 0)) {
					ampmIndex = (ampmIndex == 0) ? 1 : 0;
					// ampmWheel.setCurrentItem(dayIndex);
				}
				hourIndex = newValue % 24;
				if (!isUseSystemTime) {
					useSystemTimeBox.setChecked(false);
				}
			}
		});

		minutesWheel = (WheelView) findViewById(R.id.datetime_minuteswheel);
		minutesWheel.setAdapter(new StrericWheelAdapter(minutesList));
		minutesWheel.setLabel(mContext.getResources().getString(
				R.string.datetime_minute));
		minutesWheel.setCurrentItem(minuteIndex);
		minutesWheel.setCyclic(true);
		minutesWheel.setTextSize(mContext.getResources().getDimensionPixelSize(
				R.dimen.normal_text_size));
		minutesWheel.setInterpolator(new AnticipateOvershootInterpolator());
		minutesWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				minuteIndex = newValue % 60;
				if (!isUseSystemTime) {
					useSystemTimeBox.setChecked(false);
				}
			}
		});
	}

	private void updateDayWheel(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		int dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		ArrayList<String> dayList = new ArrayList<String>();
		for (int i = 1; i <= dayOfMonth; i++) {
			dayList.add(String.format("%02d", i));
		}

		if (dayWheel != null) {
			dayWheel.setAdapter(new StrericWheelAdapter(dayList));
			dayWheel.invalidate();
		}
	}

	public interface OnPopupDialogButtonClick {
		public void onButtonClick(Object arg1, Object arg2);
	}

	public void setRightButtonClick(OnPopupDialogButtonClick rightButtonClick) {
		this.rightButtonClick = rightButtonClick;
	}

	public int getLayoutWidth() {
		return layoutWidth;
	}

	public void setLayoutWidth(int layoutWidth) {
		this.layoutWidth = layoutWidth;
	}

	public int getLayoutHeight() {
		return layoutHeight;
	}

	public void setLayoutHeight(int layoutHeight) {
		this.layoutHeight = layoutHeight;
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public void setTitleText(int titleTextId) {
		if (mContext != null)
			this.titleText = mContext.getResources().getString(titleTextId);
	}

	public Object getArg1() {
		return arg1;
	}

	public void setArg1(Object arg1) {
		this.arg1 = arg1;
	}

	public Object getArg2() {
		return arg2;
	}

	public void setArg2(Object arg2) {
		this.arg2 = arg2;
	}

	public OnPopupDialogButtonClick getRightButtonClick() {
		return rightButtonClick;
	}

	public int getDialogType() {
		return dialogType;
	}

	public void setDialogType(int dialogType) {
		this.dialogType = dialogType;
	}

	public BaseAdapter getPopupAdapter() {
		return popupAdapter;
	}

	public void setPopupAdapter(BaseAdapter popupAdapter) {
		this.popupAdapter = popupAdapter;
	}

	public ArrayList<Boolean> getSelecteItems() {
		return selecteItems;
	}

	public void setSelecteItems(ArrayList<Boolean> selecteItems) {
		this.selecteItems = selecteItems;
	}

	public boolean isHasTitle() {
		return hasTitle;
	}

	public void setHasTitle(boolean hasTitle) {
		this.hasTitle = hasTitle;
	}

	public int getButtonCount() {
		return buttonCount;
	}

	public void setButtonCount(int buttonCount) {
		this.buttonCount = buttonCount;
	}

	public OnPopupDialogButtonClick getLeftButtonClick() {
		return leftButtonClick;
	}

	public void setLeftButtonClick(OnPopupDialogButtonClick leftButtonClick) {
		this.leftButtonClick = leftButtonClick;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public int getDialogLayout() {
		return dialogLayout;
	}

	public void setDialogLayout(int dialogLayout) {
		this.dialogLayout = dialogLayout;
	}

	public View.OnClickListener getMenuItemClickListener() {
		return menuItemClickListener;
	}

	public void setMenuItemClickListener(
			View.OnClickListener menuItemClickListener) {
		this.menuItemClickListener = menuItemClickListener;
	}

	public class AdnNameLengthFilter implements InputFilter {
		private int nMax;

		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			int destCount = isChinese(dest.toString());
			int sourceCount = isChinese(source.toString());
			/*if (isChinese(dest.toString()) || isChinese(source.toString())) {
				nMax = 6;
			} else {
				nMax = 20;
			}*/

			//int keep = nMax - (dest.length() - (dend - dstart));
			int keep = 21 - (destCount + sourceCount);
			if (keep <= 0) {
				return "";
			} else if (keep >= end - start) {
				return null; // keep original
			} else {
				return source.subSequence(start, start + keep);
			}
		}

		private int isChinese(String str) {
			char[] chars = str.toCharArray();
			int count = 0;
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
				if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
						|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
						|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
						|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
						|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
						|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
					count+=3;
				}else{
					count++;
				}
			}
			return count;
		}
	}
}
