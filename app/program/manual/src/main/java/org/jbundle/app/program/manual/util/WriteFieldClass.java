package org.jbundle.app.program.manual.util;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.util.DBConstants;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.app.program.db.FieldData;
import org.jbundle.app.program.db.LogicFile;
import org.jbundle.app.program.db.ClassProject.CodeType;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;


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

        String strFieldDataHandle = this.getProperty(FieldData.kFieldDataFile);
        if (strFieldDataHandle != null)
        {
            FieldData recFieldData = (FieldData)this.getRecord(FieldData.kFieldDataFile);
            try {
                recFieldData.setHandle(strFieldDataHandle, DBConstants.OBJECT_ID_HANDLE);  // Get the record
                String strRecordClass = recFieldData.getField(FieldData.kFieldFileName).toString();
                String strFieldName = recFieldData.getField(FieldData.kFieldName).toString();
                String strBaseFieldName = recFieldData.getField(FieldData.kBaseFieldName).toString();
                m_BasedFieldHelper.getBasedField(recFieldData, strRecordClass, strFieldName, strBaseFieldName);  // Get the field this is based on
        
                String strClassInfoHandle = this.getProperty(ClassInfo.kClassInfoFile);
                if (strClassInfoHandle != null)
                    recClassInfo.setHandle(strClassInfoHandle, DBConstants.OBJECT_ID_HANDLE);  // Get the record
                else
                {   // Had to fake a class for this field
                    recClassInfo.addNew();
                    recClassInfo.getField(ClassInfo.kClassName).setString(this.getProperty(recClassInfo.getField(ClassInfo.kClassName).getFieldName()));
                    recClassInfo.getField(ClassInfo.kBaseClassName).setString(this.getProperty(recClassInfo.getField(ClassInfo.kBaseClassName).getFieldName()));
                    recClassInfo.getField(ClassInfo.kClassPackage).setString(this.getProperty(recClassInfo.getField(ClassInfo.kClassPackage).getFieldName()));
                    recClassInfo.getField(ClassInfo.kClassProjectID).setString(this.getProperty(recClassInfo.getField(ClassInfo.kClassProjectID).getFieldName()));
                }
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }
        strFieldClass = recClassInfo.getField(ClassInfo.kClassName).getString();

        this.writeHeading(strFieldClass, this.getPackage(codeType), CodeType.BASE);   // Write the first few lines of the files
        this.writeIncludes();

        if (m_MethodNameList.size() != 0)
            m_MethodNameList.removeAllElements();
    
        this.writeClassInterface();
    
        this.writeClassFields(false);        // Write the C++ fields for this class
        this.writeDefaultConstructor(strFieldClass);
        this.writeFieldInit();
        this.writeInit();               // Special case... zero all class fields!
    
        this.writeInitField();
    
        this.writeClassMethods();   // Write the remaining methods for this class
        this.writeEndCode(true);
    }
    /**
     *  Write the field initialize code
     */
    public void writeFieldInit()
    {
        String strClassName;
        Record recClassInfo = this.getMainRecord();
        strClassName = recClassInfo.getField(ClassInfo.kClassName).getString();
        if (this.readThisMethod(strClassName))
            this.writeThisMethod();
        else
            this.writeThisMethod();
    }
    /**
     *  Write the field init code
     */
    public void writeInitField()
    {
        Record recLogicFile = this.getRecord(LogicFile.kLogicFileFile);
        try   {
            String strClassName;
            Record recClassInfo = this.getMainRecord();
            FieldData recFieldData = (FieldData)this.getRecord(FieldData.kFieldDataFile);
            strClassName = recClassInfo.getField(ClassInfo.kClassName).getString();
            if (this.readThisMethod("initField"))
                this.writeThisMethod();
            else
            {
                String strInitField = this.getInitField(recFieldData, false, false);
                if (strInitField.length() != 0)
                {
                    recLogicFile.getField(LogicFile.kMethodName).setString("initField");
                    recLogicFile.setKeyArea(LogicFile.kMethodClassNameKey);
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
                            m_MethodsOut.writeit("\treturn this" + setOp + "(" + strInitField + ", displayOption, DBConstants.INIT_MOVE);\n");
                        m_MethodsOut.writeit("}\n");
                    }
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            recLogicFile.setKeyArea(LogicFile.kSequenceKey);   // set this back
        }
    }
}
