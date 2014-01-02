package com.dynamicname.dynaswitcher;

import java.util.Observable;
import java.util.Observer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentQueryMap;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

public class DynaProvider extends AppWidgetProvider {
	
	static final String TAG = "DynaProvider";
	static final String TOGGLE_WIFI = "TOGGLE_WIFI";
	static final String TOGGLE_MOBILE = "TOGGLE_MOBILE";
	static final String TOGGLE_BRIGHTNESS = "TOGGLE_BRIGHTNESS";
	static final String TOGGLE_SYNC = "TOGGLE_SYNC";
	static final String TOGGLE_ROTATION = "TOGGLE_ROTATION";
	static final String TOGGLE_RINGER = "TOGGLE_RINGER";
	
	final class DynaObserver implements Observer {

		final Context context;
		final Class<?> cls;
		
		public DynaObserver(Context c, Class<?> cs) {
			context = c;
			cls = cs;
		}
		
		@Override
		public void update(Observable observable, Object obj) {
			if (null != observable) {
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
				Log.v(TAG, ((ContentQueryMap)observable).getRows().toString());
				ContentValues value = ((ContentQueryMap)observable).getValues("mobile_data");
				if (null != value) {
					//Log.v(TAG, value.toString());
					if (1 == value.getAsInteger("value"))
						views.setImageViewResource(R.id.imagebutton2, R.drawable.ic_home_apn_on);
					else
						views.setImageViewResource(R.id.imagebutton2, R.drawable.ic_home_apn_off);
				}
				value = ((ContentQueryMap)observable).getValues(Settings.System.ACCELEROMETER_ROTATION);
				if (null != value) {
					//Log.v(TAG, value.toString());
					if (1 == value.getAsInteger("value"))
						views.setImageViewResource(R.id.imagebutton9, R.drawable.ic_home_rotate_on);
					else
						views.setImageViewResource(R.id.imagebutton9, R.drawable.ic_home_rotate_off);
				}
				int brightness = 0;
				int brightness_mode = 0;
				value = ((ContentQueryMap)observable).getValues(Settings.System.SCREEN_BRIGHTNESS);
				if (null != value) {
					//Log.v(TAG, value.toString());
					brightness = value.getAsInteger("value");
				}
				value = ((ContentQueryMap)observable).getValues(Settings.System.SCREEN_BRIGHTNESS_MODE);
				if (null != value) {
					//Log.v(TAG, value.toString());
					brightness_mode = value.getAsInteger("value");
				}
				switch(SwitchHelper.getBrightness(brightness_mode, brightness)) {
				case SwitchHelper.LIGHT_AUTO:
					views.setImageViewResource(R.id.imagebutton6, R.drawable.ic_home_brightness_auto);
					break;
				case SwitchHelper.LIGHT_25_PERCENT:
					views.setImageViewResource(R.id.imagebutton6, R.drawable.ic_home_brightness_off);
					break;
				case SwitchHelper.LIGHT_50_PERCENT:
					views.setImageViewResource(R.id.imagebutton6, R.drawable.ic_home_brightness_fairly);
					break;
				case SwitchHelper.LIGHT_100_PERCENT:
					views.setImageViewResource(R.id.imagebutton6, R.drawable.ic_home_brightness_on);
					break;
				}
				appWidgetManager.updateAppWidget(new ComponentName(context, cls), views);
			}
		}
		
	};
	
	private static boolean inited = false;
	private static ContentQueryMap global_query = null;
	private static Observer global_observer = null;
	private static ContentQueryMap system_query = null;
	private static Observer system_observer = null;
	
	@Override
	public void onUpdate(Context context,
			AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		synchronized(this) {
			if (!inited) {
				inited = true;
				Cursor cursor = context.getContentResolver().query(
						Settings.Global.CONTENT_URI,
						null,
						"(" + Settings.Global.NAME + "=?)",
						new String[] {
								"mobile_data"
						},
						null);
				global_query = new ContentQueryMap(cursor, Settings.Global.NAME, true, null);
				global_observer = new DynaObserver(context, this.getClass());
				global_query.addObserver(global_observer);
				
				cursor = context.getContentResolver().query(
						Settings.System.CONTENT_URI,
						null,
						"(" + Settings.System.NAME + "=? or " + Settings.System.NAME + "=? or " + Settings.System.NAME + "=?)",
						new String[] {
								Settings.System.ACCELEROMETER_ROTATION,
								Settings.System.SCREEN_BRIGHTNESS,
								Settings.System.SCREEN_BRIGHTNESS_MODE
						},
						null);
				system_query = new ContentQueryMap(cursor, Settings.System.NAME, true, null);
				system_observer = global_observer;
				system_query.addObserver(system_observer);
			}
		}
		
		final int N = appWidgetIds.length;
		for(int i = 0; i < N; ++i) {
			Intent intent = null;
			PendingIntent pendingIntent = null;
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			
			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_WIFI);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton1, pendingIntent);
			if (SwitchHelper.checkWifi(context))
				views.setImageViewResource(R.id.imagebutton1, R.drawable.ic_home_wifi_on);
			else
				views.setImageViewResource(R.id.imagebutton1, R.drawable.ic_home_wifi_off);

