/**
 * @(#)MenusScreen.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.screen;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.main.db.*;

/**
 *  MenusScreen - .
 */
public class MenusScreen extends FolderScreen
{
    protected Map<String,Object> oldProperties = null;
    /**
     * Default constructor.
     */
    public MenusScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param properties Addition properties to pass to the screen.
     */
    public MenusScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        oldProperties = null;
        String databaseName = parentScreen.getProperty("main" + BaseDatabase.DBSHARED_PARAM_SUFFIX);
        if ((databaseName == null) || (!databaseName.equalsIgnoreCase(Utility.addToPath("main", Utility.getSystemSuffix(parentScreen.getProperty(DBConstants.SYSTEM_NAME), parentScreen.getProperty(DBConstants.DEFAULT_SYSTEM_NAME)), BaseDatabase.DB_NAME_SEPARATOR))))
        {
            Task task = parentScreen.getTask();
            oldProperties = Utility.copyAppProperties(null, task.getProperties());
            Map<String,Object> prop = Utility.copyAppProperties(null, task.getApplication().getProperties());
            prop = Utility.copyAppProperties(prop, task.getProperties());
            BaseDatabase.addDBProperties(prop, parentScreen, null);
            prop.put("main" + BaseDatabase.DBSHARED_PARAM_SUFFIX, Utility.addToPath("main", Utility.getSystemSuffix(parentScreen.getProperty(DBConstants.SYSTEM_NAME), parentScreen.getProperty(DBConstants.DEFAULT_SYSTEM_NAME)), BaseDatabase.DB_NAME_SEPARATOR));
            prop.put(DBConstants.MODE, BaseDatabase.RUN_MODE);
            prop.put(SQLParams.AUTO_COMMIT_PARAM, DBConstants.FALSE);
            if (record != null)
                if (record.getTable().getDatabase().getDatabaseOwner() != null)
            {
                record.free();
                record = null;
            }
            parentScreen.setProperties(prop);
        }
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
        this.getTask().setProperties(oldProperties);
    }
    /**
     * MenusScreen Method.
     */
    public MenusScreen(Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        this();
        this.init(recHeader, record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * GetDatabaseOwner Method.
     */
    public DatabaseOwner getDatabaseOwner()
    {
        return this;    // I'm the database owner
    }
    /**
     * Free Method.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new Menus(this);
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        this.getRecord(Menus.MENUS_FILE).getField(Menus.CODE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.NAME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.COMMENT).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.SEQUENCE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.TYPE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.AUTO_DESC).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.PROGRAM).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.PARAMS).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.ICON_RESOURCE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.KEYWORDS).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.XML_DATA).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.MENUS_HELP).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
