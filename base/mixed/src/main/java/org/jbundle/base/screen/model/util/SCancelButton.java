package org.jbundle.base.screen.model.util;

/**
 * @(#)SCancelButton.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SCannedBox;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * SCancelButton - Included for compatibility.
 * A canned button (box) that sends the cancel message.
 */
public class SCancelButton extends SCannedBox
{

    public SCancelButton( ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, BaseField field)
    {
        super(itsLocation, parentScreen, fieldConverter, ThinMenuConstants.CANCEL, iDisplayFieldDesc, field);
    }
}
