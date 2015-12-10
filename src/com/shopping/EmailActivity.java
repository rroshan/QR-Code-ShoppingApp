package com.shopping;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.spopping.R;

public class EmailActivity extends Activity 
{
	private CheckBox checkBox;
	private Handler h;
	private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.email);
        checkBox = (CheckBox)findViewById(R.id.start_id);
        checkBox.setChecked(load());
        h = new Handler();
    }
	
	public void onRestart()
	{
		super.onRestart();
		checkBox.setChecked(load());
	}
	
	public void onStart()
	{
		super.onStart();
		checkBox.setChecked(load());
	}
	
	public void onPause() 
	{
	    super.onPause();
	    save(checkBox.isChecked());
	}

	@Override
	public void onResume() 
	{
	    super.onResume();
	    checkBox.setChecked(load());
	}
	
	public void onStop()
	{
		super.onStop();
		save(checkBox.isChecked());
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		save(checkBox.isChecked());
	}
	
	private void save(final boolean isChecked) 
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	    SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putBoolean("check", isChecked);
	    editor.commit();
	}

	private boolean load() 
	{ 
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	    return sharedPreferences.getBoolean("check", false);
	}
	
	private String loadEmail()
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		return sharedPreferences.getString("email", null);
	}

	public void onDone(View view)
	{
		EditText et = (EditText)findViewById(R.id.email_id);
		String email = et.getText().toString();
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putString("email", email);
	    editor.commit();
	    et.setText("");
	    
	    Toast toast = Toast.makeText(this, "Email ID Set...Now please enter Address!", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 25, 400);
		toast.show();
	}
	
	public static DefaultHttpClient getThreadSafeClient() 
	{
		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,mgr.getSchemeRegistry()), params);
		return client;
	}
	
	public void db_operations(String email,String addr)
	{
		nameValuePairs.add(new BasicNameValuePair("email",email));
		nameValuePairs.add(new BasicNameValuePair("address",addr));
		
		class InnerClass implements Runnable
    	{
    		public void run()
    		{
    			class InnerInnerClass implements Runnable
    			{
    				public void run()
    				{
    					EditText et = (EditText)findViewById(R.id.address);
    					et.setText("");
    					finish();
    				}
    			}
    			
    			try
    	    	{
    	    	     HttpPost httppost = new HttpPost("http://192.168.1.2/add_email_addr.php");
    	    	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	    	     getThreadSafeClient().execute(httppost);
    	    	}
    	    	catch(Exception e)
    	    	{
    	    	         Log.e("log_tag", "Error in http connection "+e.toString());
    	    	}
    			h.post(new InnerInnerClass());
    	    }
    	}
    	
    	Thread t = new Thread(new InnerClass());
    	t.start();
	}
	
	public void onSetAddress(View view)
	{
		EditText et = (EditText)findViewById(R.id.address);
		String address = et.getText().toString();
		
		//Put data in server table
		if(address.equalsIgnoreCase(""))
		{
			Toast toast = Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 25, 400);
			toast.show();
		}
		else
		{
			String email;
			if((email = loadEmail()) != null)
			{
				db_operations(email, address);
			}
		    else
		    {
		    	Toast toast = Toast.makeText(this, "Please enter your email address first!!", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 25, 400);
				toast.show();
		    }
		}
	}
}
