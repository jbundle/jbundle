/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)FileRemoveBOnClose.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseListener;

/**
 * If either of these behaviors are freed, the other is also.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FileRemoveBOnCloseHandler extends FileListener
{
    /**
     * FileRemoveBOnClose - Constructor.
     */
    public FileRemoveBOnCloseHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param listener The dependent listener.
     */
    public FileRemoveBOnCloseHandler(BaseListener listener)
    {
        this();
        this.init(null, listener);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param listener The dependent listener.
     */
    public void init(Record record, BaseListener listener)
    {
        super.init(record);
        this.setDependentListener(listener);
    }
}
