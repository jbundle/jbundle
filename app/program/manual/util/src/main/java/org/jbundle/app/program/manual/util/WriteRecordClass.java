/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util;

import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import org.jbundle.app.program.db.ClassFields;
import org.jbundle.app.program.db.ClassFieldsTypeField;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.app.program.db.ClassProject;
import org.jbundle.app.program.db.FieldData;
import org.jbundle.app.program.db.FieldTypeField;
import org.jbundle.app.program.db.FileHdr;
import org.jbundle.app.program.db.IncludeScopeField;
import org.jbundle.app.program.db.KeyInfo;
import org.jbundle.app.program.db.KeyTypeField;
import org.jbundle.app.program.db.ProgramControl;
import org.jbundle.app.program.db.util.ResourcesUtilities;
import org.jbundle.app.program.manual.util.data.FieldStuff;
import org.jbundle.app.program.resource.db.ResourceTypeField;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.field.CurrencyField;
import org.jbundle.base.field.DateField;
import org.jbundle.base.field.DateTimeField;
import org.jbundle.base.field.DoubleField;
import org.jbundle.base.field.FloatField;
import org.jbundle.base.field.IntegerField;
import org.jbundle.base.field.MemoField;
import org.jbundle.base.field.RealField;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.field.ShortField;
import org.jbundle.base.field.TimeField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.app.program.db.ClassProjectModel.CodeType;
import org.jbundle.model.db.Rec;


/**
 *  WriteJava - Constructor.
 */
public class WriteRecordClass extends WriteSharedClass
{
    /**
     * Special resource only base class.
     */
    public static final String RESOURCE_CLASS = "ListResourceBundle";     // Resource only class

    Record recClassInfo2 = null;
    Record recKeyInfo2 = null;

    /**
     * Constructor.
     */
    public WriteRecordClass()
    {
        super();
    }
    /**
     * Constructor.
     */
    public WriteRecordClass(Task taskParent, Record recordMain, Map<String,Object> properties)
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
        
