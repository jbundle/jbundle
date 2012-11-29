/**
 * @(#)MessageInfo.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.msg.db.*;

public class MessageInfo extends FieldList
    implements MessageInfoModel
{
    private static final long serialVersionUID = 1L;


    public MessageInfo()
    {
        super();
    }
    public MessageInfo(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String MESSAGE_INFO_FILE = "MessageInfo";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? MessageInfo.MESSAGE_INFO_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, DESCRIPTION, 50, null, null);
        field = new FieldInfo(this, CODE, 30, null, null);
        field = new FieldInfo(this, MESSAGE_CLASS, 127, null, null);
        field = new FieldInfo(this, MESSAGE_PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, MESSAGE_INFO_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, new Integer(1));
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, REVERSE_MESSAGE_INFO_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, CONTACT_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, REQUEST_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
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
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, DESCRIPTION_KEY);
        keyArea.addKeyField(DESCRIPTION, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.SECONDARY_KEY, CODE_KEY);
        keyArea.addKeyField(CODE, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, MESSAGE_INFO_TYPE_ID_KEY);
        keyArea.addKeyField(MESSAGE_INFO_TYPE_ID, Constants.ASCENDING);
        keyArea.addKeyField(CONTACT_TYPE_ID, Constants.ASCENDING);
        keyArea.addKeyField(REQUEST_TYPE_ID, Constants.ASCENDING);
    }

}
