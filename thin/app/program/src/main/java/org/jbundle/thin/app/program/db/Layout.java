/**
 * @(#)Layout.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.thin.main.db.*;
import org.jbundle.model.app.program.db.*;

public class Layout extends Folder
    implements LayoutModel
{

    public Layout()
    {
        super();
    }
    public Layout(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String LAYOUT_FILE = "Layout";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Layout.LAYOUT_FILE : super.getTableNames(bAddQuotes);
    }
    /**
     *  Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "program";
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
        field = new FieldInfo(this, "Name", 50, null, null);
        field = new FieldInfo(this, "ParentFolderID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Sequence", 5, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "Comment", 255, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "Code", 30, null, null);
        field = new FieldInfo(this, "Type", 50, null, null);
        field = new FieldInfo(this, "FieldValue", 255, null, null);
        field = new FieldInfo(this, "ReturnsValue", 50, null, null);
        field = new FieldInfo(this, "Max", 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "System", 30, null, null);
        field = new FieldInfo(this, "Comments", 10, null, null);
        field.setDataClass(Boolean.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ParentFolderID");
        keyArea.addKeyField("ParentFolderID", Constants.ASCENDING);
        keyArea.addKeyField("Sequence", Constants.ASCENDING);
        keyArea.addKeyField("ID", Constants.ASCENDING);
    }

}
