/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.gridbig;

/**
 * JScreen.java:    Applet
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.screen.AbstractThinTableModel;
import org.jbundle.thin.base.screen.grid.JGridScreen;
import org.jbundle.thin.base.screen.grid.ThinTableModel;


/**
 * A Basic data entry screen.
 * This screen is made of a panel with a GridBagLayout. Labels in the first column, aligned right.
 * Data fields in the second column aligned left.
 */
public class JTestGridScreen extends JGridScreen
{
    private static final long serialVersionUID = 1L;

    /**
     *  JScreen Class Constructor.
     */
    public JTestGridScreen()
    {
        super();
    }
    /**
     *  JScreen Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public JTestGridScreen(Object parent, Object record)
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
     * Build the list of fields that make up the screen.
     * Override this to create a new record.
     * @return The fieldlist for this screen.
     */
    public FieldList buildFieldList()
    {
        return new TestTable(null);   // If overriding class didn't set this
    }
    /**
     * Build the list of fields that make up the screen.
     * Override this to create a new record.
     * @return The fieldlist for this screen.
     */
    public AbstractThinTableModel createGridModel(FieldList record)
    {
        return new ThinTableModel(record.getTable());
    }
}
