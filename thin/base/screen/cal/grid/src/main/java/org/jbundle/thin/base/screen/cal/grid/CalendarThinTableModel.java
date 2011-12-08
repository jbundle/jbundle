/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.grid;

import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;

import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.grid.ThinTableModel;
import org.jbundle.util.calendarpanel.event.MyListSelectionEvent;
import org.jbundle.util.calendarpanel.event.MyListSelectionListener;
import org.jbundle.util.calendarpanel.model.CalendarItem;
import org.jbundle.util.calendarpanel.model.CalendarModel;


/**
 * This is the ThinTableModel with a few extensions for the CalendarModel interface.
 */
public class CalendarThinTableModel extends ThinTableModel
    implements CalendarModel
{
    private static final long serialVersionUID = 1L;

    protected String m_strStartDateTimeField = "StartDateTime";
    protected String m_strEndDateTimeField = "EndDateTime";
    protected String m_strDescriptionField = "Description";
    protected String m_strStatusField = "Status";

    /**
     * Called to start the applet.  You never need to call this directly; it
     * is called when the applet's document is visited.
     */
    public CalendarThinTableModel()
    {
        super();
    }
    /**
     * Called to start the applet.  You never need to call this directly; it
     * is called when the applet's document is visited.
     */
    public CalendarThinTableModel(FieldTable table)
    {
        this();
        this.init(table);
    }
    /**
     * Constructor.
     */
    public CalendarThinTableModel(FieldTable table,String strStartDateTimeField,String strEndDateTimeField,String strDescriptionField,String strStatusField)
    {
        this();
        this.init(table, strStartDateTimeField, strEndDateTimeField, strDescriptionField, strStatusField);
    }
    /**
     * Constructor.
     */
    public void init(FieldTable table, String strStartDateTimeField, String strEndDateTimeField, String strDescriptionField, String strStatusField)
    {
        super.init(table);
        m_strStartDateTimeField = strStartDateTimeField;
        m_strEndDateTimeField = strEndDateTimeField; 
        m_strDescriptionField = strDescriptionField;
        m_strStatusField = strStatusField;
    }
    /**
     * Get the row count.
     */
    public int getRowCount()
    {
        return super.getRowCount();
    }
    /**
     * Start date for the calendar; return null to automatically set from the model.
     */
    public Date getStartDate()
    {
        return null;
    }
    /**
     * End date for the calendar; return null to automatically set from the model.
     */
    public Date getEndDate()
    {
        return null;
    }
    /**
     * Start date for the calendar; return null to automatically set from the model.
     */
    public Date getSelectDate()
    {
        return null;
    }
    /**
     * Get the icon for a meal.
     */
    public ImageIcon getHeaderIcon()
    {
        return BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/Meal.gif", "Meal");
    }
    /**
     * Get this item.
     * Note: There is no guarantee of the mode of this record, if you want to change it,
     * remember to call makeRowCurrent(i, true).
     */
    public CalendarItem getItem(int i)
    {
        FieldList item = this.makeRowCurrent(i, false);
        int iRowCount = this.getRowCount();
        if (this.isAppending())
            iRowCount--;
        if (i >= iRowCount)
            return null;
        if (item instanceof CalendarItem)
            return (CalendarItem)item;
        else if (item != null)
            return this.getFieldListProxy(item);
        return null;
    }
    /**
     * Create a CalendarItem Proxy using this field list.
     * Usually, you use CalendarItemFieldListProxy, or override it.
     */
    public CalendarItem getFieldListProxy(FieldList fieldList)
    {
        return new CalendarItemFieldListProxy(fieldList, m_strStartDateTimeField, m_strEndDateTimeField, m_strDescriptionField, m_strStatusField);
    }
    /**
     * Notify listeners this row is selected; pass a -1 to de-select all rows.
     */
    public void fireTableRowSelected(Object source, int iRowIndex, int iSelectionType)
    {
        this.fireMySelectionChanged(new MyListSelectionEvent(source, this, iRowIndex, iSelectionType));
    }
    /**
     * The listener list.
     */
    protected EventListenerList listenerList = new EventListenerList();
    /**
     * Add a listener to my list.
     */
    public void addMySelectionListener(MyListSelectionListener l)
    {
        listenerList.add(MyListSelectionListener.class, l);
    }
    /**
     * Remove a listener from my list.
     */
    public void removeMySelectionListener(MyListSelectionListener l)
    {
        listenerList.remove(MyListSelectionListener.class, l);
    }
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     */
    protected void fireMySelectionChanged(MyListSelectionEvent event)
    {
        this.selectionChanged(event.getSource(), event.getRow(), event.getRow(), event.getType());  // Make sure model knows
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2)
        {
            if (listeners[i] == MyListSelectionListener.class)
                if (listeners[i] != event.getSource())  // Don't send it back to source
            { // Send this message
                ((MyListSelectionListener)listeners[i+1]).selectionChanged(event);
            }
        }
    }
    /**
     * User selected a new row.
     * From the ListSelectionListener interface.
     */
    public boolean selectionChanged(Object source, int iStartRow, int iEndRow, int iSelectType)
    {
        boolean bChanged = super.selectionChanged(source, iStartRow, iEndRow, iSelectType);
        if (bChanged)
        { // Notify all the listSelectionListeners
            this.fireMySelectionChanged(new MyListSelectionEvent(source, this, m_iSelectedRow, iSelectType));
        }
        return bChanged;
    }
}
