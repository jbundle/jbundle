/*
 * @(#)HtmlDemo.java    1.4 99/07/23
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util.html;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseToolbar;
import org.jbundle.thin.base.screen.html.base.JBaseHtmlView;
import org.jbundle.thin.base.util.Application;


/**
 * Html View
 */
public class JHtmlView extends JBaseHtmlView
{
	private static final long serialVersionUID = 1L;

    /**
     * The actual editor pane.
     */
    protected JHtmlEditor m_editorPane = null;
    
    /**
     * HTMLView Constructor.
     */
    public JHtmlView()
    {
        super();
    }
    /**
     * Initialize the HTMLView window.
     * @param parent The parent screen/applet.
     * @param strURL The initial URL (otherwise, you should supply the URL with the properties).
     */
    public JHtmlView(Object parent, Object strURL)
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
        
        this.setLayout(new BorderLayout());

        BaseApplet applet = null;
        if (strURL instanceof BaseApplet)
            applet = (BaseApplet)strURL;
        if (applet == null)
            applet = this.getBaseApplet();
        URL url = null;
        String path = null;
        if ((strURL instanceof String) && (((String)strURL).length() > 0))
            path = strURL.toString();
        else if (strURL instanceof URL)
            url = (URL)strURL;
        else if (applet != null)
            path = applet.getProperty(Params.URL);
        //System.out.println("URL: " + path);
        if ((url == null) && (path != null))
            url = JHtmlView.getURLFromPath(path, applet);

        if (applet == null)
            applet = BaseApplet.getSharedInstance();
        m_editorPane = new JHtmlEditor(applet, url);
        m_editorPane.setOpaque(false);

        JScrollPane scrollpane = new JScrollPane();
        scrollpane.setOpaque(false);  // If no background, show mine.
        scrollpane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollpane.setPreferredSize(new Dimension(10,10));
        JViewport vp = scrollpane.getViewport();
        vp.setOpaque(false);
        vp.add(m_editorPane);
        this.add(scrollpane, BorderLayout.CENTER);
    }
    /**
     * Free this screen (and get rid of the reference if this is the help screen).
     */
    public void free()
    {
    	m_editorPane.free();	// You will still need to remove it
        super.free();
    }
    /**
     * Get the URL from the path.
     * @param The resource path.
     * @return The URL.
     */
    public static URL getURLFromPath(String path, BaseApplet applet)
    {
        URL url = null;
        try {
            if ((url == null) && (path != null))
                url = new URL(path);
        } catch (MalformedURLException ex) {
            Application app = null;
            if (applet == null)
            	applet = (BaseApplet)BaseApplet.getSharedInstance().getApplet();
            if (applet != null)
            	app = applet.getApplication();
            if ((app != null) && (url == null) && (path != null))
                url = app.getResourceURL(path, applet);
            else
                ex.printStackTrace();
        }
        return url;
    }
    /**
     * Process this action.
     * @param strAction The command to execute.
     * @return True if handled.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        if (Constants.BACK.equalsIgnoreCase(strAction))
        {
            String strPrevAction = this.getBaseApplet().popHistory(1, true);   // Current screen
            strAction = getBaseApplet().popHistory(1, true);   // Last screen
            if (strAction != null)
            {
                if (!Constants.BACK.equalsIgnoreCase(strAction)) // Never (prevent endless recursion)
                    return this.handleAction(strAction, this, iOptions);
            }
            else if (strPrevAction != null)
                this.getBaseApplet().pushHistory(strPrevAction, ((iOptions & Constants.DONT_PUSH_TO_BROSWER) == Constants.PUSH_TO_BROSWER));    // If top of stack, leave it alone.
        }
        else
        { // The only other actions are URLS, so change the page.
            URL url = null;
            try {
            	url = new URL(strAction);
            } catch (MalformedURLException e) {
            	url = JHtmlView.getURLFromPath(strAction, this.getBaseApplet());
            }
            if (url != null)
            	m_editorPane.linkActivated(url, this.getBaseApplet(), 0);
        }
        return true;    // Handled
    }
    /**
     * Add the toolbars?
     * @return The newly created toolbar or null.
     */
    public JComponent createToolbar()
    {
        return new JBaseToolbar(this, null);
    }
    /**
     * Get the command string that can be used to create this screen.
     * @return The screen command (defaults to ?applet=&screen=xxx.xxx.xxxx).
     */
    public String getScreenCommand()
    {
        return null;
    }
    public JEditorPane getControl()
    {
        return m_editorPane;
    }
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
    public void linkActivated(URL url)
    {
    	this.linkActivated(url, this.getBaseApplet());
    }
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
    public void linkActivated(URL url, BaseApplet applet)
    {
    	m_editorPane.linkActivated(url, applet, 0);
    }
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
    public void linkActivated(URL url, BaseApplet applet, int iOptions)
    {
    	m_editorPane.linkActivated(url, applet, iOptions);
    }
    /**
     * Get html editor.
     * @return
     */
    public JHtmlEditor getHtmlEditor()
    {
    	return m_editorPane;
    }
}
