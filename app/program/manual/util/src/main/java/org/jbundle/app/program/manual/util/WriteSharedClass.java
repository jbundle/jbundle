/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util;

import java.util.Map;

import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.app.program.db.FieldData;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.app.program.db.ClassProjectModel.CodeType;


/**
 *  WriteJava - Constructor.
 */
public class WriteSharedClass extends WriteClass
{
    protected ClassInfo m_recClassInfo2 = null;

    /**
     * Constructor.
     */
    public WriteSharedClass()
    {
        super();
    }
    /**
     * Constructor.
     */
    public WriteSharedClass(Task taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Init.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     *
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        
        new FieldData(this);
        
        m_recClassInfo2 = new ClassInfo(this);
        this.removeRecord(m_recClassInfo2);  // Do not mix up with class info.
    }
    /**
     *
     */
    public void addListeners()
    {
        super.addListeners();
        
        Record recClassInfo = this.getMainRecord();

        Record recFieldData = this.getRecord(FieldData.FIELD_DATA_FILE);          // Open the Agency File
        recFieldData.setKeyArea(FieldData.FIELD_FILE_NAME_KEY);
        recFieldData.addListener(new SubFileFilter(recClassInfo.getField(ClassInfo.CLASS_NAME), FieldData.FIELD_FILE_NAME, null, null, null, null));
    }
    /**
     *
     */
    public void free()
    {
        if (m_recClassInfo2 != null)
            m_recClassInfo2.free();
        m_recClassInfo2 = null;
        
        super.free();
    }
    /**
     * ReadFieldClass.
     * Required:
     *  recFieldData record
     * Returns:
     *  strFieldClass - BaseField Class to read
     *  strBaseClass - Base BaseField Class
     *  recFieldData - with the correct fields
     */
    public ClassInfo readFieldClass()
    {
        try   {
            String strBaseClass = null;
            String strFieldClass = "Error-no based records";
            Record recClassInfo = this.getMainRecord();
            FieldData recFieldData = (FieldData)this.getRecord(FieldData.FIELD_DATA_FILE);
            String strRecordClass = recFieldData.getField(FieldData.FIELD_FILE_NAME).getString();
            String strFieldName = recFieldData.getField(FieldData.FIELD_NAME).getString();
            String strBaseFieldName = recFieldData.getField(FieldData.BASE_FIELD_NAME).getString();
            m_BasedFieldHelper.getBasedField(recFieldData, strRecordClass, strFieldName, strBaseFieldName);  // Get the field this is based on
            strFieldClass = recFieldData.getField(FieldData.FIELD_CLASS).getString();
            m_recClassInfo2.getField(ClassInfo.CLASS_NAME).setString(strFieldClass);
            m_recClassInfo2.setKeyArea(ClassInfo.CLASS_NAME_KEY);
            boolean bClassInfoExists = m_recClassInfo2.seek("=");
            if (bClassInfoExists)
                strBaseClass = m_recClassInfo2.getField(ClassInfo.BASE_CLASS_NAME).getString();  // This will probally be the base class
            String strInitField = this.getInitField(recFieldData, false, false);
            if (strInitField.length() != 0)
            {
                strBaseClass = strFieldClass;
                strFieldClass = recFieldData.getField(FieldData.FIELD_NAME).getString();
                strFieldClass = strRecordClass + "_" + strFieldClass;
                m_recClassInfo2.addNew(); // Clear info
                m_recClassInfo2.getField(ClassInfo.CLASS_NAME).setString(strFieldClass);
                m_recClassInfo2.getField(ClassInfo.BASE_CLASS_NAME).setString(strBaseClass);
                m_recClassInfo2.getField(ClassInfo.CLASS_PACKAGE).setString(this.getPackage(CodeType.THICK)); // Manual classes are from the same package
                m_recClassInfo2.getField(ClassInfo.CLASS_SOURCE_FILE).moveFieldToThis(recClassInfo.getField(ClassInfo.CLASS_SOURCE_FILE));
                m_recClassInfo2.getField(ClassInfo.CLASS_PROJECT_ID).moveFieldToThis(recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID));
                return m_recClassInfo2;            // Yes, this field has a unique class
            }
            if (bClassInfoExists)
//?                if (recClassInfo.getField(ClassInfo.CLASS_SOURCE_FILE).getString().equalsIgnoreCase(m_recClassInfo2.getField(ClassInfo.CLASS_SOURCE_FILE).toString()))
                    return m_recClassInfo2;        // Yes, this field has a unique class
//            recClassInfo.getField(ClassInfo.CLASS_NAME).setString(strFieldClass);
//            recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).initField(false);        // Blank base class = other class
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
        return null;           // No, this field uses a different class
    }
    /**
     * GetInitField.
     * @param bConstantsNotVariables If true, return constants; if false, return only variables
     */
    public String getInitField(FieldData recFieldData, boolean bConstantsNotVariables, boolean bThinConstants)
    {
        boolean bConstantValue = false;
        boolean bValidThinConstant = true;
        String strInitField = recFieldData.getField(FieldData.INITIAL_VALUE).getString();
        if (strInitField != null) if (strInitField.length() > 0)
        {
            if (strInitField.charAt(0) == '\"')
                bConstantValue = true;  // This is a constant value (a string)
            if (Character.isDigit(strInitField.charAt(0)))
                bConstantValue = true;  // This is a constant value (a number)
            if ((strInitField.equalsIgnoreCase("true")) || (strInitField.equalsIgnoreCase("false")))
                bConstantValue = true;  // This is a constant value (a number)
            if (strInitField.startsWith("new "))
                bConstantValue = true;  // This is a constant value (an object)
            if (strInitField.indexOf('.') != -1)
                if (strInitField.lastIndexOf('(') <= 1) // Not found or a class cast.
                    if (!Utility.isNumeric(strInitField))
                {
                    bConstantValue = true;  // This is a constant value (a member constant - not a function call)
                    bValidThinConstant = false;
                }
        }
        if (bConstantsNotVariables != bConstantValue)
            strInitField = DBConstants.BLANK;          // Not asking for this type of value
        if (bThinConstants)
            if (!bValidThinConstant)
                strInitField = DBConstants.BLANK;          // Not asking for this type of value
        return strInitField;
    }
}
