/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.util;

/**
 * @(#)SPrintButton.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SCannedBox;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * SPrintButton - Implements a button to carry out some standard functions.
 * <br/>This button sends the "print" command.
 */
public class SPrintButton extends SCannedBox
{
    public SPrintButton( ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Record query)
    {
        super(itsLocation, parentScreen, fieldConverter, ThinMenuConstants.PRINT, iDisplayFieldDesc, query);
    }
}
