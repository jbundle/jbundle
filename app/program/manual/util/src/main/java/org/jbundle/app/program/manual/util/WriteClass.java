/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.jbundle.app.program.db.ClassFields;
import org.jbundle.app.program.db.ClassFieldsTypeField;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.app.program.db.ClassProject;
import org.jbundle.app.program.db.FieldData;
import org.jbundle.app.program.db.IncludeScopeField;
import org.jbundle.app.program.db.LogicFile;
import org.jbundle.app.program.db.ProgramControl;
import org.jbundle.app.program.db.ScreenIn;
import org.jbundle.app.program.manual.util.data.NameList;
import org.jbundle.app.program.manual.util.data.StreamOut;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.app.program.db.ClassProjectModel.CodeType;
import org.jbundle.thin.base.db.Constants;


/**
 *  WriteJava - Constructor.
 */
public class WriteClass extends BaseProcess
{
    /**
     * Method output file.
     */
    protected StreamOut m_StreamOut = null;
    /**
     * List of all included files.
     */
    protected Includes m_IncludeNameList = null;
    /**
     * List of all methods written for this class so far
     */
    protected NameList m_MethodNameList = null;
    /**
     * Field helper
     */
    protected GetFieldData m_BasedFieldHelper = null;
    /**
     * Method helper
     */
    protected GetMethodInfo m_MethodHelper = null;

    protected String m_strParams = null;
    protected String m_strLastMethodInterface = null;
    protected String m_strLastMethod = null;
    
