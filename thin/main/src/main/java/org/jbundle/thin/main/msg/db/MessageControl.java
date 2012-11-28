/**
 * @(#)MessageControl.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.msg.db.*;

public class MessageControl extends FieldList
    implements MessageControlModel
{
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, WEB_SERVICES_SERVER, 128, null, "/ws");
        field = new FieldInfo(this, DEFAULT_VERSION_ID, 20, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, BASE_NAMESPACE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, BASE_SCHEMA_LOCATION, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, WEB_MESSAGE_TRANSPORT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
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
    }
    /**
     * GetVersionFromSchemaLocation Method.
     */
    public String getVersionFromSchemaLocation(String schemaLocation)
    {
        return null;    // Not impl in thin
    }

}
