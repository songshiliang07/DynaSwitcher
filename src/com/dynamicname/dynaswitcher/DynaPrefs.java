package com.dynamicname.dynaswitcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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
			Set<String> set = new HashSet<String>();
			final int length = vec.length;
			for(int i = 0; i < length; ++i) {
				set.add(String.valueOf(vec[i]));
			}
			SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putStringSet(String.valueOf(mAppWidgetId), set);
			editor.apply();
		}
	}

	synchronized static public int[] loadPrefs(Context context, int mAppWidgetId) {
		loadPrefs(context);
		int[] button_vector = widget_settings.get(mAppWidgetId);
		if (null == button_vector) {
			button_vector = DynaProvider.default_button_vector;
			widget_settings.put(mAppWidgetId, button_vector);
		}
		return button_vector;
	}

	synchronized static public void deletePrefs(Context context, int mAppWidgetId) {
		if (null != widget_settings) {
			Log.v(TAG, String.valueOf(
					mAppWidgetId)
					+ "  memory  "
					+ Arrays.toString(widget_settings.get(mAppWidgetId)));
			widget_settings.remove(mAppWidgetId);
		}
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Log.v(TAG, String.valueOf(
				mAppWidgetId)
				+ "  prefs   "
				+ settings.getStringSet(String.valueOf(mAppWidgetId), null));
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(String.valueOf(mAppWidgetId));
		editor.apply();
	}

	synchronized static public SparseArray<int[]> loadPrefs(Context context) {
		if (null == widget_settings) {
			widget_settings = new SparseArray<int[]>();
			SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			Set<String> keys = settings.getAll().keySet();
			Iterator<String> keys_iter = keys.iterator();
			while(keys_iter.hasNext()) {
				int mAppWidgetId = Integer.parseInt(keys_iter.next());
				if (AppWidgetManager.INVALID_APPWIDGET_ID != mAppWidgetId) {
					Set<String> set = settings.getStringSet(String.valueOf(mAppWidgetId), null);
					if (null != set) {
						int[] button_vector = new int[set.size()];
						Iterator<String> iter = set.iterator();
						int i = 0;
						while(iter.hasNext()) {
							button_vector[i] = Integer.parseInt(iter.next());
							++i;
						}
						widget_settings.put(mAppWidgetId, button_vector);
						Log.v(TAG, "loadPrefs all: " + mAppWidgetId + " " + Arrays.toString(button_vector));
					}
				}
			}
		}
		return widget_settings;
	}

}
