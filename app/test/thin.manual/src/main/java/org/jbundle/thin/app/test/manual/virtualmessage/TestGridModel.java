/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.virtualmessage;

/**
 * OrderEntry.java:   Applet
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jbundle.model.db.Convert;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.screen.grid.ThinTableModel;


/**
 * Main Class for applet OrderEntry.
 * Note: This extends the CalendarThinTableModel rather
 * than the ThinTableModel, so I have the
 * use of the MySelectionListener.
 */
public class TestGridModel extends ThinTableModel
{
    private static final long serialVersionUID = 1L;

    public static final int DESC_COLUMN = 0;
    public static final int PRICE = DESC_COLUMN + 1;
    public static final int VIRTUAL = PRICE + 1;
    public static final int COLUMN_COUNT = VIRTUAL + 1;
    
    /**
     *  OrderEntry Class Constructor.
     */
    public TestGridModel()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public TestGridModel(FieldTable table)
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
     * Get the row count.
     */
//    public int getColumnCount()
//    {
//        return COLUMN_COUNT;
//    }
    /**
     * Get this field (or return null if this field doesn't belong on the screen).
     * This is the method to use to filter the items to display on the screen.
     */
    public Convert getFieldInfo(int iIndex)
    {
/*        FieldList fieldList = m_table.getFieldList();
        switch (iIndex)
        {
            case DESC_COLUMN:
                return fieldList.getFieldInfo(TestTable.TEST_NAME);
            case PRICE:
                return fieldList.getFieldInfo(TestTable.TEST_CURRENCY);
            case VIRTUAL:
                return fieldList.getFieldInfo(TestTable.TEST_VIRTUAL);
        }
*/        return super.getFieldInfo(iIndex);
    }
    /**
     * Get the column class.
     * Returns String by default, override to supply a different class.
     */
    public Class<?> getColumnClass(int iColumnIndex)
    {
        return super.getColumnClass(iColumnIndex);
    }
    /**
     * Get the value (this method returns the RAW data rather than Thin's String.
     */
    public Object getColumnValue(int iColumnIndex, int iEditMode)
    {
        return super.getColumnValue(iColumnIndex, iEditMode);
    }
    /**
     * Get the cell editor for this column.
     * @param The column to get the cell editor for.
     * @return The cell editor or null to use the default.
     */
    public TableCellEditor createColumnCellEditor(int iColumnIndex)
    {
        return super.createColumnCellEditor(iColumnIndex);
    }
    /**
     * Get the cell renderer for this column.
     * @param The column to get the cell renderer for.
     * @return The cell renderer or null to use the default.
     */
    public TableCellRenderer createColumnCellRenderer(int iColumnIndex)
    {
        return super.createColumnCellRenderer(iColumnIndex);
    }
    /**
     * Returns the name of the column at columnIndex.
     */
    public String getColumnName(int iColumnIndex)
    {
        return super.getColumnName(iColumnIndex);
    }
}
