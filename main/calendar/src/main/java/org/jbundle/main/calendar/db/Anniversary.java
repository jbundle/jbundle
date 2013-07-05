/**
 * @(#)Anniversary.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.calendar.db;

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
import org.jbundle.base.db.shared.*;
import org.jbundle.model.main.calendar.db.*;

/**
 *  Anniversary - Recurring appointments.
 */
public class Anniversary extends CalendarEntry
     implements AnniversaryModel
{
    private static final long serialVersionUID = 1L;

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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(ANNIVERSARY_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
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
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
        {   // This is a little weird... can't directly change this table, must edit AnnivMaster
            Record recAnnivMaster = ((ReferenceField)this.getField(Anniversary.ANNIV_MASTER_ID)).getReferenceRecord(this.getRecordOwner());
            recAnnivMaster.setOpenMode(recAnnivMaster.getOpenMode() & ~DBConstants.OPEN_READ_ONLY);
            if ((this.getEditMode() == DBConstants.EDIT_CURRENT)
                || (this.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    recAnnivMaster = ((ReferenceField)this.getField(Anniversary.ANNIV_MASTER_ID)).getReference();
            // Disconnect recAnnivMaster and free this
            ((ReferenceField)this.getField(Anniversary.ANNIV_MASTER_ID)).setReferenceRecord(null);
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
        //if (iFieldSeq == 3)
        //{
        //  field = new CalendarEntryTypeField(this, CALENDAR_ENTRY_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 4)
        //  field = new DateTimeField(this, START_DATE_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 5)
        //  field = new DateTimeField(this, END_DATE_TIME, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == 6)
        //  field = new StringField(this, DESCRIPTION, 60, null, null);
        //if (iFieldSeq == 7)
        //{
        //  field = new CalendarCategoryField(this, CALENDAR_CATEGORY_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.addListener(new InitOnceFieldHandler(null));
        //}
        //if (iFieldSeq == 8)
        //  field = new BooleanField(this, HIDDEN, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //if (iFieldSeq == 9)
        //  field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 10)
            field = new AnnivMasterField(this, ANNIV_MASTER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add all standard file & field behaviors.
     * Override this to add record listeners and filters.
     */
    public void addListeners()
    {
        super.addListeners();
        
        this.addListener(new SharedFileHandler(CalendarEntry.CALENDAR_ENTRY_TYPE_ID, ANNIVERSARY_ID));
    }

}
