/**
 * @(#)QueueName.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class QueueName extends org.jbundle.thin.base.db.FieldList
    implements org.jbundle.model.main.msg.db.QueueNameModel
{

    public QueueName()
    {
        super();
    }
    public QueueName(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String QUEUE_NAME_FILE = "QueueName";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? QueueName.QUEUE_NAME_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.TABLE;
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
        field = new FieldInfo(this, "Name", 30, null, null);
        field = new FieldInfo(this, "ExternalQueueName", 60, null, null);
        field = new FieldInfo(this, "QueueType", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, "PrivateQueue", 10, null, null);
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, "ReverseQueueNameID", Constants.DEFAULT_FIELD_LENGTH, null, null);
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
        keyArea = new KeyAreaInfo(this, Constants.SECONDARY_KEY, "Name");
        keyArea.addKeyField("Name", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "Code");
        keyArea.addKeyField("Code", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ExternalQueueName");
        keyArea.addKeyField("ExternalQueueName", Constants.ASCENDING);
    }

}
