package com.shopping;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class MyObjectOutputStream extends ObjectOutputStream 
{
	private boolean header;
	
	protected MyObjectOutputStream(OutputStream out,boolean header) throws IOException 
	{
		super(out);
		this.header = header;
	}
	
	protected void writeStreamHeader() throws IOException
	{
		if(header)
		{
			super.writeStreamHeader();
		}
	}
}
