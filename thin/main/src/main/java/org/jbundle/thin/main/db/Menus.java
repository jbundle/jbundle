/**
 * @(#)Menus.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

import org.jbundle.thin.main.db.*;
import org.jbundle.model.main.db.*;

public class Menus extends Folder
    implements MenusModel
{
    private static final long serialVersionUID = 1L;


    public Menus()
    {
        super();
    }
    public Menus(Object recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    public static final String MENUS_FILE = "Menus";
    /**
     *  Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Menus.MENUS_FILE : super.getTableNames(bAddQuotes);
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
        return Constants.LOCAL | Constants.SHARED_DATA | Constants.LOCALIZABLE;
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
        field = new FieldInfo(this, NAME, 50, null, null);
        field = new FieldInfo(this, PARENT_FOLDER_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, SEQUENCE, 5, null, new Short((short)0));
        field.setDataClass(Short.class);
        field = new FieldInfo(this, COMMENT, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, CODE, 30, null, null);
        field = new FieldInfo(this, TYPE, 10, null, null);
        field = new FieldInfo(this, AUTO_DESC, 10, null, new Boolean(true));
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, PROGRAM, 255, null, null);
        field = new FieldInfo(this, PARAMS, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, ICON_RESOURCE, 255, null, null);
        field = new FieldInfo(this, KEYWORDS, 50, null, null);
        field = new FieldInfo(this, XML_DATA, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, MENUS_HELP, Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
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
        keyArea.addKeyField(SEQUENCE, Constants.ASCENDING);
        keyArea.addKeyField(TYPE, Constants.ASCENDING);
        keyArea.addKeyField(NAME, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.SECONDARY_KEY, CODE_KEY);
        keyArea.addKeyField(CODE, Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, TYPE_KEY);
        keyArea.addKeyField(TYPE, Constants.ASCENDING);
        keyArea.addKeyField(PROGRAM, Constants.ASCENDING);
    }
    /**
     * GetLink Method.
     */
    public String getLink()
    {
        return null;    // TODO FIx thin impl
    }
    /**
     * GetSubMenuXML Method.
     */
    public String getSubMenuXML()
    {
        return null;    // Add thin impl
    }

}
