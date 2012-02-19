/**
 * @(#)IncludeScopeField.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db;

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
import org.jbundle.model.app.program.db.*;

/**
 *  IncludeScopeField - Include scope.
 */
public class IncludeScopeField extends IntegerField
{
    /**
     * Default constructor.
     */
    public IncludeScopeField()
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
    public IncludeScopeField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
        ScreenComponent screenField = null;
        
        createScreenComponent(ScreenModel.STATIC_STRING, itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
        String strDisplay = converter.getFieldDesc();
        if ((strDisplay != null) && (strDisplay.length() > 0))
        {
            ScreenLoc descLocation = targetScreen.getNextLocation(ScreenConstants.FIELD_DESC, ScreenConstants.DONT_SET_ANCHOR);
            Map<String, Object> descProps = new HashMap<String, Object>();
            descProps.put(ScreenModel.DISPLAY_STRING, strDisplay);
            createScreenComponent(ScreenModel.STATIC_STRING, descLocation, targetScreen, null, 0, properties);
        }
        
        Converter dayConverter = (Converter)converter;
        dayConverter = new FieldDescConverter(dayConverter, "Thick");
        dayConverter = new BitConverter(dayConverter, 0, true, true);
        itsLocation = targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST_CHECKBOX, ScreenConstants.DONT_SET_ANCHOR);
        screenField = dayConverter.setupDefaultView(itsLocation, targetScreen, iDisplayFieldDesc);
        
        dayConverter = new FieldDescConverter(dayConverter, "Thin");
        dayConverter = new BitConverter(dayConverter, 1, true, true);
        itsLocation = targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST_CHECKBOX, ScreenConstants.DONT_SET_ANCHOR);
        screenField = dayConverter.setupDefaultView(itsLocation, targetScreen, iDisplayFieldDesc);
        
        dayConverter = new FieldDescConverter(dayConverter, "Interface");
        dayConverter = new BitConverter(dayConverter, 2, true, true);
        itsLocation = targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST_CHECKBOX, ScreenConstants.DONT_SET_ANCHOR);
        screenField = dayConverter.setupDefaultView(itsLocation, targetScreen, iDisplayFieldDesc);
        return screenField;
    }
    /**
     * IncludeThis Method.
     */
    public boolean includeThis(ClassProjectModel.CodeType codeType, boolean hasAnInterface)
    {
        int scope = (int)(this.getValue() + 0.5);
        int target = 0;
        if (codeType == ClassProjectModel.CodeType.THICK)
            target = LogicFile.INCLUDE_THICK;
        if (codeType == ClassProjectModel.CodeType.THIN)
            target = LogicFile.INCLUDE_THIN;
        if (codeType == ClassProjectModel.CodeType.INTERFACE)
            target = LogicFile.INCLUDE_INTERFACE;
        if ((hasAnInterface)
            && ((scope & LogicFile.INCLUDE_INTERFACE) != 0) && (codeType != ClassProjectModel.CodeType.INTERFACE))
                return false;   // If this has already been included in the interface, don't include it here
        if ((!hasAnInterface) && (scope == LogicFile.INCLUDE_INTERFACE) && (codeType == ClassProjectModel.CodeType.THICK))
            return true;    // If there isn't going to be an interface class, make sure it is included in thick
        return ((target & scope) != 0);
    }

}
