/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.sample.remote;

import java.awt.Color;
import java.util.Date;

import javax.swing.ImageIcon;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.util.calendarpanel.model.CalendarItem;
import org.jbundle.thin.main.calendar.db.Appointment;


public class AppointmentCalendarItem extends Appointment
    implements CalendarItem
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public AppointmentCalendarItem()
    {
        super();
    }
    /**
     * Constructor.
     */
    public AppointmentCalendarItem(Object object)
    {
        this();
        this.init(object);
    }
    /**
     * Constructor.
     */
    public void init(Object object)
    {
        super.init(object);
    }
    /**
     * I'm done with this item, free the resources.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Delete this item.
     */
    public boolean remove()
    {
        boolean bSuccess = false;
        try   {
            if ((this.getEditMode() == Constants.EDIT_IN_PROGRESS)
                || (this.getEditMode() == Constants.EDIT_CURRENT))
                    this.getTable().remove();
        } catch (Exception ex)  {
            ex.printStackTrace();
            bSuccess = false;
        }
        return bSuccess;
    }
    /**
     * Get the name of the remote class.
     */
    public String getRemoteClassName()
    {
        String strClassName = Appointment.class.getName().toString();
        int iThinPos = strClassName.indexOf("thin.");
        return strClassName.substring(0, iThinPos) + strClassName.substring(iThinPos + 5);
    }
    /**
     * Get the description.
     */
    public String getDescription()
    {
        return this.getField("Description").toString();
    }
    /**
     * Get the start time of this service.
     */
    public Date getStartDate()
    {
        return (Date)this.getField("StartDateTime").getData();
    }
    /**
     * Get the ending time of this service.
     */
    public Date getEndDate()
    {
        return (Date)this.getField("EndDateTime").getData();
    }
    /**
     * Get the meal description on this date.
     */
    public String getMealDesc(Date date)
    {
        return null;
    }
    /**
     * Get the icon (opt).
     */
    public ImageIcon getIcon(int iIconType)
    {
            return null;
    }
    /**
     * Highlight color (optional).
     */
    public Color getHighlightColor()
    {
        return Color.green;
    }
    /**
     * Highlight color (optional).
     */
    public Color getSelectColor()
    {
        return Color.red;
    }
    /**
     * Change the start time of this service.
     */
    public Date setStartDate(Date time)
    {
        try   {
            this.getTable().edit();
            this.getField("StartDateTime").setData(time);
            this.getTable().set(this);
            this.getTable().seek(null);    // Read this record
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        return this.getStartDate();
    }
    /**
     * Change the ending time of this service.
     */
    public Date setEndDate(Date time)
    {
        try   {
            this.getTable().edit();
            this.getField("EndDateTime").setData(time);
            this.getTable().set(this);
            this.getTable().seek(null);    // Read this record
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        return this.getEndDate();
    }
    /**
     * Set the icon (opt).
     */
    public void setIcon(ImageIcon icon, int iIconType)
    {
    }
    /**
     * Get the display window for this object.
     */
    public Object getVisualJavaBean(int iPanelType)
    {
        return null;
    }
    /**
     * Get the status of this object.
     */
    public int getStatus()
    {
        return 0;
    }
    /**
     * Set the status of this item.
     */
    public int setStatus(int iStatus)
    {
        return 0;
    }
}
