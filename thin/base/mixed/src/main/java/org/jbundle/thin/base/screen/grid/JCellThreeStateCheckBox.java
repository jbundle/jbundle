/**
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 */
 
package org.jbundle.thin.base.screen.grid;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.JScreen;
import org.jbundle.thin.base.screen.util.JThreeStateCheckBox;


/** 
 * JCellThreeStateCheckBox is a three state check-box that works in a JTable.
 * Add this code to make it work:
 * <pre>
    JCellButton button = new JCellButton(icon);
    newColumn.setCellEditor(button);
    button.addActionListener(this);
    button = new JCellButton(icon);
    newColumn.setCellRenderer(button);
 * </pre>
 *
 * @author  Administrator
 * @version 1.0.0
 */
public class JCellThreeStateCheckBox extends JThreeStateCheckBox
    implements TableCellRenderer, TableCellEditor
{
	private static final long serialVersionUID = 1L;

	/**
     * The editor helper.
     */
    protected JCellEditorHelper m_cellEditorHelper = null;

    /**
     * Creates new JCellThreeStateCheckBox.
     */
    public JCellThreeStateCheckBox()
    {
        super();
    }
    /**
     * Creates new JCellCheckBox.
     * @param The checkbox text.
     */
    public JCellThreeStateCheckBox(String text)
    {
        super(text);
        this.setOpaque(true);
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
        if (hasFocus)
            this.setBorder(JScreen.m_borderLine);
        else
            this.setBorder(null);
        if (isSelected && !hasFocus) {
            this.setForeground(table.getSelectionForeground());
            this.setBackground(table.getSelectionBackground());
        }
        else {
            this.setForeground(table.getForeground());
            this.setBackground(table.getBackground());
        }
        this.setControlValue(value);
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
        this.setControlValue(value);
        return this;
    }
    /**
     * Get the value.
     * From the TableCellEditor interface.
     * @return A TRUE or FALSE value.
     */
    public Object getCellEditorValue()
    {
        return this.getControlValue();
    }
    /**
     * Is this cell editable.
     * From the TableCellEditor interface.
     * @return true So you can click the button.
     */
    public boolean isCellEditable(EventObject anEvent)
    {
        return true;
    }
    public boolean shouldSelectCell(EventObject anEvent)
    {
        return true;
    }
    /**
     * Should I select this cell.
     * From the TableCellEditor interface.
     * @return true for a checkbox.
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
    public void cancelCellEditing()
    {
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
    /**
     * Set the value of this control.
     */
    public void setControlValue(Object objValue)
    {
        if (objValue instanceof String)
        {
            if (((String)objValue).equalsIgnoreCase(Constants.TRUE))
                objValue = Boolean.TRUE;
            else if (((String)objValue).equalsIgnoreCase(Constants.FALSE))
                objValue = Boolean.FALSE;
        }
        super.setControlValue(objValue);
    }
    /**
     * Get the value (On, Off or Null).
     * @return The Boolean TRUE or FALSE.
     */
    public Object getControlValue()
    {
        Object objValue = super.getControlValue();
        if (objValue instanceof Boolean)
        {
            if (((Boolean)objValue).booleanValue())
                objValue = Constants.TRUE.toLowerCase();
            else
                objValue = Constants.FALSE.toLowerCase();
        }
        return objValue;
    }
}
