/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)SFakeScreenField.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
 * Included in screen list, but no control.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SFakeScreenField extends ScreenField
{

    /**
     * Constructor.
     */
    public SFakeScreenField()
    {
        super();
    }
    /**
     * SFakeScreenField.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public SFakeScreenField(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(null, parentScreen, fieldConverter, iDisplayFieldDesc);
        m_bIsFocusTarget = false; // Can't tab to a static item
    }
}
