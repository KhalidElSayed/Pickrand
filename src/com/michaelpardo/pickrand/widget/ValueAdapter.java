package com.michaelpardo.pickrand.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.michaelpardo.pickrand.model.IValue;

public class ValueAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<IValue> mItems;

	public ValueAdapter(Context context, Collection<? extends IValue> items) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mItems = new ArrayList<IValue>();
		mItems.addAll(items);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mItems.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		IValue value = (IValue) getItem(position);
		ViewHolder holder = new ViewHolder();

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.simple_list_item_1, null);
			holder.text = (TextView) convertView.findViewById(R.id.text1);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.text.setText(value.getValue());

		return convertView;
	}

	class ViewHolder {
		TextView text;
	}
}
