/**
 * @(#)ClassInfoGridScreen.
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
import org.jbundle.main.screen.*;
import org.jbundle.app.program.script.process.*;
import org.jbundle.app.program.manual.convert.*;
import org.jbundle.thin.base.thread.*;
import org.jbundle.thin.base.screen.*;
import org.jbundle.base.thread.*;

/**
 *  ClassInfoGridScreen - Grid Screen.
 */
public class ClassInfoGridScreen extends DetailGridScreen
{
    /**
     * Default constructor.
     */
    public ClassInfoGridScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?.
     */
    public ClassInfoGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return "Grid Screen";
    }
    /**
     * ClassInfoGridScreen Method.
     */
    public ClassInfoGridScreen(Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        this();
        this.init(recHeader, record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new ClassInfo(this);
    }
    /**
     * OpenHeaderRecord Method.
     */
    public Record openHeaderRecord()
    {
        return new ClassProject(this);
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new ClassInfoScreenRecord(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        
            // Link the screen field to the passed in record
        ((ReferenceField)this.getScreenRecord().getField(ClassInfoScreenRecord.CLASS_PROJECT_ID)).syncReference(this.getHeaderRecord());
        this.getScreenRecord().getField(ClassInfoScreenRecord.CLASS_PROJECT_ID).addListener(new FieldReSelectHandler(this));
        
        this.getMainRecord().addListener(new ExtractRangeFilter(ClassInfo.CLASS_NAME, this.getScreenRecord().getField(ClassInfoScreenRecord.NAME)));
        this.getScreenRecord().getField(ClassInfoScreenRecord.NAME).addListener(new FieldReSelectHandler(this));
        
        this.getMainRecord().addListener(new ExtractRangeFilter(ClassInfo.CLASS_PACKAGE, this.getScreenRecord().getField(ClassInfoScreenRecord.PACKAGE)));
        this.getScreenRecord().getField(ClassInfoScreenRecord.PACKAGE).addListener(new FieldReSelectHandler(this));
    }
    /**
     * If there is a header record, return it, otherwise, return the main record.
     * The header record is the (optional) main record on gridscreens and is sometimes used
     * to enter data in a sub-record when a header is required.
     * @return The header record.
     */
    public Record getHeaderRecord()
    {
        return this.getRecord(ClassProject.CLASS_PROJECT_FILE);
    }
    /**
     * AddSubFileFilter Method.
     */
    public void addSubFileFilter()
    {
        super.addSubFileFilter();
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(ClassInfo.CLASS_INFO_FILE).getField(ClassInfo.CLASS_NAME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(ClassInfo.CLASS_INFO_FILE).getField(ClassInfo.BASE_CLASS_NAME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(ClassInfo.CLASS_INFO_FILE).getField(ClassInfo.CLASS_PROJECT_ID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(ClassInfo.CLASS_INFO_FILE).getField(ClassInfo.CLASS_PACKAGE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Make a sub-screen.
     * @return the new sub-screen.
     */
    public BasePanel makeSubScreen()
    {
        return new ClassProjectHeaderScreen(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
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
        if ((MenuConstants.FORM.equalsIgnoreCase(strCommand)) || (MenuConstants.FORMLINK.equalsIgnoreCase(strCommand)))
            if (this.getMainRecord().getEditMode() == DBConstants.EDIT_ADD)
                if (!this.getMainRecord().getField(ClassInfo.CLASS_PROJECT_ID).isNull())
        {
            strCommand = Utility.addURLParam(null, DBParams.COMMAND, strCommand);
            strCommand = Utility.addURLParam(strCommand, DBParams.RECORD, ClassInfo.class.getName());
            strCommand = Utility.addURLParam(strCommand, DBParams.HEADER_OBJECT_ID, this.getMainRecord().getField(ClassInfo.CLASS_PROJECT_ID).toString());
        }
        return super.doCommand(strCommand, sourceSField, iCommandOptions);
    }

}
