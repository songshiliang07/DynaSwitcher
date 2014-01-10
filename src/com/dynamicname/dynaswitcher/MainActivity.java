package com.dynamicname.dynaswitcher;

import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends Activity implements View.OnClickListener {

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
        setContentView(R.layout.activity_main);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.textview_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		final int size = spinner_ids.length;
		for(int i = 0; i < size; ++i) {
			Spinner spinner = (Spinner) findViewById(spinner_ids[i]);
			spinner.setAdapter(adapter);
			spinner.setSelection(i);
		}

        ((Button) findViewById(R.id.button2)).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
        final int size = spinner_ids.length;
        int [] vec = new int[size];
        for(int i = 0; i < size; ++i) {
			Spinner spinner = (Spinner) findViewById(spinner_ids[i]);
			vec[i] = spinner.getSelectedItemPosition();
        }
        Log.v("", "onClick " + Arrays.toString(vec));
        DynaProvider.updateAll(this, vec);

		finish();
	}

}
