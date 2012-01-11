/**
 * @(#)MessageProcessInfo.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class MessageProcessInfo extends org.jbundle.thin.base.db.FieldList
    implements org.jbundle.model.main.msg.db.MessageProcessInfoModel
{

    public MessageProcessInfo()
    {
        super();
    }
    public MessageProcessInfo(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String MESSAGE_PROCESS_INFO_FILE = "MessageProcessInfo";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? MessageProcessInfo.MESSAGE_PROCESS_INFO_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, "Description", 50, null, null);
        field = new FieldInfo(this, "QueueNameID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ReplyMessageProcessInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "LocalMessageProcessInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MessageInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MessageTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ProcessTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ProcessorClass", 127, null, null);
        field = new FieldInfo(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "DefaultMessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "InitialMessageStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
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
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "MessageInfoID");
        keyArea.addKeyField("MessageInfoID", Constants.ASCENDING);
        keyArea.addKeyField("MessageTypeID", Constants.ASCENDING);
        keyArea.addKeyField("ProcessTypeID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "Description");
        keyArea.addKeyField("Description", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "Code");
        keyArea.addKeyField("Code", Constants.ASCENDING);
    }

}
