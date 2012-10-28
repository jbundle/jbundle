/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.xslservlet;

/**
 * @(#)DBServlet.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.control.servlet.BasicServlet;
import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.base.screen.control.servlet.html.BaseServlet;
import org.jbundle.base.screen.control.servlet.xml.XMLServlet;


/**
 * XSLServlet
 * 
 * This is the xsl servlet.
 */
public class XSLServlet extends XMLServlet
{
	private static final long serialVersionUID = 1L;

	public XSLServlet()
	{
		super();
	}
	/**
     * returns the servlet info
     */ 
    public String getServletInfo()
    {
        return "This the xsl servlet";
    }
    /**
     * init method.
     * @exception ServletException From inherited class.
     */
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
    }
    /**
     * Destroy this Servlet and any active applications.
     * This is only called when all users are done using this Servlet.
     */
    @Override
    public void destroy()
    {
        super.destroy();
    }
    /**
     * This thread feeds data to the pipe.
     * @author don
     */
    class ProcessOutputThread extends Thread
    {
        Writer outWriter = null;
        ServletTask servletTask = null;
        HttpServletRequest req = null;
        ScreenModel screen = null;
        BasicServlet servlet = null;
        boolean freeWhenDone = false;
        public ProcessOutputThread(BasicServlet servlet, Writer outWriter, ServletTask servletTask, HttpServletRequest req, ScreenModel screen, boolean freeWhenDone)
        {
            super();
            this.outWriter = outWriter;
            this.servletTask = servletTask;
            this.req = req;
            this.screen = screen;
            this.servlet = servlet;
            this.freeWhenDone = freeWhenDone;
        }
        public void run()
        {
            PrintWriter writer = new PrintWriter(outWriter);
            try {
                servletTask.status = 12;
                servletTask.doProcessOutput(servlet, req, null, writer, screen);
                servletTask.status = 11;
            } catch (ServletException e) {
                servletTask.status = 13;
                e.printStackTrace();
            } catch (IOException e) {
                servletTask.status = 14;
                e.printStackTrace();
            } finally {
                servletTask.status = 15;
                writer.flush();
                writer.close();
                if (freeWhenDone)
                    servletTask.free();
            }
            
        }        
    }
    
    /**
     *  process an HTML get or post.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        ServletTask servletTask = new ServletTask(this, BasicServlet.SERVLET_TYPE.COCOON);
        try {
            servletTask.status = 1;
            this.addBrowserProperties(req, servletTask);
            servletTask.status = 2;
    		ScreenModel screen = servletTask.doProcessInput(this, req, null);
            servletTask.status = 3;
    		
            Transformer transformer = getTransformer(req, servletTask, screen); // Screen can't be freed when I call this.
            servletTask.status = 4;
    
            PipedReader in = new PipedReader();
            PipedWriter out = new PipedWriter(in);
            servletTask.status = 5;
            
            new ProcessOutputThread(this, out, servletTask, req, screen, true).start();
            // Note: Print Thread frees the servlettask when it is done.
            servletTask.status = 6;
    
            StreamSource source = new StreamSource(in);
    
            ServletOutputStream outStream = res.getOutputStream();
            Result result = new StreamResult(outStream);
            servletTask.status = 7;

            synchronized (transformer)
            {
                transformer.transform(source, result);  // Get the data from the pipe (thread) and transform it to http
            }
            servletTask.status = 8;
        } catch (TransformerException ex) {
            servletTask.status = 8;
            ex.printStackTrace();
            servletTask.free();
        } catch (ServletException ex) {
            servletTask.status = 9;
            servletTask.free();
            throw ex;
        } catch (IOException ex) {
            servletTask.status = 10;
            servletTask.free();
            throw ex;
        } catch (Exception ex) {
            servletTask.status = 11;
            servletTask.free();
        }
	
    	//x Don't call super.service(req, res);
    }

    private int MAX_CACHED = 20;
    TransformerFactory tFact = null;
    @SuppressWarnings({ "deprecation", "unchecked" })
    Map<String, Transformer> hmTransformers = Collections.synchronizedMap(new org.apache.commons.collections.LRUMap(MAX_CACHED));
    
    /**
     * Get or Create a transformer for the specified stylesheet.
     * @param req
     * @param servletTask
     * @param screen
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public Transformer getTransformer(HttpServletRequest req, ServletTask servletTask, ScreenModel screen)
            throws ServletException, IOException
    {
        String stylesheet = null;
        if (stylesheet == null)
            stylesheet = req.getParameter(DBParams.TEMPLATE);
        if (stylesheet == null)
            if (screen != null)
                if (screen.getScreenFieldView() != null)
                    stylesheet = screen.getScreenFieldView().getStylesheetPath();
        if (stylesheet == null)
            stylesheet = req.getParameter("stylesheet");
        if (stylesheet == null)
            stylesheet = "docs/styles/xsl/flat/base/menus";
        
        try {
            if (hmTransformers.get(stylesheet) != null)
                return hmTransformers.get(stylesheet);
            String stylesheetFixed = BaseServlet.fixStylesheetPath(stylesheet, screen, true);
            
            InputStream stylesheetStream = this.getFileStream(servletTask, stylesheetFixed, null);
            if (stylesheetStream == null)
            {
                stylesheetFixed = BaseServlet.fixStylesheetPath(stylesheet, screen, false);  // Try it without browser mod
                stylesheetStream = this.getFileStream(servletTask, stylesheetFixed, null);
            }
            if (stylesheetStream == null)
                Utility.getLogger().warning("XmlFile not found " + stylesheetFixed);   // TODO - Display an error here
            StreamSource stylesheetSource = new StreamSource(stylesheetStream);
            
            if (tFact == null)
                tFact = TransformerFactory.newInstance();
            URIResolver resolver = new MyURIResolver(servletTask, stylesheetFixed);
            tFact.setURIResolver(resolver);
            Transformer transformer = tFact.newTransformer(stylesheetSource);
            hmTransformers.put(stylesheet, transformer);
            // debug transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            return transformer;
        } catch (TransformerConfigurationException ex)    {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Class to return base path of imports and includes.
     * @author don
     */
    public class MyURIResolver
    	implements URIResolver
    {
    	String mainStylesheet;
    	ServletTask servletTask;
    	
        public MyURIResolver(ServletTask servletTask, String mainStylesheet)
        {
        	super();
        	this.mainStylesheet = mainStylesheet;
        	this.servletTask = servletTask;
        }

        /**
         * Using the main stylesheet, create a path to this stylesheet
         */
		@Override
		public Source resolve(String href, String base)
				throws TransformerException {
			int lastSlash = mainStylesheet.lastIndexOf('/');
			if (lastSlash == -1)
				lastSlash = mainStylesheet.lastIndexOf(File.separatorChar);
			if (lastSlash == -1)
				return null;	// Never
			if ((href.startsWith("/")) || (href.endsWith(File.separator)))
				lastSlash--;
			String sourceStylesheet = mainStylesheet.substring(0, lastSlash + 1) + href;
			
			try {
				return new StreamSource(getFileStream(servletTask, sourceStylesheet, null));
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		}
    	
    }
}
