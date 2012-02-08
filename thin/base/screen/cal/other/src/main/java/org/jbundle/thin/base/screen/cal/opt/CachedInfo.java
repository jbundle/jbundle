/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.opt;

import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;

import org.jbundle.model.util.Colors;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.cal.popup.ProductTypeInfo;
import org.jbundle.util.calendarpanel.model.CalendarConstants;
import org.jbundle.util.calendarpanel.model.CalendarItem;

/**
 * A Cached Product is used to save the information on each item so the target
 * (probably remote) item does not get continally accessed on each panel pass.
 * ALSO, this class provides synchronization so more than one task can access
 * and change the items at the same time.
 * Usually, you override this class and override the methods that set the information
 * such as startDate, endDate, etc.
 */
public class CachedInfo extends Object
    implements CalendarItem
{
    protected String m_description = null;
    protected Date m_startTime = null;
    protected Date m_endTime = null;
    protected String[] m_rgstrMeals = null;
    protected int m_colorHighlight = Colors.NULL;
    protected int m_colorSelect = Colors.NULL;
    protected int m_iStatus = 0;

    private ImageIcon m_rgIcons[] = null;

    /**
     * Constructor.
     */
    public CachedInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CachedInfo(ProductTypeInfo productType, Date startTime, Date endTime, String description, String[] rgstrMeals, int iStatus)
    {
        this();
        this.init(productType, startTime, endTime, description, rgstrMeals, iStatus);
    }
    /**
     * Constructor.
     */
    public void init(ProductTypeInfo productType, Date startTime, Date endTime, String description, String[] rgstrMeals, int iStatus)
    {
        m_startTime = startTime;
        m_endTime = endTime;
        m_description = description;
        m_iStatus = iStatus;
        if (productType != null)
        {
            m_colorHighlight = productType.getHighlightColor();
            m_colorSelect = productType.getSelectColor();
            this.setIcon(productType.getStartIcon(), CalendarConstants.START_ICON);
            this.setIcon(productType.getEndIcon(), CalendarConstants.END_ICON);
        }
        m_rgstrMeals = rgstrMeals;
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
        return m_description;
    }
    /**
     * Get the start time of this service.
     */
    public Date getStartDate()
    {
        return m_startTime;
    }
    /**
     * Get the ending time of this service.
     */
    public Date getEndDate()
    {
        if ((m_startTime != null) && (m_endTime != null))
        {
            if (m_endTime.before(m_startTime))
                return m_startTime;
        }
        return m_endTime;
    }
    /**
     * Get the meal description on this date.
     * Note: date is guaranteed to be at 0:00:00.0000
     */
    public String getMealDesc(Date date)
    {
        if (m_rgstrMeals == null)
            return Constants.BLANK;

        Date startDate = this.getStartDate();
        Converter.initGlobals();    // Make sure calendars are set up
        Calendar calendar = Converter.gCalendar;
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        startDate = calendar.getTime();
        
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        
        long lTimeToTarget = date.getTime() - startDate.getTime();
        int iTargetDay = (int)(lTimeToTarget / Constants.KMS_IN_A_DAY);   // Day 0 = first day
        if ((iTargetDay < 0) || (iTargetDay >= m_rgstrMeals.length))
            return Constants.BLANK;
        String strMeals = m_rgstrMeals[iTargetDay];
        if (strMeals == null)
            return Constants.BLANK;
        return strMeals;
    }
    /**
     * Highlight color (optional).
     */
    public int getHighlightColor()
    {
        return m_colorHighlight;
    }
    /**
     * Highlight color (optional).
     */
    public int getSelectColor()
    {
        return m_colorSelect;
    }
    /**
     * Change the start time of this service.
     * First, move the item on the screen, then call the method to change the remote data.
     */
    public synchronized Date setStartDate(Date time)
    {
        if ((m_startTime != null) && (m_endTime != null) && (time != null))
        {   // As a default, shift the end date the same distance as the start date
            long lChange = time.getTime() - m_startTime.getTime();
            m_endTime = new Date(m_endTime.getTime() + lChange);
        }
        m_startTime = time;
        return this.getStartDate();
    }
    /**
     * Change the ending time of this service.
     * First, move the item on the screen, then call the method to change the remote data.
     */
    public synchronized Date setEndDate(Date time)
    {
        m_endTime = time;
        return this.getEndDate();
    }
    /**
     * Get the icon (opt).
     */
    public ImageIcon getIcon(int iIconType)
    {
        if (m_rgIcons == null)
            return null;
        return m_rgIcons[iIconType - CalendarConstants.START_ICON];
    }
    /**
     * Set the icon (opt).
     */
    public synchronized void setIcon(ImageIcon icon, int iIconType)
    {
        if (m_rgIcons == null)
            m_rgIcons = new ImageIcon[CalendarConstants.END_ICON - CalendarConstants.START_ICON + 1];
        m_rgIcons[iIconType - CalendarConstants.START_ICON] = icon;   // Always at location 0
    }
    /**
     * Get the display window for this object.
     */
    public Object getVisualJavaBean(int iPanelType)
    {
        return null;
    }
    /**
     * Remove this icon from the list of icons alternating for this label.
     */
    public void removeIcon(int iIconType)
    {
        if (m_rgIcons == null)
            return;
        ImageIcon icon = m_rgIcons[iIconType - CalendarConstants.START_ICON];
        if (icon == null)
            return;
        m_rgIcons[iIconType - CalendarConstants.START_ICON] = null;   // Always at location 0
    }
    /**
     * Change the cache data without calling the methods to change the underlying model.
     * This method is used by the lineItem to change the screen model without calling a change to the model.
     */
    public synchronized void setCacheData(Date startTime, Date endTime, String description, String [] rgstrMeals)
    {
        if (startTime != null)
            m_startTime = startTime;
        if (endTime != null)
            m_endTime = endTime;
        if (description != null)
            m_description = description;
        if (rgstrMeals != null)
            m_rgstrMeals = rgstrMeals;
    }
    /**
     * Get the status of this object.
     */
    public int getStatus()
    {
        return m_iStatus;
    }
    /**
     * Set the status of this item.
     */
    public synchronized int setStatus(int iStatus)
    {
        m_iStatus = iStatus;
        return m_iStatus;
    }
}
