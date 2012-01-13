/**
 * @(#)PropertiesInput.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.properties.db;

import org.jbundle.model.db.*;
import java.util.*;
import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.model.main.properties.db.*;

public class PropertiesInput extends FieldList
    implements PropertiesInputModel
{

    public PropertiesInput()
    {
        super();
    }
    public PropertiesInput(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String PROPERTIES_INPUT_FILE = "PropertiesInput";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? PropertiesInput.PROPERTIES_INPUT_FILE : super.getTableNames(bAddQuotes);
    }
    /**
     *  Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "main";
    }
    /**
     *  Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return Constants.UNSHAREABLE_MEMORY;
    }
    /**
    * Set up the screen input fields.
    */
    public void setupFields()
    {
        FieldInfo field = null;
        field = new FieldInfo(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Key", 128, null, null);
        field = new FieldInfo(this, "Value", 255, null, null);
        field = new FieldInfo(this, "Comment", 255, null, null);
    }
    /**
    * Set up the key areas.
    */
    public void setupKeys()
    {
        KeyAreaInfo keyArea = null;
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "PrimaryKey");
        keyArea.addKeyField("ID", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.UNIQUE, "Key");
        keyArea.addKeyField("Key", Constants.ASCENDING);
    }
    /**
     * SetPropertiesField Method.
     */
    public void setPropertiesField(Field fldProperties)
    {
        // TODO - Finish the thin impl
    }
    /**
     * LoadFieldProperties Method.
     */
    public void loadFieldProperties(Field fldProperties)
    {
        // not implemented in thin
    }
    /**
     * StartEditor Method.
     */
    public ScreenComponent startEditor(Field fldProperties, boolean bAllowAppending, Map<String,Object> mapKeyDescriptions)
    {
        return null; // TODO add thin impl
    }

}
