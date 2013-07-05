/**
 * @(#)AccessScreen.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.access;

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
import java.net.*;

/**
 *  AccessScreen - This is a generalized file maintenance screen for editing and viewing all fields in a record.
 */
public class AccessScreen extends Screen
{
    /**
     * Default constructor.
     */
    public AccessScreen()
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
    public AccessScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Get the screen display title.
     */
    public String getTitle()
    {
        return "This is a generalized file maintenance screen for editing and viewing all fields in a record";
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        String strRecord = this.getProperty(DBParams.RECORD);
        String strPackage = this.getProperty("package");
        if ((strRecord == null) || (strPackage == null))
            return null;
        if (strPackage.startsWith("."))
            strPackage = DBConstants.ROOT_PACKAGE.substring(0, DBConstants.ROOT_PACKAGE.length() - 1) + strPackage;   // Never
        String strRecordClassName = strPackage + '.' + strRecord;
        String oldBaseTableProperty = this.getTask().getApplication().getProperty(DBConstants.BASE_TABLE_ONLY);
        this.getTask().getApplication().setProperty(DBConstants.BASE_TABLE_ONLY, DBConstants.TRUE);
        Record record = Record.makeRecordFromClassName(strRecordClassName, this);
        record.setEnableListeners(false);
        record.setEnableFieldListeners(false);
        // Restore properties
        this.getTask().getApplication().setProperty(DBConstants.BASE_TABLE_ONLY, oldBaseTableProperty);   // Make sure system record owner property is reset also
        return record;
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.addMainKeyBehavior();
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        Record record = this.getMainRecord();
        BaseField field = record.getField(DBConstants.MAIN_FIELD);
        if (field == record.getCounterField())
            field.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, ScreenConstants.DEFAULT_DISPLAY);
        super.setupSFields();
    }
    /**
     * Add button(s) to the toolbar.
     */
    public void addToolbarButtons(ToolScreen toolScreen)
    {
        new SCannedBox(toolScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), toolScreen, null, ScreenConstants.DEFAULT_DISPLAY, null, "Delete All", MenuConstants.DELETE, "Delete All", null);
    }
    /**
     * Process the command.
     * <br />Step 1 - Process the command if possible and return true if processed.
     * <br />Step 2 - If I can't process, pass to all children (with me as the source).
     * <br />Step 3 - If children didn't process, pass to parent (with me as the source).
     * <br />Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        if ((strCommand.equalsIgnoreCase(MenuConstants.FORM))
            || (strCommand.equalsIgnoreCase(MenuConstants.FORMLINK)))
        {
            String strHandle = null;
            if (strCommand.equalsIgnoreCase(MenuConstants.FORMLINK))
            {
                try   {
                    strHandle = this.getMainRecord().getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                } catch (DBException ex)    {
                    strHandle = null; // Ignore error - just go to form
                }
            }
            strCommand = Utility.addURLParam(null, DBParams.SCREEN, Constants.ROOT_PACKAGE + AccessScreen.class.getName());    // Screen class
            strCommand = Utility.addURLParam(strCommand, DBParams.RECORD, this.getProperty(DBParams.RECORD));
            strCommand = Utility.addURLParam(strCommand, "package", this.getProperty("package"));
            if (strHandle != null)
                strCommand = Utility.addURLParam(strCommand, DBConstants.STRING_OBJECT_ID_HANDLE, strHandle);
        }
        else if ("Delete all".equalsIgnoreCase(strCommand))
        {
            Record record = this.getMainRecord();
            record.close();
            try {
                while (record.hasNext())
                {
                    Record rec = record.next();
                    rec.edit();
                    rec.remove();
                }
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }
        return super.doCommand(strCommand, sourceSField, iCommandOptions);  // This will send the command to my parent
    }
    /**
     * Get the command string to restore screen.
     */
    public String getScreenURL()
    {
        String strCommand = super.getScreenURL();
        strCommand = Utility.addURLParam(strCommand, DBParams.RECORD, this.getProperty(DBParams.RECORD));
        strCommand = Utility.addURLParam(strCommand, "package", this.getProperty("package"));
        return strCommand;
    }

}
