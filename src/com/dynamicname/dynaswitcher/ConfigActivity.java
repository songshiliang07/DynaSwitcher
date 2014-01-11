package com.dynamicname.dynaswitcher;

import java.util.Arrays;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ConfigActivity extends Activity implements View.OnClickListener {

	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the result to CANCELED. This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);
		setContentView(R.layout.activity_main);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.textview_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final int size = MainActivity.spinner_ids.length;
		for(int i = 0; i < size; ++i) {
			Spinner spinner = (Spinner) findViewById(MainActivity.spinner_ids[i]);
			spinner.setAdapter(adapter);
			spinner.setSelection(i);
		}
		((Button) findViewById(R.id.button2)).setOnClickListener(this);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (null != extras) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if (AppWidgetManager.INVALID_APPWIDGET_ID == mAppWidgetId) {
			finish();
		}
	}

	@Override
	public void onClick(View v) {
        final int size = MainActivity.spinner_ids.length;
        int [] vec = new int[size];
        for(int i = 0; i < size; ++i) {
			Spinner spinner = (Spinner) findViewById(MainActivity.spinner_ids[i]);
			vec[i] = spinner.getSelectedItemPosition();
        }
        Log.v("", "onClick " + Arrays.toString(vec));
        DynaHandlerMap.update(this, mAppWidgetId, vec);
		
        // Make sure we pass back the original appWidgetId
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

}