        new FileHdr(this);
        new KeyInfo(this);
    }
    /**
     *
     */
    public void addListeners()
    {
        super.addListeners();
        
        Record recClassInfo = this.getMainRecord();

        Record recKeyInfo = this.getRecord(KeyInfo.KEY_INFO_FILE);
        recKeyInfo.setKeyArea(KeyInfo.KEY_FILENAME_KEY);
        SubFileFilter keyBehavior = new SubFileFilter(recClassInfo.getField(ClassInfo.CLASS_NAME), KeyInfo.KEY_FILENAME, null, null, null, null);
        recKeyInfo.addListener(keyBehavior);
        recKeyInfo.setKeyArea(KeyInfo.KEY_FILENAME_KEY);
    }
    /**
     *
     */
    public void free()
    {
        super.free();
    }
    /**
     *  Create the Record Class for this Record
     */
    public void writeClass(String strClassName, ClassProject.CodeType codeType)
    {
        FieldStuff fieldStuff = new FieldStuff();
        ClassInfo recClassInfo = (ClassInfo)this.getMainRecord();
        if (!this.readThisClass(strClassName))  // Get the field this is based on
            return;
        if (RESOURCE_CLASS.equals(recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).toString()))
        {
            this.writeFieldResources(strClassName);
            return;     // Resource only class
        }

        this.writeHeading(strClassName, this.getPackage(codeType), CodeType.THICK);        // Write the first few lines of the files
        this.writeIncludes(CodeType.THICK);
    // Now include any BaseField classes not included in this source (method include)
        try   {
            FieldData recFieldData = (FieldData)this.getRecord(FieldData.FIELD_DATA_FILE);

            this.writeRecordClassDetail(strClassName);  // Write the Thick Record Class

            this.writeFieldResources(strClassName);

            this.writeThinRecord(strClassName);

            this.writeRecordInterface(strClassName);  // Write the Record Class Interface

        // Now write out the BaseField classes
            Record recFileHdr = this.getRecord(FileHdr.FILE_HDR_FILE);
        
            FieldIterator fieldIterator = new FieldIterator(recFileHdr, recClassInfo, recFieldData, true);
            while (fieldIterator.hasNext())
            {
                recFieldData = (FieldData)fieldIterator.next();
                ClassInfo recClassInfo2 = null;
                boolean ignoreThisField = this.ignoreFieldData(recFieldData);
                this.getFieldData(fieldStuff, false);
                if ((recFieldData.getField(FieldData.FIELD_TYPE).getString().equalsIgnoreCase(FieldTypeField.VIRTUAL_FIELD)) || (fieldStuff.bNotNullField))
                    ignoreThisField = false;
                if (ignoreThisField)
                    continue;
                if ((recClassInfo2 = this.readFieldClass()) != null)
                    if (recClassInfo2.getField(ClassInfo.CLASS_SOURCE_FILE).equals(recClassInfo.getField(ClassInfo.CLASS_SOURCE_FILE)))
                {   // If they are in the same source file, write the field class also.
                    String strFieldDataHandle = recFieldData.getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                    Map<String,Object> properties = new Hashtable<String,Object>();
                    properties.put(FieldData.FIELD_DATA_FILE, strFieldDataHandle);
                    if ((recClassInfo2.getEditMode() == DBConstants.EDIT_CURRENT)
                        || (recClassInfo2.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    {
                        String strClassInfoHandle = recClassInfo2.getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                        properties.put(ClassInfo.CLASS_INFO_FILE, strClassInfoHandle);
                    }
                    else
                    {
                        properties.put(recClassInfo2.getField(ClassInfo.CLASS_NAME).getFieldName(), recClassInfo2.getField(ClassInfo.CLASS_NAME).toString());
                        properties.put(recClassInfo2.getField(ClassInfo.BASE_CLASS_NAME).getFieldName(), recClassInfo2.getField(ClassInfo.BASE_CLASS_NAME).toString());
                        properties.put(recClassInfo2.getField(ClassInfo.CLASS_PACKAGE).getFieldName(), this.getPackage(codeType));
                        properties.put(recClassInfo2.getField(ClassInfo.CLASS_PROJECT_ID).getFieldName(), recClassInfo2.getField(ClassInfo.CLASS_PROJECT_ID).toString());
                    }

                        WriteFieldClass classWriter = new WriteFieldClass(this.getTask(), null, properties);
                        String strFieldClassName = null;
                        classWriter.writeClass(strFieldClassName, codeType);
                        classWriter.free();
                        classWriter = null;
                }
            }
            recFieldData.close();
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     *  Write the includes
     */
    public void writeIncludes(CodeType codeType)
    {
        super.writeIncludes(codeType);
        if (codeType != CodeType.THICK)
            return;
        // Thick only
        FieldStuff fieldStuff = new FieldStuff();
        ClassInfo recClassInfo = (ClassInfo)this.getMainRecord();
        FieldData recFieldData = (FieldData)this.getRecord(FieldData.FIELD_DATA_FILE);
        Record recFileHdr = this.getRecord(FileHdr.FILE_HDR_FILE);

        FieldIterator fieldIterator = new FieldIterator(recFileHdr, recClassInfo, recFieldData, true);
        while (fieldIterator.hasNext())
        {
            fieldIterator.next();
            boolean ignoreThisField = this.ignoreFieldData(recFieldData);
            this.getFieldData(fieldStuff, false);
            if ((recFieldData.getField(FieldData.FIELD_TYPE).getString().equalsIgnoreCase(FieldTypeField.VIRTUAL_FIELD)) || (fieldStuff.bNotNullField))
                ignoreThisField = false;
            if (ignoreThisField)
                continue;
            ClassInfo recClassInfo2 = null;
            if ((recClassInfo2 = this.readFieldClass()) != null)   // Get the field this is based on
            {       // Not in this source
                String strFieldClass = recClassInfo2.getField(ClassInfo.CLASS_NAME).getString();
                m_IncludeNameList.addInclude(strFieldClass, null); // include the source
            }
        }
    }
    /**
     *  Write the resource file for this record class
     */
    public void writeFieldResources(String strClassName)
    {
    	boolean bResourceListBundle = ResourceTypeField.LIST_RESOURCE_BUNDLE.equals(this.getRecord(ProgramControl.PROGRAM_CONTROL_FILE).getField(ProgramControl.RESOURCE_TYPE).toString());
        Record recClassInfo = this.getMainRecord();
        String strPackage = this.getPackage(bResourceListBundle ? CodeType.RESOURCE_CODE : CodeType.RESOURCE_PROPERTIES);
    // Now, write the field resources (descriptions)
        FieldStuff fieldStuff = new FieldStuff();
        String strBaseClass = "ListResourceBundle";
        String strClassSuffix = "Resources";
        boolean bResourceOnlyFile = false;
        if (RESOURCE_CLASS.equals(recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).toString()))
            bResourceOnlyFile = true;     // Resource only class
        if (bResourceOnlyFile)
            if (strClassName.endsWith(strClassSuffix))
                strClassSuffix = "";
        if (!bResourceListBundle)
        	strClassSuffix += ".properties";
        
        this.writeHeading(strClassName + strClassSuffix, strPackage, bResourceListBundle ? ClassProject.CodeType.RESOURCE_CODE : ClassProject.CodeType.RESOURCE_PROPERTIES);
        if (bResourceListBundle)
        {
	        m_StreamOut.writeit("package " + strPackage + ";\n\n");
	
	        m_StreamOut.writeit("import java.util.*;\n\n");
	        m_StreamOut.writeit("public class "+ strClassName + strClassSuffix + " extends " + strBaseClass + "\n");
	        m_StreamOut.writeit("{\n");
	        m_StreamOut.setTabs(+1);
	        m_StreamOut.writeit("public Object[][] getContents()\n");
	        m_StreamOut.writeit("{\n");
	        m_StreamOut.writeit("\treturn contents;\n");
	        m_StreamOut.writeit("}\n");
	
	        m_StreamOut.writeit("\n");
	        m_StreamOut.writeit("// To Localize this, just change the strings in the second column\n");
	        m_StreamOut.writeit("protected static final Object[][] contents =\n");
	        m_StreamOut.writeit("{\n");
	        m_StreamOut.setTabs(-1);
        }
        int count = 0;
        try   {
            Record recFieldData = this.getRecord(FieldData.FIELD_DATA_FILE);
            if (!bResourceOnlyFile)
            {
                recFieldData.close();
                while (recFieldData.hasNext())
                {
                    recFieldData.next();

                    this.getFieldData(fieldStuff, false);

                    {
                        if (bResourceListBundle)
                            if (count > 0)
                                m_StreamOut.writeit(",");
                        if (count > 0)
                            m_StreamOut.writeit("\n");
                        if (fieldStuff.strFieldDesc.equals("null"))
                            fieldStuff.strFieldDesc = fieldStuff.strFieldName;
                        if (bResourceListBundle)
                        	m_StreamOut.writeit("\t\t{\"" + ResourcesUtilities.fixPropertyKey(fieldStuff.strFieldName) + "\", " + ResourcesUtilities.fixPropertyValue(fieldStuff.strFieldDesc, bResourceListBundle) + "}");
                        else
                        	m_StreamOut.writeit(ResourcesUtilities.fixPropertyKey(fieldStuff.strFieldName) + "=" + ResourcesUtilities.fixPropertyValue(fieldStuff.strFieldDesc, bResourceListBundle));
                        count++;
                    }
                }
    // Now write out the tips
                this.readRecordClass(strClassName);     // Return the record to the original position
            }
            String strTipSuffix = DBConstants.TIP;
            if (bResourceOnlyFile)
                strTipSuffix = "";

            recFieldData.close();
            while (recFieldData.hasNext())
            {
                recFieldData.next();
                String strPre = DBConstants.BLANK;
                if (recFieldData.getField(FieldData.FIELD_NAME).getString().equals(recFieldData.getField(FieldData.BASE_FIELD_NAME).getString()))
                	if (recFieldData.getField(FieldData.FIELD_DESC_VERTICAL).getLength() == 0)
                	{
                        if (bResourceListBundle)
                        	strPre = "//";
                        else
                        	strPre = "# ";
                	}

                this.getFieldData(fieldStuff, false);

                if (fieldStuff.strFieldTip != null)
                {
                    if (bResourceListBundle)
                        if (count > 0)
                            m_StreamOut.writeit(",");
                    if (count > 0)
                        m_StreamOut.writeit("\n");

                    if (bResourceListBundle)
                    	m_StreamOut.writeit(strPre + "\t\t{\"" + ResourcesUtilities.fixPropertyKey(fieldStuff.strFieldName + strTipSuffix) + "\", " + ResourcesUtilities.fixPropertyValue(fieldStuff.strFieldTip, bResourceListBundle) + "}");
                    else
                    	m_StreamOut.writeit(strPre + ResourcesUtilities.fixPropertyKey(fieldStuff.strFieldName + strTipSuffix) + "=" + ResourcesUtilities.fixPropertyValue(fieldStuff.strFieldTip, bResourceListBundle));
                    count++;
                }
            }

            if (bResourceListBundle)
            {
	            m_StreamOut.writeit("\n");
	            m_StreamOut.setTabs(+1);
	            m_StreamOut.writeit("// END OF MATERIAL TO LOCALIZE\n");
	            m_StreamOut.writeit("};\n");
            }
            recFieldData.close();
            this.writeEndCode(bResourceListBundle);
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
        this.readRecordClass(strClassName);     // Return the record to the original position
     }
    /**
     *  Write the resource file for this record class
     */
    public void writeRecordInterface(String strClassName)
    {
        ClassInfo recClassInfo = (ClassInfo)this.getMainRecord();
        Record recFileHdr = this.getRecord(FileHdr.FILE_HDR_FILE);
        FieldData recFieldData = (FieldData)this.getRecord(FieldData.FIELD_DATA_FILE);
        if (!recClassInfo.isARecord(false))
            return;
        String strDBType = recFileHdr.getField(FileHdr.TYPE).getString(); // Is Remote file?
        strDBType = this.fixDBType(strDBType, "Constants.");
        String strPackage = this.getPackage(CodeType.INTERFACE);
    // Now, write the field resources (descriptions)
        String strBaseClass = recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).getString();
        if ((strBaseClass.equalsIgnoreCase("Record")) || (strBaseClass.equalsIgnoreCase("QueryRecord")))
            strBaseClass = "";  // .model.db.Rec
        this.writeHeading(strClassName + "Model", strPackage, ClassProject.CodeType.INTERFACE);
        this.writeIncludes(CodeType.INTERFACE);

        String baseClassPackage = "";
        if (!strBaseClass.equals(""))
        {
            this.readRecordClass(strBaseClass);     // Return the record to the original position
            ClassProject classProject = (ClassProject)((ReferenceField)recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID)).getReference();
            baseClassPackage = classProject.getFullPackage(CodeType.INTERFACE, recClassInfo.getField(ClassInfo.CLASS_PACKAGE).toString());
            if ((baseClassPackage.equalsIgnoreCase("org.jbundle.model.db")) || (baseClassPackage.equalsIgnoreCase("org.jbundle.model.base.db")))
                strBaseClass = "";  // The only interface in the model package is 'Rec'.
            strBaseClass = strBaseClass + "Model";
        }
        if ((strBaseClass.equals("")) || (strBaseClass.equals("Model")))
            strBaseClass = "Rec";
        if (strBaseClass.equals("Rec"))
            baseClassPackage = "org.jbundle.model.db";
        m_StreamOut.writeit("import " + baseClassPackage + ".*;\n");            
        m_StreamOut.writeit("\n");
        
        m_StreamOut.writeit("public interface " + strClassName + "Model extends " + strBaseClass + "\n");
        m_StreamOut.writeit("{\n");
        m_StreamOut.setTabs(+1);

        this.readRecordClass(strClassName);     // Return the record to the original position
        FieldIterator fieldIterator = new FieldIterator(recFileHdr, recClassInfo, recFieldData, true);
        this.writeFieldOffsets(fieldIterator, CodeType.INTERFACE);
        this.writeKeyOffsets(CodeType.INTERFACE);   // Write the Key offsets
        this.writeClassFields(CodeType.INTERFACE);    // Write the thin class fields.
        
        m_StreamOut.writeit("\n");

        String dBFileName = recFileHdr.getField(FileHdr.FILE_MAIN_FILENAME).getString();
        if (dBFileName.length() != 0)
        {
            dBFileName = this.fixSQLName(dBFileName);
            if (dBFileName.equalsIgnoreCase("NONE"))
                dBFileName = null;
        }
        else if (recFileHdr.getField(FileHdr.TYPE).toString().indexOf("MAPPED") == -1)
            dBFileName = strClassName;  // Default file name (unless mapped)

        if ((dBFileName != null) && (dBFileName.length() > 0))
        {
            m_StreamOut.writeit("public static final String " + this.convertNameToConstant(strClassName) + "_FILE = \"" + dBFileName + "\";\n");

            m_StreamOut.writeit("public static final String THIN_CLASS = \"" + this.getPackage(CodeType.THIN) + "." + strClassName + "\";\n");
            m_StreamOut.writeit("public static final String THICK_CLASS = \"" + this.getPackage(CodeType.THICK) + "." + strClassName + "\";\n");
        }
         
        if (m_MethodNameList.size() != 0)
            m_MethodNameList.removeAllElements();
        this.writeClassMethods(CodeType.INTERFACE);   // Write the methods (that are) interfaces

        m_StreamOut.setTabs(-1);
        this.writeEndCode(true);
        recFieldData.close();

        this.readRecordClass(strClassName);     // Return the record to the original position
    }
    /**
     *  Write the resource file for this record class
     */
    public void writeThinRecord(String strClassName)
    {
        ClassInfo recClassInfo = (ClassInfo)this.getMainRecord();
        if (!recClassInfo.isARecord(false))
            return;
        Record recFileHdr = this.getRecord(FileHdr.FILE_HDR_FILE);
        FieldData recFieldData = (FieldData)this.getRecord(FieldData.FIELD_DATA_FILE);
        String strDatabaseName = recFileHdr.getField(FileHdr.DATABASE_NAME).getString();   // Database name
        String strDBType = recFileHdr.getField(FileHdr.TYPE).getString(); // Is Remote file?
        strDBType = this.fixDBType(strDBType, "Constants.");
        String strPackage = this.getPackage(CodeType.THIN);
        String implementsPackage = this.getPackage(CodeType.INTERFACE);
        if (implementsPackage.equalsIgnoreCase("org.jbundle.model.base.db"))
            implementsPackage = "org.jbundle.model.db";
    // Now, write the field resources (descriptions)
        String strBaseClass = recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).getString();
        if ((strBaseClass.equalsIgnoreCase("Record")) || (strBaseClass.equalsIgnoreCase("QueryRecord")))
            strBaseClass = "FieldList";
        this.writeHeading(strClassName, strPackage, ClassProject.CodeType.THIN);
        this.writeIncludes(CodeType.THIN);

        m_StreamOut.writeit("import java.util.*;\n");
        m_StreamOut.writeit("import " + DBConstants.ROOT_PACKAGE + "thin.base.util.*;\n\n");
        m_StreamOut.writeit("import " + DBConstants.ROOT_PACKAGE + "thin.base.db.*;\n\n");

        if (!strBaseClass.equals("FieldList"))
        {
            this.readRecordClass(strBaseClass);     // Return the record to the original position
            ClassProject classProject = (ClassProject)((ReferenceField)recClassInfo.getField(ClassInfo.CLASS_PROJECT_ID)).getReference();
            String baseClassPackage = classProject.getFullPackage(CodeType.THIN, recClassInfo.getField(ClassInfo.CLASS_PACKAGE).toString());
            if (baseClassPackage.equalsIgnoreCase("org.jbundle.thin.base.db"))
            {
                baseClassPackage = null;
                strBaseClass = "FieldList"; // Only valid thin base field in this package
            }
            if (baseClassPackage != null)
                m_StreamOut.writeit("import " + baseClassPackage + ".*;\n");            
        }
        m_StreamOut.writeit("import " + implementsPackage + ".*;\n");
        
        m_StreamOut.writeit("\n");
        m_StreamOut.writeit("public class " + strClassName + " extends " + strBaseClass + "\n");
        m_StreamOut.setTabs(+1);
        String implementsClass = strClassName + "Model";
        m_StreamOut.writeit("implements " + implementsClass + "\n");
        m_StreamOut.setTabs(-1);
        m_StreamOut.writeit("{\n");
        m_StreamOut.setTabs(+1);
        m_StreamOut.writeit("private static final long serialVersionUID = 1L;\n");

        this.readRecordClass(strClassName);     // Return the record to the original position
        FieldIterator fieldIterator = new FieldIterator(recFileHdr, recClassInfo, recFieldData, false);
        this.writeFieldOffsets(fieldIterator, CodeType.THIN);
        this.writeClassFields(CodeType.THIN);    // Write the thin class fields.
        
        m_StreamOut.writeit("\n");

        m_StreamOut.writeit("public " + strClassName + "()\n");
        m_StreamOut.writeit("{\n");
        m_StreamOut.setTabs(+1);
        m_StreamOut.writeit("super();\n");
        m_StreamOut.setTabs(-1);
        m_StreamOut.writeit("}\n");
        m_StreamOut.writeit("public " + strClassName + "(Object recordOwner)\n");
        m_StreamOut.writeit("{\n");
        m_StreamOut.setTabs(+1);
        m_StreamOut.writeit("this();\n");
        m_StreamOut.writeit("this.init(recordOwner);\n");
        m_StreamOut.setTabs(-1);
        m_StreamOut.writeit("}\n");

        this.writeFields(strClassName, strDatabaseName, strDBType, recClassInfo, recFileHdr, recFieldData, fieldIterator);
        
        m_MethodNameList.removeAllElements();
        this.writeClassMethods(CodeType.THIN);
        
        m_StreamOut.setTabs(-1);
        this.writeEndCode(true);
        recFieldData.close();

        this.readRecordClass(strClassName);     // Return the record to the original position
    }
    /**
     * 
     * @param strClassName
     * @param strDatabaseName
     * @param strDBType
     * @param recClassInfo
     * @param recFileHdr
     * @param recFieldData
     * @param fieldIterator
     */
    public void writeFields(String strClassName, String strDatabaseName, String strDBType, ClassInfo recClassInfo, Record recFileHdr, FieldData recFieldData, FieldIterator fieldIterator)
    {
        FieldStuff fieldStuff = new FieldStuff();
        String dBFileName = recFileHdr.getField(FileHdr.FILE_MAIN_FILENAME).getString();
        if (dBFileName.length() != 0)
        {
            dBFileName = this.fixSQLName(dBFileName);
            if (dBFileName.equalsIgnoreCase("NONE"))
                dBFileName = null;
        }
        else if (recFileHdr.getField(FileHdr.TYPE).toString().indexOf("MAPPED") == -1)
            dBFileName = strClassName;  // Default file name (unless mapped)

        if ((dBFileName != null) && (dBFileName.length() > 0))
        {
            m_StreamOut.writeit("public static final String " + this.convertNameToConstant(strClassName) + "_FILE = \"" + dBFileName + "\";\n");
            m_StreamOut.writeit("/**\n");
            m_StreamOut.writeit(" *\tGet the table name.\n");
            m_StreamOut.writeit(" */\n");
            m_StreamOut.writeit("public String getTableNames(boolean bAddQuotes)\n");
            m_StreamOut.writeit("{\n");
            m_StreamOut.writeit("\treturn (m_tableName == null) ? " + strClassName + "." + this.convertNameToConstant(strClassName) + "_FILE : super.getTableNames(bAddQuotes);\n");
            m_StreamOut.writeit("}\n");
        }
        if ((strDatabaseName != null) && (strDatabaseName.length() > 0))
        {
            m_StreamOut.writeit("/**\n");
            m_StreamOut.writeit(" *\tGet the Database Name.\n");
            m_StreamOut.writeit(" */\n");
            m_StreamOut.writeit("public String getDatabaseName()\n");
            m_StreamOut.writeit("{\n");
            m_StreamOut.writeit("\treturn \"" + strDatabaseName + "\";\n");
            m_StreamOut.writeit("}\n");
        }
        
        m_StreamOut.writeit("/**\n");
        m_StreamOut.writeit(" *\tIs this a local (vs remote) file?.\n");
        m_StreamOut.writeit(" */\n");
        m_StreamOut.writeit("public int getDatabaseType()\n");
        m_StreamOut.writeit("{\n");
        m_StreamOut.writeit("\treturn " + strDBType + ";\n");
        m_StreamOut.writeit("}\n");

        boolean bAutoCounterField = false;
        m_StreamOut.writeit("/**\n");
        m_StreamOut.writeit("* Set up the screen input fields.\n");
        m_StreamOut.writeit("*/\n");
        m_StreamOut.writeit("public void setupFields()\n");
        m_StreamOut.writeit("{\n");
        m_StreamOut.setTabs(+1);
        m_StreamOut.writeit("FieldInfo field = null;\n");

        fieldIterator.close();
        while (fieldIterator.hasNext())
        {
            fieldIterator.next();
            String strPre = DBConstants.BLANK;

            this.getFieldData(fieldStuff, true);

            if (("CounterField".equals(fieldStuff.strFieldClass + "\n"))
                || ("CounterField".equals(fieldStuff.strBaseFieldClass)))
                    bAutoCounterField = true;

            if (recFieldData.getField(FieldData.FIELD_TYPE).getString().equalsIgnoreCase(FieldTypeField.VIRTUAL_FIELD))
                strPre = "//";  // Skip virtual fields (EJB is not expecting them to be sent to the server)
            if (!fieldStuff.strFieldDesc.equals("null"))
                fieldStuff.strFieldDesc = "\"" + fieldStuff.strFieldDesc + "\"";
            fieldStuff.strFieldClass = "FieldInfo";
            fieldStuff.strFieldDesc = "null";   // Use the resource file!
            if (fieldStuff.strFieldLength.length() > 10)
            { // Default field length - translate to an actual field length
                if (fieldStuff.strBaseFieldClass != null)
                {
                    if ("BooleanField".equalsIgnoreCase(fieldStuff.strBaseFieldClass))
                        fieldStuff.strFieldLength = "10";
                    if ("CurrencyField".equalsIgnoreCase(fieldStuff.strBaseFieldClass))
                        fieldStuff.strFieldLength = Integer.toString(CurrencyField.DOUBLE_DEFAULT_LENGTH);
                    if ("DoubleField".equalsIgnoreCase(fieldStuff.strBaseFieldClass))
                        fieldStuff.strFieldLength = Integer.toString(DoubleField.DOUBLE_DEFAULT_LENGTH);
                    if ("FloatField".equalsIgnoreCase(fieldStuff.strBaseFieldClass))
                        fieldStuff.strFieldLength = Integer.toString(FloatField.FLOAT_DEFAULT_LENGTH);
                    if ("RealField".equalsIgnoreCase(fieldStuff.strBaseFieldClass))
                        fieldStuff.strFieldLength = Integer.toString(RealField.REAL_DEFAULT_LENGTH);
                    if ("IntegerField".equalsIgnoreCase(fieldStuff.strBaseFieldClass))
                        fieldStuff.strFieldLength = Integer.toString(IntegerField.INTEGER_DEFAULT_LENGTH);
                    if ("ShortField".equalsIgnoreCase(fieldStuff.strBaseFieldClass))
                        fieldStuff.strFieldLength = Integer.toString(ShortField.SHORT_DEFAULT_LENGTH);
                    if ("MemoField".equalsIgnoreCase(fieldStuff.strBaseFieldClass))
                        fieldStuff.strFieldLength = Integer.toString(MemoField.BIG_DEFAULT_LENGTH);
                    if ("DateField".equalsIgnoreCase(fieldStuff.strBaseFieldClass))
                        fieldStuff.strFieldLength = Integer.toString(DateField.DATE_DEFAULT_LENGTH);
                    if ("DateTimeField".equalsIgnoreCase(fieldStuff.strBaseFieldClass))
                        fieldStuff.strFieldLength = Integer.toString(DateTimeField.DATETIME_DEFAULT_LENGTH);
                    if ("TimeField".equalsIgnoreCase(fieldStuff.strBaseFieldClass))
                        fieldStuff.strFieldLength = Integer.toString(TimeField.TIME_DEFAULT_LENGTH);
                }
            }

            m_StreamOut.writeit(strPre + "field = new " + fieldStuff.strFieldClass + "(this, " + this.convertNameToConstant(fieldStuff.strFileFieldName) + ", " + fieldStuff.strFieldLength + ", " + fieldStuff.strFieldDesc + ", " + fieldStuff.strDefaultField + ");\n");

            if (fieldStuff.strDataClass.equals("Percent"))
                fieldStuff.strDataClass = "Float";
            if ((fieldStuff.strDataClass.equals("Currency")) || (fieldStuff.strDataClass.equals("Real")))
                fieldStuff.strDataClass = "Double";
            if ((fieldStuff.strDataClass == null) || (fieldStuff.strDataClass.length() == 0))
                fieldStuff.strDataClass = "String";                     
            if (fieldStuff.strDataClass.equals("String"))
                fieldStuff.strDataClass = null;
            if ("ImageField".equals(fieldStuff.strBaseFieldClass))
                fieldStuff.strDataClass = "Object";   //"javax.swing.ImageIcon";
            if ((fieldStuff.strDataClass != null) && (fieldStuff.strDataClass.length() > 0))
                m_StreamOut.writeit(strPre + "field.setDataClass(" + fieldStuff.strDataClass + ".class);\n");
            
            if ("RealField".equals(fieldStuff.strBaseFieldClass))
                m_StreamOut.writeit(strPre + "field.setScale(-1);\n");
            if ("DateField".equals(fieldStuff.strBaseFieldClass))
                m_StreamOut.writeit(strPre + "field.setScale(Constants.DATE_ONLY);\n");
            if ("TimeField".equals(fieldStuff.strBaseFieldClass))
                m_StreamOut.writeit(strPre + "field.setScale(Constants.TIME_ONLY);\n");
            if (fieldStuff.bHidden == true)
                m_StreamOut.writeit(strPre + "field.setHidden(true);\n");
        }

        m_StreamOut.setTabs(-1);
        m_StreamOut.writeit("}\n");

        this.writeKeyOffsets(CodeType.THIN);   // Write the Key offsets
        this.writeSetupKey(CodeType.THIN, bAutoCounterField);
    }

    /**
     *  Create the Record Class for this Record.
     */
    public void writeRecordClassDetail(String strRecordClass)
    {
        ClassInfo recClassInfo = (ClassInfo)this.getMainRecord();
        try   {
            this.readRecordClass(strRecordClass);
            if (m_MethodNameList.size() != 0)
                m_MethodNameList.removeAllElements();
            this.writeClassInterface();
        
            m_StreamOut.writeit("private static final long serialVersionUID = 1L;\n");

            Record recFieldData = this.getRecord(FieldData.FIELD_DATA_FILE);
            Record recFileHdr = this.getRecord(FileHdr.FILE_HDR_FILE);
            FieldIterator fieldIterator = new FieldIterator(recFileHdr, recClassInfo, recFieldData, true);
        	this.writeFieldOffsets(fieldIterator, CodeType.THICK); // Write the BaseField offsets
            this.writeKeyOffsets(CodeType.THICK);   // Write the Key offsets
        
            this.writeClassFields(CodeType.THICK);
            this.writeDefaultConstructor(strRecordClass);
            
            this.writeRecordInit();
            this.writeInit();   // Special case... zero all class fields!
        
            recFileHdr.getField(FileHdr.FILE_NAME).setString(strRecordClass);
            recFileHdr.setKeyArea(FileHdr.FILE_NAME_KEY);
            boolean bFileType = (recFileHdr.seek("="));
            if (bFileType) // Is there a file with this name?
            {
                String dBFileName = recFileHdr.getField(FileHdr.FILE_MAIN_FILENAME).getString();
                if (dBFileName.length() != 0)
                {
                    dBFileName = this.fixSQLName(dBFileName);
                    if (dBFileName.equalsIgnoreCase("NONE"))
                        dBFileName = null;
                }
                else if (recFileHdr.getField(FileHdr.TYPE).toString().indexOf("MAPPED") == -1)
                    dBFileName = strRecordClass;  // Default file name (unless mapped)

                //m_StreamOut.writeit("\npublic static final String k" + strRecordClass + "File = \"" + dBFileName + "\";\n");
                if (!this.readThisMethod("getTableNames"))
                {
                	this.writeMethodInterface(null, "getTableNames", "String", "boolean bAddQuotes", "", "Get the table name.", null);
                	m_StreamOut.writeit("\treturn (m_tableName == null) ? Record.formatTableNames(" + this.convertNameToConstant(strRecordClass + "File") + ", bAddQuotes) : super.getTableNames(bAddQuotes);\n}\n");
                }

                String recordName = recFileHdr.getField(FileHdr.FILE_REC_CALLED).getString();    // Description of a record
                String databaseName = recFileHdr.getField(FileHdr.DATABASE_NAME).getString(); // Database name
                String strDBType = recFileHdr.getField(FileHdr.TYPE).getString(); // Is Remote file?
                if (recordName.length() != 0)   // Valid Name
                {
                    if (!this.readThisMethod("getRecordName"))
                    {
                    	this.writeMethodInterface(null, "getRecordName", "String", "void", "", "Get the name of a single record.", null);
                    	m_StreamOut.writeit("\treturn \"" + recordName + "\";\n}\n");
                    }
                }
                if (databaseName.length() > 0)
                {   // If you don't supply a database name, you better supply your own method (See DatabaseInfo).
                    if (!this.readThisMethod("getDatabaseName"))
                    {
                    	this.writeMethodInterface(null, "getDatabaseName", "String", "void", "", "Get the Database Name.", null);
                    	m_StreamOut.writeit("\treturn \"" + databaseName + "\";\n}\n");
                    }
                }
                if (strDBType.length() > 0)
                {
                    if (!this.readThisMethod("getDatabaseType"))
                    {
                    	this.writeMethodInterface(null, "getDatabaseType", "int", "void", "", "Is this a local (vs remote) file?", null);
                    	strDBType = this.fixDBType(strDBType, "DBConstants.");
                    	m_StreamOut.writeit("\treturn " + strDBType + ";\n}\n");
                    }
                }
                this.writeFileMakeViews();
            }
            else
                if (recClassInfo.getField(ClassInfo.CLASS_TYPE).getString().equalsIgnoreCase("Record"))
                    m_StreamOut.writeit("\npublic static final String " + this.convertNameToConstant(strRecordClass + "File") + " = null;\t// Screen field\n");    // Screen fields
                    

            this.writeSetupField();
            this.readRecordClass(strRecordClass); // Must re-read this record class

            if (bFileType) // Is there a file with this name?
                this.writeSetupKey(CodeType.THICK, true);
            this.writeClassMethods(CodeType.THICK);       // Write the remaining methods
            this.writeEndCode(true);
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     *  Write the constants for the field offsets
     * @param codeType
     */
    public void writeFieldOffsets(FieldIterator fieldIterator, CodeType codeType)
    { // Now, write all the field offsets out in the header file
        ClassInfo recClassInfo = (ClassInfo)this.getMainRecord();
        boolean firstTime = true;
        String strBaseFieldName;
        String strFieldName = "MainField";     // In case there are no fields
        Record recFieldData = this.getRecord(FieldData.FIELD_DATA_FILE);

        boolean alwaysInclude = false;
        boolean alwaysExclude = false;
        if (recClassInfo.isARecord(false))
        {   // For record, all the fields are in the model class
            if (codeType == CodeType.INTERFACE)
                alwaysInclude = true;
            else
                alwaysExclude = true;
        }
        else
        {   // screen record are thick only - no model
            if (codeType == CodeType.THICK)
                alwaysInclude = true;
            else
                alwaysExclude = true;
        }
        for (int pass = 1; pass <= 2; ++pass) // Do this two times (two passes)
        {
            fieldIterator.close();
            while (fieldIterator.hasNext())
            {
                fieldIterator.next();
                strFieldName = recFieldData.getField(FieldData.FIELD_NAME).getString();
                strBaseFieldName = recFieldData.getField(FieldData.BASE_FIELD_NAME).getString();
                String type = "String";
                switch (pass)
                {
                case 1:     // First, add all fields based on the super.New8 class
                    if (strBaseFieldName.length() > 0)
                    {
                        firstTime = false;
                        String value = convertNameToConstant(strBaseFieldName);
                        String strPre = DBConstants.BLANK;
                        if (recFieldData.getField(FieldData.FIELD_NAME).getString().equals(strBaseFieldName))
                            strPre = "//";
                        strFieldName = convertNameToConstant(strFieldName);
                        boolean includeThis = ((IncludeScopeField)recFieldData.getField(FieldData.INCLUDE_SCOPE)).includeThis(codeType, true);
                        if (alwaysInclude)
                            includeThis = true;
                        if (alwaysExclude)
                            includeThis = false;
                        if (includeThis)
                            m_StreamOut.writeit("\n" + strPre + "public static final " + type + " " + strFieldName + " = " + value + ";");
                    }
                    break;
                case 2:     // Now, add all fields new to this class
                    if (strBaseFieldName.length() == 0)
                    {
                        firstTime = false;
                        boolean includeThis = ((IncludeScopeField)recFieldData.getField(FieldData.INCLUDE_SCOPE)).includeThis(codeType, true);
                        if (alwaysInclude)
                            includeThis = true;
                        if (alwaysExclude)
                            includeThis = false;
                        if (Rec.ID.equals(strFieldName))
                            includeThis = false;    // HACK
                        if (includeThis)
                            m_StreamOut.writeit("\n" + "public static final String " + this.convertNameToConstant(strFieldName) + " = \"" + strFieldName + "\";");
                        break;
                    }
                }
            }
            recFieldData.close();
            if (pass == 2)
            {
                if (!firstTime)
                    m_StreamOut.writeit("\n");
            }
        } // End of pass loop
        
    }
    /**
     * Is this key already included in a base record?
     * @return
     */
    public boolean keyInBase(Record recClassInfo, Record recKeyInfo)
    {
        try {
            if (recClassInfo2 == null)
                recClassInfo2 = new ClassInfo(this);
            if (recKeyInfo2 == null)
            {
                recKeyInfo2 = new KeyInfo(this);
                recKeyInfo2.setKeyArea(KeyInfo.KEY_FILENAME_KEY);
                SubFileFilter keyBehavior = new SubFileFilter(recClassInfo2.getField(ClassInfo.CLASS_NAME), KeyInfo.KEY_FILENAME, null, null, null, null);
                recKeyInfo2.addListener(keyBehavior);
            }

            String baseClass = recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).toString();
            while ((baseClass != null) && (baseClass.length() > 0) && (!"FieldList".equalsIgnoreCase(baseClass)))
            {
                recClassInfo2.getField(ClassInfo.CLASS_NAME).setString(baseClass);
                recClassInfo2.setKeyArea(ClassInfo.CLASS_NAME_KEY);
                if (!recClassInfo2.seek("="))   // Get this class record back
                    break;
                recKeyInfo2.close();
                while (recKeyInfo2.hasNext())
                {
                    recKeyInfo2.next();
                    String keyName = recKeyInfo.getField(KeyInfo.KEY_NAME).toString();
                    if ((keyName == null) || (keyName.length() == 0))
                        keyName = recKeyInfo.getField(KeyInfo.KEY_FIELD_1).toString();
                    String keyName2 = recKeyInfo2.getField(KeyInfo.KEY_NAME).toString();
                    if ((keyName2 == null) || (keyName2.length() == 0))
                        keyName2 = recKeyInfo2.getField(KeyInfo.KEY_FIELD_1).toString();
                    if (keyName2.equals(keyName))
                        return true;    // Match!
                }
                baseClass = recClassInfo2.getField(ClassInfo.BASE_CLASS_NAME).toString();
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
        return false;    // Not in base 
    }
    /**
     *  Initialize the class
     */
    public void writeRecordInit()
    {
        Record recClassInfo = this.getMainRecord();
        String strClassName = recClassInfo.getField(ClassInfo.CLASS_NAME).getString();
        this.readThisMethod(strClassName);
        this.writeThisMethod(CodeType.THICK);
    }
    /**
     *  Write the TableDoc code to DoMakeViews.
     */
    public void writeFileMakeViews()
    {
        if (this.readThisMethod("makeScreen"))
        {
            this.writeThisMethod(CodeType.THICK);
            return;     // if no views specified, use default methods
        }
        
        if (true)
        {
            ClassInfo recClassInfo = (ClassInfo)this.getMainRecord();
            ClassFields recClassFields = new ClassFields(this);
            recClassFields.addListener(new SubFileFilter(ClassFields.CLASS_INFO_CLASS_NAME_KEY, recClassInfo.getField(ClassInfo.CLASS_NAME)));   // Only read through the class fields
            recClassFields.close();
            try {
                boolean firstTime = true;
                while (recClassFields.hasNext())
                {
                    recClassFields.next();
                    String strClassFieldType = recClassFields.getField(ClassFields.CLASS_FIELDS_TYPE).toString();
                    if (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.SCREEN_CLASS_NAME))
                    {
                        if (firstTime)
                        {
                            this.writeMethodInterface(null, "makeScreen", "ScreenParent", "ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties", "", "Make a default screen.", null);
                            m_StreamOut.writeit("\tScreenParent screen = null;\n");
                        }
                        String screenMode = recClassFields.getField(ClassFields.CLASS_FIELD_INITIAL_VALUE).toString();
                        if ((screenMode == null) || (screenMode.length() == 0))
                            screenMode = "MAINT_MODE";
                        if (!screenMode.contains("."))
                            screenMode = "ScreenConstants." + screenMode;
                        String fieldName = recClassFields.getField(ClassFields.CLASS_FIELD_NAME).toString();
                        if (firstTime)
                            m_StreamOut.writeit("\t");
                        else
                            m_StreamOut.writeit("\telse ");
                        m_StreamOut.write("if ((iDocMode & " + screenMode + ") == " + screenMode + ")\n");
                        m_StreamOut.writeit("\t\tscreen = Record.makeNewScreen(" + fieldName + ", itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);\n");
                        firstTime = false;
                    }
                }
                if (!firstTime)
                {
                    m_StreamOut.writeit("\telse\n");
                    m_StreamOut.writeit("\t\tscreen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);\n");
                    m_StreamOut.writeit("\treturn screen;\n}\n");                    
                }
            } catch (DBException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 
     * @param recFieldData
     * @return
     */
    public boolean ignoreFieldData(FieldData recFieldData)
    {
        if (recFieldData.getField(FieldData.kID).isNull())
            return true;
        if (recFieldData.getField(FieldData.FIELD_NAME).getString().equals(recFieldData.getField(FieldData.BASE_FIELD_NAME).getString()))
        if (recFieldData.getField(FieldData.FIELD_CLASS).getLength() == 0)
        if (recFieldData.getField(FieldData.MAXIMUM_LENGTH).getLength() == 0)
        if (recFieldData.getField(FieldData.FIELD_DESC_VERTICAL).getLength() == 0)
        if (recFieldData.getField(FieldData.DEFAULT_VALUE).getLength() == 0)
        if (recFieldData.getField(FieldData.INITIAL_VALUE).getLength() == 0)
            return true;
        return false;
    }
    /**
     *  WriteSetupField.
     *  WARNING: This method changes the Class Info File Position
     */
    public void writeSetupField()
    {
        FieldStuff fieldStuff = new FieldStuff();
        ClassInfo recClassInfo = (ClassInfo)this.getMainRecord();
    // Now, write all the record methods
        Record recFileHdr = this.getRecord(FileHdr.FILE_HDR_FILE);          // Open the Agency File
        FieldData recFieldData = (FieldData)this.getRecord(FieldData.FIELD_DATA_FILE);          // Open the Agency File
        FieldIterator fieldIterator = new FieldIterator(recFileHdr, recClassInfo, recFieldData, true);
        if (!fieldIterator.hasNext())
            return;

    // Initialization method
        this.writeMethodInterface(null, "setupField", "BaseField", "int iFieldSeq", "", "Add this field in the Record's field sequence.", null);
        m_StreamOut.setTabs(+1);
        m_StreamOut.writeit("BaseField field = null;\n");

        int count = 0;
        while (fieldIterator.hasNext())
        {
            recFieldData = (FieldData)fieldIterator.next();
            String strPre = DBConstants.BLANK;
            if (this.ignoreFieldData(recFieldData))
                strPre = "//";

            this.getFieldData(fieldStuff, false);

            if ((recFieldData.getField(FieldData.FIELD_TYPE).getString().equalsIgnoreCase(FieldTypeField.VIRTUAL_FIELD)) || (fieldStuff.bNotNullField))
                strPre = DBConstants.BLANK;
            m_StreamOut.writeit(strPre + "if (iFieldSeq == " + count++ + ")\n");
            String strFieldType = recFieldData.getField(FieldData.FIELD_TYPE).getString();
            String strDefaultValue = recFieldData.getField(FieldData.DEFAULT_VALUE).getString();
            String strMinimumLength = recFieldData.getField(FieldData.MINIMUM_LENGTH).getString();
            boolean bHidden = recFieldData.getField(FieldData.HIDDEN).getState();
            if (strMinimumLength.equals("0"))
                strMinimumLength = "";
            if ((strFieldType.equalsIgnoreCase(FieldTypeField.VIRTUAL_FIELD)) || (fieldStuff.bNotNullField) || (strDefaultValue.equalsIgnoreCase("old")) || (strMinimumLength.length() > 0) || bHidden)   // Virtual BaseField?
                m_StreamOut.writeit(strPre + "{\n");
            if (!fieldStuff.strFieldDesc.equals("null"))
                fieldStuff.strFieldDesc = "\"" + fieldStuff.strFieldDesc + "\"";
            fieldStuff.strFieldDesc = "null";   // Use the resource file!
            if ((fieldStuff.strFieldClass == null) || (fieldStuff.strFieldClass.length() == 0))
                fieldStuff.strFieldClass = "StringField";

            m_StreamOut.writeit(strPre + "\tfield = new " + fieldStuff.strFieldClass + "(this, " + this.convertNameToConstant(fieldStuff.strFileFieldName) + ", " + fieldStuff.strFieldLength + ", " + fieldStuff.strFieldDesc + ", " + fieldStuff.strDefaultField + ");\n");

            if (strFieldType.equalsIgnoreCase(FieldTypeField.VIRTUAL_FIELD))     // Virtual BaseField?
                m_StreamOut.writeit(strPre + "\tfield.setVirtual(true);\n");
            if (bHidden)     // Virtual BaseField?
                m_StreamOut.writeit(strPre + "\tfield.setHidden(true);\n");
            if (fieldStuff.bNotNullField)
                m_StreamOut.writeit(strPre + "\tfield.setNullable(false);\n");
            if (strDefaultValue.equalsIgnoreCase("old"))    // Virtual BaseField?
                m_StreamOut.writeit(strPre + "\tfield.addListener(new InitOnceFieldHandler(null));\n");
            if (strMinimumLength.length() > 0)  // Virtual BaseField?
                m_StreamOut.writeit(strPre + "\tfield.setMinimumLength(" + strMinimumLength + ");\n");
            if ((strFieldType.equalsIgnoreCase(FieldTypeField.VIRTUAL_FIELD)) || (fieldStuff.bNotNullField) || (strDefaultValue.equalsIgnoreCase("old")) || (strMinimumLength.length() > 0) || bHidden)   // Virtual BaseField?
                m_StreamOut.writeit(strPre + "}\n");
        }
        m_StreamOut.writeit("if (field == null)\n");
        m_StreamOut.setTabs(+1);
        m_StreamOut.writeit("field = super.setupField(iFieldSeq);\n");
        m_StreamOut.setTabs(-1);
        m_StreamOut.writeit("return field;\n");
        m_StreamOut.setTabs(-1);
        m_StreamOut.writeit("}\n");
        fieldIterator.free();
    }
    /**
     *  Create the Record Class for this Record
     */
    public void writeKeyOffsets(CodeType codeType)
    { // Now, write all the key offsets out
        Record recClassInfo = this.getMainRecord();
        try   {
            //String previousKey;
            String strKeyName = "";
            String strRecordClass;
            String uniqueKeyFields[] = new String[10];
            String uniqueKeySeq[] = new String[10];
            int uniqueKeyCount = 0;
            strRecordClass = recClassInfo.getField(ClassInfo.CLASS_NAME).getString();
            
            Record recKeyInfo = this.getRecord(KeyInfo.KEY_INFO_FILE);
            recKeyInfo.close();
            int count = 0;
            while (recKeyInfo.hasNext())
            {
                recKeyInfo.next();
                count++;
                strKeyName = recKeyInfo.getField(KeyInfo.KEY_NAME).getString();
                if (strKeyName.length() == 0)
                    strKeyName = recKeyInfo.getField(KeyInfo.KEY_FIELD_1).getString();
                if (strKeyName.equalsIgnoreCase(DBConstants.PRIMARY_KEY))
                    strKeyName = strRecordClass + "Primary";    // To avoid re-defining kPrimaryKey

                if (!((IncludeScopeField)recKeyInfo.getField(KeyInfo.INCLUDE_SCOPE)).includeThis(codeType, true))
                    continue;
                if (keyInBase(recClassInfo, recKeyInfo))
                    continue;
                if (!strKeyName.equalsIgnoreCase(DBConstants.PRIMARY_KEY))
                    if (count == 1)
                        if (recKeyInfo.getField(KeyInfo.KEY_NUMBER).isNull())
                            count++;   // Special case, no sequence, first key is not the primary key... first key must be primary key
                if (count > 1)
                    m_StreamOut.writeit("\npublic static final String " + this.convertNameToConstant(strKeyName + "Key") + " = \"" + strKeyName +"\";\n");

                    // This logic here checks to see if this is a unique key with one field, if yes adds field to array
                String keyTypeStr, strKeyFieldName;
                keyTypeStr = recKeyInfo.getField(KeyInfo.KEY_TYPE).getString();
                if (keyTypeStr.length() == 0)
                    keyTypeStr = KeyTypeField.UNIQUE; // Unique key if no selection
                strKeyFieldName = recKeyInfo.getField(KeyInfo.KEY_FIELD_2).getString();
                if (((keyTypeStr.charAt(0) == KeyTypeField.UNIQUE.charAt(0)) || (keyTypeStr.charAt(0) == 'Y')) && (strKeyFieldName.length() == 0))
                    { // Unique key with one field, default logic... always try to read
                    strKeyFieldName = recKeyInfo.getField(KeyInfo.KEY_FIELD_1).getString();
                    uniqueKeyFields[uniqueKeyCount] = strKeyFieldName;
                    uniqueKeySeq[uniqueKeyCount] = strKeyName;
                    uniqueKeyCount++;
                }
            }
            recKeyInfo.close();
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     *  WriteSetupKey.
     */
    public void writeSetupKey(CodeType codeType, boolean bAutoCounterField)
    { // Now, write the key setup method
        Record recClassInfo = this.getMainRecord();
        try   {
            String strKeyName = DBConstants.BLANK;
            String strRecordClass = recClassInfo.getField(ClassInfo.CLASS_NAME).getString();
            
            Record recKeyInfo = this.getRecord(KeyInfo.KEY_INFO_FILE);
            recKeyInfo.close();
            if (codeType == CodeType.THICK)
                if (!recKeyInfo.hasNext())
            {
                recKeyInfo.close();
                return;
            }
        
            if (codeType == CodeType.THICK)
            {
                this.writeMethodInterface(null, "setupKey", "KeyArea", "int iKeyArea", "", "Add this key area description to the Record.", null);
                m_StreamOut.setTabs(+1);
                m_StreamOut.writeit("KeyArea keyArea = null;\n");
            }
            else
            {  // Thin
                // Now write out the setupkey method.
                m_StreamOut.writeit("/**\n");
                m_StreamOut.writeit("* Set up the key areas.\n");
                m_StreamOut.writeit("*/\n");
                m_StreamOut.writeit("public void setupKeys()\n");
                m_StreamOut.writeit("{\n");
                m_StreamOut.setTabs(+1);
                m_StreamOut.writeit("KeyAreaInfo keyArea = null;\n");
            }
        
            int count = 0;
            recKeyInfo.close();
            while (recKeyInfo.hasNext())
            {
                recKeyInfo.next();
                String finalKeyName = "";
                strKeyName = recKeyInfo.getField(KeyInfo.KEY_NAME).getString();
                if (strKeyName.length() == 0)
                {
                    strKeyName = recKeyInfo.getField(KeyInfo.KEY_FIELD_1).getString();
                    if (count == 0)
                        finalKeyName = DBConstants.PRIMARY_KEY;
                    else
                    {   // No key name, use the field name!
                        FieldData recFieldData = (FieldData)this.getRecord(FieldData.FIELD_DATA_FILE);
                        recFieldData.initRecord(false);
                        String strFieldName, strBaseFieldName;
                        strFieldName = strKeyName;
                        strBaseFieldName = "";
                        m_BasedFieldHelper.getBasedField(recFieldData, strRecordClass, strFieldName, strBaseFieldName);  // Get the field this is based on
                        finalKeyName = recFieldData.getField(FieldData.FIELD_NAME).getString();
                    }
                }

                String strKeyType = recKeyInfo.getField(KeyInfo.KEY_TYPE).getString();
                finalKeyName = this.fixSQLName(finalKeyName);
                String strUnique = "UNIQUE";
                if (strKeyType.equalsIgnoreCase(KeyTypeField.UNIQUE))
                    strUnique = "UNIQUE";
                else if (strKeyType.equalsIgnoreCase(KeyTypeField.NOT_UNIQUE))
                    strUnique = "NOT_UNIQUE";
                else if (strKeyType.equalsIgnoreCase(KeyTypeField.SECONDARY))
                    strUnique = "SECONDARY_KEY";
                else
                {   // Not specified.
                    if (strKeyName.equalsIgnoreCase(DBConstants.PRIMARY_KEY))
                        strUnique = "UNIQUE";
                    else
                        strUnique = "NOT_UNIQUE";
                }

                if (finalKeyName.length() == 0)
                    finalKeyName = strKeyName;
                if (strKeyName.equalsIgnoreCase(DBConstants.PRIMARY_KEY))
                    strKeyName = strRecordClass + "Primary";    // To avoid re-defining kPrimaryKey
                else
                {
                    if (count == 0)
                        if (recKeyInfo.getField(KeyInfo.KEY_NUMBER).isNull())
                        {   // Special case, no sequence, first key is not the primary key... first key must be primary key
                            recKeyInfo.addNew();
                            strUnique = "UNIQUE";
                            recKeyInfo.getField(KeyInfo.KEY_FIELD_1).setString(DBConstants.PRIMARY_KEY);
                            recKeyInfo.close();
                        }
                }
                if (codeType == CodeType.THICK)
                {
                    m_StreamOut.writeit("if (iKeyArea == " + count + ")\n");
                    m_StreamOut.writeit("{\n");
                    m_StreamOut.setTabs(+1);
                }
                count++;
                if (codeType == CodeType.THICK)
                    m_StreamOut.writeit("keyArea = this.makeIndex(DBConstants." + strUnique + ", " + this.convertNameToConstant(finalKeyName + "Key") + ");\n");
                else    // Thin
                    m_StreamOut.writeit("keyArea = new KeyAreaInfo(this, Constants." + strUnique + ", " + this.convertNameToConstant(finalKeyName + "Key") + ");\n");                    

                boolean bAscending = true;
                if (recKeyInfo.getField(KeyInfo.KEY_FIELD_9).getString().equalsIgnoreCase("D"))
                    bAscending = false;     // *Fix this to allow Ascending/Descending on each key area!*
                for (int i = recKeyInfo.getFieldSeq(KeyInfo.KEY_FIELD_1); i <= recKeyInfo.getFieldSeq(KeyInfo.KEY_FIELD_9); i++)
                {
                    String strKeyFieldName = recKeyInfo.getField(i).getString();
                    if (strKeyFieldName.length() == 0)
                        break;
                    String strAscending = "ASCENDING";
                    if (!bAscending)
                        strAscending = "DESCENDING";
                    if (codeType == CodeType.THICK)
                        m_StreamOut.writeit("keyArea.addKeyField(" + this.convertNameToConstant(strKeyFieldName) + ", DBConstants." + strAscending + ");\n");
                    else // Thin
                        m_StreamOut.writeit("keyArea.addKeyField(" + this.convertNameToConstant(strKeyFieldName) + ", Constants." + strAscending + ");\n");
                }
                if (codeType == CodeType.THICK)
                {
                    m_StreamOut.setTabs(-1);
                    m_StreamOut.writeit("}\n");
                }
            }
            recKeyInfo.close();
            if (codeType == CodeType.THICK)
            {
                m_StreamOut.writeit("if (keyArea == null)\n");
                m_StreamOut.setTabs(+1);
                m_StreamOut.writeit("keyArea = super.setupKey(iKeyArea);\t\t\n");
                m_StreamOut.setTabs(-1);
                m_StreamOut.writeit("return keyArea;\n");
                m_StreamOut.setTabs(-1);
                m_StreamOut.writeit("}\n");
            }
            else
            {  // Thin
                if (count == 0)
                    m_StreamOut.writeit("super.setupKeys();\n");
                
                m_StreamOut.setTabs(-1);
                m_StreamOut.writeit("}\n");
                
                if (!bAutoCounterField)
                {
                    m_StreamOut.writeit("/**\n");
                    m_StreamOut.writeit("* This is not an auto-counter record.\n");
                    m_StreamOut.writeit("*/\n");
                    m_StreamOut.writeit("public boolean isAutoSequence()\n");
                    m_StreamOut.writeit("{\n");
                    m_StreamOut.setTabs(+1);
                    m_StreamOut.writeit("return false;\n");
                    m_StreamOut.setTabs(-1);
                    m_StreamOut.writeit("}\n");
                }               
            }
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     *  Free the class.
     */
    public void writeFree()
    {
        Record recClassInfo = this.getMainRecord();
        ClassFields recClassFields = new ClassFields(this);
        try   {
            String strFieldName;
            String strFieldClass;
                //  Now, zero out all the class fields

            recClassFields.setKeyArea(ClassFields.CLASS_INFO_CLASS_NAME_KEY);
            SubFileFilter fileBehavior2 = new SubFileFilter(recClassInfo.getField(ClassInfo.CLASS_NAME), ClassFields.CLASS_INFO_CLASS_NAME, null, null, null, null);
            recClassFields.addListener(fileBehavior2);   // Only read through the class fields

            recClassFields.close();
            recClassFields.moveFirst();
        
            this.writeMethodInterface(null, "free", "void", "", "", "Release the objects bound to this record.", null);
            m_StreamOut.setTabs(+1);
            m_StreamOut.writeit("super.free();\n");
        
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
                    String strReference = "";
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
                    if (!strReference.equals("(none)"))
                        m_StreamOut.writeit("\t" + strFieldName + " = null;\n");
                }
            }
            recClassFields.close();
            m_StreamOut.setTabs(-1);
            m_StreamOut.writeit("}\n");
        } catch (DBException ex)   {
            ex.printStackTrace();
        } finally {
            if (recClassFields != null)
                recClassFields.free();
        }
    }
    /**
     *  Read the Class for this record
     */
    public void readRecordClass(String strRecordClass)
    {
        String strBaseClass;
        Record recClassInfo = this.getMainRecord();
        if (!this.readThisClass(strRecordClass))
            strBaseClass = "Record";
        else
        {
            strBaseClass = recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).getString();
            if (strBaseClass.equalsIgnoreCase("Record"))
                strBaseClass = "Record";
            if (strBaseClass.length() == 0)
                strBaseClass = "Record";
        }
        recClassInfo.getField(ClassInfo.BASE_CLASS_NAME).setString(strBaseClass);
    }
    /**
     *  Extract the actual Field Data from the record and put it in the FieldStuff object.
     */
    public void getFieldData(FieldStuff fieldStuff, boolean bThin)
    {
        FieldData recFieldData = (FieldData)this.getRecord(FieldData.FIELD_DATA_FILE);
        Record recClassInfo2 = this.readFieldClass();      // Get the field and class this is based on
        if (recClassInfo2 == null)
            fieldStuff.strFieldClass = recFieldData.getField(FieldData.FIELD_CLASS).getString();
        else
            fieldStuff.strFieldClass = recClassInfo2.getField(ClassInfo.CLASS_NAME).getString();
        fieldStuff.strFieldName = recFieldData.getField(FieldData.FIELD_NAME).getString();
        fieldStuff.strFieldLength = recFieldData.getField(FieldData.MAXIMUM_LENGTH).getString();
        if ((fieldStuff.strFieldLength.length() == 0) || (fieldStuff.strFieldLength.equalsIgnoreCase("0")))
            fieldStuff.strFieldLength = "Constants.DEFAULT_FIELD_LENGTH";
        else
            fieldStuff.strFieldLength = recFieldData.getField(FieldData.MAXIMUM_LENGTH).stripNonNumeric(fieldStuff.strFieldLength);
        fieldStuff.strFileFieldName = recFieldData.getField(FieldData.FIELD_NAME).getString();
        if (fieldStuff.strFileFieldName.length() == 0)
            fieldStuff.strFileFieldName = fieldStuff.strFieldName;
        fieldStuff.bNotNullField = recFieldData.getField(FieldData.FIELD_NOT_NULL).getState();
        fieldStuff.strFieldDesc = recFieldData.getField(FieldData.FIELD_DESC_VERTICAL).getString();
        fieldStuff.strFieldTip = recFieldData.getField(FieldData.FIELD_DESCRIPTION).getString();
        if (fieldStuff.strFieldDesc.length() == 0)
            fieldStuff.strFieldDesc = fieldStuff.strFieldTip;
        if (fieldStuff.strFieldDesc.length() == 0)
            fieldStuff.strFieldDesc = fieldStuff.strFileFieldName;
        if (fieldStuff.strFieldDesc.equalsIgnoreCase("b"))
            fieldStuff.strFieldDesc = "";
        if (fieldStuff.strFieldDesc.length() > 14)
            fieldStuff.strFieldDesc = fieldStuff.strFieldDesc.substring(0, 14);             // Max length of this string is 14
        fieldStuff.strFileFieldName = this.fixSQLName(fieldStuff.strFileFieldName);
        if ((fieldStuff.strFieldDesc.equals(fieldStuff.strFieldTip))
            || (fieldStuff.strFieldTip == null)
            || (fieldStuff.strFieldTip.length() == 0))
                fieldStuff.strFieldTip = null;  // Don't need a tip, if the tip and description are the same.
        if (fieldStuff.strFieldDesc.equals(fieldStuff.strFileFieldName))
            fieldStuff.strFieldDesc = "null"; // Default to field name fieldStuff.strFileFieldName;
        fieldStuff.strDataClass = recFieldData.getField(FieldData.DATA_CLASS).toString();
        fieldStuff.bHidden = recFieldData.getField(FieldData.HIDDEN).getState();
        this.getBaseDataClass(fieldStuff);

        fieldStuff.strDefaultField = this.getInitField(recFieldData, true, bThin);
        if ((fieldStuff.strDefaultField == null) || (fieldStuff.strDefaultField.length() == 0))
            fieldStuff.strDefaultField = "null";
        else if (fieldStuff.strDefaultField.charAt(0) != '\"')
        {
            if ((fieldStuff.strDataClass.equals("String")) || (fieldStuff.strDataClass.equals("Object")) || (fieldStuff.strDataClass == null))
                fieldStuff.strDefaultField = "\"" + fieldStuff.strDefaultField + "\"";
            else
            {
                if (fieldStuff.strDataClass.equals("Short"))
                    fieldStuff.strDefaultField = "(short)" + fieldStuff.strDefaultField;
                if (fieldStuff.strDataClass.equals("Currency"))
                    fieldStuff.strDataClass = "Double";
                if (!fieldStuff.strDefaultField.startsWith("new "))
                    fieldStuff.strDefaultField = "new " + fieldStuff.strDataClass + "(" + fieldStuff.strDefaultField + ")";
            }
        }
    }
    /**
     *  Read thru the classes until you get a Physical data class.
     */
    public void getBaseDataClass(FieldStuff fieldStuff)
    {
        Record recClassInfo2 = m_recClassInfo2;
        FieldData recFieldData = (FieldData)this.getRecord(FieldData.FIELD_DATA_FILE);
        
        String strRecordClass = recFieldData.getField(FieldData.FIELD_CLASS).getString();
        fieldStuff.strBaseFieldClass = null;
        try   {
            while (true)
            {
                recClassInfo2.setKeyArea(ClassInfo.CLASS_NAME_KEY);
                recClassInfo2.getField(ClassInfo.CLASS_NAME).setString(strRecordClass);   // Class of this record
                if ((!recClassInfo2.seek("=")) || (strRecordClass == null) || (strRecordClass.length() == 0))
                {
                    if (fieldStuff.strBaseFieldClass == null)
                        fieldStuff.strBaseFieldClass = recFieldData.getField(FieldData.FIELD_CLASS).getString();   // Never
                    return;
                }
                if (fieldStuff.strBaseFieldClass == null)
                {
                	String packageName = ((ClassInfo)recClassInfo2).getPackageName(null);
                    if (packageName.endsWith(".base.field"))
                        fieldStuff.strBaseFieldClass = recClassInfo2.getField(ClassInfo.CLASS_NAME).toString();
                }
                if (strRecordClass.indexOf("Field") != -1)
                {
                    String strType = strRecordClass.substring(0, strRecordClass.indexOf("Field"));
                    if ((strType.equals("DateTime")) || (strType.equals("Time")))
                        strType = "Date";
                    if (strType.length() > 0)
                        if ("Short Integer Double Float Boolean String Date".indexOf(strType) != -1)
                    {
                        if ((fieldStuff.strDataClass == null) || (fieldStuff.strDataClass.length() == 0))
                            fieldStuff.strDataClass = strType;      // End of based records - not found
                        return;
                    }
                }
                strRecordClass = recClassInfo2.getField(ClassInfo.BASE_CLASS_NAME).getString();
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     *
     */
    public String fixDBType(String strDBType, String strPrefix)
    {
        if (strDBType.length() < 4)
            strDBType = "LOCAL";
        String strDBTypeOut = DBConstants.BLANK;
        StringTokenizer st = new StringTokenizer(strDBType, " ");
        while (st.hasMoreTokens())
        {
            String strToken = st.nextToken();
            if (strDBTypeOut.length() > 0)
                strDBTypeOut += " ";
            if ((strToken.length() > 1)     // Not |
                && (strToken.indexOf('.') == -1))
                    strDBTypeOut += strPrefix;
            strDBTypeOut += strToken;
        }
        return strDBTypeOut;
    }
}
