/**
 * @(#)CalendarEntry.
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
import javax.swing.*;
import org.jbundle.thin.base.screen.*;
import org.jbundle.thin.base.message.*;

/**
 *  CalendarEntry - Calendar.
 */
public class CalendarEntry extends VirtualRecord
     implements org.jbundle.model.main.calendar.db.CalendarEntryModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kCalendarEntryTypeID = kVirtualRecordLastField + 1;
    public static final int kStartDateTime = kCalendarEntryTypeID + 1;
    public static final int kEndDateTime = kStartDateTime + 1;
    public static final int kDescription = kEndDateTime + 1;
    public static final int kCalendarCategoryID = kDescription + 1;
    public static final int kHidden = kCalendarCategoryID + 1;
    public static final int kProperties = kHidden + 1;
    public static final int kAnnivMasterID = kProperties + 1;
    public static final int kCalendarEntryLastField = kAnnivMasterID;
    public static final int kCalendarEntryFields = kAnnivMasterID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kStartDateTimeKey = kIDKey + 1;
    public static final int kAnnivMasterIDKey = kStartDateTimeKey + 1;
    public static final int kCalendarCategoryIDKey = kAnnivMasterIDKey + 1;
    public static final int kCalendarEntryLastKey = kCalendarCategoryIDKey;
    public static final int kCalendarEntryKeys = kCalendarCategoryIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    public static final int ANNIVERSARY_ID = 2;
    public static final int APPOINTMENT_ID = 1;
    public static final String JOB_QUEUE_NAME = "jobSchedulerQueue";
    /**
     * Default constructor.
     */
    public CalendarEntry()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CalendarEntry(RecordOwner screen)
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

    public static final String kCalendarEntryFile = "CalendarEntry";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kCalendarEntryFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Entry";
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
        return DBConstants.REMOTE | DBConstants.BASE_TABLE_CLASS | DBConstants.SHARED_TABLE | DBConstants.USER_DATA;
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
        if (iFieldSeq == kCalendarEntryTypeID)
        {
            field = new CalendarEntryTypeField(this, "CalendarEntryTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == kStartDateTime)
            field = new DateTimeField(this, "StartDateTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kEndDateTime)
            field = new DateTimeField(this, "EndDateTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kDescription)
            field = new StringField(this, "Description", 60, null, null);
        if (iFieldSeq == kCalendarCategoryID)
        {
            field = new CalendarCategoryField(this, "CalendarCategoryID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kHidden)
            field = new BooleanField(this, "Hidden", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        if (iFieldSeq == kProperties)
            field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kAnnivMasterID)
            field = new AnnivMasterField(this, "AnnivMasterID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kCalendarEntryLastField)
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
        if (iKeyArea == kAnnivMasterIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "AnnivMasterID");
            keyArea.addKeyField(kAnnivMasterID, DBConstants.ASCENDING);
            keyArea.addKeyField(kStartDateTime, DBConstants.ASCENDING);
        }
        if (iKeyArea == kCalendarCategoryIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "CalendarCategoryID");
            keyArea.addKeyField(kCalendarCategoryID, DBConstants.ASCENDING);
            keyArea.addKeyField(kStartDateTime, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kCalendarEntryLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kCalendarEntryLastKey)
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
        
        this.addListener(new FileListener(null)
        {
            public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
            {
                int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);
                if ((iChangeType == DBConstants.AFTER_ADD_TYPE) || (iChangeType == DBConstants.AFTER_UPDATE_TYPE))
                    if (getSharedRecordTypeKey().getValue() == CalendarEntry.APPOINTMENT_ID)
                        if (!getField(CalendarEntry.kProperties).isNull())
                {   // This will cause the JobScheduler to reschedule the jobs (now that they have changed)
                    BaseMessageManager messageManager = ((Application)getTask().getApplication()).getMessageManager();
                    Map<String,Object> properties = new Hashtable<String,Object>();
                    properties.put(DBParams.PROCESS, JobSchedulerReference.JOB_PROCESS_NAME);
                    if (messageManager != null)
                        messageManager.sendMessage(new MapMessage(new BaseMessageHeader(JOB_QUEUE_NAME, MessageConstants.INTRANET_QUEUE, this, null), properties));
                }
                return iErrorCode;
            }
        });
    }
    /**
     * Get the properties from the Properties field,
     * and merge with the properties from the Master field
     * if it exists.
     */
    public Map<String,Object> getProperties()
    {
        Map<String,Object> properties = ((PropertiesField)this.getField(CalendarEntry.kProperties)).getProperties();
        if (!this.getField(CalendarEntry.kAnnivMasterID).isNull())
            if (this.getField(CalendarEntry.kAnnivMasterID) instanceof ReferenceField)
        {
            Record recAnnivMaster = ((ReferenceField)this.getField(CalendarEntry.kAnnivMasterID)).getReference();
            if ((recAnnivMaster != null) && ((recAnnivMaster.getEditMode() == DBConstants.EDIT_CURRENT) || (recAnnivMaster.getEditMode() == DBConstants.EDIT_IN_PROGRESS)))
            {
                Map<String,Object> propMaster = ((PropertiesField)recAnnivMaster.getField(AnnivMaster.kProperties)).getProperties();
                if (propMaster != null)
                {
                    if (properties != null)
                        propMaster.putAll(properties);  // Merge them
                    properties = propMaster;
                }
            }
        }
        return properties;
    }
    /**
     * Get the icon for the screen display.
     */
    public ImageIcon getStartIcon()
    {
        ImageIcon iconStart = null;
        Record recCalendarCategory = ((ReferenceField)this.getField(CalendarEntry.kCalendarCategoryID)).getReference();
        if ((recCalendarCategory == null) || (recCalendarCategory.getEditMode() != DBConstants.EDIT_CURRENT))
            if (this.getField(CalendarEntry.kAnnivMasterID) instanceof ReferenceField)
        {
        //    Record recAnnivMaster = ((ReferenceField)this.getField(CalendarEntry.kAnnivMasterID)).getReference();
        //    if ((recAnnivMaster != null) && ((recAnnivMaster.getEditMode() == DBConstants.EDIT_CURRENT) || (recAnnivMaster.getEditMode() == DBConstants.EDIT_IN_PROGRESS)))
        //        recCalendarCategory = ((ReferenceField)recAnnivMaster.getField(AnnivMaster.kCalendarCategoryID)).getReference();
        }
        if ((recCalendarCategory != null) && (recCalendarCategory.getEditMode() == DBConstants.EDIT_CURRENT))
            iconStart = (ImageIcon)new ImageIcon(((ImageField)recCalendarCategory.getField(CalendarCategory.kIcon)).getImage().getImage());
        if (iconStart == null)
        {
            if (this.getTask() instanceof BaseApplet)
                iconStart = ((BaseApplet)this.getTask()).loadImageIcon("Calendar");
        }
        return iconStart;
    }
    /**
     * Get the record type from the field that specifies the record type.
     * (Override this).
     * @return The record type (as an object).
     */
    public BaseField getSharedRecordTypeKey()
    {
        return this.getField(CalendarEntry.kCalendarEntryTypeID);
    }
    /**
     * Get the shared record that goes with this key.
     * (Always override this).
     * @param objKey The value that specifies the record type.
     * @return The correct (new) record for this type (or null if none).
     */
    public Record createSharedRecord(Object objKey, RecordOwner recordOwner)
    {
        if (objKey instanceof Integer)
        {
            int iCalendarType = ((Integer)objKey).intValue();
            if (iCalendarType == CalendarEntry.APPOINTMENT_ID)
                return new Appointment(recordOwner);
            if (iCalendarType == CalendarEntry.ANNIVERSARY_ID)
                return new Anniversary(recordOwner);
        }
        return null;
    }

}
