package com.dynamicname.dynaswitcher;

import java.util.Arrays;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;

public class DynaProvider extends AppWidgetProvider {

	static final String TAG = "DynaProvider";

	private static final String TOGGLE_WIFI       = "TOGGLE_WIFI";
	private static final int    KEY_WIFI          = 0;
	private static final String TOGGLE_MOBILE     = "TOGGLE_MOBILE";
	private static final int    KEY_MOBILE        = 1;
	private static final String TOGGLE_BLUETOOTH  = "TOGGLE_BLUETOOTH";
	private static final int    KEY_BLUETOOTH     = 2;
	private static final String TOGGLE_GPS        = "TOGGLE_GPS";
	private static final int    KEY_GPS           = 3;
	private static final String TOGGLE_AIRPLANE   = "TOGGLE_AIRPLANE";
	private static final int    KEY_AIRPLANE      = 4;
	private static final String TOGGLE_BRIGHTNESS = "TOGGLE_BRIGHTNESS";
	private static final int    KEY_BRIGHTNESS    = 5;
	private static final String TOGGLE_SYNC       = "TOGGLE_SYNC";
	private static final int    KEY_SYNC          = 6;
	private static final String TOGGLE_ROTATION   = "TOGGLE_ROTATION";
	private static final int    KEY_ROTATION      = 7;
	private static final String TOGGLE_RINGER     = "TOGGLE_RINGER";
	private static final int    KEY_RINGER        = 8;
	public static final int [] default_button_vector = {
		KEY_WIFI,
		KEY_MOBILE,
		KEY_BLUETOOTH,
		KEY_GPS,
		KEY_AIRPLANE,
		KEY_BRIGHTNESS,
		KEY_SYNC,
		KEY_ROTATION,
		KEY_RINGER
	};

	private static final int [] drawable_wifi = {
		R.drawable.ic_home_wifi_on,
		R.drawable.ic_home_wifi_off
	};
	private static final int [] drawable_mobiledata = {
		R.drawable.ic_home_apn_on,
		R.drawable.ic_home_apn_off
	};
	private static final int [] drawable_bluetooth = {
		R.drawable.ic_home_bluetooth_on,
		R.drawable.ic_home_bluetooth_off
	};
	private static final int [] drawable_gps = {
		R.drawable.ic_home_gps_on,
		R.drawable.ic_home_gps_off
	};
	private static final int [] drawable_airplane = {
		R.drawable.ic_home_airplane_on,
		R.drawable.ic_home_airplane_off
	};
	private static final int [] drawable_brightness = {
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
		R.drawable.ic_home_sync_on,
		R.drawable.ic_home_sync_off
	};
	private static final int [] drawable_rotation = {
		R.drawable.ic_home_rotate_on,
		R.drawable.ic_home_rotate_off
	};
	private static final int [] drawable_ringer = {
		R.drawable.ic_home_sound_ring_on,
		R.drawable.ic_home_sound_silent,
		R.drawable.ic_home_sound_vibrate_on
	};
	private static final int [] states_ringer = {
		AudioManager.RINGER_MODE_NORMAL,
		AudioManager.RINGER_MODE_SILENT,
		AudioManager.RINGER_MODE_VIBRATE
	};
	public static final int [] resource_ids = {
		R.id.imagebutton1,
		R.id.imagebutton2,
		R.id.imagebutton3,
		R.id.imagebutton4,
		R.id.imagebutton5,
		R.id.imagebutton6,
		R.id.imagebutton7,
		R.id.imagebutton8,
		R.id.imagebutton9
	};

	private static ContentObserver mobiledata_observer = null;
	private static ContentObserver brightness_observer = null;
	private static ContentObserver rotate_observer = null;
	public  static SparseArray<DynaHandler> handler_map = null;

	@Override
	public void onUpdate(Context context, AppWidgetManager awm, int[] appWidgetIds) {
		Log.v(TAG, "onUpdate");
		super.onUpdate(context, awm, appWidgetIds);

		init(context, awm, this.getClass());

		final int N = appWidgetIds.length;
		for(int i = 0; i < N; ++i) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

			int[] button_vector = DynaPrefs.loadPrefs(context, appWidgetIds[i]);
			if (null != button_vector) {
				final int size = button_vector.length;
				for(int j = 0; j < size; ++j) {
					handler_map.get(button_vector[j]).onUpdate(context, this.getClass(), views, resource_ids[j]);
				}
			}

			awm.updateAppWidget(appWidgetIds[i], views);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "onReceive");
		super.onReceive(context, intent);

		AppWidgetManager awm = AppWidgetManager.getInstance(context);

