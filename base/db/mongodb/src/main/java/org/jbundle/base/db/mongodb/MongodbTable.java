/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.mongodb;

/**
 * @(#)MongodbTable.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.BsonType;
import org.bson.Document;

import java.util.*;

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
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.buff.str.StrBuffer;


/**
 * MongodbTable - Table for JDBC queries.
 *
 * @version 1.0.0
 * @author    Don Corley
 *
 */
public class MongodbTable extends BaseTable
{
    protected MongoCollection<Document> collection = null;

    protected MongoCursor<Document> mongoCursor = null;

    protected Document document = null;

    /**
     * Constructor.
     */
    public MongodbTable()
    {
        super();
    }
    /**
     * Constructor.
     * @param database The database to add this table to.
     * @param record The record to connect to this table.
     */
    public MongodbTable(BaseDatabase database, Record record)
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

        mongoCursor = null;
        document = null;
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
        int iErrorCode = super.dataToFields(record);

        KeyArea keyArea = this.getRecord().getKeyArea(0); // Primary index
        keyArea.setupKeyBuffer(null, DBConstants.TEMP_KEY_AREA);        // Save keys for possible update/delete

        return iErrorCode;
    }
    /**
     * Move the output buffer to this field.
     * <p />This is a little complicated:<br />
     * The first time through, use resultSet.findColumn(x) to lookup the column.<br />
     * Then call field.moveSQLToField(resultSet, column).<br />
     * The bump the column number.
     * @param fieldInfo The field to move the current data to.
     * @return The error code (From field.setData()).
     * @exception DBException File exception.
     */
    public int dataToField(Field fieldInfo) throws DBException
    {
        BaseField field = (BaseField)fieldInfo;
        if (!field.isSelected())
            return field.initField(DBConstants.DONT_DISPLAY);   // Not selected, don't move data
        Object data = document.get(field.getFieldName());
        if (data != null)
            if (data.getClass() != field.getDataClass()) {
                try {
                    data = Converter.convertObjectToDatatype(data, field.getDataClass(), null);
                } catch (Exception ex) {
                    return field.setString(data.toString(), DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);   // TODO Fix this
                }
            }
        return field.setData(data, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
    }
    /**
     * Move all the fields to the output buffer.
     * @exception DBException File exception.
     */
    public void fieldsToData(Rec record) throws DBException
    {
        document = new Document();
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
        if (!field.isNull())
            document.append(field.getFieldName(true, false), field.getData());
    }
    /**
     * Open this table (re-query the table).
     * @exception DBException Open errors passed from SQL.
     */
    public void doOpen() throws DBException
    {
        super.doOpen();
        // get a handle to the "table" collection
        String tableName = this.getRecord().makeTableNames(false);
        // Seems like a lame way to see if the collection exists
        if (!((MongodbDatabase)this.getDatabase()).doesTableExist(tableName))
            this.createNewCollection();
        collection = ((MongodbDatabase)this.getDatabase()).getMongoDatabase().getCollection(tableName);

        mongoCursor = null;
        document = null;
    }
    /**
     * Create new collection
     */
    public void createNewCollection() throws DBException {
        try {
            Record record = this.getRecord();
            String tableName = this.getRecord().makeTableNames(false);

            Document validator = new Document();
            Document validators = new Document();
            validator.append("$jsonSchema", validators);
            validators.append("bsonType", "object");
            Document fieldValidators = new Document();
            StringBuilder requiredFieldList = new StringBuilder("[ ");
            for (int iIndex = DBConstants.MAIN_FIELD; iIndex < record.getFieldCount(); iIndex++) {
                if ((!record.getField(iIndex).isNullable()) && (!record.getField(iIndex).isVirtual())) {
                    if (requiredFieldList.length() != 0)
                        requiredFieldList.append(", ");
                    requiredFieldList.append(record.getField(iIndex).getFieldName(true, false));
                }
            }
            requiredFieldList.append(" ]");
            if (requiredFieldList.length() > 4)
                fieldValidators.append("required", requiredFieldList.toString());
            Document properties = new Document();
            for (int iIndex = DBConstants.MAIN_FIELD; iIndex < record.getFieldCount(); iIndex++) {
                BaseField field = record.getField(iIndex);
                if ((!field.isVirtual()) && (field != record.getCounterField())) {
                    Document fieldProperties = new Document();
                    fieldProperties.append("bsonType", field.getSQLType(false, this.getDatabase().getProperties()));    // Note - My properties file translates to the correct type
                    properties.append(field.getFieldName(), fieldProperties);
                }
            }
            validators.append("properties", properties);
            ValidationOptions validationOptions = new ValidationOptions();
            validationOptions.validator(validator);
            CreateCollectionOptions options = new CreateCollectionOptions();
            options.validationOptions(validationOptions);

            ((MongodbDatabase) this.getDatabase()).getMongoDatabase().createCollection(tableName, options);

            collection = ((MongodbDatabase) this.getDatabase()).getMongoDatabase().getCollection(tableName);

            List<IndexModel> list = new ArrayList<IndexModel>();
            for (int iIndex = DBConstants.MAIN_KEY_AREA + 1; iIndex < record.getKeyAreaCount() + DBConstants.MAIN_KEY_AREA; iIndex++) { // Skip main key area
                KeyArea keyArea = record.getKeyArea(iIndex);
                Document keys = new Document();
                for (int iIndexSeq = DBConstants.MAIN_FIELD; iIndexSeq < keyArea.getKeyFields() + DBConstants.MAIN_FIELD; iIndexSeq++) {   // Make sure the index key is selected!
                    keys.append(keyArea.getField(iIndexSeq).getFieldName(true, false), keyArea.getKeyOrder() ? ASCENDING : DESCENDING);
                }

                IndexOptions indexOptions = new IndexOptions();
                indexOptions.unique(keyArea.getUniqueKeyCode() == DBConstants.UNIQUE);
                indexOptions.name(keyArea.getKeyName());

                IndexModel indexModel = new IndexModel(keys, indexOptions);
                list.add(indexModel);
            }
            collection.createIndexes(list);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            throw this.getDatabase().convertError(ex);
        }
    }
    public static Integer ASCENDING = new Integer(1);
    public static Integer DESCENDING = new Integer(-1);
    /**
     * Create/Clear the current object (Always called from the record class).
     * Don't need to do anything special for SQL tables.
     * @exception DBException File exception.
     */
    public void doAddNew() throws DBException
    {
    }
    /**
     * Delete this record (Always called from the record class).
     * Do a SQL delete.
     * @exception DBException INVALID_RECORD - Attempt to delete a record that is not current.
     */
    public void doRemove() throws DBException
    {
        Object bookmark = this.getRecord().getHandle(DBConstants.BOOKMARK_HANDLE);
//        String strRecordset = this.getRecord().getSQLDelete(SQLParams.USE_INSERT_UPDATE_LITERALS);
//        this.executeUpdate(strRecordset, DBConstants.SQL_DELETE_TYPE);
//        if (m_iRow != -1)
//            m_iRow--;   // Just in case I'm in an active query this will keep requeries correct
        if (mongoCursor == null)
            throw new DBException(DBConstants.INVALID_RECORD);
        try {
            Object key = document.getObjectId("_id");
            document = new Document("_id", key);
            collection.deleteOne(document);
            document = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
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
//        if (this.lockOnDBTrxType(null, DBConstants.LOCK_TYPE, true))
//            return this.lockCurrentRecord();
//        else
            return DBConstants.NORMAL_RETURN;
    }
    /**
     * Is the last record in the file?
     * @return false if file position is at last record.
     */
    public boolean doHasNext() throws DBException {
//        if ((m_iRecordStatus & DBConstants.RECORD_AT_EOF) != 0)
//            return false; // Already at EOF, can't be one after
        if (!this.isOpen())
            this.open();    // Make sure any listeners are called before disabling.
        if (mongoCursor == null)
//                if (isBOF())
//                    if ((m_iRecordStatus & DBConstants.RECORD_INVALID) != 0)
            {
                try {
                    mongoCursor = collection.find().iterator();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                }
            }
//        m_iRecordStatus |= DBConstants.RECORD_NEXT_PENDING;     // If next call is a moveNext(), return unchanged!
        return mongoCursor == null ? false : mongoCursor.hasNext();
    }
    /**
     * Move the position of the record.
     * Move the current position and read the record (optionally read several records).
     * This method is optimized for forward sequential reads.
     * @param iRelPosition relative Position to read the next record.
     * @return NORMAL_RETURN - The following are NOT mutually exclusive
     * @return RECORD_INVALID
     * @return RECORD_AT_BOF
     * @return RECORD_AT_EOF
     * @exception DBException File exception.
     */
    public int doMove(int iRelPosition) throws DBException
    {   // Do the move
        if (mongoCursor == null)
            return DBConstants.RECORD_INVALID;
        document = mongoCursor.next();
        if (document == null)
            return DBConstants.RECORD_INVALID;
//        ResultSet resultSet = (ResultSet)this.getResultSet();
//        int iRecordStatus = this.readNextToTarget(iTargetPosition, iRelPosition);
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Read the record that matches this record's current key.
     * @param strSeekSign The seek sign (defaults to '=').
     * @return true if successful.
     * @exception DBException File exception.
     */
    public boolean doSeek(String strSeekSign) throws DBException
    {
//            ResultSet resultSet = this.executeQuery(strRecordset, DBConstants.SQL_SEEK_TYPE, vParamList);
        return true;
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
//        this.executeUpdate(strRecordset, iType);
        try {
            collection.insertOne(document);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        document = null;
    }
    /**
     * Update this record (Always called from the record class).
     * @param record The record to update.
     * @exception DBException File exception.
     */
    public void doSet(Record record) throws DBException
    {
        Object bookmark = record.getHandle(DBConstants.BOOKMARK_HANDLE);
        CounterField fldID = (CounterField)record.getCounterField();
        if (fldID != null)
            if (record.isAutoSequence())
                fldID.setModified(false); // Never set a counter field
        String strRecordset = record.getSQLUpdate(SQLParams.USE_INSERT_UPDATE_LITERALS);
        int iType = DBConstants.SQL_UPDATE_TYPE;
            if (strRecordset != null)
                /*iRowsUpdated =*/ this.executeUpdate(strRecordset, iType);
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
    public Object executeQuery(String strSQL, int iType, Vector<BaseField> vParamList)
        throws DBException
    {
//        ResultSet resultSet = null;
//        m_bSelectAllFields = false;
//        m_rgiFieldColumns = null;
//        try   {
//            Utility.getLogger().info(strSQL);
//            if (SQLParams.USE_SELECT_LITERALS)
//            {
//                if (this.getStatement(iType) instanceof PreparedStatement)
//                    this.setStatement(null, iType); // Close old prepared statement
//                if (this.getStatement(iType) == null)
//                    this.setStatement(((MongodbDatabase)this.getDatabase()).getJDBCConnection().createStatement(), iType);
//                resultSet = this.getStatement(iType).executeQuery(strSQL);
//            }
//            else
//            {
//                if (this.getStatement(iType) != null)
//                {
//                    if (!strSQL.equals(this.getLastSQLStatement(iType)))
//                        this.setStatement(null, iType);   // Not the same as last time.
//                }
//                if (this.getStatement(iType) == null)
//                    this.setStatement(((MongodbDatabase)this.getDatabase()).getJDBCConnection().prepareStatement(strSQL), iType);
//                this.setLastSQLStatement(strSQL, iType);
//                m_iNextParam = 1;           // First param row
//                this.getRecord().getKeyArea(0); // Primary index
//                if ((iType == DBConstants.SQL_SELECT_TYPE) || (iType == DBConstants.SQL_AUTOSEQUENCE_TYPE) || (iType == DBConstants.SQL_SEEK_TYPE)) // Have WHERE X=?
//                {
//                    if (vParamList != null)
//                    {
//                        for (Enumeration<BaseField> e = vParamList.elements() ; e.hasMoreElements() ;)
//                        {
//                            BaseField field = e.nextElement();
//                            field.getSQLFromField((PreparedStatement)this.getStatement(iType), iType, m_iNextParam);
//                            m_iNextParam++;
//                        }
//                    }
//                }
//                resultSet = ((PreparedStatement)this.getStatement(iType)).executeQuery();
//            }
//        } catch (SQLException e)    {
//            DBException ex = this.getDatabase().convertError(e);
//            if (this.createIfNotFoundError(ex))
//            { // Table not found, the previous line created the table, re-do the query.
//                this.getRecord().setOpenMode((this.getRecord().getOpenMode() | DBConstants.OPEN_DONT_CREATE));   // Only try to create once.
//                return this.executeQuery(strSQL, iType, vParamList);
//            }
//            else if (ex.getErrorCode() == DBConstants.BROKEN_PIPE)
//            { // If there is a pipe timeout or a broken pipe, try to re-establish the connection and try again
//                if (!m_bInRecursiveCall)
//                {
//                    m_bInRecursiveCall = true;
//
//                    this.close();
//                    this.getDatabase().close();     // Next access will force an open.
//                    ResultSet result = this.executeQuery(strSQL, iType, vParamList);
//
//                    m_bInRecursiveCall = false;
//                    return result;
//                }
//            }
//            throw ex;
//        }
//        if (strSQL.indexOf('*') != -1)
//        {
//            m_bSelectAllFields = true;
//            m_iColumn = 1;  // First column
//        }
        return null; // resultSet;
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
//        try
//        {
//            Utility.getLogger().info(strSQL);
//            if (SQLParams.USE_INSERT_UPDATE_LITERALS)
//            {   // Convert all field values to literals
//                if (this.getStatement(iType) == null)
//                    this.setStatement(((MongodbDatabase)this.getDatabase()).getJDBCConnection().createStatement(), iType);
//                iRowsUpdated = this.getStatement(iType).executeUpdate(strSQL);
//            }
//            else
//            {   // Set the field values in the prepared statement, then execute it!
//                if (this.getStatement(iType) != null)
//                {
//                    if (!strSQL.equals(this.getLastSQLStatement(iType)))
//                        this.setStatement(null, iType);   // Not the same as last time.
//                }
//                if (this.getStatement(iType) == null)
//                    this.setStatement(((MongodbDatabase)this.getDatabase()).getJDBCConnection().prepareStatement(strSQL), iType);
//                this.setLastSQLStatement(strSQL, iType);
//                m_iNextParam = 1;           // First param row
//                if ((iType == DBConstants.SQL_UPDATE_TYPE) || (iType == DBConstants.SQL_INSERT_TABLE_TYPE))   // Have WHERE X=?
//                {
//                    Record record = this.getRecord();
//                    for (int iFieldSeq = DBConstants.MAIN_FIELD; iFieldSeq <= record.getFieldCount() + DBConstants.MAIN_FIELD - 1; iFieldSeq++)
//                    {
//                        BaseField field = record.getField(iFieldSeq);
//                        if (field.getSkipSQLParam(iType))
//                            continue; // Skip this param
//                        field.getSQLFromField((PreparedStatement)this.getStatement(iType), iType, m_iNextParam++);
//                    }
//                }
//                if (m_iNextParam == 1)
//                    iRowsUpdated = 1; // No data = no need to update
//                KeyArea keyArea = this.getRecord().getKeyArea(0); // Primary index
//                boolean bIncludeTempFields = (iType == DBConstants.SQL_UPDATE_TYPE);
//                if (!keyArea.isNull(DBConstants.TEMP_KEY_AREA, bIncludeTempFields))
//                    if ((iType == DBConstants.SQL_UPDATE_TYPE) || (iType == DBConstants.SQL_DELETE_TYPE)) // Have WHERE X=?
//                        m_iNextParam = keyArea.getSQLFromField((PreparedStatement)this.getStatement(iType), iType, m_iNextParam, DBConstants.TEMP_KEY_AREA, false, bIncludeTempFields);    // Always add!?
//                iRowsUpdated = ((PreparedStatement)this.getStatement(iType)).executeUpdate();
//            }
//        } catch (SQLException e)    {
//            DBException ex = this.getDatabase().convertError(e);
//            if (this.createIfNotFoundError(ex))
//            { // Table not found, create it.
//                this.getRecord().setOpenMode((this.getRecord().getOpenMode() | DBConstants.OPEN_DONT_CREATE));   // Only try to create once.
//                return this.executeUpdate(strSQL, iType);
//            }
//            else if (ex.getErrorCode() == DBConstants.BROKEN_PIPE)
//            { // If there is a pipe timeout or a broken pipe, try to re-establish the connection and try again
//                if (!m_bInRecursiveCall)
//                {
//                    m_bInRecursiveCall = true;  // Only try once
//
//                    this.close();
//                    this.getDatabase().close();     // Next access will force an open.
//                    int iResult = this.executeUpdate(strSQL, iType);
//
//                    m_bInRecursiveCall = false;
//                    return iResult;
//                }
//            }
//            String strError = "SQL Excep: " + e.getMessage() + " -> DB Ex: " + ex.getMessage() + " on " + strSQL;
//            if ((ex.getErrorCode() == DBConstants.DUPLICATE_COUNTER) || (ex.getErrorCode() == DBConstants.DUPLICATE_KEY))
//            	Utility.getLogger().info(strError);
//            else
//            	Utility.getLogger().warning(strError);
//            throw ex;
//        } finally {
//            m_iNextParam = -1;  // This way dataToFields is not confused.
//            if (CLEANUP_STATEMENTS)
//                this.setStatement(null, iType);
//        }
//        Utility.getLogger().info("Rows updated: " + iRowsUpdated);
//        if (iRowsUpdated == 0)
//            throw new DBException(DBConstants.INVALID_RECORD);
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
//        try {
//            if (DBConstants.TRUE.equals((String)this.getDatabase().getProperties().get(SQLParams.NO_PREPARED_STATEMENTS_ON_CREATE)))
//                this.setStatement(null, DBConstants.SQL_CREATE_TYPE);
//            if (this.getStatement(DBConstants.SQL_CREATE_TYPE) == null)
//            	this.setStatement(((MongodbDatabase)this.getDatabase()).getJDBCConnection().createStatement(), DBConstants.SQL_CREATE_TYPE);
//
//            String sql = "RENAME TABLE " + tableName + " TO " + newTableName;
//            this.getStatement(DBConstants.SQL_CREATE_TYPE).execute(sql);
//
//            if (DBConstants.TRUE.equals((String)this.getDatabase().getProperties().get(SQLParams.NO_PREPARED_STATEMENTS_ON_CREATE)))
//                this.setStatement(null, DBConstants.SQL_CREATE_TYPE);
//        } catch (SQLException e) {
//            throw this.getDatabase().convertError(e);
//        }
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
     * Get the DATA_SOURCE_HANDLE to the last modified or added record.
     * If the database is not using auto-sequence, this returns the bookmark object cached on add.
     * @param iHandleType The type of handle to return.
     * @return The bookmark of this type.
     */
    public Object getLastModified(int iHandleType)
    {   // Change this to call "SELECT @@IDENTITY"
        return super.getLastModified(iHandleType);
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
//        this.setResultSet(null, DBConstants.SQL_CREATE_TYPE);
//        ResultSet resultSet = (ResultSet)this.getResultSet();
//        if (resultSet == null)
//        {
//            Map<String,Object> properties = this.getDatabase().getProperties();
//            String strAltSecondaryIndex = (String)this.getDatabase().getProperties().get(SQLParams.ALT_SECONDARY_INDEX);
//            try   {
//                int iFirstIndex = 0;
//                Record record = this.getRecord();
//                StringBuilder sql = new StringBuilder();
//                sql.append("CREATE TABLE ");
//                sql.append(record.makeTableNames(true));
//                sql.append(" (");
//                for (int iIndex = 0; iIndex < record.getFieldCount(); iIndex++)
//                {
//                    BaseField field = record.getField(iIndex);
//                    if (field.isVirtual())
//                        continue;
//                    String strType = field.getSQLType(true, properties);
//                    if (field instanceof CounterField)
//                        if (this.getDatabase().isAutosequenceSupport())
//                        {
//                            String strCounterType = (String)this.getDatabase().getProperties().get(SQLParams.AUTO_SEQUENCE_TYPE);
//                            if (strCounterType != null)
//                                strType = strCounterType;
//                            String strAutoseqExtra = (String)this.getDatabase().getProperties().get(SQLParams.AUTO_SEQUENCE_EXTRA);
//                            if (strAutoseqExtra != null)
//                                strType += strAutoseqExtra;
//                            String strCounterExtra = (String)this.getDatabase().getProperties().get(SQLParams.COUNTER_EXTRA);
//                            if (strCounterExtra != null)
//                            {
//                                strType += strCounterExtra;
//                                iFirstIndex = 1;    // No need to build primary index
//                            }
//                        }
//                    if (iIndex > 0)
//                        sql.append(", ");
//                    if (DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_NULL_KEY_SUPPORT)))
//                        this.checkNullableKey(field, strAltSecondaryIndex != null); // Set the key to not nullable for create.
//                    if (!field.isNullable())
//                    	if (!DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_NULL_FIELD_SUPPORT)))	// Rare (or for locale dbs)
//                    		strType += " NOT NULL";
//                    if (strAltSecondaryIndex != null)
//                        if (this.checkIndexField(field))
//                            strType += ' ' + strAltSecondaryIndex;
//                    sql.append(field.getFieldName(true, false)).append(' ').append(strType);
//                }
//                String strPrimaryKey = (String)this.getDatabase().getProperties().get(SQLParams.AUTO_SEQUENCE_PRIMARY_KEY);
//                if (strPrimaryKey != null)
//                    if (record.getCounterField() != null)
//                        if (this.getDatabase().isAutosequenceSupport())
//                {
//                    sql.append(", ").append(strPrimaryKey).append(" (").append(record.getCounterField().getFieldName(true, false)).append(")");
//                    iFirstIndex = 1;    // No need to build primary index
//                }
//                sql.append(")");
//                Utility.getLogger().info(sql.toString());
//                if (DBConstants.TRUE.equals((String)this.getDatabase().getProperties().get(SQLParams.NO_PREPARED_STATEMENTS_ON_CREATE)))
//                    this.setStatement(null, DBConstants.SQL_CREATE_TYPE);
//                if (this.getStatement(DBConstants.SQL_CREATE_TYPE) == null)
//                    this.setStatement(((MongodbDatabase)this.getDatabase()).getJDBCConnection().createStatement(), DBConstants.SQL_CREATE_TYPE);
//                this.getStatement(DBConstants.SQL_CREATE_TYPE).execute(sql.toString());
//                // Now set up the index(es)
//                sql = new StringBuilder();
//                String strPrimaryKeySQL = (String)this.getDatabase().getProperties().get(SQLParams.CREATE_PRIMARY_INDEX);
//                if (strPrimaryKeySQL == null)   // null=use default, If empty string, not supported.
//                    strPrimaryKeySQL = "CREATE ${unique} INDEX ${keyname} ON ${table} (${fieldsandorder})";    // Default
//                String strCreateIndexSQL = (String)this.getDatabase().getProperties().get(SQLParams.CREATE_INDEX);
//                if (strCreateIndexSQL == null)   // null=use default, If empty string, not supported.
//                    strCreateIndexSQL = strPrimaryKeySQL;    // Default
//                for (int iIndex = iFirstIndex; iIndex < record.getKeyAreaCount(); iIndex++)
//                {
//                    KeyArea keyArea = record.getKeyArea(iIndex);
//                    if ((keyArea.getUniqueKeyCode() == DBConstants.UNIQUE)
//                        && (iIndex == iFirstIndex)
//                            && (keyArea.getKeyFields() == 1))
//                        sql = new StringBuilder(strPrimaryKeySQL);
//                    else
//                        sql = new StringBuilder(strCreateIndexSQL);
//                    if (sql.length() == 0)
//                        continue;   // Not supported.
//                    boolean indexCanBeUnique = true;
//                	if (DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_NULL_UNIQUE_KEYS)))
//                        if (iIndex > 0)
//                    {	// This db considers null a valid index value and will throw a unique key error on write
//                        for (int iFieldIndex = 0; iFieldIndex < keyArea.getKeyFields(); iFieldIndex++)
//                        {
//                            KeyField keyField = keyArea.getKeyField(iFieldIndex);
//                            BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
//                            if (field.isNullable())
//                            	indexCanBeUnique = false;
//                        }
//                    }
//                    if (iIndex > 0)
//                    	if (DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_NULL_FIELD_SUPPORT)))	// Rare (or for locale dbs)
//                    		indexCanBeUnique = false;
//
//                    if ((keyArea.getUniqueKeyCode() == DBConstants.UNIQUE) && (indexCanBeUnique))
//                        sql = Utility.replace(sql, "${unique}", "UNIQUE");
//                    else
//                        sql = Utility.replace(sql, "${unique}", DBConstants.BLANK);
//                    String strKeyName = keyArea.getKeyName();
//                    String strTableNames = record.makeTableNames(true);
//                    if (DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_DUPLICATE_KEY_NAMES)))
//                    {
//                    	if (strTableNames.endsWith("_temp"))
//                    		strKeyName = strTableNames.substring(0, strTableNames.length() - 5) + "_" + strKeyName;
//                    	else
//                    		strKeyName = strTableNames + "_" + strKeyName;
//                    }
//                    String strMaxKeyNameLength = (String)this.getDatabase().getProperties().get(SQLParams.MAX_KEY_NAME_LENGTH);
//                    if (strMaxKeyNameLength != null)
//                    {
//                        int iMaxKeyNameLength = Integer.parseInt(strMaxKeyNameLength);
//                        if (strKeyName.length() > iMaxKeyNameLength)
//                        {
//                        	strKeyName = strKeyName + (int)(Math.random() * 100000);
//                            strKeyName = strKeyName.substring(strKeyName.length() - iMaxKeyNameLength);
//                            while (!Character.isLetter(strKeyName.charAt(0))) {
//                            	strKeyName = strKeyName.substring(1);
//                            }
//                        }
//                    }
//                    sql = Utility.replace(sql, "${keyname}", strKeyName);
//                    sql = Utility.replace(sql, "${table}", strTableNames);
//                    String strFields = DBConstants.BLANK;
//                    String strFieldAndOrder = DBConstants.BLANK;
//                    for (int iFieldIndex = 0; iFieldIndex < keyArea.getKeyFields(); iFieldIndex++)
//                    {
//                        KeyField keyField = keyArea.getKeyField(iFieldIndex);
//                        BaseField field = keyField.getField(DBConstants.FILE_KEY_AREA);
//                        if (iFieldIndex > 0)
//                        {
//                            strFields += ", ";
//                            strFieldAndOrder += ", ";
//                        }
//                        strFields += field.getFieldName(true, false) + " ";
//                        strFieldAndOrder += field.getFieldName(true, false) + " ";
//                        if (keyField.getKeyOrder() == DBConstants.DESCENDING)
//                            strFieldAndOrder += "DESC ";
//                    }
//                    sql = Utility.replace(sql, "${fields}", strFields);
//                    sql = Utility.replace(sql, "${fieldsandorder}", strFieldAndOrder);
//                    Utility.getLogger().info(sql.toString());
//                    this.getStatement(DBConstants.SQL_CREATE_TYPE).execute(sql.toString());
//                }
//            } catch (SQLException e)    {
//                throw this.getDatabase().convertError(e);
//            } finally {
//                if (DBConstants.TRUE.equals(this.getDatabase().getProperties().get(SQLParams.NO_PREPARED_STATEMENTS_ON_CREATE)))
//                    this.setStatement(null, DBConstants.SQL_CREATE_TYPE);
//            }
//        }
//        this.setResultSet(null, DBConstants.SQL_CREATE_TYPE);
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
