/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.grid;

import javax.swing.table.TableModel;

/**
 * Calendar model.
 */
public interface ExtendedTableModel extends TableModel
{
    /**
     * Add a listener to the list that's notified each time a change to the data model occurs.
     */
//  public void addTableModelListener(TableModelListener l);
    /**
     * Returns the lowest common denominator Class in the column.
     */
//    public Class<?> getColumnClass(int columnIndex);
    /**
     * Returns the number of columns managed by the data source object.
     */
//  public int getColumnCount();
    /**
     * Returns the name of the column at columnIndex.
     */
//  public String getColumnName(int columnIndex);
    /**
     * Returns the number of records managed by the data source object.
     */
//  public int getRowCount();
    /**
     * Returns an attribute value for the cell at columnIndex and rowIndex.
     */
//  public Object getValueAt(int rowIndex, int columnIndex);
    /**
     * Returns true if the cell at rowIndex and columnIndex is editable.
     */
//  public boolean isCellEditable(int rowIndex, int columnIndex);
    /**
     * Remove a listener from the list that's notified each time a change to the data model occurs.
     */
//    public void removeTableModelListener(TableModelListener l);
    /**
     * Sets an attribute value for the record in the cell at columnIndex and rowIndex.
     */
//    public void setValueAt(Object aValue, int rowIndex, int columnIndex);
///////////// These are specifically for the calendarModel!
    /**
     * I'm done with the model, free the resources.
     */
    public void free();
}
