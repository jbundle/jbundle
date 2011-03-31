package org.jbundle.web.httpservice;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * HttpServiceTracker - Wait for the http service to come up to add servlets.
 * 
 * @author don
 *
 */
public class HttpServiceTracker extends ServiceTracker{

	// Set this param to change root URL
	public static final String WEB_CONTEXT = "org.jbundle.web.webcontext";
	
	String webContextPath = null;
	
	/**
	 * Constructor - Listen for HttpService.
	 * @param context
	 */
    public HttpServiceTracker(BundleContext context) {
        super(context, HttpService.class.getName(), null);
    }
    
    /**
     * Http Service is up, add my servlets.
     */
    public Object addingService(ServiceReference reference) {
        HttpService httpService = (HttpService) context.getService(reference);
        try {
        	webContextPath = context.getProperty(WEB_CONTEXT);
        	
        	HttpContext httpContext = null;	// new MyHttpContext(context.getBundle());
        	
            httpService.registerResources(addURLPath(webContextPath, "/"), "", httpContext);
            httpService.registerServlet(addURLPath(webContextPath, "/helloworld"), new HelloWorldServlet(), null, httpContext);

            Dictionary<String,String> dictionary = new Hashtable<String,String>();
            dictionary.put("remotehost", "localhost");
            
            httpService.registerServlet(addURLPath(webContextPath, "/tourapp"), new org.jbundle.base.screen.control.servlet.html.HTMLServlet(), dictionary, httpContext);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpService;
    }
    
    /**
     * Http Service is down, remove my servlets.
     */
    public void removedService(ServiceReference reference, Object service) {
        HttpService httpService = (HttpService) service;
        httpService.unregister(addURLPath(webContextPath, "/"));
        httpService.unregister(addURLPath(webContextPath, "/helloworld"));
        httpService.unregister(addURLPath(webContextPath, "/tourapp"));
        super.removedService(reference, service);
    }
    
    /**
     * Add to http path (**Move this to Util**)
     * @param basePath
     * @param path
     * @return
     */
    public static String addURLPath(String basePath, String path)
    {
    	if (basePath == null)
    		basePath = "";
    	if ((!basePath.endsWith("/")) && (!path.startsWith("/")))
    		path = "/" + path;
    	if (basePath.length() > 0)
    		path = basePath + path;
     	if (path.length() == 0)
    		path = "/";
     	else if ((path.length() > 1) && (path.endsWith("/")))
     		path = path.substring(0, path.length() -1);
    	return path;
    }
}