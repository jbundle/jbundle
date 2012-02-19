/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)SCheckBox.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
 * Implements a standard check box.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SCheckBox extends SToggleButton
{
    /**
     * Constructor.
     */
    public SCheckBox()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public SCheckBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, null, null, null, null, null);
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param strValue The value to set the field on button press.
     * @param strDesc The description of this button.
     */
    public SCheckBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String setValue, String fieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, setValue, fieldDesc, null, null, null);
    }
    /**
     * Set this control's value as it was submitted by the HTML post operation.
     * @return The value the field was set to.
     */
    public String getSFieldProperty(String strFieldName)
    {
        String strValue = super.getSFieldProperty(strFieldName);
        if (strValue == null)
            if (this.getParentScreen() != null)
                if (DBConstants.SUBMIT.equalsIgnoreCase(this.getParentScreen().getProperty(DBParams.COMMAND)))
                    strValue = DBConstants.NO;    // If you submit a checkbox that is off, the param is not passed (null) so I need a No.
        return strValue;
    }
    /**
     * Set this control's converter to this HTML param.
     * @param strParamValue The param to set (if matches button, do the command).
     * @return An error code.
     */
    public int setSFieldValue(String strParamValue, boolean bDisplayOption, int iMoveMode)
    {
        char chFirst = 'N';
        if (strParamValue != null) if (strParamValue.length() > 0)
            chFirst = strParamValue.charAt(0);
        boolean bState = false;
        if ((chFirst == 'T') || (chFirst == 'Y')) // True/Yes
            bState = true;
        return this.getConverter().setState(bState, bDisplayOption, iMoveMode);
    }
}
