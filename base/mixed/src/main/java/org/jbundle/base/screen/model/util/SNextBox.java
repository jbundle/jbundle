package org.jbundle.base.screen.model.util;

/**
 * @(#)SNextBox.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SCannedBox;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * SNextBox - Implements a button to carry out some standard functions.
 * <br/>This button sends the "next" command.
 */
public class SNextBox extends SCannedBox
{

    public SNextBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Record query)
    {
        super(itsLocation, parentScreen, fieldConverter, ThinMenuConstants.NEXT, iDisplayFieldDesc, query);
    }
}
