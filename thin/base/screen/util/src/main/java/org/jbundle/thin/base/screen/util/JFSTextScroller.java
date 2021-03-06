/*
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.jbundle.model.DBException;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.FieldComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.model.util.Constant;


/** 
 * A JFSTextScroller is a text pane that has a built in scroller.
 * JFSTextScroller is a button that works in a JTable.
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class JFSTextScroller extends JPanel
    implements FieldComponent, KeyListener
{
	private static final long serialVersionUID = 1L;

	public Dimension PREFERRED_SIZE = new Dimension(350, 80);
    
    /**
     * The text control.
     */
    protected JTextArea m_control = null;
    /**
     * Has the text field been changed by this user yet?
     */
    protected boolean m_bDirty = false;

    /**
     * Creates new JFSTextScroller.
     */
    public JFSTextScroller()
    {
        super();
    }
    /**
     * Creates new JFSTextScroller.
     * @param text The checkbox description.
     */
    public JFSTextScroller(Object text)
    {
        this();
        this.init(text);
    }
    /**
     * Creates new JThreeStateCheckBox.
     * @param text The checkbox description.
     */
    public void init(Object text)
    {
        this.setOpaque(false);
        this.setPreferredSize(PREFERRED_SIZE);
        int rows = 1;
        int cols = 30;
        m_control = new JTextArea(rows, cols);
        m_control.setBorder(null);      // No border inside a scroller.
        JScrollPane scrollpane = new JScrollPane(m_control, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setLayout(new BorderLayout());
        this.add(scrollpane);
        this.setControlValue(text);
        m_control.addKeyListener(this);
    }
    /**
     * Free this object's resources.
     */
    public void free()
    {
    }
    /**
     * Get the value (On, Off or Null).
     * @return The raw data (a Boolean).
     */
    public Object getControlValue()
    {
        return m_control.getText();
    }
    /**
     * Set the value.
     * @param objValue The raw data (a Boolean).
     */
    public void setControlValue(Object objValue)
    {
        if (objValue instanceof String)
            m_control.setText(objValue.toString());
        m_bDirty = false;
    }
    
    public void keyReleased(java.awt.event.KeyEvent keyEvent)
    {
    }
    
    public void keyTyped(java.awt.event.KeyEvent keyEvent)
    {
    }
    /**
     * key released, if tab, select next field.
     */
    public void keyPressed(KeyEvent evt)
    {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            boolean bDoTab = true;
            int iTextLength = m_control.getDocument().getLength();
            if (((m_control.getSelectionStart() == 0) || (m_control.getSelectionStart() == iTextLength))
                    && ((m_control.getSelectionEnd() == 0) || (m_control.getSelectionEnd() == iTextLength)))
                bDoTab = true;
            else
                bDoTab = false;     // Not fully selected, definitely process this key
            if (bDoTab)
            {
                if (m_bDirty)
                    bDoTab = false;     // Data changed, definetly process this key
            }

            if (evt.getKeyCode() == KeyEvent.VK_ENTER)
                if (!bDoTab)
                    return;     // Do not consume... process the return in the control
            evt.consume();  // Don't process the tab as an input character... tab to the next/prev field.
            this.transferFocus(); // Move focus to next component
            return;
        }
        m_bDirty = true;
    }
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Convert getConverter()
    {
        return null;
    }
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public void setConverter(Convert converter)
    {
    }
    /**
     * Enable or disable this control.
     * @param bEnable If true, enable this field.
     */
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);  // Nice, this component has this method already
        m_control.setEnabled(enabled);
    }
    /**
     * Get the top level screen.
     * @return The top level screen.
     */
    public ComponentParent getParentScreen()
    {
        return null;
    }
    /**
     * Move the control's value to the field.
     * @return An error value.
     */
    public int controlToField()
    {
        return Constant.NORMAL_RETURN;
    }
    /**
     * Move the field's value to the control.
     */
    public void fieldToControl()
    {
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLoc itsLocation, ComponentParent parentScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this.init(null);
    }
    /**
     * Request focus?
     * @param bIsFocusTarget If true this is a focus target.
     */
    public void setRequestFocusEnabled(boolean bIsFocusTarget)
    {
    }
    /**
     * Get the physical component associated with this view.
     * @return The physical control.
     */
    @Override
    public Object getControl()
    {
        return this;
    }
    /**
     * Move the HTML input to the screen record fields.
     * @param strSuffix Only move fields with the suffix.
     * @return true if one was moved.
     * @exception DBException File exception.
     */
    public int setSFieldToProperty(String strSuffix, boolean bDisplayOption, int iMoveMode)
    {
        return Constant.NORMAL_RETURN;
    }
}
