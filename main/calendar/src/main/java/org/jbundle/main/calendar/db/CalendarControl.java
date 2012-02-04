/**
 * @(#)CalendarControl.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.calendar.db;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.model.main.calendar.db.*;

/**
 *  CalendarControl - Calendar control.
 */
public class CalendarControl extends ControlRecord
     implements CalendarControlModel
{
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public CalendarControl()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CalendarControl(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(CALENDAR_CONTROL_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "main";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.LOCAL | DBConstants.USER_DATA;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == 0)
        //{
        //  field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 1)
        //{
        //  field = new RecordChangedField(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 2)
        //{
        //  field = new BooleanField(this, DELETED, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //  field.setHidden(true);
        //}
        if (iFieldSeq == 3)
        {
            field = new DateTimeField(this, LAST_UPDATE_DATE, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == 4)
        {
            field = new DateTimeField(this, START_ANNIV_DATE, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == 5)
        {
            field = new DateTimeField(this, END_ANNIV_DATE, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == 6)
            field = new IntegerField(this, UPDATE_DAYS, Constants.DEFAULT_FIELD_LENGTH, null, new Integer(1));
        if (iFieldSeq == 7)
            field = new IntegerField(this, ANNIV_BACK_DAYS, Constants.DEFAULT_FIELD_LENGTH, null, new Integer(0));
        if (iFieldSeq == 8)
            field = new IntegerField(this, ANNIVERSARY_DAYS, Constants.DEFAULT_FIELD_LENGTH, null, new Integer(60));
        if (iFieldSeq == 9)
            field = new CalendarCategoryField(this, ANNIVERSARY_CATEGORY_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 10)
            field = new CalendarCategoryField(this, APPOINTMENT_CATEGORY_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == 0)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ID");
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();   // This will read the current record
        // Make sure the AnnivMaster update is current.
        this.updateDatesAndCalendar();    // Don't do in slave
    }
    /**
     * UpdateDatesAndCalendar Method.
     */
    public void updateDatesAndCalendar()
    {
        if ((this.getEditMode() == DBConstants.EDIT_NONE) || (this.getEditMode() == DBConstants.EDIT_ADD))
            return; // Only on initial setup
        boolean bUpdateDates = false;
        int iUpdateDays = (int)this.getField(CalendarControl.UPDATE_DAYS).getValue();
        Calendar calNow = new GregorianCalendar();
        if ((this.getField(CalendarControl.START_ANNIV_DATE).isNull())
            || (this.getField(CalendarControl.END_ANNIV_DATE).isNull()))
                bUpdateDates = true;
        else
        {
            Calendar calCutoff = ((DateTimeField)this.getField(CalendarControl.LAST_UPDATE_DATE)).getCalendar();
            calCutoff.add(Calendar.DAY_OF_YEAR, iUpdateDays);
            if (calNow.after(calCutoff))
                bUpdateDates = true;
        }
        if (bUpdateDates)
        {
            int iBackDays = (int)this.getField(CalendarControl.ANNIV_BACK_DAYS).getValue();
            int iRangeDays = (int)this.getField(CalendarControl.ANNIVERSARY_DAYS).getValue();
            Calendar calStart = (Calendar)calNow.clone();
            calStart.add(Calendar.DAY_OF_YEAR, -iBackDays);
            Calendar calEnd = (Calendar)calStart.clone();
            calEnd.add(Calendar.DAY_OF_YEAR, iRangeDays - iBackDays);
            
            Calendar calOldEnd = ((DateTimeField)this.getField(CalendarControl.END_ANNIV_DATE)).getCalendar();
            if (calOldEnd == null)
                calOldEnd = calNow;
        
            ((DateTimeField)this.getField(CalendarControl.START_ANNIV_DATE)).setCalendar(calStart, true, DBConstants.SCREEN_MOVE);
            ((DateTimeField)this.getField(CalendarControl.END_ANNIV_DATE)).setCalendar(calEnd, true, DBConstants.SCREEN_MOVE);
            ((DateTimeField)this.getField(CalendarControl.LAST_UPDATE_DATE)).setCalendar(calNow, true, DBConstants.SCREEN_MOVE);
            
            this.updateCalendar(calOldEnd, calEnd);  // Update the calendar
            
            try {
                this.writeAndRefresh();
            } catch (DBException ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * UpdateCalendar Method.
     */
    public void updateCalendar(Calendar calStart, Calendar calEnd)
    {
        Anniversary recAnniversary = new Anniversary(this.getRecordOwner());
        AnnivMaster recAnnivMaster = new AnnivMaster(this.getRecordOwner());
        recAnniversary.setKeyArea(Anniversary.START_DATE_TIME_KEY);
        try {
            while (recAnniversary.hasNext())
            {
                recAnniversary.next();
                if (recAnniversary.getField(Anniversary.START_DATE_TIME).compareTo(this.getField(CalendarControl.START_ANNIV_DATE)) > 0)
                    break;  // end of the entries that are not in the current range.
                recAnniversary.edit();
                recAnniversary.remove();
            }
            
            while (recAnnivMaster.hasNext())
            {
                recAnnivMaster.next();
                recAnnivMaster.addAppointments(recAnniversary, calStart, calEnd);
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        } finally {
            recAnniversary.free();
            recAnnivMaster.free();
        }
    }

}
