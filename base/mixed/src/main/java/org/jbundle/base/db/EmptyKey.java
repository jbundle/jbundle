package org.jbundle.base.db;

/**
 * @(#)EmptyKey.java    1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.util.DBConstants;
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
