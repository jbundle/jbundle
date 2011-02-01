package org.jbundle.base.screen.control.servlet;

import java.util.Map;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.swing.JApplet;

import org.jbundle.base.util.MainApplication;
import org.jbundle.base.util.Utility;


/**
 * A ServletApplication is the special application for Servlets.
 * It listens for the valueUnbound call from the server to decide when to free itself.
 */
public class ServletApplication extends MainApplication
    implements HttpSessionBindingListener
{

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
    public ServletApplication(Object env, Map<String,Object> properties, JApplet applet)
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
    public void init(Object env, Map<String,Object> properties, JApplet applet)
    {
        super.init(env, properties, applet);
    }
    /**
     * Release the user's preferences.
     */
    public void free()
    {
        Utility.getLogger().info("ServletApplication/Free()");
        super.free();
    }
    /**
     * Notifies the object that is being bound to a session and identifies the session.
     */
    public void valueBound(HttpSessionBindingEvent event)
    {
        Utility.getLogger().info("Session bound");
    }
    /**
     * Notifies the object that is being unbound from a session and identifies the session.
     */
    public void valueUnbound(HttpSessionBindingEvent event) 
    {
        Utility.getLogger().info("Session Unbound");
        if (this.getMainTask() == null)
            this.free();
        else
            Utility.getLogger().info("Unbound error ServletApplication/77");
    }
}
