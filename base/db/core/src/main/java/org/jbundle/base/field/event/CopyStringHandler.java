/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)CopyStringHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.thin.base.db.Converter;


/**
 * When this field changes, copy this string to a new field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CopyStringHandler extends CopyDataHandler
{

    /**
     * Constructor.
     */
    public CopyStringHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fldDest The destination field.
     * @param stringValue The string to set the destination field to.
     * @param convconvCheckMark If this evaluates to false, don't do the move.
     */
    public CopyStringHandler(BaseField fldDest, String stringValue, Converter convCheckMark)
    {
        this();
        this.init(null, fldDest, stringValue, convCheckMark);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param fldDest The destination field.
     * @param stringValue The string to set the destination field to.
     * @param convconvCheckMark If this evaluates to false, don't do the move.
     */
    public void init(BaseField field, BaseField fldDest, String stringValue, Converter convCheckMark)
    {
        super.init(field, fldDest, stringValue, convCheckMark);
    }
}
