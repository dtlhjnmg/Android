package com.onemore.ble.maintain.view;

import java.util.regex.Matcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.onemore.ble.maintain.R;
import com.onemore.ble.maintain.bean.DeviceInfo;
import com.onemore.ble.maintain.util.L;

public class LightView extends View {
	private static final int ACCROSS_NORMAL = -1;
	private static final int ACCROSS_INCREATE = 1;
	private static final int ACCROSS_DECREATE = 0;
	private static final int ANIMATION_SPEED_MIN = 5;
	private static final int ANIMATION_SPEED_MAX = 400;
	private static final int ANIMATION_SPEED = 50;
	private int startDownX = 0;
	private int startDownY = 0;
	private int currentX = 0;
	private int currentY = 0;
	private int endUpX = 0;
	private int endUpY = 0;
	private long lastTime = 0L;
	private boolean isLongClickMove = false;
	private boolean isInArea = false;
	private int maxProgress = 100;
	private int minProgress = 0;
	private int minDegress = 0;
	private int progress = 30;
	private int progressStrokeWidth = 90;
	private int viewWidth = 0;
	private int viewHeight = 0;
	private int littleCir = 0;
	private Context context;
	private int brightnessDegress = 0;
	private int mStartDegree = 0;  
	private int currentDegree = 0;
    private boolean mClockwise = false, mEnableAnimation = true;  
    private long mAnimationStartTime = 0;  
    private long mAnimationEndTime = 0;
    private long mLastReceiveTime = 0;
    private long mCurrentReceiveTime = 0;
    private long speed = 270;
	private int lastZone = -1;
	private int accrossType = -1;// 1,add 360,else -360
	private Bitmap lightStateBitmap = null;
	private Bitmap brightnessBitmap = null;
	private Bitmap lightBrightnessBgBitmap = null;
	private Bitmap lightBrightnessBarBitmap = null;
	private DeviceInfo lightInfo = null;
	private Boolean isClickOnState = false;
	private Rect clickRect = new Rect(0, 0, 0, 0);
	private Boolean isCanDimming = false;
	private Boolean isFull = false;

	// ��Բ���ڵľ�������
	RectF oval;
	Paint paint;

	public LightView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO �Զ����ɵĹ��캯�����
		this.context = context;
		this.lightInfo = new DeviceInfo();
		this.currentDegree = 0;
		lightInfo.setLightBrightness(30);
		lightInfo.setLightState(true);
		oval = new RectF();
		paint = new Paint();
		lightBrightnessBarBitmap = ((BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.light_brightness_bar_cool)).getBitmap();
		lightBrightnessBgBitmap = ((BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.light_dimming_all_bg)).getBitmap();
		updateStateBg();
		brightnessBitmap = ((BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.light_dimming_all_brightness_cool))
				.getBitmap();
		brightnessDegress = (int) ((progress * 360 / maxProgress));
		if (brightnessBitmap != null && lightStateBitmap != null) {
			clickRect.left = (brightnessBitmap.getWidth() - lightStateBitmap
					.getWidth()) / 2;
			clickRect.right = clickRect.left + lightStateBitmap.getWidth();
			clickRect.top = (brightnessBitmap.getHeight() - lightStateBitmap
					.getHeight()) / 2;
			clickRect.bottom = clickRect.top + lightStateBitmap.getHeight();
		}
		progressStrokeWidth = 120;

