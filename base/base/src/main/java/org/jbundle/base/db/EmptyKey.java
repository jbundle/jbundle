/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * @(#)EmptyKey.java    1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.db.Field;

/**
 * EmptyKey - Definition of an empty index.
 */
public class EmptyKey extends KeyArea
{
	private static final long serialVersionUID = 1L;

	/**
     * Constructor
     */
    public EmptyKey(Record record)
    {
        super(record, DBConstants.UNIQUE, "");
    }
    /**
     * AddKeyField - Override and Don't add the key field.
     */
    public void addKeyField(Field field, boolean keyOrder)
    {
    } // Get the field with this seq
}
