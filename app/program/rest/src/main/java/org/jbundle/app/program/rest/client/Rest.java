package org.jbundle.app.program.rest.client;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Hello world!
 *
 */
public class Rest 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
    
    public Rest()
    {
    	
    }
    
    public void run()
    {
    	Client client = Client.create();
    	
    	WebResource webResource = client.resource("http://example.com/base");
    	
    	String s = webResource.get(String.class);
    	
    	MultivaluedMap queryParams = new MultivaluedMapImpl();
    	   queryParams.add("param1", "val1");
    	   queryParams.add("param2", "val2");
    	s = webResource.queryParams(queryParams).get(String.class);    	
    	
    	 ClientResponse response = webResource.path("user/123").delete(ClientResponse.class);
    }
}
