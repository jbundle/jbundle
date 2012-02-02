/*

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util;

/**
 *  WriteJava
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.app.program.db.FieldData;
import org.jbundle.model.DBException;


//*******************************************************************
//  GetFieldData - 
//*******************************************************************
// Required:
//  strRecordClass - class of this record
//  strFieldName - BaseField Name to read
//  strBaseFieldName - Base BaseField Name
// Returns:
//  fieldInfo - with the correct fields
public class GetFieldData extends Object
{
    FieldData m_FieldData;
    ClassInfo m_ClassInfo;
    /**
     *
     */
    public GetFieldData(RecordOwner recordOwner)
    {
        m_FieldData = null;
        m_ClassInfo = null;
        FieldData fieldInfo = new FieldData(recordOwner);
        m_FieldData = fieldInfo;
        ClassInfo classInfo = new ClassInfo(recordOwner);
        m_ClassInfo = classInfo;
    }
    /**
     *
     */
    public void free()
    {
        if (m_FieldData != null)
            m_FieldData.free();
        if (m_ClassInfo != null)
            m_ClassInfo.free();
    }
    /**
     *
     */
    public void getBasedField(FieldData fieldInfo, String strRecordClass, String strFieldName, String strBaseFieldName)
    {
        try   {
            m_ClassInfo.setKeyArea(ClassInfo.CLASS_NAME_KEY);
            m_ClassInfo.getField(ClassInfo.CLASS_NAME).setString(strRecordClass);   // Class of this record
            if ((strRecordClass == null) || (strRecordClass.length() == 0))
                return;
            if (!m_ClassInfo.seek("="))
                return;     // End of based records
            
            String strBaseRecordClass = m_ClassInfo.getField(ClassInfo.BASE_CLASS_NAME).getString();
            m_FieldData.getField(FieldData.FIELD_FILE_NAME).setString(strBaseRecordClass);
            if (strBaseFieldName.length() == 0)   // No class, use the base class, unless override required
                m_FieldData.getField(FieldData.FIELD_NAME).setString(strFieldName);
            else
                m_FieldData.getField(FieldData.FIELD_NAME).setString(strBaseFieldName);
            m_FieldData.setKeyArea(FieldData.FIELD_NAME_KEY);
            String newFieldName = m_FieldData.getField(FieldData.FIELD_NAME).getString();
            String newBaseFieldName = newFieldName;
            if (m_FieldData.seek("="))
            {
                if (fieldInfo.getField(FieldData.FIELD_DESC_VERTICAL).getLength() == 0)
                    fieldInfo.getField(FieldData.FIELD_DESC_VERTICAL).moveFieldToThis(fieldInfo.getField(FieldData.FIELD_DESCRIPTION));
                this.moveupFieldData(fieldInfo);
                newBaseFieldName = m_FieldData.getField(FieldData.BASE_FIELD_NAME).getString();
                newFieldName = m_FieldData.getField(FieldData.FIELD_NAME).getString();
            }
            this.getBasedField(fieldInfo, strBaseRecordClass, newFieldName, newBaseFieldName);
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     * Move FieldData2 fields to empty FieldData fields.
     */
    public void moveupFieldData(FieldData fieldInfo)
    {   // Move these fields up to the field passed in
        BaseField field;
        String thisFieldStr;
        int count = fieldInfo.getFieldCount() + DBConstants.MAIN_FIELD;
        for (int i = DBConstants.MAIN_FIELD; i < count; i++)
        {
            field = fieldInfo.getField(i);
            if (field.getString().length() == 0)
            {
                thisFieldStr = m_FieldData.getField(i).getString();
                if (thisFieldStr.length() != 0)
                    field.setString(thisFieldStr);
            }
        }
    }
}
