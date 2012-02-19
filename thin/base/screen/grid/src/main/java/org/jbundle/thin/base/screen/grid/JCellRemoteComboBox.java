/*
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.grid;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.util.JRemoteComboBox;


/** 
 *
 * @author  Administrator
 * JCellRemoteComboBox is a JRemoteComboBox that works in a JTable.
 * @version 1.0.0
 */
public class JCellRemoteComboBox extends JRemoteComboBox
    implements TableCellRenderer, TableCellEditor
{
	private static final long serialVersionUID = 1L;

	/**
     * The editor helper.
     */
    protected JCellEditorHelper m_cellEditorHelper = null;
    
    /**
     * Creates new JCellRemoteComboBox.
     */
    public JCellRemoteComboBox()
    {
        super();
    }
    /**
     * Build a combo box using a remote fieldlist.
     * Build a popup box using a remote fieldlist.
     * If the remote session doesn't exist, create it.
     * @param applet The top-level applet.
     * @param remoteSession The remote parent session for this record's new table session.
     * @param record The record to display.
     * @param strDesc The description for this combo-box.
     * @param strFieldName The name of the field to display in the pop-up.
     * @param strComponentName The name of this component.
     * @param bCacheTable If a table session is build, should I add a CacheTable?
     * @param strIndexValue The field name of the index for this table (ie., ID).
     */
    public JCellRemoteComboBox(BaseApplet applet, RemoteSession remoteSession, FieldList record, String strDesc, String strFieldName, String strComponentName, boolean bCacheTable, String strIndexValue, String strKeyArea)
    {
        this();
        this.init(applet, remoteSession, record, strDesc, strFieldName, strComponentName, bCacheTable, strIndexValue, strKeyArea);
    }
    /**
     * Build a combo box using a remote fieldlist.
     * Build a popup box using a remote fieldlist.
     * If the remote session doesn't exist, create it.
     * @param applet The top-level applet.
     * @param remoteSession The remote parent session for this record's new table session.
     * @param record The record to display.
     * @param strDesc The description for this combo-box.
     * @param strFieldName The name of the field to display in the pop-up.
     * @param strComponentName The name of this component.
     * @param bCacheTable If a table session is build, should I add a CacheTable?
     * @param strIndexValue The field name of the index for this table (ie., ID).
     */
    public void init(BaseApplet applet, RemoteSession remoteSession, FieldList record, String strDesc, String strFieldName, String strComponentName, boolean bCacheTable, String strIndexValue, String strKeyArea)
    {
        super.init(applet, remoteSession, record, strDesc, strFieldName, strComponentName, bCacheTable, strIndexValue, strKeyArea);
    }
    /**
     * The editor helper.
     */
    public JCellEditorHelper getCellEditorHelper()
    {
        if (m_cellEditorHelper == null)
            m_cellEditorHelper = new JCellEditorHelper(null);
        return m_cellEditorHelper;
    }
    /**
     * Get the renderer for this location in the table.
     * From the TableCellRenderer interface.
     * Sets the value of this control and returns this.
     * @return this.
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        this.setControlValue((String)value);
        if (isSelected && !hasFocus) {
            this.setForeground(table.getSelectionForeground());
            this.setBackground(table.getSelectionBackground());
        }
        else {
            this.setForeground(table.getForeground());
            this.setBackground(table.getBackground());
        }
        return this;
    }
    /**
     * Get the editor for this location in the table.
     * From the TableCellEditor interface.
     * Sets the value of this control and returns this.
     * @return this.
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        this.setControlValue((String)value);
        return this;
    }
    /**
     * Get the value.
     * From the TableCellEditor interface.
     * @return The combo box's current value.
     */
    public Object getCellEditorValue()
    {
        return this.getControlValue();
    }
    /**
     * Is this cell editable.
     * From the TableCellEditor interface.
     * @return true So you can click the combo box.
     */
    public boolean isCellEditable(EventObject anEvent)
    {
        return true;
    }
    /**
     * Should I select this cell.
     * From the TableCellEditor interface.
     * @return true for a combo box.
     */
    public boolean shouldSelectCell(EventObject anEvent)
    {
        return true;
    }
    /**
     * Stop cell editing.
     * From the TableCellEditor interface.
     * @return true for a button.
     */
    public boolean stopCellEditing()
    {
        return this.getCellEditorHelper().stopCellEditing();
    }
    /**
     * Cancel editing.
     * From the TableCellEditor interface.
     * Empty method.
     */
    public void cancelCellEditing() {
        this.getCellEditorHelper().cancelCellEditing();
    }
    /**
     * Add a cell editor listener.
     * The CellEditorHelper handles this.
     * From the CellEditor interface.
     */
    public void addCellEditorListener(CellEditorListener l)
    {
        this.getCellEditorHelper().addCellEditorListener(l);
    }
    /**
     * Remove a cell editor listener.
     * The CellEditorHelper handles this.
     * From the CellEditor interface.
     */
    public void removeCellEditorListener(CellEditorListener l)
    {
        this.getCellEditorHelper().removeCellEditorListener(l);
    }
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingStopped()
    {
        this.getCellEditorHelper().fireEditingStopped();
    }
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingCanceled()
    {
        this.getCellEditorHelper().fireEditingCanceled();
    }
}
