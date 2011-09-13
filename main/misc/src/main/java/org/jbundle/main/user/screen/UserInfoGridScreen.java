/**
 * @(#)UserInfoGridScreen.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.screen;

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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.main.user.db.*;

/**
 *  UserInfoGridScreen - .
 */
public class UserInfoGridScreen extends GridScreen
{
    protected Record m_recHeader = null;
    /**
     * Default constructor.
     */
    public UserInfoGridScreen()
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
    public UserInfoGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        m_recHeader = null;
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * UserInfoGridScreen Method.
     */
    public UserInfoGridScreen(Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        this();
        this.init(recHeader, record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        m_recHeader = null;
        m_recHeader = recHeader;
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new UserInfo(this);
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new UserScreenRecord(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        
        Record recUserInfo = this.getMainRecord();
        recUserInfo.setKeyArea(UserInfo.kUserNameKey);
        
        if (m_recHeader != null)
            ((ReferenceField)this.getScreenRecord().getField(UserScreenRecord.kUserGroupID)).setReference(m_recHeader);
        
        recUserInfo.addListener(new ExtractRangeFilter(UserInfo.kUserName, this.getScreenRecord().getField(UserScreenRecord.kNameSort)));
        recUserInfo.addListener(new CompareFileFilter(UserInfo.kUserGroupID, this.getScreenRecord().getField(UserScreenRecord.kUserGroupID), "=", null, true));
        
        this.getScreenRecord().getField(UserScreenRecord.kNameSort).addListener(new FieldReSelectHandler(this));
        this.getScreenRecord().getField(UserScreenRecord.kUserGroupID).addListener(new FieldReSelectHandler(this));
        
        this.getMainRecord(). addListener(new SetupNewUserHandler(null));
    }
    /**
     * Add button(s) to the toolbar.
     */
    public void addToolbarButtons(ToolScreen toolScreen)
    {
        toolScreen.getScreenRecord().getField(UserScreenRecord.kNameSort).setupDefaultView(toolScreen.getNextLocation(ScreenConstants.RIGHT_WITH_DESC, ScreenConstants.SET_ANCHOR), toolScreen, ScreenConstants.DEFAULT_DISPLAY);
        toolScreen.getScreenRecord().getField(UserScreenRecord.kUserGroupID).setupDefaultView(toolScreen.getNextLocation(ScreenConstants.RIGHT_WITH_DESC, ScreenConstants.SET_ANCHOR), toolScreen, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(UserInfo.kUserInfoFile).getField(UserInfo.kUserName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
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
        if ((MenuConstants.FORM.equalsIgnoreCase(strCommand))
            || (MenuConstants.FORMLINK.equalsIgnoreCase(strCommand)))
        {
            return this.handleCommand(UserInfo.VERBOSE_MAINT_SCREEN, sourceSField, iCommandOptions);
        }
        return super.doCommand(strCommand, sourceSField, iCommandOptions);
    }

}
