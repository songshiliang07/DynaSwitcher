package com.dynamicname.dynaswitcher;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	((EditText) findViewById(R.id.wifi_text)).setText(
    			conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState().toString());
    	((EditText) findViewById(R.id.mobile_text)).setText(
    			conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState().toString());
    }

    public void toggleWifi(View view) {
    	SwitchHelper.toggleWiFi(this);
    }
    
	public void toggleMobileData(View view) {
		SwitchHelper.toggleMobileData(this);
	}
    
}
