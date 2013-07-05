/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.db.mem.memory;

/**
 * @(#)KeyArea.java   1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.io.Serializable;
import java.util.Vector;

import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.KeyAreaInfo;
import org.jbundle.thin.base.db.buff.BaseBuffer;
import org.jbundle.thin.base.db.mem.base.PKeyArea;


/**
 * Definition of a memory-based raw data key area
 * An KeyArea describes a particular key area (fields and order)
 *
 * @version 1.0.0
 * @author Don Corley
 */
public class MKeyArea extends PKeyArea
    implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * Flag indicating you are at BOF.
     */
    public final static int AT_BOF = -1;
    /**
     * Flag indicating you are at EOF.
     */
    public final static int AT_EOF = -2;
    /**
     * Flag indicating you are at an Unknown position.
     */
    public final static int UNKNOWN_POSITION = -3;
    /**
     * An ordered list of all the data objects.
     */
    protected Vector<BaseBuffer> m_VectorObjects = null;    // Objects in this Class
    /**
     * The cached current position. This position is used to speed access.. the actual
     * position must be attainable from the user's key area (TEMP_KEY_AREA).
     */
    protected int m_iIndex = -1;

    /**
     * Constructor.
     */
    public MKeyArea()
    {
        super();
    }
    /**
     * Constructor.
     * @param pTable The raw data table that this raw data key area is being added to.
     */
    public MKeyArea(MTable pTable)
    {
        this();
        this.init(pTable);
    }
    /**
     * Initialize the class.
     * @param pTable The raw data table that this raw data key area is being added to.
     */
    public void init(MTable pTable)   
    {
        super.init(pTable);
        m_VectorObjects = new Vector<BaseBuffer>();   // Objects in this Class
        m_iIndex = AT_BOF;
    }
    /**
     * Release the Objects in this KeyArea.
     */
    public void free()
    {
        // Remove all the objects if this is the primary key
        if (this.getPTable().getPKeyArea(Constants.MAIN_KEY_AREA) == this)
        {
            for (int i = m_VectorObjects.size() - 1 ; i >= 0 ;i--)
            {
                BaseBuffer buffer = (BaseBuffer)m_VectorObjects.elementAt(i);
                buffer.free();
            }
        }
        m_VectorObjects.removeAllElements();
        m_VectorObjects = null;

        super.free();
    }
    /**
     * Compare these two keys and return the compare result.
     * @param areaDesc The area that the key to compare is in.
     * @param strSeekSign The seek sign.
     * @param table The table.
     * @param keyArea The table's key area.
     * @return The compare result (-1, 0, or 1).
     */
    public int compareKeys(int iAreaDesc, String strSeekSign, FieldTable table, KeyAreaInfo keyArea)
    {
        int compareValue = super.compareKeys(iAreaDesc, strSeekSign, table, keyArea);
        if (compareValue != 0)
            return compareValue;
        if (keyArea.getUniqueKeyCode() == Constants.UNIQUE)
            return compareValue;        // Found a matching record
        if ((strSeekSign == null) || (strSeekSign.equals("==")))
        {   // looking for an exact match!
            KeyAreaInfo keyArea2 = table.getRecord().getKeyArea(Constants.MAIN_KEY_AREA);
            if (keyArea == keyArea2)
                return compareValue;        // Error? Primary key has to be unique
            compareValue = keyArea2.compareKeys(iAreaDesc);
        }
        return compareValue;
    }
    /**
     * Am I still pointing at this buffer?
     * If this buffer is the buffer at the current index, then the current index is correct.
     * @param buffer The buffer to compare.
     * @exception DBException File exception.
     */
    public boolean atCurrent(BaseBuffer buffer) throws DBException
    {
        boolean bAtCurrent = false;
        try   {
            if ((m_iIndex >=0) && (m_iIndex < m_VectorObjects.size()))
                bAtCurrent = (buffer == m_VectorObjects.elementAt(m_iIndex));
        }
        catch (ArrayIndexOutOfBoundsException e)    {
            bAtCurrent = false;
        }
        return bAtCurrent;
    }
    /**
     * Delete the key from this buffer.
     * @param table The basetable.
     * @param keyArea The basetable's key area.
     * @param buffer The buffer to compare.
     * @exception DBException File exception.
     */
    public void doRemove(FieldTable table, KeyAreaInfo keyArea, BaseBuffer buffer) throws DBException
    {
        super.doRemove(table, keyArea, buffer);
    }
    /**
     * Remove the entry at the current location.
     * Note: This call requires a valid current index, so it must be within the sync call that set the index (seek).
     * @param buffer The buffer to remove.
     * @exception DBException File exception.
     */
    public void removeCurrent(BaseBuffer buffer) throws DBException
    {
        m_VectorObjects.removeElementAt(m_iIndex);
        m_iIndex--;     // Point to the record before this one
    }
    /**
     * Move the position of the record.
     * Note: This method requires an accurate m_iIndex to operate properly, so
     * before calling this method, check to see that the buffer is the same as the last
     * movenext, if not... reposition the index with seek, before calling this method (all synchronized).
     * @param iRelPosition The number and order of record to read through.
     * @param table The basetable.
     * @param keyArea The key area.
     * @exception DBException File exception.
     */
    public BaseBuffer doMove(int iRelPosition, FieldTable table, KeyAreaInfo keyArea) throws DBException
    {
        if (keyArea.getKeyOrder(Constants.MAIN_KEY_FIELD) == Constants.DESCENDING)
        {
            if (iRelPosition == Constants.FIRST_RECORD)
                iRelPosition = Constants.LAST_RECORD;
            else if (iRelPosition == Constants.LAST_RECORD)
                iRelPosition = Constants.FIRST_RECORD;
            else
                iRelPosition = -iRelPosition; // Reverse direction
        }
        if (iRelPosition == Constants.FIRST_RECORD)
            m_iIndex = 0;
        else if (iRelPosition == Constants.LAST_RECORD)
            m_iIndex = m_VectorObjects.size() - 1;
        else
        {
            // Now, calc the new position.
            if (m_iIndex == UNKNOWN_POSITION)
                throw new DBException(Constants.INVALID_RECORD);    // Can't move from here!
            if (iRelPosition >= 0) if (m_iIndex == AT_EOF)
                return null;    // Can't move past the EOF
            if (iRelPosition <= 0) if (m_iIndex == AT_BOF)
                return null;    // Can't move past the BOF
            m_iIndex += iRelPosition;   // Bump to the next record
        }
        if (m_iIndex == -1)
            m_iIndex = AT_BOF;
        else if (m_iIndex < -1)
        {
            m_iIndex = UNKNOWN_POSITION;
            throw new DBException(Constants.INVALID_RECORD);
        }
        else if (m_iIndex == m_VectorObjects.size())
            m_iIndex = AT_EOF;
        else if (m_iIndex > m_VectorObjects.size())
        {
            m_iIndex = UNKNOWN_POSITION;
            throw new DBException(Constants.INVALID_RECORD);
        }
        else
        {
            try   {
                BaseBuffer buffer = (BaseBuffer)m_VectorObjects.elementAt(m_iIndex);
                return buffer;
            }
            catch (ArrayIndexOutOfBoundsException e)    {
                m_iIndex = UNKNOWN_POSITION;
                throw new DBException(Constants.INVALID_RECORD);
            }
        }
        return null;
    }
    /**
     * Read the record that matches this record's temp key area.<p>
     * WARNING - This method changes the current record's buffer.
     *  @param strSeekSign - Seek sign:<p>
     *  <pre>
     *  "=" - Look for the first match.
     *  "==" - Look for an exact match (On non-unique keys, must match primary key also).
     *  ">" - Look for the first record greater than this.
     *  ">=" - Look for the first record greater than or equal to this.
     *  "<" - Look for the first record less than or equal to this.
     *  "<=" - Look for the first record less than or equal to this.
     *  </pre>
     *      returns: success/failure (true/false).
     * @param table The basetable.
     * @param keyArea The key area.
     * @exception DBException File exception.
     */
    public BaseBuffer doSeek(String strSeekSign, FieldTable vectorTable, KeyAreaInfo keyArea) throws DBException
    {
        BaseBuffer buffer = null;
        int iLowestMatch = -1;  // For non-exact matches
        keyArea.setupKeyBuffer(null, Constants.TEMP_KEY_AREA);
        if (keyArea.getUniqueKeyCode() != Constants.UNIQUE)   // The main key is part of the comparison
            vectorTable.getRecord().getKeyArea(Constants.MAIN_KEY_AREA).setupKeyBuffer(null, Constants.TEMP_KEY_AREA);
        int iLowKey = 0;
        int iHighKey = this.getRecordCount() - 1;
        while (iLowKey <= iHighKey)
        {
            m_iIndex = (iLowKey + iHighKey) / 2;
            buffer = (BaseBuffer)m_VectorObjects.elementAt(m_iIndex);
            buffer.resetPosition();	// Being careful
            buffer.bufferToFields(vectorTable.getRecord(), Constants.DONT_DISPLAY, Constants.READ_MOVE);
            int iCompare = this.compareKeys(Constants.TEMP_KEY_AREA, strSeekSign, vectorTable, keyArea);
            if (iCompare < 0)
                iLowKey = m_iIndex + 1;   // target key is smaller than this key
            else if (iCompare > 0)
                iHighKey = m_iIndex - 1;    // target key is larger than this key
            else    // if (iCompare == 0)
            {
                if (strSeekSign.equals(">"))
                {
                    iLowKey = m_iIndex + 1;   // target key is larger than this key
                    iLowestMatch = iLowKey;     // Not looking for an exact match (lowest so far)
                }
                else if (strSeekSign.equals("<"))
                {
                    iHighKey = m_iIndex - 1;    // target key is larger than this key
                    iLowestMatch = iHighKey;        // Not looking for an exact match (lowest so far)
                }
                else
                {
                    if (keyArea.getUniqueKeyCode() == Constants.UNIQUE)
                    {
                        return buffer;      // Found a matching record
                    }
                    else
                    {
                        if ((strSeekSign == null) || (strSeekSign.equals("==")))
                        {   // Found an exact matching record
                            return buffer;
                        }
                        else
                        { // Not looking for an exact match
                            iLowestMatch = m_iIndex;        // lowest so far.
                            iHighKey = m_iIndex - 1;    // target key is larger than this key
                        }
                    }
                }
            }
        }
        m_iIndex = iHighKey;        // Point to next lower key
        if (keyArea.getKeyOrder(Constants.MAIN_KEY_FIELD) == Constants.DESCENDING)  // Rarely
            m_iIndex = iLowKey;     // Point to next higher key
        if (iLowestMatch == -1)
        {   // For non-exact searches, return the next/previous
            if ((strSeekSign.equals(">=")) || (strSeekSign.equals(">")))
                iLowestMatch = m_iIndex + 1;
            if ((strSeekSign.equals("<=")) || (strSeekSign.equals("<")))
                iLowestMatch = m_iIndex;
        }
        if (iLowestMatch != -1)   if (iLowestMatch < m_VectorObjects.size())
        {   // There was a non-exact match to this seek
            m_iIndex = iLowestMatch;
            buffer = (BaseBuffer)m_VectorObjects.elementAt(m_iIndex);
            buffer.bufferToFields(vectorTable.getRecord(), Constants.DONT_DISPLAY, Constants.READ_MOVE);
            return buffer;      // Match (for a non-exact search)
        }
        return null;    // No match
    }
    /**
     * Add this record (Always called from the record class).
     * @param table The basetable.
     * @param keyArea The key area.
     * @param bufferNew The buffer to add.
     * @exception DBException File exception.
     */
    public void doWrite(FieldTable vectorTable, KeyAreaInfo keyArea, BaseBuffer bufferNew) throws DBException
    {
        super.doWrite(vectorTable, keyArea, bufferNew);
    }
    /**
     * Insert this record at the current location.
     * @param table The basetable.
     * @param keyArea The key area.
     * @param bufferNew The buffer to add.
     * @param iRelPosition relative position to add the record.
     * @exception DBException File exception.
     */
    public void insertCurrent(FieldTable vectorTable, KeyAreaInfo keyArea, BaseBuffer bufferNew, int iRelPosition) throws DBException
    {
        m_iIndex += iRelPosition;
        m_VectorObjects.insertElementAt(bufferNew, m_iIndex);
        m_iIndex = -1;  // The index can't be used in caches anymore.
    }
    /**
     * Get the record count.
     * @return The record count (or -1 if unknown).
     */
    public int getRecordCount()
    {
        int iSize = m_VectorObjects.size();
        return iSize;
    }
}
