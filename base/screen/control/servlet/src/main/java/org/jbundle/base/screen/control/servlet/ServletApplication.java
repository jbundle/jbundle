/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.servlet;

import java.util.Map;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.jbundle.base.model.Utility;
import org.jbundle.base.util.MainApplication;


/**
 * A ServletApplication is the special application for Servlets.
 * It listens for the valueUnbound call from the server to decide when to free itself.
 */
public class ServletApplication extends MainApplication
    implements HttpSessionBindingListener
{

    public boolean inited = false;
    public boolean freed = false;
    public boolean error = false;
    public boolean bound = false;
    public boolean unbound = false;
    public boolean bounderror = false;
    public boolean unbounderror = false;
    public boolean unboundwarning = false;
    
    /**
     * Default constructor.
     */
    public ServletApplication()
    {
        super();
    }
    /**
     * Constructor.
     * @param env The Environment.
     * @param strURL The application parameters as a URL.
     * @param args The application parameters as an initial arg list.
     * @param applet The application parameters coming from an applet.
     */
    public ServletApplication(Object env, Map<String,Object> properties, Object applet)
    {
        this();
        this.init(env, properties, applet);
    }
    /**
     * Initializes the ServletApplication.
     * Usually you pass the object that wants to use this session.
     * For example, the applet or ServletApplication.
     * @param env The Environment.
     * @param strURL The application parameters as a URL.
     * @param args The application parameters as an initial arg list.
     * @param applet The application parameters coming from an applet.
     */
    public void init(Object env, Map<String,Object> properties, Object applet)
    {
        if (inited)
            error = true;
        inited = true;
        super.init(env, properties, applet);
    }
    /**
     * Release the user's preferences.
     */
    public void free()
    {
        if (freed)
            error = true;
        freed = true;
        Utility.getLogger().info("ServletApplication/Free()");
        super.free();
    }
    /**
     * Notifies the object that is being bound to a session and identifies the session.
     */
    public void valueBound(HttpSessionBindingEvent event)
    {
        Utility.getLogger().info("Session bound");
        if (bound)
            bounderror = true;
        bound = true;
    }
    /**
     * Notifies the object that is being unbound from a session and identifies the session.
     */
    public void valueUnbound(HttpSessionBindingEvent event) 
    {
        Utility.getLogger().info("Session Unbound");
        if (unbound)
            unbounderror = true;
        unbound = true;
        if (this.getMainTask() == null)
            this.free();
        else   // Never - This would mean the session was released before the http response was sent
        {
            Utility.getLogger().warning("Unbound error ServletApplication.valueUnbound");
            unboundwarning = true;
        }
    }
}
