/**
 * @(#)MessageLog.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import org.jbundle.model.message.*;
import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.msg.db.*;

public class MessageLog extends FieldList
    implements MessageLogModel
{
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, MESSAGE_INFO_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, MESSAGE_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, MESSAGE_STATUS_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, MESSAGE_TRANSPORT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, MESSAGE_PROCESS_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, CONTACT_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, CONTACT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, DESCRIPTION, 60, null, null);
        field = new FieldInfo(this, MESSAGE_TIME, 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, TIMEOUT_SECONDS, 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, TIMEOUT_TIME, 25, null, null);
        field.setDataClass(Date.class);
        field = new FieldInfo(this, USER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, REFERENCE_TYPE, 60, null, null);
        field = new FieldInfo(this, REFERENCE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, RESPONSE_MESSAGE_LOG_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, MESSAGE_HEADER_PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, MESSAGE_INFO_PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, MESSAGE_TRANSPORT_PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, MESSAGE_CLASS_NAME, 128, null, null);
        field = new FieldInfo(this, MESSAGE_HEADER_CLASS_NAME, 128, null, null);
        field = new FieldInfo(this, MESSAGE_DATA_CLASS_NAME, 128, null, null);
        field = new FieldInfo(this, EXTERNAL_MESSAGE_CLASS_NAME, 128, null, null);
        field = new FieldInfo(this, MESSAGE_QUEUE_NAME, 60, null, null);
        field = new FieldInfo(this, MESSAGE_QUEUE_TYPE, 60, null, null);
        field = new FieldInfo(this, MESSAGE_DATA_TYPE, 30, null, null);
        field = new FieldInfo(this, XML_MESSAGE_DATA, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, MESSAGE_DATA, 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, ERROR_TEXT, 127, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
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
    /**
     * CreateMessage Method.
     */
    public Message createMessage(String strTrxID)
    {
        return null;    // No impl in thin
    }
    /**
     * GetProperty Method.
     */
    public String getProperty(String strKey)
    {
        return null;    // No thin impl.
    }
    /**
     * GetMessageLog Method.
     */
    public MessageLogModel getMessageLog(String ID)
    {
        return null;    // Not used in thin impl
    }

}
