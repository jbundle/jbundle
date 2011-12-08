/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen;

/**
 * JScreen.java:    Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */


/**
 * A Basic data entry screen.
 * This screen is made of a panel with a GridBagLayout. Labels in the first column, aligned right.
 * Data fields in the second column aligned left.
 */
public abstract class JBaseGridScreen extends JBaseScreen
{
	private static final long serialVersionUID = 1L;

    /**
     *  JScreen Class Constructor.
     */
    public JBaseGridScreen()
    {
        super();
    }
    /**
     *  JScreen Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public JBaseGridScreen(Object parent, Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Initialize this class.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public void init(Object parent, Object record)
    {
        super.init(parent, record);
    }
    /**
     * Free.
     */
    public void free()
    {
    	super.free();
    }
    /**
     * Get the grid model for this screen's JTable.
     * @return This screen's table model.
     */
    public abstract AbstractThinTableModel getGridModel();
}
