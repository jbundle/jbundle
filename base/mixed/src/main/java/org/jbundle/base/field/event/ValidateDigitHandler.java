/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)ValidateDigitHandler.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ResourceConstants;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.model.Task;


/**
 * ValidateDigitHandler - Make sure this is a valid modulus-7 number.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ValidateDigitHandler extends FieldListener
{
    /**
     * Constructor.
     */
    public ValidateDigitHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public ValidateDigitHandler(BaseField field)
    {
        this();
        this.init(field);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField field)
    {
        super.init(field);
    }
    /**
     * The Field has Changed.
     * Make sure this is a valid modulus-7 number, if not, return an error.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    { // Read a valid record
        int value = (int)((NumberField)this.getOwner()).getValue();
        if ((value / 10) % 7 != value - (value / 10 * 10))
        {
            Task task = null;
            if (this.getOwner() != null)
                if (this.getOwner().getRecord() != null)
                    task = this.getOwner().getRecord().getTask();
            String strError = "Incorrect Magic number";
            if (task != null)
            {
                strError = ((BaseApplication)task.getApplication()).getResources(ResourceConstants.ERROR_RESOURCE, true).getString(strError);
                return task.setLastError(strError);
            }
            return DBConstants.ERROR_RETURN;
        }
        return super.fieldChanged(bDisplayOption, iMoveMode);
    }
}
