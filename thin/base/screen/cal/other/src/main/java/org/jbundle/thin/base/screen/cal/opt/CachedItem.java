/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.opt;

import java.util.Date;

import javax.swing.ImageIcon;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.cal.popup.ProductTypeInfo;
import org.jbundle.thin.base.util.Application;
import org.jbundle.util.calendarpanel.model.CalendarItem;
import org.jbundle.util.calendarpanel.model.CalendarModel;
import org.jbundle.util.calendarpanel.model.swing.CachedCalendarModel;


/**
 * A Cached Product is used to save the information on each item so the target
 * (probably remote) item does not get continally accessed on each panel pass.
 * ALSO, this class provides synchronization so more than one task can access
 * and change the items at the same time.
 * Usually, you override this class and override the methods that set the information
 * such as startDate, endDate, etc.
 */
public class CachedItem extends Object
    implements CalendarItem
{
    protected CachedCalendarModel m_model = null;

    protected CachedInfo m_cachedInfo = null;

    /**
     * Constructor.
     */
    public CachedItem()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CachedItem(CalendarModel model, ProductTypeInfo productType, Date startTime, Date endTime, String description, String[] rgstrMeals, int iStatus)
    {
        this();
        this.init(model, productType, startTime, endTime, description, rgstrMeals, iStatus);
    }
    /**
     * Constructor.
     */
    public void init(CalendarModel model, ProductTypeInfo productType, Date startTime, Date endTime, String description, String[] rgstrMeals, int iStatus)
    {
        m_model = (CachedCalendarModel)model;

        m_cachedInfo = new CachedInfo(productType, startTime, endTime, description, rgstrMeals, iStatus);
    }
    /**
     * I'm done with this item, free the resources.
     */
    public void free()
    {
//+     int iThisIndex = m_model.indexOf(this);
//+     m_model.fireTableRowsDeleted(iThisIndex, iThisIndex); // Notify the models to get rid of the visual
//+     m_model.remove(this); // Remove me!
    }
    /**
     * Delete this item.
     */
    public boolean remove()
    {
        boolean bSuccess = false;
//+     bSuccess = m_model.remove(this);
        return bSuccess;
    }
    /**
     * Get the description.
     */
    public String getDescription()
    {
        return m_cachedInfo.getDescription();
    }
    /**
     * Get the start time of this service.
     */
    public Date getStartDate()
    {
        return m_cachedInfo.getStartDate();
    }
    /**
     * Get the ending time of this service.
     */
    public Date getEndDate()
    {
        return m_cachedInfo.getEndDate();
    }
    /**
     * Get the meal description on this date.
     * Note: date is guaranteed to be at 0:00:00.0000
     */
    public String getMealDesc(Date date)
    {
        return m_cachedInfo.getMealDesc(date);
    }
    /**
     * Highlight color (optional).
     */
    public int getHighlightColor()
    {
        return m_cachedInfo.getHighlightColor();
    }
    /**
     * Highlight color (optional).
     */
    public int getSelectColor()
    {
        return m_cachedInfo.getSelectColor();
    }
    /**
     * Change the start time of this service.
     * First, move the item on the screen, then call the method to change the remote data.
     */
    public synchronized Date setStartDate(Date time)
    {
        m_cachedInfo.setStartDate(time);
        int iThisIndex = m_model.indexOf(this);
        if (iThisIndex != -1)
            m_model.fireTableRowsUpdated(iThisIndex, iThisIndex);
        this.changeRemoteDate(null, null, this, time, null);
        return this.getStartDate();
    }
    /**
     * Change the ending time of this service.
     * First, move the item on the screen, then call the method to change the remote data.
     */
    public synchronized Date setEndDate(Date time)
    {
        m_cachedInfo.setEndDate(time);
        int iThisIndex = m_model.indexOf(this);
        if (iThisIndex != -1)
            m_model.fireTableRowsUpdated(iThisIndex, iThisIndex);
        this.changeRemoteDate(null, null, this, null, time);
        return this.getEndDate();
    }
    /**
     * Change the date of the actual data.
     * Override this to change the date.
     * Usually, you add flashing icons to show the tasks that are being queued, then
     * you add an override to DateChangeTask that changes the remote data in a Separate task.
     * The code usually looks something like this:
     * <pre>
     * Sample code:
     *  this.setIcon(BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/Lookup.gif", "Lookup"), CalendarConstants.START_ICON + 1);
     *  this.setIcon(BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/Price.gif", "Price"), CalendarConstants.START_ICON + 2);
     *  this.setIcon(BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/Inventory.gif", "Inventory"), CalendarConstants.START_ICON + 3);
     *  if (application == null)
     *      application = BaseApplet.getSharedInstance().getApplication();
     *  application.getTaskScheduler().addTask(new DateChangeTask(application, strParams, productItem, dateStart, dateEnd));
     * </pre>
     */
    public void changeRemoteDate(Application application, String strParams, CachedItem productItem, Date dateStart, Date dateEnd)
    {
    }
    /**
     * Get the icon (opt).
     */
    public Object getIcon(int iIconType)
    {
        return m_cachedInfo.getIcon(iIconType);
    }
    /**
     * Set the icon (opt).
     */
    public synchronized void setIcon(Object icon, int iIconType)
    {
        m_cachedInfo.setIcon(icon, iIconType);  // Always at location 0
    }
    /**
     * Remove this icon from the list of icons alternating for this label.
     */
    public void removeIcon(int iIconType)
    {
        m_cachedInfo.removeIcon(iIconType);
        int iThisIndex = m_model.indexOf(this);
        if (iThisIndex != -1)
            m_model.fireTableRowsUpdated(iThisIndex, iThisIndex);
    }
    /**
     * Get the display window for this object.
     */
    public Object getVisualJavaBean(int iPanelType)
    {
        return null;
    }
    /**
     * Get the meals on each day of this product and put them in an array.
     */
    public String[] getMealCache(Date dateStart, Date dateEnd) throws Exception
    {
        int iDays = (int)((dateEnd.getTime() - dateStart.getTime()) / Constants.KMS_IN_A_DAY) + 2;
        if (iDays <= 0)
            return null;
        String[] rgstrMeals = new String[iDays];
        Date date = new Date(dateStart.getTime());
        for (int iDay = 0; iDay < iDays; iDay++)
        {
            rgstrMeals[iDay] = this.getRemoteMealDesc(date);
            date.setTime(date.getTime() + Constants.KMS_IN_A_DAY);
        }
        return rgstrMeals;
    }
    /**
     * Change the cache data without calling the methods to change the underlying model.
     * This method is used by the lineItem to change the screen model without calling a change to the model.
     */
    public synchronized void setCacheData(Date startTime, Date endTime, String description, String [] rgstrMeals)
    {
        m_cachedInfo.setCacheData(startTime, endTime, description, rgstrMeals);
    }
    /**
     * Set the new start date for the remote item and return the new start date.
     * Don't forget to change the cache end date, description, and meals if they also change.
     */
    public Date setRemoteStartDate(Date dateStart)
    {
        return dateStart; // return ((TourLineItem)lineItem).setStartDate(dateStart);
    }
    /**
     * Set the new end date for the remote item and return the new end date.
     */
    public Date setRemoteEndDate(Date dateEnd)
    {
        return dateEnd;   // ((TourLineItem)lineItem).setEndDate(dateEnd);
    }
    /**
     * Get start date for the remote item.
     */
    public Date getRemoteStartDate()
    {
        return this.getStartDate();   // ((TourLineItem)lineItem).getEndDate();
    }
    /**
     * Set the end date for the remote item.
     */
    public Date getRemoteEndDate()
    {
        return this.getEndDate(); // ((TourLineItem)lineItem).getEndDate();
    }
    /**
     * Set the description for the remote item.
     */
    public String getRemoteDescription()
    {
        return this.getDescription(); // lineItem.getItem().getDescription();
    }
    /**
     * Get the remote meals on this day.
     */
    public String getRemoteMealDesc(Date date) throws Exception
    {
        return null;    // Override this
    }
    /**
     * Get the status of this object.
     */
    public int getStatus()
    {
        return m_cachedInfo.getStatus();
    }
    /**
     * Set the status of this item.
     */
    public synchronized int setStatus(int iStatus)
    {
        m_cachedInfo.setStatus(iStatus);
        int iThisIndex = m_model.indexOf(this);
        if (iThisIndex != -1)
            m_model.fireTableRowsUpdated(iThisIndex, iThisIndex);
        return this.getStatus();
    }
}
