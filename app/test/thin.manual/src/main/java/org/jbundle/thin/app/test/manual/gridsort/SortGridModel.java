/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.gridsort;

/**
 * OrderEntry.java:   Applet
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import javax.swing.JComponent;
import javax.swing.JTable;

import org.jbundle.thin.app.test.manual.gridformtest.TestGridModel;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.KeyAreaInfo;


/**
 * Main Class for applet OrderEntry.
 * Note: This extends the CalendarThinTableModel rather
 * than the ThinTableModel, so I have the
 * use of the MySelectionListener.
 */
public class SortGridModel extends TestGridModel
{
    private static final long serialVersionUID = 1L;
    
    /**
     *  OrderEntry Class Constructor.
     */
    public SortGridModel()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public SortGridModel(FieldTable table)
    {
        this();
        this.init(table);
    }
    /**
     * The init() method is called by the AWT when an applet is first loaded or
     * reloaded.  Override this method to perform whatever initialization your
     * applet needs, such as initializing data structures, loading images or
     * fonts, creating frame windows, setting the layout manager, or adding UI
     * components.
     */
    public void init(FieldTable table)
    {
        super.init(table);
    }
    /**
     * Do any linkage that the grid screen may need to work with this model.
     * Note: This is generally called after the panel is added to its parent, so I can correct
     * column widths, etc.
     * @param gridScreen The gridscreen.
     * @param bSetupTable If true, set up the JTable from this model.
     */
    public void setGridScreen(JComponent gridScreen, boolean bSetupJTable)
    {
        super.setGridScreen(gridScreen, bSetupJTable);
        if (gridScreen instanceof JTable)   // Always
            this.addMouseListenerToHeaderInTable((JTable)gridScreen);      // Notify model of row order changed (clicks in the header bar)
    }
    /**
     * Sort this table by this column.
     * Override this to sort by column.
     * @param iColumn The column to sort by.
     * @param bAscending True if ascending.
     * @return true if successful.
     */
    public boolean sortByColumn(int iColumn, boolean bKeyOrder)
    {
        if (iColumn == DESC_COLUMN)
        {
            FieldTable table = this.getFieldTable();
            table.close();
            FieldList record = table.getRecord();
            KeyAreaInfo keyArea = record.setKeyArea("TestCode");
            keyArea.setKeyOrder(bKeyOrder);
            this.resetTheModel();
            return true;
        }
        if (iColumn == PRICE)
        {
            FieldTable table = this.getFieldTable();
            table.close();
            FieldList record = table.getRecord();
            KeyAreaInfo keyArea = record.setKeyArea("TestKey");
            keyArea.setKeyOrder(bKeyOrder);
            this.resetTheModel();
            return true;
        }
        return super.sortByColumn(iColumn, bKeyOrder);   // For now
    }
}
