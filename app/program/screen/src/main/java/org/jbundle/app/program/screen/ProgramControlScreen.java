/**
 * @(#)ProgramControlScreen.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.screen;

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
import org.jbundle.app.program.db.*;
import org.jbundle.thin.base.remote.*;

/**
 *  ProgramControlScreen - .
 */
public class ProgramControlScreen extends Screen
{
    /**
     * Default constructor.
     */
    public ProgramControlScreen()
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
    public ProgramControlScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new ProgramControl(this);
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        BaseField field = this.getMainRecord().getField(ProgramControl.PROJECT_NAME);
        field.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, ScreenConstants.DEFAULT_DISPLAY);
        
        SCannedBox button = new SCannedBox(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, ProgramControl.SET_DEFAULT_COMMAND, Constants.SUBMIT, ProgramControl.SET_DEFAULT_COMMAND, null);
        
        field.setHidden(true);
        super.setupSFields();
        field.setHidden(false);
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
        if (ProgramControl.SET_DEFAULT_COMMAND.equalsIgnoreCase(strCommand))
        {
            try {
                Map<String,Object> properties = new HashMap<String,Object>();
                properties.put(DBConstants.SYSTEM_NAME, this.getMainRecord().getField(ProgramControl.PROJECT_NAME).toString());
                Object success = null;
        if ((this.getTask() == null)
            || (this.getTask().getApplication() == null)
                || (this.getTask().getApplication().getRemoteTask(getTask(), null, false) == null))
                    success = this.getMainRecord().doRemoteCommand(strCommand, properties); // Run locally
                else
                    success = this.getMainRecord().handleRemoteCommand(strCommand, properties); // Prefer to run it remotely
                if (success instanceof Boolean)
                    return ((Boolean)success).booleanValue();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (DBException e) {
                e.printStackTrace();
            }        
        }
        return super.doCommand(strCommand, sourceSField, iCommandOptions);
    }

}
