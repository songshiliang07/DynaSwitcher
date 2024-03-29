package com.dynamicname.dynaswitcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public final class SwitchHelper {
	
	static final String TAG = "SwitchHelper";

	public static final int LIGHT_AUTO = 0;
	public static final int LIGHT_25_PERCENT = 255 / 4;     //63
	public static final int LIGHT_50_PERCENT = 255 / 2 + 1; //128
	public static final int LIGHT_100_PERCENT = 255;
	
	public static boolean checkWifi(Context context) {
		return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
	}

	public static void toggleWiFi(Context context) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		boolean enabled = wm.isWifiEnabled();
		wm.setWifiEnabled(!enabled);
		//Toast.makeText(
		//		context,
		//		enabled ? R.string.wifi_off : R.string.wifi_on,
		//		Toast.LENGTH_SHORT).show();
	}

	private static boolean inited = false;
	private static Object iConMgr = null;                    // IConnectivityManager类的引用
	private static Method getMobileDataEnabledMethod = null; // getMobileDataEnabled方法
	private static Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法
	
	synchronized private static void initMobileDataMethod(Context context) {
		if (!inited) {
			inited = true;

			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			Class<?> conMgrClass = null;  // ConnectivityManager类
			Field iConMgrField = null;    // ConnectivityManager类中的字段
			Class<?> iConMgrClass = null; // IConnectivityManager类

			try {
				// 取得ConnectivityManager类
				conMgrClass = cm.getClass();
				// 取得ConnectivityManager类中的对象mService
				iConMgrField = conMgrClass.getDeclaredField("mService");
				// 设置mService可访问
				iConMgrField.setAccessible(true);
				// 取得mService的实例化类IConnectivityManager
				iConMgr = iConMgrField.get(cm);
				// 取得IConnectivityManager类
				iConMgrClass = iConMgr.getClass();

				// 取得IConnectivityManager类中的getMobileDataEnabled()方法
				getMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("getMobileDataEnabled");
				// 设置getMobileDataEnabled可访问
				getMobileDataEnabledMethod.setAccessible(true);
				// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
				setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
				// 设置setMobileDataEnabled方法可访问
				setMobileDataEnabledMethod.setAccessible(true);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean checkMobileData(Context context) {
		initMobileDataMethod(context);
		
		if (null != getMobileDataEnabledMethod) {
			try {
				// 调用getMobileDataEnabled方法
				return (Boolean) getMobileDataEnabledMethod.invoke(iConMgr);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}

	public static void toggleMobileData(Context context) {
		initMobileDataMethod(context);

		if (null != getMobileDataEnabledMethod && null != setMobileDataEnabledMethod) {
			try {
				// 调用getMobileDataEnabled方法
				boolean enabled = (Boolean) getMobileDataEnabledMethod.invoke(iConMgr);
				// 调用setMobileDataEnabled方法
				setMobileDataEnabledMethod.invoke(iConMgr, !enabled);
				//Toast.makeText(context,
				//		enabled ? R.string.mobile_off : R.string.mobile_on,
				//		Toast.LENGTH_SHORT).show();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

	}

	public static boolean checkBluetooth(Context context) {
		return BluetoothAdapter.getDefaultAdapter().isEnabled();
	}

	public static void toggleBluetooth(Context context) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		boolean enabled = adapter.isEnabled();
		if (enabled)
			adapter.disable();
		else
			adapter.enable();
		//Toast.makeText(
		//		context,
		//		enabled ? R.string.bluetooth_off : R.string.bluetooth_on,
		//		Toast.LENGTH_SHORT).show();
	}

	public static boolean checkGPS(Context context) {
		int enabled = Settings.Secure.LOCATION_MODE_OFF;
		try {
			enabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
			//Log.v(TAG, "Settings.Secure.LOCATION_MODE:" + enabled);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return enabled != Settings.Secure.LOCATION_MODE_OFF;
	}

	public static void toggleGPS(Context context) {
		try {
			int enabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
			if (enabled == Settings.Secure.LOCATION_MODE_OFF)
				enabled = Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
			else
				enabled = Settings.Secure.LOCATION_MODE_OFF;
			Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, enabled);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkAirplane(Context context) {
		boolean enabled = Settings.Global.getInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, 0) == 1 ? true : false;
		return enabled;
	}

	public static void toggleAirplane(Context context) {
		int enabled = Settings.Global.getInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, 0) == 1 ? 0 : 1;
		Settings.Global.putInt(context.getContentResolver(),
				Settings.Global.AIRPLANE_MODE_ON, enabled);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", enabled);
		context.sendBroadcast(intent);
	}
	
	public static int getBrightness(int brightness_mode, int brightness) {
		int light = LIGHT_AUTO;
		if (Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC == brightness_mode)
			light = LIGHT_AUTO;
		else if (Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL == brightness_mode){
			if (brightness > LIGHT_AUTO && brightness <= LIGHT_25_PERCENT)
				light = LIGHT_25_PERCENT;
			else if (brightness > LIGHT_25_PERCENT && brightness <= LIGHT_50_PERCENT)
				light = LIGHT_50_PERCENT;
			else if (brightness <= LIGHT_100_PERCENT)
				light = LIGHT_100_PERCENT;
		}
		return light;
	}
	public static int getBrightness(Context context) {
		int light = LIGHT_AUTO;
		try {
			int brightness_mode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
			//Log.v(TAG, "SCREEN_BRIGHTNESS_MODE = " + brightness_mode);
			int brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
			//Log.v(TAG, "SCREEN_BRIGHTNESS = " + brightness);
			light = getBrightness(brightness_mode, brightness);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return light;
	}
	
	public static void toggleBrightness(Context context) {
		int light = 0;
		switch(SwitchHelper.getBrightness(context)) {
		case SwitchHelper.LIGHT_AUTO:
			light = SwitchHelper.LIGHT_25_PERCENT - 1;
			Settings.System.putInt(context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE,
					Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			break;
		case SwitchHelper.LIGHT_25_PERCENT:
			light = SwitchHelper.LIGHT_50_PERCENT - 1;
			break;
		case SwitchHelper.LIGHT_50_PERCENT:
			light = SwitchHelper.LIGHT_100_PERCENT - 1;
			break;
		case SwitchHelper.LIGHT_100_PERCENT:
			light = SwitchHelper.LIGHT_100_PERCENT;
			Settings.System.putInt(context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE,
					Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
			break;
		}
		
//		try {
//			PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//			// 得到PowerManager类对应的Class对象
//			Class<?> pmClass = mPowerManager.getClass();
//			// 得到PowerManager类中的成员mService（mService为PowerManager类型）
//			Field field = pmClass.getDeclaredField("mService");
//			// 设置mService可访问
//			field.setAccessible(true);
//			// 实例化mService
//			Object iPM = field.get(mPowerManager);
//			// 得到PowerManager对应的Class对象
//			Class<?> iPMClass = iPM.getClass();
//			// 得到PowerManager的函数setBacklightBrightness对应的Method对象，
//			Method method = iPMClass.getDeclaredMethod("setBacklightBrightness", int.class);
//			// 设置setBacklightBrightness方法可访问
//			method.setAccessible(true);
//			// 调用实现PowerManager的setBacklightBrightness
//			method.invoke(iPM, light);
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
		Settings.System.putInt(context.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS,
				light);
	}
	
	public static boolean checkSync(Context context) {
        return ContentResolver.getMasterSyncAutomatically();
	}
	
	public static void toggleSync(Context context) {
		ContentResolver.setMasterSyncAutomatically(!checkSync(context));
	}

	public static boolean checkRotation(Context context) {
		int status = 0;
		try {
			status = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
			//Log.v(TAG, "rotation = " + status);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return 1 == status;
	}
	
	public static void toggleRotation(Context context) {
		int status = 0;
		try {
			status = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1 == status ? 0 : 1);
	}
	
	public static int getRinger(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		return am.getRingerMode();
	}
	
	public static void toggleRinger(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		switch(am.getRingerMode()) {
		case AudioManager.RINGER_MODE_NORMAL:
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			break;
		case AudioManager.RINGER_MODE_SILENT:
			am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		case AudioManager.RINGER_MODE_VIBRATE:
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		}
	}
	
}
