/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jbundle.model.DBException;
import org.jbundle.model.db.Convert;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;


/**
 * Creates a table model that talks to a thin table.
 * <p>To use this class, write the following code:
 * <pre>
 * ThinTableModel model = new ThinTableModel(null);
 * JTable table = new JTable(model);
 * model.setGridScreen(table, false); // Update when selection changes!
 * If you want custom column widths, do the following:
 * m_jTableScreen = new JTable(); // Do not supply the model because JTable would set up a default grid.
 * m_thinTableModel.setGridScreen(m_jTableScreen, true);
 * </pre>
 */
public abstract class AbstractThinTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;

    /**
     * The table for this model.
     */
    protected FieldTable m_table = null;

    /**
     * Allow appending.
     */
    protected boolean m_bIsAppending = true;

    /**
     * ThinTableModel constructor.
     */
    public AbstractThinTableModel()
    {
        super();
    }
    /**
     * ThinTableModel constructor.
     * @param table  The table for this model.
     */
    public AbstractThinTableModel(FieldTable table)
    {
        this();
        this.init(table);
    }
    /**
     * ThinTableModel constructor.
     * @param table  The table for this model.
     */
    public void init(FieldTable table)
    {
        m_table = table;
    }
    /**
     * I'm done with the model, free the resources.
     */
    public void free()
    {
        m_table = null;
    }
    /**
     * Add one extra blank record for appending?
     * @return true if I should add a record at the end for insertion.
     */
    public void setAppending(boolean bIsAppending)
    {
        m_bIsAppending = bIsAppending;
    }
    /**
     * Add one extra blank record for appending?
     * @return true if I should add a record at the end for insertion.
     */
    public boolean isAppending()
    {
        return m_bIsAppending;
    }
    /**
     * Get the column count.
     * @return The field count of this model's record.
     */
    public int getColumnCount()
    {
        return m_table.getRecord().size();
    }
    /**
     * Returns the field at columnIndex.
     * This should be overidden if don't want to just return the corresponding field in the record.
     * @param iColumnIndex The column to get the field from.
     * @return The field in this column.
     */
    public Convert getFieldInfo(int iColumnIndex)
    {
        Converter converter = m_table.getRecord().getField(iColumnIndex);
        if (converter != null)
            converter = converter.getFieldConverter();  // Make sure you have the front converter.
        return converter;
    }
    /**
     * Get the fieldtable this is based on.
     * @return The record.
     */
    public FieldTable getFieldTable()
    {
        return m_table;
    }
    /**
     * User selected a new row.
     * From the ListSelectionListener interface.
     * @param source The source.
     * @param iStartRow The starting row.
     * @param iEndRow The ending row..
     * @param iSelectType The type of selection.
     * @return boolean If the selection changed, return true
     */
    public abstract boolean selectionChanged(Object source, int iStartRow, int iEndRow, int iSelectType);
    /**
     * Do any linkage that the grid screen may need to work with this model.
     * Note: This is generally called after the panel is added to its parent, so I can correct
     * column widths, etc.
     * @param gridScreen The gridscreen.
     * @param bSetupJTable If true, set up the JTable from this model.
     */
    public abstract void setGridScreen(JComponent gridScreen, boolean bSetupJTable);
    /**
     * Update the currently updated record if the row is different from this row.
     * @param iRowIndex Row to read... If different from current record, update current. If -1, update and don't read.
     */
    public abstract void updateIfNewRow(int iRowIndex)
        throws DBException;
    /**
     * Physically read this row.
     * @param iRowIndex Row to read (-1 to invaliate the current row.)
     * @param bLockRecord If true, call edit() on the record.
     * @return The record for this row.
     */
    public abstract FieldList makeRowCurrent(int iRowIndex, boolean bLockRecord);
    /**
     * Return the currently selected row number (from listening to selection events).
     * @return The selected row number.
     */
    public abstract int getSelectedRow();
    /**
     * The underlying query changed, reset the model.
     */
    public abstract void resetTheModel();
    /**
     * The underlying table has increased in size, change the model to access these extra record(s).
     * Note: This is not implemented correctly.
     * @param iBumpIncrement The new table size.
     */
    public abstract void bumpTableSize(int iBumpIncrement, boolean bInsertRowsInModel);
    /**
     * Get the number of rows in the physical table.
     * Fakes the row count until EOF is hit.
     * @return The theoretical row count.
     */
    public abstract int getRowCount();
    /**
     * Get the number of rows in the physical table.
     * Fakes the row count until EOF is hit.
     * @return The theoretical row count.
     */
    public abstract int getRowCount(boolean bIncludeAppendedRow);
    /**
     * Get the current row.
     * @return The current row.
     */
    public abstract int getCurrentRow();
    /**
     * Set the current row.
     * @param iCurrentRowIndex The current row.
     */
    public abstract void setCurrentRow(int iCurrentRowIndex);
    /**
     * Get the current row.
     * @return The current row.
     */
    public abstract int getLockedRow();
    /**
     * There is no-where else to put this. 
     * Add a mouse listener to the Table to trigger a table sort 
     * when a column heading is clicked in the JTable.
     * @param table The table to listen for a header mouse click.
     */
    public abstract void addMouseListenerToHeaderInTable(JTable table);
    /**
     * Convert the 1 based field column to the table column
     * @param iFieldColumn
     * @return The field column.
     */
    public abstract int columnToViewColumn(int iFieldColumn);
    /**
     * Change the tableheader to display this sort column and order.
     */
    public abstract void setSortedByColumn(JTableHeader tableHeader, int iViewColumn, boolean bOrder);
    /**
     * Sort this table by this column.
     * Override this to sort by column.
     * @param iColumn The column to sort by.
     * @param bOrder True if ascending.
     * @return true if successful.
     */
    public abstract boolean sortByColumn(int iColumn, boolean bOrder);
    /**
     * Returns the name of the column at columnIndex.
     * @param iColumnIndex The column to get the name of.
     * @return The column's field description.
     */
    public String getColumnName(int iColumnIndex)
    {
        Convert fieldInfo = this.getFieldInfo(iColumnIndex);
        if (fieldInfo != null)
            return fieldInfo.getFieldDesc();
        return Constants.BLANK;
    }
    /**
     * Get the column class.
     * @param iColumnIndex The column to get the class of.
     * @return String by default, override to supply a different class.
     */
    public Class<?> getColumnClass(int iColumnIndex)
    {
        return String.class;
    }
    /**
     * Get the cell editor for this column.
     * @param iColumnIndex The column to get the cell editor for.
     * @return The cell editor or null to use the default.
     */
    public TableCellEditor createColumnCellEditor(int iColumnIndex)
    {
        return null;
    }
    /**
     * Get the cell renderer for this column.
     * @param iColumnIndex The column to get the cell renderer for.
     * @return The cell renderer or null to use the default.
     */
    public TableCellRenderer createColumnCellRenderer(int iColumnIndex)
    {
        return null;
    }
    /**
     * Is this cell editable.
     * @return true unless this is a deleted record.
     */
    public abstract boolean isCellEditable(int iRowIndex, int iColumnIndex);
    /**
     * Set the value at this location.
     * @param aValue The raw-data value to set.
     * @param iRowIndex The row.
     * @param iColumnIndex The column.
     */
    public abstract void setValueAt(Object aValue, int iRowIndex, int iColumnIndex);
    /**
     * Get the value of the field at the column.
     * This is NOT a TableModel override, this is my method.
     * @param iColumnIndex The column.
     * @return The string at this location.
     */
    public abstract Object getColumnValue(int iColumnIndex, int iEditMode);
}
