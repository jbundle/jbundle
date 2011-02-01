/**
 *  @(#)FileHdr.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class FileHdr extends FieldList
{

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
        field = new FieldInfo(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Deleted", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, "FileName", 40, null, null);
        field = new FieldInfo(this, "FileDesc", 40, null, null);
        field = new FieldInfo(this, "FileMainFilename", 40, null, null);
        field = new FieldInfo(this, "Type", 60, null, null);
        field = new FieldInfo(this, "FileNotes", 9999, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "DatabaseName", 20, null, null);
        field = new FieldInfo(this, "FileRecCalled", 40, null, null);
        field = new FieldInfo(this, "DisplayClass", 40, null, null);
        field = new FieldInfo(this, "MaintClass", 40, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "FileName");
        keyArea.addKeyField("FileName", Constants.ASCENDING);
    }

}
