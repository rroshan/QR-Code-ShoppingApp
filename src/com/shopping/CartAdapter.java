package com.shopping;

import java.util.ArrayList;
import com.spopping.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CartAdapter extends BaseAdapter 
{
	protected static ArrayList<CartItem> items = new ArrayList<CartItem>();
	protected static double total = 0.0;
	
	public int getCount() 
	{
		return items.size();
	}

	public Object getItem(int index) 
	{
		return items.get(index);
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
			view = layoutInflater.inflate(R.layout.cart_list_item, parent,false);
		}
		
		CartItem c = items.get(index);
		
		TextView tv1 = (TextView)view.findViewById(R.id.prod_name_view);
		tv1.setText("Product Name: "+c.getProductName());
		
		TextView tv2 = (TextView)view.findViewById(R.id.prod_id_view);
		tv2.setText("Product ID: "+c.getProductId());
		
		TextView tv3 = (TextView)view.findViewById(R.id.price_view);
		tv3.setText("Price per kg: "+c.getPricePerKg());
		
		TextView tv4 = (TextView)view.findViewById(R.id.no_of_kgs);
		tv4.setText("No of kgs:"+c.getNoOfKgs());
		
		TextView tv5 = (TextView)view.findViewById(R.id.total_price);
		try
		{
			String totalPrice = c.totalPrice();
			tv5.setText("Total Price: "+totalPrice);
		}
		catch(NumberFormatException nfe)
		{
			nfe.printStackTrace();
		}
		return view;
	}
	
	public double getTotal()
	{
		return total;
	}
	
	public void deleteItem(long id)
	{
		total = total - Double.valueOf(items.get((int)id).totalPrice()); 
		items.remove((int)id);
	}
}