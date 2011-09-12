/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.calendar;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.cal.popup.ProductTypeInfo;
import org.jbundle.util.calendarpanel.model.CalendarConstants;
import org.jbundle.util.calendarpanel.model.CalendarItem;
import org.jbundle.thin.main.calendar.db.Appointment;
import org.jbundle.thin.main.calendar.db.CalendarEntry;


public class AppointmentCalendarItem extends CalendarEntry
    implements CalendarItem
{
	private static final long serialVersionUID = 1L;

	//x   ProductTypeInfo m_productTypeInfo = new ProductTypeInfo(ProductConstants.ITEM, new Color(224, 224, 224), null, true);
    ProductTypeInfo m_productTypeInfo = new ProductTypeInfo(null, new Color(224, 224, 224), null, true);

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
            {
                this.getTable().remove();
                bSuccess = true;
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
            bSuccess = false;
        }
        return bSuccess;
    }
    /**
     * Get the remote class name (can't use super!).
     */
    public String getRemoteClassName()
    {
        String strClassName = CalendarEntry.class.getName().toString();
        int iThinPos = strClassName.indexOf(Constants.THIN_SUBPACKAGE);
        return strClassName.substring(0, iThinPos) + strClassName.substring(iThinPos + 5);
    }
    /**
     * Get the description.
     */
    public String getDescription()
    {
        return this.getField(Appointment.DESCRIPTION).toString();
    }
    /**
     * Get the start time of this service.
     */
    public Date getStartDate()
    {
        return (Date)this.getField(Appointment.START_DATE_TIME).getData();
    }
    /**
     * Get the ending time of this service.
     */
    public Date getEndDate()
    {
        Date date = (Date)this.getField(Appointment.END_DATE_TIME).getData();
        if (date == null)
        {
            Date dateStart = (Date)this.getStartDate();
            if (dateStart != null)
            {
                Converter.initGlobals();
                Calendar calendar = Converter.gCalendar;
                calendar.setTime(dateStart);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                date = calendar.getTime();
            }
        }
        return date;
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
        if (iIconType == CalendarConstants.END_ICON)
            return m_productTypeInfo.getEndIcon();  // null
        if (m_iconAnniversary == null)
        {   // First time
            m_iconAppointment = org.jbundle.thin.base.screen.BaseApplet.getSharedInstance().loadImageIcon("Appointment" + ".gif", "Appointment");
            m_iconAnniversary = org.jbundle.thin.base.screen.BaseApplet.getSharedInstance().loadImageIcon("Anniversary" + ".gif", "Anniversary");
        }
        Object objType = this.getField(CalendarEntry.CALENDAR_ENTRY_TYPE_ID).getData();
        if (objType instanceof Integer)
            if (((Integer)objType).intValue() == CalendarEntry.ANNIVERSARY_ID)
                return m_iconAnniversary;
        return m_iconAppointment;
    }
    protected ImageIcon m_iconAppointment = null;
    protected ImageIcon m_iconAnniversary = null;
    /**
     * Highlight color (optional).
     */
    public Color getHighlightColor()
    {
        return m_productTypeInfo.getHighlightColor();
    }
    /**
     * Highlight color (optional).
     */
    public Color getSelectColor()
    {
        return m_productTypeInfo.getSelectColor();
    }
    /**
     * Change the start time of this service.
     */
    public Date setStartDate(Date time)
    {
        try   {
            this.getTable().edit();
            Date dateOld = (Date)this.getField(Appointment.START_DATE_TIME).getData();
            if (dateOld != null)
            {
                Date dateEnd = (Date)this.getEndDate();
                if (dateEnd != null)
                {   // Shift the end date by the same amount
                    dateEnd = new Date(time.getTime() + (dateEnd.getTime() - dateOld.getTime()));
                    this.getField(Appointment.END_DATE_TIME).setData(dateEnd);
                }
            }
            this.getField(Appointment.START_DATE_TIME).setData(time);
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
            this.getField(Appointment.END_DATE_TIME).setData(time);
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
