/*
 * @(#)HtmlDemo.java    1.4 99/07/23
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.html.base;

import java.net.URL;

import javax.swing.JEditorPane;

import org.jbundle.thin.base.screen.BaseApplet;


/**
 * Html View
 */
public abstract class JBaseHtmlEditor extends JEditorPane
{
	private static final long serialVersionUID = 1L;

    /**
     * HTMLView Constructor.
     */
    public JBaseHtmlEditor()
    {
        super();
    }
    /**
     * Initialize the HTMLView window.
     * @param parent The parent screen/applet.
     * @param strURL The initial URL (otherwise, you should supply the URL with the properties).
     */
    public JBaseHtmlEditor(Object parent, Object strURL)
    {
        this();
        this.init(parent, strURL);
    }
    /**
     * Initialize the HTMLView window.
     * @param parent The parent screen/applet.
     * @param strURL The initial URL (otherwise, you should supply the URL with the properties).
     */
    public void init(Object parent, Object strURL)
    {
    }
    /**
     * Free this screen (and get rid of the reference if this is the help screen).
     */
    public void free()
    {
    }
    /**
     * Get the help pane.
     * @param helpPane
     */
    public abstract JBaseHelpPane getHelpPane();
    /**
     * Set the calling applet.
     * @param applet
     */
    public abstract void setCallingApplet(BaseApplet applet);
    /**
     * Follows the reference in an
     * link.  The given url is the requested reference.
     * By default this calls <a href="#setPage">setPage</a>,
     * and if an exception is thrown the original previous
     * document is restored and a beep sounded.  If an 
     * attempt was made to follow a link, but it represented
     * a malformed url, this method will be called with a
     * null argument.
     *
     * @param url the URL to follow
     */
    public abstract void linkActivated(URL url, BaseApplet applet, int iOptions);
    /**
     * 
     * @return
     */
    public abstract BaseApplet getBaseApplet();
}
