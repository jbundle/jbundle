package org.jbundle.base.screen.model;

/**
 * @(#)SEditText.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
 * A standard text input box.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SEditText extends ScreenField
{

    /**
     * Constructor.
     */
    public SEditText()
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
    public SEditText(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
}
