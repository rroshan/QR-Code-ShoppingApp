package com.shopping;

import com.spopping.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AfterScanActivity extends Activity
{
	private static String product_id;
	private static String product_name;
	private static String price_per_kg;
	private static String no_of_kgs;
	private static String trivia;
	
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.after_scan_layout);
        
        Bundle extras = getIntent().getExtras();
               
        product_id = extras.getString("PRODUCT_ID");
        product_name = extras.getString("PRODUCT_NAME");
        price_per_kg = extras.getString("PRICE_PER_KG");
        trivia = extras.getString("TRIVIA");
        
        TextView txt = (TextView)findViewById(R.id.product_id);
        txt.setText("Product ID: "+product_id);
        
        txt = (TextView)findViewById(R.id.product_name);
        txt.setText("Product Name: "+product_name);
        
        txt = (TextView)findViewById(R.id.price_per_kg);
        txt.setText("Price per KG: "+price_per_kg+" INR");
        
        String[] parts = trivia.split("\\|");
        StringBuffer buf = new StringBuffer();
        int i;
        for(i=0;i<parts.length;i++)
        {
        	buf.append(parts[i]+"\n");
        }
        
        txt = (TextView)findViewById(R.id.nutrients);
        txt.setText(buf.toString());
        
        //Setting the image
        ImageView iv = (ImageView)findViewById(R.id.portrait);
        
        if(product_name.equalsIgnoreCase("Apple"))
        {
        	iv.setImageResource(R.drawable.apple);
        }
        
        else if(product_name.equalsIgnoreCase("Orange"))
        {
        	iv.setImageResource(R.drawable.orange);
        }
        
        else if(product_name.equalsIgnoreCase("Mango"))
        {
        	iv.setImageResource(R.drawable.mango);
        }
        
        else if(product_name.equalsIgnoreCase("Pineapple"))
        {
        	iv.setImageResource(R.drawable.pineapple);
        }
        
        else if(product_name.equalsIgnoreCase("Kiwi"))
        {
        	iv.setImageResource(R.drawable.kiwi);
        }
        
        else if(product_name.equalsIgnoreCase("Tomato"))
        {
        	iv.setImageResource(R.drawable.tomato);
        }
        
        else if(product_name.equalsIgnoreCase("Cherry"))
        {
        	iv.setImageResource(R.drawable.cherry);
        }
        
        else if(product_name.equalsIgnoreCase("Banana"))
        {
        	iv.setImageResource(R.drawable.banana);
        }
      }
	
	public void onAddToCart(View view)
	{
		EditText et = (EditText)findViewById(R.id.kgs);
		no_of_kgs = et.getText().toString();
		CartItem c = new CartItem(product_id, product_name, price_per_kg,no_of_kgs); 
		CartAdapter.items.add(c);
		CartAdapter.total = CartAdapter.total + Double.valueOf(c.totalPrice());
		Toast toast = Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();
        
        finish();
	}
}
