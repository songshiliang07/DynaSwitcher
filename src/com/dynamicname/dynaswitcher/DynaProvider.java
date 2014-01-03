package com.dynamicname.dynaswitcher;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
	private static final String TOGGLE_BRIGHTNESS = "TOGGLE_BRIGHTNESS";
	private static final String TOGGLE_SYNC       = "TOGGLE_SYNC";
	private static final String TOGGLE_ROTATION   = "TOGGLE_ROTATION";
	private static final String TOGGLE_RINGER     = "TOGGLE_RINGER";
	
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
	private static final int [] drawable_rotate = {
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
	
	private static void updateEnableOrDisable(RemoteViews views, boolean state, int [] drawable) {
		if (state)
			views.setImageViewResource(drawable[0], drawable[1]);
		else
			views.setImageViewResource(drawable[0], drawable[2]);
	}
	
	private static void updateManyState(RemoteViews views, int cur_state, int [] states, int [] drawable) {
		final int length = states.length;
		int index = 0;
		for(; index < length; ++index) {
			if (cur_state == states[index]) break;
		}
		//Log.v(TAG, "cur_state = " +cur_state);
		//Log.v(TAG, "states = " + Arrays.toString(states));
		//Log.v(TAG, "index = " + index);
		if (index >= 0 && index < (drawable.length - 1))
			views.setImageViewResource(drawable[0], drawable[index + 1]);
	}
	
	private static ContentObserver mobiledata_observer = null;
	private static ContentObserver brightness_observer = null;
	private static ContentObserver rotate_observer = null;
	
	synchronized private static void initObserver(
			final Context context,
			final AppWidgetManager wm,
			final Class<?> cls) {
		if (null == mobiledata_observer) {
			mobiledata_observer = new ContentObserver(new Handler()) {
				public void onChange(boolean selfChange) {
					super.onChange(selfChange);
					RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
					updateEnableOrDisable(
							views,
							SwitchHelper.checkMobileData(context),
							drawable_mobiledata);
					wm.updateAppWidget(new ComponentName(context, cls), views);
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
					updateManyState(
							views,
							SwitchHelper.getBrightness(context),
							states_brightness,
							drawable_brightness);
					wm.updateAppWidget(new ComponentName(context, cls), views);
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
					updateEnableOrDisable(
							views,
							SwitchHelper.checkRotation(context),
							drawable_rotate
							);
					wm.updateAppWidget(new ComponentName(context, cls), views);
				}
			};
			context.getContentResolver().registerContentObserver(
					Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, rotate_observer);
		}
	}
	
	synchronized private static void finalObserver(Context context) {
		if (null != mobiledata_observer) {
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
		finalObserver(context);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager wm, int[] appWidgetIds) {
		super.onUpdate(context, wm, appWidgetIds);
		
		initObserver(context, wm, this.getClass());
		
		final int N = appWidgetIds.length;
		for(int i = 0; i < N; ++i) {
			Intent intent = null;
			PendingIntent pendingIntent = null;
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			
			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_WIFI);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton1, pendingIntent);
			updateEnableOrDisable(
					views,
					SwitchHelper.checkWifi(context),
					drawable_wifi);

			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_MOBILE);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton2, pendingIntent);
			updateEnableOrDisable(
					views,
					SwitchHelper.checkMobileData(context),
					drawable_mobiledata);
			
			intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
			pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton3, pendingIntent);
			updateEnableOrDisable(
					views,
					SwitchHelper.checkBluetooth(context),
					drawable_bluetooth);
			
			intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton4, pendingIntent);
			updateEnableOrDisable(
					views,
					SwitchHelper.checkGPS(context),
					drawable_gps);
			
			intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
			pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton5, pendingIntent);
			updateEnableOrDisable(
					views,
					SwitchHelper.checkAirplane(context),
					drawable_airplane);
			
			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_BRIGHTNESS);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton6, pendingIntent);
			updateManyState(
					views,
					SwitchHelper.getBrightness(context),
					states_brightness,
					drawable_brightness);
			
			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_SYNC);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton7, pendingIntent);
			updateEnableOrDisable(
					views,
					SwitchHelper.checkSync(context),
					drawable_sync);
			
			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_ROTATION);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton8, pendingIntent);
			updateEnableOrDisable(
					views,
					SwitchHelper.checkRotation(context),
					drawable_rotate
					);
			
			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_RINGER);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton9, pendingIntent);
			updateManyState(
					views,
					((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode(),
					states_ringer,
					drawable_ringer);

			//intent = new Intent(context, MainActivity.class);
			//pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			//views.setOnClickPendingIntent(R.id.imagebutton7, pendingIntent);
			
			wm.updateAppWidget(appWidgetIds[i], views);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		AppWidgetManager wm = AppWidgetManager.getInstance(context);
		initObserver(context, wm, this.getClass());
		
		final String action = intent.getAction();
		Log.v(TAG, intent.toString());
		if (TOGGLE_WIFI.equals(action)) {
			SwitchHelper.toggleWiFi(context);
		}
		else if (TOGGLE_MOBILE.equals(action)) {
			SwitchHelper.toggleMobileData(context);
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
		else if (WifiManager.WIFI_STATE_CHANGED_ACTION/*"android.net.wifi.WIFI_STATE_CHANGE"*/.equals(action)) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			int wifi_state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
			//int wifi_previous_state = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
			//Log.v(TAG, "WIFI_STATE " + wifi_previous_state + " -> " + wifi_state);
			updateEnableOrDisable(
					views,
					WifiManager.WIFI_STATE_ENABLED == wifi_state,
					drawable_wifi);
			wm.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
		else if (BluetoothAdapter.ACTION_STATE_CHANGED/*"android.bluetooth.adapter.action.STATE_CHANGED"*/.equals(action)) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			//Log.v(TAG, BluetoothAdapter.ACTION_STATE_CHANGED
			//		+ " "
			//		+ intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF)
			//		+ " -> "
			//		+ intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF));
			updateEnableOrDisable(
					views,
					SwitchHelper.checkBluetooth(context),
					drawable_bluetooth);
			wm.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
		else if (LocationManager.PROVIDERS_CHANGED_ACTION/*"android.location.PROVIDERS_CHANGED"*/.equals(action)) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			updateEnableOrDisable(
					views,
					SwitchHelper.checkGPS(context),
					drawable_gps);
			wm.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
		else if (Intent.ACTION_AIRPLANE_MODE_CHANGED/*"android.intent.action.AIRPLANE_MODE"*/.equals(action)) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			boolean enabled = intent.getBooleanExtra("state", true);
			//Log.v(TAG, "state = " + enabled);
			updateEnableOrDisable(
					views,
					enabled,
					drawable_airplane);
			wm.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
		else if ("com.android.sync.SYNC_CONN_STATUS_CHANGED".equals(action)) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			updateEnableOrDisable(
					views,
					SwitchHelper.checkSync(context),
					drawable_sync);
			wm.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
		else if (AudioManager.RINGER_MODE_CHANGED_ACTION/*"android.media.RINGER_MODE_CHANGED"*/.equals(action)) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			int mode = intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, AudioManager.RINGER_MODE_NORMAL);
			//Log.v(TAG, "mode = " + mode);
			updateManyState(
					views,
					mode,
					states_ringer,
					drawable_ringer);
			wm.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
	}

}
