package org.jbundle.base.field.convert;

/**
 * @(#)FieldLengthConverter.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.text.MessageFormat;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.util.ResourceConstants;
import org.jbundle.model.Service;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Converter;


/**
 * Fake/override the field length.
 * This is often used to shorten the display length of long fields on grids.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FieldLengthConverter extends FieldConverter
{
    protected int m_iFakeLength = -1;       // Current physical length
    protected int m_iMinimumLength = -1;    // Minimum length

    /**
     * Constructor.
     */
    public FieldLengthConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param iFakeLength The maximum field length to return.
     */
    public FieldLengthConverter(Converter converter, int iFakeLength)
    {
        this();
        this.init(converter, iFakeLength, -1);
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param iFakeLength The maximum field length to return.
     */
    public FieldLengthConverter(Converter converter, int iFakeLength, int iMinimumLength)
    {
        this();
        this.init(converter, iFakeLength, iMinimumLength);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     * @param iFakeLength The maximum field length to return.
     */
    public void init(Converter converter, int iFakeLength, int iMinimumLength)
    {
        super.init(converter);
        m_iFakeLength = iFakeLength;
        m_iMinimumLength = iMinimumLength;
    }
    /**
     * Get the maximum length of this field.
     * @return The fake field length.
     */
    public int getMaxLength() 
    {
        if (m_iFakeLength != -1)
            return m_iFakeLength;
        else
            return super.getMaxLength();
    }
    /**
     * 
     */
    public int setString(String strValue, boolean bDisplayOption, int iMoveMode)
    {
        BaseField field = (BaseField)this.getField();
        if ((strValue == null) || (strValue.length() == 0))
            return super.setString(strValue, bDisplayOption, iMoveMode);  // Don't trip change or display
        if (m_iMinimumLength != -1)
            if (strValue.length() < m_iMinimumLength)
        {
            Task task = field.getRecord().getRecordOwner().getTask();
            Service application = task.getApplication();
            return task.setLastError(MessageFormat.format(application.getResources(ResourceConstants.ERROR_RESOURCE, true).getString("Too Short"), m_iMinimumLength));
        }
        return super.setString(strValue, bDisplayOption, iMoveMode);
    }
}
