package com.dynamicname.dynaswitcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfigActivity.initSpinner(this, this);
    }

	@Override
	public void onClick(View v) {
        DynaHandlerMap.update(this, ConfigActivity.getSelections(this));

		finish();
	}

}
