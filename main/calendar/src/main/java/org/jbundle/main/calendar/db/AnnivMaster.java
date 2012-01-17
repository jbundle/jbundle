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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.main.calendar.screen.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.model.main.calendar.db.*;

/**
 *  AnnivMaster - Recurring appointments.
 */
public class AnnivMaster extends VirtualRecord
     implements AnnivMasterModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kStartDateTime = kVirtualRecordLastField + 1;
    public static final int kEndDateTime = kStartDateTime + 1;
    public static final int kDescription = kEndDateTime + 1;
    public static final int kRepeatIntervalID = kDescription + 1;
    public static final int kRepeatCount = kRepeatIntervalID + 1;
    public static final int kCalendarCategoryID = kRepeatCount + 1;
    public static final int kHidden = kCalendarCategoryID + 1;
    public static final int kProperties = kHidden + 1;
    public static final int kAnnivMasterLastField = kProperties;
    public static final int kAnnivMasterFields = kProperties - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kStartDateTimeKey = kIDKey + 1;
    public static final int kAnnivMasterLastKey = kStartDateTimeKey;
    public static final int kAnnivMasterKeys = kStartDateTimeKey - DBConstants.MAIN_KEY_FIELD + 1;
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

    public static final String kAnnivMasterFile = "AnnivMaster";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kAnnivMasterFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = BaseScreen.makeNewScreen(ANNIV_MASTER_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = BaseScreen.makeNewScreen(ANNIV_MASTER_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kStartDateTime)
            field = new DateTimeField(this, "StartDateTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kEndDateTime)
            field = new DateTimeField(this, "EndDateTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kDescription)
            field = new StringField(this, "Description", 60, null, null);
        if (iFieldSeq == kRepeatIntervalID)
            field = new RepeatIntervalField(this, "RepeatIntervalID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kRepeatCount)
            field = new ShortField(this, "RepeatCount", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kCalendarCategoryID)
            field = new CalendarCategoryField(this, "CalendarCategoryID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kHidden)
            field = new BooleanField(this, "Hidden", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        if (iFieldSeq == kProperties)
            field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kAnnivMasterLastField)
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
        if (iKeyArea == kStartDateTimeKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "StartDateTime");
            keyArea.addKeyField(kStartDateTime, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kAnnivMasterLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kAnnivMasterLastKey)
                keyArea = new EmptyKey(this);
        }
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
                            if (!getField(AnnivMaster.kProperties).isNull())
                    {   // This will cause the JobScheduler to reschedule the jobs (now that they have changed)
                        BaseMessageManager messageManager = ((Application)getTask().getApplication()).getMessageManager();
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
            
            Record recRepeat = ((ReferenceField)this.getField(AnnivMaster.kRepeatIntervalID)).getReference();
            String strRepeat = null;
            if (recRepeat != null)
                strRepeat = recRepeat.getField(RepeatInterval.kDescription).toString();
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
            short sRepeatCount = (short)this.getField(AnnivMaster.kRepeatCount).getValue();
            if (sRepeatCount == 0)
                sRepeatCount = 1;
            Date timeStart = ((DateTimeField)this.getField(AnnivMaster.kStartDateTime)).getDateTime();
            Date timeEnd = ((DateTimeField)this.getField(AnnivMaster.kEndDateTime)).getDateTime();
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
                    ((DateTimeField)recAnniversary.getField(Anniversary.kStartDateTime)).setDateTime(timeStart, false, DBConstants.SCREEN_MOVE);
                    if (timeEnd != null)
                        ((DateTimeField)recAnniversary.getField(Anniversary.kEndDateTime)).setDateTime(timeEnd, false, DBConstants.SCREEN_MOVE);
                    recAnniversary.getField(Anniversary.kDescription).moveFieldToThis(this.getField(AnnivMaster.kDescription));
                    ((ReferenceField)recAnniversary.getField(Anniversary.kAnnivMasterID)).setReference(this);
                    recAnniversary.getField(Anniversary.kCalendarCategoryID).moveFieldToThis(this.getField(AnnivMaster.kCalendarCategoryID));
                    recAnniversary.getField(Anniversary.kHidden).moveFieldToThis(this.getField(AnnivMaster.kHidden));
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
