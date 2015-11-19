package com.listviewaddheader.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.example.listviewaddheader.R;
import com.listviewaddheader.cache.ImageDownloader;

public class TopViewPagerAdapter extends PagerAdapter {

	private ArrayList<View> mPageViewList;
	private List<String> mImageList;
	private Context mContext;
	private LayoutInflater mInflater;
	private ImageDownloader mImageDownloader;
	private ImageDownloader dd;

	public TopViewPagerAdapter(Context context, List<String> imageList) {
		mContext = context;
		mImageList = imageList;
		mPageViewList = new ArrayList<View>();
		if (mInflater == null) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		for (int i = 0; i < imageList.size(); i++) {
			View view = mInflater.inflate(R.layout.header_item, null);
			mPageViewList.add(view);
		}
	}

	@Override
	public int getCount() {
		return mPageViewList != null ? mPageViewList.size() : 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(mPageViewList.get(arg1));
	}

	@Override
	public Object instantiateItem(View arg0, final int arg1) {
		((ViewPager) arg0).addView(mPageViewList.get(arg1));
		final ImageView ImageV = (ImageView) mPageViewList.get(arg1)
				.findViewById(R.id.image);
		mImageDownloader = new ImageDownloader(mContext);
		mImageDownloader.download(mImageList.get(arg1), ImageV,
				ScaleType.FIT_XY);
		ImageV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "µã»÷Í¼Æ¬", Toast.LENGTH_LONG).show();
			}
		});
		return mPageViewList.get(arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		//
	}

	@Override
	public Parcelable saveState() {
		//
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		//
	}

	@Override
	public void finishUpdate(View arg0) {
		//
	}

}
