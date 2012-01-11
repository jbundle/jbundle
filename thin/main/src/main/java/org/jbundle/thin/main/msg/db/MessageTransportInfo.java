/**
 * @(#)MessageTransportInfo.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class MessageTransportInfo extends org.jbundle.thin.base.db.FieldList
    implements org.jbundle.model.main.msg.db.MessageTransportInfoModel
{

    public MessageTransportInfo()
    {
        super();
    }
    public MessageTransportInfo(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String MESSAGE_TRANSPORT_INFO_FILE = "MessageTransportInfo";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? MessageTransportInfo.MESSAGE_TRANSPORT_INFO_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.REMOTE | Constants.USER_DATA;
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
        field = new FieldInfo(this, "Code", 30, null, null);
        field = new FieldInfo(this, "MessageProcessInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MessageVersionID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Active", 10, null, new Boolean(true));
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, "DefaultTransport", 10, null, null);
        field.setDataClass(Boolean.class);
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
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "MessageProcessInfoID");
        keyArea.addKeyField("MessageProcessInfoID", Constants.ASCENDING);
        keyArea.addKeyField("MessageTransportID", Constants.ASCENDING);
        keyArea.addKeyField("MessageVersionID", Constants.ASCENDING);
    }

}
