/**
 * @(#)MessageLog.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class MessageLog extends org.jbundle.thin.base.db.FieldList
    implements org.jbundle.model.main.msg.db.MessageLogModel
{

    public MessageLog()
    {
        super();
    }
    public MessageLog(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String MESSAGE_LOG_FILE = "MessageLog";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? MessageLog.MESSAGE_LOG_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.REMOTE | Constants.USER_DATA | Constants.SERVER_REWRITES | Constants.DONT_LOG_TRX;
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
        field = new FieldInfo(this, "MessageInfoTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MessageTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MessageStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MessageProcessInfoID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ContactTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ContactID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Description", 60, null, null);
        field = new FieldInfo(this, "MessageTime", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "TimeoutSeconds", 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "TimeoutTime", 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, "UserID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ReferenceType", 60, null, null);
        field = new FieldInfo(this, "ReferenceID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "ResponseMessageLogID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MessageHeaderProperties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "MessageInfoProperties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "MessageTransportProperties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "MessageClassName", 128, null, null);
        field = new FieldInfo(this, "MessageHeaderClassName", 128, null, null);
        field = new FieldInfo(this, "MessageDataClassName", 128, null, null);
        field = new FieldInfo(this, "ExternalMessageClassName", 128, null, null);
        field = new FieldInfo(this, "MessageQueueName", 60, null, null);
        field = new FieldInfo(this, "MessageQueueType", 60, null, null);
        field = new FieldInfo(this, "MessageDataType", 30, null, null);
        field = new FieldInfo(this, "XMLMessageData", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "MessageData", 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "ErrorText", 127, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ReferenceID");
        keyArea.addKeyField("ReferenceID", Constants.ASCENDING);
        keyArea.addKeyField("MessageTime", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ContactTypeID");
        keyArea.addKeyField("ContactTypeID", Constants.ASCENDING);
        keyArea.addKeyField("ContactID", Constants.ASCENDING);
        keyArea.addKeyField("MessageTime", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "MessageTime");
        keyArea.addKeyField("MessageTime", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "Timeout");
        keyArea.addKeyField("MessageStatusID", Constants.ASCENDING);
        keyArea.addKeyField("TimeoutTime", Constants.ASCENDING);
    }

}
