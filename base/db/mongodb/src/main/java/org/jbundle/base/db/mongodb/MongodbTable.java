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
import org.bson.Document;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.bson.conversions.Bson;
import org.jbundle.base.db.*;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.CounterField;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Field;
import org.jbundle.model.db.Rec;
import org.jbundle.thin.base.db.Converter;

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
        Object data = this.getDataField(field, true);
        return field.setData(data, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
    }
    /**
     * Get the raw data for this field from the current mongo document.
     * @param field The field to move the current data to.
     * @return The raw data from the mongo document.
     */
    public Object getDataField(Field field, boolean convertToFieldDataType)
    {
        Object data = document.get(field.getFieldName(false, false, true));
        if (convertToFieldDataType)
            if (data != null)
                if (data.getClass() != field.getDataClass()) {
                    try {
                        data = Converter.convertObjectToDatatype(data, field.getDataClass(), null);
                    } catch (Exception ex) {
                        return field.setString(data.toString(), DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);   // TODO Fix this
                    }
            }
        return data;
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
        if (!field.isNull()) {
            document.append(field.getFieldName(true, false, true), ((BaseField)field).getBsonData());
        }
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
        if (collection == null) {
            if (!((MongodbDatabase) this.getDatabase()).doesTableExist(tableName)) {
                boolean loadInitialData = false;
                boolean useTemporaryFilename = false;
                if (this.getDatabase() != null)
                {
                    if (DBConstants.TRUE.equalsIgnoreCase(this.getDatabase().getProperty(DBConstants.LOAD_INITIAL_DATA)))
                        if ((this.getDatabase().getDatabaseOwner() == null) || (!DBConstants.FALSE.equalsIgnoreCase(this.getDatabase().getDatabaseOwner().getProperty(DBConstants.LOAD_INITIAL_DATA))))   // Global switch
                            loadInitialData = true;
                    if (DBConstants.TRUE.equalsIgnoreCase(this.getDatabase().getProperty(SQLParams.RENAME_TABLE_SUPPORT)))
                        useTemporaryFilename = true;
                }
                if (useTemporaryFilename)
                    if (loadInitialData)
                    {
                        tableName = this.getRecord().getTableNames(false);
                        this.getRecord().setTableNames(tableName + TEMP_SUFFIX);
                    }
                boolean bSuccess = this.create();
                if (bSuccess)
                    if (loadInitialData)
                    {
                        this.loadInitialData();
                        if (useTemporaryFilename)
                            this.renameTable(tableName + TEMP_SUFFIX, tableName);
                    }
            }
            collection = ((MongodbDatabase) this.getDatabase()).getMongoDatabase().getCollection(tableName);
        }

        mongoCursor = null;
        document = null;
    }
    /**
     * Create new collection
     */
    public boolean create() throws DBException {
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
                    requiredFieldList.append(record.getField(iIndex).getFieldName(true, false, true));
                }
            }
            requiredFieldList.append(" ]");
            if (requiredFieldList.length() > 4)
                fieldValidators.append("required", requiredFieldList.toString());
            Document properties = new Document();
            for (int iIndex = DBConstants.MAIN_FIELD; iIndex < record.getFieldCount(); iIndex++) {
                BaseField field = record.getField(iIndex);
                if ((!field.isVirtual()) && (field != record.getCounterField()) && (!(field instanceof ReferenceField))) {
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
                    keys.append(keyArea.getField(iIndexSeq).getFieldName(true, false, true), keyArea.getKeyOrder() ? ASCENDING : DESCENDING);
                }

                IndexOptions indexOptions = new IndexOptions();
                indexOptions.unique(keyArea.getUniqueKeyCode() == DBConstants.UNIQUE);
                indexOptions.name(keyArea.getKeyName());

                IndexModel indexModel = new IndexModel(keys, indexOptions);
                list.add(indexModel);
            }
            if (list.size() > 0)
                collection.createIndexes(list);
            return true;
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
            Object key = this.getDataField(this.getRecord().getCounterField(), false);
            document = new Document(this.getRecord().getCounterField().getFieldName(false, false, true), key);
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
                    Bson search = this.getSearchParams(true);
                    Bson sort = this.getSortParams(true, DBConstants.FILE_KEY_AREA);
                    mongoCursor = collection.find(search).sort(sort).iterator();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                }
            }
