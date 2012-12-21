/**
 * @(#)MenusScreen.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.screen;

import java.awt.*;
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
    protected App oldApp = null;
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
        oldApp = null;
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
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
        String databaseName = this.getProperty("main" + BaseDatabase.DBSHARED_PARAM_SUFFIX);
        if ((databaseName == null) || (!databaseName.equalsIgnoreCase(Utility.addToPath("main", BaseDatabase.getSystemSuffix(this.getProperty(DBConstants.SYSTEM_NAME)), BaseDatabase.DB_NAME_SEPARATOR))))
        {
            Task task = this.getTask();
            oldApp = task.getApplication();
            Map<String,Object> properties = Utility.copyAppProperties(null, oldApp.getProperties());
            
            BaseDatabase.addDBProperties(properties, this, null);
            properties.put("main" + BaseDatabase.DBSHARED_PARAM_SUFFIX, Utility.addToPath("main", BaseDatabase.getSystemSuffix(this.getProperty(DBConstants.SYSTEM_NAME)), BaseDatabase.DB_NAME_SEPARATOR));
            properties.put(DBConstants.MODE, BaseDatabase.RUN_MODE);
        
            Environment env = ((BaseApplication)oldApp).getEnvironment();
            BaseApplication newApp = new MainApplication(env, properties, null);
            oldApp.removeTask(task);
            newApp.addTask(task, null);
            task.setApplication(newApp);
        
            return newApp;
        }
        return super.getDatabaseOwner();
    }
    /**
     * Free Method.
     */
    public void free()
    {
        if (oldApp != null)
        {
            Task task = this.getTask();
            task.getApplication().removeTask(task);
            oldApp.addTask(task, null);
            task.setApplication(oldApp);            
        }
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
