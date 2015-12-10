package com.shopping;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import com.spopping.R;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ShopActivity extends Activity
{
	private String result = "";
	private InputStream is = null;
	private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	private Intent i;
	private double rate = 0.0;
	private String trivia = null;
	private Handler h;
	
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
        setContentView(R.layout.shop_layout);
        h = new Handler();
    }
	
	public void onScanQRCode(View view)
	{
		try 
        {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        }
		catch(ActivityNotFoundException anfe)
		{
			anfe.printStackTrace();
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
        if (requestCode == 0) 
        {
            if (resultCode == RESULT_OK) 
            {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String[] str = contents.split("/");
                       
                i = new Intent(this,AfterScanActivity.class);
                i.putExtra("PRODUCT_ID", str[0]);
                i.putExtra("PRODUCT_NAME", str[1]);
                
                nameValuePairs.add(new BasicNameValuePair("product_id", str[0]));
                
                class InnerClass implements Runnable
            	{
            		public void run()
            		{
            			class InnerInnerClass implements Runnable
            			{
            				public void run()
            				{
            					i.putExtra("PRICE_PER_KG",Double.toString(rate));
            	    	    	i.putExtra("TRIVIA", trivia);
            	                startActivity(i);
            				}
            			}
            			
            			try
            	    	{
            	    	     HttpPost httppost = new HttpPost("http://192.168.1.2/rate.php");
            	    	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            	    	     HttpResponse response = getThreadSafeClient().execute(httppost);
            	    	     HttpEntity entity = response.getEntity();
            	    	     is = entity.getContent();
            	    	}
            	    	catch(Exception e)
            	    	{
            	    	         Log.e("log_tag", "Error in http connection "+e.toString());
            	    	}
            	    	
            	    	//convert response to string
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
        	    	    	rate = json.getDouble("price");
        	    	    	trivia = json.getString("trivia");
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
            
            else if (resultCode == RESULT_CANCELED) 
            {
                Toast toast = Toast.makeText(this, "Scan was Cancelled!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
	}
	
	public void onGoToCart(View view)
	{
		Intent intent = new Intent(this,CartActivity.class);
		startActivity(intent);
	}
}
