/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 * FileListener - File Behaviors.
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.BaseListener;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.util.osgi.finder.ClassServiceUtility;


/**
 * FileListener - Base File Listener class.
 * Generally, filelisteners fall into two classes:
 * <br/>1. Filters, which filter the records on queries.
 * <br/>2. Handlers, which respond to record behaviors.
 * <p/>There is also some extensive plumbing to pass the current state from a
 * client listener to a mirrored server listener. To enable a class to pass its
 * parameters to the server listener, just override
 * initRemoteStub(ObjectOutputStream daOut) and add all your data to the stream, and override
 * initRemoteSkel(ObjectInputStream daIn) to deserialize your member variables and call init(xxx),
 * and you must set the clientserver flag to SERVER.
 * See CompareFileFilter for an example.
 * Note, if your listener is a client-only listener, or if if just sets the initial or end keys,
 * server support is not necessary.
 */
public class FileListener extends BaseListener
{
    /**
     * If this bit is set this listener responds if it is in the master space.
     */
    public static final int RUN_IN_MASTER = 1;
    /**
     * If this bit is set this listener responds if it is in the slave space.
     * And if this listener is in the master space it is replicated and linked to the slave space.
     */
    public static final int RUN_IN_SLAVE = 2;
    /**
     * If this bit is set this listener will not be replicated to the slave space
     * but it will run in the space indicated by the other flags.
     */
    public static final int DONT_REPLICATE_TO_SLAVE = 4;
    /**
     * If this bit is set in the clientserver flag, this listener is linked to a client listener.
     */
    public static final int LINKED_TO_SLAVE = 8;
    /**
     * If this bit is set in the clientserver flag, this listener is inited using client listener params.
     */
    public static final int INITED_IN_SLAVE = 16;

    /**
     * The client/server flag. Tells whether to respond when it is in a particular space.
     */
    protected int m_iMasterSlave = FileListener.RUN_IN_MASTER;

