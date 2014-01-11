package com.dynamicname.dynaswitcher;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class DynaProvider extends AppWidgetProvider {

	static final String TAG = "DynaProvider";

	public static final String TOGGLE_WIFI       = "TOGGLE_WIFI";
	public static final int    KEY_WIFI          = 0;
	public static final String TOGGLE_MOBILE     = "TOGGLE_MOBILE";
	public static final int    KEY_MOBILE        = 1;
	public static final String TOGGLE_BLUETOOTH  = "TOGGLE_BLUETOOTH";
	public static final int    KEY_BLUETOOTH     = 2;
	public static final String TOGGLE_GPS        = "TOGGLE_GPS";
	public static final int    KEY_GPS           = 3;
	public static final String TOGGLE_AIRPLANE   = "TOGGLE_AIRPLANE";
	public static final int    KEY_AIRPLANE      = 4;
	public static final String TOGGLE_BRIGHTNESS = "TOGGLE_BRIGHTNESS";
	public static final int    KEY_BRIGHTNESS    = 5;
	public static final String TOGGLE_SYNC       = "TOGGLE_SYNC";
	public static final int    KEY_SYNC          = 6;
	public static final String TOGGLE_ROTATION   = "TOGGLE_ROTATION";
	public static final int    KEY_ROTATION      = 7;
	public static final String TOGGLE_RINGER     = "TOGGLE_RINGER";
	public static final int    KEY_RINGER        = 8;
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

	@Override
	public void onUpdate(Context context, AppWidgetManager awm, int[] appWidgetIds) {
		Log.v(TAG, "onUpdate");
		super.onUpdate(context, awm, appWidgetIds);

		DynaHandlerMap.init(context, awm);

		final int N = appWidgetIds.length;
		for(int i = 0; i < N; ++i) {
			DynaHandlerMap.update(context, awm, appWidgetIds[i]);
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
			DynaHandlerMap.init(context, awm);
			DynaHandlerMap.receive(context, awm, action);
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
		DynaHandlerMap.destroy(context);
	}

}