    /**
     * Constructor.
     */
    public WriteClass()
    {
        super();
    }
    /**
     * Constructor.
     */
    public WriteClass(Task taskParent, Record recordMain, Map<String,Object> properties)
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
    public Record openMainRecord()
    {
        return new ClassInfo(this);
    }
    /**
     *
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        
        new ClassFields(this);
        new LogicFile(this);
        new ScreenIn(this);
        new ProgramControl(this);	// Program control file
    }
    /**
     *
     */
    public void addListeners()
    {
        super.addListeners();
    // Open the source files
        m_BasedFieldHelper = new GetFieldData(this);
        m_MethodHelper = new GetMethodInfo(this);
        m_MethodNameList = new NameList();

        Record recClassInfo = this.getMainRecord();

        Record recClassFields = this.getRecord(ClassFields.CLASS_FIELDS_FILE);      // Open the Agency File
        recClassFields.setKeyArea(ClassFields.CLASS_INFO_CLASS_NAME_KEY);
        SubFileFilter fileBehavior2 = new SubFileFilter(recClassInfo.getField(ClassInfo.CLASS_NAME), ClassFields.CLASS_INFO_CLASS_NAME, null, null, null, null);
        recClassFields.addListener(fileBehavior2);   // Only read through the class fields

        Record recLogicFile = this.getRecord(LogicFile.LOGIC_FILE_FILE);      // Open the Agency File
        recLogicFile.setKeyArea(LogicFile.SEQUENCE_KEY);
        SubFileFilter logicBehavior = new SubFileFilter(recClassInfo.getField(ClassInfo.CLASS_NAME), LogicFile.METHOD_CLASS_NAME, null, null, null, null);
        recLogicFile.addListener(logicBehavior);
    
        Record recScreenIn = this.getRecord(ScreenIn.SCREEN_IN_FILE);
        recScreenIn.setKeyArea(ScreenIn.SCREEN_IN_PROG_NAME_KEY);
        SubFileFilter fileBehavior3 = new SubFileFilter(recLogicFile.getField(LogicFile.METHOD_CLASS_NAME), ScreenIn.SCREEN_IN_PROG_NAME, null, null, null, null);
        recScreenIn.addListener(fileBehavior3);      // Only read through the class fields
    }
    /**
     *
     */
    public void free()
    {
        if (m_IncludeNameList != null)
            m_IncludeNameList.free();
        m_IncludeNameList = null;
        if (m_MethodNameList != null)
            m_MethodNameList.free();
        m_MethodNameList = null;
        super.free();
    }
    /**
     *  Create the Class for this field.
     * @param codeType
     */
    public void writeClass(String strClassName, CodeType codeType)
    {
        if (!this.readThisClass(strClassName))  // Get the field this is based on
            return;
        this.writeHeading(strClassName, this.getPackage(codeType), codeType);        // Write the first few lines of the files
        this.writeIncludes(codeType);

        if (m_MethodNameList.size() != 0)
            m_MethodNameList.removeAllElements();
    
        this.writeClassInterface();
    
        this.writeClassFields(CodeType.THICK);        // Write the C++ fields for this class
        this.writeDefaultConstructor(strClassName);
    
        this.writeClassInit();
        this.writeInit();   // Special case... zero all class fields!
    
        this.writeProgramDesc(strClassName);
    
        this.writeClassMethods(CodeType.THICK);   // Write the remaining methods for this class
        this.writeEndCode(true);
    }
    /**
     *  Read the class with this name.
     */
    public boolean readThisClass( String strClassName)
    {
        try   {
            Record recClassInfo = this.getMainRecord();
            recClassInfo.getField(ClassInfo.CLASS_NAME).setString(strClassName);
            recClassInfo.setKeyArea(ClassInfo.CLASS_NAME_KEY);
            return recClassInfo.seek("=");   // Get this class record back
        } catch (DBException ex)   {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Get the package name for this class.
     * @param codeType
     *
     */
    public String getPackage(CodeType codeType)
    {
        Record recClassInfo = this.getMainRecord();
        String packageName = recClassInfo.getField(ClassInfo.CLASS_PACKAGE).getString();
        ClassProject classProject = (ClassProject)((ReferenceField)recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID)).getReference();
        return classProject.getFullPackage(codeType, packageName);
    }
    /**
     *  Write the starting source code for this file
     * @param codeType
     */
    public void writeHeading(String strClassName, String strPackage, CodeType codeType)
    { // Write out the first few lines of the header
    	String strFileName = DBConstants.BLANK;
        try   {
            ClassInfo recClassInfo = (ClassInfo)this.getMainRecord();
            ClassProject recClassProject = (ClassProject)((ReferenceField)recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID)).getReference();
        	strFileName = this.getFileName(strClassName, strPackage, codeType, recClassProject);
            File file = new File(strFileName);
            String strPath = file.getParent();
            File fileDir = new File(strPath);
            fileDir.mkdirs();
            m_StreamOut = new StreamOut(strFileName);
            m_IncludeNameList = new Includes(this, m_StreamOut);
        } catch (IOException ex)   {
            ex.printStackTrace();
        }

        String header = null;
        String resourceName = Utility.getDomainName(strPackage);
        if (resourceName == null)
        	resourceName = Constants.ROOT_PACKAGE.substring(0, Constants.ROOT_PACKAGE.length() - 1);
        if (strFileName.endsWith(".java"))
        	resourceName = resourceName + ".java";
        resourceName = resourceName + ".header";
        ResourceBundle resourceBundle = this.getTask().getApplication().getResources(Constants.ROOT_PACKAGE + "res.app.program.program.Program", false);
        if (resourceBundle != null)
        	header = resourceBundle.getString(resourceName);
        if (header == null)
        {
	        if (strFileName.endsWith(".java"))
	        {
		        header = "/**\n" +
		        		" * @(#)${className}.\n" +
		        		" * Copyright © 2012 tourgeek.com. All rights reserved.\n" +
		        		" */";
	        }
	        else
	        {
		        header = "# ${className}.\n" +
		        	"# Copyright © 2012 tourgeek.com. All rights reserved.";
	        }
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("className", strClassName);
        header = Utility.replaceResources(header, null, map, this);
    // Write the first few lines of the method file
        m_StreamOut.writeit(header + "\n");
    }
    /**
     * Get the file name.
     * @param codeType
     * @param recClassProject
     */
    public String getFileName(String strFileName, String strPackage, CodeType codeType, ClassProject recClassProject)
    {
        return recClassProject.getFileName(strFileName, strPackage, codeType, true, true);
    }
    /**
     *  Start the interface.
     */
    public void writeClassInterface()
    {
        Record recClassInfo = this.getMainRecord();
        String strClassName = recClassInfo.getField(ClassInfo.CLASS_NAME).getString();
        String strBaseClass = recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).getString();
        String strClassDesc = recClassInfo.getField(ClassInfo.CLASS_DESC).getString();
        String strClassInterface = recClassInfo.getField(ClassInfo.CLASS_IMPLEMENTS).getString();
        String implementsClass = null;
        if (((ClassInfo)recClassInfo).isARecord(false))
            implementsClass = strClassName + "Model";
        if ((implementsClass != null) && (implementsClass.length() > 0))
        {
            m_IncludeNameList.addInclude(this.getPackage(CodeType.INTERFACE), null);    // Make sure this is included
            if ((strClassInterface == null) || (strClassInterface.length() == 0))
                strClassInterface = implementsClass;
            else
                strClassInterface = implementsClass + ", " + strClassInterface;
        }
        m_IncludeNameList.addInclude(strBaseClass, null);    // Make sure this is included
    
        m_StreamOut.writeit("\n/**\n *\t" + strClassName + " - " + strClassDesc + ".\n */\n");

        if ((strClassInterface == null) || (strClassInterface.length() == 0))
            strClassInterface = "";
        else
            strClassInterface = "\n\t implements " + strClassInterface;
        String strClassType = "class";
        if ("interface".equals(recClassInfo.getField(ClassInfo.CLASS_TYPE).toString()))
            strClassType = "interface";
        String strExtends = " extends ";
        if (strBaseClass.length() == 0)
            strExtends = "";
        m_StreamOut.writeit("public " + strClassType + " " + strClassName + strExtends + strBaseClass + strClassInterface + "\n{\n");
        m_StreamOut.setTabs(+1);
    }
    /**
     *  Write the includes
     * @param codeType Target method types
     */
    public void writeIncludes(CodeType codeType)
    {
        ClassInfo recClassInfo2 = null;
        ClassFields recClassFields = null;
        try   {
        	ClassInfo recClassInfo = (ClassInfo)this.getMainRecord();
        	String classType = recClassInfo.getField(ClassInfo.CLASS_TYPE).toString();
            String strPackage = this.getPackage(codeType);
            m_StreamOut.writeit("package " + strPackage + ";\n\n");
            m_IncludeNameList.addName(strPackage);     // Don't include this!!!
            if ("interface".equalsIgnoreCase(classType))
                codeType = CodeType.INTERFACE;

            if (codeType == CodeType.THICK)
            {
                //m_StreamOut.writeit("import java.awt.*;\n"); //j Temp
                m_StreamOut.writeit("import java.util.*;\n\n");    //j Temp

                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "base.db");        // Don't include this!!!
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "thin.base.util");   // Don't include this!!!
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "thin.base.db");   // Don't include this!!!
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "base.db.event");      // Don't include this!!!
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "base.db.filter");     // Don't include this!!!
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "base.field");     // Don't include this!!!
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "base.field.convert");     // Don't include this!!!
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "base.field.event");   // Don't include this!!!
                if (("Screen".equalsIgnoreCase(classType)) || ("Report".equalsIgnoreCase(classType)))
                {
                    m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "base.screen.model");      // Don't include this!!!
                    m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "base.screen.model.util");     // Don't include this!!!
                }
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "base.model");      // Don't include this!!!
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "base.util");      // Don't include this!!!
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "model");      // Don't include this!!!
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "model.db");      // Don't include this!!!
                m_IncludeNameList.addPackage(DBConstants.ROOT_PACKAGE + "model.screen");      // Don't include this!!!
            }            
        // Now write the include files for any base classes not in this file or fields with class defs not in file
            recClassInfo2 = new ClassInfo(this);

            recClassFields = new ClassFields(this);
            recClassFields.setKeyArea(ClassFields.CLASS_INFO_CLASS_NAME_KEY);
            SubFileFilter fileBehavior2 = new SubFileFilter(recClassInfo2.getField(ClassInfo.CLASS_NAME), ClassFields.CLASS_INFO_CLASS_NAME, null, null, null, null);
            recClassFields.addListener(fileBehavior2);   // Only read through the class fields

            String strFileName = recClassInfo.getField(ClassInfo.CLASS_SOURCE_FILE).toString();
            
            recClassInfo2.setKeyArea(ClassInfo.CLASS_SOURCE_FILE_KEY);
            StringSubFileFilter fileBehavior = new StringSubFileFilter(strFileName, ClassInfo.CLASS_SOURCE_FILE, null, null, null, null);
            recClassInfo2.addListener(fileBehavior);  // Only select records which match m_strFileName

            recClassInfo2.setKeyArea(ClassInfo.CLASS_SOURCE_FILE_KEY);
            recClassInfo2.close();
            while (recClassInfo2.hasNext())
            {
                recClassInfo2.next();
                if ((codeType == CodeType.THIN) && (!recClassInfo2.isARecord(false)))
                    continue;
                //if ((codeType == CodeType.INTERFACE) && (!recClassInfo2.isARecord(false)))
                //    continue;

                String strBaseRecordClass = recClassInfo2.getField(ClassInfo.BASE_CLASS_NAME).getString();
                if (codeType == CodeType.THICK)
                    m_IncludeNameList.addInclude(strBaseRecordClass, null);  // Include the base class if it isn't in this file
                recClassFields.close();
                while (recClassFields.hasNext())
                {
                    recClassFields.next();
                    
                    if (!((IncludeScopeField)recClassFields.getField(ClassFields.INCLUDE_SCOPE)).includeThis(codeType, false))
                        continue;

                    String strFieldName = recClassFields.getField(ClassFields.CLASS_FIELD_NAME).getString();
                    String strFieldClass = recClassFields.getField(ClassFields.CLASS_FIELD_CLASS).getString();
                    String strReference = "";
                    String strClassFieldType = recClassFields.getField(ClassFields.CLASS_FIELDS_TYPE).toString();
                    if (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.INCLUDE_PACKAGE))
                    {
                        if (strFieldClass.length() > 0) if (strFieldClass.charAt(0) == '.')
                        {
                        	ClassProject classProject = (ClassProject)((ReferenceField)recClassInfo2.getField(ClassInfo.CLASS_PROJECT_ID)).getReference();
                        	if ((classProject != null)
                        			&& ((classProject.getEditMode() == DBConstants.EDIT_CURRENT) || (classProject.getEditMode() == DBConstants.EDIT_IN_PROGRESS)))
                        	{
                        		CodeType codeType2 = CodeType.THICK;
                        		if (strFieldClass.startsWith(".thin"))
                        				codeType2 = CodeType.THIN;
                        		if (strFieldClass.startsWith(".res"))
                    				codeType2 = CodeType.RESOURCE_CODE;
                        		strFieldClass = classProject.getFullPackage(CodeType.THICK, strFieldClass);
                        		if (codeType2 != CodeType.THICK)
                        		{
                        			int end = strFieldClass.indexOf(codeType2 == CodeType.THIN ? ".thin" : ".res");
                        			int start = strFieldClass.indexOf('.');
                        			if (start != -1)
                        				start = strFieldClass.indexOf('.', start + 1);
                        			if (start != -1)
                        				strFieldClass = strFieldClass.substring(0, start) + strFieldClass.substring(end);
                        		}
                        	}
                        	else
                        		strFieldClass = DBConstants.ROOT_PACKAGE + strFieldClass.substring(1);
                        }
                        m_IncludeNameList.addPackage(strFieldClass);
                    }
                    else if ((strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.INCLUDE_CLASS_PACKAGE))
                        || (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.INCLUDE_MODEL_PACKAGE))   // For now
                        || (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.INCLUDE_RES_PACKAGE))   // For now
                        || (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.INCLUDE_THIN_PACKAGE))   // For now
                        || (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.CLASS_FIELD))   // For now
                        || (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.INCLUDE_CLASS)))
                    {
                        CodeType codeType3 = CodeType.THICK;
                        if (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.INCLUDE_MODEL_PACKAGE))
                            codeType3 = CodeType.INTERFACE;
                        if (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.INCLUDE_RES_PACKAGE))   // For now
                            codeType3 = CodeType.RESOURCE_CODE;
                        if (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.INCLUDE_THIN_PACKAGE))   // For now
                            codeType3 = CodeType.THIN;
                        m_IncludeNameList.addInclude(strFieldClass, codeType3);
                        strReference = "Y";
                    }
                    if (recClassFields.getField(ClassFields.CLASS_FIELD_PROTECT).getString().equalsIgnoreCase("S")) // Static, initialize now
                    {
                        if (strReference.length() == 0)
                            strReference = "0";
                        if (strReference.charAt(0) == 'Y')
                            strReference = "null";
                        else
                        {
                            strReference = recClassFields.getField(ClassFields.CLASS_FIELD_INITIAL).getString();
                            if (strReference.length() == 0)
                                strReference = "0";
                        }
                        if (!strReference.equals("(none)"))
                            m_StreamOut.writeit(strFieldClass + " " + strFieldName + " = " + strReference + ";\n");
                    }
                }
                recClassFields.close();
            } // End of write record method(s) loop
            recClassInfo2.removeListener(fileBehavior, true);
            recClassInfo2.close();
        } catch (DBException ex)   {
            ex.printStackTrace();
        } finally   {
            if (recClassInfo2 != null)
                recClassInfo2.free();
            if (recClassFields != null)
                recClassFields.free();
        }
    }
    /**
     *  Write out all the data fields for this class (not file fields!)
     * Required: strClassName - Current Class
     */
    public void writeClassFields(CodeType codeType)
    {
        try   {
            ClassInfo classInfo = (ClassInfo)this.getRecord(ClassInfo.CLASS_INFO_FILE);
            boolean hasAnInterface = classInfo.isARecord(false);
            Record recClassFields = this.getRecord(ClassFields.CLASS_FIELDS_FILE);
            recClassFields.close();
            while (recClassFields.hasNext())
            {
                recClassFields.next();
                if (!((IncludeScopeField)recClassFields.getField(ClassFields.INCLUDE_SCOPE)).includeThis(codeType, hasAnInterface))
                    continue;
                String strFieldName = recClassFields.getField(ClassFields.CLASS_FIELD_NAME).getString();
                String strFieldClass = recClassFields.getField(ClassFields.CLASS_FIELD_CLASS).getString();
                String strReference = "";
                String strClassFieldType = recClassFields.getField(ClassFields.CLASS_FIELDS_TYPE).toString();
                if ((strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.CLASS_FIELD))
                    || (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.NATIVE_FIELD))
                    || (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.CLASS_NAME))
                    || (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.SCREEN_CLASS_NAME)))
                        if (strFieldName.length() != 0)
                {
                        if (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.CLASS_FIELD))
                            strReference = " = null";
                        else
                            strReference = "";
                    String strProtection = "protected";
                    if (recClassFields.getField(ClassFields.CLASS_FIELD_PROTECT).getString().equalsIgnoreCase("S"))
                        strProtection = "protected static";  // Static member function
                    else if (!recClassFields.getField(ClassFields.CLASS_FIELD_PROTECT).isNull())
                        strProtection = recClassFields.getField(ClassFields.CLASS_FIELD_PROTECT).toString();
                    String strInitialValue = "";
                    String strAssignmentOperator = " = ";
                    if ("enum".equals(strFieldClass))
                        strAssignmentOperator = " ";
                    if (!recClassFields.getField(ClassFields.CLASS_FIELD_INITIAL_VALUE).isNull())
                        strInitialValue = strAssignmentOperator + recClassFields.getField(ClassFields.CLASS_FIELD_INITIAL_VALUE).toString();
                    if ((strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.CLASS_NAME))
                            || (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.SCREEN_CLASS_NAME)))
                    {
                        if (recClassFields.getField(ClassFields.CLASS_FIELD_PROTECT).isNull())
                            strProtection = "public static final";  // Default
                        ClassInfo recClassInfo2 = new ClassInfo(this);
                        recClassInfo2.setKeyArea(ClassInfo.CLASS_NAME_KEY);
                        recClassInfo2.getField(ClassInfo.CLASS_NAME).setString(strFieldClass);   // Class of this record
                        if (recClassInfo2.seek("="))
                        {
                            strInitialValue = " = \"" + recClassInfo2.getFullClassName() + "\"";
                            strFieldClass = "String";
                        }
                    }
                    m_StreamOut.writeit(strProtection + " " + strFieldClass + " " + strFieldName + strReference + strInitialValue + ";\n");
                }
            }
            recClassFields.close();
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     *  Write out the default constructor for this class (no variables in interface).
     */
    public void writeDefaultConstructor(String strClassName)
    {
        this.readThisMethod(strClassName);
        Record recClassInfo = this.getMainRecord();
        if ("interface".equals(recClassInfo.getField(ClassInfo.CLASS_TYPE).toString()))
            return;
        MethodInfo methodInfo = new MethodInfo();
        LogicFile recLogicFile = (LogicFile)this.getRecord(LogicFile.LOGIC_FILE_FILE);
        m_MethodHelper.getTheMethodInfo(recLogicFile, methodInfo);   // Get the correct interface, etc..

        if (methodInfo.strMethodReturns.equalsIgnoreCase("INIT"))
            return;

        this.writeMethodInterface(null, strClassName, "", "", methodInfo.strMethodThrows, "Default constructor", null);
        this.writeDefaultMethodCode(strClassName, "", "", strClassName);
        m_StreamOut.writeit("}\n");
    }
    /**
     *  Write the end of class code.
     */
    public void writeEndCode(boolean bJavaFile)
    {
        m_StreamOut.setTabs(-1);
        if (bJavaFile)
        	m_StreamOut.writeit("\n}");
        m_StreamOut.writeit("\n");

        m_StreamOut.free();
        m_StreamOut = null;
        m_IncludeNameList.free();
        m_IncludeNameList = null;
    }
    /**
     *  Read this method.
     */
    public boolean readThisMethod(String strMethodName)
    {
        Record recClassInfo = this.getMainRecord();
        LogicFile recLogicFile = (LogicFile)this.getRecord(LogicFile.LOGIC_FILE_FILE);
        try   {
            String strClassName = recClassInfo.getField(ClassInfo.CLASS_NAME).getString();
            recLogicFile.getField(LogicFile.METHOD_CLASS_NAME).setString(strClassName);
            recLogicFile.getField(LogicFile.METHOD_NAME).setString(strMethodName);
            recLogicFile.setKeyArea(LogicFile.METHOD_CLASS_NAME_KEY);
            if (recLogicFile.seek("="))
                return true;
            else
            {   // Set up a fake record and return
                recLogicFile.handleNewRecord(false);   // Clear the record
                recLogicFile.getField(LogicFile.METHOD_CLASS_NAME).setString(strClassName);
                recLogicFile.getField(LogicFile.METHOD_NAME).setString(strMethodName);
                return false;
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            recLogicFile.setKeyArea(LogicFile.SEQUENCE_KEY);   // set this back
        }
        return false;
    }
    /**
     *  Write the Get Record method (Required for each non-public record)
     */
    public void writeMethodInterface(String strProtection, String strMethodName, String strReturns, String strParams, String strThrows, String strDescription, String strCodeBody)
    {
        if ((strProtection == null) || (strProtection.length() == 0))
            strProtection = "public";
        String strInterfaceEnd = "\n";
        if (strCodeBody == null)
            strCodeBody = "{\n";
        if (";\n".equals(strCodeBody))
        {
            strInterfaceEnd = strCodeBody;
            strCodeBody = null;
        }
        String strDesc = strDescription;
        if (strDesc.length() == 0)
        {
            strDesc = strMethodName + " Method";
            strDesc = new Character(Character.toUpperCase(strDesc.charAt(0))) + strDesc.substring(1);
        }
        strDesc = this.convertDescToJavaDoc(strDesc);
        if (strThrows.length() > 0)
            strThrows = " throws " + strThrows;
        m_StreamOut.writeit("/**\n" + strDesc + "\n */\n");
        if (strParams.equalsIgnoreCase("void"))
            strParams = DBConstants.BLANK;
        if (strReturns.length() == 0)
        {
            m_StreamOut.writeit("public " + strMethodName + "(" + strParams + ")" + strThrows + "\n");
            m_strParams = strParams;    // Also default params for Init() call!
        }
        else
            m_StreamOut.writeit(strProtection + " " + strReturns + " " + strMethodName + "(" + strParams + ")" + strThrows + strInterfaceEnd);
        if (strCodeBody != null)
            m_StreamOut.writeit(strCodeBody);
    }
    /**
     * No code supplied, write default code.
     */
    public void writeDefaultMethodCode(String strMethodName, String strMethodReturns, String strMethodInterface, String strClassName)
    {
        String strBaseClass, strMethodVariables = DBConstants.BLANK;
        Record recClassInfo = this.getMainRecord();
        LogicFile recLogicFile = (LogicFile)this.getRecord(LogicFile.LOGIC_FILE_FILE);
        strBaseClass = recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).getString();  // Get the base class name
        strMethodVariables = this.getMethodVariables(strMethodInterface);
        if (strClassName.equals(strMethodName))
        { // Call super.NewC initializer
            if (strMethodVariables.equalsIgnoreCase("VOID"))
                strMethodVariables = "";
            if (strMethodInterface.length() == 0)
                m_StreamOut.writeit("\tsuper();\n");
            else
            {
                boolean bSuperFound = false;
                if ((strMethodReturns.length() == 0) || (strMethodReturns.equalsIgnoreCase("void")))
                    if (recLogicFile.getField(LogicFile.LOGIC_SOURCE).getLength() > 0)
                        strMethodReturns =  strMethodVariables;   // Special case - if you have code, pass the default variables
                if ((strMethodReturns.length() == 0) || (strMethodReturns.equalsIgnoreCase("void")))
                    m_StreamOut.writeit("\tthis();\n\tthis.init(" + strMethodVariables + ");\n");
                else
                {   // Special Case - Different variables are passed in, must supply and init method w/the correct interface
                    if (!strMethodReturns.equalsIgnoreCase("INIT"))
                    {
                        m_StreamOut.writeit("\tthis();\n\tthis.init(" + strMethodVariables + ");\n");
                        m_StreamOut.writeit("}\n");
                        m_strLastMethodInterface = strMethodInterface;
                        m_strLastMethod = strMethodName;
                        this.writeMethodInterface(null, "init", "void", strMethodInterface, "", "Initialize class fields", null);                   
                        if (strMethodReturns.equalsIgnoreCase("VOID"))
                            strMethodReturns = "";
                        this.writeClassInitialize(true);
                    }
                    else
                        bSuperFound = true; // Don't call init.super();
                    if (recLogicFile.getField(LogicFile.LOGIC_SOURCE).getString().length() != 0)
                    {
                        m_StreamOut.setTabs(+1);
//x                     bSuperFound = m_MethodsOut.writeit(recLogicFile.getField(LogicFile.LOGIC_SOURCE).getString() + "\n");
                        bSuperFound = bSuperFound | this.writeTextField(recLogicFile.getField(LogicFile.LOGIC_SOURCE), strBaseClass, "init", strMethodReturns, strClassName);
                        m_StreamOut.setTabs(-1);
                    }
                    if (!bSuperFound)
                        m_StreamOut.writeit("\tsuper.init(" + strMethodReturns + ");\n");
                }
            }
        }
        else
        { // Call super.NewC
            if (!strMethodName.equals("setupSFields"))
            {
                String beginString = "";
                if (strMethodReturns.length() == 0)
                    beginString = "return ";
                m_StreamOut.writeit("\t" + beginString + "super." + strMethodName + "(" + strMethodVariables + ");\n");
            }
        else
            this.writeSetupSCode(strMethodName, strMethodReturns, strMethodInterface, strClassName);
        }
    }
    /**
     *  Initialize the class.
     */
    public void writeClassInit()
    {
        Record recClassInfo = this.getMainRecord();
        String strClassName = recClassInfo.getField(ClassInfo.CLASS_NAME).getString();
        if ("interface".equals(recClassInfo.getField(ClassInfo.CLASS_TYPE).toString()))
            return;
        if (this.readThisMethod(strClassName))
            this.writeThisMethod(CodeType.THICK);
        else
            this.writeThisMethod(CodeType.THICK);
    }
    /**
     *  Initialize the class
     *  @param bUseInitValues If true, use value. If false, init to null/0/etc.
     */
    public void writeClassInitialize(boolean bUseInitValues)
    {
        Record recClassFields = this.getRecord(ClassFields.CLASS_FIELDS_FILE);
        try   {
            String strFieldName, strReference;
            String strFieldClass;
                //  Now, zero out all the class fields
            recClassFields.close();
            while (recClassFields.hasNext())
            {
                recClassFields.next();
                strFieldName = recClassFields.getField(ClassFields.CLASS_FIELD_NAME).getString();
                String strClassFieldType = recClassFields.getField(ClassFields.CLASS_FIELDS_TYPE).toString();
                if ((strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.CLASS_FIELD))
                    || (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.NATIVE_FIELD)))
                        if (strFieldName.length() != 0)
                    if (!recClassFields.getField(ClassFields.CLASS_FIELD_PROTECT).getString().equalsIgnoreCase("S"))  // Not static
                {
                    strReference = "";
                    if (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.CLASS_FIELD))
                        strReference = "null";
                    else
                    {
                        strReference = recClassFields.getField(ClassFields.CLASS_FIELD_INITIAL).getString();
                        if (strReference.length() == 0)
                        {
                            strReference = "0";
                            strFieldClass = recClassFields.getField(ClassFields.CLASS_FIELD_CLASS).getString();
                            if (strFieldClass.equalsIgnoreCase("String"))
                                strReference = "\"\"";
                        }
                    }
                    if ((!strReference.equals("(none)"))
                        && (recClassFields.getField(ClassFields.CLASS_FIELD_INITIAL_VALUE).isNull()))
                            m_StreamOut.writeit("\t" + strFieldName + " = " + strReference + ";\n");
                }
            }
            recClassFields.close();
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     *  Write the Init() method.
     */
    public void writeInit()
    {
        Record recClassInfo = this.getMainRecord();
        LogicFile recLogicFile = (LogicFile)this.getRecord(LogicFile.LOGIC_FILE_FILE);
        Record recClassFields = this.getRecord(ClassFields.CLASS_FIELDS_FILE);
        try   {
            String strClassName;
            String strMethodName;
            strClassName = recClassInfo.getField(ClassInfo.CLASS_NAME).getString();
            if ("interface".equalsIgnoreCase(recClassInfo.getField(ClassInfo.CLASS_TYPE).toString()))
                return;
            if (this.readThisMethod("init"))
                this.writeThisMethod(CodeType.THICK);
            else
            {
                strMethodName = "init";
                recLogicFile.addNew();
                recLogicFile.getField(LogicFile.METHOD_CLASS_NAME).setString(strClassName);
                recLogicFile.getField(LogicFile.METHOD_NAME).setString(strMethodName);
                MethodInfo methodInfo = new MethodInfo();
                m_MethodHelper.getTheMethodInfo(recLogicFile, methodInfo);   // Get the correct interface, etc..
                    //  Now, zero out all the class fields
        
                if (methodInfo.strMethodInterface.length() == 0)
                {   // Spcial case - no Init method, but fields to Init
                    recClassFields.close();
        
                    if (recClassFields.hasNext())
                        methodInfo.strMethodInterface = m_strParams;    // Same as constructor
                }
                if (methodInfo.strMethodInterface.length() != 0)
                    if (!(strClassName.equals(m_strLastMethod)))    // Don't write same init method twice!
                    if (!(methodInfo.strMethodInterface.equals(m_strLastMethodInterface)))
                {
                    recClassFields.close();
                    recClassFields.moveFirst();
        
                    this.writeMethodInterface(null, "init", "void", methodInfo.strMethodInterface, "", "Initialize class fields", null);
                    methodInfo.strMethodInterface = this.getMethodVariables(methodInfo.strMethodInterface);
                    this.writeClassInitialize(false);
                    m_StreamOut.writeit("\tsuper.init(" + methodInfo.strMethodInterface + ");\n");
                    m_StreamOut.writeit("}\n");
                }
            }
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     * WriteSetupSCode.
     */
    public void writeSetupSCode(String strMethodName, String strMethodReturns, String strMethodInterface, String strClassName)
    {
        Record recFieldData = new FieldData(this);
        try   {
            String strScreenFieldName, strScreenLocation, strScreenFieldDesc, strScreenRow, strScreenCol, strScreenOutNumber, strScreenSetAnchor;
            String strFileName = DBConstants.BLANK;
            String getFile = "getMainRecord()";
            int oldRow = 2, oldCol = 21;

            Record recScreenIn = this.getRecord(ScreenIn.SCREEN_IN_FILE);
            SubFileFilter newBehavior = new SubFileFilter(recScreenIn.getField(ScreenIn.SCREEN_FILE_NAME), FieldData.FIELD_FILE_NAME, recScreenIn.getField(ScreenIn.SCREEN_FIELD_NAME), FieldData.FIELD_NAME, null, null);
            recFieldData.addListener(newBehavior);
            recFieldData.setKeyArea(FieldData.FIELD_NAME_KEY);
        
            recScreenIn.close();
            while (recScreenIn.hasNext())
            {
                recScreenIn.next();
                if (recScreenIn.getField(ScreenIn.SCREEN_FIELD_NAME).getString().length() != 0)
                {
                    strScreenFieldName = recScreenIn.getField(ScreenIn.SCREEN_FIELD_NAME).getString();
                    strScreenLocation = recScreenIn.getField(ScreenIn.SCREEN_LOCATION).getString();
                    strScreenFieldDesc = recScreenIn.getField(ScreenIn.SCREEN_FIELD_DESC).getString();
                    strScreenOutNumber = recScreenIn.getField(ScreenIn.SCREEN_OUT_NUMBER).getString();
                    strScreenSetAnchor = recScreenIn.getField(ScreenIn.SCREEN_ANCHOR).getString();
                    int row = (int)((NumberField)recScreenIn.getField(ScreenIn.SCREEN_ROW)).getValue();
                    int col = (int)((NumberField)recScreenIn.getField(ScreenIn.SCREEN_COL)).getValue();
                    if (strScreenOutNumber.equalsIgnoreCase("1")) if (strScreenLocation.length() == 0)
                    {
                        if (row > 2)
                            ((NumberField)recScreenIn.getField(ScreenIn.SCREEN_ROW)).setValue(row-2, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
                    }
                    strScreenRow = recScreenIn.getField(ScreenIn.SCREEN_ROW).getString();
                    strScreenCol = recScreenIn.getField(ScreenIn.SCREEN_COL).getString();
        
                        if (recScreenIn.getField(ScreenIn.SCREEN_FILE_NAME).getLength() != 0)
                        {
                            strFileName = recScreenIn.getField(ScreenIn.SCREEN_FILE_NAME).getString();
                            getFile = "getRecord(" + strFileName + "." + this.convertNameToConstant(strFileName + "File") + ")";
                        }
                        else
                            getFile = "getMainRecord()";
                    // Screen location
                    if (strScreenOutNumber.equalsIgnoreCase("1"))
                        if (strScreenLocation.length() == 0)
                            if (row == oldRow+1) if (col == oldCol)
                                strScreenLocation = "NEXT_LOGICAL";   // Next Logical
        
                    if (strScreenLocation.length() == 0)
                        strScreenLocation = "NEXT_LOGICAL";         // Use this R/C
                    if (strScreenLocation.length() == 1)
                    {
                        switch (strScreenLocation.charAt(0))
                        {
                            case 'U':
                                strScreenLocation = "USE_ROW_COL";break;
                            case 'R':
                                strScreenLocation = "RIGHT_OF_LAST";break;
                            case 'T':
                                strScreenLocation = "TOP_NEXT";break;
                            case 'B':
                                strScreenLocation = "BELOW_LAST";break;
                            case 'A':
                                strScreenLocation = "AT_ANCHOR";break;
                            case 'L':
                                strScreenLocation = "USE_THIS_LOCATION";break;
                            case 'D':
                            case 'N':
                            default:
                                strScreenLocation = "NEXT_LOGICAL";break;
                        }
                    }
                    if (strScreenLocation.equalsIgnoreCase("USE_ROW_COL"))
                        strScreenLocation = "this.getNextLocation(" + strScreenCol + ", " + strScreenRow;
                    else
                        strScreenLocation = "this.getNextLocation(ScreenConstants." + strScreenLocation;
// Set anchor?
                    if (strScreenSetAnchor.equalsIgnoreCase("N"))
                        strScreenSetAnchor = "DONT_SET_ANCHOR";
                    else if (strScreenSetAnchor.equalsIgnoreCase("Y"))
                        strScreenSetAnchor = "SET_ANCHOR";
                    else if (strScreenSetAnchor.length() == 0)
                        strScreenSetAnchor = "ANCHOR_DEFAULT";
                    strScreenLocation += ", ScreenConstants." + strScreenSetAnchor + ")";
// Display the field desc?
                    if (strScreenFieldDesc.equalsIgnoreCase("Y"))
                        strScreenFieldDesc = "DISPLAY_FIELD_DESC";
                    else if (strScreenFieldDesc.equalsIgnoreCase("N"))
                        strScreenFieldDesc = "DONT_DISPLAY_FIELD_DESC";
                    else if (strScreenFieldDesc.length() == 0)
                        strScreenFieldDesc = "DEFAULT_DISPLAY";
                    strScreenFieldDesc = "ScreenConstants." + strScreenFieldDesc;
// View Control type
                    String controlType = recScreenIn.getField(ScreenIn.SCREEN_CONTROL_TYPE).getString();
                    String strDisabledEnding = DBConstants.BLANK;
                    if ("disabled".equals(controlType))
                    {
                        controlType = DBConstants.BLANK;
                        strDisabledEnding = ".setEnabled(false)";
                    }
                    String fieldString = "this." + getFile + ".getField(" + strFileName + "." + this.convertNameToConstant(strScreenFieldName) + ")";
                    if (controlType.length() == 1)
                    {
                        switch (controlType.charAt(0))
                        {
                        case 'S': // Static
                            controlType = "SStaticText";break;
                        case 'T':
                            controlType = "STEView";break;
                        case 'N':
                            controlType = "SNumberText";break;
                        case 'C':
                            controlType = "SCheckBox";break;
                        case 'R':
                            controlType = "SRadioButton";break;
                        case 'B':
                            controlType = "SButtonBox";break;
                        case 'P':
                            controlType = "SPopupBox";break;
                        };
                    }
                    if (controlType.length() == 0)  // Default
                        m_StreamOut.writeit("\t" + fieldString + ".setupDefaultView(" + strScreenLocation + ", this, " + strScreenFieldDesc + ")" + strDisabledEnding + ";\n");
                    else
                    {
                        if (controlType.startsWith("S"))
                            if (Character.isUpperCase(controlType.charAt(1)))
                                controlType = controlType.substring(1);
                        controlType = this.convertNameToConstant(controlType);
                        m_StreamOut.writeit("\tcreateScreenComponent(ScreenModel." + controlType + ", " + strScreenLocation + ", this, fieldString, " + strScreenFieldDesc + ", null)" + strDisabledEnding + ";\n");
                        //m_StreamOut.writeit("\tnew " + controlType + "(" + strScreenLocation + ", this, " + fieldString + ", " + strScreenFieldDesc + ", null)" + strDisabledEnding + ";\n");
                    }
                    oldRow = row;
                    oldCol = col;
                }
                if (recScreenIn.getField(ScreenIn.SCREEN_TEXT).getString().length() != 0)
                {
                    m_StreamOut.setTabs(+1);
                    String tempString = recScreenIn.getField(ScreenIn.SCREEN_TEXT).getString();
                    m_StreamOut.writeit(tempString);
                    if (tempString.charAt(tempString.length() - 1) != '\n')
                        m_StreamOut.writeit("\n");
                    m_StreamOut.setTabs(-1);
                }
            }
            recScreenIn.close();
        
            recFieldData.removeListener(newBehavior, true);  // Remove and free this listener
        } catch (DBException ex)   {
            ex.printStackTrace();
        } finally {
            recFieldData.free();
        }
    }
    /**
     *  Create the default file Class
     */
    public void writeProgramDesc(String strClassName)
    {
        // Override this to write the program desc.
        ClassInfo recClassInfo = (ClassInfo)this.getRecord(ClassInfo.CLASS_INFO_FILE);
        if ("Screen".equalsIgnoreCase(recClassInfo.getField(ClassInfo.CLASS_TYPE).toString()))
        {
            if (!recClassInfo.getField(ClassInfo.CLASS_DESC).isNull())
                this.writeGetTitleCode(recClassInfo.getField(ClassInfo.CLASS_DESC).getString());
        }
    }
    public void writeGetTitleCode(String strClassDesc)
    {
        this.writeMethodInterface(null, "getTitle", "String", "", "", "Get the screen display title", null);
        m_StreamOut.writeit("\treturn \"" + strClassDesc + "\";\n");
        m_StreamOut.writeit("}\n");
    }
    /**
     * Write the methods for this class.
     * @param codeType Interface only
     */
    public void writeClassMethods(CodeType codeType)
    {
        LogicFile recLogicFile = (LogicFile)this.getRecord(LogicFile.LOGIC_FILE_FILE);
        try   {
            Set<String> methodsIncluded = new HashSet<String>();
            Set<String> methodInterfaces = new HashSet<String>();
            recLogicFile.close();
            while (recLogicFile.hasNext())
            {
                recLogicFile.next();
                if (((IncludeScopeField)recLogicFile.getField(LogicFile.INCLUDE_SCOPE)).includeThis(codeType, false))
                {
                    this.writeThisMethod(codeType);
                    String strMethodName = recLogicFile.getField(LogicFile.METHOD_NAME).toString();
                    if (strMethodName.length() > 2) if (strMethodName.charAt(strMethodName.length() - 2) == '*')
                        strMethodName = strMethodName.substring(0, strMethodName.length() - 2);
                    methodsIncluded.add(strMethodName);
                }
                if (((IncludeScopeField)recLogicFile.getField(LogicFile.INCLUDE_SCOPE)).includeThis(CodeType.INTERFACE, false))
                {
                    String strMethodName = recLogicFile.getField(LogicFile.METHOD_NAME).toString();
                    if (strMethodName.length() > 2) if (strMethodName.charAt(strMethodName.length() - 2) == '*')
                        strMethodName = strMethodName.substring(0, strMethodName.length() - 2);
                    methodInterfaces.add(strMethodName);                    
                }
            }
            // Now add a empty implementation for any methods that I don't have
            recLogicFile.close();
            while (recLogicFile.hasNext())
            {
                recLogicFile.next();
                String strMethodName = recLogicFile.getField(LogicFile.METHOD_NAME).toString();
                if (strMethodName.length() > 2) if (strMethodName.charAt(strMethodName.length() - 2) == '*')
                    strMethodName = strMethodName.substring(0, strMethodName.length() - 2);
                if ((methodInterfaces.contains(strMethodName)) && (!methodsIncluded.contains(strMethodName)))
                    this.writeThisMethod(codeType); // Write a default impl of this method.
            }
            recLogicFile.close();
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     * Write this text field out - line by line.
     *  @return   true if "super" found in text.
     */
    public boolean writeTextField(BaseField textField, String strBaseClass, String strMethodName, String strMethodInterface, String strClassName)
    {
        if (textField.getString().length() == 0)
            return false;
        String beforeMethodCode;    //, currentString;
        beforeMethodCode = textField.getString();
        int inher = beforeMethodCode.indexOf("super;");
        if (inher != -1)
        {
            String strSuper = "super";
            if (strClassName != strMethodName)
                strSuper = "super." + strMethodName;
            strMethodInterface = this.getMethodVariables(strMethodInterface);
            beforeMethodCode = beforeMethodCode.substring(0, inher) + strSuper + "(" + strMethodInterface + ");" + beforeMethodCode.substring(inher + 6, beforeMethodCode.length());
        }
        m_StreamOut.writeit(beforeMethodCode);
        m_StreamOut.writeit("\n");
        return (inher != -1);
    }
    /**
     * GetMethodVariables.
     */
    public String getMethodVariables(String strMethodInterface)
    {
        int endChar = 0, startChar = 0;
        String strMethodVariables = "";
        int i = 0;
        boolean bBracketFound = false;
        for (i = 0; i < strMethodInterface.length(); i++)
        {
            if (strMethodInterface.charAt(i) == '<')
                bBracketFound = true;
            if (strMethodInterface.charAt(i) == '>')
                bBracketFound = false;
            if (bBracketFound)
                continue;   // Skip the area between the brackets.
            if (strMethodInterface.charAt(i) == ' ')
                startChar = i + 1;
            if (strMethodInterface.charAt(i) == ',')
            {
                endChar = i;
                if (endChar > startChar)
                {
                    if (strMethodVariables.length() != 0)
                        strMethodVariables += ", ";
                    strMethodVariables += strMethodInterface.substring(startChar, endChar);
                }
            }
        }
        endChar = i;
        if (endChar > startChar)
        {
            if (strMethodVariables.length() != 0)
                strMethodVariables += ", ";
            strMethodVariables += strMethodInterface.substring(startChar, endChar);
        }
        return strMethodVariables;
    }
    /**
     * WriteThisMethod - Write this method for this class.
     * @param codeType Interface only
     */
    public void writeThisMethod(CodeType codeType)
    {
        Record recClassInfo = this.getMainRecord();
        LogicFile recLogicFile = (LogicFile)this.getRecord(LogicFile.LOGIC_FILE_FILE);
        String strClassName = recClassInfo.getField(ClassInfo.CLASS_NAME).getString();
        MethodInfo methodInfo = new MethodInfo();
        m_MethodHelper.getTheMethodInfo(recLogicFile, methodInfo);   // Get the correct interface, etc..
        String strMethodName = recLogicFile.getField(LogicFile.METHOD_NAME).getString();
        String strMethodDesc = recLogicFile.getField(LogicFile.LOGIC_DESCRIPTION).getString();
        String strProtection = recLogicFile.getField(LogicFile.PROTECTION).getString();
        if (m_MethodNameList.addName(strMethodName) == false)
            return;     // Don't write it, its already here
        if (strMethodName.length() > 2) if (strMethodName.charAt(strMethodName.length() - 2) == '*')
            strMethodName = strMethodName.substring(0, strMethodName.length() - 2);
        if (strMethodName.equals(strClassName))
        {
            if (codeType == CodeType.THICK)
            {
                if ("".equalsIgnoreCase(methodInfo.strMethodInterface))
                    return;    // Default constructor is already there
                this.writeMethodInterface(strProtection, strMethodName, "", methodInfo.strMethodInterface, methodInfo.strMethodThrows, strMethodDesc, null);
                this.writeDefaultMethodCode(strMethodName, methodInfo.strMethodReturns, methodInfo.strMethodInterface, strClassName);
            }
        }
        else
        {
            if (methodInfo.strMethodReturns.length() >= 7) if (methodInfo.strMethodReturns.substring(methodInfo.strMethodReturns.length() - 7, methodInfo.strMethodReturns.length()).equalsIgnoreCase("  "))
                methodInfo.strMethodReturns = methodInfo.strMethodReturns.substring(0, methodInfo.strMethodReturns.length() - 6);
            String strCodeBody = null;
            if ((codeType == CodeType.INTERFACE) || ("interface".equals(recClassInfo.getField(ClassInfo.CLASS_TYPE).toString())))
            {
                if (!((IncludeScopeField)recLogicFile.getField(LogicFile.INCLUDE_SCOPE)).includeThis(CodeType.INTERFACE, false))
                    return;
                strCodeBody = ";\n";
            }
            else if (!((IncludeScopeField)recLogicFile.getField(LogicFile.INCLUDE_SCOPE)).includeThis(codeType, false))
            {   // Special case - write a default impl.
                if ((methodInfo.strMethodReturns == null) || ("void".equals(methodInfo.strMethodReturns)) || (DBConstants.BLANK.equals(methodInfo.strMethodReturns)))
                    strCodeBody = "{\n\t// Empty implementation\n}\n";
                else if ("boolean".equals(methodInfo.strMethodReturns))
                    strCodeBody = "{\n\treturn false; // Empty implementation\n}\n";
                else if (("int".equals(methodInfo.strMethodReturns)) || ("short".equals(methodInfo.strMethodReturns)) || ("long".equals(methodInfo.strMethodReturns)))
                    strCodeBody = "{\n\treturn -1; // Empty implementation\n}\n";
                else
                    strCodeBody = "{\n\treturn null; // Empty implementation\n}\n";
            }
            this.writeMethodInterface(strProtection, strMethodName, methodInfo.strMethodReturns, methodInfo.strMethodInterface, methodInfo.strMethodThrows, strMethodDesc, strCodeBody);
        }
        if ((codeType == CodeType.INTERFACE) || ("interface".equals(recClassInfo.getField(ClassInfo.CLASS_TYPE).toString())))
            return;
        if (!((IncludeScopeField)recLogicFile.getField(LogicFile.INCLUDE_SCOPE)).includeThis(codeType, false))
            return;
        if (!strMethodName.equals(strClassName))
        {
            if (recLogicFile.getField(LogicFile.LOGIC_SOURCE).getString().length() != 0)
            {
                String strBaseClass;
                strBaseClass = recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).getString();
                m_StreamOut.setTabs(+1);
                this.writeTextField(recLogicFile.getField(LogicFile.LOGIC_SOURCE), strBaseClass, strMethodName, methodInfo.strMethodInterface, strClassName);
                m_StreamOut.setTabs(-1);
            }
            else
                this.writeDefaultMethodCode(strMethodName, methodInfo.strMethodReturns, methodInfo.strMethodInterface, strClassName);
        }
        m_StreamOut.writeit("}\n");
    }
    /**
     *  Take out the spaces in the name.
     */
    public String fixSQLName(String strName)
    {
        int index = 0;
        while (index != -1)
        {
            index = strName.indexOf(' ');
            if (index != -1)
                strName = strName.substring(0, index) + strName.substring(index + 1, strName.length());
        }
        return strName;
    }
    /**
     * Convert the description to a javadoc compatible description.
     * @param strDesc The original description string.
     * @return The new javadoc description.
     */
    String convertDescToJavaDoc(String strDesc)
    {
        for (int i = 0; i < strDesc.length() - 1; i++)
        {
            if (strDesc.charAt(i) == '\n')
                strDesc = strDesc.substring(0, i + 1) + " * " + strDesc.substring(i + 1);
        }
        strDesc = " * " + strDesc;
        if (!strDesc.endsWith("."))
            strDesc += '.';
        return strDesc;
    }
    /**
     * A utility name to convert a java name to a constant.
     * (ie., thisClassName -> THIS_CLASS_NAME)
     */
    public String convertNameToConstant(String strName)
    {
        String strConstants = DBConstants.BLANK;
        int iLastUpper = -1;
        for (int i = 0; i < strName.length(); i++)
        {
            char chNext = strName.charAt(i);
            if (!Character.isLowerCase(chNext))
            {   // The next word is always uppercase.
                if (i != iLastUpper + 1)
                    strConstants += "_";    // Previous letter was not uppercase, this starts a new word
                else if ((iLastUpper != -1) && (i + 1 < strName.length()) && (Character.isLowerCase(strName.charAt(i + 1))))
                    strConstants += "_";    // Previous letter was upper and next is lower, start a new word
                iLastUpper = i;
            }
            strConstants += Character.toUpperCase(chNext);
        }
        return strConstants;
    }
}
