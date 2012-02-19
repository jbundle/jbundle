/**
 * @(#)BaseFixData.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.data.importfix.base;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.thread.*;
import org.jbundle.thin.base.screen.*;

/**
 *  BaseFixData - Base process to import or fix data.
 */
public class BaseFixData extends BaseProcess
{
    public static final String[] ABREVIATIONS = {
        "AFB",  // Air force base
        "MIL",  // Military
    };
    /**
     * Default constructor.
     */
    public BaseFixData()
    {
        super();
    }
    /**
     * Constructor.
     */
    public BaseFixData(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Open the main file.
     */
    public Record openMainRecord()
    {
        if (this.getProperty("recordClass") != null)
        {
                String strClassName = this.getProperty("recordClass");
                if (strClassName != null)
                    return Record.makeRecordFromClassName(strClassName, this);
        }
        return super.openMainRecord();
    }
    /**
     * GetRecordFromDescription Method.
     */
    public static Record getRecordFromDescription(BaseField field, String strDesc)
    {
        Record recSecond = ((ReferenceField)field).getReferenceRecord();
        return BaseFixData.getRecordFromDescription(strDesc, null, recSecond);
    }
    /**
     * GetRecordFromDescription Method.
     */
    public static Record getRecordFromDescription(String strDesc, String strFieldName, Record recSecond)
    {
        if ((strDesc == null) || (strDesc.length() == 0))
            return null;
        BaseField fldSecond = null;
        if (strFieldName != null)
            fldSecond = recSecond.getField(strFieldName);
        if (fldSecond == null)
            fldSecond = recSecond.getField("Name");
        if (fldSecond == null)
            fldSecond = recSecond.getField("Description");
        if (fldSecond == null)
            return null;
        recSecond.setKeyArea(fldSecond);
        fldSecond.setString(strDesc);
        try {
            if (recSecond.seek(DBConstants.EQUALS))
                return recSecond;
        } catch (DBException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * GetRecordFromCode Method.
     */
    public static Record getRecordFromCode(BaseField field, String strDesc)
    {
        return BaseFixData.getRecordFromCode(field, strDesc, null);
    }
    /**
     * GetRecordFromCode Method.
     */
    public static Record getRecordFromCode(BaseField field, String strDesc, String strFieldName)
    {
        Record recSecond = ((ReferenceField)field).getReferenceRecord();
        return BaseFixData.getRecordFromCode(field, strDesc, strFieldName, recSecond);
    }
    /**
     * GetRecordFromCode Method.
     */
    public static Record getRecordFromCode(BaseField field, String strDesc, String strFieldName, Record recSecond)
    {
        if ((strDesc == null) || (strDesc.length() == 0))
            return null;
        BaseField fldSecond = null;
        if (strFieldName != null)
            fldSecond = recSecond.getField(strFieldName);
        if (fldSecond == null)
            fldSecond = recSecond.getField("Code");
        if (fldSecond == null)
            return null;
        recSecond.setKeyArea(fldSecond);
        fldSecond.setString(strDesc);
        try {
            if (recSecond.seek(DBConstants.EQUALS))
                return recSecond;
        } catch (DBException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * FixCapitalization Method.
     */
    public static void fixCapitalization(BaseField field)
    {
        String string = field.toString();
        string = BaseFixData.fixCapitalization(string);
        field.setString(string);
    }
    /**
     * FixCapitalization Method.
     */
    public static String fixCapitalization(String string)
    {
        if (string != null)
        {
            for (int i = 0; i < string.length(); i++)
            {
                if (Character.isLowerCase(string.charAt(i)))
                    return string;  // Already lower case
            }
            StringBuffer sb = new StringBuffer();
            boolean bPreviousSpace = true;
            for (int i = 0; i < string.length(); i++)
            {
                Character character = string.charAt(i);
                if (!bPreviousSpace)
                {
                    character = Character.toLowerCase(character);
                }
                bPreviousSpace = false;
                if (Character.isSpaceChar(character))
                    bPreviousSpace = true;
                if (!Character.isLetterOrDigit(character))
                    bPreviousSpace = true;
                if (character == 'c')
                    if (i > 0)
                        if (string.charAt(i - 1) == 'M')
                            bPreviousSpace = true;  // McName
                if (!bPreviousSpace)
                    bPreviousSpace = BaseFixData.checkAbreviations(string, i);
                sb.append(character);
            }
            string = sb.toString();
        }        
        return string;
    }
    /**
     * CheckAbreviations Method.
     */
    public static boolean checkAbreviations(String string, int i)
    {
        // Check first character
        for (int index = 0; index < ABREVIATIONS.length; index++)
        {
            if (ABREVIATIONS[index].length() > 1)
                if (string.charAt(i) == ABREVIATIONS[index].charAt(0))
                    if (string.length() > i + 1)
                        if (string.charAt(i+1) == ABREVIATIONS[index].charAt(1))
                            if (string.length() > i + 2)
                                if (string.charAt(i+2) == ABREVIATIONS[index].charAt(2))
                                    return true;
        }
        // Check second character
        for (int index = 0; index < ABREVIATIONS.length; index++)
        {
            if (ABREVIATIONS[index].length() > 2)
                if (i > 0)
                    if (string.charAt(i-1) == ABREVIATIONS[index].charAt(0))
                        if (string.charAt(i) == ABREVIATIONS[index].charAt(1))
                            if (string.length() > i + 1)
                                if (string.charAt(i+1) == ABREVIATIONS[index].charAt(2))
                                    return true;
        }
        return false;
    }

}
