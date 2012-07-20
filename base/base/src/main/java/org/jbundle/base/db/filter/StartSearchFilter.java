/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.filter;

/**
 * @(#)StartSearchFilter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Vector;

import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.DateField;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.field.event.FieldReSelectHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Converter;


/**
 * StartSearchFilter is a specialized version of the CompareFileFilter to compare
 * fields that change with the current key area.
 * For example, you could have a grid display with the ability to change the order.
 * You could have a string field to display all records greater the the field.
 * This class has to have the ability to compare a field against whatever key
 * area is currently selected, including the ability to compare a string to a date.
 * Start the query with this value (ie., WHERE firstIndexField >= fldToCompare).
 * Note: This is a server-only behavior, but doesn't need any more code as CompareFileFilter
 * does it all.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class StartSearchFilter extends CompareFileFilter
{
    /**
     * If the compare field is a date, use this field to convert the string to a date and compare.
     */
    protected DateField m_fldFakeDate = null;

    /**
     * Constructor.
     */
    public StartSearchFilter()
    {
        super();
    }
    /**
     * Constructor.
     * @param pfldToCompare The field to compare.
     */
    public StartSearchFilter(BaseField fldToCompare)
    {
        this();
        this.init(null, null, null, null, true, null, fldToCompare);
    }
    /**
     * Constructor.
     * @param pfldToCompare The field to compare.
     */
    public StartSearchFilter(BaseField fldToCompare, String strCompareOperation)
    {
        this();
        this.init(null, null, strCompareOperation, null, true, null, fldToCompare);
    }
    /**
     * Constructor.
     * NOTE: I use the same exact init params as super since this is a client/server filter.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param pfldToCompare The field to compare.
     */
    public void init(Record record, String strToCompare, String strSeekSign, Converter pconvFlag, boolean bDontFilterIfNullCompare, BaseField fldToCheck, BaseField fldToCompare)
    {
        m_fldFakeDate = null;
        super.init(record, null, strToCompare, null, pconvFlag, bDontFilterIfNullCompare, fldToCheck, fldToCompare);
        if (strSeekSign == null)
            m_strSeekSign = DBConstants.BLANK;    // I Must do this here because I don't want the default value
    }
    /**
     * Free this listener.
     */
    public void free()
    {
        if (m_fldFakeDate != null)
            m_fldFakeDate.free();
        m_fldFakeDate = null;
        super.free();
    }
    /*
     * Set up/do the remote criteria.
     * @param strbFilter The SQL query string to add to.
     * @param bIncludeFileName Include the file name with this query?
     * @param vParamList The param list to add the raw data to (for prepared statements).
     * @return True if you should not skip this record (does a check on the local data).
     */
    public boolean doRemoteCriteria(StringBuffer strbFilter, boolean bIncludeFileName, Vector<BaseField> vParamList)
    {
        BaseField fldToCompare = m_fldToCompare; // Cache this in case it is changed
    
        KeyArea keyArea = this.getOwner().getKeyArea();
        m_fldToCheck = keyArea.getField(DBConstants.MAIN_KEY_FIELD);
        String strSeekSignSave = m_strSeekSign;
        if (m_strSeekSign == DBConstants.BLANK)
        {
            m_strSeekSign = GREATER_THAN_EQUAL;
            if (keyArea.getKeyOrder(DBConstants.MAIN_KEY_FIELD) == DBConstants.DESCENDING)
                m_strSeekSign = LESS_THAN_EQUAL;
        }
                // Now, we have to convert the field to a CDate for the compare to use
        if (m_fldToCheck instanceof DateField)
            this.fakeTheDate();   // Convert the string field to a date for compare
        boolean bDontSkip = super.doRemoteCriteria(strbFilter, bIncludeFileName, vParamList); // Dont skip this record
        m_fldToCheck = null;
        m_fldToCompare = fldToCompare;   // Set this value back
        m_strSeekSign = strSeekSignSave;  // Restore this.
        return bDontSkip;
    }
    public static final String FAKE_DATE = "Fake Date";
    /**
     * If the current key starts with a date, convert the search field to a date and compare.
     */
    public void fakeTheDate()
    {
        int ecErrorCode;
        BaseField fldToCompare = m_fldToCompare; // Cache this in case it is changed
        if (m_fldToCompare.isNull())
            return;   // Don't convert the date if this is NULL!
        if (m_fldFakeDate == null)
            m_fldFakeDate = new DateField(null, FAKE_DATE, DBConstants.DEFAULT_FIELD_LENGTH, FAKE_DATE, null);   //m_FieldToCompare->GetFile()->GetField(kBkFakeDate);
        m_fldToCompare = m_fldFakeDate;
        ecErrorCode = m_fldToCompare.moveFieldToThis(fldToCompare, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);  // Convert the date
        boolean bOldState = fldToCompare.getListener(FieldReSelectHandler.class.getName()).setEnabledListener(false); // Disable the reselect listener for a second
        if ((ecErrorCode == DBConstants.NORMAL_RETURN) && (((NumberField)m_fldToCompare).getValue() != 0))
            fldToCompare.moveFieldToThis(m_fldToCompare, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);    // Reformat and display the date
        else
        {
            fldToCompare.initField(DBConstants.DISPLAY); // Clear this wierd field
            m_fldToCompare.initField(DBConstants.DISPLAY);  // Clear this wierd field
        }
        fldToCompare.getListener(FieldReSelectHandler.class.getName()).setEnabledListener(bOldState);    // Reenable the reselect listener for a second
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new StartSearchFilter(m_fldToCompare);
    }
}