//        m_iRecordStatus |= DBConstants.RECORD_NEXT_PENDING;     // If next call is a moveNext(), return unchanged!
        return mongoCursor == null ? false : mongoCursor.hasNext();
    }
    /**
     * Is the first record in the file?
     * @return false if file position is at first record.
     */
    public boolean doHasPrevious() throws DBException
    {
//        if ((m_iRecordStatus & DBConstants.RECORD_AT_BOF) != 0)
//            return false; // Already at BOF (can't be one before)
        if (!this.isOpen())
            this.open();    // Make sure any listeners are called before disabling.
        if (mongoCursor == null)
//                if (isBOF())
//                    if ((m_iRecordStatus & DBConstants.RECORD_INVALID) != 0)
            {
                try {
                    Bson search = this.getSearchParams(true);
                    Bson sort = this.getSortParams(false, DBConstants.FILE_KEY_AREA);
                    mongoCursor = collection.find(search).sort(sort).iterator();
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
        if (iRelPosition == DBConstants.FIRST_RECORD) {
            this.close();
            if (!this.doHasNext())
                return DBConstants.RECORD_INVALID | DBConstants.RECORD_AT_EOF;
        }
        if (iRelPosition == DBConstants.LAST_RECORD) {
            this.close();
            if (!this.doHasPrevious())
                return DBConstants.RECORD_INVALID | DBConstants.RECORD_AT_BOF;
        }
        if (mongoCursor == null)
            return DBConstants.RECORD_INVALID;
        if (!mongoCursor.hasNext())  // TODO If this does a call to mongo, change code to catch EOF error on 'Next'
            return DBConstants.RECORD_INVALID | (iRelPosition < 0 ? DBConstants.RECORD_AT_BOF : DBConstants.RECORD_AT_EOF);
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
        Document find = this.addSelectParams(strSeekSign, DBConstants.FILE_KEY_AREA, false, false, false, null);
        if ((find == null) || (find.isEmpty()))
            return false;
        if (collection == null)
            this.doOpen();
        FindIterable<Document>docs = collection.find(find);
        if (docs == null)
            return false;
        MongoCursor<Document> cursor = docs.cursor();
        if ((cursor == null) || (!cursor.hasNext()))
            return false;
        document = cursor.next();
        return true;
    }
    /**
     * Get the document for the current search.
     * @param seekSign The seek sign (defaults to '=').
     * @param iAreaDesc The temp key are to use
     * @param doc The document to add query to (if null, create a new document)
     * @return The search document if successful.
     * @exception DBException File exception.
     */
    public Document addSelectParams(String seekSign, int iAreaDesc, boolean bAddOnlyMods, boolean bForceUniqueKey, boolean bIncludeTempFields, Document doc) throws DBException {
        if (doc == null)
            doc = new Document();
        int iKeyFieldCount = this.getRecord().getKeyArea().getKeyFields(bForceUniqueKey, bIncludeTempFields);
        if (bAddOnlyMods)
            iKeyFieldCount = this.getRecord().getKeyArea().lastModified(iAreaDesc, bForceUniqueKey) + 1;
        for (int iKeyFieldSeq = DBConstants.MAIN_KEY_FIELD; iKeyFieldSeq < iKeyFieldCount; iKeyFieldSeq++) {
            BaseField field = this.getRecord().getKeyArea().getKeyField(iKeyFieldSeq).getField(iAreaDesc);
            if (field.isNull())
                continue;
            doc = FileListener.addComparisonToDoc(doc, FileListener.getJSONOperator(seekSign), this.getRecord().getKeyArea().getKeyField(iKeyFieldSeq).getField(DBConstants.FILE_KEY_AREA).getFieldName(false, false, true), field.getBsonData());
        }
        return doc;
    }
    /**
     * Get the document describing the sort order
     * @param normalOrder True equals ascending
     * @param keyArea The key are to use for the field name
     * @return The document describing the sort order
     */
    public Document getSortParams(boolean normalOrder, int keyArea) {
        Document bson = new Document();
        for (int i = DBConstants.MAIN_KEY_FIELD ; i < this.getRecord().getKeyArea().getKeyFields() + DBConstants.MAIN_KEY_FIELD; i++) {
            int order = normalOrder ? ASCENDING : DESCENDING;
            if (this.getRecord().getKeyArea().getKeyField(i).getKeyOrder() == DBConstants.DESCENDING)
                order = normalOrder ? DESCENDING : ASCENDING;
            bson.append(this.getRecord().getKeyArea().getKeyField(i).getField(keyArea).getFieldName(false, false, true), order);    // Opposite
        }
        return bson;
    }
    /**
     * Get the SQL SELECT string.
     * @param bUseCurrentValues If true, use the current field value, otherwise, use '?'.
     * @return The SQL select string.
     */
    public Bson getSearchParams(boolean bUseCurrentValues) throws DBException {
        Document doc = new Document();

        this.getRecord().handleInitialKey();        // Set up the smaller key
        doc = this.addSelectParams(FileListener.GREATER_THAN_EQUAL, DBConstants.START_SELECT_KEY,true, true, false, doc);   // Add only if changed
        this.getRecord().handleEndKey();            // Set up the larger key
        doc = this.addSelectParams(FileListener.LESS_THAN_EQUAL, DBConstants.END_SELECT_KEY,true, true, false, doc);   // Add only if changed
        this.getRecord().handleRemoteCriteria(null, this.getRecord().isQueryRecord(), null, doc);  // Add any selection criteria (from behaviors)
        return doc;
    }
    /**
     * Get the document for the current search.
     * @param doc The document to add query to (if null, create a new document)
     * @return The search document if successful.
     * @exception DBException File exception.
     */
    public Document getSearchDocument(Document doc) throws DBException {
        if (doc == null)
            doc = new Document();
        for (int i = DBConstants.MAIN_KEY_FIELD; i < this.getRecord().getKeyArea().getKeyFields() + DBConstants.MAIN_KEY_FIELD; i++) {
            BaseField field = this.getRecord().getKeyArea().getKeyField(i).getField(0);
            if (field.isNull())
                continue;
            Document compareDoc = new Document();
            compareDoc.append(FileListener.getJSONOperator(null), field.getBsonData());
            doc.append(field.getFieldName(false, false, true), compareDoc);
        }
        return doc;
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
            iHandleType = DBConstants.BOOKMARK_HANDLE;
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
        if (collection == null) {
            Document doc = document;
            this.doOpen();
            document = doc;
        }
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
                this.doAddAutoSequence(record);     // Special code to get next sequence
                return;
            }
        try {
            collection.insertOne(document);
            this.dataToField(this.getRecord().getCounterField());   // Grab the id
            m_objLastModHint = this.getRecord().getCounterField().getData();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        document = null;
    }
    /**
     * Add this record.
     * This is the special logic to add a record without db autosequence.
     * <br />Read the largest key.
     * <br />Write the next largest (and loop until you get one)
     * <br />Save the counter for possible get last modified call.
     * @exception DBException File exception.
     */
    public void doAddAutoSequence(Record record) throws DBException
    {
        record = record.getBaseRecord();    // Must operate on the raw table

        m_objLastModHint = null;
        CounterField fldID = (CounterField)record.getCounterField();
        // First step - get the largest current key
        // Save current order and ascending/descending and selection
        int iOrigOrder = record.getDefaultOrder();
//        boolean bOrigDirection = record.getKeyArea(DBConstants.MAIN_KEY_AREA).getKeyField(DBConstants.MAIN_KEY_FIELD).getKeyOrder();
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
//        record.getKeyArea(DBConstants.MAIN_KEY_AREA).getKeyField(DBConstants.MAIN_KEY_FIELD).setKeyOrder(DBConstants.DESCENDING);
        boolean[] rgbEnabled = record.setEnableListeners(false);    // Temporarily disable behaviors

        mongoCursor = null;
//        m_iColumn = 1;
        try {
            if (this.doHasPrevious()) {
                int iRecordStatus = this.doMove(DBConstants.PREVIOUS_RECORD);
                this.dataToField(fldID);    // Move the high value to the ID
            } else {
                int iStartingID = record.getStartingID();
                fldID.setValue(iStartingID - 1, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
            }
        fldID.setModified(true);        // Just to be sure
        } catch (Exception ex) {
            record.setEnableListeners(rgbEnabled); // Re-enable behaviors
            throw this.getDatabase().convertError(ex);
        } finally {
            for (int iFieldSeq = DBConstants.MAIN_FIELD; iFieldSeq <= record.getFieldCount() + DBConstants.MAIN_FIELD - 1; iFieldSeq++)
            {
                record.getField(iFieldSeq).setSelected(brgSelected[iFieldSeq]);
            }
            fldID.setSelected(bCounterSelected);
        }
        mongoCursor = null;
        if (!bCounterSelected)
            fldID.setSelected(true);    // Counter must be selected to write this record
        // Set back, before I forget.
        record.setKeyArea(iOrigOrder);
//        record.getKeyArea(DBConstants.MAIN_KEY_AREA).getKeyField(DBConstants.MAIN_KEY_FIELD).setKeyOrder(bOrigDirection);

        // Second step - bump the key and write until successful.
        int iCount = 0;
        while (iCount++ < MAX_DUPLICATE_ATTEMPTS)
        {
            fldID.setValue(fldID.getValue() + 1, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);  // Bump counter
            int statementType = DBConstants.SQL_INSERT_TABLE_TYPE;
            int iRowsUpdated = 0;
            try   {
                this.fieldsToData(record);
                this.setLastModHint(record);
                collection.insertOne(document);
                this.dataToField(this.getRecord().getCounterField());   // Grab the id
                m_objLastModHint = this.getRecord().getCounterField().getData();
                iRowsUpdated = 1;   // Success
            } catch (Exception ex2)    {
                DBException ex = this.getDatabase().convertError(ex2);
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
        m_objLastModHint = record.getHandle(DBConstants.BOOKMARK_HANDLE);
    }
    /**
     * Optionally set a hint to be used to find the last modified record.
     * This method is only called immediately before adding the physical record to the table.
     * The default logic saves the first field it finds that is modified,
     * since this is the field that would have triped the add logic.
     * NOTE: This is a last resort, try to override getLastModified and use a DB Specific call.
     */
    public void setLastModHint(Record record)
    {
        m_objLastModHint = null;
        m_iLastModType = DBConstants.MAIN_FIELD;
    }
    /**
     * Get the Handle to the last modified or added record.
     * @param iHandleType The handle type.
     * @return The bookmark.
     */
    public Object getLastModified(int iHandleType)
    {
        return m_objLastModHint;
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
//        String strRecordset = record.getSQLUpdate(SQLParams.USE_INSERT_UPDATE_LITERALS);
//        int iType = DBConstants.SQL_UPDATE_TYPE;
//            if (strRecordset != null)
//                /*iRowsUpdated =*/ this.executeUpdate(strRecordset, iType);
//        if (mongoCursor == null)
        if ((document == null) || (fldID.isNull()))
            throw new DBException(DBConstants.INVALID_RECORD);
        try {
            Object key = this.getDataField(this.getRecord().getCounterField(), false);
            Bson bson = new Document(this.getRecord().getCounterField().getFieldName(false, false, true), key);
            collection.replaceOne(bson, document);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        document = null;
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
