/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)MainReadOnlyHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;

/**
 * This class is added to the key field for a display.
 * If the user changes the field, a read is attempted and the data
 * is displayed. If it doesn't exist, this record is cleared.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MainReadOnlyHandler extends MainFieldHandler
{

    /**
     * Constructor.
     */
    public MainReadOnlyHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param iKeySeq The key area this field accesses.
     */
    public MainReadOnlyHandler(String keyName)
    {
        this();
        this.init(null, keyName);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param iKeySeq The key area to read.
     */
    public void init(BaseField field, String keyName)
    {
        super.init(field, keyName);
        
        m_bReadOnly = true;
    }
}
