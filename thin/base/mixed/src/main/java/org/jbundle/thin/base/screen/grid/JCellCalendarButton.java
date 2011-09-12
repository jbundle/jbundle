/*
 * CalendarButton.java
 *
 * Created on January 28, 2001, 4:48 AM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.grid;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jbundle.model.Freeable;
import org.jbundle.model.db.FieldComponent;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.screen.JScreen;
import org.jbundle.thin.base.screen.util.cal.JCalendarDualField;
import org.jbundle.util.jcalendarbutton.JCalendarButton;
import org.jbundle.util.jcalendarbutton.JCalendarPopup;



/** 
 * A JCalendarButton is a button that displays a popup calendar (A JCalendarPopup).
 * Note: This button doesn't use some of the global constants and methods
 * because it is used in other programs where they are not available.
 * @author  Administrator
 * @version 1.0.0
 */
public class JCellCalendarButton extends JCalendarButton
    implements TableCellRenderer, TableCellEditor, FieldComponent, Freeable
{
	private static final long serialVersionUID = 1L;

	/**
     * The editor helper.
     */
    protected JCellEditorHelper m_cellEditorHelper = null;
    /**
     * The field this component is tied to.
     */
    protected Converter m_converter = null;   
    
    /**
     * Creates new CalendarButton.
     */
    public JCellCalendarButton()
    {
        super();
    }
    /**
     * Creates new CalendarButton.
     * @param dateTarget The initial date for this button.
     */
    public JCellCalendarButton(Converter converter)
    {
        super();
        this.init(converter);
    }
    /**
     * Creates new CalendarButton.
     * @param strDateParam The name of the date property (defaults to "date").
     * @param dateTarget The initial date for this button.
     * @param strLanguage The language to use.
     */
    public void init(Converter converter)
    {
        m_converter = converter;
        Date dateTarget = (Date)m_converter.getData();
        String strLanguage = null;
        super.init(JCalendarPopup.DATE_PARAM, dateTarget, strLanguage);
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        m_converter = null;
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
     * Handle action listener (button press).
     * The is exactly the same as inherited except the popup in instructed not to transfer focus after selection.
     * If they press the button, display the calendar popup.
     * @param e The actionevent.
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == this)
        {
            Container container = this.getParent();
            if (container instanceof JCalendarDualField)
                container = container.getParent();
            if (container instanceof JTable)
            {   // Always
                JTable jtable = (JTable)container;
                int iRow = jtable.getSelectedRow();
                Object value = jtable.getValueAt(iRow, m_iColumn);
                this.setControlValue(value);    // Make sure I have the right value
            }
            Date dateTarget = this.getTargetDate();
            
            JCalendarPopup popup = JCalendarPopup.createCalendarPopup(this.getDateParam(), this.getTargetDate(), this, this.getLanguage());
            popup.setTransferFocus(false);
            popup.addPropertyChangeListener(this);
        }
    }
    /**
     * Propagate the change to my listeners.
     * Watch for date and language changes, so I can keep up to date.
     * @param evt The property change event.
     */
    public void propertyChange(final java.beans.PropertyChangeEvent evt)
    {
        super.propertyChange(evt);
        if (this.getDateParam().equalsIgnoreCase(evt.getPropertyName()))
            if (evt.getNewValue() instanceof Date)
        {
            Container container = this.getParent();
            if (container instanceof JCalendarDualField)
                container = container.getParent();
            if (container instanceof JTable)
            {   // Always
                JTable jtable = (JTable)container;
                int iRow = jtable.getSelectedRow();
                jtable.setValueAt(evt.getNewValue(), iRow, m_iColumn);
            }
        }
    }
    /**
     * Get the value (On, Off or Null).
     * @return The raw-date value of this component.
     */
    public Object getControlValue()
    {
        Container container = this.getParent();
        if (container instanceof JCalendarDualField)
            container = container.getParent();
        if (container instanceof JTable)
        {   // Always
            JTable jtable = (JTable)container;
            int iRow = jtable.getSelectedRow();
            Object value = jtable.getValueAt(iRow, m_iColumn);
            this.setControlValue(value);    // Make sure I have the right value
        }
        Object objReturn = this.getTargetDate();
        if (this.getConverter() instanceof FieldInfo)
        { // Always
            this.getConverter().setData(objReturn);
            objReturn = this.getConverter().getString(); // Convert to a string and fix.
        }
        return objReturn;
    }
    /**
     * Set the value.
     * @param objValue The raw-date value of this component.
     */
    public void setControlValue(Object objValue)
    {
        try   {
            if (!(this.getConverter() instanceof FieldInfo))
                objValue = null;
            if (objValue instanceof String)
                objValue = ((FieldInfo)this.getConverter()).stringToDate((String)objValue);
        } catch (Exception ex)  {
            objValue = null;
        }
        this.setTargetDate((Date)objValue);
    }
    /**
     * Get the renderer for this location in the table.
     * From the TableCellRenderer interface.
     * @return this.
     */
    protected int m_iColumn = -1;
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        m_iColumn = column;
        if (hasFocus)
        {
            this.setOpaque(false);
            this.setBorder(JScreen.m_borderLine);
        }
        else
        {
            this.setOpaque(isSelected);
            if (isSelected)
                this.setBackground(table.getSelectionBackground());
            this.setBorder(null);
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
        m_iColumn = column;
        this.setControlValue(value);
//x     if (isSelected)
//x         m_tf.requestFocus();
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
    /**
     * Should I select this cell.
     * @param @anEvent The event object.
     * @return true if i should select this cell (return true).
     */
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
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Converter getConverter()
    {
        return m_converter;
    }
}
