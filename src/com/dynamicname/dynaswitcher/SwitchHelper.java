package com.dynamicname.dynaswitcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.widget.Toast;

public final class SwitchHelper {
	
	static final String TAG = "SwitchHelper";

	public static boolean checkWifi(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wm.isWifiEnabled();
	}

	public static void toggleWiFi(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		boolean enabled = wm.isWifiEnabled();
		wm.setWifiEnabled(!enabled);
		Toast.makeText(context, enabled ? R.string.wifi_off : R.string.wifi_on,
				Toast.LENGTH_SHORT).show();
	}

	public static boolean checkMobileData(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段
		Object iConMgr = null; // IConnectivityManager类的引用
		Class<?> iConMgrClass = null; // IConnectivityManager类
		Method getMobileDataEnabledMethod = null; // getMobileDataEnabled方法

		try {
			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// 取得ConnectivityManager类中的对象mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(conMgr);
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// 取得IConnectivityManager类中的getMobileDataEnabled()方法
			getMobileDataEnabledMethod = iConMgrClass
					.getDeclaredMethod("getMobileDataEnabled");
			// 设置getMobileDataEnabled可访问
			getMobileDataEnabledMethod.setAccessible(true);
			// 调用getMobileDataEnabled方法
			return (Boolean) getMobileDataEnabledMethod.invoke(iConMgr);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static void toggleMobileData(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段
		Object iConMgr = null; // IConnectivityManager类的引用
		Class<?> iConMgrClass = null; // IConnectivityManager类
		Method getMobileDataEnabledMethod = null; // getMobileDataEnabled方法
		Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法

		try {
			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// 取得ConnectivityManager类中的对象mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(conMgr);
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgr.getClass().getName());

			// 取得IConnectivityManager类中的getMobileDataEnabled()方法
			getMobileDataEnabledMethod = iConMgrClass
					.getDeclaredMethod("getMobileDataEnabled");
			// 设置getMobileDataEnabled可访问
			getMobileDataEnabledMethod.setAccessible(true);
			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
			// 设置setMobileDataEnabled方法可访问
			setMobileDataEnabledMethod.setAccessible(true);

			// 调用getMobileDataEnabled方法
			boolean enabled = (Boolean) getMobileDataEnabledMethod
					.invoke(iConMgr);
			// 调用setMobileDataEnabled方法
			setMobileDataEnabledMethod.invoke(iConMgr, !enabled);
			Toast.makeText(context,
					enabled ? R.string.mobile_off : R.string.mobile_on,
					Toast.LENGTH_SHORT).show();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
		} catch (InvocationTargetException e) {
			e.printStackTrace();
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
		Toast.makeText(context,
				enabled ? R.string.bluetooth_off : R.string.bluetooth_on,
				Toast.LENGTH_SHORT).show();
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

}
