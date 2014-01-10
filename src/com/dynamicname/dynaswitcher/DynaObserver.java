package com.dynamicname.dynaswitcher;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.SparseArray;

public class DynaObserver extends ContentObserver {

	final Context context;
	final int type;
	
	public DynaObserver(Handler handler, Context context, int type) {
		super(handler);
		this.context = context;
		this.type = type;
	}

	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		if (null != DynaProvider.handler_map) {
			SparseArray<int[]> widget_settings = DynaPrefs.loadPrefs(context);
			final int length = widget_settings.size();
			for(int i = 0; i < length; ++i) {
				int[] button_vector = widget_settings.valueAt(i);
				final int size = button_vector.length;
				for(int j = 0; j < size; ++j) {
					if (type == button_vector[j]) {
						AppWidgetManager awm = AppWidgetManager.getInstance(context);
						DynaProvider.handler_map.get(button_vector[j]).onReceive(context, awm, widget_settings.keyAt(i), DynaProvider.resource_ids[j]);
						break;
					}
				}
			}
		}
	}

}
