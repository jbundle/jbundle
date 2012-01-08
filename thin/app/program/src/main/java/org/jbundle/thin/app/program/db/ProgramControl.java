/**
 * @(#)ProgramControl.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class ProgramControl extends FieldList
{

    public ProgramControl()
    {
        super();
    }
    public ProgramControl(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String PROGRAM_CONTROL_FILE = "ProgramControl";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? ProgramControl.PROGRAM_CONTROL_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.LOCAL | Constants.USER_DATA;
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
        field = new FieldInfo(this, "ProjectName", 30, null, null);
        field = new FieldInfo(this, "BaseDirectory", 127, null, "/home/don/workspace/tour/");
        field = new FieldInfo(this, "SourceDirectory", 127, null, "src/main/java/");
        field = new FieldInfo(this, "ClassDirectory", 127, null, "target/classes/");
        field = new FieldInfo(this, "ArchiveDirectory", 127, null, "data/archive/");
        field = new FieldInfo(this, "ResourceType", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, "ClassResourceType", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, "PackageName", 40, null, null);
        field = new FieldInfo(this, "InterfacePackage", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field = new FieldInfo(this, "ThinPackage", 40, null, null);
        field = new FieldInfo(this, "ResourcePackage", 40, null, null);
        field = new FieldInfo(this, "LastPackageUpdate", 25, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "PackagesBasePath", 128, null, null);
        field = new FieldInfo(this, "PackagesPath", 128, null, null);
        field.setDataClass(Object.class);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
    }

}
