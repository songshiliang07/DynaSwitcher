package com.dynamicname.dynaswitcher;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

public class DynaPrefs {

	static final String TAG = "DynaPrefs";
	private static final String PREFS_NAME = "com.dynamicname.dynaswitcher";
	private static SparseArray<int[]> widget_settings = null;

	synchronized static public void savePrefs(Context context, int mAppWidgetId, int [] vec) {
		if (AppWidgetManager.INVALID_APPWIDGET_ID != mAppWidgetId
				&& null != vec) {
			if (null == widget_settings)
				loadPrefs(context);
			if (null != widget_settings)
				widget_settings.put(mAppWidgetId, vec);
			//Log.v(TAG, "savePrefs orig: " + mAppWidgetId + " " + Arrays.toString(vec));
			SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(String.valueOf(mAppWidgetId), Arrays.toString(vec));
			editor.apply();
		}
	}

	synchronized static public void deletePrefs(Context context, int mAppWidgetId) {
		if (null != widget_settings) {
			//Log.v(TAG, String.valueOf(
			//		mAppWidgetId)
			//		+ "  memory  "
			//		+ Arrays.toString(widget_settings.get(mAppWidgetId)));
			widget_settings.remove(mAppWidgetId);
		}
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		//Log.v(TAG, String.valueOf(
		//		mAppWidgetId)
		//		+ "  prefs   "
		//		+ settings.getString(String.valueOf(mAppWidgetId), null));
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(String.valueOf(mAppWidgetId));
		editor.apply();
	}

	synchronized static public void clearPrefs(Context context) {
		if (null != widget_settings)
			widget_settings = null;
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.apply();
	}

	synchronized static public int[] loadPrefs(Context context, int mAppWidgetId) {
		if (null == widget_settings)
			loadPrefs(context);
		int[] button_vector = widget_settings.get(mAppWidgetId);
		if (null == button_vector) {
			button_vector = DynaProvider.default_button_vector;
			savePrefs(context, mAppWidgetId, button_vector);
		}
		//Log.v(TAG, "loadPrefs one : " + mAppWidgetId + " " + Arrays.toString(button_vector));
		return button_vector;
	}

	synchronized static public SparseArray<int[]> loadPrefs(Context context) {
		if (null == widget_settings) {
			widget_settings = new SparseArray<int[]>();
			SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			Set<String> keys = settings.getAll().keySet();
			Iterator<String> keys_iter = keys.iterator();
			try {
				while(keys_iter.hasNext()) {
					int mAppWidgetId = Integer.valueOf(keys_iter.next());
					if (AppWidgetManager.INVALID_APPWIDGET_ID != mAppWidgetId) {
						String set = settings.getString(String.valueOf(mAppWidgetId), null);
						if (null != set) {
							String[] intValues = set.substring(1, set.length() - 1).split(", ");
							final int length = intValues.length;
							int[] button_vector = new int[length];
							for(int i = 0; i < length; ++i) {
								button_vector[i] = Integer.valueOf(intValues[i]);
							}
							widget_settings.put(mAppWidgetId, button_vector);
							//Log.v(TAG, "loadPrefs all : " + mAppWidgetId + " " + Arrays.toString(button_vector));
						}
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return widget_settings;
	}

}
