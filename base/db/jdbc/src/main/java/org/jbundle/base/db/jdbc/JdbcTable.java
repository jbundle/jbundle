/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.jdbc;

/**
 * @(#)JDBCTable.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.KeyField;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.CounterField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.buff.str.StrBuffer;


/**
 * JDBCTable - Table for JDBC queries.
 *
 * @version 1.0.0
 * @author    Don Corley
 *
 */
public class JdbcTable extends BaseTable
{
    /**
     * Used for open/queries.
     */
    private Statement m_queryStatement = null;
    /**
     * Used for Updates, Inserts, and Deletes (prepared statement).
     */
    private Statement m_updateStatement = null;
    /**
     * Used for Auto sequence, and Seek (prepared statement).
     */
    private Statement m_seekStatement = null;
    /**
     * Used for Updates, Inserts, and Deletes (prepared statement).
     */
    private ResultSet m_seekResultSet = null;
    /**
     * Used for Updates, Inserts, and Deletes (prepared statement).
     */
    private ResultSet m_selectResultSet = null;
    /**
     * Used for Updates, Inserts, and Deletes (prepared statement).
     */
    private ResultSet m_autoSequenceResultSet = null;
    /**
     * Current resultSet type.
     */
    private int m_iResultSetType = -1;
    /**
     * Last SQL string.
     */
    protected String m_strStmtLastSQL = null;
    /**
     * Last seek SQL string.
     */
    protected String m_strStmtLastSeekSQL = null;
    /**
     * Last update SQL string.
     */
    protected String m_strLastSQL = null;
    /**
     * true: SELECT * FROM...; false: SELECT FLDNAME, FLDNAME2 FROM...
     */
    protected boolean m_bSelectAllFields = false;
    /**
     * Current select field column.
     */
    protected int m_iColumn = -1;
    /**
     * The field columns.
     */
    protected int m_rgiFieldColumns[] = null;
        // These are the params needed for a prepared statement:
    /**
     * Current parameter column.
     */
    protected int m_iNextParam = -1;
    /**
     * result set row 0 = First row.
     */
    protected int m_iRow = -1;
    /**
     * Last valid row.
     */
    protected int m_iEOFRow = Integer.MAX_VALUE;
    /**
     * Last order.
     */
    protected int m_iLastOrder = -1;
    /**
     * Last modified bookmark.
     */
    protected Object m_LastModifiedBookmark = null;
    /**
     * In recursive call.
     */
    protected boolean m_bInRecursiveCall = false;
    /**
     *  If true, close all statements after use. This is a requirement for data source pools. (recommend true)
     */
    public static final boolean CLEANUP_STATEMENTS = true;
    /**
     *  If true, share statements between auto sequence, seek, and move next queries. (recommend false - but false not tested)
     */
    public static final boolean SHARE_STATEMENTS = false;
    
