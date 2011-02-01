package org.jbundle.base.field.event;

/**
 * @(#)ReadSecondIfCheckHandler.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Converter;


/**
 * Read secondary of record only if this convFlag is checked.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ReadSecondIfCheckHandler extends ReadSecondaryHandler
{
    /**
     * The flag to check.
     */
    protected Converter m_convFlag = null;

    /**
     * Constructor.
     */
    public ReadSecondIfCheckHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The secondary record to read.
     * @param iQueryKeyArea The key area to read from.
     * @param bCloseOnFree Close the record when this behavior is removed?
     * @param convFlag The flag to check.
     */
    public ReadSecondIfCheckHandler(Record record, int iQueryKeyArea, boolean bCloseOnFree, Converter convFlag)
    {
        this();
        this.init(null, record, iQueryKeyArea, bCloseOnFree, convFlag);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param record The secondary record to read.
     * @param iQueryKeyArea The key area to read from.
     * @param bCloseOnFree Close the record when this behavior is removed?
     * @param convFlag The flag to check.
     */
    public void init(BaseField field, Record record, int iQueryKeyArea, boolean bCloseOnFree, Converter convFlag)
    {
        m_convFlag = convFlag;
        super.init(field, record, iQueryKeyArea, bCloseOnFree, false, true);
    }
    /**
     * The Field has Changed.
     * If the flag is true, do inherited (read secondary), otherwise do initRecord.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        if (m_convFlag.getState())
            return super.fieldChanged(bDisplayOption, iMoveMode);
        else
        {
            try
            {
                m_record.initRecord(bDisplayOption);    // Clear the fields
                m_record.addNew();
            }
            catch(DBException ex)
            {
                ex.printStackTrace(); // Never
            }
        }
        return DBConstants.NORMAL_RETURN;
    }
}
