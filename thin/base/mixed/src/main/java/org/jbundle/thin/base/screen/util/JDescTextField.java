package org.jbundle.thin.base.screen.util;

/**
 * OrderEntry.java:   Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

import org.jbundle.thin.base.db.Constants;


/**
 * This is a JTextField that automatically displays an italic field
 * description when the field is blank.
 */
public class JDescTextField extends JTextField
{
	private static final long serialVersionUID = 1L;

	/**
     * The description to display for a blank field.
     */
    protected String m_strDescription = null;
    /**
     * Is this field currently focused?
     */
    protected boolean m_bInFocus = false;
    /**
     * The action listener for this field (must be removed and return added for this to work).
     */
    protected ActionListener m_actionListener = null;
    /**
     * Cached copy of the original font.
     */
    protected Font m_fontNormal = null;
    /**
     * Cached copy of the italicized font.
     */
    protected Font m_fontDesc = null;
    /**
     * Cached copy of the original color.
     */
    protected Color m_colorNormal = null;
    /**
     * Cached copy of the italicized font.
     */
    protected Color m_colorDesc = null;

    /**
     * Constructor.
     * @param cols The columns for this text field.
     * @param strDescription The description to display when this component is blank.
     * @param actionListener The action listener for this field (must be removed and return added for this to work).
     */
    public JDescTextField(int cols, String strDescription, ActionListener actionListener)
    {
        super(cols);
        this.init(cols, strDescription, actionListener);
    }
    /**
     * Constructor.
     * @param cols The columns for this text field.
     * @param strDescription The description to display when this component is blank.
     * @param actionListener The action listener for this field (must be removed and return added for this to work).
     */
    public void init(int cols, String strDescription, ActionListener actionListener)
    {
        m_strDescription = strDescription;
        m_actionListener = actionListener;
        this.setText(null);
        this.addFocusListener(new FocusAdapter()
        { // Make sure a tab with a changed field triggers action performed.
            String m_strOldValue;
            public void focusGained(FocusEvent evt)
            {
                myFocusGained();
                m_strOldValue = getText();
                super.focusLost(evt);
            }
            public void focusLost(FocusEvent evt)
            {
                super.focusLost(evt);
                myFocusLost();
                if (m_actionListener != null)
                    if (!m_strOldValue.equalsIgnoreCase(getText()))
                        m_actionListener.actionPerformed(new ActionEvent(JDescTextField.this, evt.getID(), null));
            }
        });
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setAlignmentY(TOP_ALIGNMENT);
        if (m_actionListener != null)
            this.addActionListener(m_actionListener);   // Validate on change
    }
    /**
     * Get text from JTextField.
     * This method factors out the description.
     * @return The text for this component.
     */
    public String getText()
    {
        String strText = super.getText();
        if (strText != null)
            if (strText.equals(m_strDescription))
                strText = Constants.BLANK;
        return strText;
    }
    /**
     * Set this text component to this text string.
     * This method factors out the description.
     * @param text The text for this component.
     */
    public void setText(String strText)
    {
        if ((strText == null) || (strText.length() == 0))
        {
            if (!m_bInFocus)
            {
                strText = m_strDescription;
                this.changeFont(true);
            }
        }
        else
            this.changeFont(false);
        super.setText(strText);
    }
    /**
     * Gained the focus.
     * Need to get rid of the description (temporarily).
     */
    public void myFocusGained()
    {
        m_bInFocus = true;
        String strText = super.getText();
        if (m_strDescription.equals(strText))
        {   // Don't notify of change
            if (m_actionListener != null)
                this.removeActionListener(m_actionListener);
            this.changeFont(false);
            super.setText(Constants.BLANK);
            if (m_actionListener != null)
                this.addActionListener(m_actionListener);
        }
    }
    /**
     * Gained the focus.
     * Need to set the description back if this component is empty.
     */
    public void myFocusLost()
    {
        m_bInFocus = false;
        String strText = super.getText();
        if ((strText == null) || (strText.length() == 0))
        {   // Don't notify of change
            if (m_actionListener != null)
                this.removeActionListener(m_actionListener);
            this.changeFont(true);
            super.setText(m_strDescription);
            if (m_actionListener != null)
                this.addActionListener(m_actionListener);
        }
    }
    /**
     * Change the font depending of whether you are displaying the description or text.
     * @param bDescription If true, set the description text.
     */
    public void changeFont(boolean bDescription)
    {
        if (m_fontNormal == null)
        {
            m_fontNormal = this.getFont();
            m_fontDesc = m_fontNormal.deriveFont(Font.ITALIC);
            m_colorNormal = this.getForeground();
            m_colorDesc = Color.gray;
        }
        if (bDescription)
        {
            this.setFont(m_fontDesc);
            this.setForeground(m_colorDesc);
        }
        else
        {
            this.setFont(m_fontNormal);
            this.setForeground(m_colorNormal);
        }
    }
}
