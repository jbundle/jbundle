/**
 * @(#)MessageDetail.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.msg.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class MessageDetail extends org.jbundle.thin.main.db.PropertiesRecord
    implements org.jbundle.model.main.msg.db.MessageDetailModel
{

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
        field = new FieldInfo(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Deleted", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "ContactTypeID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "PersonID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MessageProcessInfoID", 60, null, null);
        field.setDataClass(Integer.class);
        //field = new FieldInfo(this, "DestinationSite", 127, null, null);
        //field = new FieldInfo(this, "DestinationPath", 127, null, null);
        //field = new FieldInfo(this, "ReturnSite", 127, null, null);
        //field = new FieldInfo(this, "ReturnPath", 127, null, null);
        //field = new FieldInfo(this, "XSLTDocument", 127, null, null);
        //field = new FieldInfo(this, "DefaultMessageVersionID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //field.setDataClass(Integer.class);
        field = new FieldInfo(this, "DefaultMessageTransportID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        //field = new FieldInfo(this, "InitialManualTransportStatusID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //field.setDataClass(Integer.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ContactTypeID");
        keyArea.addKeyField("ContactTypeID", Constants.ASCENDING);
        keyArea.addKeyField("PersonID", Constants.ASCENDING);
        keyArea.addKeyField("MessageProcessInfoID", Constants.ASCENDING);
        keyArea.addKeyField("MessageTransportID", Constants.ASCENDING);
    }

}
