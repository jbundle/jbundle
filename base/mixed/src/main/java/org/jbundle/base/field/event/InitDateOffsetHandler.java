package org.jbundle.base.field.event;

/**
 * @(#)InitDateOffsetHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Calendar;
import java.util.Date;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.util.DBConstants;


/**
 * When this field is initialized, initialize it to the current date plus
 * the given number of year/month/day offset.
 * Alternatively, add the number of days in the fieldSource field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class InitDateOffsetHandler extends InitFieldHandler
{
    protected int m_lYears = 0;
    protected int m_lMonths = 0;
    protected int m_lDays = 0;
    protected DateTimeField m_fldStartDate = null;
    protected boolean m_bCalcIfNull = true;

    /**
     * Constructor.
     */
    public InitDateOffsetHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fldSource The field with the source number of days to use as an offset.
     * @param lYears Offset by this number of years.
     * @param lMonths Offset by this number of months.
     * @param lDays Offset by this number of days.
     */
    public InitDateOffsetHandler(BaseField fldSource, int lYears, int lMonths, int lDays)
    {
        this();
        this.init(null, fldSource, null, lYears, lMonths, lDays, true);
    }
    /**
     * Constructor.
     * @param fldSource The field with the source number of days to use as an offset.
     * @param lYears Offset by this number of years.
     * @param lMonths Offset by this number of months.
     * @param lDays Offset by this number of days.
     */
    public InitDateOffsetHandler(BaseField fldSource, DateTimeField startDate)
    {
        this();
        this.init(null, fldSource, startDate, 0, 0, 0, true);
    }
    /**
     * Constructor.
     * @param fldSource The field with the source number of days to use as an offset.
     * @param lYears Offset by this number of years.
     * @param lMonths Offset by this number of months.
     * @param lDays Offset by this number of days.
     */
    public InitDateOffsetHandler(BaseField fldSource, int lYears, int lMonths, int lDays, boolean bCalcIfNull)
    {
        this();
        this.init(null, fldSource, null, lYears, lMonths, lDays, bCalcIfNull);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param fldSource The field with the source number of days to use as an offset.
     * @param lYears Offset by this number of years.
     * @param lMonths Offset by this number of months.
     * @param lDays Offset by this number of days.
     */
    public void init(BaseField field, BaseField fldSource, DateTimeField fldStartDate, int lYears, int lMonths, int lDays, boolean bCalcIfNull)
    {
        super.init(field, fldSource, null, true, false);
        m_lYears = lYears;
        m_lMonths = lMonths;
        m_lDays = lDays;
        m_fldStartDate = fldStartDate;
        m_bCalcIfNull = bCalcIfNull;
    }
    /**
     * Set this cloned listener to the same state at this listener.
     * @param field The field this new listener will be added to.
     * @param The new listener to sync to this.
     * @param Has the init method been called?
     * @return True if I called init.
     */
    public boolean syncClonedListener(BaseField field, FieldListener listener, boolean bInitCalled)
    {
        if (!bInitCalled)
        {
            BaseField fldSource = this.getSyncedListenersField(m_fldSource, listener);
            DateTimeField fldStartDate = (DateTimeField)this.getSyncedListenersField(m_fldStartDate, listener);
            ((InitDateOffsetHandler)listener).init(null, fldSource, fldStartDate, m_lYears, m_lMonths, m_lDays, m_bCalcIfNull);
        }
        return super.syncClonedListener(field, listener, true);
    }
    /**
     * The Field has Changed. (listens only for inits.)
     * Inititiazize it to the current date plus the given offset.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     * This is ONLY called on field initialize.
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        if (!(this.getOwner() instanceof DateTimeField))
            return DBConstants.NORMAL_RETURN;
        if (m_fldSource != null)
        {
            Date time = null;
            if (m_fldStartDate != null)
            {
                if ((m_fldStartDate.isNull()) || (m_fldStartDate.getDateTime().getTime() == 0))
                    return this.getOwner().setData(null, bDisplayOption, iMoveMode);
                time = m_fldStartDate.getDateTime();
            }
            else
                time = new Date();     // Current time
            double timeSpan = 0;
            if (!m_fldSource.isNull())
                timeSpan = m_fldSource.getValue();
            else
            {
                if (!m_bCalcIfNull)
                    return super.fieldChanged(bDisplayOption, iMoveMode);
            }
            timeSpan = Math.floor(timeSpan * DBConstants.KMS_IN_A_DAY + 0.5);  // Rounded number of ms
            Date newTime = new Date(time.getTime() + (long)timeSpan);
            return ((DateTimeField)this.getOwner()).setDateTime(newTime, bDisplayOption, iMoveMode);
        }
        else
        {
            Calendar calendar = Calendar.getInstance();   // Current time
            calendar.add(Calendar.YEAR, m_lYears);
            calendar.add(Calendar.MONTH, m_lMonths);
            calendar.add(Calendar.DATE, m_lDays);
            return ((DateTimeField)this.getOwner()).setCalendar(calendar, bDisplayOption, iMoveMode);
        }
    }
}
