/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model;

import java.net.URL;

public interface BaseAppletReference extends Freeable {
    /**
     * Initializes the applet.  You never need to call this directly; it is
     * called automatically by the system once the applet is created.
     */
    public void init();
    /**
     * Called to start the applet.  You never need to call this directly; it
     * is called when the applet's document is visited.
     */
    public void start();
    /**
     * Gets the base URL. This is the URL of the directory which contains this applet.  
     *
     * @return  the base {@link java.net.URL} of
     *          the directory which contains this applet.
     * @see     java.applet.Applet#getDocumentBase()
     */
    public URL getCodeBase();
    /**
     * If this is an applet, return the instance.
     * If Standalone, return a null.
     * @return The applet if this was started as an applet.
     */
    public BaseAppletReference getApplet();
    /**
     * Display the status text.
     * @param strMessage The message to display.
     */
    public Object setStatus(int iStatus, Object comp, Object cursor);
    /**
     * Get the display preference for the help window.
     * @return
     */
    public int getHelpPageOptions(int iOptions);
    /**
     * Push this command onto the history stack.
     * @param strHistory The history command to push onto the stack.
     */
    public void pushBrowserHistory(String strHistory, String browserTitle, boolean bPushToBrowser);
    /**
     * Pop this command off the history stack.
     * NOTE: Do not use this method in most cases, use the method in BaseApplet.
     * @return The history command on top of the stack.
     */
    public void popBrowserHistory(int quanityToPop, boolean bPushToBrowser, String browserTitle);
}
