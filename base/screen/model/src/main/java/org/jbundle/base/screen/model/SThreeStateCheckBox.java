/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)SCheckBox.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
 * Implements a three-state check box.
 * (The third state is shaded and equates to null).
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SThreeStateCheckBox extends SCheckBox
{
    /**
     * Constructor.
     */
    public SThreeStateCheckBox()
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
    public SThreeStateCheckBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
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
     * @param setValue The value to set the converter to on check.
     * @param fieldDesc The field description.
     */
    public SThreeStateCheckBox(ScreenLocation itsLocation,BasePanel parentScreen,Converter fieldConverter,int iDisplayFieldDesc, String setValue, String fieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, setValue, fieldDesc, null, null, null);
    }
    /**
     * Set this control's converter to this HTML param.
     * @param strParamValue The value.
     * @return The error code.
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
