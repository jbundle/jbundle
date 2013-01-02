/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.opt;

import java.util.EventListener;

import org.jbundle.model.App;
import org.jbundle.thin.base.thread.AutoTask;
import org.jbundle.util.calendarpanel.model.CalendarModel;
import org.jbundle.util.calendarpanel.model.swing.CachedCalendarModel;


//ximport org.jbundle.thin.tour.booking.remote.*;

public class ReadItemsTask extends AutoTask
    implements CalendarModel
{
    protected CachedCalendarModel m_model = null;
//    protected Order m_order = null;

    /**
     * Constructor.
     */
    public ReadItemsTask()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ReadItemsTask(App application,String strParams,CachedCalendarModel model, Object order)
    {
        this();
        this.init(application, strParams, model, order);
    }
    /**
     * Constructor.
     */
    public void init(App application, String strParams, CachedCalendarModel model, Object order)
    {
        super.init(application, strParams, null);
        m_model = model;
//        m_order = order;
    }
    /**
     * Start running this thread.
     */
    public void runTask()
    {
/**     try   {
            LineItem lineItem = null;
            Vector vector = new Vector();
            while ((lineItem = m_order.getNextLineItem()) != null)
            {
//              CalendarItem item = new org.jbundle.thin.app.tourcalendar.simple.CalendarProduct(m_model, lineItem);
                CalendarItem item = new JfoProductItem(m_model, lineItem);
                vector.addElement(item);
            }
            while (vector.size() > 0)
            {
                CalendarItem item = (CalendarItem)vector.remove(0);
                m_model.addElement(item);
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
*/  }
    public void fireTableRowsUpdated(int p1,int p2)
    {
        m_model.fireTableRowsUpdated(p1,p2);
    }
    public void free()
    {
        m_model.free();
    }
    public void fireTableRowSelected(Object source, int p1,int p2)
    {
        m_model.fireTableRowSelected(source, p1, p2);
    }
    /**
     * Add a listener to my list.
     */
    public void addMySelectionListener(EventListener l)
    {
        m_model.addMySelectionListener(l);
    }
    /**
     * Remove a listener from my list.
     */
    public void removeMySelectionListener(EventListener l)
    {
        m_model.removeMySelectionListener(l);
    }
    public javax.swing.ImageIcon getHeaderIcon()
    {
        return m_model.getHeaderIcon();
    }
    public java.util.Date getStartDate()
    {
        return m_model.getStartDate();
    }
    public java.util.Date getEndDate()
    {
        return m_model.getEndDate();
    }
    public java.util.Date getSelectDate()
    {
        return m_model.getSelectDate();
    }
    public org.jbundle.util.calendarpanel.model.CalendarItem getItem(int p1)
    {
        return m_model.getItem(p1);
    }
    public int getRowCount()
    {
        return m_model.getRowCount();
    }
    public void setValueAt(final java.lang.Object p1,int p2,int p3)
    {
        m_model.setValueAt(p1, p2, p3);
    }
    public void removeTableModelListener(final javax.swing.event.TableModelListener p1)
    {
        m_model.removeTableModelListener(p1);
    }
    public boolean isCellEditable(int p1,int p2)
    {
        return m_model.isCellEditable(p1, p2);
    }
    public int getColumnCount()
    {
        return m_model.getColumnCount();
    }
    public java.lang.Object getValueAt(int p1,int p2)
    {
        return m_model.getValueAt(p1, p2);
    }
    public java.lang.Class<?> getColumnClass(int p1)
    {
        return m_model.getColumnClass(p1);
    }
    public java.lang.String getColumnName(int p1)
    {
        return m_model.getColumnName(p1);
    }
    public void addTableModelListener(final javax.swing.event.TableModelListener p1)
    {
        m_model.addTableModelListener(p1);
    }
}
