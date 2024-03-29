package com.vidmt.lmei.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vidmt.lmei.R;
import com.vidmt.lmei.adapter.AdapterFriends.ViewHolder;
import com.vidmt.lmei.entity.Persion;
import com.vidmt.lmei.util.rule.ScreenUtils;
import com.vidmt.lmei.widget.RoundImageView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdapterSelectFriends extends BaseAdapter {
	private Context context;
	private List<Persion> list;
	protected ImageLoader imageLoader;
	protected DisplayImageOptions options;
	public AdapterSelectFriends(Context context,List<Persion> list,ImageLoader imageLoader,
			DisplayImageOptions options)
	{
		this.context = context;
		this.list = list;
		this.imageLoader = imageLoader;
		this.options = options;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null)
		{
			holder = new ViewHolder();
			LayoutInflater lif = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = lif.inflate(R.layout.adapter_select_friends, null);
			holder.friendsico= (ImageView) convertView.findViewById(R.id.friendsico);
			holder.friendsname= (TextView) convertView.findViewById(R.id.friendsname);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		
			Persion person = (Persion)list.get(position);
			
			if(person!=null)
			{				
					imageLoader.displayImage(person.getPhoto(), holder.friendsico, options);				
					holder.friendsname.setText(person.getNick_name());					
			}
		return convertView;
	}

	class ViewHolder
	{
		
	
		TextView friendsname;
		ImageView friendsico;
		
	}
}
