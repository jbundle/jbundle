/**
 * @(#)Anniversary.
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
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.db.shared.*;
import org.jbundle.model.main.calendar.db.*;

/**
 *  Anniversary - Recurring appointments.
 */
public class Anniversary extends CalendarEntry
     implements AnniversaryModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    //public static final int kCalendarEntryTypeID = kCalendarEntryTypeID;
    //public static final int kStartDateTime = kStartDateTime;
    //public static final int kEndDateTime = kEndDateTime;
    //public static final int kDescription = kDescription;
    //public static final int kCalendarCategoryID = kCalendarCategoryID;
    //public static final int kHidden = kHidden;
    //public static final int kProperties = kProperties;
    //public static final int kAnnivMasterID = kAnnivMasterID;
    public static final int kAnniversaryLastField = kCalendarEntryLastField;
    public static final int kAnniversaryFields = kCalendarEntryLastField - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public Anniversary()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Anniversary(RecordOwner screen)
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

    public static final String kAnniversaryFile = "CalendarEntry";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kAnniversaryFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        return DBConstants.REMOTE | DBConstants.SHARED_TABLE | DBConstants.USER_DATA;
    }
    /**
     * MakeScreen Method.
     */
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
        {   // This is a little weird... can't directly change this table, must edit AnnivMaster
            Record recAnnivMaster = ((ReferenceField)this.getField(Anniversary.kAnnivMasterID)).getReferenceRecord(this.getRecordOwner());
            recAnnivMaster.setOpenMode(recAnnivMaster.getOpenMode() & ~DBConstants.OPEN_READ_ONLY);
            if ((this.getEditMode() == DBConstants.EDIT_CURRENT)
                || (this.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    recAnnivMaster = ((ReferenceField)this.getField(Anniversary.kAnnivMasterID)).getReference();
            // Disconnect recAnnivMaster and free this
            ((ReferenceField)this.getField(Anniversary.kAnnivMasterID)).setReferenceRecord(null);
            this.free();
            return recAnnivMaster.makeScreen(itsLocation, parentScreen, iDocMode, properties);
        }
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
        //if (iFieldSeq == kCalendarEntryTypeID)
        //{
        //  field = new CalendarEntryTypeField(this, "CalendarEntryTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == kStartDateTime)
        //  field = new DateTimeField(this, "StartDateTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kEndDateTime)
        //  field = new DateTimeField(this, "EndDateTime", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kDescription)
        //  field = new StringField(this, "Description", 60, null, null);
        //if (iFieldSeq == kCalendarCategoryID)
        //{
        //  field = new CalendarCategoryField(this, "CalendarCategoryID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.addListener(new InitOnceFieldHandler(null));
        //}
        //if (iFieldSeq == kHidden)
        //  field = new BooleanField(this, "Hidden", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //if (iFieldSeq == kProperties)
        //  field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kAnnivMasterID)
            field = new AnnivMasterField(this, "AnnivMasterID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kAnniversaryLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add all standard file & field behaviors.
     * Override this to add record listeners and filters.
     */
    public void addListeners()
    {
        super.addListeners();
        
        this.addListener(new SharedFileHandler(CalendarEntry.kCalendarEntryTypeID, CalendarEntry.ANNIVERSARY_ID));
    }

}
