/**
 * @(#)ClassResource.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.app.program.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.app.program.db.*;

public class ClassResource extends FieldList
    implements ClassResourceModel
{
    private static final long serialVersionUID = 1L;


    public ClassResource()
    {
        super();
    }
    public ClassResource(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String CLASS_RESOURCE_FILE = "ClassResource";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? ClassResource.CLASS_RESOURCE_FILE : super.getTableNames(bAddQuotes);
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
        field = new FieldInfo(this, CLASS_NAME, 40, null, null);
        field = new FieldInfo(this, SEQUENCE_NO, 4, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, KEY_NAME, 40, null, null);
        field = new FieldInfo(this, VALUE_NAME, 255, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "ID");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ClassName");
        keyArea.addKeyField("ClassName", Constants.ASCENDING);
        keyArea.addKeyField("SequenceNo", Constants.ASCENDING);
    }

}
