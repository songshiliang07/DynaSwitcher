package com.dynamicname.dynaswitcher;

import java.util.Vector;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

public class DynaProvider extends AppWidgetProvider {
	
	static final String TAG = "DynaProvider";
	
	private static final String TOGGLE_WIFI       = "TOGGLE_WIFI";
	private static final String TOGGLE_MOBILE     = "TOGGLE_MOBILE";
	private static final String TOGGLE_BLUETOOTH  = "TOGGLE_BLUETOOTH";
	private static final String TOGGLE_GPS        = "TOGGLE_GPS";
	private static final String TOGGLE_AIRPLANE   = "TOGGLE_AIRPLANE";
	private static final String TOGGLE_BRIGHTNESS = "TOGGLE_BRIGHTNESS";
	private static final String TOGGLE_SYNC       = "TOGGLE_SYNC";
	private static final String TOGGLE_ROTATION   = "TOGGLE_ROTATION";
	private static final String TOGGLE_RINGER     = "TOGGLE_RINGER";
	
	private static final String PREFS_NAME = "com.dynamicname.dynaswitcher";
	private static final String PREFS_1    = "_PREFS_1";
	private static final String PREFS_2    = "_PREFS_2";
	private static final String PREFS_3    = "_PREFS_3";
	private static final String PREFS_4    = "_PREFS_4";
	private static final String PREFS_5    = "_PREFS_5";
	private static final String PREFS_6    = "_PREFS_6";
	private static final String PREFS_7    = "_PREFS_7";
	private static final String PREFS_8    = "_PREFS_8";
	private static final String PREFS_9    = "_PREFS_9";
	
	private static final int [] drawable_wifi = {
		R.id.imagebutton1,
		R.drawable.ic_home_wifi_on,
		R.drawable.ic_home_wifi_off
	};
	private static final int [] drawable_mobiledata = {
		R.id.imagebutton2,
		R.drawable.ic_home_apn_on,
		R.drawable.ic_home_apn_off
	};
	private static final int [] drawable_bluetooth = {
		R.id.imagebutton3,
		R.drawable.ic_home_bluetooth_on,
		R.drawable.ic_home_bluetooth_off
	};
	private static final int [] drawable_gps = {
		R.id.imagebutton4,
		R.drawable.ic_home_gps_on,
		R.drawable.ic_home_gps_off
	};
	private static final int [] drawable_airplane = {
		R.id.imagebutton5,
		R.drawable.ic_home_airplane_on,
		R.drawable.ic_home_airplane_off
	};
	private static final int [] drawable_brightness = {
		R.id.imagebutton6,
		R.drawable.ic_home_brightness_auto,
		R.drawable.ic_home_brightness_off,
		R.drawable.ic_home_brightness_fairly,
		R.drawable.ic_home_brightness_on
	};
	private static final int [] states_brightness = {
		SwitchHelper.LIGHT_AUTO,
		SwitchHelper.LIGHT_25_PERCENT,
		SwitchHelper.LIGHT_50_PERCENT,
		SwitchHelper.LIGHT_100_PERCENT
	};
	private static final int [] drawable_sync = {
		R.id.imagebutton7,
		R.drawable.ic_home_sync_on,
		R.drawable.ic_home_sync_off
	};
	private static final int [] drawable_rotation = {
		R.id.imagebutton8,
		R.drawable.ic_home_rotate_on,
		R.drawable.ic_home_rotate_off
	};
	private static final int [] drawable_ringer = {
		R.id.imagebutton9,
		R.drawable.ic_home_sound_ring_on,
		R.drawable.ic_home_sound_silent,
		R.drawable.ic_home_sound_vibrate_on
	};
	private static final int [] states_ringer = {
		AudioManager.RINGER_MODE_NORMAL,
		AudioManager.RINGER_MODE_SILENT,
		AudioManager.RINGER_MODE_VIBRATE
	};
	
	static void update(RemoteViews views, boolean state, final int [] drawable) {
		if (state)
			views.setImageViewResource(drawable[0], drawable[1]);
		else
			views.setImageViewResource(drawable[0], drawable[2]);
	}
	
