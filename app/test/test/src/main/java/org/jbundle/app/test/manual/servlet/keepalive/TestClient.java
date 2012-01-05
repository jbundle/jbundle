/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.servlet.keepalive;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TestClient extends Object {

	private static final long serialVersionUID = 1L;
	
	private String urlAddress = "http://www.tourgeek.com:8181/test/TestServlet";
	
	public static final void main(String[] args)
	{
		String url = null;
		if (args != null)
			if (args.length > 0)
				url = args[0];
		TestClient client = new TestClient(url);
		client.run();
	}
	
	public TestClient()
	{
		super();
	}
	
	public TestClient(String urlAddress)
	{
		this();
		if (urlAddress != null)
			this.urlAddress = urlAddress;
	}
	
	public void run ()
	{
		try {
			URL url = new URL(urlAddress);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			
			System.out.println(conn.getClass().getName());
			System.out.println("conn: " + conn);
		} catch (MalformedURLException e) { 
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		HttpURLConnection connection = new HttpURLConnection(urlAddress);
	}

}
