package com.shopping;

import java.util.ArrayList;
import java.util.Iterator;
import com.spopping.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Receipt extends Activity 
{
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.receipt);
        Bundle extras = getIntent().getExtras();
        
        int id = extras.getInt("PRODUCT_ID");
        ShoppingSession ss = HistoryAdapter.arr.get(id);
        ArrayList<CartItem> citems = ss.getCartItems();
        
        LinearLayout ll = (LinearLayout)findViewById(R.id.ll_receipt);
        
        double total = 0.0;
        Iterator<CartItem> it = citems.iterator();
		while(it.hasNext())
        {
			CartItem c = it.next();
			TextView tv = new TextView(this);
			String str = "Product Name:"+c.getProductName()+"\n"+"No of Kilograms:"+c.getNoOfKgs()+"\n"+"Price per Kilogram:"+c.getPricePerKg()+"\n"+"Price:"+c.totalPrice()+"\n";
			total = total + Double.valueOf(c.totalPrice());
			tv.setText(str);
			ll.addView(tv);
        }
		
		String str = "\n\nTotal price:"+total;
		TextView tv = new TextView(this);
		tv.setText(str);
		ll.addView(tv);
    }
}
