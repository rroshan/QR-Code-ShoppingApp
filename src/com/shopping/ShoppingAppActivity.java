package com.shopping;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import com.spopping.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class ShoppingAppActivity extends Activity 
{
	private Handler h;
	private InputStream is;
	private String result;
	private String did_you_know;
	
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
        setContentView(R.layout.main);
        
        h = new Handler();
        
        class InnerClass implements Runnable
    	{
    		public void run()
    		{
    			class InnerInnerClass implements Runnable
    			{
    				public void run()
    				{
    					TextView tv = (TextView)findViewById(R.id.tip_id);
    			    	tv.setText(did_you_know);
    				}
    			}
    			
    			try
    	    	{
    	    	     HttpPost httppost = new HttpPost("http://192.168.1.2/didyouknow.php");
    	    	     HttpResponse response = getThreadSafeClient().execute(httppost);
    	    	     HttpEntity entity = response.getEntity();
    	    	     is = entity.getContent();
    	    	}
    	    	catch(Exception e)
    	    	{
    	    	         Log.e("log_tag", "Error in http connection "+e.toString());
    	    	}
    	    	
    			try
    	    	{
    	    	    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
    	    	    StringBuilder sb = new StringBuilder();
    	    	    String line = null;
    	    	    while ((line = reader.readLine()) != null) 
    	    	    {
    	    	        sb.append(line + "\n");
    	    	    }
    	    	    
    	    	    is.close();

    	    	    result=sb.toString();
    	    	}
    	    	catch(Exception e)
    	    	{
    	    	    Log.e("log_tag", "Error converting result "+e.toString());
    	    	}
    	    	
    	    	try
    	    	{
    	    		JSONObject json = new JSONObject(result);
    	    	    did_you_know = json.getString("fact");
    	    	}
    	    	catch(JSONException e)
    	    	{
    	    	    Log.e("log_tag", "Error parsing data "+e.toString());
    	    	}
    	    	h.post(new InnerInnerClass());
    		}
    	}
    	
    	Thread t = new Thread(new InnerClass());
    	t.start();
    }
    
    public void onQuit(View view)
    {
    	finish();
    }
    
    public void onHistory(View view)
    {
    	Intent intent = new Intent(this,HistoryActivity.class);
    	startActivity(intent);
    }
    
    public void onShop(View view)
    {
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String address = sharedPreferences.getString("email", null);
    	
		if(address != null)
    	{
    		Intent intent = new Intent(this,ShopActivity.class);
    		startActivity(intent);
    	}
    	else
    	{
    		Toast toast = Toast.makeText(this, "Please enter your email address and Delivery address before you can start shopping!!", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 25, 400);
			toast.show();
    	}
    }
    
    public void onSettings(View view)
    {
    	Intent intent = new Intent(this,EmailActivity.class);
    	startActivity(intent);
    }
}