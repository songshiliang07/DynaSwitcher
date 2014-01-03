package com.dynamicname.dynaswitcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toggleWifi(View view) {
    	SwitchHelper.toggleWiFi(this);
    }
    
	public void toggleMobileData(View view) {
		SwitchHelper.toggleMobileData(this);
	}
    
}
