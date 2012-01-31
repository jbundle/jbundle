/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * Query description class.
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import org.jbundle.base.db.event.RemoveFromQueryRecordOnCloseHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.FieldList;


/**
 * Query description class.
 *      Base object for all multi-table queries.
 */
public class QueryRecord extends Record
{
	private static final long serialVersionUID = 1L;

	/**
     * The start field number -1 for overriding classes.
     */
    public static final int kQueryRecordLastField = -1;
    /**
     * m_vRecordList contains all the open records for this screen.
     */
    protected RecordList m_vRecordList = null;  // Files in use for this window
    /**
     * The table links.
     */
    protected Vector<TableLink> m_LinkageList = null;
    
    /**
     * Constructor.
     */
    public QueryRecord()
    {
        super();
    }
    /**
     * Constructor.
     * @param recordOwner The recordowner for this queryrecord.
     */
    public QueryRecord(RecordOwner recordOwner)
    {
        this();
        this.init(recordOwner);
    }
    /**
     * Initialize the class.
     * @param recordOwner The recordowner for this queryrecord.
     */
    public void init(RecordOwner recordOwner)
    {
        m_vRecordList = new RecordList(null);
        m_LinkageList = new Vector<TableLink>();
        
        this.addTables(recordOwner);       // Add any query records or overriding records
        boolean bMainRecord = false;
        for (int i = 0; i < this.getRecordlistCount(); i++)
        {   // DO NOT set the recordowner in queries
            Record record = this.getRecordlistAt(i);
            if (record == recordOwner.getMainRecord())
                bMainRecord = true;
            recordOwner.removeRecord(record);
        }
        
        super.init(recordOwner);
        
        if (bMainRecord == true)
            if (this != recordOwner.getMainRecord())
            {
                recordOwner.removeRecord(this);
                recordOwner.addRecord(this, true);
            }
        this.setupRelationships();
    }
    /**
     * Get the Database Name.
     * Always override this method.
     * @return The database name.
     */
    public String getDatabaseName()
    {
        // ****Override this****, If not, try to figure it out
        if (m_vRecordList != null)
            if (this.getRecordlistAt(0) != null)
                return this.getRecordlistAt(0).getDatabaseName();
        return super.getDatabaseName(); // Blank
    }
    /**
     * Get the database type.
     * Always override this method.
     * @return The database type (LOCAL/REMOTE/SCREEN/etc).
     */
    public int getDatabaseType()
    {
        // ****Override this****, If not, try to figure it out
        if (m_vRecordList != null)
            if (this.getRecordlistAt(0) != null)
                return this.getRecordlistAt(0).getDatabaseType() & DBConstants.TABLE_MASK;  // Only type and location
        return super.getDatabaseType(); // LOCAL
    }
    /**
     * Free the query record.
     */
    public void free()
    {
        super.free(); // Free first in case you have to Update() the current record!
        while (m_LinkageList.size() > 0)
        {
            TableLink tableLink = (TableLink)m_LinkageList.elementAt(0);
            tableLink.free();
        }
        m_LinkageList.removeAllElements();
        m_LinkageList = null;
        m_vRecordList.free();   // Free all the records
        m_vRecordList = null;
    }
    /**
     * Add this table link to this query.
     * @param tableLink The tablelink to add.
     */
    public void addRelationship(TableLink tableLink)
    {
        m_LinkageList.addElement(tableLink);
    }
    /**
     * Add this table link to this query.
     * Creates a new tablelink and adds it to the link list.
     */
    public void addRelationship(int iLinkType, Record recLeft, Record recRight, int ifldLeft1, int ifldRight1)
    {
        String fldLeft1 = recLeft.getField(ifldLeft1).getFieldName();
        String fldRight1 = recRight.getField(ifldRight1).getFieldName();
        this.addRelationship(iLinkType, recLeft, recRight, fldLeft1, fldRight1);
    }
    /**
     * Add this table link to this query.
     * Creates a new tablelink and adds it to the link list.
     */
    public void addRelationship(int iLinkType, Record recLeft, Record recRight, String ifldLeft1, String ifldRight1)
    {
        new TableLink(this, iLinkType, recLeft, recRight, ifldLeft1, ifldRight1, null, null, null, null);
    }
    /**
     * Add this table link to this query.
     * Creates a new tablelink and adds it to the link list.
     */
    public void addRelationship(int linkType, Record recLeft, Record recRight, int ifldLeft1, int ifldRight1, int ifldLeft2, int ifldRight2, int ifldLeft3, int ifldRight3)
    {
        String fldLeft1 = recLeft.getField(ifldLeft1).getFieldName();
        String fldRight1 = recRight.getField(ifldRight1).getFieldName();
        String fldLeft2 = ifldLeft2 != -1 ? recLeft.getField(ifldLeft2).getFieldName() : null;
        String fldRight2 = ifldRight2 != -1 ? recRight.getField(ifldRight2).getFieldName() : null;
        String fldLeft3 = ifldLeft3 != -1 ? recLeft.getField(ifldLeft3).getFieldName() : null;
        String fldRight3 = ifldRight3 != -1 ? recRight.getField(ifldRight3).getFieldName() : null;
        new TableLink(this, linkType, recLeft, recRight, fldLeft1, fldRight1, fldLeft2, fldRight2, fldLeft3, fldRight3);
    }
    /**
     * Add this table link to this query.
     * Creates a new tablelink and adds it to the link list.
     */
    public void addRelationship(int linkType, Record recLeft, Record recRight, String fldLeft1, String fldRight1, String fldLeft2, String fldRight2, String fldLeft3, String fldRight3)
    {
        new TableLink(this, linkType, recLeft, recRight, fldLeft1, fldRight1, fldLeft2, fldRight2, fldLeft3, fldRight3);
    }
    /**
     * Add to this SQL Key Filter.
     * @param seekSign The seek sign.
     * @param bAddOnlyMods Add only the keys which have modified?
     * @param bIncludeFileName Include the file name in the string?
     * @param bUseCurrentValues Use current values?
     * @param vParamList The parameter list.
     * @param bForceUniqueKey If params must be unique, if they aren't, add the unique key to the end.
     * @param iAreaDesc The key area to select.
     * @return The select string.
     */
    public String addSelectParams(String seekSign, int areaDesc, boolean bAddOnlyMods, boolean bIncludeFileName, boolean bUseCurrentValues, Vector<BaseField> vParamList, boolean bForceUniqueKey, boolean bIncludeTempFields)
    {
        String sFilter = super.addSelectParams(seekSign, areaDesc, bAddOnlyMods, bIncludeFileName, bUseCurrentValues, vParamList, bForceUniqueKey, bIncludeTempFields);
        if (sFilter.length() > 0)
            return sFilter;     // Sort string was specified for this "QueryRecord"
        for (int iIndex = 0; iIndex < this.getRecordlistCount(); iIndex++)
        {
            Record stmtTable = this.getRecordlistAt(iIndex);
            if (stmtTable != null)
                sFilter += stmtTable.addSelectParams(seekSign, areaDesc, bAddOnlyMods, bIncludeFileName, bUseCurrentValues, vParamList, bForceUniqueKey, bIncludeTempFields);
        }
        return sFilter;
    }
    /**
     * Setup the SQL Sort String.
     * @param bIncludeFileName If true, include the filename with the fieldname in the string.
     * @param bForceUniqueKey If params must be unique, if they aren't, add the unique key to the end.
     * @return The SQL sort string.
     */
    public String addSortParams(boolean bIncludeFileName, boolean bForceUniqueKey)
    {
        String strSort = super.addSortParams(bIncludeFileName, bForceUniqueKey);
        if (strSort.length() > 0)
            return strSort;     // Sort string was specified for this "QueryRecord"
        Record stmtTable = this.getRecordlistAt(0);
        if (stmtTable != null)
            strSort += stmtTable.addSortParams(bIncludeFileName, bForceUniqueKey);
        return strSort;
    }
    /**
     * Add this table to this query.
     * @param record The record to add to this query list.
     */
    public void addTable(Record record)
    {
        m_vRecordList.addRecord(record);
        record.addListener(new RemoveFromQueryRecordOnCloseHandler(this));
    }
    /**
     * Override this to Setup all the tables for this query.
     * @param recordOwner The recordowner to init all the records with.
     */
    public void addTables(RecordOwner recordOwner)
    {
// ie.,   Record record = new Record(recordOwner);
// ie.,   this.addTable(record);
    }
    /**
     * Remove this table from this query.
     * @param record The record to remove from this query.
     */
    public boolean removeRecord(FieldList record)
    {
        return m_vRecordList.removeRecord(record);
    }
    /**
     * Remove this table link from this query.
     * @param tableLink The tableLink to remove from this list.
     */
    public boolean removeTableLink(TableLink tableLink)
    {
        return m_LinkageList.removeElement(tableLink);
    }
    /**
     * Get the record at this position in the list.
     * @param i The index to retrieve.
     * @return The record at that location.
     */
    public Record getRecordlistAt(int i) 
    {
        return (Record)m_vRecordList.getRecordAt(i);
    }
    /**
     * Number of tables in this query.
     * @param The record count.
     */
    public int getRecordlistCount()
    {
        return m_vRecordList.getRecordCount();
    }
    /**
     * Get the main record of this query, to add, edit or whatever.
     * @return The base record.
     */
    public Record getBaseRecord() 
    {
        return this.getRecordlistAt(0).getBaseRecord();
    }
    /**
     * Get this field in the record.
     * This is a little different for a query record, because I have to look through multiple records.
     * @param iFieldSeq The position in the query to retrieve.
     * @return The field at this location.
     */
    public BaseField getField(int iFieldSeq)
    {
        for (int i = 0; i < this.getRecordlistCount(); i++)
        {
            Record record = this.getRecordlistAt(i);
            if (record.getTable() != null)
                if (this.getTable() != null)
                    if (this.isManualQuery()) // Only if I am faking a query
                record = record.getTable().getCurrentTable().getRecord(); // Just in case record is a merge
            int iFieldCount = record.getFieldCount();
            if (iFieldSeq < iFieldCount)
            {
                return record.getField(iFieldSeq);
            }
            iFieldSeq -= iFieldCount;
        }
        return null;    // Not found
    }
    /**
     * Get this field in the record.
     * This is a little different for a query record, because I have to look through multiple records.
     * @param strFieldName The field in the query to retrive.
     * @return The field at this location.
     */
    public BaseField getField(String strFieldName)    // Lookup this field
    {
        for (int i = 0; i < this.getRecordlistCount(); i++)
        {
            Record record = this.getRecordlistAt(i);
            BaseField field = record.getField(strFieldName);
            if (field != null)
                return field;
        }
        return null;    // Not found
    }
    /**
     * Get this field in the record.
     * This is a little different for a query record, because I have to look through multiple records.
     * @param iFieldSeq The position in the query to retrive.
     * @param strTableName The record name.
     * @return The field at this location.
     */
    public BaseField getField(String strTableName, int iFieldSeq)   // Lookup this field
    {
        Record record = this.getRecord(strTableName);
        if (record != null)
            return record.getField(iFieldSeq);
        return null;    // Not found
    }
    /**
     * This is a little different for a query record, because I have to look through multiple records.
     * @param strTableName The record name.
     * @param strFieldName The field in the query to retrive.
     * @return The field at this location.
     */
    public BaseField getField(String strTableName, String strFieldName)     // Lookup this field
    {
        Record record = this.getRecord(strTableName);
        if (record != null)
            return record.getField(strFieldName);
        return null;    // Not found
    }
    /**
     * Number of Fields in this record.
     * @return The field count.
     */
    public int getFieldCount()
    {
        int iFieldCount = 0;
        for (int i = 0; i < this.getRecordlistCount(); i++)
        {
            iFieldCount += this.getRecordlistAt(i).getFieldCount();
        }
        return iFieldCount;
    }
    /**
     * Get this table in the query.
     * @param strFileName The record to retrieve.
     * @return The record with this name.
     */
    public Record getRecord(String sFileName)
    {
        Record pQueryCore = null;
        Record pStmtTable = null;
        pQueryCore = super.getRecord(sFileName);
        if (pQueryCore != null)
            return pQueryCore;
        for (int i = 0; i < this.getRecordlistCount(); i++)
        {
            pStmtTable = this.getRecordlistAt(i);
            pQueryCore = pStmtTable.getRecord(sFileName);
            if (pQueryCore != null)
                return pQueryCore;
        }
        return null;
    }
    /**
     * The name of this table.<p>
     * Create the SQL string to query these records.
     * @param bAddQuotes Add quotes if there are spaces in the table names?
     * @return The sql table name.
     */
    public String makeTableNames(boolean bAddQuotes)
    {
        String cString = this.getTableNames(bAddQuotes);
        Map<String,Object> properties = this.getTable().getDatabase().getProperties();
        if ((cString.length() > 0) && (DBConstants.TRUE.equals(properties.get(SQLParams.USE_BUILT_IN_QUERIES))))
            return cString;     // Use the built-in query (If a valid table/query name and I can use Built-in queries)
        cString = DBConstants.BLANK;
        if (m_LinkageList.size() == 0)
        {
            if (this.getRecordlistCount() == 0)
                return cString;     // Impossible
            for (int i = 0; i < this.getRecordlistCount(); i++)
            {
                Record cTable = this.getRecordlistAt(i);
                if (cString.length() != 0)
                    cString += ", ";
                cString += cTable.getTableNames(bAddQuotes);
            }
        }
        else
        { // If a link is described, use the description
            for (Enumeration<TableLink> e = m_LinkageList.elements() ; e.hasMoreElements() ;)
            {
                TableLink tableLink = e.nextElement();
                String strTemp = tableLink.getTableNames(bAddQuotes, properties);
                if (cString.length() != 0)
                {
                    cString = "(" + cString + ")";
                    strTemp = strTemp.substring(strTemp.indexOf(' '));  // Dont repeat left link
                }
                cString += strTemp;
            }
        }
        return cString;
    }
    /**
     * Is this record a query (or a query stmt)?
     * This class is a query stmt.
     * @return true for a query record.
     */
    public boolean isQueryRecord()
    {
        return true;
    }
    /**
     * Special logic to move a QueryRecord using the tables.
     * The method also retrives any linked record.
     * @param iRelPosition The positions to move.
     * @return The record at this location.
     * @exception DBException File exception.
     */
    public Record moveTableQuery(int iRelPosition) throws DBException
    {
        BaseTable table = null;
        Record record = null;
        Record recordLeft = null;
        boolean bFirstTime = true;
        for (Enumeration<TableLink> e = m_LinkageList.elements() ; e.hasMoreElements() ;)
        {
            TableLink tableLink = e.nextElement();
            table = tableLink.getLeftRecord().getTable();
            if (bFirstTime)
            {   // First time through - far left table!
                recordLeft = record = (Record)table.move(iRelPosition);
                if (record == null)
                    break;
            }
            table = tableLink.getRightRecord().getTable();
            record = table.getRecord();
            record.addNew();
            tableLink.moveDataRight();  // Fill the right table with key
            boolean bFound = record.seek("=");
            if (!bFound)
            {
                if (tableLink.getJoinType() == DBConstants.LEFT_OUTER)  // DBConstants.LEFT_INNER)
                    record.initRecord(DBConstants.DISPLAY);
                else
                {   // Skip this record **NOT TESTED**
                    if ((iRelPosition > 0) || (iRelPosition == DBConstants.FIRST_RECORD))
                        return this.moveTableQuery(+1);
                    return this.moveTableQuery(-1);
                }
            }
            bFirstTime = false;
        }
        // Sync the mode
        if (recordLeft != null)
        {   // Special test for local criteria
            if ((this.handleLocalCriteria(null, false, null) == false)
                || (this.handleRemoteCriteria(null, false, null) == false))
            { // This record didn't pass the test, get the next one that matches
                if ((iRelPosition > 0) || (iRelPosition == DBConstants.FIRST_RECORD))
                    return this.moveTableQuery(+1);
                return this.moveTableQuery(-1);
            }
            this.setEditMode(recordLeft.getEditMode());
        }
        else
            this.setEditMode(DBConstants.END_OF_FILE);
        return recordLeft;
    }
    /**
     * Special logic to open a QueryRecord using the tables.
     * @exception DBException File exception.
     */
    public void openTableQuery() throws DBException
    {
        Record record = null;
        boolean bFirstTime = true;
        for (Enumeration<TableLink> e = m_LinkageList.elements() ; e.hasMoreElements() ;)
        {
            TableLink tableLink = e.nextElement();
            record = tableLink.getLeftRecord();
            if (bFirstTime)
            {
                if (!record.isOpen())
                    record.open();
            }
            record = tableLink.getRightRecord();
             if (!record.isOpen())
                record.open();
            bFirstTime = false;
        }
    }
    /**
     * Mark the main grid file and key order.<p>
     * Included as a utility for backward compatibility (Use SetupKeys now).
     * Basically, this method clones the key area for this record.
     * @param record The record in my list to get the key area from.
     * @param iKeyNo The key area in the record to retrieve.
     * @param The index of this key area.
     */
    public int setGridFile(Record record, int iKeyNo)
    {
        String keyAreaName = null;
        if (iKeyNo != -1)
            keyAreaName = record.getKeyArea(iKeyNo).getKeyName();
        return this.setGridFile(record, keyAreaName);
    }
    /**
     * Mark the main grid file and key order.<p>
     * Included as a utility for backward compatibility (Use SetupKeys now).
     * Basically, this method clones the key area for this record.
     * @param record The record in my list to get the key area from.
     * @param iKeyNo The key area in the record to retrieve.
     * @param The index of this key area.
     */
    public int setGridFile(Record record, String keyAreaName)
    {
        KeyArea recordKeyArea = record.getKeyArea(keyAreaName);
        KeyArea newKeyArea = this.makeIndex(recordKeyArea.getUniqueKeyCode(), recordKeyArea.getKeyName());
        for (int iKeyField = 0; iKeyField < recordKeyArea.getKeyFields(); iKeyField++)
        {
            BaseField field = recordKeyArea.getField(iKeyField);
            boolean bOrder = recordKeyArea.getKeyOrder(iKeyField);
            newKeyArea.addKeyField(field, bOrder);
        }
        return this.getKeyAreaCount() - 1;  // Index of new key area
    }
    /**
     * Override this to add the query links.
     */
    public void setupRelationships()
    {
    }
    /**
     * Select the fields for this query.
     */
    public void selectFields()
    {
        // By default, all fields are selected
        super.selectFields();
        this.checkLinkedFields();   // Make sure linked fields are selected
    }
    /**
     * Make sure all the linked fields are selected.
     */
    public void checkLinkedFields()
    {
        if (this.isManualQuery()) // ? Manually set up query?
        { // It is a requirement that all linked fields be selected in case a manual query is needed
            for (Enumeration<TableLink> e = m_LinkageList.elements() ; e.hasMoreElements() ;)
            {
                TableLink tableLink = e.nextElement();
                for (int i = 0; i < 3; i++)
                {
                    if (tableLink.getLeftField(i) != null)
                        tableLink.getLeftField(i).setSelected(true);
                    if (tableLink.getRightField(i) != null)
                        tableLink.getRightField(i).setSelected(true);
                }
            }
        }
    }
    /**
     * Set the default key order.
     * @param String strKeyName the current index.
     * @return The key area you are set to.
     */
    public KeyArea setKeyArea(String strKeyName)
    {
        KeyArea keyArea = super.setKeyArea(strKeyName);
        if (this.isManualQuery())
        { // Manual table - Special, set target table to this same key
            TableLink tableLink = (TableLink)m_LinkageList.elementAt(0);
            Record record = tableLink.getLeftRecord();
            if (keyArea != null)
            {   // Find the closest matching key and set it
                KeyArea keyAreaQuery = keyArea;
                keyArea = null;
                for (int iKeyArea = 0; iKeyArea < record.getKeyAreaCount(); iKeyArea++)
                {
                    KeyArea keyAreaTarget = record.getKeyArea(iKeyArea);
                    if (keyAreaTarget.getField(0).getFieldName().equals(keyAreaQuery.getField(0).getFieldName()))
                    {   // Found matching key area
                        keyArea = keyAreaTarget;
                        break;
                    }
                }
            }
            if (keyArea == null)
            {   // Try to find this key in the left record
                keyArea = record.setKeyArea(strKeyName);
            }
            if (keyArea != null)
                record.setKeyArea(keyArea.getKeyName());
        }
        return keyArea;
    }
    /**
     * Is this a manual query (a fake query)?
     * @return true If the current table is a QueryTable.
     */
    public boolean isManualQuery()
    {
        boolean bIsManual = false;
        if (this.getTable().getCurrentTable() instanceof QueryTable) // Only if I am faking a query
            bIsManual = true;
        if (this.getDatabaseType() == DBConstants.MANUAL_QUERY)
            bIsManual = true;
        return bIsManual;
    }
    /**
     * Is one of the sub-queries a multi-table.
     * @return true if this is a query on top of an object query.
     */
    public boolean isComplexQuery()
    {
        for (int i = 0; i < this.getRecordlistCount(); i++)
        {
            if (this.getRecordlistAt(i).getTable() instanceof org.jbundle.base.db.shared.MultiTable)
                return true;
        }
        return false;
    }
}
