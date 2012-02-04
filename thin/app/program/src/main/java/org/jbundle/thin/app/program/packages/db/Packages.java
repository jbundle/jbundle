/**
 * @(#)Packages.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.packages.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.thin.main.db.*;
import org.jbundle.model.app.program.packages.db.*;

public class Packages extends Folder
    implements PackagesModel
{
    private static final long serialVersionUID = 1L;


    public Packages()
    {
        super();
    }
    public Packages(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String PACKAGES_FILE = "Packages";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Packages.PACKAGES_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, NAME, 40, null, null);
        field = new FieldInfo(this, PARENT_FOLDER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, SEQUENCE, 5, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, COMMENT, 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, CODE, 30, null, null);
        field = new FieldInfo(this, CLASS_PROJECT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, CODE_TYPE, Constants.DEFAULT_FIELD_LENGTH, null, "BASE");
        field = new FieldInfo(this, PART_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, RECURSIVE, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, EXCLUDE, 10, null, null);
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, MANUAL, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, LAST_UPDATED, 25, null, null);
        field.setDataClass(Date.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ParentFolderID");
        keyArea.addKeyField("ParentFolderID", Constants.ASCENDING);
        keyArea.addKeyField("Sequence", Constants.ASCENDING);
        keyArea.addKeyField("Name", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.SECONDARY_KEY, "Code");
        keyArea.addKeyField("Code", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "Name");
        keyArea.addKeyField("ParentFolderID", Constants.ASCENDING);
        keyArea.addKeyField("Name", Constants.ASCENDING);
        keyArea.addKeyField("ClassProjectID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "PartID");
        keyArea.addKeyField("PartID", Constants.ASCENDING);
        keyArea.addKeyField("Sequence", Constants.ASCENDING);
    }

}
