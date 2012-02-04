/**
 * @(#)MessageDetail.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.thin.main.db.*;
import org.jbundle.model.main.msg.db.*;

public class MessageDetail extends PropertiesRecord
    implements MessageDetailModel
{
    private static final long serialVersionUID = 1L;


    public MessageDetail()
    {
        super();
    }
    public MessageDetail(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String MESSAGE_DETAIL_FILE = "MessageDetail";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? MessageDetail.MESSAGE_DETAIL_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, CONTACT_TYPE_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, PERSON_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, MESSAGE_TRANSPORT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, MESSAGE_PROCESS_INFO_ID, 60, null, null);
        field.setDataClass(Integer.class);
        //field = new FieldInfo(this, DESTINATION_SITE, 127, null, null);
        //field = new FieldInfo(this, DESTINATION_PATH, 127, null, null);
        //field = new FieldInfo(this, RETURN_SITE, 127, null, null);
        //field = new FieldInfo(this, RETURN_PATH, 127, null, null);
        //field = new FieldInfo(this, XSLT_DOCUMENT, 127, null, null);
        //field = new FieldInfo(this, DEFAULT_MESSAGE_VERSION_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //field.setDataClass(Integer.class);
        field = new FieldInfo(this, DEFAULT_MESSAGE_TRANSPORT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        //field = new FieldInfo(this, INITIAL_MANUAL_TRANSPORT_STATUS_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //field.setDataClass(Integer.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ContactTypeID");
        keyArea.addKeyField("ContactTypeID", Constants.ASCENDING);
        keyArea.addKeyField("PersonID", Constants.ASCENDING);
        keyArea.addKeyField("MessageProcessInfoID", Constants.ASCENDING);
        keyArea.addKeyField("MessageTransportID", Constants.ASCENDING);
    }

}
