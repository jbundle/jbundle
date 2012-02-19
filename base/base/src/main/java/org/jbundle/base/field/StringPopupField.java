/*
 *  @(#)User.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Constants;


/**
 * Special Alpha Field to easily display a popup of translation values code->desc.
 */
public class StringPopupField extends StringField
{
	private static final long serialVersionUID = 1L;

	/**
     *  Default constructor.
     */
    public StringPopupField()
    {
        super();
    }
    /**
     *  Field Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public StringPopupField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     *  Initialize class fields.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     *  getPopupMap Method - Override this.
     */
    public String[][] getPopupMap()
    {
        String string[][] = {
        {"none", "No popup map specified"}, 
        {"none2", "No popup map specified"}, 
        };
        return string;  // Never (hopefully)
    }
    /**
     * Convert this index to a display field.
     * @param index The index to convert.
     * @return The display string.
     */
    public String convertIndexToDisStr(int index)
    {
        String[][] rgstring = this.getPopupMap();
        if (index >= rgstring.length)
            return Constants.BLANK;
        return rgstring[index][1];
    }
    /**
     * Convert this index to a string.
     * This method is usually overidden by popup fields.
     * @param index The index to convert.
     * @return The display string.
     */
    public String convertIndexToString(int index)
    {
        String[][] rgstring = this.getPopupMap();
        if (index >= rgstring.length)
            index = 0;
        return rgstring[index][0];
    }
    /**
     * Convert the field's value to a index (for popup) (usually overidden).
     * @param string The string to convert to an index.
     * @return The resulting index.
     */
    public int convertStringToIndex(String tempString)
    {
        String[][] rgstring = this.getPopupMap();
        for (int index = 0; index < rgstring.length; index++)
        {
            if (tempString.equalsIgnoreCase(rgstring[index][0]))
                return index;       
        }
        return -1;
    }
    /**
     * Set up the default screen control for this field (A SPopupBox).
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        return createScreenComponent(ScreenModel.POPUP_BOX, itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
    }
}
