package com.shopping;

import java.io.Serializable;

public class CartItem implements Serializable
{
	private String productId;
	private String productName;
	private String pricePerKg;
	private String noOfKgs;
	private String totalPrice;
		
	public CartItem(String productId,String productName,String pricePerKg,String noOfKgs)
	{
		this.productId = productId;
		this.productName = productName;
		this.pricePerKg = pricePerKg;
		this.noOfKgs = noOfKgs;
		
		double ppk = Double.valueOf(pricePerKg);
		double nok = Double.valueOf(noOfKgs);
		double totalPrice = ppk*nok;
		this.totalPrice = Double.toString(totalPrice);
	}
	
	public String getProductId()
	{
		return productId;
	}
	
	public String getProductName()
	{
		return productName;
	}
	
	public String getPricePerKg()
	{
		return pricePerKg;
	}
	
	public String getNoOfKgs()
	{
		return noOfKgs;
	}
	
	public String totalPrice()
	{
		return totalPrice;
	}
	
	public String toString()
	{
		String str = "Product ID:"+productId+"\nProduct Name:"+productName+"\nPrice per KG:"+pricePerKg+"\nNumber of KGs:"+noOfKgs+"\nTotal Price:"+totalPrice;
		return str;
	}
}
