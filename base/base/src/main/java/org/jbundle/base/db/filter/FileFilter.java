/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.filter;

/**
 * @(#)DependentFileFilter.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.field.BaseField;

/**
 * A DependentFileFilter is the base for filters that pass fields or strings that
 * are the initial or end values of a record query.
 * This class makes sure to call the setMainKey method when setInitialKey or setEndKey
 * are called.
 * @version 1.0.0
 * @author    Don Corley
 */
public class FileFilter extends FileListener
{
    /**
     * DependentFileFilter.
     */
    public FileFilter()
    {
        super();
    }
    /**
     * Constructor.
     */
    public FileFilter(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public void init(Record record)
    {
        super.init(record);
    }
    /**
     * Get the foreign field that references this record.
     * There can be more than one, so supply an index until you get a null.
     * @param iIndex The index of the reference to retrieve
     * @return The referenced field
     */
    public BaseField getReferencedField(int iIndex)
    {
        return null;    // Override this!
    }
}
