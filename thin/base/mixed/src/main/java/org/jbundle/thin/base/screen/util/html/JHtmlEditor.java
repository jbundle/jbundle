/*
 * @(#)HtmlDemo.java    1.4 99/07/23
 */
package org.jbundle.thin.base.screen.util.html;

import java.awt.Dimension;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.jbundle.model.Freeable;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.print.thread.SwingSyncPageWorker;
import org.jbundle.thin.base.screen.print.thread.SyncPage;
import org.jbundle.thin.base.thread.PrivateTaskScheduler;
import org.jbundle.thin.base.thread.TaskScheduler;


/**
 * Html View
 */
public class JHtmlEditor extends JEditorPane
    implements SyncPage, Freeable
{
	private static final long serialVersionUID = 1L;

	public static final String HTML_CONTENT = "text/html";

    public static final String WAIT_PARAM = "waitHtml";
    public static final String WAIT_TEXT = "<html><head></head><body><center><p><h3>"
        + "Please wait... loading page"
        + "</h3></center></body></html>";

    protected TaskScheduler m_taskScheduler = null;
    
    protected BaseApplet m_applet = null;

    protected JHelpPane m_helpPane = null;

    /**
     * HTMLView Constructor.
     */
    public JHtmlEditor()
    {
        super();
    }
    /**
     * HTMLView Constructor.
     */
    public JHtmlEditor(BaseApplet applet, URL url)
    {
        this();
        this.init(applet, url);
    }
    /**
     * HTMLView Constructor.
     */
    public void init(BaseApplet applet, URL url)
    {
    	m_applet = applet;
    	
        this.setContentType(HTML_CONTENT);
        this.setEditable(false);
        this.addHyperlinkListener(this.createHyperLinkListener());
        this.setOpaque(false);
        this.setSize(new Dimension(500, 800));
        
        if (url != null) 
            this.linkActivated(url, applet, 0);
    }
    /**
     * Free this screen (and get rid of the reference if this is the help screen).
     */
    public void free()
    {
    	if (m_taskScheduler != null)
    	{
			m_taskScheduler.addTask(PrivateTaskScheduler.CLEAR_JOBS);
			m_taskScheduler.addTask(PrivateTaskScheduler.END_OF_JOBS);
	        m_taskScheduler.free();
    	}
    	if (m_callingApplet != null)
    	{
    		m_callingApplet.setHelpView(null);
    		m_callingApplet = null;
    	}
        m_taskScheduler = null;
        m_helpPane = null;
        m_applet = null;
    }
    /**
     * This attribute indicates whether the method
     * paint(Graphics) has been called at least once since the
     * construction of this window.<br>
     * This attribute is used to notify method splash(Image)
     * that the window has been drawn at least once
     * by the AWT event dispatcher thread.<br>
     * This attribute acts like a latch. Once set to true,
     * it will never be changed back to false again.
     *
     * @see #paint
     * @see #splash
     */
    private boolean paintCalled = true;
    /**
     * Paints the image on the window.
     */
    public void paint(Graphics g) {
        super.paint(g);
        
        // Notify the page loader that the window
        // has been painted.
        // Note: To improve performance we do not enter
        // the synchronized block unless we have to.
        if (! paintCalled) {
            paintCalled = true;
            synchronized (this) { notifyAll(); }
        }
    }
    public boolean isPaintCalled()
    {
        return paintCalled;
    }
    
    public void setPaintCalled(boolean bPaintCalled)
    {
        paintCalled = bPaintCalled;
    }
    
    /**
     * Create the hyperlink listener.
     * @return The hyperlink listener.
     */
    public HyperlinkListener createHyperLinkListener()
    {
        return new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent e)
            {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    if (e instanceof HTMLFrameHyperlinkEvent)
                    {
                        ((HTMLDocument)getDocument()).processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent)e);
                    } else {
                        URL url = e.getURL();
                        
                        if (getCallingApplet() != null)
                        	if (getHelpPane() != null)
                        {
                        	String strQuery = url.getQuery();
                        	if ((strQuery != null) && (strQuery.length() > 0) && (strQuery.indexOf(Params.HELP + "=") == -1))
                        	{	// This is a command, not a new help screen
                        		if (BaseApplet.handleAction("?" + strQuery, getCallingApplet(), null, 0))	// Try to have my calling screen handle it
                        			url = null;	// Handled
                        	}
                        }
                        if (url != null)
                        	linkActivated(url, m_applet, 0);
                    }
                }
            }
        };
    }
    /**
     * Set this control to this html text.
     * @param strHtmlText
     */
    public void setHTMLText(String strHtmlText)
    {
    	if (m_helpPane != null)
    		m_helpPane.setVisible(strHtmlText != null);

    	this.getTaskScheduler().addTask(PrivateTaskScheduler.CLEAR_JOBS);     // Dump any stacked tasks
        Thread thread = new SwingSyncPageWorker(this, new SyncPageLoader(null, strHtmlText, this, m_applet, false));
        this.getTaskScheduler().addTask(thread);
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
     * @param iOptions TODO
     */
    public void linkActivated(URL url, BaseApplet applet, int iOptions)
    {
    	if (m_helpPane != null)
    		m_helpPane.setVisible(url != null);

    	if (applet == null)
    		applet = m_applet;
    	if ((iOptions & Constants.DONT_PUSH_HISTORY) == 0)
    		if (applet != null)
    			if (url != null)
    				applet.pushHistory(url.toString(), false);	// Don't push to browser
        
        String strText = WAIT_PARAM;
        if (applet != null)
        	strText = applet.getString(strText);
        if ((strText == null)
            || (strText.length() == 0)
            || (strText.equals(WAIT_PARAM)))
                strText = WAIT_TEXT;
    	if (url != null)
    		this.setHTMLText(strText);
        
        Thread thread = new SwingSyncPageWorker(this, new SyncPageLoader(url, null, this, applet, (url != null)));
        this.getTaskScheduler().addTask(thread);
    }
    /**
     * Get my private task scheduler.
     * @return The scheduler.
     */
    public TaskScheduler getTaskScheduler()
    {
    	if (m_taskScheduler == null)
    		m_taskScheduler = new PrivateTaskScheduler(this.getBaseApplet().getApplication(), 0, true);
    	return m_taskScheduler;
    }
    /**
     * 
     * @return
     */
    public BaseApplet getBaseApplet()
    {
    	return m_applet;
    }
    /**
     * Set the help pane.
     * @param helpPane
     */
    public void setHelpPane(JHelpPane helpPane)
    {
    	m_helpPane = helpPane;
    }
    /**
     * Get the help pane.
     * @param helpPane
     */
    public JHelpPane getHelpPane()
    {
    	return m_helpPane;
    }
    /**
     * Set the calling applet.
     * @param applet
     */
    public void setCallingApplet(BaseApplet applet)
    {
    	m_callingApplet = applet;
    }
    /**
     * Get the calling applet.
     * @return applet
     */
    public BaseApplet getCallingApplet()
    {
    	return m_callingApplet;
    }
    /**
     * The calling applet.
     */
    protected BaseApplet m_callingApplet = null;
}
