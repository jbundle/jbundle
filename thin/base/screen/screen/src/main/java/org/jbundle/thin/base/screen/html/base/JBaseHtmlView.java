/*
 * @(#)HtmlDemo.java    1.4 99/07/23
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.html.base;

import org.jbundle.thin.base.screen.JBasePanel;




/**
 * Html View
 */
public abstract class JBaseHtmlView extends JBasePanel
{
	private static final long serialVersionUID = 1L;

    /**
     * HTMLView Constructor.
     */
    public JBaseHtmlView()
    {
        super();
    }
    /**
     * Initialize the HTMLView window.
     * @param parent The parent screen/applet.
     * @param strURL The initial URL (otherwise, you should supply the URL with the properties).
     */
    public JBaseHtmlView(Object parent, Object strURL)
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
        super.init(parent, strURL);
    }
    /**
     * Free this screen (and get rid of the reference if this is the help screen).
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get html editor.
     * @return
     */
    public abstract JBaseHtmlEditor getHtmlEditor();
}
