package com.dynamicname.dynaswitcher;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

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
		DynaHandlerMap.receive(context, type);
	}

}
