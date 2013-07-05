/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)ControlFileHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;


/**
 * ControlFileHandler - Special for control files.
 * A control file is a file with only one record (which generally
 * has sub-system properties).
 * This class reads first record ONLY on setOwner() unless this is
 * the first time, in which case a new record is created.
 * Doesn't allow new or move record.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ControlFileHandler extends FileListener
{

    /**
     * ControlFileHandler - Constructor.
     */
    public ControlFileHandler()
    {
        super();
    }
    /**
     * ControlFileHandler - Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public ControlFileHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * ControlFileHandler - Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public void init(Record record)
    {
        super.init(record);
        this.setMasterSlaveFlag(FileListener.RUN_IN_MASTER | FileListener.DONT_REPLICATE_TO_SLAVE);
    }
    /**
     * Set the field or file that owns this listener.
     * This method calls doNewRecord when the owner is set.
     * @see doNewRecord.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner == null)
            return;
        boolean bEnabledFlag = this.setEnabledListener(true);   // Disabled automatically on setOwner.
        try   {     // Open this table and move to the control record
            if (!this.getOwner().isOpen())
                if (this.isEnabled()) // Not on a slave
                    this.getOwner().open();     // Open the control file
            if (this.getOwner().getEditMode() == Constants.EDIT_NONE)
                if (this.isEnabled()) // Not on a slave
                    this.doNewRecord(true);     // Read the control file
        } catch (DBException ex)   {
            ex.printStackTrace();
        } finally   {
            this.setEnabledListener(bEnabledFlag);  // Set back
        }
    }
    /**
     * Called when a new blank record is required for the table/query.
     * If the file is empty, this does an addNew, otherwise, the first record
     * is read.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)
    {
        super.doNewRecord(bDisplayOption);
        try   {
            if (this.getOwner().isOpen())   // Don't do first time!
            {
                boolean bOldEnableState = this.isEnabledListener();
                this.setEnabledListener(false);      // Just in case AddNew decides to call this
                this.getOwner().close();
                if (this.getOwner().hasNext())  // records yet?
                    this.getOwner().next();
                else
                    this.getOwner().addNew(); // Make a new one
                this.setEnabledListener(bOldEnableState);
            }
        } catch (DBException ex)    {
            if (ex.getErrorCode() == DBConstants.FILE_NOT_FOUND)
                if ((this.getOwner().getOpenMode() & DBConstants.OPEN_DONT_CREATE) == DBConstants.OPEN_DONT_CREATE)
                    return;   // Special case - they didn't want the table created if not found
            ex.printStackTrace(); // Never
        }
    }
}
