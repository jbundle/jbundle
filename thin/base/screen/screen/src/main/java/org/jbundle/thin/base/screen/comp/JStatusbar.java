/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.comp;

/**
 * ErrorDialog.java:    Applet
 *  Copyright © 2012 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.util.ThinMenuConstants;

/**
 * Status bar for displaying status in standalone windows.
 */
public class JStatusbar extends JPanel
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public JStatusbar()
    {
        super();
    }
    /**
     * Constructor.
     * @param obj Undefined.
     */
    public JStatusbar(Object obj)
    {
        this();
        this.init(obj);
    }
    /**
     * Constructor.
     * @param obj Undefined.
     */
    public void init(Object obj)
    {
        this.setOpaque(false);
        this.setBorder(new EtchedBorder());
        
        this.setLayout(new BorderLayout());
        this.add(m_textArea = new JLabel(), BorderLayout.CENTER);
        this.add(m_iconArea = new JLabel(), BorderLayout.EAST);
    }
    protected JLabel m_textArea = null;
    protected JLabel m_iconArea = null;
    /**
     * Display the status text.
     * @param strMessage The message to display.
     */
    public void showStatus(String strMessage, ImageIcon icon, int iWarningLevel)
    {
        if (strMessage == null)
            strMessage = Constants.BLANK;
        if (m_textArea != null)
            m_textArea.setText(strMessage);
        if (iWarningLevel == Constants.WARNING)
        {
            m_textArea.setForeground(Color.RED);
            m_textArea.setBackground(Color.PINK);
            m_textArea.setOpaque(true);
        }
        else if (iWarningLevel == Constants.WAIT)
        {
            m_textArea.setForeground(Color.BLUE);
            m_textArea.setBackground(Color.CYAN);
            m_textArea.setOpaque(true);
        }
        else
        {
            m_textArea.setForeground(Color.BLACK);
            m_textArea.setOpaque(false);
        }
        if (icon != null)
            m_textArea.setIcon(icon);
        else
            m_textArea.setIcon(null);
    }
    /**
     * Display the status text.
     * @param strMessage The message to display.
     */
    public void setStatus(int iStatus)
    {
        if (m_iconArea != null)
        {
            if (iStatus == Cursor.WAIT_CURSOR)
                m_iconArea.setIcon(BaseApplet.getSharedInstance().loadImageIcon(ThinMenuConstants.WAIT));
            else
                m_iconArea.setIcon(null);
        }
    }
    /**
     * Minimum panel size.
     * @return The minimum size (same as the preferred size).
     */
    public Dimension getMinimumSize()
    {
        return this.getPreferredSize();
    }
    /**
     * Preferred panel size.
     * PENDING(Don) Fix this to calc the height by the font height.
     * @return The preferred statusbar height.
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(super.getPreferredSize().width, 20);
    }
}