    /**
     * Constructor.
     */
    public JdbcTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public JdbcTable(BaseDatabase database, Record record)
    {
        this();
        this.init(database, record);
    }
    /**
     * Init this table.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public void init(BaseDatabase database, Record record)
    {
        super.init(database, record);
        m_iRow = -1;        // resultSet row 0 = First row.
        m_iEOFRow = Integer.MAX_VALUE;  // Last valid row.
        m_iLastOrder = -1;
    }
    /**
     * Free this object.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Close this table.
     */
    public void close()
    {
        super.close();  // Write the current record, etc.

        try {
            if (this.lockOnDBTrxType(null, DBConstants.AFTER_REFRESH_TYPE, false))  // AFTER_REFRESH_TYPE = Close since there is no close
                this.unlockIfLocked(this.getRecord(), null);    // Release any locks
        } catch (DBException ex)    {
            ex.printStackTrace();
        }

        try   {
            if (m_seekResultSet != null)
                m_seekResultSet.close();
            m_seekResultSet = null;
            if (m_selectResultSet != null)
                m_selectResultSet.close();
            m_selectResultSet = null;
            if (m_autoSequenceResultSet != null)
                m_autoSequenceResultSet.close();
            m_autoSequenceResultSet = null;
            if (m_queryStatement != null)
                m_queryStatement.close();
            m_queryStatement = null;
            if (m_updateStatement != null)
                m_updateStatement.close();
            m_updateStatement = null;
            if (m_seekStatement != null)
                m_seekStatement.close();
            m_seekStatement = null;
        } catch (SQLException e)    {
            e.printStackTrace();    // No error on close
        }
        m_iRow = -1;
        m_iEOFRow = Integer.MAX_VALUE;  // Last valid row.
        m_strStmtLastSQL = null;    // Last SQL string.
        m_strLastSQL = null;    // Last SQL string.
        m_strStmtLastSeekSQL = null;    // Last SQL string.
    }
    /**
     * Move the data source buffer to all the fields.
     *  <p />In addition to the regular call, save the primary key
     * field in the temp area for a possible keyed update/delete.
     * @return An error code.
     * @exception DBException File exception.
     */
    public int dataToFields(Rec record) throws DBException
    {
        m_iColumn = 1;
        int iErrorCode = super.dataToFields(record);

        KeyArea keyArea = this.getRecord().getKeyArea(0); // Primary index
        keyArea.setupKeyBuffer(null, DBConstants.TEMP_KEY_AREA);        // Save keys for possible update/delete
        if (this.getResultSetType() != DBConstants.SQL_SELECT_TYPE)
            if (CLEANUP_STATEMENTS)
                this.setResultSet(null, this.getResultSetType());  // Close this statement

        return iErrorCode;
    }
    /**
     * Move the output buffer to this field.
     * <p />This is a little complicated:<br />
     * The first time through, use resultSet.findColumn(x) to lookup the column.<br />
     * Then call field.moveSQLToField(resultSet, column).<br />
     * The bump the column number.
     * @param field The field to move the current data to.
     * @return The error code (From field.setData()).
     * @exception DBException File exception.
     */
    public int dataToField(Field fieldInfo) throws DBException
    {
        BaseField field = (BaseField)fieldInfo;
        if (!field.isSelected())
        {   // Not selected, don't move data
            return field.initField(DBConstants.DONT_DISPLAY);
        }
        // Get the fields in the recordset
        int iColumn = m_iColumn;    // By default
        ResultSet resultSet = (ResultSet)this.getResultSet();
        if (m_bSelectAllFields)
        {   // Special case SELECT * (must find this field in the output column)
            if (m_rgiFieldColumns == null)
            {
                Record record = this.getRecord();
                int iFieldCount = record.getFieldCount();
                m_rgiFieldColumns = new int[iFieldCount+1];
                for (int i = 1; i <= iFieldCount; i++)
                    m_rgiFieldColumns[i] = -1;
                m_iColumn = 1;  // First column in array
            }
            iColumn = m_rgiFieldColumns[m_iColumn];
            if (iColumn == -1)
            {
                boolean bIsQueryRecord = false;     //this.getRecord().isQueryRecord(); // For some some reason, can't pass filefile.fieldname
                String strFieldName = field.getFieldName(true, bIsQueryRecord);
                try {
                    iColumn = resultSet.findColumn(strFieldName);
                } catch (Exception e)    {
                    Utility.getLogger().warning( e.getMessage() + "JDBC Table/dataToField Field = [" + field.getFieldName(false, true) + "]");
                    throw this.getDatabase().convertError(e);
                }
                if (iColumn == 0)
                {   // Could not find field name in query, look through my fields
                    Record record = this.getRecord();
                    int iFieldCount = record.getFieldCount();
                    for (int i = 0; i < iFieldCount; i++)
                    {
                        if (record.getField(i).isSelected())
                            iColumn++;  // SQL columns start at 1
                        if (field == record.getField(i))
                            break;
                    }
                }
                m_rgiFieldColumns[m_iColumn] = iColumn;
            }
        }
        try   {
            field.moveSQLToField(resultSet, iColumn);
        } catch (SQLException e)    {
            if ("No data found".equalsIgnoreCase(e.getMessage()))
                if (this.getRecord().isQueryRecord())
            { // With query records, I probably looked up the first occurance of the same field name - find the next
                try { // HACK - with the current JDBC, I can't retrieve the field by table.field, so If there is a dup, I just get the first and second.
                    ResultSetMetaData md = resultSet.getMetaData();
                    boolean bFirstFound = false;
                    int iColumnCount = md.getColumnCount();
                    String strFieldName = field.getFieldName(true, false);
                    for (iColumn = 1; iColumn <= iColumnCount; iColumn++)
                    {
                        String strColumnName = md.getColumnName(iColumn);
                        if (strFieldName.equals(strColumnName))
                        {
                            if (bFirstFound)
                            {
                                m_rgiFieldColumns[m_iColumn] = iColumn;
                                field.moveSQLToField(resultSet, iColumn);
                                break;
                            }
                            bFirstFound = true;
                        }
                    }
                } catch (SQLException ex) {
                    Utility.getLogger().warning( ex.getMessage() + "JDBC Table/255 Field = [" + field.getFieldName() + "]");
                    throw this.getDatabase().convertError(ex);
                }
            }
            else
            {
                Utility.getLogger().warning(e.getMessage() + "JdbcTable/261 Field = [" + field.getFieldName() + "]");
                throw this.getDatabase().convertError(e);
            }
        }
        m_iColumn++;    // Next column.
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Get this statement type
     */
    public Statement getStatement(int iType)
    {
        switch (iType)
        {
        case DBConstants.SQL_UPDATE_TYPE:
        case DBConstants.SQL_INSERT_TABLE_TYPE:
        case DBConstants.SQL_DELETE_TYPE:
            return m_updateStatement;
        case DBConstants.SQL_AUTOSEQUENCE_TYPE:
        case DBConstants.SQL_SEEK_TYPE:
            if (!SHARE_STATEMENTS)
                return m_seekStatement;
        case DBConstants.SQL_SELECT_TYPE:
        case DBConstants.SQL_CREATE_TYPE:
        default:
            return m_queryStatement;
        }
    }
    /**
     * Get this statement type
     */
    public void setStatement(Statement statement, int iType)
    {
        try {
            if (this.getStatement(iType) != null)
                this.getStatement(iType).close();
            if (statement == null)
                this.setLastSQLStatement(null, iType);
        } catch (SQLException e) {
            e.printStackTrace();    // Never
        }

        switch (iType)
        {
        case DBConstants.SQL_UPDATE_TYPE:
        case DBConstants.SQL_INSERT_TABLE_TYPE:
        case DBConstants.SQL_DELETE_TYPE:
            m_updateStatement = statement;
            break;
        case DBConstants.SQL_AUTOSEQUENCE_TYPE:
        case DBConstants.SQL_SEEK_TYPE:
            if (!SHARE_STATEMENTS)
            {
                m_seekStatement = statement;
                break;
            }
        case DBConstants.SQL_SELECT_TYPE:
        case DBConstants.SQL_CREATE_TYPE:
        default:
            m_queryStatement = statement;
        }
    }
    /**
     * Get the last SQL string for this statement type
     */
    public String getLastSQLStatement(int iType)
    {
        switch (iType)
        {
        case DBConstants.SQL_UPDATE_TYPE:
        case DBConstants.SQL_INSERT_TABLE_TYPE:
        case DBConstants.SQL_DELETE_TYPE:
            return m_strLastSQL;
        case DBConstants.SQL_AUTOSEQUENCE_TYPE:
        case DBConstants.SQL_SEEK_TYPE:
            if (!SHARE_STATEMENTS)
                return m_strStmtLastSeekSQL;           
        case DBConstants.SQL_SELECT_TYPE:
        case DBConstants.SQL_CREATE_TYPE:
        default:
            return m_strStmtLastSQL;
        }
    }
    /**
     * Set the last SQL string for this statement type
     */
    public void setLastSQLStatement(String strStatement, int iType)
    {
        switch (iType)
        {
        case DBConstants.SQL_UPDATE_TYPE:
        case DBConstants.SQL_INSERT_TABLE_TYPE:
        case DBConstants.SQL_DELETE_TYPE:
            m_strLastSQL = strStatement;
            break;
        case DBConstants.SQL_AUTOSEQUENCE_TYPE:
        case DBConstants.SQL_SEEK_TYPE:
            if (!SHARE_STATEMENTS)
            {
                m_strStmtLastSeekSQL = strStatement;
                break;
            }
        case DBConstants.SQL_SELECT_TYPE:
        case DBConstants.SQL_CREATE_TYPE:
        default:
            m_strStmtLastSQL = strStatement;
        }
    }
    /**
     * Get this statement type
     */
    public ResultSet getResultSet()
    {
        return this.getResultSet(this.getResultSetType());
    }
    /**
     * Get this statement type
     */
    public ResultSet getResultSet(int iType)
    {
        switch (iType)
        {
            case DBConstants.SQL_SEEK_TYPE:
                if (!SHARE_STATEMENTS)
                    return m_seekResultSet;
            case DBConstants.SQL_SELECT_TYPE:
            case DBConstants.SQL_CREATE_TYPE:
                return m_selectResultSet;
            case DBConstants.SQL_AUTOSEQUENCE_TYPE:
                return m_autoSequenceResultSet;
            case DBConstants.SQL_UPDATE_TYPE:
            case DBConstants.SQL_INSERT_TABLE_TYPE:
            case DBConstants.SQL_DELETE_TYPE:
            default:  // Never
                break;
        }
        return null;
    }
    /**
     * Get this statement type
     */
    public int getResultSetType()
    {
        return m_iResultSetType;
    }
    /**
     * Set the ResultSet for this select or seek statement type.
     * @param resultSet The resultSet to set.
     * @return The old resultSet.
     */
    public void setResultSet(ResultSet resultSet, int iType)
    {
        if (this.getResultSet(iType) != null)
        {   // If this is a new resultSet for my current statement, close the old resultSet.
            try   {
                this.getResultSet(iType).close();
            } catch (SQLException e)    {
                e.printStackTrace();    // Never
            }            
        }
        switch (iType)
        {
            case DBConstants.SQL_SEEK_TYPE:
                if (!SHARE_STATEMENTS)
                {
                    m_seekResultSet = resultSet;
                    break;
                }
            case DBConstants.SQL_SELECT_TYPE:
            case DBConstants.SQL_CREATE_TYPE:
                m_selectResultSet = resultSet;
                break;
            case DBConstants.SQL_AUTOSEQUENCE_TYPE:
                m_autoSequenceResultSet = resultSet;
                break;
            case DBConstants.SQL_UPDATE_TYPE:
            case DBConstants.SQL_INSERT_TABLE_TYPE:
            case DBConstants.SQL_DELETE_TYPE:
            default:
                // Never
                break;
        }
        m_iResultSetType = iType;
        if (iType == DBConstants.SQL_SELECT_TYPE)
        {
            m_iRow = -1;        // Starting from a new query
            m_iEOFRow = Integer.MAX_VALUE;
        }
    }
    /**
     * Move all the fields to the output buffer.
     * @exception DBException File exception.
     */
    public void fieldsToData(Rec record) throws DBException
    {
        super.fieldsToData(record);
    }
    /**
     * Move this Field's data to the record area (Override this for non-standard buffers).
     * For SQL, DO NOT Move the data. wait until the prepare statement is executed first!
     * @param field The field to move to the data area.
     * @exception DBException File exception.
     */
    public void fieldToData(Field field) throws DBException
    {
        // Do not move the fields yet (must prepare the statement first!)
    }
    /**
     * Open this table (re-query the table).
     * @exception DBException Open errors passed from SQL.
     */
    public void doOpen() throws DBException
    {
        if (this.lockOnDBTrxType(null, DBConstants.AFTER_REQUERY_TYPE, false))  // AFTER_REQUERY_TYPE = Open since there is no close
            this.unlockIfLocked(this.getRecord(), null);    // Release any locks

        super.doOpen();

        m_iLastOrder = this.getRecord().getDefaultOrder();
        m_iRow = -1;
        m_iEOFRow = Integer.MAX_VALUE;
    }
    /**
     * Create/Clear the current object (Always called from the record class).
     * Don't need to do anything special for SQL tables.
     * @exception DBException File exception.
     */
    public void doAddNew() throws DBException
    {
        if (this.lockOnDBTrxType(null, DBConstants.ADD_TYPE, false))  // ADD_TYPE = Add new; Should I do the unlock in my code?
            this.unlockIfLocked(this.getRecord(), null);    // Release any locks
    }
    /**
     * Delete this record (Always called from the record class).
     * Do a SQL delete.
     * @exception DBException INVALID_RECORD - Attempt to delete a record that is not current.
     */
    public void doRemove() throws DBException
    {
        Object bookmark = this.getRecord().getHandle(DBConstants.BOOKMARK_HANDLE);
        String strRecordset = this.getRecord().getSQLDelete(SQLParams.USE_INSERT_UPDATE_LITERALS);
        this.executeUpdate(strRecordset, DBConstants.SQL_DELETE_TYPE);
        if (m_iRow != -1)
            m_iRow--;   // Just in case I'm in an active query this will keep requeries correct
        if (this.lockOnDBTrxType(null, DBConstants.AFTER_DELETE_TYPE, false))  // Should I do the unlock in my code?
            this.unlockIfLocked(this.getRecord(), bookmark);
    }
    /**
     * Lock the current record.
     * This method responds differently depending on what open mode the record is in:
     * OPEN_DONT_LOCK - A physical lock is not done. This is usually where deadlocks are possible
     * (such as screens) and where transactions are in use (and locks are not needed).
     * OPEN_LOCK_ON_EDIT - Holds a lock until an update or close. (Update crucial data, or hold records for processing)
     * Returns false is someone already has a lock on this record.
     * OPEN_WAIT_FOR_LOCK - Don't return from edit until you get a lock. (ie., Add to the total).
     * Returns false if someone has a hard lock or time runs out.
     * @return true if successful, false is lock failed.
     * @exception DBException FILE_NOT_OPEN
     * @exception DBException INVALID_RECORD - Record not current.
     */
    public int doEdit() throws DBException
    {
        Record record = this.getRecord();
        if (record.getKeyArea(0) != null)
        {   // Record was set up using a cached copy, restore the primary key so you can update it!
            KeyArea keyArea = record.getKeyArea(0); // Primary index
            keyArea.setupKeyBuffer(null, DBConstants.TEMP_KEY_AREA);        // Save keys for possible update/delete
        }
        if (this.lockOnDBTrxType(null, DBConstants.LOCK_TYPE, true))
            return this.lockCurrentRecord();
        else
            return DBConstants.NORMAL_RETURN;
    }
    /**
     * Move the position of the record.
     * Move the current position and read the record (optionally read several records).
     * This method is optomized for forward sequential reads.
     * @param iRelPosition relative Position to read the next record.
     * @return NORMAL_RETURN - The following are NOT mutually exclusive
     * @return RECORD_INVALID
     * @return RECORD_AT_BOF
     * @return RECORD_AT_EOF
     * @exception DBException File exception.
     */
    public int doMove(int iRelPosition) throws DBException
    {
        // Note: actually, I should check if (iRelPos == FIRST) rec.moveFirst(),
        //  but, I use the same constants for First/Last as JDBC
        if (this.getRecord().getDefaultOrder() != m_iLastOrder)
        {   // Order was changed
            int iCurrentRow = -1;
            if (m_iLastOrder == -1)
                iCurrentRow = m_iRow;   // Special case... A seek was done between the last read, so I need to return to the current row instead of the first row
            this.setResultSet(null, DBConstants.SQL_SELECT_TYPE);
            if (iCurrentRow != -1)
                if (iRelPosition != DBConstants.FIRST_RECORD)
                    if (iRelPosition != DBConstants.LAST_RECORD)
                        iRelPosition = iRelPosition + iCurrentRow + 1;   // Special case... This will set the target position to the correct (next) row
            m_iLastOrder = this.getRecord().getDefaultOrder();
            m_iEOFRow = Integer.MAX_VALUE;
        }
        if ((iRelPosition == DBConstants.PREVIOUS_RECORD) && (m_iRow == -1))
            iRelPosition = DBConstants.LAST_RECORD;
        int iTargetPosition = m_iRow + iRelPosition;
        if (iRelPosition == DBConstants.FIRST_RECORD)
            iTargetPosition = 0;    // First Record
        if (iRelPosition == DBConstants.LAST_RECORD)
            iTargetPosition = m_iEOFRow;    // Last Record
        if (m_iRow != -1) 
            if (iTargetPosition != -1)
                if (iTargetPosition <= m_iRow)
                    this.setResultSet(null, DBConstants.SQL_SELECT_TYPE);   // Have to start from the beginning
    // Do the move
        ResultSet resultSet = (ResultSet)this.getResultSet();
        try {
            if ((resultSet == null) || (resultSet.isClosed())) {
                Vector<BaseField> vParamList = new Vector<BaseField>();
                String strRecordset = this.getRecord().getSQLQuery(
                        SQLParams.USE_SELECT_LITERALS, vParamList);
                resultSet = this.executeQuery(strRecordset,
                        DBConstants.SQL_SELECT_TYPE, vParamList);
                this.setResultSet(resultSet, DBConstants.SQL_SELECT_TYPE);
                vParamList.removeAllElements();
            }
        } catch (Exception e) {
            e.printStackTrace();    // Never
        }
        if (iRelPosition == DBConstants.LAST_RECORD) if (m_iEOFRow == Integer.MAX_VALUE)
        {   // Haven't found EOF yet... find it.
            iTargetPosition = m_iEOFRow;    // Last Record
            this.readNextToTarget(iTargetPosition, iRelPosition); // Get EOF
            iTargetPosition = m_iEOFRow;    // Move to last row
            Vector<BaseField> vParamList = new Vector<BaseField>();
            String strRecordset = this.getRecord().getSQLQuery(SQLParams.USE_SELECT_LITERALS, vParamList);
            resultSet = this.executeQuery(strRecordset, DBConstants.SQL_SELECT_TYPE, vParamList);
            this.setResultSet(resultSet, DBConstants.SQL_SELECT_TYPE);
            vParamList.removeAllElements();
        }
        int iRecordStatus = this.readNextToTarget(iTargetPosition, iRelPosition);
    // Done!
        if (m_iRow != iTargetPosition)
        {
            if (iTargetPosition < 0) if (m_iRow == -1)
                iRecordStatus |= DBConstants.RECORD_AT_BOF;
            if (CLEANUP_STATEMENTS)
                this.setResultSet(null, DBConstants.SQL_SELECT_TYPE);  // Close this statement (at EOF)
        }
        return iRecordStatus;
    }
    /**
     * Read the record that matches this record's current key.
     * @param strSeekSign The seek sign (defaults to '=').
     * @return true if successful.
     * @exception DBException File exception.
     */
    public boolean doSeek(String strSeekSign) throws DBException
    {
        Vector<BaseField> vParamList = new Vector<BaseField>();
        try   {
            if (this.lockOnDBTrxType(null, DBConstants.SELECT_TYPE, false))  // SELECT = seek for now; Should I do the unlock in my code?
                this.unlockIfLocked(this.getRecord(), null);    // Release any locks

            // Submit a query, creating a ResultSet object
            this.setResultSet(null, DBConstants.SQL_SEEK_TYPE);
            String strRecordset = this.getRecord().getSQLSeek(strSeekSign, SQLParams.USE_SELECT_LITERALS, vParamList);
            ResultSet resultSet = this.executeQuery(strRecordset, DBConstants.SQL_SEEK_TYPE, vParamList);
            this.setResultSet(resultSet, DBConstants.SQL_SEEK_TYPE);
            boolean more = resultSet.next();
            if (SHARE_STATEMENTS)
                m_iLastOrder = -1;      // This will force a requery next time if there is an active query
            return more;    // Success if at least one record
        } catch (SQLException ex) {
            throw this.getDatabase().convertError(ex);
        } finally {
            vParamList.removeAllElements();
        }
    }
    /**
     * Read the record given the ID to this persistent object.
     *<p />Note: ObjectID and DataSource handles are the same as bookmark handles for SQL.
     * @param objectID java.lang.Object
     * @exception DBException File exception.
     */
    public boolean doSetHandle(Object objectID, int iHandleType) throws DBException
    {
        if (iHandleType == DBConstants.OBJECT_ID_HANDLE)
        {
            if (objectID instanceof String)     // It is okay to pass in a string, but convert it first!
            {
                try   {
                    objectID = new Integer(Converter.stripNonNumber((String)objectID));
                } catch (NumberFormatException ex)  {
                    objectID = new StrBuffer((String)objectID);
                }
            }
            iHandleType = DBConstants.BOOKMARK_HANDLE;
        }
        if (iHandleType == DBConstants.DATA_SOURCE_HANDLE)
            iHandleType = DBConstants.BOOKMARK_HANDLE;
        return super.doSetHandle(objectID, iHandleType);    // Same logic (for JDBC)
    }
    /**
     * Add this record (Always called from the record class).
     * <br />In most cases, autosequence is not supported, so I have to get the largest
     * sequence and set mine to the next highest before writing.
     * @exception DBException File exception.
     */
    public void doAdd(Record record) throws DBException
    {
        if (this.lockOnDBTrxType(null, DBConstants.AFTER_ADD_TYPE, false))  // Should I do the unlock in my code?
            this.unlockIfLocked(this.getRecord(), null);    // Release any locks
        m_LastModifiedBookmark = null;
        CounterField fldID = (CounterField)record.getCounterField();
        if (fldID != null)
            if (!fldID.isNull())
                if (DBConstants.FALSE.equalsIgnoreCase(this.getProperty(SQLParams.AUTO_SEQUENCE_ENABLED)))
                    fldID = null;   // Special case - autosequence is disabled, so write the field.
        if (fldID != null)
            fldID.setModified(false); // Never set a counter field
        if (fldID != null)
            if (!this.getDatabase().isAutosequenceSupport())
        { // If the db does not support identity, you must get the next larger key by doing a select desc on the primary key
            this.doAddAutosequence(record);     // Special code to get next sequence
            return;
        }
        String strRecordset = record.getSQLInsert(SQLParams.USE_INSERT_UPDATE_LITERALS);
        int iType = DBConstants.SQL_INSERT_TABLE_TYPE;
        this.executeUpdate(strRecordset, iType);
    }
    /**
     * Add this record.
     * This is the special logic to add a record without db autosequence.
     * <br />Read the largest key.
     * <br />Write the next largest (and loop until you get one)
     * <br />Save the counter for possible get last modified call.
     * @exception DBException File exception.
     */
    public void doAddAutosequence(Record record) throws DBException
    {
        record = record.getBaseRecord();    // Must operate on the raw table
        
        String strRecordset = null;
        m_LastModifiedBookmark = null;
        CounterField fldID = (CounterField)record.getCounterField();
        // First step - get the largest current key
    // Save current order and ascending/descending and selection
        int iOrigOrder = record.getDefaultOrder();
        boolean bOrigDirection = record.getKeyArea(DBConstants.MAIN_KEY_AREA).getKeyField(DBConstants.MAIN_KEY_FIELD).getKeyOrder();
        boolean bCounterSelected = fldID.isSelected();
        boolean brgSelected[] = new boolean[record.getFieldCount()];
        for (int iFieldSeq = DBConstants.MAIN_FIELD; iFieldSeq <= record.getFieldCount() + DBConstants.MAIN_FIELD - 1; iFieldSeq++)
        {
            brgSelected[iFieldSeq] = record.getField(iFieldSeq).isSelected();
            record.getField(iFieldSeq).setSelected(false);  // De-select all fields
        }
        fldID.setSelected(true);    // Only select the counter field
    // Set reverse order, descending
        record.setKeyArea(DBConstants.MAIN_KEY_AREA);
        record.getKeyArea(DBConstants.MAIN_KEY_AREA).getKeyField(DBConstants.MAIN_KEY_FIELD).setKeyOrder(DBConstants.DESCENDING);
        boolean[] rgbEnabled = record.setEnableListeners(false);    // Temporarily disable behaviors

        strRecordset = record.getSQLQuery(false, null);
        ResultSet resultSet = this.executeQuery(strRecordset, DBConstants.SQL_AUTOSEQUENCE_TYPE, null);
        this.setResultSet(resultSet, DBConstants.SQL_AUTOSEQUENCE_TYPE);
    // Set back, before I forget.
        record.setKeyArea(iOrigOrder);
        record.getKeyArea(DBConstants.MAIN_KEY_AREA).getKeyField(DBConstants.MAIN_KEY_FIELD).setKeyOrder(bOrigDirection);

        try   {
            m_iColumn = 1;
            boolean bMore = resultSet.next();
            if (!bMore)
            {
                int iStartingID = record.getStartingID();
                fldID.setValue(iStartingID - 1, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
            }
            else
                this.dataToField(fldID);    // Move the high value to the ID
            fldID.setModified(true);        // Just to be sure
        } catch (SQLException ex) {
            record.setEnableListeners(rgbEnabled); // Re-enable behaviors
            throw this.getDatabase().convertError(ex);
        } finally {
            for (int iFieldSeq = DBConstants.MAIN_FIELD; iFieldSeq <= record.getFieldCount() + DBConstants.MAIN_FIELD - 1; iFieldSeq++)
            {
                record.getField(iFieldSeq).setSelected(brgSelected[iFieldSeq]);
            }
            fldID.setSelected(bCounterSelected);
            this.setResultSet(null, DBConstants.SQL_AUTOSEQUENCE_TYPE);    // Close resultset. If I share statements with an active query, reset the active query (Ouch-serious performance hit)
        }
        if (!bCounterSelected)
            fldID.setSelected(true);    // Counter must be selected to write this record

        // Second step - bump the key and write until successful.
        int iCount = 0;
        while (iCount++ < MAX_DUPLICATE_ATTEMPTS)
        {
            fldID.setValue(fldID.getValue() + 1, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);  // Bump counter
            strRecordset = record.getSQLInsert(SQLParams.USE_INSERT_UPDATE_LITERALS);
            int iType = DBConstants.SQL_INSERT_TABLE_TYPE;
            int iRowsUpdated = 0;
            try   {
                iRowsUpdated = this.executeUpdate(strRecordset, iType);
            } catch (DBException ex)    {
                if (ex.getErrorCode() == DBConstants.DUPLICATE_COUNTER)   // Duplicate key
                {
                    Utility.getLogger().info("Duplicate key, bumping value");
                    iRowsUpdated = 0;
                }
                else
                {
                    if (!bCounterSelected)      // Set back to orig value
                        fldID.setSelected(bCounterSelected);
                    record.setEnableListeners(rgbEnabled); // Re-enable behaviors
                    throw ex;
                }
            }
            if (iRowsUpdated == 1)
                break;      // Success!!!
        }

        if (!bCounterSelected)      // Set back to orig value
            fldID.setSelected(bCounterSelected);
        record.setEnableListeners(rgbEnabled); // Re-enable behaviors

        if (iCount > MAX_DUPLICATE_ATTEMPTS)
            throw new DBException(DBConstants.DUPLICATE_KEY);  // Highly unlikely
        // Third step - Save the counter for possible getLastModified call
        m_LastModifiedBookmark = record.getHandle(DBConstants.BOOKMARK_HANDLE);
    }
    public static final int MAX_DUPLICATE_ATTEMPTS = 50;
    /**
     * Update this record (Always called from the record class).
     * @param record The record to update.
     * @exception DBException File exception.
     */
    public void doSet(Record record) throws DBException
    {
        if (this.lockOnDBTrxType(null, DBConstants.UPDATE_TYPE, true))
        {
            int iErrorCode = this.lockCurrentRecord();
            if ((iErrorCode == DBConstants.NORMAL_RETURN) || (iErrorCode == DBConstants.RECORD_CHANGED))
            {
                iErrorCode = record.refreshToCurrent(DBConstants.UPDATE_TYPE, true);
            }
        }
//        int iRowsUpdated = 0;
        Object bookmark = record.getHandle(DBConstants.BOOKMARK_HANDLE);
        CounterField fldID = (CounterField)record.getCounterField();
        if (fldID != null)
            if (record.isAutoSequence())
                fldID.setModified(false); // Never set a counter field
        String strRecordset = record.getSQLUpdate(SQLParams.USE_INSERT_UPDATE_LITERALS);
        int iType = DBConstants.SQL_UPDATE_TYPE;
            if (strRecordset != null)
                /*iRowsUpdated =*/ this.executeUpdate(strRecordset, iType);
//            else
//                iRowsUpdated = 1; // No changes
        if (this.lockOnDBTrxType(null, DBConstants.AFTER_UPDATE_TYPE, false))  // ADD_TYPE = Add new; Should I do the unlock in my code?
            this.unlockIfLocked(record, bookmark);
    }
    /**
     * Execute this SQL seek or open statement.
     * Note: This code uses prepared statements to optimize the call of the
     * same SQL query. Also, if the table doesn't exist, it can be created automatically
     * and re-do the call.
     * @param strSQL The SQL statement to prepare.
     * @param vParamList The list of params for the query.
     * @param iType the type of SQL statement.
     * @return number of rows updated.
     * @exception DBException Converts and returns SQLExceptions, or if no rows updated, throws INVALID_RECORD.
     */
    public ResultSet executeQuery(String strSQL, int iType, Vector<BaseField> vParamList)
        throws DBException
    {
        ResultSet resultSet = null;
        m_bSelectAllFields = false;
        m_rgiFieldColumns = null;
        try   {
            Utility.getLogger().info(strSQL);
            if (SQLParams.USE_SELECT_LITERALS)
            {
                if (this.getStatement(iType) instanceof PreparedStatement)
                    this.setStatement(null, iType); // Close old prepared statement
                if (this.getStatement(iType) == null)
                    this.setStatement(((JdbcDatabase)this.getDatabase()).getJDBCConnection().createStatement(), iType);
                resultSet = this.getStatement(iType).executeQuery(strSQL);
            }
            else
            {
                if (this.getStatement(iType) != null)
                {
                    if (!strSQL.equals(this.getLastSQLStatement(iType)))
                        this.setStatement(null, iType);   // Not the same as last time.
                }
                if (this.getStatement(iType) == null)
                    this.setStatement(((JdbcDatabase)this.getDatabase()).getJDBCConnection().prepareStatement(strSQL), iType);
                this.setLastSQLStatement(strSQL, iType);
                m_iNextParam = 1;           // First param row
                this.getRecord().getKeyArea(0); // Primary index
                if ((iType == DBConstants.SQL_SELECT_TYPE) || (iType == DBConstants.SQL_AUTOSEQUENCE_TYPE) || (iType == DBConstants.SQL_SEEK_TYPE)) // Have WHERE X=?
                {
                    if (vParamList != null)
                    {
                        for (Enumeration<BaseField> e = vParamList.elements() ; e.hasMoreElements() ;)
                        {
                            BaseField field = e.nextElement();
                            field.getSQLFromField((PreparedStatement)this.getStatement(iType), iType, m_iNextParam);
                            m_iNextParam++;
                        }
                    }
                }
                resultSet = ((PreparedStatement)this.getStatement(iType)).executeQuery();
            }
        } catch (SQLException e)    {
            DBException ex = this.getDatabase().convertError(e);
            if (this.createIfNotFoundError(ex))
            { // Table not found, the previous line created the table, re-do the query.
                this.getRecord().setOpenMode((this.getRecord().getOpenMode() | DBConstants.OPEN_DONT_CREATE));   // Only try to create once.
                return this.executeQuery(strSQL, iType, vParamList);
            }
            else if (ex.getErrorCode() == DBConstants.BROKEN_PIPE)
            { // If there is a pipe timeout or a broken pipe, try to re-establish the connection and try again
                if (!m_bInRecursiveCall)
                {
                    m_bInRecursiveCall = true;

                    this.close();
                    this.getDatabase().close();     // Next access will force an open.
                    ResultSet result = this.executeQuery(strSQL, iType, vParamList);

                    m_bInRecursiveCall = false;
                    return result;
                }
            }
            throw ex;
        }
        if (strSQL.indexOf('*') != -1)
        {
            m_bSelectAllFields = true;
            m_iColumn = 1;  // First column
        }
        return resultSet;
    }
    /**
     * Get/create the JDBC statement object.
     * @param strSQL The SQL statement to prepare.
     * @param iType the type of SQL statement.
     * @return number of rows updated.
     * @exception DBException Converts and returns SQLExceptions, or if no rows updated, throws INVALID_RECORD.
     */
    public int executeUpdate(String strSQL, int iType)
        throws DBException
    {
        int iRowsUpdated = 0;
        try
        {
            Utility.getLogger().info(strSQL);
            if (SQLParams.USE_INSERT_UPDATE_LITERALS)
            {   // Convert all field values to literals
                if (this.getStatement(iType) == null)
                    this.setStatement(((JdbcDatabase)this.getDatabase()).getJDBCConnection().createStatement(), iType);
                iRowsUpdated = this.getStatement(iType).executeUpdate(strSQL);
            }
            else
            {   // Set the field values in the prepared statement, then execute it!
                if (this.getStatement(iType) != null)
                {
                    if (!strSQL.equals(this.getLastSQLStatement(iType)))
                        this.setStatement(null, iType);   // Not the same as last time.
                }
                if (this.getStatement(iType) == null)
                    this.setStatement(((JdbcDatabase)this.getDatabase()).getJDBCConnection().prepareStatement(strSQL), iType);
                this.setLastSQLStatement(strSQL, iType);
                m_iNextParam = 1;           // First param row
                if ((iType == DBConstants.SQL_UPDATE_TYPE) || (iType == DBConstants.SQL_INSERT_TABLE_TYPE))   // Have WHERE X=?
                {
                    Record record = this.getRecord();
                    for (int iFieldSeq = DBConstants.MAIN_FIELD; iFieldSeq <= record.getFieldCount() + DBConstants.MAIN_FIELD - 1; iFieldSeq++)
                    {
                        BaseField field = record.getField(iFieldSeq);
                        if (field.getSkipSQLParam(iType))
                            continue; // Skip this param
                        field.getSQLFromField((PreparedStatement)this.getStatement(iType), iType, m_iNextParam++);
                    }
                }
                if (m_iNextParam == 1)
                    iRowsUpdated = 1; // No data = no need to update
                KeyArea keyArea = this.getRecord().getKeyArea(0); // Primary index
                boolean bIncludeTempFields = (iType == DBConstants.SQL_UPDATE_TYPE);
                if (!keyArea.isNull(DBConstants.TEMP_KEY_AREA, bIncludeTempFields))
                    if ((iType == DBConstants.SQL_UPDATE_TYPE) || (iType == DBConstants.SQL_DELETE_TYPE)) // Have WHERE X=?
                        m_iNextParam = keyArea.getSQLFromField((PreparedStatement)this.getStatement(iType), iType, m_iNextParam, DBConstants.TEMP_KEY_AREA, false, bIncludeTempFields);    // Always add!?   
                iRowsUpdated = ((PreparedStatement)this.getStatement(iType)).executeUpdate();
            }
        } catch (SQLException e)    {
            DBException ex = this.getDatabase().convertError(e);
            if (this.createIfNotFoundError(ex))
            { // Table not found, create it.
                this.getRecord().setOpenMode((this.getRecord().getOpenMode() | DBConstants.OPEN_DONT_CREATE));   // Only try to create once.
                return this.executeUpdate(strSQL, iType);
            }
            else if (ex.getErrorCode() == DBConstants.BROKEN_PIPE)
            { // If there is a pipe timeout or a broken pipe, try to re-establish the connection and try again
                if (!m_bInRecursiveCall)
                {
                    m_bInRecursiveCall = true;  // Only try once

                    this.close();
                    this.getDatabase().close();     // Next access will force an open.
                    int iResult = this.executeUpdate(strSQL, iType);

                    m_bInRecursiveCall = false;
                    return iResult;
                }
            }
            String strError = "SQL Excep: " + e.getMessage() + " -> DB Ex: " + ex.getMessage() + " on " + strSQL;
            if ((ex.getErrorCode() == DBConstants.DUPLICATE_COUNTER) || (ex.getErrorCode() == DBConstants.DUPLICATE_KEY))
            	Utility.getLogger().info(strError);
            else
            	Utility.getLogger().warning(strError);
            throw ex;
        } finally {
            m_iNextParam = -1;  // This way dataToFields is not confused.
            if (CLEANUP_STATEMENTS)
                this.setStatement(null, iType);
        }
        Utility.getLogger().info("Rows updated: " + iRowsUpdated);
        if (iRowsUpdated == 0)
            throw new DBException(DBConstants.INVALID_RECORD);
        return iRowsUpdated;
    }
    /**
     * Rename this table
     * @param tableName
     * @param newTableName
     * @return
     */
    public boolean renameTable(String tableName, String newTableName) throws DBException
    {
        try {
            if (DBConstants.TRUE.equals((String)this.getDatabase().getProperties().get(SQLParams.NO_PREPARED_STATEMENTS_ON_CREATE)))
                this.setStatement(null, DBConstants.SQL_CREATE_TYPE);
            if (this.getStatement(DBConstants.SQL_CREATE_TYPE) == null)
            	this.setStatement(((JdbcDatabase)this.getDatabase()).getJDBCConnection().createStatement(), DBConstants.SQL_CREATE_TYPE);

            String sql = "RENAME TABLE " + tableName + " TO " + newTableName;
            this.getStatement(DBConstants.SQL_CREATE_TYPE).execute(sql);
            
            if (DBConstants.TRUE.equals((String)this.getDatabase().getProperties().get(SQLParams.NO_PREPARED_STATEMENTS_ON_CREATE)))
                this.setStatement(null, DBConstants.SQL_CREATE_TYPE);
        } catch (SQLException e) {
            throw this.getDatabase().convertError(e);
        }
        return true;
    }
    /**
     * Get a unique key that can be used to reposition to this record.
     * @param iHandleType The type of handle to return.
     * @return The bookmark of this type.
     * @exception DBException File exception.
     */
    public Object getHandle(int iHandleType) throws DBException
    {
        Object bookmark = null;
        try   {
            if ((iHandleType & DBConstants.OBJECT_SOURCE_HANDLE) == 0)
                iHandleType = DBConstants.BOOKMARK_HANDLE;      // For JDBC, always use bookmark
            bookmark = super.getHandle(iHandleType);
        } catch (DBException ex)    {
            throw ex;
        }
        return bookmark;
    }
    /**
     * Return this table drive type for the getObjectSource call. (Override this method)
     * @return java.lang.String Return "JDBC".
     */
    public String getSourceType()
    {
        return DBParams.JDBC;
    }
    /**
     * Is this a table (or recordset/SQL) type of "Table"?.
     * @return false Since JDBC types are not tables.
     */
    public boolean isTable()
    {
        return false;
    }
    /**
     * Get the next record object in this class.
     * @return null, since This is the final record.
     */
    public Record nextRecord()
    {
        return null;
    }
    /**
     * Read next until you get to this position.
     * @param iTargetPosition The position to read to.
     * @param iRelPosition The relative position I'm reading to (in case of first or last).
     * @exception DBException File exception.
     */
    public int readNextToTarget(int iTargetPosition, int iRelPosition) throws DBException
    {
        int iRecordStatus = DBConstants.RECORD_NORMAL;
        boolean bMore = true;
        ResultSet resultSet = (ResultSet)this.getResultSet();
        while (m_iRow < iTargetPosition)
        {   // Spin until you get to the row or hit EOF
            try   {
                bMore = resultSet.next();
                if (!bMore)
                {
                    m_iEOFRow = m_iRow;   // Last physical row = this row - 1.
                    break;  // Hit EOF
                }
                m_iRow++; // Current row
            } catch (SQLException e)    {
                DBException dbEx = this.getDatabase().convertError(e);
                int iErrorCode = dbEx.getErrorCode();
                if ((iRelPosition == DBConstants.FIRST_RECORD) || ((iRelPosition == DBConstants.LAST_RECORD)))
                    if (iErrorCode == DBConstants.INVALID_RECORD)
                        return (DBConstants.RECORD_INVALID | DBConstants.RECORD_AT_BOF | DBConstants.RECORD_AT_EOF);    // Move first - no records
                throw dbEx;
            }
        }
        if ((m_iRow == -1) || (iTargetPosition == -1))
            iRecordStatus |= DBConstants.RECORD_INVALID | DBConstants.RECORD_AT_BOF;
        if (!bMore)
        {
            iRecordStatus |= DBConstants.RECORD_AT_EOF;
            int iEOFRow = m_iEOFRow;   // Save for a sec
            this.setResultSet(null, DBConstants.SQL_SELECT_TYPE);  // Can't use anymore
            m_iEOFRow = iEOFRow;       // I do know the EOF
        }
        return iRecordStatus;
    }
    /**
     * Get the DATA_SOURCE_HANDLE to the last modified or added record.
     * If the database is not using auto-sequence, this returns the bookmark object cached on add.
     * @param iHandleType The type of handle to return.
     * @return The bookmark of this type.
     */
    public Object getLastModified(int iHandleType)
    {   // Change this to call "SELECT @@IDENTITY"
        if (this.getDatabase().isAutosequenceSupport())
            return super.getLastModified(iHandleType);
        else
        { // No auto-sequence support
            return m_LastModifiedBookmark;      // Ignore the iHandleType (Always bookmark type for JDBC)
        }
    }
    /**
     * Create a new empty table using the definition in the record.
     * Use a SQL string such as: "CREATE TABLE Temp (ID int, Name Char(40))";
     * @exception DBException Open errors passed from SQL.
     * @return true if successful.
     */
    public boolean create() throws DBException
    {
        boolean bSuccess = true;
        this.setResultSet(null, DBConstants.SQL_CREATE_TYPE);
        ResultSet resultSet = (ResultSet)this.getResultSet();
        if (resultSet == null)
        {
            Map<String,Object> properties = this.getDatabase().getProperties();
            String strAltSecondaryIndex = (String)this.getDatabase().getProperties().get(SQLParams.ALT_SECONDARY_INDEX);
            try   {
                int iFirstIndex = 0;
                Record record = this.getRecord();
                StringBuilder sql = new StringBuilder();
                sql.append("CREATE TABLE ");
                sql.append(record.makeTableNames(true));
                sql.append(" (");
                for (int iIndex = 0; iIndex < record.getFieldCount(); iIndex++)
                {
                    BaseField field = record.getField(iIndex);
                    if (field.isVirtual())
                        continue;
                    String strType = field.getSQLType(true, properties);
                    if (field instanceof CounterField)
                        if (this.getDatabase().isAutosequenceSupport())
                        {
                            String strCounterType = (String)this.getDatabase().getProperties().get(SQLParams.AUTO_SEQUENCE_TYPE);
                            if (strCounterType != null)
                                strType = strCounterType;
                            String strAutoseqExtra = (String)this.getDatabase().getProperties().get(SQLParams.AUTO_SEQUENCE_EXTRA);
                            if (strAutoseqExtra != null)
                                strType += strAutoseqExtra;
                            String strCounterExtra = (String)this.getDatabase().getProperties().get(SQLParams.COUNTER_EXTRA);
                            if (strCounterExtra != null)
                            {
                                strType += strCounterExtra;
                                iFirstIndex = 1;    // No need to build primary index
                            }
                        }
                    if (iIndex > 0)
                        sql.append(", ");
                    if (DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_NULL_KEY_SUPPORT)))
                        this.checkNullableKey(field, strAltSecondaryIndex != null); // Set the key to not nullable for create.
                    if (!field.isNullable())
                    	if (!DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_NULL_FIELD_SUPPORT)))	// Rare (or for locale dbs)
                    		strType += " NOT NULL";
                    if (strAltSecondaryIndex != null)
                        if (this.checkIndexField(field))
                            strType += ' ' + strAltSecondaryIndex;
                    sql.append(field.getFieldName(true, false)).append(' ').append(strType);
                }
                String strPrimaryKey = (String)this.getDatabase().getProperties().get(SQLParams.AUTO_SEQUENCE_PRIMARY_KEY);
                if (strPrimaryKey != null)
                    if (record.getCounterField() != null)
                        if (this.getDatabase().isAutosequenceSupport())
                {
                    sql.append(", ").append(strPrimaryKey).append(" (").append(record.getCounterField().getFieldName(true, false)).append(")");
                    iFirstIndex = 1;    // No need to build primary index
                }
                sql.append(")");
                Utility.getLogger().info(sql.toString());
                if (DBConstants.TRUE.equals((String)this.getDatabase().getProperties().get(SQLParams.NO_PREPARED_STATEMENTS_ON_CREATE)))
                    this.setStatement(null, DBConstants.SQL_CREATE_TYPE);
                if (this.getStatement(DBConstants.SQL_CREATE_TYPE) == null)
                    this.setStatement(((JdbcDatabase)this.getDatabase()).getJDBCConnection().createStatement(), DBConstants.SQL_CREATE_TYPE);
                this.getStatement(DBConstants.SQL_CREATE_TYPE).execute(sql.toString());
                // Now set up the index(es)
                sql = new StringBuilder();
                String strPrimaryKeySQL = (String)this.getDatabase().getProperties().get(SQLParams.CREATE_PRIMARY_INDEX);
                if (strPrimaryKeySQL == null)   // null=use default, If empty string, not supported.
                    strPrimaryKeySQL = "CREATE ${unique} INDEX ${keyname} ON ${table} (${fieldsandorder})";    // Default
                String strCreateIndexSQL = (String)this.getDatabase().getProperties().get(SQLParams.CREATE_INDEX);
                if (strCreateIndexSQL == null)   // null=use default, If empty string, not supported.
                    strCreateIndexSQL = strPrimaryKeySQL;    // Default
                for (int iIndex = iFirstIndex; iIndex < record.getKeyAreaCount(); iIndex++)
                {
                    KeyArea keyArea = record.getKeyArea(iIndex);
                    if ((keyArea.getUniqueKeyCode() == DBConstants.UNIQUE)
                        && (iIndex == iFirstIndex)
                            && (keyArea.getKeyFields() == 1))
                        sql = new StringBuilder(strPrimaryKeySQL);
                    else
                        sql = new StringBuilder(strCreateIndexSQL);
                    if (sql.length() == 0)
                        continue;   // Not supported.
                    boolean indexCanBeUnique = true;
                	if (DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_NULL_UNIQUE_KEYS)))
                        if (iIndex > 0)
                    {	// This db considers null a valid index value and will throw a unique key error on write
                        for (int iFieldIndex = 0; iFieldIndex < keyArea.getKeyFields(); iFieldIndex++)
                        {
                            KeyField keyField = keyArea.getKeyField(iFieldIndex);
                            BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
                            if (field.isNullable())
                            	indexCanBeUnique = false;
                        }
                    }
                    if (iIndex > 0)
                    	if (DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_NULL_FIELD_SUPPORT)))	// Rare (or for locale dbs)
                    		indexCanBeUnique = false;
                    
                    if ((keyArea.getUniqueKeyCode() == DBConstants.UNIQUE) && (indexCanBeUnique))
                        sql = Utility.replace(sql, "${unique}", "UNIQUE");
                    else
                        sql = Utility.replace(sql, "${unique}", DBConstants.BLANK);
                    String strKeyName = keyArea.getKeyName();
                    String strTableNames = record.makeTableNames(true);
                    if (DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_DUPLICATE_KEY_NAMES)))
                    {
                    	if (strTableNames.endsWith("_temp"))
                    		strKeyName = strTableNames.substring(0, strTableNames.length() - 5) + "_" + strKeyName;
                    	else
                    		strKeyName = strTableNames + "_" + strKeyName;
                    }
                    String strMaxKeyNameLength = (String)this.getDatabase().getProperties().get(SQLParams.MAX_KEY_NAME_LENGTH);
                    if (strMaxKeyNameLength != null)
                    {
                        int iMaxKeyNameLength = Integer.parseInt(strMaxKeyNameLength);
                        if (strKeyName.length() > iMaxKeyNameLength)
                        {
                        	strKeyName = strKeyName + (int)(Math.random() * 100000);
                            strKeyName = strKeyName.substring(strKeyName.length() - iMaxKeyNameLength);
                            while (!Character.isLetter(strKeyName.charAt(0))) {
                            	strKeyName = strKeyName.substring(1);
                            }
                        }
                    }
                    sql = Utility.replace(sql, "${keyname}", strKeyName);
                    sql = Utility.replace(sql, "${table}", strTableNames);
                    String strFields = DBConstants.BLANK;
                    String strFieldAndOrder = DBConstants.BLANK;
                    for (int iFieldIndex = 0; iFieldIndex < keyArea.getKeyFields(); iFieldIndex++)
                    {
                        KeyField keyField = keyArea.getKeyField(iFieldIndex);
                        BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
                        if (iFieldIndex > 0)
                        {
                            strFields += ", ";
                            strFieldAndOrder += ", ";
                        }
                        strFields += field.getFieldName(true, false) + " ";
                        strFieldAndOrder += field.getFieldName(true, false) + " ";
                        if (keyField.getKeyOrder() == DBConstants.DESCENDING)
                            strFieldAndOrder += "DESC ";
                    }
                    sql = Utility.replace(sql, "${fields}", strFields);
                    sql = Utility.replace(sql, "${fieldsandorder}", strFieldAndOrder);
                    Utility.getLogger().info(sql.toString());
                    this.getStatement(DBConstants.SQL_CREATE_TYPE).execute(sql.toString());
                }
            } catch (SQLException e)    {
                throw this.getDatabase().convertError(e);
            } finally {
                if (DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_PREPARED_STATEMENTS_ON_CREATE)))
                    this.setStatement(null, DBConstants.SQL_CREATE_TYPE);
            }
        }
        this.setResultSet(null, DBConstants.SQL_CREATE_TYPE);
        return bSuccess;    // Success!!!
    }
    /**
     * This is a special method - If the db doesn't allow null keys,
     * you must make sure they are not nullable.
     * <br />This is used on create.
     * @param field Field to check, if it is in a key and is nullable, set to not nullable.
     */
    public void checkNullableKey(BaseField field, boolean bFirstFieldOnly)
    {
        for (int iKeySeq = 0; iKeySeq < this.getRecord().getKeyAreaCount(); iKeySeq++)
        {
            KeyArea keyArea = this.getRecord().getKeyArea(iKeySeq);
            for (int iKeyFieldSeq = 0; iKeyFieldSeq < keyArea.getKeyFields(); iKeyFieldSeq++)
            {
                if (keyArea.getField(iKeyFieldSeq) == field)
                {
                    if (field.isNullable() == true)
                    {   // Don't allow this key to be null
                        field.setNullable(false);
                        return;
                    }
                }
                if (bFirstFieldOnly)
                    break;
            }
        }
    }
    /**
     * This is a special method - If the db doesn't allow multiple keys indexes,
     * see if this field is a first field on an index (non-primary).
     * @return true if true.
     */
    public boolean checkIndexField(BaseField field)
    {
        for (int iKeySeq = 0; iKeySeq < this.getRecord().getKeyAreaCount(); iKeySeq++)
        {
            KeyArea keyArea = this.getRecord().getKeyArea(iKeySeq);
            if (keyArea.getField(0) == field)
                if (field != this.getRecord().getCounterField())
                    return true;
        }
        return false;
    }
}
