/**
 * @(#)Menus.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.thin.main.db;

import java.util.*;
import org.jbundle.thin.base.util.*;

import org.jbundle.thin.base.db.*;

public class Menus extends org.jbundle.thin.main.db.Folder
    implements org.jbundle.model.main.db.MenusModel
{

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
        field = new FieldInfo(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field.setHidden(true);
        field = new FieldInfo(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Date.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Deleted", 10, null, new Boolean(false));
        field.setDataClass(Boolean.class);
        field.setHidden(true);
        field = new FieldInfo(this, "Name", 50, null, null);
        field = new FieldInfo(this, "ParentFolderID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Integer.class);
        field = new FieldInfo(this, "Sequence", 5, null, new Short((short)0));
        field.setDataClass(Short.class);
        field = new FieldInfo(this, "Comment", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "Code", 30, null, null);
        field = new FieldInfo(this, "Type", 10, null, null);
        field = new FieldInfo(this, "AutoDesc", 10, null, new Boolean(true));
        field.setDataClass(Boolean.class);
        field = new FieldInfo(this, "Program", 255, null, null);
        field = new FieldInfo(this, "Params", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "IconResource", 255, null, null);
        field = new FieldInfo(this, "Keywords", 50, null, null);
        field = new FieldInfo(this, "XmlData", Constants.DEFAULT_FIELD_LENGTH, null, null);
        field.setDataClass(Object.class);
        field = new FieldInfo(this, "MenusHelp", Constants.DEFAULT_FIELD_LENGTH, null, null);
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
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "ParentFolderID");
        keyArea.addKeyField("ParentFolderID", Constants.ASCENDING);
        keyArea.addKeyField("Sequence", Constants.ASCENDING);
        keyArea.addKeyField("Type", Constants.ASCENDING);
        keyArea.addKeyField("Name", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.SECONDARY_KEY, "Code");
        keyArea.addKeyField("Code", Constants.ASCENDING);
        keyArea = new KeyAreaInfo(this, Constants.NOT_UNIQUE, "Type");
        keyArea.addKeyField("Type", Constants.ASCENDING);
        keyArea.addKeyField("Program", Constants.ASCENDING);
    }
    @Override
    public String getLink() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getSubMenuXML() {
        // TODO Auto-generated method stub
        return null;
    }

}
