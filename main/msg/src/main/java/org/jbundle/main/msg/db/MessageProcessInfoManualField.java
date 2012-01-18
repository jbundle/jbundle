/**
 * @(#)MessageProcessInfoManualField.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.db;

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
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;

/**
 *  MessageProcessInfoManualField - Display only the manual messages on lookup.
Override doCommand in your screen to intercept the LOOKUP_WITH_PARAMS command..
 */
public class MessageProcessInfoManualField extends MessageProcessInfoField
{
    public static final String LOOKUP_WITH_PARAMS = "lookupWithParams";
    /**
     * Default constructor.
     */
    public MessageProcessInfoManualField()
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
    public MessageProcessInfoManualField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
        ScreenComponent screenField = super.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
        
        for (int i = 0; ; i++)
        {
            Object comp = converter.getField().getComponent(i);
            if (comp == null)
                break;
            if (comp instanceof SCannedBox)
            {
                ((SCannedBox)comp).free();
                i--;
            }
        }
        
        Record record = this.makeReferenceRecord();
        properties = new HashMap<String,Object>();
        properties.put(ScreenModel.RECORD, record);
        properties.put(ScreenModel.COMMAND, ThinMenuConstants.LOOKUP);
        properties.put(ScreenModel.IMAGE, ThinMenuConstants.LOOKUP);
        createScreenComponent(ScreenModel.CANNED_BOX, targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        /*
        new SSelectBox(targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, ScreenConstants.DONT_DISPLAY_DESC, record)
        {
            public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
            {
                boolean bHandled = false;
                if (ThinMenuConstants.LOOKUP.equalsIgnoreCase(strCommand))
                    if (this.getParentScreen() != null) // Give the parent screen a shot at it.
                        bHandled = this.getParentScreen().handleCommand(LOOKUP_WITH_PARAMS, sourceSField, iCommandOptions);
                if (!bHandled)
                    bHandled = super.doCommand(strCommand, sourceSField, iCommandOptions);
                return bHandled;
            }
            
        };
        */
        properties = new HashMap<String,Object>();
        properties.put(ScreenModel.FIELD, this);
        properties.put(ScreenModel.COMMAND, ScreenModel.CLEAR);
        properties.put(ScreenModel.IMAGE, ScreenModel.CLEAR);
        createScreenComponent(ScreenModel.CANNED_BOX, targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        //new SCannedBox(targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, SCannedBox.CLEAR, ScreenConstants.DONT_DISPLAY_FIELD_DESC, this);
        
        return screenField;
    }

}
