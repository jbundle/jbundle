/**
 * @(#)Appointment.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.calendar.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class Appointment extends org.jbundle.thin.main.calendar.db.CalendarEntry
    implements org.jbundle.model.main.calendar.db.AppointmentModel
{

    public Appointment()
    {
        super();
    }
    public Appointment(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String APPOINTMENT_FILE = "CalendarEntry";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Appointment.APPOINTMENT_FILE : super.getTableNames(bAddQuotes);
    }
    /**
     *  Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "main";
    }
    /**
     *  Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return Constants.REMOTE | Constants.SHARED_TABLE | Constants.USER_DATA;
    }
    /**
    * Set up the screen input fields.
    */
    public void setupFields()
    {
        FieldInfo field = null;
        field = new FieldInfo(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Deleted", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, "CalendarEntryTypeID", 10, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, "StartDateTime", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "EndDateTime", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "Description", 60, null, null);
        field = new FieldInfo(this, "CalendarCategoryID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Hidden", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "AnnivMasterID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        super.setupKeys();
    }

}