		if (brightnessBitmap != null && lightStateBitmap != null) {
			clickRect.left = brightnessBitmap.getWidth() / 2
					- progressStrokeWidth;
			clickRect.right = brightnessBitmap.getWidth() - progressStrokeWidth;
			clickRect.top = brightnessBitmap.getHeight() / 2
					- progressStrokeWidth;
			clickRect.bottom = brightnessBitmap.getHeight()
					- progressStrokeWidth;
		}
	}

	public LightView(Context context, DeviceInfo lightInfo) {
		super(context);
		// TODO �Զ����ɵĹ��캯�����
		this.context = context;
		oval = new RectF();
		paint = new Paint();
		lightBrightnessBarBitmap = ((BitmapDrawable) context
				.getResources()
				.getDrawable(
						lightInfo.isColorWarm() ? R.drawable.light_brightness_bar_warm
								: R.drawable.light_brightness_bar_cool))
				.getBitmap();
		lightBrightnessBgBitmap = ((BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.light_dimming_all_bg)).getBitmap();
		updateStateBg();

		this.lightInfo = lightInfo;
		this.progress = lightInfo.getLightBrightness();
		this.brightnessDegress = (int) ((progress * 360 / maxProgress));
		progressStrokeWidth = brightnessBitmap.getWidth();
	}

	public void updateStateBg() {
		if (lightInfo != null && lightInfo.getLightState()) {
			brightnessBitmap = ((BitmapDrawable) context
					.getResources()
					.getDrawable(
							lightInfo.isColorWarm() ? R.drawable.light_dimming_all_brightness_warm
									: R.drawable.light_dimming_all_brightness_cool))
					.getBitmap();

			lightStateBitmap = ((BitmapDrawable) context
					.getResources()
					.getDrawable(
							lightInfo.isColorWarm() ? R.drawable.light_poweron_large_warm
									: R.drawable.light_poweron_large_cool))
					.getBitmap();
			
			lightBrightnessBarBitmap = ((BitmapDrawable) context.getResources()
					.getDrawable(lightInfo.isColorWarm() ?R.drawable.light_brightness_bar_warm:R.drawable.light_brightness_bar_cool)).getBitmap();
		} else {
			lightStateBitmap = ((BitmapDrawable) context.getResources()
					.getDrawable(R.drawable.light_light_poweroff_large))
					.getBitmap();
		}
	}

	public void changeLightState(boolean lightState) {
		this.lightInfo.setLightState(lightState);
		updateStateBg();
		invalidate();
	}

	public void changeLightColorTemp(int colorTemperature) {
		this.lightInfo.setColorTemperature(colorTemperature);
		updateStateBg();
		invalidate();
	}

	public DeviceInfo getLightInfo() {
		return lightInfo;
	}

	public void setLightInfo(DeviceInfo lightInfo,Boolean isAnim) {
		this.lightInfo = lightInfo;
		updateStateBg();
		this.progress = lightInfo.getLightBrightness();
		this.brightnessDegress = (int) ((progress * 360 / maxProgress));
		if(isAnim){
			mStartDegree = currentDegree;
			int diff = brightnessDegress - currentDegree; 
			mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis(); 
			mCurrentReceiveTime = AnimationUtils.currentAnimationTimeMillis();
			
			long diffTime = lightInfo.getChangeTime();
			diffTime = Math.max(diffTime, ANIMATION_SPEED_MIN);
			diffTime = Math.min(diffTime, ANIMATION_SPEED_MAX);
			if(mLastReceiveTime!=0&& diffTime!=0){
				speed = ANIMATION_SPEED *500/(diffTime);
			}else{
				speed = ANIMATION_SPEED;
			}
			//L.d("diff="+diff+",diffTime="+diffTime+",mCurrentReceiveTime="+mCurrentReceiveTime+",mLastReceiveTime="+mLastReceiveTime+",speed="+speed);
			if(speed<=0){
				speed = ANIMATION_SPEED;
			}
			mLastReceiveTime = mCurrentReceiveTime;
            mClockwise = diff >= 0;  
            mAnimationEndTime = mAnimationStartTime  
                    + Math.abs(diff) * 1000 / speed;  
		}else{
			currentDegree = brightnessDegress;
		}
		invalidate();
	}

	public void getSector(Canvas canvas, float center_X, float center_Y,
			float r, float startAngle, float sweepAngle) {

		Path path = new Path();
		// �����ǻ��һ�������εļ�����

		path.moveTo(center_X, center_Y); // Բ��

		path.lineTo(
				(float) (center_X + r * Math.cos(startAngle * Math.PI / 180)), // ��ʼ��Ƕ���Բ�϶�Ӧ�ĺ�����
				(float) (center_Y + r * Math.sin(startAngle * Math.PI / 180))); // ��ʼ��Ƕ���Բ�϶�Ӧ��������

		path.lineTo(
				(float) (center_X + r * Math.cos(sweepAngle * Math.PI / 180)), // �յ�Ƕ���Բ�϶�Ӧ�ĺ�����

				(float) (center_Y + r * Math.sin(sweepAngle * Math.PI / 180))); // �յ��Ƕ���Բ�϶�Ӧ��������

		path.close();

		// //����һ�������Σ�����Բ

		RectF rectF = new RectF(center_X - r, center_Y - r, center_X + r,
				center_Y + r);

		// �����ǻ�û��μ������ķ���

		path.addArc(rectF, startAngle, sweepAngle - startAngle);
		
		canvas.clipPath(path);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO �Զ����ɵķ������
		super.onDraw(canvas);
		viewWidth = this.getWidth();
		viewHeight = this.getHeight();

		paint.setAntiAlias(true); // ���û���Ϊ�����
		paint.setStyle(Style.STROKE);

		if (viewWidth != viewHeight) {
			int min = Math.min(viewWidth, viewHeight);
			viewWidth = min;
			viewHeight = min;
		}
		int r = lightStateBitmap.getWidth() / 2
				+ (viewWidth - lightStateBitmap.getWidth()) / 2;
		canvas.drawBitmap(lightBrightnessBgBitmap,
				(viewWidth - lightStateBitmap.getWidth()) / 2,
				(viewHeight - lightStateBitmap.getHeight()) / 2, paint);

		if (lightInfo.getLightState()) {
			if (currentDegree != brightnessDegress) {  
	            long time = AnimationUtils.currentAnimationTimeMillis();  
	            if (time < mAnimationEndTime) {  
	                int deltaTime = (int)(time - mAnimationStartTime);  
	                int degree = mStartDegree + (int)speed  
	                        * (mClockwise ? deltaTime : -deltaTime) / 1000;  
	            	//Log.d("speed","mStartDegree="+mStartDegree+",mClockwise="+mClockwise+",degree"+degree);
	            	if(degree==360 && !mClockwise){
	            		
	            	}else{
	            		degree = degree >= 0 ? degree % 360 : degree % 360 + 360; 
	            	}
	                currentDegree = degree; 
	                invalidate();  
	            } else {  
	            	currentDegree = brightnessDegress;  
	            }  
	        }  
			
			canvas.save();
			getSector(canvas, r, r, viewWidth / 2 + 6, -90, -90
					+ currentDegree);
			// canvas.drawColor(context.getResources().getColor(R.color.history_electricity_item_select_color));
			// // ��ɫ����
			canvas.drawBitmap(brightnessBitmap,
					(viewWidth - lightStateBitmap.getWidth()) / 2,
					(viewWidth - lightStateBitmap.getWidth()) / 2, paint);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			canvas.restore();

			paint.setColor(context.getResources().getColor(
					R.color.search_list_item_text_color));
			paint.setStrokeWidth(context.getResources()
					.getDimensionPixelOffset(R.dimen.light_bar_width));
			
			int barR = (lightStateBitmap.getWidth())/2;
			int barX = (int) (barR * (1 + Math.cos((-90 + currentDegree)
					* Math.PI / 180)));
			int barY = (int) (barR * (1 + Math.sin((-90 + currentDegree)
					* Math.PI / 180)));
			canvas.drawBitmap(lightBrightnessBarBitmap,
					barX,
					barY, paint);
			
		}

		if (lightInfo.getLightState()&&lightStateBitmap != null) {
			canvas.save();

			Path mPath = new Path();
			int radio = lightStateBitmap.getWidth()*210/632;
			float clipX =(float)(viewWidth/2 - radio*2*(progress*1.0/100)*Math.cos(30* Math.PI / 180));
			float clipY = (float)(viewWidth/2 - radio*2*(progress*1.0/100)*Math.sin(30* Math.PI / 180));
			//L.d("Math.cos(30)="+Math.cos(30* Math.PI / 180)+",Math.sin(30)="+Math.sin(30* Math.PI / 180));
			//L.d("viewWidth/2="+viewWidth/2+",radio="+radio+",clipX="+clipX+",clipY="+clipY);
			mPath.addCircle(clipX,
					clipY,radio,Path.Direction.CCW);
			
			canvas.clipPath(mPath,Region.Op.DIFFERENCE);
			canvas.drawBitmap(lightStateBitmap,
					(viewWidth - lightStateBitmap.getWidth()) / 2,
					(viewHeight - lightStateBitmap.getHeight()) / 2, paint);
			//canvas.drawColor(Color.WHITE);
			
			canvas.restore();
		}
		else{
			canvas.drawBitmap(lightStateBitmap,
					(viewWidth - lightStateBitmap.getWidth()) / 2,
					(viewHeight - lightStateBitmap.getHeight()) / 2, paint);
		}
		/*if (lightInfo.getLightState()) {
			paint.setStrokeWidth(3);
			paint.setColor(context.getResources().getColor(
					R.color.text_color_black));
			String text = progress + "%";
			int textHeight = viewHeight / 7;
			paint.setTextSize(textHeight);
			int textWidth = (int) paint.measureText(text, 0, text.length());
			paint.setStyle(Style.FILL);
			canvas.drawText(text, viewWidth / 2 - textWidth / 2, viewHeight / 2
					+ textHeight * 4 / 10, paint);
		}*/

	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	public void setMinProgress(int minProgress) {
		this.minProgress = minProgress;
		this.minDegress = (minProgress * 360 / 100);
	}

	public void setProgress(int progress) {
		this.progress = progress;
		this.invalidate();
	}

	public void setScale(float scale) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

	}

	/**
	 * �ǣգ��̵߳���
	 */
	public void setProgressNotInUiThread(int progress) {
		this.progress = progress;
		this.postInvalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startDownX = (int) event.getX();
			startDownY = (int) event.getY();
			isCanDimming = false;
			isInArea = true;
			accrossType = ACCROSS_NORMAL;
			if (clickRect.contains(startDownX, startDownY)) {
				isClickOnState = true;
				isInArea = false;
			}
			isFull = (brightnessDegress==360);
			break;
		case MotionEvent.ACTION_MOVE:
			currentX = (int) event.getX();
			currentY = (int) event.getY();
			if (isInArea) {
				if (isClickOnState == true) {
					isClickOnState = false;
				}
				int toDegress = computeCurrentAngle(currentX, currentY);
				//L.d("currentX:" +
				// currentX+",currentY="+currentY+",toDegress="+toDegress+",brightnessDegress="+brightnessDegress);
				if (toDegress >= 360) {
					toDegress = 360;
				} else if (toDegress <= 0) {
					toDegress = 0;
				}
				if (!isCanDimming) {
					if (Math.abs(toDegress - brightnessDegress) < 30) {
						isCanDimming = true;
					}
				}
				if (isCanDimming) {
					brightnessDegress = toDegress;
					// L.d("minDegress="+minDegress);
					if (brightnessDegress < minDegress) {
						brightnessDegress = minDegress;
					}
					progress = (int) (brightnessDegress * 100 / 360);
					if (brightnessDegress >= 360) {
						brightnessDegress = 360;
						progress = maxProgress;
					} else if (brightnessDegress <= 10) {
						brightnessDegress = 10;
						progress = 10;
					}
					if (onLightBrightnessChangeListener != null) {
						onLightBrightnessChangeListener
								.onLightBrightnessChanged(LightView.this, 0,
										progress, false);
					}
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			endUpX = (int) event.getX();
			endUpY = (int) event.getY();
			if (isClickOnState == true) {
				if (clickRect.contains(endUpX, endUpY)
						&& Math.abs(endUpX - startDownX) < 10
						&& Math.abs(endUpY - startDownY) < 10) {
					// changeLightInfo(!lightInfo.getLightState());
					if (onLightBrightnessChangeListener != null) {
						onLightBrightnessChangeListener.onLightStateChanged(
								LightView.this, 0, !lightInfo.getLightState());
					}
				}
				isClickOnState = false;
			} else {
				if (onLightBrightnessChangeListener != null) {
					onLightBrightnessChangeListener.onLightBrightnessChanged(
							LightView.this, 0, progress, true);
				}
			}
			break;
		}
		return (isCanDimming) ? false : true;
	}

	public void changeLightInfo(Boolean lightState) {
		this.lightInfo.setLightState(lightState);

		updateStateBg();
		this.invalidate();
	}

	private boolean isLongPressed(float lastX, float lastY, float curX,
			float curY, long lastDownTime, long curEventTime, long longPressTime) {
		float offsetX = Math.abs(curX - lastX);
		float offsetY = Math.abs(curY - lastY);
		long intervaltime = curEventTime - lastDownTime;
		if (offsetX <= 30 && intervaltime >= longPressTime) {
			return true;
		}
		return false;
	}

	private int computeCurrentAngle(float x, float y) {
		float distance = (float) Math.sqrt(((x - viewWidth / 2)
				* (x - viewWidth / 2) + (y - viewHeight / 2)
				* (y - viewHeight / 2)));
		int degree = (int) (Math.asin((y - viewHeight / 2) / distance) * 180 / Math.PI);
		int zone = whichZone(x - viewWidth / 2, y - viewHeight / 2);

		switch (zone) {
		case 0:
			degree = 90 + degree;
			break;
		case 1:
			degree = 90 + degree;
			break;
		case 2:
			degree = 270 - degree;
			break;
		case 3:
			degree = 270 - degree;
			break;
		}
		//L.d("degree:" + degree+",lastZone="+lastZone+",zone="+zone+",accrossType="+accrossType);
		if (lastZone != zone) {
			if (lastZone == 3 && zone == 0) {
				if (accrossType == ACCROSS_NORMAL) {// ���������,
					accrossType = ACCROSS_INCREATE;
				} else if (accrossType == ACCROSS_DECREATE) {
					accrossType = ACCROSS_NORMAL;
				}
			} else if (lastZone == 0 && zone == 3) {
				if (accrossType == ACCROSS_NORMAL) {// ���������,
					accrossType = ACCROSS_DECREATE;
				} else if (accrossType == ACCROSS_INCREATE) {
					accrossType = ACCROSS_NORMAL;
				}
				if(isFull){
					isFull = false;
					accrossType = ACCROSS_NORMAL;
				}
			}
			lastZone = zone;
		}
		if (accrossType == ACCROSS_INCREATE) {
			degree += 360;
		} else if (accrossType == ACCROSS_DECREATE) {
			degree -= 360;
		}
		
		return degree;
	}

	private int whichZone(float x, float y) {
		// �򵥵����޵㴦��
		// ��һ���������½ǣ��ڶ����������½ǣ��������������ʱ�룬������˳ʱ��
		if (x >= 0 && y >= 0) {
			return 1;
		} else if (x >= 0 && y <= 0) {
			return 0;
		} else if (x <= 0 && y <= 0) {
			return 3;
		} else if (x <= 0 && y >= 0) {
			return 2;
		}
		return -1;
	}

	public interface OnLightBrightnessChangeListener {
		public void onLightBrightnessChanged(LightView view, int lightIndex,
				int progress, boolean isSendNow);

		public void onLightStateChanged(LightView view, int lightIndex,
				Boolean lightState);
	}

	public interface LightTransformer {
		public void transformCanvas(Canvas canvas, float percentOpen);
	}

	private OnLightBrightnessChangeListener onLightBrightnessChangeListener;

	public void setOnLightBrightnessChangeListener(
			OnLightBrightnessChangeListener listener) {
		this.onLightBrightnessChangeListener = listener;
	}
}
