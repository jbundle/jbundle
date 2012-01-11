/**
 * @(#)LogicFile.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class LogicFile extends org.jbundle.thin.base.db.FieldList
{

    public LogicFile()
    {
        super();
    }
    public LogicFile(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String LOGIC_FILE_FILE = "LogicFile";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? LogicFile.LOGIC_FILE_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, "Sequence", 10, null, new Integer(1000));
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "MethodName", 40, null, null);
        field = new FieldInfo(this, "LogicDescription", 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "MethodReturns", 255, null, null);
        field = new FieldInfo(this, "MethodInterface", 255, null, null);
        field = new FieldInfo(this, "MethodClassName", 40, null, null);
        field = new FieldInfo(this, "LogicSource", 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "LogicThrows", 255, null, null);
        field = new FieldInfo(this, "Protection", 60, null, null);
        field = new FieldInfo(this, "CopyFrom", 40, null, null);
        field = new FieldInfo(this, "IncludeScope", 10, null, new Integer(0x001));
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
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "MethodClassName");
        keyArea.addKeyField("MethodClassName", Constants.ASCENDING);
        keyArea.addKeyField("MethodName", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "Sequence");
        keyArea.addKeyField("MethodClassName", Constants.ASCENDING);
        keyArea.addKeyField("Sequence", Constants.ASCENDING);
        keyArea.addKeyField("MethodName", Constants.ASCENDING);
    }

}
