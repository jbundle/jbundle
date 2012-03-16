/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * @(#)TableLink.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import java.util.Map;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;


/**
 * TableLink - Contains all the table links in this query.
 */
public class TableLink extends Object
{
    /**
     * My parent query record.
     */
    protected QueryRecord m_QueryRecord = null;
    /**
     * The join type (LEFT_INNER/RIGHT/etc).
     */
    protected int m_JoinType = DBConstants.LEFT_INNER;
    /**
     * The record to my left.
     */
    protected Record m_recLeft = null;
    /**
     * The record to my right.
     */
    protected Record m_recRight = null;
    /**
     * The equal fields on the left.
     */
    protected Object m_rgiFldLeft[] = null;
    /**
     * The equal fields on the right.
     */
    protected String m_rgiFldRight[] = null;

    /**
     * Constructor.
     */
    public TableLink()
    {
        super();
    }
    /**
     * Constructor.
     */
    public TableLink(QueryRecord queryRecord, int joinType, Record recLeft, Record recRight)
    {
        this();
        this.init(queryRecord, joinType, recLeft, recRight);
    }
    /**
     * Constructor.
     */
    public TableLink(QueryRecord queryRecord, int joinType,
            Record recLeft, Record recRight,
            String fldLeft1, String fldRight1,
            String fldLeft2, String fldRight2,
            String fldLeft3, String fldRight3)
    {
        this();
        this.init(queryRecord, joinType, recLeft, recRight);
        this.addLink(fldLeft1, fldRight1);
        this.addLink(fldLeft2, fldRight2);
        this.addLink(fldLeft3, fldRight3);
    }
    /**
     * init.
     */
    public void init(QueryRecord queryRecord, int joinType, Record recLeft, Record recRight)
    {
        m_QueryRecord = queryRecord;
        m_JoinType = joinType;

        m_recLeft = recLeft;
        m_recRight = recRight;

        m_QueryRecord.addRelationship(this);
    }
    public void addLink(int fsLeft, Object fsRight)
    {
        if (fsLeft == -1)
            return;
        this.addLink(new Integer(fsLeft), fsRight);
    }
    public void addLink(Object objLeft, String fsRight)
    {
        if (objLeft == null)
            return;
        int iNewSize = 2;
        if (m_rgiFldLeft != null)
            iNewSize = m_rgiFldLeft.length + 1;
        Object[] rgiFldLeftOld = m_rgiFldLeft;
        String[] rgiFldRightOld = m_rgiFldRight;
        m_rgiFldLeft = new Object[iNewSize];
        m_rgiFldRight = new String[iNewSize];
        if (rgiFldLeftOld != null)
        {
            for (int i = 1; i < iNewSize - 1; i++)
            {
                m_rgiFldLeft[i] = rgiFldLeftOld[i];
                m_rgiFldRight[i] = rgiFldRightOld[i];
            }
        }
        m_rgiFldLeft[iNewSize - 1] = objLeft;
        m_rgiFldRight[iNewSize - 1] = fsRight;
    }
    /**
     * Free.
     */
    public void free()
    {
        m_QueryRecord.removeTableLink(this);
        m_recLeft = null;
        m_recRight = null;
        m_rgiFldLeft = null;
        m_rgiFldRight = null;
    }
    /**
     * Get the join type for this link.
     * @return the join type.
     */
    public int getJoinType()
    {
        return m_JoinType;
    }
    /**
     * Get the left table for this link.
     * @return The left record.
     */
    public Record getLeftRecord()
    {
        return m_recLeft;
    }
    /**
     * Get the right table for this link.
     * @return the right record.
     */
    public Record getRightRecord()
    {
        return m_recRight;
    }
    /**
     * Get the equal field from the left table for this link.
     * @param iFieldSeq The sequence of this field.
     * @return The field.
     */
    public BaseField getLeftField(int iFieldSeq)
    {
        if (m_rgiFldLeft.length <= iFieldSeq)
            return null;
        if ((m_rgiFldLeft[iFieldSeq] instanceof String))
            if (!Utility.isNumeric((String)m_rgiFldLeft[iFieldSeq]))
                return this.getLeftRecord().getTable().getCurrentTable().getRecord().getField((String)m_rgiFldLeft[iFieldSeq]);
        if (!(m_rgiFldLeft[iFieldSeq] instanceof Integer))
            return null;
        int fs = ((Integer)m_rgiFldLeft[iFieldSeq]).intValue();
        return this.getLeftRecord().getTable().getCurrentTable().getRecord().getField(fs);
    }
    /**
     *  Get the equal field from the right table for this link.
     * @param iFieldSeq The sequence of this field.
     * @return The field.
     */
    public BaseField getRightField(int iFieldSeq)
    {
        if (m_rgiFldRight.length <= iFieldSeq)
            return null;
        if (m_rgiFldRight[iFieldSeq] == null)
            return null;
        // Note that the right record does NOT get the current record (because you will need the BASE record to do a seek).
        return this.getRightRecord().getField(m_rgiFldRight[iFieldSeq]);
    }
    public String getLeftFieldNameOrValue(int iFieldSeq, boolean bAddQuotes, boolean bIncludeFileName)
    {
        if (m_rgiFldLeft.length <= iFieldSeq)
            return null;
        BaseField field = this.getLeftField(iFieldSeq);
        if (field != null)
            return field.getFieldName(bAddQuotes, bIncludeFileName);
        if ((m_rgiFldLeft[iFieldSeq] instanceof String))
            if (!Utility.isNumeric((String)m_rgiFldLeft[iFieldSeq]))
                return (String)m_rgiFldLeft[iFieldSeq];
        return m_rgiFldLeft[iFieldSeq].toString();
    }
    /**
     * Get the SQL table names for this link.
     * @param bAddQuotes Add quotes to fields with spaces?
     * @param properties The database properties.
     * @return The SQL table string.
     */
    public String getTableNames(boolean bAddQuotes, Map<String, Object> properties)
    {
        String strString = "";
        strString += this.getLeftRecord().getTableNames(bAddQuotes);
        String strOn = "ON";
        switch (m_JoinType)
        {
        case DBConstants.LEFT_INNER:
            if (properties.get("INNER_JOIN") != null)
                strString += ' ' + (String)properties.get("INNER_JOIN") + ' ';
            else
                strString += ' ' + "INNER_JOIN ";
            if (properties.get("INNER_JOIN_ON") != null)
                strOn = (String)properties.get("INNER_JOIN_ON");
            break;
        case DBConstants.LEFT_OUTER:
            strString += " LEFT JOIN ";
            break;
        }
        strString += this.getRightRecord().getTableNames(bAddQuotes);
        strString += ' ' + strOn + ' ';
        for (int i = 1; i < m_rgiFldLeft.length; i++)
        {
            if (this.getLeftField(i) == null)
                break;
            if (i > 1)
                strString += " AND ";
            strString += this.getLeftFieldNameOrValue(i, bAddQuotes, true); // Include file name
            strString += " = ";
            strString += this.getRightField(i).getFieldName(bAddQuotes, true); // Include file name
        }
        return strString;
    }
    /**
     * Fill the right fields with the values from the left fields.
     */
    public void moveDataRight()
    {
        for (int i = 1; i < m_rgiFldLeft.length; i++)
        {
            if (this.getRightField(i) == null)
                break;
            if (this.getLeftField(i) == null)
                this.getRightField(i).setString(this.getLeftFieldNameOrValue(i, true, true), DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
            else
                this.getRightField(i).moveFieldToThis(this.getLeftField(i), DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
        }
    }
    /**
     * Move these key fields to the other file (return true if they were already the same!).
     * (Not tested!)
     */
    public boolean setupKey(boolean reverse)
    {
        boolean sameFlag = true;
        for (int index = 1; index < m_rgiFldLeft.length; index++)
        {
            BaseField fldLeft = this.getLeftField(index);
            BaseField fldRight = this.getRightField(index);
            if (fldRight == null)
                break;
            if (fldLeft == fldRight)
            {
            }
            else
            {
                sameFlag = false;
                if (reverse)
                {
                    if (fldLeft != null)
                        fldLeft.moveFieldToThis(fldRight, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
                }
                else
                {
                    if (fldLeft == null)
                        fldRight.setString(this.getLeftFieldNameOrValue(index, true, true), DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
                    else
                        fldRight.moveFieldToThis(fldLeft, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
                }
            }
        }
        return sameFlag;
    }
}
