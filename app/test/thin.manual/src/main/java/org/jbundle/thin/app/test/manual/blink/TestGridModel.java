/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.blink;

/**
 * OrderEntry.java:   Applet
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import org.jbundle.model.db.Convert;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.screen.grid.ThinTableModel;

/**
 * Main Class for applet OrderEntry
 */
public class TestGridModel extends ThinTableModel
{
    private static final long serialVersionUID = 1L;
    
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
    public int getColumnCount()
    {
        return 4;
    }
    /**
     * Get this field (or return null if this field doesn't belong on the screen).
     * This is the method to use to filter the items to display on the screen.
     */
    public Convert getFieldInfo(int iIndex)
    {
        FieldList fieldList = m_table.getRecord();
        if (iIndex == 1)
            return fieldList.getField("TestShort");
        if (iIndex == 3)
            return fieldList.getField("TestShort");
        return super.getFieldInfo(iIndex);
    }
    /**
     * Get the column class.
     * Returns String by default, override to supply a different class.
     */
    public Class<?> getColumnClass(int iColumnIndex)
    {
//      if (iColumnIndex == 1)
//          return ImageIcon.class;
        return super.getColumnClass(iColumnIndex);
    }
    /**
     * Get the value (this method returns the RAW data rather than Thin's String.
     */
    public Object getValueAt(int iRowIndex, int iColumnIndex)
    {
        return super.getValueAt(iRowIndex, iColumnIndex);
    }
    /**
     *  This default implementation returns false for all cells
     */
    public boolean isCellEditable(int iRowIndex, int iColumnIndex)
    {
//          if (iIndex == 1)
//              return true;    // RequestQty
        return true;
    }
    /**
     * Returns the name of the column at columnIndex.
     */
    public String getColumnName(int iColumnIndex)
    {
//          if (iIndex == 1)
//              return "+";
        return super.getColumnName(iColumnIndex);
    }
}
