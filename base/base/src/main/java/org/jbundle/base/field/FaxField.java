/*

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)FaxField.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;


/**
 * Extends the text field and adds a Fax button.
 */
public class FaxField extends StringField
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public FaxField()
    {
        super();
    }
    /**
     * BaseField Constructor
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public FaxField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Get the faxto HTML Hyperlink.
     */
    public String getHyperlink() 
    {
        String strMailTo = this.getString();
        if (strMailTo != null) if (strMailTo.length() > 0)
            strMailTo = DBParams.FAX + ":" + strMailTo;
        return strMailTo;
    }
    /**
     * Initialize the member fields.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
        if (iDataLength == DBConstants.DEFAULT_FIELD_LENGTH)
            m_iMaxLength = 24;
    }
    /**
     * Set up the default screen control for this field.
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        ScreenComponent screenField = super.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
        properties = new HashMap<String,Object>();
        properties.put(ScreenModel.FIELD, this);
        properties.put(ScreenModel.COMMAND, ScreenModel.FAX);
        properties.put(ScreenModel.IMAGE, ScreenModel.FAX);
        ScreenComponent pSScreenField = createScreenComponent(ScreenModel.CANNED_BOX, targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, iDisplayFieldDesc, properties);
        pSScreenField.setRequestFocusEnabled(false);
        return screenField;
    }
}
