package org.jbundle.web.httpservice;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Servlet;

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
            Servlet servlet = null;
            Dictionary<String,String> dictionary = null;

        	webContextPath = context.getProperty(WEB_CONTEXT);
        	
        	HttpContext httpContext = null;	// new MyHttpContext(context.getBundle());
        	
            httpService.registerResources(addURLPath(webContextPath, "/"), "", httpContext);

            servlet = new org.jbundle.base.remote.proxy.ProxyServlet();
            dictionary = new Hashtable<String,String>();
            dictionary.put("remotehost", "localhost");
            httpService.registerServlet(addURLPath(webContextPath, "/proxy"), servlet, dictionary, httpContext);

            servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
            dictionary = null;
            dictionary = new Hashtable<String,String>();
            dictionary.put("remotehost", "localhost");
            
            httpService.registerServlet(addURLPath(webContextPath, "/tourapp/table"), servlet, dictionary, httpContext);
            
            servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
            httpService.registerServlet(addURLPath(webContextPath, "/tourapp/image"), servlet, dictionary, httpContext);
            
            servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
            httpService.registerServlet(addURLPath(webContextPath, "/tourapp/jnlp"), servlet, dictionary, httpContext);
            
            servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
            httpService.registerServlet(addURLPath(webContextPath, "/tourapp/wsdl"), servlet, dictionary, httpContext);
            
            servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
            httpService.registerServlet(addURLPath(webContextPath, "/wsdl"), servlet, dictionary, httpContext);
            
            servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
            httpService.registerServlet(addURLPath(webContextPath, "/HTMLServlet"), servlet, dictionary, httpContext);
            
            servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
            httpService.registerServlet(addURLPath(webContextPath, "/tourappajax"), servlet, dictionary, httpContext);
            
            servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
            httpService.registerServlet(addURLPath(webContextPath, "/tourapp.jnlp"), servlet, dictionary, httpContext);
            
            servlet = new org.jbundle.base.screen.control.servlet.html.HTMLServlet();
            httpService.registerServlet(addURLPath(webContextPath, "/tourapp"), servlet, dictionary, httpContext);
            
            dictionary = null;

//            servlet = cocoon();
//            httpService.registerServlet(addURLPath(webContextPath, "/tourapp/*"), servlet, dictionary, httpContext);

            servlet = new org.jbundle.base.screen.control.servlet.xml.XMLServlet();
            dictionary = new Hashtable<String,String>();
            dictionary.put("stylesheet-path", "docs/styles/xsl/ajax/base/");
            dictionary.put("remotehost", "localhost");
            httpService.registerServlet(addURLPath(webContextPath, "/tourappxml"), servlet, dictionary, httpContext);

            dictionary = null;
//          servlet = cocoon();
//          httpService.registerServlet(addURLPath(webContextPath, "/tourappxsl"), servlet, dictionary, httpContext);

//          servlet = cocoon();
//          httpService.registerServlet(addURLPath(webContextPath, "/tourappxhtml"), servlet, dictionary, httpContext);

            //servlet = new XMLServlet();
            //httpService.registerServlet(addURLPath(webContextPath, "/tourappxml"), servlet, dictionary, httpContext);

//            servlet = jnlp.sample.servlet.JnlpDownloadServlet();
//            httpContext = new JnlpHttpContext(context.getBundle());
//            httpService.registerServlet(addURLPath(webContextPath, "*.jnlp"), servlet, dictionary, httpContext);

            httpContext = null;

            servlet = new org.jbundle.base.remote.proxy.AjaxServlet();
            dictionary = new Hashtable<String,String>();
            dictionary.put("remotehost", "localhost");
            httpService.registerServlet(addURLPath(webContextPath, "/ajax"), servlet, dictionary, httpContext);

            dictionary = new Hashtable<String,String>();
            dictionary.put("regex", "www.+.tourgeek.com");
            dictionary.put("regexTarget", "demo/index.html");
            dictionary.put("ie", "tourappxsl");
            dictionary.put("firefox", "tourappxsl");
            dictionary.put("chrome", "tourappxsl");
            dictionary.put("safari", "tourappxsl");
            dictionary.put("webkit", "tourappxsl");
            dictionary.put("java", "tourappxhtml");
            servlet = new org.jbundle.util.webapp.redirect.RegexRedirectServlet();
            httpService.registerServlet(addURLPath(webContextPath, "/index.html"), servlet, dictionary, httpContext);

            servlet = new org.jbundle.base.message.trx.transport.html.MessageServlet();
            dictionary = new Hashtable<String,String>();
            dictionary.put("remotehost", "localhost");
            httpService.registerServlet(addURLPath(webContextPath, "/message"), servlet, dictionary, httpContext);

//+            servlet = new org.jbundle.base.message.trx.transport.soap.MessageReceivingServlet();
//+            dictionary = new Hashtable<String,String>();
//+            dictionary.put("remotehost", "localhost");
//+            httpService.registerServlet(addURLPath(webContextPath, "/ws"), servlet, dictionary, httpContext);

            servlet = new org.jbundle.base.message.trx.transport.xml.XMLMessageReceivingServlet();
            dictionary = new Hashtable<String,String>();
            dictionary.put("remotehost", "localhost");
            httpService.registerServlet(addURLPath(webContextPath, "/xmlws"), servlet, dictionary, httpContext);

            dictionary = null;
/*            servlet = new Cocoon();
            httpService.registerServlet(addURLPath(webContextPath, "*.xml"), servlet, dictionary, httpContext);
*/
//          servlet = new Cocoon();
//          httpService.registerServlet(addURLPath(webContextPath, "/hello.html"), servlet, dictionary, httpContext);

//          servlet = new Cocoon();
//          httpService.registerServlet(addURLPath(webContextPath, "/docs/test/xml/*"), servlet, dictionary, httpContext);

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