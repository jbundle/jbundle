package org.jbundle.thin.app.test.manual.nettable;

/**
 * OrderEntry.java:   Applet
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import javax.swing.ImageIcon;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.grid.ThinTableModel;


/**
 * Main Class for applet OrderEntry.
 * Note: This extends the CalendarThinTableModel rather then the ThinTableModel, so I have the
 * use of the MySelectionListener.
 */
public class TestGridModel extends ThinTableModel
{
    private static final long serialVersionUID = 1L;

    public static final int ADD_BUTTON_COLUMN = 0;
    public static final int DESC_COLUMN = ADD_BUTTON_COLUMN + 1;
    public static final int PRICE = DESC_COLUMN + 1;
    public static final int COLUMN_COUNT = PRICE + 1;
    
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
        return COLUMN_COUNT;
    }
    /**
     * Get this field (or return null if this field doesn't belong on the screen).
     * This is the method to use to filter the items to display on the screen.
     */
    public Converter getFieldInfo(int iIndex)
    {
        FieldList fieldList = m_table.getRecord();
        switch (iIndex)
        {
            case ADD_BUTTON_COLUMN:
                return fieldList.getField("TestCode");
            case DESC_COLUMN:
                return fieldList.getField("TestName");
            case PRICE:
                return fieldList.getField("ID");
        }
        return super.getFieldInfo(iIndex);
    }
    /**
     * Get the column class.
     * Returns String by default, override to supply a different class.
     */
    public Class<?> getColumnClass(int iColumnIndex)
    {
        switch (iColumnIndex)
        {
            case ADD_BUTTON_COLUMN:
                return ImageIcon.class;
        }
        return super.getColumnClass(iColumnIndex);
    }
    /**
     * Get the value (this method returns the RAW data rather than Thin's String.
     */
    public Object getValueAt(int iRowIndex, int iColumnIndex)
    {
        switch (iColumnIndex) // RequestInputID
        {
            case ADD_BUTTON_COLUMN:
                return BaseApplet.getSharedInstance().loadImageIcon(Constants.FILE_ROOT + "Form", "");
        }
        return super.getValueAt(iRowIndex, iColumnIndex);
    }
    /**
     *  This default implementation returns false for all cells
     */
    public boolean isCellEditable(int iRowIndex, int iColumnIndex)
    {
        switch (iColumnIndex) // RequestInputID
        {
            case ADD_BUTTON_COLUMN:
                return true;    // RequestQty
        }
        return false; // Don't allow changes to any line
    }
    /**
     * Don't allow appending.
     */
    public boolean isAppending()
    {
        return false;
    }
    /**
     * Returns the name of the column at columnIndex.
     */
    public String getColumnName(int iColumnIndex)
    {
        switch (iColumnIndex)
        {
            case ADD_BUTTON_COLUMN:
                return "+";
        }
        return super.getColumnName(iColumnIndex);
    }
}
