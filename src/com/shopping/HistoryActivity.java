package com.shopping;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;

import com.spopping.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class HistoryActivity extends Activity 
{
	protected static HistoryAdapter ha;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.history_view);
        
        ListView lv = (ListView)findViewById(R.id.history_list);
        //clearing the contents of the History Adapter...initially
        HistoryAdapter.arr.clear();
        //creating a new history adapter
        ha = new HistoryAdapter();
        fileOperations();
        lv.setAdapter(ha);
        
        //setOnClickItemListener
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position,long id)
            {
        		Intent intent = new Intent(HistoryActivity.this,Receipt.class);
        		intent.putExtra("PRODUCT_ID", position);
        		startActivity(intent);
            }
        });
	}
	
	
	public void fileOperations()
	{
		String COUNT_FILENAME = "count";
		File countFile = new File("/data/data/com.spopping/files/count");
		
		if(!countFile.exists())
		{
			Toast toast = Toast.makeText(this, "You have no Shopping history", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 25, 400);
			toast.show();
			finish();
		}
		
		int count = 0;
		try
		{
			FileInputStream fis = openFileInput(COUNT_FILENAME);
			DataInputStream dis = new DataInputStream(fis);
			count = dis.readInt();
			dis.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		int i=0;
		String HISTORY_FILENAME = "history";
		ShoppingSession ss;
		
		try
		{
			FileInputStream fis = openFileInput(HISTORY_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			for(i=0;i<count;i++)
			{
				ss = (ShoppingSession)ois.readObject();
				HistoryAdapter.arr.add(ss);
			}
			ois.close();
		}
		catch(ClassNotFoundException c)
		{
			c.printStackTrace();
		}
			
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		Collections.sort(HistoryAdapter.arr);
	}
}
