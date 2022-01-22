/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen;

/**
 * JBaseFrameAdapter.java:  
 *  Copyright © 2012 jbundle.org. All rights reserved.
 */
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Close a BaseFrame when windowClosing is called.
 */
public class JBaseFrameAdapter extends WindowAdapter
{
    /**
     * The base frame to close.
     */
    protected JBaseFrame m_dialog = null;
    /**
     * Constructor.
     * @param dialog The frame to close.
     */
    public JBaseFrameAdapter(JBaseFrame dialog)
    {
        super();
        m_dialog = dialog;
    }
    /**
     * The window is closing, free the sub-BaseApplet.
     * @param e The window event.
     */
    public void windowClosing(WindowEvent e)
    {
        if (m_dialog.getContentPane().getComponentCount() > 0)
            if (m_dialog.getContentPane().getComponent(0) instanceof BaseApplet)
                ((BaseApplet)m_dialog.getContentPane().getComponent(0)).free();
        m_dialog.dispose();     // Remove dialog.
    }
}
