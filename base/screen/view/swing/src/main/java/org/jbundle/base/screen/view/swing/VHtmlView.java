package org.jbundle.base.screen.view.swing;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.StringWriter;
import java.net.URL;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.util.html.SyncPageLoader;


/**
 * Multi-line scrollable HTML text box.
 */
public class VHtmlView extends VTEView
{
    public static final String HTML_CONTENT = "text/html";
    /**
     *
     */
    protected SyncPageLoader m_pageLoader = null;

    /**
     * Constructor.
     */
    public VHtmlView()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VHtmlView(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public void init(ScreenField model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        if (m_pageLoader != null)
            m_pageLoader.setURL(null);  // Stop it if it hasn't started yet.

        if (this.getControl(DBConstants.CONTROL_TO_FREE) != null)
            if (m_bEditableControl)
        {
            this.getControl(DBConstants.CONTROL_TO_FREE).removeFocusListener(this);
            this.getControl(DBConstants.CONTROL_TO_FREE).removeKeyListener(this);
            m_bEditableControl = false;     // Don't remove more listeners in super
        }
        super.free();
    }
    /**
     * Create the physical control.
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument)htmlKit.createDefaultDocument(); // Create an empty document to start
//?        StyleSheet styles = doc.getStyleSheet();        // target pane contains the formatted HTML
        
        JTextPane control = new JTextPane();
        ((JTextPane)control).setDocument(doc);
        control.setEditorKit(htmlKit);

        control.setBorder(null);        // No border inside a scroller.
        if (bEditableControl)
        {
            control.addFocusListener(this);
            control.addKeyListener(this);
        }
        new JScrollPane(control, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return control;
    }
    /**
     * Get one of the physical components associated with this SField.
     * @param int iLevel    CONTROL_TOP - Parent physical control; CONTROL_BOTTOM - Lowest child control
     * NOTE: This method is used for complex controls such as a scroll box, where this control must be
     * added to the parent, but sub-controls must be added to a lower level physical control.
     * @param iLevel The level for this control (top/bottom/etc).
     * @return The control for this view.
     */
    public Component getControl(int iLevel)
    {
        if (iLevel == DBConstants.CONTROL_TOP)
        {
            Container parent = this.getControl().getParent();
            while (!(parent instanceof JScrollPane))
            {
                parent = parent.getParent();
            }
            if (parent != null)
                return parent;  // scrollpane
        }
        return super.getControl(iLevel);
    }
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @param control The control to get the state from.
     * @return The control's value.
     */
    public Object getComponentState(Component control)
    {
        String string = null;
        HTMLEditorKit htmlKit = (HTMLEditorKit)((JTextPane)control).getEditorKit();
        try {
            Document dstDoc = ((JTextPane)control).getDocument();
            StringWriter writer = new StringWriter();
            int len = dstDoc.getLength();
            htmlKit.write(writer, dstDoc, 0, len);
            string = writer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (string == null)
            string = Constants.BLANK;
        return string;
    }
    /**
     * Set the component to this state. State is defined by the component.
     * @param control The control to set the state to.
     * @param objValue The value to set the control to.
     */
    public void setComponentState(Component control, Object objValue)
    {
        String strText = (String)objValue;
        if (strText == null)
            strText = DBConstants.BLANK;
        JTextPane textControl = (JTextPane)this.getControl();
        if (m_pageLoader != null)
            m_pageLoader.setURL(null);  // Stop it if it hasn't started yet.
        BaseApplet applet = (BaseApplet)this.getScreenField().getRootScreen().getAppletScreen().getScreenFieldView().getControl();
        SwingUtilities.invokeLater(m_pageLoader = new SyncPageLoader(null, strText, textControl, applet, true));
    }
    /**
     * Key released, if tab, select next field.
     * @param evt The Key event.
     */
    public void keyPressed(KeyEvent evt)
    {
        super.keyPressed(evt);
    }
    /**
     *  This control gained focus (A Focus Listener).
     * @param evt The focus event.
     */
    public void focusGained(FocusEvent evt)
    {
        super.focusGained(evt);
        JTextPane control = (JTextPane)this.getControl();
        Document doc = control.getDocument();
        int iPos = doc.getEndPosition().getOffset();
        if (control.getCaretPosition() == 0)    // Position hasn't been set by mouse click
            if (iPos > 1)
                control.setCaretPosition(iPos - 1); // Then, set the position to the end.
    }
    /**
     * Create the hyperlink listener.
     * @return The hyperlink listener.
     */
    public HyperlinkListener setupHyperLinkListener(ScreenField actionTarget)
    {
        HtmlLinkListener listener = new HtmlLinkListener(actionTarget);
        JTextPane control = (JTextPane)this.getControl();
        control.addHyperlinkListener(listener);
        return listener;
    }
        class HtmlLinkListener extends Object
            implements HyperlinkListener
        {
            ScreenField m_actionTarget = null;
            public HtmlLinkListener(ScreenField actionTarget)
            {
                super();
                m_actionTarget = actionTarget;
            }
            public void hyperlinkUpdate(HyperlinkEvent e)
            {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    if (e instanceof HTMLFrameHyperlinkEvent)
                    {
                        JTextPane control = (JTextPane)getControl();
                        ((HTMLDocument)control.getDocument()).processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent)e);
                    } else {
                        if (m_actionTarget == null)
                            linkActivated(e.getURL());
                        else
                            m_actionTarget.handleCommand(e.getDescription(), null, ScreenConstants.USE_SAME_WINDOW | DBConstants.PUSH_TO_BROSWER);
                    }
                }
            }
        };
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
        JTextPane control = (JTextPane)this.getControl();
        if (m_pageLoader != null)
            m_pageLoader.setURL(null);  // Stop it if it hasn't started yet.
        BaseApplet applet = (BaseApplet)this.getScreenField().getRootScreen().getAppletScreen().getScreenFieldView().getControl();
        SwingUtilities.invokeLater(m_pageLoader = new SyncPageLoader(url, null, control, applet, true));
    }
}
