/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)MainFieldHandler.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.buff.VectorBuffer;


/**
 * This class is used when the user enters a field that is the key field
 * for a key area. If they enter it into a new record, a read is attempted.
 * If the user tries to change a main key field, this class checks for a duplicate
 * key field.
 * Read keyed on change/Check for dup on change w/current record.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MainFieldHandler extends FieldListener
{
    /**
     * The key area to check.
     */
    protected String keyName = null;
    /**
     * Read only?
     */
    protected boolean m_bReadOnly = false;

    /**
     * Constructor.
     */
    public MainFieldHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param keyName The key area this field accesses.
     */
    public MainFieldHandler(String keyName)
    {
        this();
        this.init(null, keyName);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param keyName The key area this field accesses.
     */
    public void init(BaseField field, String keyName)
    {
        super.init(field);
        this.keyName = keyName;
        m_bInitMove = false;        // DONT respond to these moves!
        m_bReadMove = false;
        
        m_bReadOnly = false;
    }
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (this.getOwner() != null)
        {
            if (this.getOwner().getListener(this.getClass()) != this)
                if (((MainFieldHandler)this.getOwner().getListener(this.getClass())).getActualKeyArea() == this.getActualKeyArea())
                    this.getOwner().removeListener(this.getOwner().getListener(this.getClass()), true); // Make sure there is only one of these
        }
    }
    /**
     * Set this cloned listener to the same state at this listener.
     * @param field The field this new listener will be added to.
     * @param listener The new listener to sync to this.
     * @param bInitCalled Has the init method been called?
     * @return True if I called init.
     */
    public boolean syncClonedListener(BaseField field, FieldListener listener, boolean bInitCalled)
    {
        if (!bInitCalled)
            ((MainFieldHandler)listener).init(null, keyName);
        return super.syncClonedListener(field, listener, true);
    }
    /**
     * Move the physical binary data to this field.
     * The code here is kinda complicated. This is the unique key field.
     * If this is a new record and there haven't been any changes yet,
     * I do a seek on this index.
     * @param data the raw data to set the basefield to.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int doSetData(Object data, boolean bDisplayOption, int iMoveMode)
    {
        if (!m_bReadOnly)
        {
            Object bookmark = null;
            BaseBuffer buffer = null; // In case a temp buffer is needed
            boolean bSuccess = false;
            Object oldBuff = this.getOwner().getData();     // Get a copy of the old key field
            Record record = this.getOwner().getRecord();
            if (record.getEditMode() != Constants.EDIT_ADD)
            {   // Current record (but no changes) - save the bookmark only for possible re-read.
                try   {
                    bookmark = record.getHandle(DBConstants.DATA_SOURCE_HANDLE);
                } catch (DBException e)   {
                    bookmark = null;
                }
            }
            int result = super.doSetData(data, bDisplayOption, iMoveMode);  // Do the rest of the behaviors
            if (result != DBConstants.NORMAL_RETURN)
                return result;  // If error
            if ((!this.getOwner().isJustModified()) || (data == null))
                return result;  // If no change or set to null
            String iOldKeySeq = record.getKeyArea(-1).getKeyName();
            record.setKeyArea(keyName);
            if ((!record.isModified(true)) && ((record.getEditMode() == Constants.EDIT_ADD) || (record.getEditMode() == Constants.EDIT_NONE)))   // Modified or valid record
            {   // This is a new record and this is the first mod
                try   {
                    buffer = new VectorBuffer(null);
                    record.getKeyArea(keyName).setupKeyBuffer(buffer, DBConstants.FILE_KEY_AREA);   // Save the keys
                    bSuccess = this.seek(record); // Read this record (display if found)
                    record.setKeyArea(iOldKeySeq);      // Set the key order back
                    if (!bSuccess)
                    {   // Not found, restore the data and the new key
                        record.addNew();            // This may wipe out the keys
                        if (buffer != null)
                            buffer.resetPosition();   // Just to be careful
                        record.getKeyArea(keyName).reverseKeyBuffer(buffer, DBConstants.FILE_KEY_AREA); // Restore the keys
                        result = DBConstants.NORMAL_RETURN;     // Everything went okay
                    }
                    else
                    {   // Record found - good
                        if (record.getRecordOwner().isBatch())
                        {   // Special case - Can't display this record
                            return DBConstants.DUPLICATE_KEY;
                        }
                    }
                } catch (DBException e)   {
                    return e.getErrorCode();    // Never
                }
            }
            else if (record.getKeyArea(keyName).getUniqueKeyCode() == DBConstants.UNIQUE)
            { // Data already entered, see if this entry makes a duplicate key!
                buffer = new VectorBuffer(null);
                try   {
                    buffer.fieldsToBuffer(record, BaseBuffer.ALL_FIELDS); // Save the entire record
                    bSuccess = this.seek(record); // See if this key already exists
                    if (bookmark != null)
                    {
                        if (record.setHandle(bookmark, DBConstants.DATA_SOURCE_HANDLE) != null)   // Set the pointer back to the old key
                            record.edit();
                        else
                            record.addNew();    // Never
                    }
                    else
                        record.addNew();// This is a new record, and they entered non-key data already
                    record.setKeyArea(iOldKeySeq);      // Set the key order back
                    buffer.bufferToFields(record, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);   // Restore the data
                    for (int iFieldSeq = DBConstants.MAIN_FIELD; iFieldSeq < record.getFieldCount() + DBConstants.MAIN_FIELD; iFieldSeq++)
                    {   // Redisplay all the fields
                        record.getField(iFieldSeq).displayField();
                    }
                } catch (DBException e)   {
                    return e.getErrorCode();    // Never
                }
                if (bSuccess)
                {
                    this.getOwner().setData(oldBuff, bDisplayOption, iMoveMode);        // Restore a copy of the old key field
                    result = DBConstants.DUPLICATE_KEY;     // Can't enter this key, you'll get a dup key!
                }
                else
                    result = DBConstants.NORMAL_RETURN;     // Good, this is a valid key
            }
            return result;
        }
        else
        {       // Read only
            Record record = this.getOwner().getRecord();
            int result = super.doSetData(data, bDisplayOption, iMoveMode);  // Do the rest of the behaviors
            if (result != DBConstants.NORMAL_RETURN)
                return result;  // If error or no change
            if (!this.getOwner().isJustModified())
                return result;  // If error or no change
            String strOldKeySeq = Constants.BLANK;
            strOldKeySeq = record.getKeyArea(-1).getKeyName();
            record.setKeyArea(keyName);
            if (!record.isModified(true))   // Modified or valid (Need valid for secondary lookups)
            {
                try   {
                    if ((data == null)
                        || (data.equals(Constants.BLANK)))
                    {
                        record.addNew();  // Clear the fields!
                    }
                    else
                    {   // This is a new record and this is the first mod
                        boolean bSuccess = this.seek(record);    // Read this record (display if found)
                        if (!bSuccess)
                            record.initRecord(bDisplayOption);  // Clear the fields!
                    }
                } catch (DBException e)   {
                    return e.getErrorCode();    // Never
                } finally {
                    record.setKeyArea(strOldKeySeq);      // Set the key order back
                }
            }
            return result;
        }
    }
    /**
     * Read the record (assumes record has correct data in it to do the read).
     * This method can be overidden if keyed access is not what you want to do (see SCF file).
     * @param record The record to seek from.
     * @exception DBException seek exceptions.
     */
    public boolean seek(Record record) throws DBException
    {
        return record.seek(DBConstants.EQUALS);
    }
    /**
     * Set this behavior to read only on field change.
     * @param bReadOnly If true, read only.
     */
    public void setReadOnly(boolean bReadOnly)
    {
        m_bReadOnly = bReadOnly;
    }
    /**
     * Get the key sequence.
     * @return
     */
    public String getActualKeyArea()
    {
        if (keyName == null)
            return Record.ID_KEY;
        return keyName;
    }
}
