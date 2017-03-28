package com.htq.baidu.coolnote.adapter;

import java.util.List;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.entity.BgPicEntity;
import com.htq.baidu.coolnote.utils.SystemUtils;

/**
 * 
 */

public class BgPicGridAdapter extends BaseAdapter {
	

	private List<BgPicEntity> bgList;
	private Resources resources;
	private Activity mActivity;
	private String mDefaultBgPath;
	private SystemUtils systemUtil;
	public BgPicGridAdapter(Activity mActivity,List<BgPicEntity> list)
	{
		this.bgList = list;
		this.mActivity=mActivity;
		this.resources = mActivity.getResources();
		systemUtil=new SystemUtils(mActivity);
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bgList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return bgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mActivity).inflate(
					R.layout.bg_pic_grid_item, null);
			viewHolder.backgroundIv = (ImageView) convertView
					.findViewById(R.id.gridview_item_iv);
			viewHolder.checkedIv = (ImageView) convertView
					.findViewById(R.id.gridview_item_checked_iv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.backgroundIv.setBackgroundDrawable(new BitmapDrawable(
				resources, ((BgPicEntity)getItem(position)).bitmap));
		
		mDefaultBgPath=systemUtil.getPath();
		if (((BgPicEntity)getItem(position)).path.equals(mDefaultBgPath)) {
			viewHolder.checkedIv.setVisibility(View.VISIBLE);
		} else {
			viewHolder.checkedIv.setVisibility(View.GONE);
		}

		return convertView;
		
		
	}
	private class ViewHolder {
		ImageView checkedIv, backgroundIv;
	}


}
