/**
 * @(#)MessageTransport.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import org.jbundle.model.*;
import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.msg.db.*;

public class MessageTransport extends FieldList
    implements MessageTransportModel
{
    private static final long serialVersionUID = 1L;


    public MessageTransport()
    {
        super();
    }
    public MessageTransport(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String MESSAGE_TRANSPORT_FILE = "MessageTransport";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? MessageTransport.MESSAGE_TRANSPORT_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, DESCRIPTION, 30, null, null);
        field = new FieldInfo(this, CODE, 10, null, null);
        field = new FieldInfo(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, MESSAGE_TRANSPORT_TYPE, Constants.DEFAULT_FIELD_LENGTH, null, null);
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
    }
    /**
     * Get the message transport for this type
     * @param messageTransportType
     * @returns The concrete BaseMessageTransport implementation.
     */
    public Object createMessageTransport(String messageTransportType, Task task)
    {
        return null; // Empty implementation
    }

}
