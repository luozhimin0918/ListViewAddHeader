package com.listviewaddheader.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.listviewaddheader.R;
import com.listviewaddheader.model.Information;

public class ListViewAdapter extends BaseAdapter {

	private Context mContext = null;
	private List<Information> mInformationList;
	private LayoutInflater mLayoutInflater;

	public ListViewAdapter(Context context, List<Information> informationList) {
		mContext = context;
		mInformationList = informationList;
		if (mLayoutInflater == null) {
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mInformationList != null ? mInformationList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mInformationList != null ? mInformationList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListViewHolder viewHolder = new ListViewHolder();
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.list_item, null);
			viewHolder = getListViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			// 取得缓存数据
			viewHolder = (ListViewHolder) convertView.getTag();
		}

		setContentView(viewHolder, position);

		return convertView;
	}

	private void setContentView(ListViewHolder viewHolder, int position) {
		// TODO Auto-generated method stub
		Information information = new Information();
		if (mInformationList != null && mInformationList.size() > 0) {
			information = mInformationList.get(position);
			viewHolder.mTitleTextView.setText(information.getTitle());
			viewHolder.mDescTextView.setText(information.getDesc());
		}
	}

	private ListViewHolder getListViewHolder(View convertView) {
		ListViewHolder holder = new ListViewHolder();
		holder.mTitleTextView = (TextView) convertView
				.findViewById(R.id.tv_title);
		holder.mDescTextView = (TextView) convertView
				.findViewById(R.id.tv_desc);
		return holder;
	}

	public class ListViewHolder {
		public TextView mTitleTextView;
		public TextView mDescTextView;
	}

}
