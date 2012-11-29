/**
 * @(#)MessageProcessInfo.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import org.jbundle.model.message.*;
import org.jbundle.model.db.*;
import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.msg.db.*;

public class MessageProcessInfo extends FieldList
    implements MessageProcessInfoModel
{
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, CODE, 30, null, null);
        field = new FieldInfo(this, DESCRIPTION, 50, null, null);
        field = new FieldInfo(this, QUEUE_NAME_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, REPLY_MESSAGE_PROCESS_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, LOCAL_MESSAGE_PROCESS_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, MESSAGE_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, MESSAGE_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, PROCESS_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, PROCESSOR_CLASS, 127, null, null);
        field = new FieldInfo(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, DEFAULT_MESSAGE_TRANSPORT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, INITIAL_MESSAGE_STATUS_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, ID_KEY);
        keyArea.addKeyField(ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, MESSAGE_INFO_ID_KEY);
        keyArea.addKeyField(MESSAGE_INFO_ID, Constants.ASCENDING);
        keyArea.addKeyField(MESSAGE_TYPE_ID, Constants.ASCENDING);
        keyArea.addKeyField(PROCESS_TYPE_ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, DESCRIPTION_KEY);
        keyArea.addKeyField(DESCRIPTION, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, CODE_KEY);
        keyArea.addKeyField(CODE, Constants.ASCENDING);
    }
    /**
     * GetMessageProcessInfo Method.
     */
    public MessageProcessInfoModel getMessageProcessInfo(String strMessageKey)
    {
        return null;    // Not impl in thin
    }
    /**
     * GetMessageProcessInfo Method.
     */
    public MessageProcessInfoModel getMessageProcessInfo(String strMessageInfoType, String strContactType, String strRequestType, String strMessageProcessType, String strProcessType)
    {
        return null;    // No thin impl
    }
    /**
     * SetupMessageHeaderFromCode Method.
     */
    public boolean setupMessageHeaderFromCode(Message trxMessage, String strMessageCode, String strVersion)
    {
        return false; // TODO - Add thin impl
    }
    /**
     * CreateReplyMessage Method.
     */
    public Message createReplyMessage(Message message)
    {
        return null;    // TODO add thin impl
    }

}
