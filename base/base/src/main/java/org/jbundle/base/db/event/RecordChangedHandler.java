/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * @(#)DateChangedHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Calendar;

import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.KeyField;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.field.event.FieldDataScratchHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.model.Utility;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldInfo;


/**
 * DateChangedHandler - Set the "Date Changed" field whenever the record is changed.
 * NOTE: There is no Clone method, because this method will always be placed in the addListeners method of the file, which is called on Clone.
 * @version 1.0.0
 * @author    Don Corley
 */
public class RecordChangedHandler extends FileListener
{
    /**
     * The date changed field in this record.
     */
    protected DateTimeField m_field = null;
    /**
     * Temporary fake key field.
     */
    protected KeyField m_fakeKeyField = null;
    /**
     * Number of retries.
     */
    protected int m_iErrorCount = 0;
    /**
     * Max number of retries.
     */    
    public static final int MAX_WRITE_ATTEMPTS = 50;

    /**
     * Constructor.
     */
    public RecordChangedHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param field The date changed field in this record.
     */
    public RecordChangedHandler(DateTimeField field)
    {
        this();
        this.init(null, field);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param iMainFilesField The sequence of the date changed field in this record.
     * @param field The date changed field in this record.
     */
    public void init(Record record, DateTimeField field)
    {
        super.init(record);
        m_field = field;
        FieldDataScratchHandler listener = new FieldDataScratchHandler(null);
        listener.setAlwaysEnabled(true);    // Since this holds the current key
        m_field.addListener(listener); // I will need the original value for the sql update
        this.setMasterSlaveFlag(FileListener.RUN_IN_SLAVE | FileListener.RUN_IN_MASTER); // Set date key in slave code, check for error in master code
    }
    /**
     * Free this listener.
     */
    public void free()
    {
        if (m_fakeKeyField != null)
            m_fakeKeyField.free();
        m_fakeKeyField = null;
        super.free();
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * This method sets the field to the current time on an add or update.
     * @param field If this file change is due to a field, this is the field.
     * @param changeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    { // Read a valid record
    	if ((this.getOwner().getMasterSlave() & RecordOwner.SLAVE) != 0)
    	{	// Only do this in the slave and shared code
	        switch (iChangeType)
	        {
	            case DBConstants.REFRESH_TYPE:
	            case DBConstants.ADD_TYPE:
	                this.setTheDate();
	                break;
	            case DBConstants.UPDATE_TYPE:
	                if (this.getOwner().isModified(true))
	                {   // Don't need to set the last changed date if the record hasn't changed
	                    this.setTheDate();
	                    if (this.isModLockMode())
	                        this.setTemporaryKeyField();
	                }
	                break; // DO NOT Reset error count!
	            case DBConstants.AFTER_UPDATE_TYPE:
	                this.clearTemporaryKeyField();
	                m_iErrorCount = 0;  // Reset error count - Success
	                break;
	        }
    	}
        return super.doRecordChange(field, iChangeType, bDisplayOption);        // Initialize the record
    }
    /**
     * Called when a error happens on a file operation, return the error code, or fix the problem.
     * @param changeType The type of change that occurred.
     * @param iErrorCode The error code from the previous listener.
     * @return The new error code.
     */
    public int doErrorReturn(int iChangeType, int iErrorCode)        // init this field override for other value
    {
        if (iChangeType == DBConstants.AFTER_UPDATE_TYPE)
            if (this.isModLockMode())
            {
            	if (this.getOwner().getTable().getCurrentTable() == this.getOwner().getTable())
            	{	// Must be the top-level record to do the merge. Otherwise, return an error and let the current record handle the merge
	            	boolean bRunMergeCode = false;
	            	if ((this.getOwner().getMasterSlave() & RecordOwner.MASTER) != 0)
	            		if ((this.getOwner().getDatabaseType() & DBConstants.SERVER_REWRITES) == 0)
	            			bRunMergeCode = true;	// Typically refresh in the client so listeners can be run
	            	if ((this.getOwner().getMasterSlave() & RecordOwner.SLAVE) != 0)
	            		if ((this.getOwner().getDatabaseType() & DBConstants.SERVER_REWRITES) != 0)
	            			bRunMergeCode = true;	// Unless server rewrite is set, then do the rewrite in the server
	            	if (bRunMergeCode)
	            		return this.refreshMergeAndRewrite(iErrorCode);		// Only do this in the master and shared code
            	}
            }
        return super.doErrorReturn(iChangeType, iErrorCode);
    }
    /**
     * Am I checking for modification locks?
     * @return True if I am.
     */
    public boolean isModLockMode()
    {
        Record record = this.getOwner();
        if (record != null)
            if ((record.getOpenMode() & DBConstants.OPEN_LAST_MOD_LOCK_TYPE) == DBConstants.OPEN_LAST_MOD_LOCK_TYPE)
                return true;
        return false;
    }
    /**
     * Set the date field to the current time.
     * Also make sure the time is not the same as it is currently.
     */
    public void setTheDate()
    {
        boolean[] rgbEnabled = m_field.setEnableListeners(false);
        Calendar calAfter = m_field.getCalendar();
        Calendar calBefore = m_field.getCalendar();
        m_field.setValue(DateTimeField.currentTime(), DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);   // File written or updated, set the update date
        calAfter = m_field.getCalendar();
        if (calBefore != null)
            if (calAfter.before(calBefore))
                calAfter = calBefore;   // If this was set with a different computer (clock), make sure it always increases!
        if (calAfter != null)
            if (calAfter.equals(calBefore))
        {
            calAfter.add(Calendar.SECOND, 1);   // Can't be the same as last time.
            m_field.setCalendar(calAfter, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
        }
        Utility.getLogger().info("Set date: " + m_field.toString());
        m_field.setEnableListeners(rgbEnabled);
    }
    /**
     * Set up/do the remote criteria.
     * @param strbFilter The SQL query string to add to.
     * @param bIncludeFileName Include the file name with this query?
     * @param vParamList The param list to add the raw data to (for prepared statements).
     * @return True if you should not skip this record (does a check on the local data).
     */
    public void setTemporaryKeyField()
    {
        FieldDataScratchHandler fieldDataScratchHandler = (FieldDataScratchHandler)m_field.getListener(FieldDataScratchHandler.class);
        Record record = this.getOwner();
        KeyArea keyArea = record.getKeyArea(0);
        if (keyArea.getKeyFields(false, true) == 1)
        {
            if (m_fakeKeyField == null)
            {
                m_fakeKeyField = new KeyField(keyArea, m_field, DBConstants.ASCENDING);
                m_fakeKeyField.setIsTemporary(true);
            }
            else
                keyArea.addKeyField(m_fakeKeyField);
        }
        BaseField paramField = m_fakeKeyField.getField(DBConstants.TEMP_KEY_AREA);
        paramField.setData(fieldDataScratchHandler.getOriginalData());
        Utility.getLogger().info("Set temp key field: " + paramField.toString());
    }
    /**
     * Remove the temporary key field.
     */
    public void clearTemporaryKeyField()
    {
        Record record = this.getOwner();
        KeyArea keyArea = record.getKeyArea(0);
        if (keyArea.getKeyFields(false, true) == 2)
            if (m_fakeKeyField != null) // Always
                keyArea.removeKeyField(m_fakeKeyField);
    }
    /**
     * I got an error writing this since someone else updated this record.
     * I need to:
     * 1. Read the new record.
     * 2. Merge it with my data.
     * 3. Try to write it again.
     * 4. Return a normal or error return.
     */
    public int refreshMergeAndRewrite(int iErrorCode)
    {
    	if ((this.getOwner().getMasterSlave() & RecordOwner.SLAVE) != 0)
    		this.clearTemporaryKeyField();		// Don't do this for master only code (slave will take care of the unique incrementing date)

        Record record = this.getOwner();        
        if (iErrorCode == DBConstants.INVALID_RECORD)
        {
            Object[] rgobjEnabledFields = null;
            boolean[] rgbEnabled = null;
            try {
                m_iErrorCount++;
                if (m_iErrorCount >= MAX_WRITE_ATTEMPTS)
                {
                    Utility.getLogger().warning("Max rewrites reached: " + this.getOwner().getRecordName());
                    m_iErrorCount = 0;  // Reset (since this will error out)
                    return DBConstants.DUPLICATE_KEY; // VERY VERY Rare
                }
                // Note: This listener is disabled (because it is called, so I need to do these writes manually)
                m_field.setModified(false); // Make sure refresh gets the correct LastModDate.
                m_bIsTempEnabled++;    // Force this listener to resolve the duplicate until I'm done
                iErrorCode = record.refreshToCurrent(DBConstants.AFTER_UPDATE_TYPE, false);
                if ((record.isModified()) || (iErrorCode == DBConstants.RECORD_CHANGED))
                {   // Possible that refresh did a set or refresh is now the same as this.
                	if ((this.getOwner().getMasterSlave() & RecordOwner.SLAVE) != 0)
                	{		// Don't do this for master only code (slave will take care of the unique incrementing date)
                        this.setTheDate();	// Add one second to the date (Only in server code)
                		this.setTemporaryKeyField();
                	}
                    rgobjEnabledFields = record.setEnableFieldListeners(false);    // These will be called when I exit - Don't call now
                    rgbEnabled = record.setEnableListeners(false);
                    record.set();
                }
                iErrorCode = DBConstants.NORMAL_RETURN; // Success
            } catch (DBException ex) {
                iErrorCode = ex.getErrorCode();     // Should still be DBConstants.INVALID_RECORD
                if (iErrorCode != DBConstants.INVALID_RECORD)
                    m_iErrorCount = 0;  // Reset (since this will error out)
            } finally {
                if (rgbEnabled != null)
                    record.setEnableListeners(rgbEnabled);
                if (rgobjEnabledFields != null)
                    record.setEnableFieldListeners(rgobjEnabledFields);
                m_bIsTempEnabled--;
            }
        	if ((this.getOwner().getMasterSlave() & RecordOwner.SLAVE) != 0)		// Don't do this for master only code (slave will take care of the unique incrementing date)
        		this.clearTemporaryKeyField();
        }
        return iErrorCode;
    }
    /**
     * Is this listener enabled flag set?
     * @return true if enabled.
     */
    public boolean isEnabledListener()
    {
        if (m_bIsTempEnabled > 0)
            return true;    // Always enabled!
        else
            return super.isEnabledListener();
    }
    protected int m_bIsTempEnabled = 0;
}
