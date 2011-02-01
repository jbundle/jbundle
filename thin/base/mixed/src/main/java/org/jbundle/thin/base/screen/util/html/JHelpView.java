/*
 * @(#)HtmlDemo.java    1.4 99/07/23
 */
package org.jbundle.thin.base.screen.util.html;

import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.screen.BaseApplet;

/**
 * Html View
 */
public class JHelpView extends JHtmlView
{
	private static final long serialVersionUID = 1L;

    /**
     * HTMLView Constructor.
     */
    public JHelpView()
    {
        super();
    }
    /**
     * Initialize the HTMLView window.
     * @param parent The parent screen/applet.
     * @param strURL The initial URL (otherwise, you should supply the URL with the properties).
     */
    public JHelpView(Object parent, Object strURL)
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
        BaseApplet applet = null;
        if (parent instanceof BaseApplet)
            applet = (BaseApplet)parent;
        if (strURL instanceof BaseApplet)
            applet = (BaseApplet)strURL;
        if (applet == null)
            applet = this.getBaseApplet();

        if (strURL == null)
        	if (applet != null)
        		if (applet.getProperty(Params.URL) == null)
        			strURL = "?" + Params.HELP + "=";

        this.setOpaque(false);
        super.init(parent, strURL);

        if (parent instanceof BaseApplet)
        	((BaseApplet)parent).setHelpView(this.getHtmlEditor());
    }
    /**
     * Free this screen (and get rid of the reference if this is the help screen).
     */
    public void free()
    {
        super.free();
    }
 }
