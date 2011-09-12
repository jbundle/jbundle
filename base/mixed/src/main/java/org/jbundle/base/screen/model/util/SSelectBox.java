/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.util;

/**
 * @(#)SSelectBox.java  0.00 12-Feb-97 Don Corley
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
 * Implements a button to carry out some standard functions.
 * <br/>This button sends the "lookup" command.
 */
public class SSelectBox extends SCannedBox
{

    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public SSelectBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Record query)
    {
        super(itsLocation, parentScreen, fieldConverter, ThinMenuConstants.LOOKUP, iDisplayFieldDesc, query);
    }
}
