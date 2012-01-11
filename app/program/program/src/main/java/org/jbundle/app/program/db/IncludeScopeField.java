/**
 * @(#)IncludeScopeField.
 * Copyright © 2011 jbundle.org. All rights reserved.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;

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
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenField setupDefaultView(ScreenLocation itsLocation, BasePanel targetScreen, Converter converter, int iDisplayFieldDesc)
    {
        ScreenField screenField = null;
        
        new SStaticString(itsLocation, targetScreen, DBConstants.BLANK);
        String strDisplay = converter.getFieldDesc();
        if ((strDisplay != null) && (strDisplay.length() > 0))
        {
            ScreenLocation descLocation = targetScreen.getNextLocation(ScreenConstants.FIELD_DESC, ScreenConstants.DONT_SET_ANCHOR);
            SStaticString staticString = new SStaticString(descLocation, targetScreen, strDisplay);
        }
        
        Converter dayConverter = converter;
        dayConverter = new FieldDescConverter(dayConverter, "Thick");
        dayConverter = new BitConverter(dayConverter, 0, true, true);
        itsLocation = targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST_CHECKBOX, ScreenConstants.DONT_SET_ANCHOR);
        screenField = (ScreenField)dayConverter.setupDefaultView(itsLocation, targetScreen, iDisplayFieldDesc);
        
        dayConverter = new FieldDescConverter(dayConverter, "Thin");
        dayConverter = new BitConverter(dayConverter, 1, true, true);
        itsLocation = targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST_CHECKBOX, ScreenConstants.DONT_SET_ANCHOR);
        screenField = (ScreenField)dayConverter.setupDefaultView(itsLocation, targetScreen, iDisplayFieldDesc);
        
        dayConverter = new FieldDescConverter(dayConverter, "Interface");
        dayConverter = new BitConverter(dayConverter, 2, true, true);
        itsLocation = targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST_CHECKBOX, ScreenConstants.DONT_SET_ANCHOR);
        screenField = (ScreenField)dayConverter.setupDefaultView(itsLocation, targetScreen, iDisplayFieldDesc);
        return screenField;
    }

}