/**
 * @(#)Part.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.packages.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class Part extends org.jbundle.thin.base.db.FieldList
    implements org.jbundle.model.app.program.packages.db.PartModel
{

    public Part()
    {
        super();
    }
    public Part(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String PART_FILE = "Part";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Part.PART_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, "Description", 60, null, null);
        field = new FieldInfo(this, "Sequence", 10, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Kind", 10, null, null);
        field = new FieldInfo(this, "PartType", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, "JnlpFileID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Path", 128, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "Description");
        keyArea.addKeyField("Description", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "JnlpFileID");
        keyArea.addKeyField("JnlpFileID", Constants.ASCENDING);
        keyArea.addKeyField("Sequence", Constants.ASCENDING);
        keyArea.addKeyField("Description", Constants.ASCENDING);
    }

}
