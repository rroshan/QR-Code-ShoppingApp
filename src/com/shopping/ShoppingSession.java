package com.shopping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class ShoppingSession implements Comparable<ShoppingSession>, Serializable
{
	private ArrayList<CartItem> citems;
	private String total;
	private String date;
	
	public ShoppingSession(String date,double total,ArrayList<CartItem> arr)
	{
		citems = arr;
		this.date = date; 
		this.total = Double.toString(total);
	}
	
	public ArrayList<CartItem> getCartItems()
	{
		return citems;
	}
	
	public String getTotal()
	{
		return total;
	}
	
	public String getDate()
	{
		return date;
	}
	
	public String toString()
	{
		Iterator<CartItem> it = citems.iterator();
		StringBuffer str = new StringBuffer();
		str.append("Thank you for shopping with US!!\n\n\n");
		str.append("Your Receipt:\n\n");
		int count = 1;
		while(it.hasNext())
		{
			CartItem c = it.next();
			str.append(Integer.toString(count)+")\n"+c.toString()+"\n");
			count++;
		}
		
		str.append("\nTotal Price:"+total);
		return str.toString();
	}

	public int compareTo(ShoppingSession s) 
	{
		int flag = date.compareTo(s.getDate()); 
		if(flag > 0)
		{
			return -1;
		}
		
		else if(flag == 0)
		{
			return 0;
		}
		
		else
		{
			return 1;
		}
			
	}
}
