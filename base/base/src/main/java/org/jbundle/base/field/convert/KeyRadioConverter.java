/*

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)KeyRadioConverter.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * A converter to set a value to a specific key area of a file.
 * Note: You will still need a behavior on the field to reqery the file,
 * this converter only changes the order (and displays a correct key area description,
 * since RadioConverter inherits from DescConverter).
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class KeyRadioConverter extends RadioConverter
{
    /**
     * The key area for this converter.
     */
    protected int m_iKeyArea = -1;
    /**
     * The target record.
     */
    protected Record m_recordTarget = null;

    /**
     * Constructor.
     */
    public KeyRadioConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param recordTarget The record that this key radio button refers to.
     * @param iKeyArea The key area this button refers to.
     */
    public KeyRadioConverter(Converter converter, Record recordTarget, int iKeyArea)
    {
        this();
        this.init(converter, recordTarget, iKeyArea);
    }
    /**
     * Init this converter.
     */
    public void init(Converter converter, Record recordTarget, int iKeyArea)
    {
        super.init(converter, Constants.BLANK, true);

        m_iKeyArea = iKeyArea;
        m_recordTarget = recordTarget;
        m_objTarget = Integer.toString(iKeyArea);
        
        m_strAltDesc = "Key " + m_objTarget;    // Default
        if (m_recordTarget != null)
        {
            BaseField mainField = m_recordTarget.getTable().getCurrentTable().getRecord().getKeyArea(m_iKeyArea).getField(DBConstants.MAIN_KEY_FIELD);
            m_strAltDesc = mainField.getFieldDesc();
        }
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
        m_recordTarget = null;
    }
}
