/**
 * @(#)CalendarEntry.
 * Copyright © 2012 jbundle.org. All rights reserved.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import javax.swing.*;
import org.jbundle.thin.base.screen.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.model.message.*;
import org.jbundle.model.main.calendar.db.*;

/**
 *  CalendarEntry - Calendar.
 */
public class CalendarEntry extends VirtualRecord
     implements CalendarEntryModel
{
    private static final long serialVersionUID = 1L;

    public static final String JOB_PROCESS_NAME = "org.jbundle.main.schedule.app.JobSchedulerProcess";
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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(CALENDAR_ENTRY_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
            field = new CalendarEntryTypeField(this, CALENDAR_ENTRY_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setHidden(true);
        }
        if (iFieldSeq == 4)
            field = new DateTimeField(this, START_DATE_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 5)
            field = new DateTimeField(this, END_DATE_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 6)
            field = new StringField(this, DESCRIPTION, 60, null, null);
        if (iFieldSeq == 7)
        {
            field = new CalendarCategoryField(this, CALENDAR_CATEGORY_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 8)
            field = new BooleanField(this, HIDDEN, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        if (iFieldSeq == 9)
            field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 10)
            field = new AnnivMasterField(this, ANNIV_MASTER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
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
        if (iKeyArea == 2)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "AnnivMasterID");
            keyArea.addKeyField(ANNIV_MASTER_ID, DBConstants.ASCENDING);
            keyArea.addKeyField(START_DATE_TIME, DBConstants.ASCENDING);
        }
        if (iKeyArea == 3)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "CalendarCategoryID");
            keyArea.addKeyField(CALENDAR_CATEGORY_ID, DBConstants.ASCENDING);
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
        
        this.addListener(new FileListener(null)
        {
            public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
            {
                int iErrorCode = super.doRecordChange(field, iChangeType, bDisplayOption);
                if ((iChangeType == DBConstants.AFTER_ADD_TYPE) || (iChangeType == DBConstants.AFTER_UPDATE_TYPE))
                    if (getSharedRecordTypeKey().getValue() == CalendarEntry.APPOINTMENT_ID)
                        if (!getField(CalendarEntry.PROPERTIES).isNull())
                {   // This will cause the JobScheduler to reschedule the jobs (now that they have changed)
                    MessageManager messageManager = ((Application)getTask().getApplication()).getMessageManager();
                    Map<String,Object> properties = new Hashtable<String,Object>();
                    properties.put(DBParams.PROCESS, JOB_PROCESS_NAME);
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
        Map<String,Object> properties = ((PropertiesField)this.getField(CalendarEntry.PROPERTIES)).getProperties();
        if (!this.getField(Anniversary.ANNIV_MASTER_ID).isNull())
            if (this.getField(Anniversary.ANNIV_MASTER_ID) instanceof ReferenceField)
        {
            Record recAnnivMaster = ((ReferenceField)this.getField(Anniversary.ANNIV_MASTER_ID)).getReference();
            if ((recAnnivMaster != null) && ((recAnnivMaster.getEditMode() == DBConstants.EDIT_CURRENT) || (recAnnivMaster.getEditMode() == DBConstants.EDIT_IN_PROGRESS)))
            {
                Map<String,Object> propMaster = ((PropertiesField)recAnnivMaster.getField(AnnivMaster.PROPERTIES)).getProperties();
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
        Record recCalendarCategory = ((ReferenceField)this.getField(CalendarEntry.CALENDAR_CATEGORY_ID)).getReference();
        if ((recCalendarCategory == null) || (recCalendarCategory.getEditMode() != DBConstants.EDIT_CURRENT))
            if (this.getField(Anniversary.ANNIV_MASTER_ID) instanceof ReferenceField)
        {
        //    Record recAnnivMaster = ((ReferenceField)this.getField(Anniversary.ANNIV_MASTER_ID)).getReference();
        //    if ((recAnnivMaster != null) && ((recAnnivMaster.getEditMode() == DBConstants.EDIT_CURRENT) || (recAnnivMaster.getEditMode() == DBConstants.EDIT_IN_PROGRESS)))
        //        recCalendarCategory = ((ReferenceField)recAnnivMaster.getField(AnnivMaster.CALENDAR_CATEGORY_ID)).getReference();
        }
        if ((recCalendarCategory != null) && (recCalendarCategory.getEditMode() == DBConstants.EDIT_CURRENT))
            iconStart = (ImageIcon)new ImageIcon((Image)((ImageField)recCalendarCategory.getField(CalendarCategory.ICON)).getImage().getImage());
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
        return this.getField(CalendarEntry.CALENDAR_ENTRY_TYPE_ID);
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
