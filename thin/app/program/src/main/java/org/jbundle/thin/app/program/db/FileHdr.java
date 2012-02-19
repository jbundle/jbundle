/**
 * @(#)FileHdr.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.program.db.*;

public class FileHdr extends FieldList
    implements FileHdrModel
{
    private static final long serialVersionUID = 1L;


    public FileHdr()
    {
        super();
    }
    public FileHdr(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String FILE_HDR_FILE = "FileHdr";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? FileHdr.FILE_HDR_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, FILE_NAME, 40, null, null);
        field = new FieldInfo(this, FILE_DESC, 40, null, null);
        field = new FieldInfo(this, FILE_MAIN_FILENAME, 40, null, null);
        field = new FieldInfo(this, TYPE, 60, null, null);
        field = new FieldInfo(this, FILE_NOTES, 9999, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, DATABASE_NAME, 20, null, null);
        field = new FieldInfo(this, FILE_REC_CALLED, 40, null, null);
        field = new FieldInfo(this, DISPLAY_CLASS, 40, null, null);
        field = new FieldInfo(this, MAINT_CLASS, 40, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "FileName");
        keyArea.addKeyField("FileName", Constants.ASCENDING);
    }

}
