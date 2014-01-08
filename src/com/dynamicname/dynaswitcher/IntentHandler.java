package com.dynamicname.dynaswitcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class IntentHandler {
	
	private final Context context;
	private final Class<?> cls;
	private final String action;
	private final String sys_action;
	private final Method method;
	private final int [] states;
	private final int [] drawables;
	
	public IntentHandler(
			final Context context,
			final Class<?> cls,
			final String action,
			final String sys_action,
			final Method method,
			final int [] states,
			final int [] drawables) {
		this.context = context;
		this.cls = cls;
		this.action = action;
		this.sys_action = sys_action;
		this.method = method;
		this.states = states;
		this.drawables = drawables;
	}
	
	public void onUpdate(RemoteViews views) {
		if (null == action)
			return;
		
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		views.setOnClickPendingIntent(drawables[0], pendingIntent);
		if (null != method) {
			try {
				if (method.getReturnType() == boolean.class) {
					DynaProvider.update(
							views,
							(Boolean)(method.invoke(null, context)),
							drawables);
				}
				else if (method.getReturnType() == int.class) {
					DynaProvider.update(
							views,
							(Integer)(method.invoke(null, context)),
							states,
							drawables);
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
	
	public void onReceive(AppWidgetManager awm, final String action) {
		if (null == sys_action)
			return;
		
		if (action.equals(sys_action)) {
			if (null != method) {
				try {
					RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
					if (method.getReturnType() == boolean.class) {
						DynaProvider.update(
								views,
								(Boolean)(method.invoke(null, context)),
								drawables);
					}
					else if (method.getReturnType() == int.class) {
						DynaProvider.update(
								views,
								(Integer)(method.invoke(null, context)),
								states,
								drawables);
					}
					awm.updateAppWidget(new ComponentName(context, cls), views);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}				
			}
		}
	}

}
