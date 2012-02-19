/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util;

import java.util.Map;

import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.app.program.db.FieldData;
import org.jbundle.app.program.db.LogicFile;
import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.app.program.db.ClassProjectModel.CodeType;


/**
 *  WriteJava - Constructor.
 */
public class WriteFieldClass extends WriteSharedClass
{

    /**
     * Constructor.
     */
    public WriteFieldClass()
    {
        super();
    }
    /**
     * Constructor.
     */
    public WriteFieldClass(Task taskParent, Record recordMain, Map<String,Object> properties)
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
    }
    /**
     *
     */
    public void addListeners()
    {
        super.addListeners();
    }
    /**
     *
     */
    public void free()
    {
        super.free();
    }
    /**
     *  Create the Class for this field
     */
    public void writeClass(String strClassName, CodeType codeType)
    {
        String strFieldClass;
        Record recClassInfo = this.getMainRecord();

        String strFieldDataHandle = this.getProperty(FieldData.FIELD_DATA_FILE);
        if (strFieldDataHandle != null)
        {
            FieldData recFieldData = (FieldData)this.getRecord(FieldData.FIELD_DATA_FILE);
            try {
                recFieldData.setHandle(strFieldDataHandle, DBConstants.OBJECT_ID_HANDLE);  // Get the record
                String strRecordClass = recFieldData.getField(FieldData.FIELD_FILE_NAME).toString();
                String strFieldName = recFieldData.getField(FieldData.FIELD_NAME).toString();
                String strBaseFieldName = recFieldData.getField(FieldData.BASE_FIELD_NAME).toString();
                m_BasedFieldHelper.getBasedField(recFieldData, strRecordClass, strFieldName, strBaseFieldName);  // Get the field this is based on
        
                String strClassInfoHandle = this.getProperty(ClassInfo.CLASS_INFO_FILE);
                if (strClassInfoHandle != null)
                    recClassInfo.setHandle(strClassInfoHandle, DBConstants.OBJECT_ID_HANDLE);  // Get the record
                else
                {   // Had to fake a class for this field
                    recClassInfo.addNew();
                    recClassInfo.getField(ClassInfo.CLASS_NAME).setString(this.getProperty(recClassInfo.getField(ClassInfo.CLASS_NAME).getFieldName()));
                    recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).setString(this.getProperty(recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).getFieldName()));
                    recClassInfo.getField(ClassInfo.CLASS_PACKAGE).setString(this.getProperty(recClassInfo.getField(ClassInfo.CLASS_PACKAGE).getFieldName()));
                    recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID).setString(this.getProperty(recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID).getFieldName()));
                }
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }
        strFieldClass = recClassInfo.getField(ClassInfo.CLASS_NAME).getString();

        this.writeHeading(strFieldClass, this.getPackage(codeType), CodeType.THICK);   // Write the first few lines of the files
        this.writeIncludes(CodeType.THICK);

        if (m_MethodNameList.size() != 0)
            m_MethodNameList.removeAllElements();
    
        this.writeClassInterface();
    
        this.writeClassFields(CodeType.THICK);        // Write the C++ fields for this class
        this.writeDefaultConstructor(strFieldClass);
        this.writeFieldInit();
        this.writeInit();               // Special case... zero all class fields!
    
        this.writeInitField();
    
        this.writeClassMethods(CodeType.THICK);   // Write the remaining methods for this class
        this.writeEndCode(true);
    }
    /**
     *  Write the field initialize code
     */
    public void writeFieldInit()
    {
        String strClassName;
        Record recClassInfo = this.getMainRecord();
        strClassName = recClassInfo.getField(ClassInfo.CLASS_NAME).getString();
        if (this.readThisMethod(strClassName))
            this.writeThisMethod(CodeType.THICK);
        else
            this.writeThisMethod(CodeType.THICK);
    }
    /**
     *  Write the field init code
     */
    public void writeInitField()
    {
        Record recLogicFile = this.getRecord(LogicFile.LOGIC_FILE_FILE);
        try   {
            String strClassName;
            Record recClassInfo = this.getMainRecord();
            FieldData recFieldData = (FieldData)this.getRecord(FieldData.FIELD_DATA_FILE);
            strClassName = recClassInfo.getField(ClassInfo.CLASS_NAME).getString();
            if (this.readThisMethod("initField"))
                this.writeThisMethod(CodeType.THICK);
            else
            {
                String strInitField = this.getInitField(recFieldData, false, false);
                if (strInitField.length() != 0)
                {
                    recLogicFile.getField(LogicFile.METHOD_NAME).setString("initField");
                    recLogicFile.setKeyArea(LogicFile.METHOD_CLASS_NAME_KEY);
                    if (!recLogicFile.seek("="))
                    {
                        this.writeMethodInterface(null, "initField", "int", "boolean displayOption", "", "Initialize the field.", null);
                        String setOp = ".setString";
                        if ((strInitField.equalsIgnoreCase("todaysDate()")) || (strInitField.equalsIgnoreCase("currentTime()")) || (strInitField.equalsIgnoreCase("getUserID()")))
                            setOp = ".setValue";
                        if (strInitField.length() > 0)
                            if ((strInitField.charAt(0) == 'k') ||
                                (Character.isDigit(strInitField.charAt(0))) ||
                                (strInitField.indexOf(".k") != -1))
                            setOp = ".setValue";
                        if ((strInitField.equals("true")) || (strInitField.equals("false")))
                            setOp = ".setState";
                        if ((strClassName.equalsIgnoreCase("IntegerField"))   //**Change this to look through the base classes for NumberField
                            || (strClassName.equalsIgnoreCase("ShortField"))
                            || (strClassName.equalsIgnoreCase("FloatField"))
                            || (strClassName.equalsIgnoreCase("RealField"))
                            || (strClassName.equalsIgnoreCase("CurrencysField"))
                            || (strClassName.equalsIgnoreCase("DoubleField")))
                                setOp = ".setValue";
                        if (strInitField.equals("all"))
                        {
                            strInitField = "\"all\""; // HACK
                            setOp = ".setString";
                        }
                        if ((strInitField.indexOf('_') != -1) && (strInitField.indexOf('.') == -1))
                        {
                            strInitField = "\"\"/**" + strInitField + "*/";   // HACK
                            setOp = ".setString";
                        }
                        if (strInitField != strClassName)
                            m_StreamOut.writeit("\treturn this" + setOp + "(" + strInitField + ", displayOption, DBConstants.INIT_MOVE);\n");
                        m_StreamOut.writeit("}\n");
                    }
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            recLogicFile.setKeyArea(LogicFile.SEQUENCE_KEY);   // set this back
        }
    }
}