		final String action = intent.getAction();
		Log.v(TAG, intent.toString());
		if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)
				|| AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)
				|| AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)
				|| AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)
				|| AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED.equals(action)) {
			return;
		}
		else if (TOGGLE_WIFI.equals(action)) {
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
		else {
			if (null == handler_map) {
				Log.v(TAG, "handler_map null");
				init(context, awm, this.getClass());
			}
			SparseArray<int[]> widget_settings = DynaPrefs.loadPrefs(context);
			final int length = widget_settings.size();
			for(int i = 0; i < length; ++i) {
				int[] button_vector = widget_settings.valueAt(i);
				final int size = button_vector.length;
				for(int j = 0; j < size; ++j) {
					handler_map.get(button_vector[j]).onReceive(context, awm, widget_settings.keyAt(i), resource_ids[j], action);
				}
			}
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.v(TAG, "onDeleted");
		super.onDeleted(context, appWidgetIds);
		final int size = appWidgetIds.length;
		for(int i = 0; i < size; ++i) {
			DynaPrefs.deletePrefs(context, appWidgetIds[i]);
		}
	}

	@Override
	public void onEnabled(Context context) {
		Log.v(TAG, "onEnabled");
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		Log.v(TAG, "onDisabled");
		super.onDisabled(context);
		destroy(context);
	}

	synchronized private static void init(
			final Context context,
			final AppWidgetManager awm,
			final Class<?> cls_provider) {
		if (null == handler_map) {
			handler_map = new SparseArray<DynaHandler>();
			try {
				Class<?> cls = Class.forName("com.dynamicname.dynaswitcher.SwitchHelper");
				handler_map.put(
						KEY_WIFI,
						new DynaHandler(TOGGLE_WIFI,
								WifiManager.WIFI_STATE_CHANGED_ACTION,
								cls.getMethod("checkWifi", Context.class),
								null,
								drawable_wifi));
				handler_map.put(
						KEY_MOBILE,
						new DynaHandler(
								TOGGLE_MOBILE,
								null,
								cls.getMethod("checkMobileData", Context.class),
								null,
								drawable_mobiledata));
				handler_map.put(
						KEY_BLUETOOTH,
						new DynaHandler(
								TOGGLE_BLUETOOTH,
								BluetoothAdapter.ACTION_STATE_CHANGED,
								cls.getMethod("checkBluetooth", Context.class),
								null,
								drawable_bluetooth));
				handler_map.put(
						KEY_GPS,
						new DynaHandler(
								TOGGLE_GPS,
								LocationManager.PROVIDERS_CHANGED_ACTION,
								cls.getMethod("checkGPS", Context.class),
								null,
								drawable_gps));
				handler_map.put(
						KEY_AIRPLANE,
						new DynaHandler(
								TOGGLE_AIRPLANE,
								Intent.ACTION_AIRPLANE_MODE_CHANGED,
								cls.getMethod("checkAirplane", Context.class),
								null,
								drawable_airplane));
				handler_map.put(
						KEY_BRIGHTNESS,
						new DynaHandler(
								TOGGLE_BRIGHTNESS,
								null,
								cls.getMethod("getBrightness", Context.class),
								states_brightness,
								drawable_brightness));
				handler_map.put(
						KEY_SYNC,
						new DynaHandler(
								TOGGLE_SYNC,
								"com.android.sync.SYNC_CONN_STATUS_CHANGED",
								cls.getMethod("checkSync", Context.class),
								null,
								drawable_sync));
				handler_map.put(
						KEY_ROTATION,
						new DynaHandler(
								TOGGLE_ROTATION,
								null,
								cls.getMethod("checkRotation", Context.class),
								null,
								drawable_rotation));
				handler_map.put(
						KEY_RINGER,
						new DynaHandler(
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
		}

		if (null == mobiledata_observer) {
			mobiledata_observer = new DynaObserver(new Handler(), context, KEY_MOBILE);
			context.getContentResolver().registerContentObserver(
					Settings.Global.getUriFor("mobile_data"), false, mobiledata_observer);
		}
		if (null == brightness_observer) {
			brightness_observer = new DynaObserver(new Handler(), context, KEY_BRIGHTNESS);
			context.getContentResolver().registerContentObserver(
					Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, brightness_observer);
			context.getContentResolver().registerContentObserver(
					Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), false, brightness_observer);
		}
		if (null == rotate_observer) {
			rotate_observer = new DynaObserver(new Handler(), context, KEY_ROTATION);
			context.getContentResolver().registerContentObserver(
					Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, rotate_observer);
		}
	}

	synchronized private static void destroy(Context context) {
		DynaPrefs.clearPrefs(context);
		if (null != handler_map) {
			handler_map.clear();
			handler_map = null;
		}
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

	static public void updateAll(Context context, int [] vec) {
		if (null != handler_map) {
			AppWidgetManager awm = AppWidgetManager.getInstance(context);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			SparseArray<int[]> widget_settings = DynaPrefs.loadPrefs(context);
			final int length = widget_settings.size();
			for(int i = 0; i < length; ++i) {
				Log.v(TAG, "before " + Arrays.toString(widget_settings.valueAt(i)));
				widget_settings.setValueAt(i, vec);
				Log.v(TAG, "after  " + Arrays.toString(widget_settings.valueAt(i)));
				int[] button_vector = widget_settings.valueAt(i);
				final int size = button_vector.length;
				for(int j = 0; j < size; ++j) {
					handler_map.get(button_vector[j]).onUpdate(context, DynaProvider.class, views, resource_ids[j]);
					awm.updateAppWidget(widget_settings.keyAt(i), views);
				}
			}
		}
	}

	static public void update(Context context, int mAppWidgetId, int [] vec) {
		if (AppWidgetManager.INVALID_APPWIDGET_ID != mAppWidgetId
				&& null != vec) {
			AppWidgetManager awm = AppWidgetManager.getInstance(context);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			Log.v(TAG, "before " + Arrays.toString(DynaPrefs.loadPrefs(context, mAppWidgetId)));
			DynaPrefs.savePrefs(context, mAppWidgetId, vec);
			Log.v(TAG, "after  " + Arrays.toString(DynaPrefs.loadPrefs(context, mAppWidgetId)));
			int[] button_vector = DynaPrefs.loadPrefs(context, mAppWidgetId);
			final int size = button_vector.length;
			for(int j = 0; j < size; ++j) {
				handler_map.get(button_vector[j]).onUpdate(context, DynaProvider.class, views, resource_ids[j]);
				awm.updateAppWidget(mAppWidgetId, views);
			}
		}
	}

}
