/**
 * @(#)ClassProject.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.thin.main.db.*;
import org.jbundle.model.app.program.db.*;

public class ClassProject extends Folder
    implements ClassProjectModel
{
    private static final long serialVersionUID = 1L;


    public ClassProject()
    {
        super();
    }
    public ClassProject(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String CLASS_PROJECT_FILE = "ClassProject";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? ClassProject.CLASS_PROJECT_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.REMOTE | Constants.SHARED_DATA | Constants.HIERARCHICAL;
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
        field = new FieldInfo(this, NAME, 100, null, null);
        field = new FieldInfo(this, PARENT_FOLDER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, SEQUENCE, 5, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, COMMENT, 32000, null, null);
        field = new FieldInfo(this, CODE, 30, null, null);
        field = new FieldInfo(this, DESCRIPTION, 100, null, null);
        field = new FieldInfo(this, SYSTEM_CLASSES, 10, null, null);
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, PACKAGE_NAME, 100, null, null);
        field = new FieldInfo(this, PROJECT_PATH, 100, null, null);
        field = new FieldInfo(this, INTERFACE_PACKAGE, 100, null, null);
        field = new FieldInfo(this, INTERFACE_PROJECT_PATH, 100, null, null);
        field = new FieldInfo(this, THIN_PACKAGE, 100, null, null);
        field = new FieldInfo(this, THIN_PROJECT_PATH, 100, null, null);
        field = new FieldInfo(this, RESOURCE_PACKAGE, 100, null, null);
        field = new FieldInfo(this, RES_PROJECT_PATH, 100, null, null);
        field = new FieldInfo(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, ARTIFACT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, GROUP_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, ID_KEY);
        keyArea.addKeyField(ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, PARENT_FOLDER_ID_KEY);
        keyArea.addKeyField(PARENT_FOLDER_ID, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, NAME_KEY);
        keyArea.addKeyField(NAME, Constants.ASCENDING);
    }

}
