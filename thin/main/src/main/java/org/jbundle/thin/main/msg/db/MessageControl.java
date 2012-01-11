/**
 * @(#)MessageControl.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class MessageControl extends org.jbundle.thin.base.db.FieldList
    implements org.jbundle.model.main.msg.db.MessageControlModel
{

    public MessageControl()
    {
        super();
    }
    public MessageControl(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String MESSAGE_CONTROL_FILE = "MessageControl";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? MessageControl.MESSAGE_CONTROL_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.LOCAL | Constants.USER_DATA;
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
        field = new FieldInfo(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "WebServicesServer", 128, null, "/ws");
        field = new FieldInfo(this, "DefaultVersionID", 20, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "BaseNamespace", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, "BaseSchemaLocation", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, "WebMessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
    }

}
