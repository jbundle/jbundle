/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)CopyLastHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.thin.base.db.Converter;


/**
 * When this field changes, copy the last name to a new field.
 * The last name is the last word of the string.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CopyLastHandler extends CopyFieldHandler
{

    /**
     * Constructor.
     */
    public CopyLastHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param fieldName The sequence of the last name field in this record.
     */
    public CopyLastHandler(String fieldName)
    {
        this();
        this.init(null, null, null, fieldName);
    }
    /**
     * Constructor.
     * @param fldTarget The destination last name field.
     */
    public CopyLastHandler(BaseField fldTarget)
    { // Split name into title/first/middle/last
        this();
        this.init(null, fldTarget, null, null);
    }
    /**
     * Constructor.
     * @param fldTarget The destination last name field.
     * @param checkMark If false, do not move the last name.
     */
    public CopyLastHandler(BaseField fldTarget, Converter checkMark)
    { // Split name into title/first/middle/last
        this();
        this.init(null, fldTarget, checkMark, null);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param field The destination last name field.
     * @param fieldName The sequence of the last name field in this record.
     * @param checkMark If false, do not move the last name.
     */
    public void init(BaseField field, BaseField fldTarget, Converter checkMark, String fieldName)
    {
        super.init(field, fldTarget, checkMark, fieldName);
    }
    /**
     * Do the physical move operation.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int moveSourceToDest(boolean bDisplayOption, int iMoveMode)
    {
        String string = this.getSourceField().getString();        // Get this string
        int i = string.lastIndexOf(' ');
        if (i != -1)
            string = string.substring(i+1);
        return m_fldDest.setData(string, bDisplayOption, iMoveMode);
    }
}
