/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.opt;

import java.util.Date;

import org.jbundle.model.App;
import org.jbundle.thin.base.thread.AutoTask;


public class DateChangeTask extends AutoTask
{
    protected CachedItem m_productItem = null;
    protected Date m_dateStart = null;
    protected Date m_dateEnd = null;

    /**
     * Constructor.
     */
    public DateChangeTask()
    {
        super();
    }
    /**
     * Constructor.
     */
    public DateChangeTask(App application, String strParams, CachedItem productItem, Date dateStart, Date dateEnd)
    {
        this();
        this.init(application, strParams, productItem, dateStart, dateEnd);
    }
    /**
     * Constructor.
     */
    public void init(App application, String strParams, CachedItem productItem, Date dateStart, Date dateEnd)
    {
        m_productItem = productItem;
        m_dateStart = dateStart;
        m_dateEnd = dateEnd;
        super.init(application, strParams, null);
    }
    /**
     * Start running this thread.
     */
    public void runTask()
    {
        if (m_dateStart != null)
            this.setStartDate(m_dateStart);
        if (m_dateEnd != null)
            this.setEndDate(m_dateEnd);
    }
    /**
     * Set the new start date for the remote item.
     */
    public void setStartDate(Date dateStart)
    {
        try   {
            // NOTE: I ignore dateStart and use the date as it appears on the screen
            dateStart = m_productItem.getStartDate(); // Date as it appears on the screen
            Date timeNew = m_productItem.setRemoteStartDate(dateStart);
            if (!timeNew.equals(dateStart))
            {
                Date timeEnd = m_productItem.getRemoteEndDate();    // ((TourLineItem)lineItem).getEndDate();
                String strDescription = m_productItem.getRemoteDescription(); // lineItem.getItem().getDescription();
                String[] rgstrMeals = m_productItem.getMealCache(timeNew, timeEnd);
                
                m_productItem.setCacheData(timeNew, timeEnd, strDescription, rgstrMeals);
                // Usually, you would have to update the data, but removing the icon in the next step will do this.
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        try   {
            Thread.sleep(2000);     // Sleep for 2 seconds
        } catch (InterruptedException e)    {
        }
        m_productItem.setStatus(m_productItem.getStatus() & ~(1 << 1));
    }
    /**
     * Set the new end date for the remote item.
     */
    public void setEndDate(Date dateEnd)
    {
        try   {
            dateEnd = m_productItem.getEndDate(); // Date as it appears on the screen
            Date timeNew = m_productItem.setRemoteEndDate(dateEnd);
            if (!timeNew.equals(dateEnd))
            {
                Date dateStart = m_productItem.getStartDate();
                String[] rgstrMeals = m_productItem.getMealCache(dateStart, dateEnd);

                m_productItem.setCacheData(null, timeNew, null, rgstrMeals);
                // Usually, you would have to update the data, but removing the icon in the next step will do this.
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        try   {
            Thread.sleep(2000);     // Sleep for 2 seconds
        } catch (InterruptedException e)    {
        }
        m_productItem.setStatus(m_productItem.getStatus() & ~(1 << 1));
    }
}