	static void update(RemoteViews views, int cur_state, final int [] states, final int [] drawable) {
		final int length = states.length;
		int index = 0;
		for(; index < length; ++index) {
			if (cur_state == states[index]) break;
		}
		//Log.v(TAG, "cur_state = " +cur_state);
		//Log.v(TAG, "states = " + Arrays.toString(states));
		//Log.v(TAG, "index = " + index);
		if (index >= 0 && index < length)
			views.setImageViewResource(drawable[0], drawable[index + 1]);
	}
	
	private static ContentObserver mobiledata_observer = null;
	private static ContentObserver brightness_observer = null;
	private static ContentObserver rotate_observer = null;
	private static Vector<IntentHandler> v = null;
	
	synchronized private static void initObserver(
			final Context context,
			final AppWidgetManager awm,
			final Class<?> cls,
			BroadcastReceiver receiver) {
		if (null == mobiledata_observer) {
			mobiledata_observer = new ContentObserver(new Handler()) {
				public void onChange(boolean selfChange) {
					super.onChange(selfChange);
					RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
					update(
							views,
							SwitchHelper.checkMobileData(context),
							drawable_mobiledata);
					awm.updateAppWidget(new ComponentName(context, cls), views);
				}
			};
			context.getContentResolver().registerContentObserver(
					Settings.Global.getUriFor("mobile_data"), false, mobiledata_observer);
		}
		if (null == brightness_observer) {
			brightness_observer = new ContentObserver(new Handler()) {
				public void onChange(boolean selfChange) {
					super.onChange(selfChange);
					RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
					update(
							views,
							SwitchHelper.getBrightness(context),
							states_brightness,
							drawable_brightness);
					awm.updateAppWidget(new ComponentName(context, cls), views);
				}
			};
			context.getContentResolver().registerContentObserver(
					Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, brightness_observer);
			context.getContentResolver().registerContentObserver(
					Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), false, brightness_observer);
		}
		if (null == rotate_observer) {
			rotate_observer = new ContentObserver(new Handler()) {
				public void onChange(boolean selfChange) {
					super.onChange(selfChange);
					RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
					update(
							views,
							SwitchHelper.checkRotation(context),
							drawable_rotation
							);
					awm.updateAppWidget(new ComponentName(context, cls), views);
				}
			};
			context.getContentResolver().registerContentObserver(
					Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, rotate_observer);
		}
	}
	
	synchronized private static void finalObserver(Context context, BroadcastReceiver receiver) {
		if (null != mobiledata_observer) {
			//context.unregisterReceiver(receiver);
			context.getContentResolver().unregisterContentObserver(mobiledata_observer);
			mobiledata_observer = null;
		}
		if (null != brightness_observer) {
			context.getContentResolver().unregisterContentObserver(brightness_observer);
			brightness_observer = null;
		}
		if (null != rotate_observer) {
			context.getContentResolver().unregisterContentObserver(rotate_observer);
			rotate_observer = null;
		}
	}
	
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		finalObserver(context, this);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager awm, int[] appWidgetIds) {
		super.onUpdate(context, awm, appWidgetIds);
		
		initObserver(context, awm, this.getClass(), this);
		
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Log.v(TAG, String.valueOf(settings.getInt(PREFS_1, 1)));
		Log.v(TAG, String.valueOf(settings.getInt(PREFS_2, 2)));
		Log.v(TAG, String.valueOf(settings.getInt(PREFS_3, 3)));
		Log.v(TAG, String.valueOf(settings.getInt(PREFS_4, 4)));
		Log.v(TAG, String.valueOf(settings.getInt(PREFS_5, 5)));
		Log.v(TAG, String.valueOf(settings.getInt(PREFS_6, 6)));
		Log.v(TAG, String.valueOf(settings.getInt(PREFS_7, 7)));
		Log.v(TAG, String.valueOf(settings.getInt(PREFS_8, 8)));
		Log.v(TAG, String.valueOf(settings.getInt(PREFS_9, 9)));
		
		v = new Vector<IntentHandler>();
		try {
			Class<?> cls = Class.forName("com.dynamicname.dynaswitcher.SwitchHelper");
			v.add(new IntentHandler(
					context,
					this.getClass(),
					TOGGLE_WIFI,
					WifiManager.WIFI_STATE_CHANGED_ACTION,
					cls.getMethod("checkWifi", Context.class),
					null,
					drawable_wifi));
			v.add(new IntentHandler(
					context,
					this.getClass(),
					TOGGLE_MOBILE,
					null,
					cls.getMethod("checkMobileData", Context.class),
					null,
					drawable_mobiledata));
			v.add(new IntentHandler(
					context,
					this.getClass(),
					TOGGLE_BLUETOOTH,
					BluetoothAdapter.ACTION_STATE_CHANGED,
					cls.getMethod("checkBluetooth", Context.class),
					null,
					drawable_bluetooth));
			v.add(new IntentHandler(
					context,
					this.getClass(),
					TOGGLE_GPS,
					LocationManager.PROVIDERS_CHANGED_ACTION,
					cls.getMethod("checkGPS", Context.class),
					null,
					drawable_gps));
			v.add(new IntentHandler(
					context,
					this.getClass(),
					TOGGLE_AIRPLANE,
					Intent.ACTION_AIRPLANE_MODE_CHANGED,
					cls.getMethod("checkAirplane", Context.class),
					null,
					drawable_airplane));
			v.add(new IntentHandler(
					context,
					this.getClass(),
					TOGGLE_BRIGHTNESS,
					null,
					cls.getMethod("getBrightness", Context.class),
					states_brightness,
					drawable_brightness));
			v.add(new IntentHandler(
					context,
					this.getClass(),
					TOGGLE_SYNC,
					"com.android.sync.SYNC_CONN_STATUS_CHANGED",
					cls.getMethod("checkSync", Context.class),
					null,
					drawable_sync));
			v.add(new IntentHandler(
					context,
					this.getClass(),
					TOGGLE_ROTATION,
					null,
					cls.getMethod("checkRotation", Context.class),
					null,
					drawable_rotation));
			v.add(new IntentHandler(
					context,
					this.getClass(),
					TOGGLE_RINGER,
					AudioManager.RINGER_MODE_CHANGED_ACTION,
					cls.getMethod("getRinger", Context.class),
					states_ringer,
					drawable_ringer));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		final int N = appWidgetIds.length;
		for(int i = 0; i < N; ++i) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			
			final int size = v.size();
			for(int j = 0; j < size; ++j) {
				v.get(j).onUpdate(views);
			}
			
			awm.updateAppWidget(appWidgetIds[i], views);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		AppWidgetManager awm = AppWidgetManager.getInstance(context);
		initObserver(context, awm, this.getClass(), this);
		
		final String action = intent.getAction();
		Log.v(TAG, intent.toString());
		if (TOGGLE_WIFI.equals(action)) {
			SwitchHelper.toggleWiFi(context);
		}
		else if (TOGGLE_MOBILE.equals(action)) {
			SwitchHelper.toggleMobileData(context);
		}
		else if (TOGGLE_BLUETOOTH.equals(action)) {
			intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		else if (TOGGLE_GPS.equals(action)) {
			intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		else if (TOGGLE_AIRPLANE.equals(action)) {
			intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		else if (TOGGLE_BRIGHTNESS.equals(action)) {
			SwitchHelper.toggleBrightness(context);
		}
		else if (TOGGLE_SYNC.equals(action)) {
			SwitchHelper.toggleSync(context);
		}
		else if (TOGGLE_ROTATION.equals(action)) {
			SwitchHelper.toggleRotation(context);
		}
		else if (TOGGLE_RINGER.equals(action)) {
			SwitchHelper.toggleRinger(context);
		}

		if (null != v) {
			final int size = v.size();
			for(int j = 0; j < size; ++j) {
				v.get(j).onReceive(awm, action);
			}
		}
	}

}
