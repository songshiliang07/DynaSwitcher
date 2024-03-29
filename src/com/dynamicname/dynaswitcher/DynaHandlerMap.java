package com.dynamicname.dynaswitcher;

import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.SparseArray;
import android.widget.RemoteViews;

public final class DynaHandlerMap {

	static final String TAG = "DynaHandlerMap";

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
		R.id.imageview1,
		R.id.imageview2,
		R.id.imageview3,
		R.id.imageview4,
		R.id.imageview5,
		R.id.imageview6,
		R.id.imageview7,
		R.id.imageview8,
		R.id.imageview9
	};

	private static ContentObserver mobiledata_observer = null;
	private static ContentObserver brightness_observer = null;
	private static ContentObserver rotate_observer = null;
	private static SparseArray<DynaHandler> handler_map = null;
	
	synchronized public static int getCount() {
		if (null == handler_map)
			return 0;
		else
			return handler_map.size();
	}
	
	synchronized public static DynaHandler getHandler(int position) {
		if (null == handler_map)
			return null;
		else
			return handler_map.valueAt(position);
	}
	
	synchronized public static void init(Context context) {
		if (null == handler_map) {
			handler_map = new SparseArray<DynaHandler>();
			try {
				Class<?> cls = Class.forName("com.dynamicname.dynaswitcher.SwitchHelper");
				Resources res = context.getResources();
				handler_map.put(
						DynaProvider.KEY_WIFI,
						new DynaHandler(
								DynaProvider.KEY_WIFI,
								res.getString(R.string.toggle_wifi),
								DynaProvider.ACTION_WIFI,
								WifiManager.WIFI_STATE_CHANGED_ACTION,
								cls.getMethod("checkWifi", Context.class),
								null,
								drawable_wifi));
				handler_map.put(
						DynaProvider.KEY_MOBILE,
						new DynaHandler(
								DynaProvider.KEY_MOBILE,
								res.getString(R.string.toggle_mobiledata),
								DynaProvider.ACTION_MOBILE,
								null,
								cls.getMethod("checkMobileData", Context.class),
								null,
								drawable_mobiledata));
				handler_map.put(
						DynaProvider.KEY_BLUETOOTH,
						new DynaHandler(
								DynaProvider.KEY_BLUETOOTH,
								res.getString(R.string.toggle_bluetooth),
								DynaProvider.ACTION_BLUETOOTH,
								BluetoothAdapter.ACTION_STATE_CHANGED,
								cls.getMethod("checkBluetooth", Context.class),
								null,
								drawable_bluetooth));
				handler_map.put(
						DynaProvider.KEY_GPS,
						new DynaHandler(
								DynaProvider.KEY_GPS,
								res.getString(R.string.toggle_gps),
								DynaProvider.ACTION_GPS,
								LocationManager.PROVIDERS_CHANGED_ACTION,
								cls.getMethod("checkGPS", Context.class),
								null,
								drawable_gps));
				handler_map.put(
						DynaProvider.KEY_AIRPLANE,
						new DynaHandler(
								DynaProvider.KEY_AIRPLANE,
								res.getString(R.string.toggle_airplane),
								DynaProvider.ACTION_AIRPLANE,
								Intent.ACTION_AIRPLANE_MODE_CHANGED,
								cls.getMethod("checkAirplane", Context.class),
								null,
								drawable_airplane));
				handler_map.put(
						DynaProvider.KEY_BRIGHTNESS,
						new DynaHandler(
								DynaProvider.KEY_BRIGHTNESS,
								res.getString(R.string.toggle_brightness),
								DynaProvider.ACTION_BRIGHTNESS,
								null,
								cls.getMethod("getBrightness", Context.class),
								states_brightness,
								drawable_brightness));
				handler_map.put(
						DynaProvider.KEY_SYNC,
						new DynaHandler(
								DynaProvider.KEY_SYNC,
								res.getString(R.string.toggle_sync),
								DynaProvider.ACTION_SYNC,
								"com.android.sync.SYNC_CONN_STATUS_CHANGED",
								cls.getMethod("checkSync", Context.class),
								null,
								drawable_sync));
				handler_map.put(
						DynaProvider.KEY_ROTATION,
						new DynaHandler(
								DynaProvider.KEY_ROTATION,
								res.getString(R.string.toggle_rotation),
								DynaProvider.ACTION_ROTATION,
								null,
								cls.getMethod("checkRotation", Context.class),
								null,
								drawable_rotation));
				handler_map.put(
						DynaProvider.KEY_RINGER,
						new DynaHandler(
								DynaProvider.KEY_RINGER,
								res.getString(R.string.toggle_ringer),
								DynaProvider.ACTION_RINGER,
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
			mobiledata_observer = new DynaObserver(new Handler(), context, DynaProvider.KEY_MOBILE);
			context.getContentResolver().registerContentObserver(
					Settings.Global.getUriFor("mobile_data"), false, mobiledata_observer);
		}
		if (null == brightness_observer) {
			brightness_observer = new DynaObserver(new Handler(), context, DynaProvider.KEY_BRIGHTNESS);
			context.getContentResolver().registerContentObserver(
					Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, brightness_observer);
			context.getContentResolver().registerContentObserver(
					Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), false, brightness_observer);
		}
		if (null == rotate_observer) {
			rotate_observer = new DynaObserver(new Handler(), context, DynaProvider.KEY_ROTATION);
			context.getContentResolver().registerContentObserver(
					Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false, rotate_observer);
		}
	}

	synchronized public static void destroy(Context context) {
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

	synchronized public static void update(Context context, AppWidgetManager awm, int mAppWidgetId) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

		int[] button_vector = DynaPrefs.loadPrefs(context, mAppWidgetId);
		if (null != button_vector) {
			final int size = button_vector.length;
			for(int j = 0; j < size; ++j) {
				handler_map.get(button_vector[j]).onUpdate(context, DynaProvider.class, views, resource_ids[j]);
			}
		}

		awm.updateAppWidget(mAppWidgetId, views);
	}

	static public void update(Context context, int mAppWidgetId, int [] vec) {
		if (AppWidgetManager.INVALID_APPWIDGET_ID != mAppWidgetId
				&& null != vec) {
			AppWidgetManager awm = AppWidgetManager.getInstance(context);
			//Log.v(TAG, "before : " + mAppWidgetId + " " + Arrays.toString(DynaPrefs.loadPrefs(context, mAppWidgetId)));
			DynaPrefs.savePrefs(context, mAppWidgetId, vec);
			//Log.v(TAG, "after  : " + mAppWidgetId + " " + Arrays.toString(DynaPrefs.loadPrefs(context, mAppWidgetId)));
			DynaHandlerMap.update(context, awm, mAppWidgetId);
		}
	}

	static public void update(Context context, int [] vec) {
		if (null != handler_map) {
			AppWidgetManager awm = AppWidgetManager.getInstance(context);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			SparseArray<int[]> widget_settings = DynaPrefs.loadPrefs(context);
			final int length = widget_settings.size();
			for(int i = 0; i < length; ++i) {
				//Log.v(TAG, "before : " + widget_settings.keyAt(i) + " " + Arrays.toString(widget_settings.valueAt(i)));
				widget_settings.setValueAt(i, vec);
				//Log.v(TAG, "after  : " + widget_settings.keyAt(i) + " " + Arrays.toString(widget_settings.valueAt(i)));
				int[] button_vector = widget_settings.valueAt(i);
				final int size = button_vector.length;
				for(int j = 0; j < size; ++j) {
					handler_map.get(button_vector[j]).onUpdate(context, DynaProvider.class, views, resource_ids[j]);
					awm.updateAppWidget(widget_settings.keyAt(i), views);
				}
			}
		}
	}

	synchronized public static void receive(Context context, AppWidgetManager awm, final String action) {
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

	synchronized public static void receive(Context context, int type) {
		if (null != handler_map) {
			SparseArray<int[]> widget_settings = DynaPrefs.loadPrefs(context);
			final int size = widget_settings.size();
			for(int i = 0; i < size; ++i) {
				int[] button_vector = widget_settings.valueAt(i);
				final int length = button_vector.length;
				for(int j = 0; j < length; ++j) {
					if (type == button_vector[j]) {
						AppWidgetManager awm = AppWidgetManager.getInstance(context);
						handler_map.get(button_vector[j]).onReceive(context, awm, widget_settings.keyAt(i), resource_ids[j]);
					}
				}
			}
		}
	}

}
