/*
 * @(#)HtmlDemo.java    1.4 99/07/23
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util.html;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.text.MessageFormat;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import org.jbundle.thin.base.screen.BaseApplet;


/**
 * Temporary class that loads synchronously (although later than the request so
 * that a cursor change can be done).
 */
public class SyncPageLoader extends Object
    implements Runnable
{
    public static final String HTML_CONTENT = "text/html";

    public static final String DEFAULT_ERROR_TEXT = "<html><body><center><br/>"
        + "Error retrieving document.<br/>"
        + "Document: {0}.<br/>"
        + "Error: <b>{1}</b>."
        + "</center></body></html>";

    /**
     * Cursor to restore to m_control when the work is over.
     */
    protected Object m_oldCursor = null;

    protected URL m_url = null;
    
    protected String m_strHtmlText = null;

    protected String m_strHtmlErrorText = null;

    protected JEditorPane m_editorPane = null;
    /**
     * Optional applet screen to reset cursor/status to when work is over.
     */
    protected BaseApplet m_applet = null;
    
    protected boolean m_bChangeCursor = false;
    
    /**
     * Constructor.
     */
    public SyncPageLoader(URL url, String strHtmlText, JEditorPane editorPane, BaseApplet applet, boolean bChangeCursor)
    {
        super();
        
        this.init(url, strHtmlText, editorPane, applet, bChangeCursor);
    }
    /**
     * Constructor.
     */
    public void init(URL url, String strHtmlText, JEditorPane editorPane, BaseApplet applet, boolean bChangeCursor)
    {
        m_url = url;
        m_editorPane = editorPane;
        m_strHtmlText = strHtmlText;
        m_applet = applet;
        m_bChangeCursor = bChangeCursor;
    }
    /**
     * 
     */
    public void setURL(URL url)
    {
        m_url = url;
    }
    /**
     * Display the target html in the pane.
     */
    public void run()
    {
        if ((m_url == null) && (m_strHtmlText == null))
        {   // restore the original cursor
            if (m_bChangeCursor)
            {
                if (m_applet != null)
                    m_applet.setStatus(0, m_editorPane, m_oldCursor);
                else
                {
                	Component component = m_editorPane;
                	while (component.getParent() != null)
                	{
                		component = component.getParent();
                	}
                    component.setCursor((Cursor)m_oldCursor);
                }
                Container parent = m_editorPane.getParent();
                parent.repaint();
            }
        }
        else
        {
            if (m_bChangeCursor)
            {
                if (m_applet != null)
                    m_oldCursor = m_applet.setStatus(Cursor.WAIT_CURSOR, m_editorPane, null);
                else
                {
                	Component component = m_editorPane;
                	boolean bIsVisible = true;
                	while (component.getParent() != null)
                	{
                		component = component.getParent();
                		if (!component.isVisible())
                			bIsVisible = false;
                	}
                	m_oldCursor = component.getCursor();
                    Cursor cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
                    if (bIsVisible)
                    	component.setCursor(cursor);
                }
            }
            
            synchronized (m_editorPane)
            {
                Document doc = m_editorPane.getDocument();
                try {
                    if (m_url != null) {
                        m_editorPane.setPage(m_url);
                    } else {
                    	this.setText(doc, m_strHtmlText);
                    }
                } catch (IOException ioe) {
                	String error = ioe.getLocalizedMessage();
                	if ((error != null)
                		&& (error.length() > 0))
                	{
                		String errorText = m_strHtmlErrorText;
                		if (errorText == null)
                			errorText = DEFAULT_ERROR_TEXT;
                		errorText = MessageFormat.format(errorText, m_url.toString(), error);
                    	this.setText(doc, errorText);
                	}
                	else
                		m_editorPane.setDocument(doc);
                    // getToolkit().beep();
                } finally {
                    // schedule the cursor to revert after
                    // the paint has happened.
                    m_url = null;
                    m_strHtmlText = null;
                    if (m_bChangeCursor)
                        SwingUtilities.invokeLater(this);
                }
            }
        }
    }
    /**
     * Set the control to this html text.
     * @param doc
     * @param text
     */
    public void setText(Document doc, String text)
    {
        StringReader reader = new StringReader(text);
        HTMLEditorKit kit = (HTMLEditorKit) m_editorPane.getEditorKitForContentType(HTML_CONTENT);
        try {
            int iLength = doc.getLength();
            if (iLength > 0)
                doc.remove(0, iLength);
            kit.read(reader, doc, 0);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }    	
    }
}
