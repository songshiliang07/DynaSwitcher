package com.dynamicname.dynaswitcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.Spinner;

public class DynaAdapter extends ArrayAdapter<DynaHandler> {

	final Spinner spinner;

	public DynaAdapter(Context context, Spinner spinner, int resource) {
		super(context, resource);
		this.spinner = spinner;
		DynaHandlerMap.init(context);
	}

	@Override
	public int getCount() {
		return DynaHandlerMap.getCount();
	}

	@Override
	public DynaHandler getItem(int position) {
		return DynaHandlerMap.getHandler(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getPosition(DynaHandler item) {
		return item.key;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View spinView = null;
		if (null == convertView) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			spinView = inflater.inflate(R.layout.spinner_item, parent, false);
		}
		else
			spinView = convertView;
		DynaHandler item = getItem(position);
		ImageView imageView = (ImageView) spinView.findViewById(R.id.imageView1);
		CheckedTextView checkedTextView = (CheckedTextView) spinView.findViewById(R.id.checkedTextView1);
		imageView.setImageResource(item.drawables[0]);
		imageView.setContentDescription(item.name);
		checkedTextView.setText(item.name);
		return spinView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View spinView = null;
		if (null == convertView) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			spinView = inflater.inflate(R.layout.spinner_item, parent, false);
		}
		else
			spinView = convertView;
		DynaHandler item = getItem(position);
		ImageView imageView = (ImageView) spinView.findViewById(R.id.imageView1);
		CheckedTextView checkedTextView = (CheckedTextView) spinView.findViewById(R.id.checkedTextView1);
		imageView.setImageResource(item.drawables[0]);
		imageView.setContentDescription(item.name);
		checkedTextView.setText(item.name);
		if (spinner.getSelectedItemPosition() == position) {
			checkedTextView.setChecked(true);
			checkedTextView.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
		}
		else
			checkedTextView.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
		return spinView;
	}

}
