/**
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 */
 
package org.jbundle.thin.base.screen.grid;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/** 
 * JCellCheckBox is a checkbox that works in a JTable.
 * Add this code to make it work:
 * <pre>
    JCellButton button = new JCellButton(icon);
    newColumn.setCellEditor(button);
    button.addActionListener(this);
    button = new JCellButton(icon);
    newColumn.setCellRenderer(button);
 * </pre>
 *
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class JCellCheckBox extends JCheckBox
    implements TableCellRenderer, TableCellEditor
{
	private static final long serialVersionUID = 1L;

	/**
     * The editor helper.
     */
    protected JCellEditorHelper m_cellEditorHelper = null;

    /**
     * Creates new JCellButton.
     */
    public JCellCheckBox()
    {
        super();
    }
    /**
     * Creates new JCellCheckBox.
     * @param The checkbox text (ignored for a table).
     */
    public JCellCheckBox(String text)
    {
        this();
        this.init(text);
    }
    /**
     * Creates new JCellButton
     */
    public void init(String text)
    {
        this.setOpaque(true);
        this.setHorizontalTextPosition(JToggleButton.LEFT);
        this.setAlignmentX(JComponent.CENTER_ALIGNMENT);
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
        this.setControlValue(value);
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
        this.setControlValue(value);
        return this;
    }
    /**
     * Get the value.
     * From the TableCellEditor interface.
     * @return A TRUE or FASLE string.
     */
    public Object getCellEditorValue()
    {
        boolean bValue = this.isSelected();
        if (bValue)
            return Boolean.TRUE.toString();
        else
            return Boolean.FALSE.toString();
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
    /**
     * Should I select this cell.
     * From the TableCellEditor interface.
     * @return true for a checkbox.
     */
    public boolean shouldSelectCell(EventObject anEvent)
    {
        return true;
    }
    /**
     * Set the value of this checkbox.
     * @param value The value to set.
     */
    public void setControlValue(Object value)
    {
        Boolean boolValue = null;
        if (value instanceof String)    // Always
            if (((String)value).length() > 0)
                boolValue = new Boolean((String)value);
        if (boolValue == null)
            boolValue = Boolean.FALSE;
        this.setSelected(boolValue.booleanValue());
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
}
