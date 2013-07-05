/**
 * @(#)ProgramControl.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import java.io.*;
import org.jbundle.main.db.*;
import org.jbundle.base.thread.*;
import org.jbundle.thin.base.thread.*;
import org.jbundle.app.program.resource.db.*;
import org.jbundle.model.app.program.db.*;

/**
 *  ProgramControl - Program control.
 */
public class ProgramControl extends ControlRecord
     implements ProgramControlModel
{
    private static final long serialVersionUID = 1L;

    public static final String SET_DEFAULT_COMMAND = "Set as default system";
    /**
     * Default constructor.
     */
    public ProgramControl()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ProgramControl(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(PROGRAM_CONTROL_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "program";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.LOCAL | DBConstants.USER_DATA;
    }
    /**
     * Make a default screen.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & (ScreenConstants.MAINT_MODE & ScreenConstants.DISPLAY_MODE)) == (ScreenConstants.MAINT_MODE & ScreenConstants.DISPLAY_MODE))
            screen = Record.makeNewScreen(MAINT_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else
            screen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);
        return screen;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == 0)
        //{
        //  field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 1)
        //{
        //  field = new RecordChangedField(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 2)
        //{
        //  field = new BooleanField(this, DELETED, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //  field.setHidden(true);
        //}
        if (iFieldSeq == 3)
            field = new StringField(this, PROJECT_NAME, 30, null, null);
        if (iFieldSeq == 4)
        {
            field = new PropertiesStringField(this, BASE_DIRECTORY, 100, null, "${user.home}/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 5)
        {
            field = new StringField(this, SOURCE_DIRECTORY, 100, null, "src/main/java/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 6)
            field = new StringField(this, RESOURCES_DIRECTORY, 100, null, "src/main/resources/");
        if (iFieldSeq == 7)
        {
            field = new StringField(this, CLASS_DIRECTORY, 100, null, "target/classes/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 8)
        {
            field = new PropertiesStringField(this, ARCHIVE_DIRECTORY, 127, null, "${user.dir}/src/main/resources/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 9)
            field = new PropertiesStringField(this, DEV_ARCHIVE_DIRECTORY, 127, null, "${user.dir}/src/main/resources/");
        if (iFieldSeq == 10)
            field = new ResourceTypeField(this, RESOURCE_TYPE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 11)
            field = new ResourceTypeField(this, CLASS_RESOURCE_TYPE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 12)
            field = new StringField(this, PACKAGE_NAME, 40, null, null);
        if (iFieldSeq == 13)
        {
            field = new StringField(this, INTERFACE_PACKAGE, Constants.DEFAULT_FIELD_LENGTH, null, ".model");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 14)
        {
            field = new StringField(this, THIN_PACKAGE, 40, null, ".thin");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 15)
        {
            field = new StringField(this, RESOURCE_PACKAGE, 40, null, ".res");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 16)
            field = new DateTimeField(this, LAST_PACKAGE_UPDATE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 17)
            field = new StringField(this, PACKAGES_BASE_PATH, 100, null, null);
        if (iFieldSeq == 18)
            field = new StringField(this, PACKAGES_PATH, 100, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == 0)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, ID_KEY);
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
        this.getField(ProgramControl.LAST_PACKAGE_UPDATE).setEnabled(false);
        
        this.addListener(new FileListener()
        {
            public void doNewRecord(boolean bDisplayOption)
            {
                super.doNewRecord(bDisplayOption);
                
                PropertiesStringField field = (PropertiesStringField)this.getOwner().getField(ProgramControl.BASE_DIRECTORY);
                field.enableConversion = false;
                if (!field.isModified())
                    if (field.getDefault().equals(field.getData()))
                {
                    try {
                        String home = System.getProperty("user.home");
                        String current = System.getProperty("user.dir");
                        if (!home.equals(current))
                            if (current.startsWith(home))
                        {   // Set the base directory to the current directories' home
                            current = Utility.addToPath((String)field.getDefault(), current.substring(home.length()));
                            int lastPathSeparator = current.lastIndexOf(File.separator);
                            if (lastPathSeparator == -1)
                                lastPathSeparator = current.lastIndexOf('/');
                            if (lastPathSeparator != -1)
                            {
                                ((InitOnceFieldHandler)field.getListener(InitOnceFieldHandler.class)).setFirstTime(true);
                                field.setString(current.substring(0, lastPathSeparator), DBConstants.DISPLAY, DBConstants.INIT_MOVE);
                            }
                        }
                    } catch (SecurityException e) {
                        // Ignore (default is fine)
                    }
                }
                field.enableConversion = true;
            }
        });
    }
    /**
     * Get the base path.
     * Replace all the params first.
     */
    public String getBasePath()
    {
        String basePath = DBConstants.BLANK;
        basePath = this.getField(ProgramControl.BASE_DIRECTORY).toString();
        PropertyOwner propertyOwner = null;
        if (this.getOwner() instanceof PropertyOwner)
            propertyOwner = (PropertyOwner)this.getOwner();
        try {
            basePath = Utility.replaceResources(basePath, null, Utility.propertiesToMap(System.getProperties()), propertyOwner);
        } catch (SecurityException e) {
            basePath = Utility.replaceResources(basePath, null, null, propertyOwner);
        }
        return basePath;
    }
    /**
     * Do a remote command.
     * @param strCommand The command
     * @param properties The properties for the command
     * @return The return value or Boolean.FALSE if not handled.
     */
    public Object doRemoteCommand(String strCommand, Map<String, Object> properties)
    {
        if (SET_DEFAULT_COMMAND.equalsIgnoreCase(strCommand))
            if (properties != null)
                if (properties.get(DBConstants.SYSTEM_NAME) != null)
        {
            if (this.getTask() != null)
            if (this.getTask().getApplication() instanceof BaseApplication)
            if (((BaseApplication)this.getTask().getApplication()).getEnvironment() != null)
            {
                String systemName = properties.get(DBConstants.SYSTEM_NAME).toString();
                Environment env = ((BaseApplication)this.getTask().getApplication()).getEnvironment();
                properties.put(DBConstants.SYSTEM_NAME, "base");
                BaseApplication app = new MainApplication(env, properties, null);
                try {
                    Task task = new AutoTask(app, null, properties);
                    RecordOwner recordOwner = new BaseProcess(task, null, properties);
                    Menus menus = new Menus(recordOwner);
                    menus.setKeyArea(Menus.CODE_KEY);
                    menus.getField(Menus.CODE).setString(ResourceConstants.DEFAULT_RESOURCE);
                    if (menus.seek(null))
                        menus.edit();
                    else
                        menus.addNew();
                    menus.getField(Menus.CODE).setString(ResourceConstants.DEFAULT_RESOURCE);
                    ((PropertiesField)menus.getField(Menus.PARAMS)).setProperty(DBConstants.SYSTEM_NAME, systemName);
                    if (menus.getEditMode() == DBConstants.EDIT_ADD)
                        menus.add();
                    else
                        menus.set();
                } catch (DBException e) {
                    e.printStackTrace();
                } finally {
                    app.free();
                }
                env.getDefaultApplication().getSystemRecordOwner().setProperty(DBConstants.DEFAULT_SYSTEM_NAME, systemName);
                return Boolean.TRUE;    // Success
            }
        }
        return super.doRemoteCommand(strCommand, properties);
    }

}
