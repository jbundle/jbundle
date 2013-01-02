/**
 * @(#)IssueGridScreen.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.issue.screen;

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
import org.jbundle.app.program.issue.db.*;

/**
 *  IssueGridScreen - .
 */
public class IssueGridScreen extends GridScreen
{
    /**
     * Default constructor.
     */
    public IssueGridScreen()
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
    public IssueGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return new Issue(this);
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new IssueScreenRecord(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        
        Record recIssue = this.getMainRecord();
        SortOrderHandler keyBehavior = new SortOrderHandler(this, true);
        this.getScreenRecord().getField(IssueScreenRecord.KEY_ORDER).setValue(1);
        this.getScreenRecord().getField(IssueScreenRecord.KEY_ORDER).addListener(keyBehavior);
        
        recIssue.addListener(new CompareFileFilter(Issue.PROJECT_ID, this.getScreenRecord().getField(IssueScreenRecord.PROJECT_ID), "=", null, true));
        recIssue.addListener(new CompareFileFilter(Issue.PROJECT_VERSION_ID, this.getScreenRecord().getField(IssueScreenRecord.PROJECT_VERSION_ID), "=", null, true));
        recIssue.addListener(new CompareFileFilter(Issue.ISSUE_TYPE_ID, this.getScreenRecord().getField(IssueScreenRecord.ISSUE_TYPE_ID), "=", null, true));
        recIssue.addListener(new CompareFileFilter(Issue.ISSUE_STATUS_ID, this.getScreenRecord().getField(IssueScreenRecord.ISSUE_STATUS_ID), "=", null, true));
        recIssue.addListener(new CompareFileFilter(Issue.ASSIGNED_USER_ID, this.getScreenRecord().getField(IssueScreenRecord.ASSIGNED_USER_ID), "=", null, true));
        recIssue.addListener(new CompareFileFilter(Issue.ISSUE_PRIORITY_ID, this.getScreenRecord().getField(IssueScreenRecord.ISSUE_PRIORITY_ID), "=", null, true));
        
        this.getScreenRecord().getField(IssueScreenRecord.PROJECT_ID).addListener(new FieldReSelectHandler(this));
        this.getScreenRecord().getField(IssueScreenRecord.PROJECT_VERSION_ID).addListener(new FieldReSelectHandler(this));
        this.getScreenRecord().getField(IssueScreenRecord.ISSUE_TYPE_ID).addListener(new FieldReSelectHandler(this));
        this.getScreenRecord().getField(IssueScreenRecord.ISSUE_STATUS_ID).addListener(new FieldReSelectHandler(this));
        this.getScreenRecord().getField(IssueScreenRecord.ASSIGNED_USER_ID).addListener(new FieldReSelectHandler(this));
        this.getScreenRecord().getField(IssueScreenRecord.ISSUE_PRIORITY_ID).addListener(new FieldReSelectHandler(this));
        this.getScreenRecord().getField(IssueScreenRecord.PROJECT_ID).addListener(new FieldReSelectHandler(this));
        
        this.getScreenRecord().getField(IssueScreenRecord.PROJECT_VERSION_ID).addListener(new RegisterValueHandler(null));
        this.getScreenRecord().getField(IssueScreenRecord.ISSUE_TYPE_ID).addListener(new RegisterValueHandler(null));
        this.getScreenRecord().getField(IssueScreenRecord.ISSUE_STATUS_ID).addListener(new RegisterValueHandler(null));
        this.getScreenRecord().getField(IssueScreenRecord.ASSIGNED_USER_ID).addListener(new RegisterValueHandler(null));
        this.getScreenRecord().getField(IssueScreenRecord.ISSUE_PRIORITY_ID).addListener(new RegisterValueHandler(null));
    }
    /**
     * Add button(s) to the toolbar.
     */
    public void addToolbarButtons(ToolScreen toolScreen)
    {
        new SCannedBox(toolScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), toolScreen, null, ScreenConstants.DEFAULT_DISPLAY, null, MenuConstants.FORMDETAIL, MenuConstants.FORMDETAIL, MenuConstants.FORMDETAIL, null);
        toolScreen.getScreenRecord().getField(IssueScreenRecord.PROJECT_ID).setupDefaultView(toolScreen.getNextLocation(ScreenConstants.RIGHT_WITH_DESC, ScreenConstants.SET_ANCHOR), toolScreen, ScreenConstants.DEFAULT_DISPLAY);
        toolScreen.getScreenRecord().getField(IssueScreenRecord.PROJECT_VERSION_ID).setupDefaultView(toolScreen.getNextLocation(ScreenConstants.NEXT_INPUT_LOCATION, ScreenConstants.SET_ANCHOR), toolScreen, ScreenConstants.DEFAULT_DISPLAY);
        toolScreen.getScreenRecord().getField(IssueScreenRecord.ISSUE_TYPE_ID).setupDefaultView(toolScreen.getNextLocation(ScreenConstants.RIGHT_WITH_DESC, ScreenConstants.SET_ANCHOR), toolScreen, ScreenConstants.DEFAULT_DISPLAY);
        toolScreen.getScreenRecord().getField(IssueScreenRecord.ISSUE_STATUS_ID).setupDefaultView(toolScreen.getNextLocation(ScreenConstants.RIGHT_WITH_DESC, ScreenConstants.SET_ANCHOR), toolScreen, ScreenConstants.DEFAULT_DISPLAY);
        toolScreen.getScreenRecord().getField(IssueScreenRecord.ASSIGNED_USER_ID).setupDefaultView(toolScreen.getNextLocation(ScreenConstants.NEXT_INPUT_LOCATION, ScreenConstants.SET_ANCHOR), toolScreen, ScreenConstants.DEFAULT_DISPLAY);
        toolScreen.getScreenRecord().getField(IssueScreenRecord.ISSUE_PRIORITY_ID).setupDefaultView(toolScreen.getNextLocation(ScreenConstants.RIGHT_WITH_DESC, ScreenConstants.SET_ANCHOR), toolScreen, ScreenConstants.DEFAULT_DISPLAY);
        
        new SCannedBox(toolScreen.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), toolScreen, null, ScreenConstants.DEFAULT_DISPLAY, null, MenuConstants.FORMDETAIL, MenuConstants.FORMDETAIL, MenuConstants.FORMDETAIL, null);
    }
    /**
     * Add the navigation button(s) to the left of the grid row.
     */
    public void addNavButtons()
    {
        new SCannedBox(this.getNextLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, null, MenuConstants.FORMDETAIL, MenuConstants.FORMDETAIL, null);
        super.addNavButtons();  // Next buttons will be "First!"
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(Issue.ISSUE_FILE).getField(Issue.ISSUE_PRIORITY_ID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Issue.ISSUE_FILE).getField(Issue.ISSUE_SEQUENCE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        Converter converter = new FieldLengthConverter(this.getRecord(Issue.ISSUE_FILE).getField(Issue.DESCRIPTION), 50);
        converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Issue.ISSUE_FILE).getField(Issue.PROJECT_ID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Issue.ISSUE_FILE).getField(Issue.ISSUE_STATUS_ID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Issue.ISSUE_FILE).getField(Issue.ISSUE_TYPE_ID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
