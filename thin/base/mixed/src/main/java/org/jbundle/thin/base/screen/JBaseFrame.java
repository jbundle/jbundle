/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen;

/**
 * JBaseFrame.java:   
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import java.awt.Dimension;

import javax.swing.JFrame;


/**
 * This frame class acts as a top-level window in which the applet appears
 * when it's run as a standalone application.
 */
public class JBaseFrame extends JFrame
{
	private static final long serialVersionUID = 1L;

	/**
     * Constructor (don't call this constructor).
     */
    public JBaseFrame()
    {
        super();
    }
    /**
     * Constructor - For any applet.
     * Note: For regular java applets (not BaseApplets) fakes the init() and start() calls.
     * @param str The frame's title.
     * @param The java applet to add to this frame.
     */
    public JBaseFrame(String str, java.applet.Applet applet)
    {
        this();
        this.init(str, applet);
    }
    /**
     * Constructor - For any applet.
     * Note: For regular java applets (not BaseApplets) fakes the init() and start() calls.
     * @param str The frame's title.
     * @param The java applet to add to this frame.
     */
    public void init(String str, java.applet.Applet applet)
    {
        if (str != null)
            this.setTitle(str);
        this.addWindowListener(new JBaseFrameAdapter(this));

        this.getContentPane().add("Center", applet);
        if (!(applet instanceof BaseApplet))
        {   // Fake the applet start (only for normal Java Applets).
            applet.init();
            applet.start();
        }
        this.pack();
        Dimension size = this.getSize();
        if (size.getHeight() < 200)
            size.setSize(size.width, JScreenConstants.PREFERRED_SCREEN_SIZE.height);
        if (size.getWidth() < 200)
            size.setSize(JScreenConstants.PREFERRED_SCREEN_SIZE.width, size.height);
        this.setSize(size);
        this.setVisible(true);
    }
    /**
     * Close this frame and free the baseApplet.
     */
    public void free()
    {
        if (this.getContentPane().getComponentCount() > 0)
            if (this.getContentPane().getComponent(0) instanceof BaseApplet)
                ((BaseApplet)this.getContentPane().getComponent(0)).free();
        this.dispose();
    }
    /**
     * Set the frame title.
     */
    public void setTitle(String title)
    {
        if (title == null)
            title = DEFAULT_FRAME_TITLE;
        super.setTitle(title);
    }
    public static final String DEFAULT_FRAME_TITLE = "Applet";
}
