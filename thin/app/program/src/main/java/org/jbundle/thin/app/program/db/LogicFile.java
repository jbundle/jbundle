/**
 * @(#)LogicFile.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.program.db.*;

public class LogicFile extends FieldList
    implements LogicFileModel
{
    private static final long serialVersionUID = 1L;


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
        field = new FieldInfo(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, DELETED, 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, SEQUENCE, 10, null, new Integer(1000));
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, METHOD_NAME, 40, null, null);
        field = new FieldInfo(this, LOGIC_DESCRIPTION, 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, METHOD_RETURNS, 255, null, null);
        field = new FieldInfo(this, METHOD_INTERFACE, 255, null, null);
        field = new FieldInfo(this, METHOD_CLASS_NAME, 40, null, null);
        field = new FieldInfo(this, LOGIC_SOURCE, 32000, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, LOGIC_THROWS, 255, null, null);
        field = new FieldInfo(this, PROTECTION, 60, null, null);
        field = new FieldInfo(this, COPY_FROM, 40, null, null);
        field = new FieldInfo(this, INCLUDE_SCOPE, 10, null, new Integer(0x001));
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
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, METHOD_CLASS_NAME_KEY);
        keyArea.addKeyField(METHOD_CLASS_NAME, Constants.ASCENDING);
        keyArea.addKeyField(METHOD_NAME, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, SEQUENCE_KEY);
        keyArea.addKeyField(METHOD_CLASS_NAME, Constants.ASCENDING);
        keyArea.addKeyField(SEQUENCE, Constants.ASCENDING);
        keyArea.addKeyField(METHOD_NAME, Constants.ASCENDING);
    }

}
