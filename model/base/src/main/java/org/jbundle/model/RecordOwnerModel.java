/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model;

import java.util.Map;

import org.jbundle.model.db.Rec;


/**
 * A RecordOwnerParent is the parent of a record owner.
 * A parent of a record owner can be either a record owner or a task.
 */
public interface RecordOwnerModel
    extends RecordOwnerParent
{
        /**
         * Initialize the RecordOwner.
         */
        public void init(RecordOwnerParent parent, Rec recordMain, Map<String, Object> properties);
        /**
         * Add this record to this screen.
         * @param record The record to add.
         * @param bMainQuery If this is the main record.
         */
        public void addRecord(Rec record, boolean bMainQuery);
        /**
         * Remove this record from this screen.
         * @param record The record to remove.
         * @return true if successful.
         */
        public boolean removeRecord(Rec record);
        /**
         * Lookup this record for this recordowner.
         * @param strFileName The record's name.
         * @return The record with this name (or null if not found).
         */
        public Rec getRecord(String strFileName);
        /**
         * Get the main record for this screen.
         * @return The main record (or null if none).
         */
        public Rec getMainRecord();
        /**
         * Get the screen record.
         * @return The screen record.
         */
        public Rec getScreenRecord();
}
