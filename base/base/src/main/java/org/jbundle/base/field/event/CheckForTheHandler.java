/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)CheckForTheHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.field.BaseField;

/**
 * CheckForTheHandler - Strip out a leading "The ".
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CheckForTheHandler extends FieldListener
{

    /**
     * Constructor.
     * Put this in after the convert to upper listener
     */
    public CheckForTheHandler()
    {
        super();
        m_bReadMove = false;  // Don't move on read!
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public CheckForTheHandler(BaseField field)
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
        m_bReadMove = false;  // Don't move on read!
    }
    /**
     * Move the physical binary data to this field.
     * @param objData the raw data to set the basefield to.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int doSetData(Object objData, boolean bDisplayOption, int moveMode)
    {
        if (objData instanceof String)
            if (((String)objData).length() > 3) if (((String)objData).substring(0, 4).equalsIgnoreCase("THE "))
        {
            objData = ((String)objData).substring(4);
        }
        return super.doSetData(objData, bDisplayOption, moveMode);
    }
}
