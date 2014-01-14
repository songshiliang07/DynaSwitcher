package com.dynamicname.dynaswitcher;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class ConfigActivity extends Activity implements View.OnClickListener {

	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	public static final int [] spinner_ids = {
		R.id.spinner1,
		R.id.spinner2,
		R.id.spinner3,
		R.id.spinner4,
		R.id.spinner5,
		R.id.spinner6,
		R.id.spinner7,
		R.id.spinner8,
		R.id.spinner9
	};

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the result to CANCELED. This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);

		initSpinner(this, this);

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
        DynaHandlerMap.update(this, mAppWidgetId, getSelections(this));
		
        // Make sure we pass back the original appWidgetId
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

	public static void initSpinner(Activity activity, View.OnClickListener listener) {
		activity.setContentView(R.layout.activity_main);

		final int size = spinner_ids.length;
		for(int i = 0; i < size; ++i) {
			Spinner spinner = (Spinner) activity.findViewById(spinner_ids[i]);
			DynaAdapter adapter = new DynaAdapter(activity, spinner, R.layout.spinner_item);
			spinner.setAdapter(adapter);
			spinner.setSelection(i);
		}

        ((Button) activity.findViewById(R.id.button2)).setOnClickListener(listener);
	}

	public static int[] getSelections(Activity activity) {
        final int size = spinner_ids.length;
        int [] vec = new int[size];
        for(int i = 0; i < size; ++i) {
			Spinner spinner = (Spinner) activity.findViewById(spinner_ids[i]);
			vec[i] = spinner.getSelectedItemPosition();
        }
        return vec;
	}

}
