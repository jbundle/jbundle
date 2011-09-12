/**
 * @(#)CodeTypeField.
 * Copyright © 2011 jbundle.org. All rights reserved.
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
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;

/**
 *  CodeTypeField - .
 */
public class CodeTypeField extends StringPopupField
{
    private static final long serialVersionUID = 1L;
    /**
     * Default constructor.
     */
    public CodeTypeField()
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
    public CodeTypeField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
    }

    public static final String kCodeTypeFieldFile = null; // Screen field
    /**
     * Get the conversion Map.
     */
    public String[][] getPopupMap()
    {
        String string[][] = {
                {"BASE", "Base"}, 
                {"THIN", "Thin"},
                {"RESOURCE_CODE", "Resource code"},
                {"RESOURCE_PROPERTIES", "Resource properties"}
        };
        return string;
    }
    /**
     * GetCodeType Method.
     */
    public ClassProject.CodeType getCodeType()
    {
        String code = this.toString();
        if ("BASE".equalsIgnoreCase(code))
            return ClassProject.CodeType.BASE;
        if ("THIN".equalsIgnoreCase(code))
            return ClassProject.CodeType.THIN;
        if ("RESOURCE_CODE".equalsIgnoreCase(code))
            return ClassProject.CodeType.RESOURCE_CODE;
        if ("RESOURCE_PROPERTIES".equalsIgnoreCase(code))
            return ClassProject.CodeType.RESOURCE_PROPERTIES;
        return null;
    }
    /**
     * SetCodeType Method.
     */
    public int setCodeType(ClassProject.CodeType codeType)
    {
        String codeString = null;
        if (codeType == ClassProject.CodeType.BASE)
            codeString = "BASE";
        if (codeType == ClassProject.CodeType.THIN)
            codeString = "THIN";
        if (codeType == ClassProject.CodeType.RESOURCE_CODE)
            codeString = "RESOURCE_CODE";
        if (codeType == ClassProject.CodeType.RESOURCE_PROPERTIES)
            codeString = "RESOURCE_PROPERTIES";
        return this.setString(codeString);
    }

}
