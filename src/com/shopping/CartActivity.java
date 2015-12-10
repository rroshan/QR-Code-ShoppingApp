package com.shopping;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import com.spopping.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CartActivity extends Activity 
{
	protected static CartAdapter ca;
	private static final int DELETE_ID = 0;
	private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
	public static DefaultHttpClient getThreadSafeClient() 
	{
		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,mgr.getSchemeRegistry()), params);
		return client;
	}
	
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.cart_view);
        //creating the list view....getView will be called implicitly for all the items in the Cart
        ListView listView = (ListView)findViewById(R.id.cart_list);
        ca = new CartAdapter();
        listView.setAdapter(ca);
        //Setting the total
        TextView t = (TextView)findViewById(R.id.total);
        double total = ca.getTotal();
        t.setText("Grand Total: "+Double.toString(total)+" INR");
        registerForContextMenu(listView);
    }
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}
	
	public boolean onContextItemSelected(MenuItem item) 
	{
	    switch(item.getItemId()) 
	    {
	    	case DELETE_ID:
	    		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    		ca.deleteItem(info.id);
	    		ca.notifyDataSetChanged();
	    		
	    		TextView t = (TextView)findViewById(R.id.total);
	            double total = ca.getTotal();
	            t.setText(Double.toString(total)+" INR");
	            
	    		return true;
	    }
	    return super.onContextItemSelected(item);
	}
	
	public boolean fileOperations(ShoppingSession ss)
	{	
		if(ss.getCartItems().isEmpty())
		{
			return false;
		}
		
		String COUNT_FILENAME = "count";
		File countFile = new File("/data/data/com.spopping/files/count");
		if(!countFile.exists())
		{
			int count = 0;
			try 
			{
				FileOutputStream fos = openFileOutput(COUNT_FILENAME,Context.MODE_PRIVATE);
				DataOutputStream dos = new DataOutputStream(fos);
				dos.writeInt(count);
				dos.close();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
				return false;
			}
			catch(IOException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//read the count file and increment count by 1
		int count = 0;
		try
		{
			FileInputStream fis = openFileInput(COUNT_FILENAME);
			DataInputStream dis = new DataInputStream(fis);
			count = dis.readInt();
			count++;
			dis.close();
			
			FileOutputStream fos = openFileOutput(COUNT_FILENAME,Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeInt(count);
			dos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
		
		String HISTORY_FILENAME = "history";
			
		try
		{
			FileOutputStream fos = openFileOutput(HISTORY_FILENAME,Context.MODE_APPEND);
			MyObjectOutputStream oos;
			if(count == 1)
			{
				oos = new MyObjectOutputStream(fos, true);
				oos.writeStreamHeader();
			}
			else
			{
				oos = new MyObjectOutputStream(fos, false);
				oos.writeStreamHeader();
			}
			oos.writeObject(ss);
			oos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void onPurchase(View view)	
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String d = dateFormat.format(cal.getTime());
		
		//Creating a Shopping session object....with the current date and time, Bill total and the cart items.
		ShoppingSession ss = new ShoppingSession(d,CartAdapter.total,CartAdapter.items);
		
		//Sending Email feature
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String address = sharedPreferences.getString("email", null);;
				
		//setting nameValuePairs
		nameValuePairs.add(new BasicNameValuePair("email_id", address));
	    nameValuePairs.add(new BasicNameValuePair("date_time", d));
	    nameValuePairs.add(new BasicNameValuePair("gross_amount", ss.getTotal()));
	    nameValuePairs.add(new BasicNameValuePair("delivered", "No"));
	    
	    class InnerClass implements Runnable
    	{
    		public void run()
    		{
    			try
    	    	{
    	    	     HttpPost httppost = new HttpPost("http://192.168.1.2/total_insert.php");
    	    	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	    	     getThreadSafeClient().execute(httppost);
    	    	}
    	    	catch(Exception e)
    	    	{
    	    	         Log.e("log_tag", "Error in http connection "+e.toString());
    	    	}
    		}
    	}
    	
    	Thread t = new Thread(new InnerClass());
    	t.start();
    	try
    	{
    		t.join();
    	}
    	catch(InterruptedException irre)
    	{
    		irre.printStackTrace();
    	}
		
    	//writing individual items of session into table
    	nameValuePairs = new ArrayList<NameValuePair>();
	    Iterator<CartItem> it = ss.getCartItems().iterator();
	    while(it.hasNext())
	    {
	    	CartItem c = it.next();
	    	nameValuePairs.add(new BasicNameValuePair("email_id", address));
	    	nameValuePairs.add(new BasicNameValuePair("date_time", d));
	    	nameValuePairs.add(new BasicNameValuePair("prod_id", c.getProductId()));
	    	nameValuePairs.add(new BasicNameValuePair("prod_name", c.getProductName()));
	    	nameValuePairs.add(new BasicNameValuePair("prod_ppk", c.getPricePerKg()));
	    	nameValuePairs.add(new BasicNameValuePair("no_of_kgs", c.getNoOfKgs()));
	    	nameValuePairs.add(new BasicNameValuePair("total_price", c.totalPrice()));
	    	
	    	class CartItemClass implements Runnable
	    	{
	    		public void run()
	    		{
	    			try
	    	    	{
	    	    	     HttpPost httppost = new HttpPost("http://192.168.1.2/individual_insert.php");
	    	    	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    	    	     getThreadSafeClient().execute(httppost);
	    	    	}
	    	    	catch(Exception e)
	    	    	{
	    	    	         Log.e("log_tag", "Error in http connection "+e.toString());
	    	    	}
	    		}
	    	}
	    	
	    	t = new Thread(new CartItemClass());
	    	t.start();
	    	try
	    	{
	    		t.join();
	    	}
	    	catch(InterruptedException irre)
	    	{
	    		irre.printStackTrace();
	    	}
	    }
	    
	    //Saving to History
	    if(fileOperations(ss))
	    {
	    	Toast toast = Toast.makeText(this, "Purchase Successful!!", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 25, 400);
			toast.show();
	    }
	    
	    //sending mail
	    boolean mail =  sharedPreferences.getBoolean("check", false);
	    if(mail)
	    {
	    	File f = new File("/data/data/com.spopping/files/email");
	    	if(f.exists())
	    	{
	    		//email intent
	    		Intent sendIntent = new Intent(Intent.ACTION_SEND);
	    		//setting information type
	    		sendIntent.setType("plain/text");
	    		//setting the email address
	    		sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{address});
	    		//Setting subject
	    		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Smart Shopping Receipt");
	    		//Setting Text
	    		sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, ss.toString());
	    		//Send Email
	    		startActivity(sendIntent);
	    	}
	    }
	    
	    CartAdapter.items.clear();
        ca.notifyDataSetChanged();
        CartAdapter.total = 0.0;
        TextView txt = (TextView)findViewById(R.id.total);
        double total = ca.getTotal();
        txt.setText(Double.toString(total)+" INR");
	}
}
