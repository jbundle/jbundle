/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;


/**
 * ScreenRecord - A record just for screen use - navigation is disabled.
 */
public class ScreenRecord extends Record
{
	private static final long serialVersionUID = 1L;

	public static final int kScreenRecordLastField = kRecordLastField;

    /**
     * Constructor.
     */
    public ScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ScreenRecord(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * initialize the screen record.
     */
    public void init(RecordOwner recordOwner)
    {
        super.init(recordOwner);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * addNew - Ignored for Screenrecords.
     */
    public void addNew() throws DBException
    {
    }
    /**
     * Close - Ignored for Screenrecords.
     */
    public void close()
    {
    }
    /**
     * Get the Database Name.
     * @return Always returns "Screen" for screen records.
     */
    public String getDatabaseName()
    {
        return "Screen";
    }
    /**
     * Is this a local (vs remote) file?
     * @return Always returns type DBConstants.SCREEN.
     */
    public int getDatabaseType()
    {
        return DBConstants.SCREEN;
    }
    /**
     * IsOpen - Ignored for Screenrecords.
     * @return Always returns true for Screenrecords.
     */
    public boolean isOpen()
    {
        return true;
    }
    /**
     * Open - Ignored for Screenrecords.
     */
    public void open() throws DBException
    {
    }
}
