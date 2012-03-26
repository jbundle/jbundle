/**
 * @(#)ClassFieldsTypeField.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;

/**
 *  ClassFieldsTypeField - .
 */
public class ClassFieldsTypeField extends StringPopupField
{
    public static final String INCLUDE_CLASS_PACKAGE = "I";
    public static final String INCLUDE_CLASS = "C";
    public static final String CLASS_FIELD = "F";
    public static final String NATIVE_FIELD = "N";
    public static final String INCLUDE_PACKAGE = "P";
    public static final String CLASS_NAME = "X";
    public static final String SCREEN_CLASS_NAME = "S";
    /**
     * Default constructor.
     */
    public ClassFieldsTypeField()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public ClassFieldsTypeField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
        if (iDataLength == DBConstants.DEFAULT_FIELD_LENGTH)
            m_iMaxLength = 1;

    }
    /**
     * Get the default value.
     * @return The default value.
     */
    public Object getDefault()
    {
        Object objDefault = super.getDefault();
        if (objDefault == null)
            objDefault = INCLUDE_CLASS_PACKAGE;
        return objDefault;
    }
    /**
     * Get the conversion Map.
     */
    public String[][] getPopupMap()
    {
        String string[][] = {
        {INCLUDE_CLASS_PACKAGE, "Include class package"},
        {INCLUDE_PACKAGE, "Include package"},
        {INCLUDE_CLASS, "Include class"},
        {CLASS_FIELD, "Class field"},
        {NATIVE_FIELD, "Native field"},
        {CLASS_NAME, "Class name"},
        {SCREEN_CLASS_NAME, "Screen class name"},
        };
        return string;
    }

}
