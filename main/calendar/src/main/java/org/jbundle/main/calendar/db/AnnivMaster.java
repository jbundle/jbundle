/**
 * @(#)AnnivMaster.
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
import org.jbundle.main.calendar.screen.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.model.message.*;
import org.jbundle.model.main.calendar.db.*;

/**
 *  AnnivMaster - Recurring appointments.
 */
public class AnnivMaster extends VirtualRecord
     implements AnnivMasterModel
{
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public AnnivMaster()
    {
        super();
    }
    /**
     * Constructor.
     */
    public AnnivMaster(RecordOwner screen)
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
        return (m_tableName == null) ? Record.formatTableNames(ANNIV_MASTER_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Anniversary";
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
        return DBConstants.REMOTE | DBConstants.USER_DATA;
    }
    /**
     * Make a default screen.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(ANNIV_MASTER_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = Record.makeNewScreen(ANNIV_MASTER_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else
            screen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);
        return screen;
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
            field = new DateTimeField(this, START_DATE_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 4)
            field = new DateTimeField(this, END_DATE_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 5)
            field = new StringField(this, DESCRIPTION, 60, null, null);
        if (iFieldSeq == 6)
            field = new RepeatIntervalField(this, REPEAT_INTERVAL_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 7)
            field = new ShortField(this, REPEAT_COUNT, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 8)
            field = new CalendarCategoryField(this, CALENDAR_CATEGORY_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 9)
            field = new BooleanField(this, HIDDEN, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        if (iFieldSeq == 10)
            field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
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
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "StartDateTime");
            keyArea.addKeyField(START_DATE_TIME, DBConstants.ASCENDING);
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
        super.addMasterListeners();
        
        if ((this.getMasterSlave() & RecordOwner.MASTER) != 0)    // Don't do in slave
        {
            this.addListener(new AnnivMasterHandler(null));
            
            this.addListener(new FileListener(null)
            {
                public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
                {
                    int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);
                    if ((iChangeType == DBConstants.AFTER_ADD_TYPE) || (iChangeType == DBConstants.AFTER_UPDATE_TYPE))
                            if (!getField(AnnivMaster.PROPERTIES).isNull())
                    {   // This will cause the JobScheduler to reschedule the jobs (now that they have changed)
                        MessageManager messageManager = ((Application)getTask().getApplication()).getMessageManager();
                        Map<String,Object> properties = new Hashtable<String,Object>();
                        properties.put(DBParams.PROCESS, CalendarEntry.JOB_PROCESS_NAME);
                        if (messageManager != null)
                            messageManager.sendMessage(new MapMessage(new BaseMessageHeader(CalendarEntry.JOB_QUEUE_NAME, MessageConstants.INTRANET_QUEUE, this, null), properties));
                    }
                    return iErrorCode;
                }
            });
        }
    }
    /**
     * RemoveAppointments Method.
     */
    public void removeAppointments(Anniversary recAnniversary)
    {
        SubFileFilter listener = new SubFileFilter(this);
        recAnniversary.addListener(listener);
        try {
            recAnniversary.close();
            
            while (recAnniversary.hasNext())
            {
                recAnniversary.next();
                recAnniversary.edit();
                recAnniversary.remove();
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        } finally {
            recAnniversary.removeListener(listener, true);
        }
    }
    /**
     * AddAppointments Method.
     */
    public void addAppointments(Anniversary recAnniversary, Calendar calStart, Calendar calEnd)
    {
        try {
            Converter.initGlobals();
            Calendar calendar = Converter.gCalendar;
            
            Record recRepeat = ((ReferenceField)this.getField(AnnivMaster.REPEAT_INTERVAL_ID)).getReference();
            String strRepeat = null;
            if (recRepeat != null)
                strRepeat = recRepeat.getField(RepeatInterval.DESCRIPTION).toString();
            char chRepeat;
            if ((strRepeat == null) || (strRepeat.length() == 0))
                chRepeat = 'Y';
            else
                chRepeat = strRepeat.toUpperCase().charAt(0);
            int iRepeatCode;
            if (chRepeat == 'D')
                iRepeatCode = Calendar.DATE;
            else if (chRepeat == 'W')
                iRepeatCode = Calendar.WEEK_OF_YEAR;
            else if (chRepeat == 'M')
                iRepeatCode = Calendar.MONTH;
            else
                iRepeatCode = Calendar.YEAR;
            short sRepeatCount = (short)this.getField(AnnivMaster.REPEAT_COUNT).getValue();
            if (sRepeatCount == 0)
                sRepeatCount = 1;
            Date timeStart = ((DateTimeField)this.getField(AnnivMaster.START_DATE_TIME)).getDateTime();
            Date timeEnd = ((DateTimeField)this.getField(AnnivMaster.END_DATE_TIME)).getDateTime();
            long lTimeLength = -1;
            if (timeEnd != null)
                lTimeLength = timeEnd.getTime() - timeStart.getTime();
        
            calendar.setTime(timeStart);
            for (int i = 0; i < 100; i++)
            {
                if ((calendar.after(calStart))
                    && (calendar.before(calEnd)))
                {
                    timeStart = calendar.getTime();
                    timeEnd = null;
                    if (lTimeLength != -1)
                        timeEnd = new Date(timeStart.getTime() + lTimeLength);
        
                    recAnniversary.addNew();
                    ((DateTimeField)recAnniversary.getField(Anniversary.START_DATE_TIME)).setDateTime(timeStart, false, DBConstants.SCREEN_MOVE);
                    if (timeEnd != null)
                        ((DateTimeField)recAnniversary.getField(Anniversary.END_DATE_TIME)).setDateTime(timeEnd, false, DBConstants.SCREEN_MOVE);
                    recAnniversary.getField(Anniversary.DESCRIPTION).moveFieldToThis(this.getField(AnnivMaster.DESCRIPTION));
                    ((ReferenceField)recAnniversary.getField(Anniversary.ANNIV_MASTER_ID)).setReference(this);
                    recAnniversary.getField(Anniversary.CALENDAR_CATEGORY_ID).moveFieldToThis(this.getField(AnnivMaster.CALENDAR_CATEGORY_ID));
                    recAnniversary.getField(Anniversary.HIDDEN).moveFieldToThis(this.getField(AnnivMaster.HIDDEN));
                    // Don't move properties (you will have to read the AnnivMaster to get the properties)
                    recAnniversary.add();
                }
                calendar.add(iRepeatCode, sRepeatCount);
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }

}