			//android.intent.action.ANY_DATA_STATE
			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_MOBILE);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton2, pendingIntent);
			if (SwitchHelper.checkMobileData(context))
				views.setImageViewResource(R.id.imagebutton2, R.drawable.ic_home_apn_on);
			else
				views.setImageViewResource(R.id.imagebutton2, R.drawable.ic_home_apn_off);
			
			intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
			pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton3, pendingIntent);
			if (SwitchHelper.checkBluetooth(context))
				views.setImageViewResource(R.id.imagebutton3, R.drawable.ic_home_bluetooth_on);
			else
				views.setImageViewResource(R.id.imagebutton3, R.drawable.ic_home_bluetooth_off);
			
			intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton4, pendingIntent);
			if (SwitchHelper.checkGPS(context))
				views.setImageViewResource(R.id.imagebutton4, R.drawable.ic_home_gps_on);
			else
				views.setImageViewResource(R.id.imagebutton4, R.drawable.ic_home_gps_off);
			
			intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
			pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton5, pendingIntent);
			if (SwitchHelper.checkAirplane(context))
				views.setImageViewResource(R.id.imagebutton5, R.drawable.ic_home_airplane_on);
			else
				views.setImageViewResource(R.id.imagebutton5, R.drawable.ic_home_airplane_off);
			
			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_BRIGHTNESS);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton6, pendingIntent);
			switch(SwitchHelper.getBrightness(context)) {
			case SwitchHelper.LIGHT_AUTO:
				views.setImageViewResource(R.id.imagebutton6, R.drawable.ic_home_brightness_auto);
				break;
			case SwitchHelper.LIGHT_25_PERCENT:
				views.setImageViewResource(R.id.imagebutton6, R.drawable.ic_home_brightness_off);
				break;
			case SwitchHelper.LIGHT_50_PERCENT:
				views.setImageViewResource(R.id.imagebutton6, R.drawable.ic_home_brightness_fairly);
				break;
			case SwitchHelper.LIGHT_100_PERCENT:
				views.setImageViewResource(R.id.imagebutton6, R.drawable.ic_home_brightness_on);
				break;
			}
			
			//intent = new Intent(context, MainActivity.class);
			//pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			//views.setOnClickPendingIntent(R.id.imagebutton7, pendingIntent);
			
			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_SYNC);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton8, pendingIntent);
			if (SwitchHelper.checkSync(context))
				views.setImageViewResource(R.id.imagebutton8, R.drawable.ic_home_sync_on);
			else
				views.setImageViewResource(R.id.imagebutton8, R.drawable.ic_home_sync_off);
			
			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_ROTATION);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton9, pendingIntent);
			if (SwitchHelper.checkRotation(context))
				views.setImageViewResource(R.id.imagebutton9, R.drawable.ic_home_rotate_on);
			else
				views.setImageViewResource(R.id.imagebutton9, R.drawable.ic_home_rotate_off);
			
			intent = new Intent(context, this.getClass());
			intent.setAction(TOGGLE_RINGER);
			pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.imagebutton10, pendingIntent);
			switch(((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode()) {
			case AudioManager.RINGER_MODE_NORMAL:
				views.setImageViewResource(R.id.imagebutton10, R.drawable.ic_home_sound_ring_on);
				break;
			case AudioManager.RINGER_MODE_SILENT:
				views.setImageViewResource(R.id.imagebutton10, R.drawable.ic_home_sound_silent);
				break;
			case AudioManager.RINGER_MODE_VIBRATE:
				views.setImageViewResource(R.id.imagebutton10, R.drawable.ic_home_sound_vibrate_on);
				break;
			}

			appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
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
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			switch(audioManager.getRingerMode()) {
			case AudioManager.RINGER_MODE_NORMAL:
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				break;
			case AudioManager.RINGER_MODE_SILENT:
				audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				break;
			case AudioManager.RINGER_MODE_VIBRATE:
				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				break;
			}
		}
		else if (WifiManager.WIFI_STATE_CHANGED_ACTION/*"android.net.wifi.WIFI_STATE_CHANGE"*/.equals(action)) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			int wifi_state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
			int wifi_previous_state = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
			Log.v(TAG, "WIFI_STATE " + wifi_previous_state + " -> " + wifi_state);
			if (WifiManager.WIFI_STATE_ENABLED == wifi_state)
				views.setImageViewResource(R.id.imagebutton1, R.drawable.ic_home_wifi_on);
			else
				views.setImageViewResource(R.id.imagebutton1, R.drawable.ic_home_wifi_off);
			appWidgetManager.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
//		else if (ConnectivityManager.CONNECTIVITY_ACTION/*"android.net.conn.CONNECTIVITY_CHANGE"*/.equals(action)) {
//			int networkType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, ConnectivityManager.TYPE_DUMMY);
//			Log.v(TAG, "EXTRA_NETWORK_TYPE = " + networkType);
//			if (ConnectivityManager.TYPE_MOBILE == networkType
//					|| ConnectivityManager.TYPE_MOBILE_DUN == networkType
//					|| ConnectivityManager.TYPE_MOBILE_HIPRI == networkType
//					|| ConnectivityManager.TYPE_MOBILE_MMS == networkType
//					|| ConnectivityManager.TYPE_MOBILE_SUPL == networkType
//					) {
//				Log.v(TAG, "FAILOVER_CONNECTION = " + intent.getBooleanExtra("FAILOVER_CONNECTION", false));
//				Log.v(TAG, "EXTRA_NO_CONNECTIVITY = " + intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false));
//				NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(networkType);
//				Log.v(TAG, networkInfo.toString());
//				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//				if (networkInfo.isConnected())
//					views.setImageViewResource(R.id.imagebutton2, R.drawable.ic_home_apn_on);
//				else
//					views.setImageViewResource(R.id.imagebutton2, R.drawable.ic_home_apn_off);
//				appWidgetManager.updateAppWidget(new ComponentName(context, this.getClass()), views);
//			}
//		}
		else if (BluetoothAdapter.ACTION_STATE_CHANGED/*"android.bluetooth.adapter.action.STATE_CHANGED"*/.equals(action)) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			Log.v(TAG, BluetoothAdapter.ACTION_STATE_CHANGED
					+ " "
					+ intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF)
					+ " -> "
					+ intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF));
			if (SwitchHelper.checkBluetooth(context))
				views.setImageViewResource(R.id.imagebutton3, R.drawable.ic_home_bluetooth_on);
			else
				views.setImageViewResource(R.id.imagebutton3, R.drawable.ic_home_bluetooth_off);
			appWidgetManager.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
		else if (LocationManager.PROVIDERS_CHANGED_ACTION/*"android.location.PROVIDERS_CHANGED"*/.equals(action)) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			if (SwitchHelper.checkGPS(context))
				views.setImageViewResource(R.id.imagebutton4, R.drawable.ic_home_gps_on);
			else
				views.setImageViewResource(R.id.imagebutton4, R.drawable.ic_home_gps_off);
			appWidgetManager.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
		else if (Intent.ACTION_AIRPLANE_MODE_CHANGED/*"android.intent.action.AIRPLANE_MODE"*/.equals(action)) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			boolean enabled = intent.getBooleanExtra("state", true);
			Log.v(TAG, "state = " + enabled);
			if (enabled)
				views.setImageViewResource(R.id.imagebutton5, R.drawable.ic_home_airplane_on);
			else
				views.setImageViewResource(R.id.imagebutton5, R.drawable.ic_home_airplane_off);
			appWidgetManager.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
		else if ("com.android.sync.SYNC_CONN_STATUS_CHANGED".equals(action)) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			if (SwitchHelper.checkSync(context))
				views.setImageViewResource(R.id.imagebutton8, R.drawable.ic_home_sync_on);
			else
				views.setImageViewResource(R.id.imagebutton8, R.drawable.ic_home_sync_off);
			appWidgetManager.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
		else if ("android.media.RINGER_MODE_CHANGED".equals(action)) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			switch(((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode()) {
			case AudioManager.RINGER_MODE_NORMAL:
				views.setImageViewResource(R.id.imagebutton10, R.drawable.ic_home_sound_ring_on);
				break;
			case AudioManager.RINGER_MODE_SILENT:
				views.setImageViewResource(R.id.imagebutton10, R.drawable.ic_home_sound_silent);
				break;
			case AudioManager.RINGER_MODE_VIBRATE:
				views.setImageViewResource(R.id.imagebutton10, R.drawable.ic_home_sound_vibrate_on);
				break;
			}
			appWidgetManager.updateAppWidget(new ComponentName(context, this.getClass()), views);
		}
	}

}
