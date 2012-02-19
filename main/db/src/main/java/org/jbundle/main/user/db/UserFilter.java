/**
 * @(#)UserFilter.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.db;

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
import org.jbundle.thin.base.screen.*;
import org.jbundle.main.db.*;

/**
 *  UserFilter - .
 */
public class UserFilter extends ReferenceField
{
    /**
     * Default constructor.
     */
    public UserFilter()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public UserFilter(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Get (or make) the current record for this reference.
     */
    public Record makeReferenceRecord(RecordOwner recordOwner)
    {
        return new UserInfo(recordOwner);
    }
    /**
     * Set up the default screen control for this field.
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @param properties Extra properties
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        UserInfo user = (UserInfo)this.makeReferenceRecord();
        user.addListener(new StringSubFileFilter(Integer.toString(UserGroup.RES_USER), user.getField(UserInfo.USER_GROUP_ID), null, null, null, null));
        Converter convName = new FirstMLastConverter(user, null, UserInfo.FIRST_NAME, null, UserInfo.LAST_NAME);
        
        ScreenComponent screenField = this.setupTableLookup(itsLocation, targetScreen, converter, iDisplayFieldDesc, user, UserInfo.USER_NAME_KEY, convName, true, false);
        properties = new HashMap<String,Object>();
        properties.put(ScreenModel.FIELD, this);
        properties.put(ScreenModel.COMMAND, ThinMenuConstants.HOME);
        properties.put(ScreenModel.IMAGE, ThinMenuConstants.HOME);
        createScreenComponent(ScreenModel.CANNED_BOX, targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        /*
        new SCannedBox(targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, ThinMenuConstants.HOME, ScreenConstants.DONT_DISPLAY_FIELD_DESC, this)
        {
            public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
            {
                boolean bHandled = false;
                if (ThinMenuConstants.HOME.equals(strCommand))
                {
                    bHandled = true;
                    String strUserID = ((BaseApplication)getField().getRecord().getRecordOwner().getTask().getApplication()).getUserID();
                    if (strUserID != null)
                        if (strUserID.length() > 0)
                            getField().setString(strUserID, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
                }
                if (!bHandled)
                    bHandled = super.doCommand(strCommand, sourceSField, iCommandOptions);
                return bHandled;
            }
        };
         */        
        return screenField;
    }

}
