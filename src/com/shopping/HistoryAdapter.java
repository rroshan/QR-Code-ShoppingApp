package com.shopping;

import java.util.ArrayList;

import com.spopping.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter 
{
	protected static ArrayList<ShoppingSession> arr = new ArrayList<ShoppingSession>();
		
	public int getCount() 
	{
		return arr.size();
	}

	public Object getItem(int index) 
	{
		return arr.get(index);
	}

	public long getItemId(int index) 
	{
		return index;
	}

	public View getView(int index, View view, ViewGroup parent) 
	{
		if(view == null)
		{
			LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			view = layoutInflater.inflate(R.layout.history_item_view, parent,false);
		}
		
		ShoppingSession ss = arr.get(index);
		
		TextView tv = (TextView)view.findViewById(R.id.date);
		tv.setText(ss.getDate());
		
		tv = (TextView)view.findViewById(R.id.total);
		tv.setText(ss.getTotal()+" INR");
		
		return view;
	}
}
