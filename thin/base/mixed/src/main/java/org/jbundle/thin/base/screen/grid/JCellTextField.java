/**
 * JCellButton.java
 *
 * Created on February 23, 2000, 5:31 AM
 */
 
package org.jbundle.thin.base.screen.grid;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jbundle.model.db.FieldComponent;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.JScreen;


/** 
 * JCellTextField is a checkbox that works in a JTable.
 * This class is usually used for text field that have
 * non-default behavior, such as right justification;
 * </pre>
 *
 * @author  Don Corley don@tourgeek.com
 * @version 1.0.0
 */
public class JCellTextField extends JTextField
    implements FieldComponent, TableCellRenderer, TableCellEditor
{
	private static final long serialVersionUID = 1L;

	/**
     * The editor helper.
     */
    protected JCellEditorHelper m_cellEditorHelper = null;

    /**
     * Creates new JCellTextField.
     */
    public JCellTextField()
    {
        super();
    }
    /**
     * Creates new JCellTextField.
     * @param iMaxLength The number of columns of text in this field.
     * @param bAlignRight If true, align the text to the right.
     */
    public JCellTextField(int iMaxLength, boolean bAlignRight)
    {
        this();
        this.init(iMaxLength, bAlignRight);
    }
    /**
     * Creates new JCellButton.
     * @param iMaxLength The number of columns of text in this field.
     * @param bAlignRight If true, align the text to the right.
     */
    public void init(int iMaxLength, boolean bAlignRight)
    {
        this.setColumns(iMaxLength);
        this.setBorder(null);
        if (bAlignRight)
            this.setHorizontalAlignment(JTextField.RIGHT);
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
        if (hasFocus)
        {
            if (this.isFocusable())
                this.setOpaque(false);
            this.setBorder(JScreen.m_borderLine);
        }
        else
        {
            this.setBorder(null);
        }
        if (isSelected && !hasFocus) {
            if (this.isFocusable()) // This denotes a 'disabled' field
                this.setOpaque(true);
            this.setForeground(table.getSelectionForeground());
            this.setBackground(table.getSelectionBackground());
        }
        else {
            if (this.isFocusable()) // This denotes a 'disabled' field
                this.setOpaque(false);
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
        return this.getControlValue();
    }
    /**
     * Is this cell editable.
     * From the TableCellEditor interface.
     * @return true So you can click the button.
     */
    public boolean isCellEditable(EventObject anEvent)
    {
        return this.getCellEditorHelper().isCellEditable(anEvent);
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
    /**
     * Get the value (On, Off or Null).
     * @return The raw data (a Boolean).
     */
    public Object getControlValue()
    {
        return this.getText();
    }
    /**
     * Set the value.
     * @param objValue The raw data (a Boolean).
     */
    public void setControlValue(Object objValue)
    {
        if (objValue instanceof String)
            this.setText(objValue.toString());
    }
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Converter getConverter()
    {
        return null;
    }
}
