/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.model;


/**
 * A RecordOwnerParent is the parent of a record owner.
 * A parent of a record owner can be either a record owner or a task.
 */
public interface RecordOwnerParent
    extends PropertyOwner, Freeable
{
    /**
     * Get the task for this record owner.
     * If this is a RecordOwner, return the parent task. If this is a Task, return this.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public Task getTask();
    /**
     * Add this record owner to my list.
     * @param recordOwner The record owner to add
     * @return True if successful
     */
    public boolean addRecordOwner(RecordOwnerParent recordOwner);
    /**
     * Remove this record owner to my list.
     * @param recordOwner The record owner to remove.
     * @return True if successful
     */
    public boolean removeRecordOwner(RecordOwnerParent recordOwner);
}
