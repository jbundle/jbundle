/**
 * @(#)MessageVersion.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.msg.db.*;

public class MessageVersion extends FieldList
    implements MessageVersionModel
{

    public MessageVersion()
    {
        super();
    }
    public MessageVersion(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String MESSAGE_VERSION_FILE = "MessageVersion";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? MessageVersion.MESSAGE_VERSION_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.TABLE | Constants.SHARED_DATA;
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
        field = new FieldInfo(this, "Description", 30, null, null);
        field = new FieldInfo(this, "Code", 20, null, null);
        field = new FieldInfo(this, "Namespace", 128, null, null);
        field = new FieldInfo(this, "SchemaLocation", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, "NumericVersion", 20, null, null);
        field = new FieldInfo(this, "VersionID", 20, null, null);
        field = new FieldInfo(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.SECONDARY_KEY, "Code");
        keyArea.addKeyField("Code", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "Description");
        keyArea.addKeyField("Description", Constants.ASCENDING);
    }

}
