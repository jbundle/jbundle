/*
 * MuffinManager.java
 *
 * Created on January 30, 2001, 12:14 AM
 */
 
package org.jbundle.thin.base.screen;

import java.applet.Applet;
import java.util.HashMap;
import java.util.Map;

import netscape.javascript.JSObject;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.util.Util;


/** 
 * BrowserManager - This code handles the browser interface for the java web start program.
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class BrowserManager extends Object
{
	private Applet m_applet = null;
	
	protected Map<String,Object> m_propertiesInitialCommand = null;
	
    /**
     * Creates new BrowserManager.
     */
    public BrowserManager()
    {
        super();
    }
    /**
     * Creates new BrowserManager.
     * @param applet The applet object.
     * @param strInitialCommand The initial command params
     */
    public BrowserManager(Applet applet, Map<String,Object> mapInitialCommands)
    {
        this();
        this.init(applet, mapInitialCommands);
    }
    /**
     * Creates new MuffinManager.
     * @param applet The applet object.
     */
    public void init(Applet applet, Map<String,Object> mapInitialCommands)
    {
    	m_applet = applet;
		m_propertiesInitialCommand = mapInitialCommands;
    }
    /**
     * Push this command onto the browser's history stack.
     * @param command
     */
	public void pushBrowserHistory(String command, String title)
	{
        String args[] = new String[2];
        args[0] = this.getCommandForBrowser(command);
    	if (title == null)
    		title = Constants.BLANK;
        args[1] = title;
        this.callJavascript("pushBrowserHistory", args);
	}
	/**
	 * Given this command, return the full command in an array of 1.
	 * @param command
	 * @return
	 */
	public String getCommandForBrowser(String command)
	{
        if ((command != null) || (m_propertiesInitialCommand != null))
        {
    		Map<String,Object> properties = new HashMap<String,Object>();
    		if (m_propertiesInitialCommand != null)
    			properties.putAll(m_propertiesInitialCommand);
        	if (command != null)
        		Util.parseArgs(properties, command);	// Note - duplicates will go with this version
    		command = Constants.BLANK;
    		for (String key : properties.keySet())
    		{
    			if (("samewindow".equalsIgnoreCase(key))
        			|| ("navmenus".equalsIgnoreCase(key))
    				|| ("menubars".equalsIgnoreCase(key)))
    					continue;
    			if (properties.get(key) == null)
    				properties.put(key, Constants.BLANK);
    			command = Util.addURLParam(command, key, properties.get(key).toString());
    		}
        }
        return command;
	}
	/**
	 * Pop some commands from the browser's history.
	 * Note: The browser will NOT call your java doJavaBrowserBack method, you need to do the navigation yourself.
	 * Note: If you pop more commands than you have on the stack, the browser will back up
	 * into it's html page history and display the target web page.
	 * @param commandsToPop The number of commands to pop from the history.
	 */
	public void popBrowserHistory(int commandsToPop, boolean bCommandHandledByJava, String title)
	{
        String args[] = new String[3];
    	args [0] = Integer.toString(commandsToPop);
    	args [1] = Boolean.toString(bCommandHandledByJava);
    	if (title == null)
    		title = Constants.BLANK;
        args [2] = title;
        this.callJavascript("popBrowserHistory", args);
	}
	/**
	 * Call javascript with this command.
	 * @param command
	 * @param args
	 */
	public void callJavascript(String command, String args[])
	{
		try {
	        JSObject win = JSObject.getWindow(m_applet);
	        win.call(command, args);
		} catch (Exception ex) {
			// Ignore any errors
		}		
	}
}
