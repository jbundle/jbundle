package org.jbundle.base.screen.view.swing.calendar;

import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;

import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.screen.model.CalendarScreen;
import org.jbundle.base.screen.view.swing.grid.GridTableModel;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.util.calendarpanel.event.MyListSelectionEvent;
import org.jbundle.util.calendarpanel.event.MyListSelectionListener;
import org.jbundle.util.calendarpanel.model.CalendarItem;
import org.jbundle.util.calendarpanel.model.CalendarModel;


/**
 * The calendar model for swing views.
 */
public class CalendarTableModel extends GridTableModel
    implements CalendarModel
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public CalendarTableModel()
    {
        super();
    }
    /**
     * Constructor using default field locations.
     * @param gridScreen The screen.
     */
    public CalendarTableModel(BaseScreen gridScreen)
    {
        this();
        this.init(gridScreen);
    }
    /**
     * Constructor.
     * @param gridScreen The screen.
     * @param iIconField The location of the icon field.
     * @param iStartDateTimeField The location of the start time.
     * @param iEndDateTimeField The location of the end time.
     * @param iDescriptionField The location of the description.
     * @param iStatusField The location of the status.
     */
    public void init(BaseScreen gridScreen)
    {
        super.init(gridScreen);
    }
    /**
     * Free the resources.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the row count.
     * @return The row count.
     */
    public int getRowCount()
    {
        return super.getRowCount();
    }
    /**
     * Start date for the calendar; return null to automatically set from the model.
     * @return The start date.
     */
    public Date getStartDate()
    {
        if (m_gridScreen instanceof CalendarScreen) // Always
            return ((CalendarScreen)m_gridScreen).getStartDate();
        return null;
    }
    /**
     * End date for the calendar; return null to automatically set from the model.
     * @return The end date.
     */
    public Date getEndDate()
    {
        if (m_gridScreen instanceof CalendarScreen) // Always
            return ((CalendarScreen)m_gridScreen).getEndDate();
        return null;
    }
    /**
     * Initial select date for the calendar; return null to automatically set from the model.
     * @return The select date.
     */
    public Date getSelectDate()
    {
        if (m_gridScreen instanceof CalendarScreen) // Always
            return ((CalendarScreen)m_gridScreen).getSelectDate();
        return null;
    }
    /**
     * Get the icon for a meal.
     * @return The icon for a meal.
     */
    public ImageIcon getHeaderIcon()
    {
        return org.jbundle.thin.base.screen.BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/Meal.gif", "Meal");
    }
    /**
     * Get this item.
     * @param i The row to make current.
     * @return The item at this row.
     */
    public CalendarItem getItem(int i)
    {
        FieldList item = this.makeRowCurrent(i, false);
        if ((item.getEditMode() == Constants.EDIT_CURRENT)
            || (item.getEditMode() == Constants.EDIT_IN_PROGRESS))
        {
            if (item instanceof CalendarItem)
                return (CalendarItem)item;
            else
                return this.getFieldListProxy(item);
        }
        return null;
    }
    /**
     * Create a CalendarItem Proxy using this field list.
     * Usually, you use CalendarItemFieldListProxy, or override it.
     * @param fieldList the fieldlist to encapsulate.
     * @return The new calendar item.
     */
    public CalendarItem getFieldListProxy(FieldList fieldList)
    {
        return ((CalendarScreen)m_gridScreen).getCalendarItem(fieldList);
//        return new CalendarRecordItem(m_GridScreen, m_iIconField, m_iStartDateTimeField, m_iEndDateTimeField, m_iDescriptionField, m_iStatusField);
    }
    /**
     * Notify listeners this row is selected; pass a -1 to de-select all rows.
     * @param source The source.
     * @param iRowIndex The row.
     * @param iSelectionType The type of selection.
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
     * @param l The listener to add to my list.
     */
    public void addMySelectionListener(MyListSelectionListener l)
    {
        listenerList.add(MyListSelectionListener.class, l);
    }
    /**
     * Remove a listener from my list.
     * @param l The listener to remove from the list.
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
     * @param event The event to notify all the listeners of.
     */
    protected void fireMySelectionChanged(MyListSelectionEvent event)
    {
        this.selectionChanged(event.getSource(), event.getRow(), event.getRow(), event.getType());
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2)
        {
            if (listeners[i]==MyListSelectionListener.class)
                if (listeners[i] != event.getSource())  // Don't send it back to source
            { // Send this message
                ((MyListSelectionListener)listeners[i+1]).selectionChanged(event);
            }
        }
    }
}
