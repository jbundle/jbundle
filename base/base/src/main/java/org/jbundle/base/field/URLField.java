/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)URLField.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.convert.FieldLengthConverter;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Converter;


/**
 * Extends the text field and adds a URL button.
 */
public class URLField extends StringField
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public URLField()
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
    public URLField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
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
        m_iMaxLength = 100;
    }
    /**
     * Get the HTML Input Type.
     * @return The HTML type (url).
     */
    public String getInputType(String strViewType)
    {
        if (ScreenModel.HTML_TYPE.equalsIgnoreCase(strViewType))
            return "url";
        else //if (TopScreen.XML_TYPE.equalsIgnoreCase(strViewType))
            return super.getInputType(strViewType);
    }
    /**
     * Get the HTML Hyperlink.
     * @return The string unchanged.
     */
    public String getHyperlink() 
    {
        return this.getString();    // Field is the hyperlink
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
        if (converter.getMaxLength() > ScreenConstants.kMaxSingleChars)
            converter = new FieldLengthConverter((Converter)converter, ScreenConstants.kMaxSingleChars);   // Show as a single line.
        ScreenComponent sScreenField = super.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
        properties = new HashMap<String,Object>();
        properties.put(ScreenModel.FIELD, this);
        properties.put(ScreenModel.COMMAND, ScreenModel.URL);
        properties.put(ScreenModel.IMAGE, ScreenModel.URL);
        ScreenComponent pSScreenField = createScreenComponent(ScreenModel.CANNED_BOX, targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, iDisplayFieldDesc, properties);
        pSScreenField.setRequestFocusEnabled(false);
        return sScreenField;
    }
}
