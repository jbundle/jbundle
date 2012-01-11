/**
 * @(#)ClassProject.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class ClassProject extends org.jbundle.thin.main.db.Folder
    implements org.jbundle.model.app.program.db.ClassProjectModel
{

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
        field = new FieldInfo(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Deleted", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Name", 40, null, null);
        field = new FieldInfo(this, "ParentFolderID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Sequence", 5, null, null);
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "Comment", 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "Code", 30, null, null);
        field = new FieldInfo(this, "Description", 40, null, null);
        field = new FieldInfo(this, "SystemClasses", 10, null, null);
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, "PackageName", 30, null, null);
        field = new FieldInfo(this, "ProjectPath", 128, null, null);
        field = new FieldInfo(this, "InterfacePackage", 40, null, null);
        field = new FieldInfo(this, "InterfaceProjectPath", 128, null, null);
        field = new FieldInfo(this, "ThinPackage", 40, null, null);
        field = new FieldInfo(this, "ThinProjectPath", 128, null, null);
        field = new FieldInfo(this, "ResourcePackage", 40, null, null);
        field = new FieldInfo(this, "ResProjectPath", 128, null, null);
        field = new FieldInfo(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "ArtifactId", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, "GroupId", Constants.DEFAULT_FIELD_LENGTH, null, null);
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
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "Name");
        keyArea.addKeyField("Name", Constants.ASCENDING);
    }

}
