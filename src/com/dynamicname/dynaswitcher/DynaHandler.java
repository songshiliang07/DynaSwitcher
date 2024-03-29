package com.dynamicname.dynaswitcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class DynaHandler {

	public  final int    key;
	public  final String name;
	private final String local_action;
	private final String android_action;
	private final Method method;
	private final int [] states;
	public  final int [] drawables;

	@Override
	public String toString() {
		return name;
	}

	public DynaHandler(
			final int    key,
			final String name,
			final String local_action,
			final String android_action,
			final Method method,
			final int [] states,
			final int [] drawables) {
		this.key = key;
		this.name = name;
		this.local_action = local_action;
		this.android_action = android_action;
		this.method = method;
		this.states = states;
		this.drawables = drawables;
	}

	public void onUpdate(Context context, Class<?> cls, RemoteViews views, int rid) {
		if (null == local_action)
			return;
		
		Intent intent = new Intent(context, cls);
		intent.setAction(local_action);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		views.setOnClickPendingIntent(rid, pendingIntent);
		if (null != method) {
			try {
				if (method.getReturnType() == boolean.class) {
					update(views, rid, (Boolean)(method.invoke(null, context)));
				}
				else if (method.getReturnType() == int.class) {
					update(views, rid, (Integer)(method.invoke(null, context)));
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}				
		}
	}

	public void onReceive(Context context, AppWidgetManager awm, int appWidgetId, int rid, final String action) {
		if (null == android_action)
			return;
		
		if (action.equals(android_action)) {
			onReceive(context, awm, appWidgetId, rid);
		}
	}

	public void onReceive(Context context, AppWidgetManager awm, int appWidgetId, int rid) {
		if (null != method) {
			try {
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
				if (method.getReturnType() == boolean.class) {
					update(views, rid, (Boolean)(method.invoke(null, context)));
				}
				else if (method.getReturnType() == int.class && null != states) {
					update(views, rid, (Integer)(method.invoke(null, context)));
				}
				awm.updateAppWidget(appWidgetId, views);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void update(RemoteViews views, int rid, boolean cur_state) {
		if (cur_state)
			views.setImageViewResource(rid, drawables[0]);
		else
			views.setImageViewResource(rid, drawables[1]);
	}

	private void update(RemoteViews views, int rid, int cur_state) {
		final int length = states.length;
		int index = 0;
		for(; index < length; ++index) {
			if (cur_state == states[index]) break;
		}
		if (index >= 0 && index < length)
			views.setImageViewResource(rid, drawables[index]);
	}

}
