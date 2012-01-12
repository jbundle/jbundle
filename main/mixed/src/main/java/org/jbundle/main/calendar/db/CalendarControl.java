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
import org.jbundle.base.util.*;
import org.jbundle.model.*;

/**
 *  CalendarControl - Calendar control.
 */
public class CalendarControl extends ControlRecord
     implements org.jbundle.model.main.calendar.db.CalendarControlModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kLastUpdateDate = kControlRecordLastField + 1;
    public static final int kStartAnnivDate = kLastUpdateDate + 1;
    public static final int kEndAnnivDate = kStartAnnivDate + 1;
    public static final int kUpdateDays = kEndAnnivDate + 1;
    public static final int kAnnivBackDays = kUpdateDays + 1;
    public static final int kAnniversaryDays = kAnnivBackDays + 1;
    public static final int kAnniversaryCategoryID = kAnniversaryDays + 1;
    public static final int kAppointmentCategoryID = kAnniversaryCategoryID + 1;
    public static final int kCalendarControlLastField = kAppointmentCategoryID;
    public static final int kCalendarControlFields = kAppointmentCategoryID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kCalendarControlLastKey = kIDKey;
    public static final int kCalendarControlKeys = kIDKey - DBConstants.MAIN_KEY_FIELD + 1;
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

    public static final String kCalendarControlFile = "CalendarControl";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kCalendarControlFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kLastUpdateDate)
        {
            field = new DateTimeField(this, "LastUpdateDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == kStartAnnivDate)
        {
            field = new DateTimeField(this, "StartAnnivDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == kEndAnnivDate)
        {
            field = new DateTimeField(this, "EndAnnivDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == kUpdateDays)
            field = new IntegerField(this, "UpdateDays", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(1));
        if (iFieldSeq == kAnnivBackDays)
            field = new IntegerField(this, "AnnivBackDays", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(0));
        if (iFieldSeq == kAnniversaryDays)
            field = new IntegerField(this, "AnniversaryDays", Constants.DEFAULT_FIELD_LENGTH, null, new Integer(60));
        if (iFieldSeq == kAnniversaryCategoryID)
            field = new CalendarCategoryField(this, "AnniversaryCategoryID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kAppointmentCategoryID)
            field = new CalendarCategoryField(this, "AppointmentCategoryID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kCalendarControlLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == kIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "PrimaryKey");
            keyArea.addKeyField(kID, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kCalendarControlLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kCalendarControlLastKey)
                keyArea = new EmptyKey(this);
        }
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
        int iUpdateDays = (int)this.getField(CalendarControl.kUpdateDays).getValue();
        Calendar calNow = new GregorianCalendar();
        if ((this.getField(CalendarControl.kStartAnnivDate).isNull())
            || (this.getField(CalendarControl.kEndAnnivDate).isNull()))
                bUpdateDates = true;
        else
        {
            Calendar calCutoff = ((DateTimeField)this.getField(CalendarControl.kLastUpdateDate)).getCalendar();
            calCutoff.add(Calendar.DAY_OF_YEAR, iUpdateDays);
            if (calNow.after(calCutoff))
                bUpdateDates = true;
        }
        if (bUpdateDates)
        {
            int iBackDays = (int)this.getField(CalendarControl.kAnnivBackDays).getValue();
            int iRangeDays = (int)this.getField(CalendarControl.kAnniversaryDays).getValue();
            Calendar calStart = (Calendar)calNow.clone();
            calStart.add(Calendar.DAY_OF_YEAR, -iBackDays);
            Calendar calEnd = (Calendar)calStart.clone();
            calEnd.add(Calendar.DAY_OF_YEAR, iRangeDays - iBackDays);
            
            Calendar calOldEnd = ((DateTimeField)this.getField(CalendarControl.kEndAnnivDate)).getCalendar();
            if (calOldEnd == null)
                calOldEnd = calNow;
        
            ((DateTimeField)this.getField(CalendarControl.kStartAnnivDate)).setCalendar(calStart, true, DBConstants.SCREEN_MOVE);
            ((DateTimeField)this.getField(CalendarControl.kEndAnnivDate)).setCalendar(calEnd, true, DBConstants.SCREEN_MOVE);
            ((DateTimeField)this.getField(CalendarControl.kLastUpdateDate)).setCalendar(calNow, true, DBConstants.SCREEN_MOVE);
            
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
        recAnniversary.setKeyArea(Anniversary.kStartDateTimeKey);
        try {
            while (recAnniversary.hasNext())
            {
                recAnniversary.next();
                if (recAnniversary.getField(Anniversary.kStartDateTime).compareTo(this.getField(CalendarControl.kStartAnnivDate)) > 0)
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
