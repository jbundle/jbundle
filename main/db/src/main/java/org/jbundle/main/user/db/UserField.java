/**
 * @(#)UserField.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.db;

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
import org.jbundle.thin.base.screen.*;
import org.jbundle.main.db.*;

/**
 *  UserField - Popup "All" users.
 */
public class UserField extends ReferenceField
{
    /**
     * Default constructor.
     */
    public UserField()
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
    public UserField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
        if (this.getRecord() instanceof Person)
            this.addListener(new UserContactHandler(null));
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
        Record record = this.makeReferenceRecord();
        return this.setupTableLookup(itsLocation, targetScreen, converter, iDisplayFieldDesc, record, -1, record.getField(UserInfo.USER_NAME), true, true);
    }
    /**
     * Get the current User's ID.
     */
    public int getUserID()
    {
        int iUserID = -1;
        String strUserID = DBConstants.BLANK;
        if (this.getRecord().getRecordOwner() != null)
            if (this.getRecord().getRecordOwner().getTask() != null)
                if (this.getRecord().getRecordOwner().getTask().getApplication() != null)
            strUserID = ((BaseApplication)this.getRecord().getRecordOwner().getTask().getApplication()).getUserID();
        try   {
            iUserID = Integer.parseInt(strUserID);
            if (iUserID == 0)
                iUserID = -1;
        } catch (NumberFormatException e) {
            iUserID = -1;
        }
        return iUserID;
    }

}