    /**
     * Constructor.
     */
    public FileListener()
    {
        super();
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     */
    public FileListener(Record record)
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
        m_iMasterSlave = FileListener.RUN_IN_MASTER;
        super.init(record);
    }
    /**
     * Free this listener.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get this listener's owner.
     * @return My owner (a record).
     */
    public Record getOwner()
    {
        return (Record)this.getListenerOwner();
    }
    /**
     * Get the client/server flag.
     * @return The client/server flag.
     */
    public int getMasterSlaveFlag()
    {
        return m_iMasterSlave;
    }
    /**
     * Set the client/server flag.
     * @param iMasterSlave The client server flag.
     */
    public void setMasterSlaveFlag(int iMasterSlave)
    {
        m_iMasterSlave = iMasterSlave;
    }
    /**
     * Is this listener enabled?
     * Note: This logic checks for 'client-only behaviors' trying to run in
     * an environment that doesn't support 'servers' (client-only environments).
     * @return true if this should be used.
     */
    public boolean isEnabled()
    {
        if ((this.getMasterSlaveFlag() & FileListener.RUN_IN_SLAVE) == 0) // Is NOT enabled in a SERVER only environment
            if (this.getOwner() != null)
                if ((this.getOwner().getMasterSlave() & RecordOwner.MASTER) == 0) // Is not a client environment
            return false;   // Don't run this listener in a server-only environment
        if ((this.getMasterSlaveFlag() & FileListener.RUN_IN_MASTER) == 0) // Is NOT enabled in a CLIENT only environment
            if (this.getOwner() != null)
                if ((this.getOwner().getMasterSlave() & RecordOwner.SLAVE) == 0) // Is not a server environment
            return false;   // Don't run this listener in a client-only environment
        return super.isEnabled();
    }
    /**
     * Setup the initial key position in this record... Save it!
     */
    public void doInitialKey()
    {
        FileListener nextListener = (FileListener)this.getNextEnabledListener();
        if (nextListener != null)
        {
            boolean bOldState = nextListener.setEnabledListener(false);  // Don't allow it to be called again
            nextListener.doInitialKey();
            nextListener.setEnabledListener(bOldState);
        }
        else if (this.getOwner() != null)
            this.getOwner().doInitialKey();
    }
    /**
     * Setup the end key position in this record.
     */
    public void doEndKey()
    {
        FileListener nextListener = (FileListener)this.getNextEnabledListener();
        if (nextListener != null)
        {
            boolean bOldState = nextListener.setEnabledListener(false);  // Don't allow it to be called again
            nextListener.doEndKey();
            nextListener.setEnabledListener(bOldState);
        }
        else if (this.getOwner() != null)
            this.getOwner().doEndKey();
    }
    /**
     * Called when a error happens on a file operation, return the error code, or fix the problem.
     * @param iChangeType The type of change that occurred.
     * @param iErrorCode The error code from the previous listener.
     * @return The new error code.
     */
    public int doErrorReturn(int iChangeType, int iErrorCode)
    {
        return iErrorCode;
    }
    /**
     * Set up/do the local criteria.
     * @param strbFilter The SQL query string to add to.
     * @param bIncludeFileName Include the file name with this query?
     * @param vParamList The param list to add the raw data to (for prepared statements).
     * @return True if you should not skip this record (does a check on the local data).
     */
    public boolean doLocalCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
    {
        boolean bDontSkip = true;
        FileListener nextListener = (FileListener)this.getNextEnabledListener();
        if (nextListener != null)
        {
            boolean bOldState = nextListener.setEnabledListener(false);  // Don't allow it to be called again
            bDontSkip = nextListener.doLocalCriteria(strbFilter, bIncludeFileName, vParamList);
            nextListener.setEnabledListener(bOldState);
        }
        else if (this.getOwner() != null)
            bDontSkip = this.getOwner().doLocalCriteria(strbFilter, bIncludeFileName, vParamList);
        return bDontSkip; // Don't skip (no criteria)
    }
    /**
     * Set up/do the remote criteria.
     * @param strbFilter The SQL query string to add to.
     * @param bIncludeFileName Include the file name with this query?
     * @param vParamList The param list to add the raw data to (for prepared statements).
     * @return True if you should not skip this record (does a check on the local data).
     */
    public boolean doRemoteCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
    {
        boolean bDontSkip = true;
        FileListener nextListener = (FileListener)this.getNextEnabledListener();
        if (nextListener != null)
        {
            boolean bOldState = nextListener.setEnabledListener(false);  // Don't allow it to be called again
            bDontSkip = nextListener.doRemoteCriteria(strbFilter, bIncludeFileName, vParamList);
            nextListener.setEnabledListener(bOldState);
        }
        else if (this.getOwner() != null)
            bDontSkip = this.getOwner().doRemoteCriteria(strbFilter, bIncludeFileName, vParamList);
        return bDontSkip; // Don't skip (no criteria)
    }
    /**
     * Called when a new blank record is required for the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)   // init this field override for other value
    {
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     * ADD_TYPE - Before a write.
     * UPDATE_TYPE - Before an update.
     * DELETE_TYPE - Before a delete.
     * AFTER_UPDATE_TYPE - After a write or update.
     * LOCK_TYPE - Before a lock.
     * SELECT_TYPE - After a select.
     * DESELECT_TYPE - After a deselect.
     * MOVE_NEXT_TYPE - After a move.
     * AFTER_REQUERY_TYPE - Record opened.
     * SELECT_EOF_TYPE - EOF Hit.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {   // Return an error to stop the change
    	return super.doRecordChange(field, iChangeType, bDisplayOption);
    }
    /**
     * Called when a valid record is read from the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption) // init this field override for other value
    {
    }
    /**
     * Compare these fields using this compare string and setup
     * the params (if String given).
     * @param recordField The field in this record to compare.
     * @param strCompare the data string to compare to.
     * @param strSeekSign The SQL Seek Sign.
     * @param strbFilter The SQL query string to add to.
     * @param bIncludeFileName Include the file name with this query?
     * @param vParamList The param list to add the raw data to (for prepared statements).
     * @return true if the comparison is valid (For local Criteria or LTable).
     */
    public boolean fieldCompare(BaseField recordField, String strCompare, String strSeekSign, StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
    {
        if (strbFilter != null)
        { // Set up the SQL search string
            boolean bAddAnd = true;
            if (strbFilter.length() == 0)
                bAddAnd = false;
            if (bAddAnd) if ((strbFilter.charAt(strbFilter.length() - 1) == '('))
                bAddAnd = false;
            if (bAddAnd) if ((strbFilter.length() > 4) &&
                    (strbFilter.charAt(strbFilter.length() - 4) == ' ') &&
                    (strbFilter.charAt(strbFilter.length() - 3) == 'O') &&
                    (strbFilter.charAt(strbFilter.length() - 2) == 'R') &&
                    (strbFilter.charAt(strbFilter.length() - 1) == ' '))
                bAddAnd = false;
            if (bAddAnd)
                strbFilter.append(" AND ");
            String strSign = strSeekSign;
            if ((strSeekSign == null) || (strSeekSign.equals("==")))
                strSign = DBConstants.EQUALS;
            strbFilter.append(recordField.getFieldName(true, bIncludeFileName, true) + " " + recordField.getSQLFilter(strSign, strCompare, true));
            return true;
        }
        else
        {
            String strRecord = recordField.getString();
            if ((strCompare == null) || (strRecord == null))
            {
                if (strRecord == null)
                    return ((strCompare == null) || (strCompare.length() == 0));
                return (recordField.isNull());
            }
            int compare = strRecord.compareTo(strCompare);
            if ((strSeekSign.equals("!=")) || (strSeekSign.equals(NOT_EQUAL)))
                return (compare != 0);
            if (strSeekSign.equals(GREATER_THAN))
                return (compare > 0);
            if (strSeekSign.equals(LESS_THAN))
                return (compare < 0);
            if (strSeekSign.equals(GREATER_THAN_EQUAL))
                return (compare >= 0);
            if (strSeekSign.equals(LESS_THAN_EQUAL))
                return (compare <= 0);
        //  All Others
            return (compare == 0);
        }
    }
    public static final String EQUALS = DBConstants.EQUALS;
    public static final String NOT_EQUAL = "<>";
    public static final String LESS_THAN_EQUAL = "<=";
    public static final String GREATER_THAN_EQUAL = ">=";
    public static final String LESS_THAN = "<";
    public static final String GREATER_THAN = ">";
    /**
     * Compare these fields using this compare string
     *  and setup the params (if String given).
     * @param recordField The field in this record to compare.
     * @param compareField the field to compare to.
     * @param strSeekSign The SQL Seek Sign.
     * @param strbFilter The SQL query string to add to.
     * @param bIncludeFileName Include the file name with this query?
     * @param vParamList The param list to add the raw data to (for prepared statements).
     * @return true if the comparison is valid (For local Criteria or LTable).
     */
    public boolean fieldCompare(BaseField recordField, BaseField compareField, String strSeekSign, StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
    {
        if (strbFilter != null)
        { // Set up the SQL search string
            boolean bAddAnd = true;
            if (strbFilter.length() == 0)
                bAddAnd = false;
            if (bAddAnd) if ((strbFilter.charAt(strbFilter.length() - 1) == '('))
                bAddAnd = false;
            if (bAddAnd) if ((strbFilter.length() > 4) &&
                    (strbFilter.charAt(strbFilter.length() - 4) == ' ') &&
                    (strbFilter.charAt(strbFilter.length() - 3) == 'O') &&
                    (strbFilter.charAt(strbFilter.length() - 2) == 'R') &&
                    (strbFilter.charAt(strbFilter.length() - 1) == ' '))
                bAddAnd = false;
            if (bAddAnd)
                strbFilter.append(" AND ");
            String strCompare = null;
            if (vParamList != null) if ((!compareField.isNull()) || (recordField.isNullable() == false))
            {
                strCompare = "?";
                vParamList.add(compareField);
            }
            strbFilter.append(recordField.getFieldName(true, bIncludeFileName, true) + compareField.getSQLFilter(strSeekSign, strCompare, true));   //**FIX THIS**
            return true;
        }
        else
        {
            int compare = recordField.compareTo(compareField);
            if ((strSeekSign.equals("!=")) || (strSeekSign.equals(NOT_EQUAL)))
                return (compare != 0);
            if (strSeekSign.equals(GREATER_THAN))
                return (compare > 0);
            if (strSeekSign.equals(LESS_THAN))
                return (compare < 0);
            if (strSeekSign.equals(GREATER_THAN_EQUAL))
                return (compare >= 0);
            if (strSeekSign.equals(LESS_THAN_EQUAL))
                return (compare <= 0);
        //  All Others
            return (compare == 0);
        }
    }
    /**
     * Marshall all of this listener's params to pass to mirror copy to initialize a new object.
     * Note: Do not call super.initRemoteSkel() when you override, because you only
     * call this.init(xxx) one time per object.
     * @param daOut The output stream to marshal the data to.
     */
    public void initRemoteStub(ObjectOutputStream daOut)
    {
    }
    /**
     * Use these marshalled params to initialize this object.
     * Note: Do not call super.initRemoteSkel() when you override, because you only
     * call this.init(xxx) one time per object.
     * @param daIn The input stream to unmarshal the data from.
     */
    public void initRemoteSkel(ObjectInputStream daIn)
    {
        this.init(null);        // No overriding methods, call default init method.
    }
    /**
     * Encode and write this field's data to the stream.
     * @param daOut The output stream to marshal the data to.
     * @param converter The field to serialize.
     */
    public void writeField(ObjectOutputStream daOut, Converter converter) throws IOException
    {
        String strFieldName = DBConstants.BLANK;
        if (converter != null) if (converter.getField() != null)
            strFieldName = converter.getField().getClass().getName();
        Object data = null;
        if (converter != null)
            data = converter.getData();
        daOut.writeUTF(strFieldName);
        daOut.writeObject(data);
    }
    /**
     * Decode and read this field from the stream.
     * Will create a new field, init it and set the data if the field is not passed in.
     * @param daIn The input stream to unmarshal the data from.
     * @param fldCurrent The field to unmarshall the data into (optional).
     */
    public BaseField readField(ObjectInputStream daIn, BaseField fldCurrent) throws IOException, ClassNotFoundException
    {
        String strFieldName = daIn.readUTF();
        Object objData = daIn.readObject();
        if (fldCurrent == null) if (strFieldName.length() > 0)
        {
        	fldCurrent = (BaseField)ClassServiceUtility.getClassService().makeObjectFromClassName(strFieldName);
        	if (fldCurrent != null)
        	{
                fldCurrent.init(null, null, DBConstants.DEFAULT_FIELD_LENGTH, null, null);
                if (this.getOwner() != null)    // Make sure it is cleaned up
                    this.getOwner().addListener(new RemoveConverterOnCloseHandler(fldCurrent));
        	}
        }
        if (fldCurrent != null)
            fldCurrent.setData(objData);
        return fldCurrent;
    }
    /**
     * Enable state is dependent on this listener.
     * @param dependentStateListener The listener to get the enabled state from.
     */
    public void setDependentStateListener(BaseListener dependentStateListener)
    {
        if (!(dependentStateListener instanceof FileListener))
            dependentStateListener = null;
        else if (this.getOwner() != ((FileListener)dependentStateListener).getOwner())
            dependentStateListener = null;
        super.setDependentStateListener(dependentStateListener);
    }
}
