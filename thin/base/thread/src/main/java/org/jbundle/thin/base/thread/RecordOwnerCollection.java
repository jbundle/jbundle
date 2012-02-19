/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.thread;

/**
 * @(#)AutoTask.java    1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.HashSet;

import org.jbundle.model.RecordOwnerParent;

/**
 * An autotask is a wrapper for an independent task thread.
 * Override the runTask method to do something.
 * NOTE: This class is commonly used as a basic task process. This is fine, just remember
 * to free this task manually when you are finished (or free the app that owns this task).
 */
public class RecordOwnerCollection extends HashSet<RecordOwnerParent>
{
	private static final long serialVersionUID = 1L;

	protected RecordOwnerParent recordOwnerParent = null;
	
	/**
	 * Constructor.
	 */
	public RecordOwnerCollection()
	{
		super(1);	// Commonly only one entry
	}
	/**
	 * Constructor.
	 */
	public RecordOwnerCollection(RecordOwnerParent recordOwnerParent)
	{
		this();
		this.init(recordOwnerParent);
	}
	/**
	 * Constructor.
	 */
	public void init(RecordOwnerParent recordOwnerParent)
	{
		this.recordOwnerParent = recordOwnerParent;
	}
	/**
	 * 
	 */
	public void free()
	{
		while (!this.isEmpty())
		{
			RecordOwnerParent recordOwner = this.iterator().next();
			recordOwner.free();
		}
		recordOwnerParent = null;
	}
    /**
     * Add this record owner to my list.
     * @param recordOwner The recordowner to add
     */
    public boolean addRecordOwner(RecordOwnerParent recordOwner)
    {
    	return this.add(recordOwner);
    }
    /**
     * Remove this record owner to my list.
     * @param recordOwner The recordowner to remove.
     */
    public boolean removeRecordOwner(RecordOwnerParent recordOwner)
    {
    	return this.remove(recordOwner);
    }
}
