/*
 * Copyright © 2011 jbundle.org. All rights reserved.
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
import org.jbundle.app.program.db.FileHdr;
import org.jbundle.app.program.db.KeyInfo;
import org.jbundle.app.program.db.LogicFile;
import org.jbundle.app.program.db.ProgramControl;
import org.jbundle.app.program.manual.util.data.FieldStuff;
import org.jbundle.app.program.resource.db.ResourceTypeField;
import org.jbundle.app.program.resource.screen.WriteResources;
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
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.app.program.db.ClassProjectModel.CodeType;


/**
 *  WriteJava - Constructor.
 */
public class WriteRecordClass extends WriteSharedClass
{
    /**
     * Special resource only base class.
     */
    public static final String RESOURCE_CLASS = "ListResourceBundle";     // Resource only class
    /**
     *
     */
    public static final String VIRTUAL_FIELD_TYPE = "N";

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

        Record recKeyInfo = this.getRecord(KeyInfo.kKeyInfoFile);
        recKeyInfo.setKeyArea(KeyInfo.kKeyFilenameKey);
        SubFileFilter keyBehavior = new SubFileFilter(recClassInfo.getField(ClassInfo.kClassName), KeyInfo.kKeyFilename, null, -1, null, -1);
        recKeyInfo.addListener(keyBehavior);
        recKeyInfo.setKeyArea(KeyInfo.kKeyFilenameKey);
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
        Record recClassInfo = this.getMainRecord();
        if (!this.readThisClass(strClassName))  // Get the field this is based on
            return;
        if (RESOURCE_CLASS.equals(recClassInfo.getField(ClassInfo.kBaseClassName).toString()))
        {
            this.writeFieldResources(strClassName);
            return;     // Resource only class
        }

        this.writeHeading(strClassName, this.getPackage(codeType), CodeType.BASE);        // Write the first few lines of the files
        this.writeIncludes(CodeType.BASE);
    // Now include any BaseField classes not included in this source (method include)
        try   {
            Record recFieldData = this.getRecord(FieldData.kFieldDataFile);

            this.writeRecordClassDetail(strClassName);  // Write the Thick Record Class

            this.writeFieldResources(strClassName);

            this.writeThinRecord(strClassName);

            this.writeRecordInterface(strClassName);  // Write the Record Class Interface

        // Now write out the BaseField classes
            Record recFileHdr = this.getRecord(FileHdr.kFileHdrFile);
        
            FieldIterator fieldIterator = new FieldIterator(recFileHdr, recClassInfo, recFieldData);
            while (fieldIterator.hasNext())
            {
                fieldIterator.next();
                ClassInfo recClassInfo2 = null;
                if ((recClassInfo2 = this.readFieldClass()) != null)
                    if (recClassInfo2.getField(ClassInfo.kClassSourceFile).equals(recClassInfo.getField(ClassInfo.kClassSourceFile)))
                {   // If they are in the same source file, write the field class also.
                    String strFieldDataHandle = recFieldData.getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                    Map<String,Object> properties = new Hashtable<String,Object>();
                    properties.put(FieldData.kFieldDataFile, strFieldDataHandle);
                    if ((recClassInfo2.getEditMode() == DBConstants.EDIT_CURRENT)
                        || (recClassInfo2.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    {
                        String strClassInfoHandle = recClassInfo2.getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                        properties.put(ClassInfo.kClassInfoFile, strClassInfoHandle);
                    }
                    else
                    {
                        properties.put(recClassInfo2.getField(ClassInfo.kClassName).getFieldName(), recClassInfo2.getField(ClassInfo.kClassName).toString());
                        properties.put(recClassInfo2.getField(ClassInfo.kBaseClassName).getFieldName(), recClassInfo2.getField(ClassInfo.kBaseClassName).toString());
                        properties.put(recClassInfo2.getField(ClassInfo.kClassPackage).getFieldName(), this.getPackage(codeType));
                        properties.put(recClassInfo2.getField(ClassInfo.kClassProjectID).getFieldName(), recClassInfo2.getField(ClassInfo.kClassProjectID).toString());
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
        if (codeType != CodeType.BASE)
            return;
        // Thick only
        Record recClassInfo = this.getMainRecord();
        Record recFieldData = this.getRecord(FieldData.kFieldDataFile);
        Record recFileHdr = this.getRecord(FileHdr.kFileHdrFile);

        FieldIterator fieldIterator = new FieldIterator(recFileHdr, recClassInfo, recFieldData);
        while (fieldIterator.hasNext())
        {
            fieldIterator.next();
            ClassInfo recClassInfo2 = null;
            if ((recClassInfo2 = this.readFieldClass()) != null)   // Get the field this is based on
            {       // Not in this source
                String strFieldClass = recClassInfo2.getField(ClassInfo.kClassName).getString();
                m_IncludeNameList.addInclude(strFieldClass); // include the source
            }
        }
    }
    /**
     *  Write the resource file for this record class
     */
    public void writeFieldResources(String strClassName)
    {
    	boolean bResourceListBundle = ResourceTypeField.LIST_RESOURCE_BUNDLE.equals(this.getRecord(ProgramControl.kProgramControlFile).getField(ProgramControl.kResourceType).toString());
        Record recClassInfo = this.getMainRecord();
        String strPackage = this.getPackage(bResourceListBundle ? CodeType.RESOURCE_CODE : CodeType.RESOURCE_PROPERTIES);
    // Now, write the field resources (descriptions)
        FieldStuff fieldStuff = new FieldStuff();
        String strBaseClass = "ListResourceBundle";
        String strClassSuffix = "Resources";
        boolean bResourceOnlyFile = false;
        if (RESOURCE_CLASS.equals(recClassInfo.getField(ClassInfo.kBaseClassName).toString()))
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
            Record recFieldData = this.getRecord(FieldData.kFieldDataFile);
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
                        	m_StreamOut.writeit("\t\t{\"" + WriteResources.fixPropertyKey(fieldStuff.strFieldName) + "\", " + WriteResources.fixPropertyValue(fieldStuff.strFieldDesc, bResourceListBundle) + "}");
                        else
                        	m_StreamOut.writeit(WriteResources.fixPropertyKey(fieldStuff.strFieldName) + "=" + WriteResources.fixPropertyValue(fieldStuff.strFieldDesc, bResourceListBundle));
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
                if (recFieldData.getField(FieldData.kFieldName).getString().equals(recFieldData.getField(FieldData.kBaseFieldName).getString()))
                	if (recFieldData.getField(FieldData.kFieldDescVertical).getLength() == 0)
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
                    	m_StreamOut.writeit(strPre + "\t\t{\"" + WriteResources.fixPropertyKey(fieldStuff.strFieldName + strTipSuffix) + "\", " + WriteResources.fixPropertyValue(fieldStuff.strFieldTip, bResourceListBundle) + "}");
                    else
                    	m_StreamOut.writeit(strPre + WriteResources.fixPropertyKey(fieldStuff.strFieldName + strTipSuffix) + "=" + WriteResources.fixPropertyValue(fieldStuff.strFieldTip, bResourceListBundle));
//                    m_MethodsOut.writeit(strPre + "\t\t{\"" + fieldStuff.strFieldName + strTipSuffix + "\", \"" + fieldStuff.strFieldTip + "\"}");
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
        Record recFileHdr = this.getRecord(FileHdr.kFileHdrFile);
        FieldData recFieldData = (FieldData)this.getRecord(FieldData.kFieldDataFile);
        if (!recClassInfo.isARecord())
            return;
        String strDBType = recFileHdr.getField(FileHdr.kType).getString(); // Is Remote file?
        strDBType = this.fixDBType(strDBType, "Constants.");
        String strPackage = this.getPackage(CodeType.INTERFACE);
    // Now, write the field resources (descriptions)
        String strBaseClass = recClassInfo.getField(ClassInfo.kBaseClassName).getString();
        if ((strBaseClass.equalsIgnoreCase("Record")) || (strBaseClass.equalsIgnoreCase("QueryRecord")))
            strBaseClass = "";  // .model.db.Rec
        this.writeHeading(strClassName + "Model", strPackage, ClassProject.CodeType.INTERFACE);
        this.writeIncludes(CodeType.INTERFACE);

        String baseClassPackage = "";
        if (!strBaseClass.equals(""))
        {
            this.readRecordClass(strBaseClass);     // Return the record to the original position
            ClassProject classProject = (ClassProject)((ReferenceField)recClassInfo.getField(ClassInfo.kClassProjectID)).getReference();
            baseClassPackage = classProject.getFullPackage(CodeType.INTERFACE, recClassInfo.getField(ClassInfo.kClassPackage).toString());
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
        ThinFieldIterator fieldIterator = new ThinFieldIterator(recFileHdr, recClassInfo, recFieldData);
        // Write out any field constants that should be include in the thin record.
        fieldIterator.close();
        while (fieldIterator.hasNext())
        {
            fieldIterator.next();
            int methodType = (int)(recFieldData.getField(FieldData.kIncludeScope).getValue() + .001);
            if ((methodType & LogicFile.INCLUDE_INTERFACE) != 0)
                if (strClassName.equalsIgnoreCase(recFieldData.getField(FieldData.kFieldFileName).toString()))  // Only for concrete class
            {
                String strFieldName = recFieldData.getField(FieldData.kFieldName).toString();
                String strFieldConstant = this.convertNameToConstant(strFieldName);
                m_StreamOut.writeit("public static final String " + strFieldConstant + " = \"" + strFieldName + "\";\n");
            }
        }
        this.writeKeyOffsets(CodeType.INTERFACE);   // Write the Key offsets
        this.writeClassFields(LogicFile.INCLUDE_INTERFACE);    // Write the thin class fields.
        
        m_StreamOut.writeit("\n");

        String dBFileName = recFileHdr.getField(FileHdr.kFileMainFilename).getString();
        if (dBFileName.length() != 0)
        {
            dBFileName = this.fixSQLName(dBFileName);
            if (dBFileName.equalsIgnoreCase("NONE"))
                dBFileName = null;
        }
        else if (recFileHdr.getField(FileHdr.kType).toString().indexOf("MAPPED") == -1)
            dBFileName = strClassName;  // Default file name (unless mapped)

        if ((dBFileName != null) && (dBFileName.length() > 0))
        {
            m_StreamOut.writeit("public static final String " + this.convertNameToConstant(strClassName) + "_FILE = \"" + dBFileName + "\";\n");

            m_StreamOut.writeit("public static final String THIN_CLASS = \"" + this.getPackage(CodeType.THIN) + "." + strClassName + "\";\n");
            m_StreamOut.writeit("public static final String THICK_CLASS = \"" + this.getPackage(CodeType.BASE) + "." + strClassName + "\";\n");
//            m_StreamOut.writeit("public static final String RESOURCE_CLASS = \"" + this.getPackage(CodeType.RESOURCE_CODE) + "." + strClassName + "\";\n");
        }
         
        if (m_MethodNameList.size() != 0)
            m_MethodNameList.removeAllElements();
        this.writeClassMethods(LogicFile.INCLUDE_INTERFACE);   // Write the methods (that are) interfaces

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
        if (!recClassInfo.isARecord())
            return;
        Record recFileHdr = this.getRecord(FileHdr.kFileHdrFile);
        FieldData recFieldData = (FieldData)this.getRecord(FieldData.kFieldDataFile);
        String strDatabaseName = recFileHdr.getField(FileHdr.kDatabaseName).getString();   // Database name
        String strDBType = recFileHdr.getField(FileHdr.kType).getString(); // Is Remote file?
        strDBType = this.fixDBType(strDBType, "Constants.");
        String strPackage = this.getPackage(CodeType.THIN);
        String implementsPackage = this.getPackage(CodeType.INTERFACE);
        if (implementsPackage.equalsIgnoreCase("org.jbundle.model.base.db"))
            implementsPackage = "org.jbundle.model.db";
    // Now, write the field resources (descriptions)
        String strBaseClass = recClassInfo.getField(ClassInfo.kBaseClassName).getString();
        if ((strBaseClass.equalsIgnoreCase("Record")) || (strBaseClass.equalsIgnoreCase("QueryRecord")))
            strBaseClass = "FieldList";
        this.writeHeading(strClassName, strPackage, ClassProject.CodeType.THIN);
        this.writeIncludes(CodeType.THIN);

        m_StreamOut.writeit("import java.util.*;\n");
        m_StreamOut.writeit("import " + DBConstants.ROOT_PACKAGE + "thin.base.util.*;\n\n");
        m_StreamOut.writeit("import " + DBConstants.ROOT_PACKAGE + "thin.base.db.*;\n\n");

/*        recFileHdr.getField(FileHdr.kFileName).setString(strBaseClass);
        try {
            if (!recFileHdr.seek(null))
                strBaseClass = "FieldList";
            recFileHdr.getField(FileHdr.kFileName).setString(strClassName); // Restore
            recFileHdr.seek(null);
        } catch (DBException e) {
            e.printStackTrace();
        }
*/
        if (!strBaseClass.equals("FieldList"))
        {
            this.readRecordClass(strBaseClass);     // Return the record to the original position
            ClassProject classProject = (ClassProject)((ReferenceField)recClassInfo.getField(ClassInfo.kClassProjectID)).getReference();
            String baseClassPackage = classProject.getFullPackage(CodeType.THIN, recClassInfo.getField(ClassInfo.kClassPackage).toString());
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

        this.readRecordClass(strClassName);     // Return the record to the original position
        ThinFieldIterator fieldIterator = new ThinFieldIterator(recFileHdr, recClassInfo, recFieldData);
        // Write out any field constants that should be include in the thin record.
        fieldIterator.close();
        while (fieldIterator.hasNext())
        {
            fieldIterator.next();
            int methodType = (int)(recFieldData.getField(FieldData.kIncludeScope).getValue() + 0.5);
            boolean concreteClass = true;
            if (recFieldData.getField(FieldData.kID).isNull())  // Only for concrete class
                concreteClass = false;
//          if (!recFieldData.getField(FieldData.kFieldFileName).equals(strClassName))
  //            concreteClass = false;
            if (!recFieldData.getField(FieldData.kBaseFieldName).isNull())
                if (!recFieldData.getField(FieldData.kBaseFieldName).toString().equalsIgnoreCase(recFieldData.getField(FieldData.kBaseFieldName).toString()))
                    concreteClass = false;
            if (concreteClass)
                if (((methodType & LogicFile.INCLUDE_THIN) != 0) && ((methodType & LogicFile.INCLUDE_INTERFACE) == 0))
            {
                String strFieldName = recFieldData.getField(FieldData.kFieldName).toString();
                String strFieldConstant = this.convertNameToConstant(strFieldName);
                m_StreamOut.writeit("public static final String " + strFieldConstant + " = \"" + strFieldName + "\";\n");
            }
        }
        this.writeClassFields(LogicFile.INCLUDE_THIN);    // Write the thin class fields.
        
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

        if ((strDatabaseName != null) && (strDatabaseName.length() > 0))
            this.writeFields(strClassName, strDatabaseName, strDBType, recClassInfo, recFileHdr, recFieldData, fieldIterator);
        
        this.writeClassMethods(LogicFile.INCLUDE_THIN);
        
        m_StreamOut.setTabs(-1);
        this.writeEndCode(true);
        recFieldData.close();

        this.readRecordClass(strClassName);     // Return the record to the original position
    }
    public void writeFields(String strClassName, String strDatabaseName, String strDBType, ClassInfo recClassInfo, Record recFileHdr, FieldData recFieldData, ThinFieldIterator fieldIterator)
    {
        FieldStuff fieldStuff = new FieldStuff();
        String dBFileName = recFileHdr.getField(FileHdr.kFileMainFilename).getString();
        if (dBFileName.length() != 0)
        {
            dBFileName = this.fixSQLName(dBFileName);
            if (dBFileName.equalsIgnoreCase("NONE"))
                dBFileName = null;
        }
        else if (recFileHdr.getField(FileHdr.kType).toString().indexOf("MAPPED") == -1)
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
        m_StreamOut.writeit("/**\n");
        m_StreamOut.writeit(" *\tGet the Database Name.\n");
        m_StreamOut.writeit(" */\n");
        m_StreamOut.writeit("public String getDatabaseName()\n");
        m_StreamOut.writeit("{\n");
        m_StreamOut.writeit("\treturn \"" + strDatabaseName + "\";\n");
        m_StreamOut.writeit("}\n");
        
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

        try   {
            fieldIterator.close();
            while (fieldIterator.hasNext())
            {
                fieldIterator.next();
                String strPre = DBConstants.BLANK;

                this.getFieldData(fieldStuff, true);

                if (("CounterField".equals(fieldStuff.strFieldClass + "\n"))
                    || ("CounterField".equals(fieldStuff.strBaseFieldClass)))
                        bAutoCounterField = true;

                if (recFieldData.getField(FieldData.kFieldType).getString().equalsIgnoreCase(VIRTUAL_FIELD_TYPE))
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
                m_StreamOut.writeit(strPre + "field = new " + fieldStuff.strFieldClass + "(this, \"" + fieldStuff.strFileFieldName + "\", " + fieldStuff.strFieldLength + ", " + fieldStuff.strFieldDesc + ", " + fieldStuff.strDefaultField + ");\n");
                if (fieldStuff.strDataClass.equals("Percent"))
                    fieldStuff.strDataClass = "Float";
                if ((fieldStuff.strDataClass.equals("Currency")) || (fieldStuff.strDataClass.equals("Real")))
                    fieldStuff.strDataClass = "Double";
                if ((fieldStuff.strDataClass == null) || (fieldStuff.strDataClass.length() == 0))
                    fieldStuff.strDataClass = "Object";                     
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
            this.writeClassKeys(strClassName, recClassInfo, recFieldData, bAutoCounterField);
            
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }

    public void writeClassKeys(String strClassName, ClassInfo recClassInfo, FieldData recFieldData, boolean bAutoCounterField)
        throws DBException
    {
        // Now write out the setupkey method.
        this.readRecordClass(strClassName);     // Return the record to the original position
        String strRecordClass = recClassInfo.getField(ClassInfo.kClassName).getString();
        m_StreamOut.writeit("/**\n");
        m_StreamOut.writeit("* Set up the key areas.\n");
        m_StreamOut.writeit("*/\n");
        m_StreamOut.writeit("public void setupKeys()\n");
        m_StreamOut.writeit("{\n");
        m_StreamOut.setTabs(+1);
        m_StreamOut.writeit("KeyAreaInfo keyArea = null;\n");
        int count = 0;
        Record recKeyInfo = this.getRecord(KeyInfo.kKeyInfoFile);
        
        recKeyInfo.close();
        while (recKeyInfo.hasNext())
        {
            recKeyInfo.next();
            count++;
            String strDaoKeyName = "";
            String strKeyName = recKeyInfo.getField(KeyInfo.kKeyName).getString();
            if (strKeyName.length() == 0)
            {
                strKeyName = recKeyInfo.getField(KeyInfo.kKeyField1).getString();
                recFieldData.initRecord(false);
                String strFieldName, strBaseFieldName;
                strFieldName = strKeyName;
                strBaseFieldName = "";
                m_BasedFieldHelper.getBasedField(recFieldData, strRecordClass, strFieldName, strBaseFieldName);  // Get the field this is based on
                strDaoKeyName = recFieldData.getField(FieldData.kFieldName).getString();
                if (count == 1)
                {
                    strDaoKeyName = DBConstants.PRIMARY_KEY;
                }
            }
            if (strDaoKeyName.length() == 0)
                strDaoKeyName = strKeyName;
            if (strKeyName.equalsIgnoreCase(DBConstants.PRIMARY_KEY))
                strKeyName = strRecordClass + "Primary";    // To avoid re-defining kPrimaryKey
            strKeyName = strKeyName + "Key";

            String strKeyType, strKeyFieldName;
            strKeyType = recKeyInfo.getField(KeyInfo.kKeyType).getString();
            strDaoKeyName = this.fixSQLName(strDaoKeyName);
            String strUnique = "UNIQUE";
            if (strKeyType.equalsIgnoreCase("U"))
                strUnique = "UNIQUE";
            else if (strKeyType.equalsIgnoreCase("N"))
                strUnique = "NOT_UNIQUE";
            else if (strKeyType.equalsIgnoreCase("S"))
                strUnique = "SECONDARY_KEY";
            else
            {   // Not specified.
                if (strKeyName.equalsIgnoreCase(DBConstants.PRIMARY_KEY))
                    strUnique = "UNIQUE";
                else
                    strUnique = "NOT_UNIQUE";
            }
            m_StreamOut.writeit("keyArea = new KeyAreaInfo(this, Constants." + strUnique + ", \"" + strDaoKeyName + "\");\n");
            boolean bAscending = true;
            if (recKeyInfo.getField(KeyInfo.kKeyField9).getString().equalsIgnoreCase("D"))
                bAscending = false;     // *Fix this to allow Ascending/Descending on each key area!*
            for (int i = KeyInfo.kKeyField1; i <= KeyInfo.kKeyField9; i++)
            {
                strKeyFieldName = recKeyInfo.getField(i).getString();
                if (strKeyFieldName.length() == 0)
                    break;
                String strAscending = "ASCENDING";
                if (!bAscending)
                    strAscending = "DESCENDING";
                m_StreamOut.writeit("keyArea.addKeyField(\"" + strKeyFieldName + "\", Constants." + strAscending + ");\n");
            }
        }
        recKeyInfo.close();
        
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
    /**
     *  Create the Record Class for this Record.
     */
    public void writeRecordClassDetail(String strRecordClass)
    {
        Record recClassInfo = this.getMainRecord();
        try   {
            this.readRecordClass(strRecordClass);
            if (m_MethodNameList.size() != 0)
                m_MethodNameList.removeAllElements();
            this.writeClassInterface();
        
            m_StreamOut.writeit("private static final long serialVersionUID = 1L;\n");

        	this.writeFieldOffsets(); // Write the BaseField offsets
            this.writeKeyOffsets(CodeType.BASE);   // Write the Key offsets
        
            this.writeClassFields(LogicFile.INCLUDE_THICK);
            this.writeDefaultConstructor(strRecordClass);
            
            this.writeRecordInit();
            this.writeInit();   // Special case... zero all class fields!
        
            FileHdr recFileHdr = (FileHdr)this.getRecord(FileHdr.kFileHdrFile);
            recFileHdr.getField(FileHdr.kFileName).setString(strRecordClass);
            recFileHdr.setKeyArea(FileHdr.kFileNameKey);
            boolean bFileType = (recFileHdr.seek("="));
            if (bFileType) // Is there a file with this name?
            {
                String dBFileName = recFileHdr.getField(FileHdr.kFileMainFilename).getString();
                if (dBFileName.length() != 0)
                {
                    dBFileName = this.fixSQLName(dBFileName);
                    if (dBFileName.equalsIgnoreCase("NONE"))
                        dBFileName = null;
                }
                else if (recFileHdr.getField(FileHdr.kType).toString().indexOf("MAPPED") == -1)
                    dBFileName = strRecordClass;  // Default file name (unless mapped)

                if ((dBFileName != null) && (dBFileName.length() > 0))
                {
                    m_StreamOut.writeit("\npublic static final String k" + strRecordClass + "File = \"" + dBFileName + "\";\n");
                    if (!this.readThisMethod("getTableNames"))
                    {
                    	this.writeMethodInterface(null, "getTableNames", "String", "boolean bAddQuotes", "", "Get the table name.", null);
                    	m_StreamOut.writeit("\treturn (m_tableName == null) ? Record.formatTableNames(k" + strRecordClass + "File, bAddQuotes) : super.getTableNames(bAddQuotes);\n}\n");
                    }
                }
                String recordName = recFileHdr.getField(FileHdr.kFileRecCalled).getString();    // Description of a record
                String databaseName = recFileHdr.getField(FileHdr.kDatabaseName).getString(); // Database name
                String strDBType = recFileHdr.getField(FileHdr.kType).getString(); // Is Remote file?
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
                if (recClassInfo.getField(ClassInfo.kClassType).getString().equalsIgnoreCase("Record"))
                    m_StreamOut.writeit("\npublic static final String k" + strRecordClass + "File = null;\t// Screen field\n");    // Screen fields

            this.writeSetupField();
            this.readRecordClass(strRecordClass); // Must re-read this record class

            if (bFileType) // Is there a file with this name?
                this.writeSetupKey();
            this.writeClassMethods(LogicFile.INCLUDE_THICK);       // Write the remaining methods
            this.writeEndCode(true);
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     *  Write the constants for the field offsets
     */
    public void writeFieldOffsets()
    { // Now, write all the field offsets out in the header file
        Record recClassInfo = this.getMainRecord();
        int iFieldCount = 0;
        boolean firstTime = true;
        String strBaseRecordClass, strBaseFieldName, strFieldName, strRecordClass;
        strRecordClass = recClassInfo.getField(ClassInfo.kClassName).getString();
        strBaseRecordClass = recClassInfo.getField(ClassInfo.kBaseClassName).getString();
        strFieldName = "MainField";     // In case there are no fields
        Record recFieldData = this.getRecord(FieldData.kFieldDataFile);
        Record recFileHdr = this.getRecord(FileHdr.kFileHdrFile);

        FieldIterator fieldIterator = new FieldIterator(recFileHdr, recClassInfo, recFieldData);
        String strLastField = strBaseRecordClass + "LastField";

        fieldIterator.close();
        while (fieldIterator.hasNext())
        {
            fieldIterator.next();
            int methodType = (int)(recFieldData.getField(FieldData.kIncludeScope).getValue() + .001);
            if ((methodType & LogicFile.INCLUDE_THICK) != 0)
            {
                strFieldName = recFieldData.getField(FieldData.kFieldName).toString();
                String strFieldConstant = this.convertNameToConstant(strFieldName);
                m_StreamOut.writeit("public static final String " + strFieldConstant + " = \"" + strFieldName + "\";\n");
            }
        }
        for (int pass = 1; pass <= 2; ++pass) // Do this two times (two passes)
        {
            fieldIterator.close();
            int count = 0;
            while (fieldIterator.hasNext())
            {
                fieldIterator.next();
                strFieldName = recFieldData.getField(FieldData.kFieldName).getString();
                strBaseFieldName = recFieldData.getField(FieldData.kBaseFieldName).getString();
                switch (pass)
                {
                case 1:     // First, add all fields based on the super.New8 class
                    iFieldCount++;      // Total BaseField Count
                    if (strBaseFieldName.length() > 0)
                    {
                        count++;
                        firstTime = false;
                        String tempStr3 = " = k" + strBaseFieldName + ";";
                        String strPre = DBConstants.BLANK;
                        if (recFieldData.getField(FieldData.kFieldName).getString().equals(strBaseFieldName))
                            strPre = "//";
                        m_StreamOut.writeit("\n" + strPre + "public static final int k" + strFieldName + tempStr3);
                    }
                    break;
                case 2:     // Now, add all fields new to this class
                    if (strBaseFieldName.length() == 0)
                    {
                        firstTime = false;
                        count++;
                        String tempStr3 = " = k" + strLastField + " + 1;";
                        m_StreamOut.writeit("\npublic static final int k" + strFieldName + tempStr3);
                        strLastField = strFieldName;
                        break;
                    }
                }
            }
            recFieldData.close();
            if (pass == 2)
            {
                if (!firstTime)
                {
                    m_StreamOut.writeit("\npublic static final int k" + strRecordClass + "LastField = k" + strLastField + ";\n");
                    m_StreamOut.writeit("public static final int k" + strRecordClass + "Fields = k" + strLastField + " - DBConstants.MAIN_FIELD + 1;\n");
                }
            }
        } // End of pass loop
        
    }
    /**
     *  Create the Record Class for this Record
     */
    public void writeKeyOffsets(CodeType codeType)
    { // Now, write all the key offsets out
        Record recClassInfo = this.getMainRecord();
        try   {
            String previousKey;
            String strKeyName = "";
            String strRecordClass;
            String uniqueKeyFields[] = new String[10];
            String uniqueKeySeq[] = new String[10];
            int uniqueKeyCount = 0;
            strRecordClass = recClassInfo.getField(ClassInfo.kClassName).getString();
            
            Record recKeyInfo = this.getRecord(KeyInfo.kKeyInfoFile);
            recKeyInfo.close();
            int count = 0;
            String strLastKeyName = "DBConstants.MAIN_KEY_FIELD;";
            while (recKeyInfo.hasNext())
            {
                recKeyInfo.next();
                count++;
                strKeyName = recKeyInfo.getField(KeyInfo.kKeyName).getString();
                if (strKeyName.length() == 0)
                {
                    strKeyName = recKeyInfo.getField(KeyInfo.kKeyField1).getString();
                }
                if (strKeyName.equalsIgnoreCase(DBConstants.PRIMARY_KEY))
                    strKeyName = strRecordClass + "Primary";    // To avoid re-defining kPrimaryKey
                if (count == 1)
                {
        //j         m_HeadersOut.Writeit("\nenum e" + strRecordClass + "Keys {");
                    previousKey = "DBConstants.MAIN_KEY_FIELD;";
                }
                else
                    previousKey = "k" + strLastKeyName + "Key + 1;";
                
                if ((codeType == CodeType.THIN) || (codeType == CodeType.INTERFACE))
                {
                    int scope = (int)(recKeyInfo.getField(KeyInfo.kIncludeScope).getValue() + 0.5);
                    if ((codeType == CodeType.INTERFACE) && ((scope & LogicFile.INCLUDE_INTERFACE) == 0))
                        continue;
                    if ((codeType == CodeType.THIN) && (((scope & LogicFile.INCLUDE_THIN) == 0) || ((scope & LogicFile.INCLUDE_INTERFACE) != 0)))
                        continue;
                    if (count > 1)
                        m_StreamOut.writeit("\npublic static final String " + this.convertNameToConstant(strKeyName + "Key") + " = \"" + strKeyName +"\";\n");
                    continue;
                }
                    
                m_StreamOut.writeit("\npublic static final int k" + strKeyName + "Key = " + previousKey);
                    // This logic here checks to see if this is a unique key with one field, if yes adds field to array
                String keyTypeStr, strKeyFieldName;
                keyTypeStr = recKeyInfo.getField(KeyInfo.kKeyType).getString();
                if (keyTypeStr.length() == 0)
                    keyTypeStr = "U"; // Unique key if no selection
                strKeyFieldName = recKeyInfo.getField(KeyInfo.kKeyField2).getString();
                if (((keyTypeStr.charAt(0) == 'U') || (keyTypeStr.charAt(0) == 'Y')) && (strKeyFieldName.length() == 0))
                    { // Unique key with one field, default logic... always try to read
                    strKeyFieldName = recKeyInfo.getField(KeyInfo.kKeyField1).getString();
                    uniqueKeyFields[uniqueKeyCount] = strKeyFieldName;
                    uniqueKeySeq[uniqueKeyCount] = strKeyName;
                    uniqueKeyCount++;
                }
                strLastKeyName = strKeyName;
            }
            recKeyInfo.close();
            if (count > 0)
                if (codeType == CodeType.BASE)
            {
                m_StreamOut.writeit("\npublic static final int k" + strRecordClass + "LastKey = k" + strKeyName + "Key;\n");
                m_StreamOut.writeit("public static final int k" + strRecordClass + "Keys = k" + strKeyName + "Key - DBConstants.MAIN_KEY_FIELD + 1;\n");
            }
            return;
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     *  Initialize the class
     */
    public void writeRecordInit()
    {
        Record recClassInfo = this.getMainRecord();
        String strClassName = recClassInfo.getField(ClassInfo.kClassName).getString();
        this.readThisMethod(strClassName);
        this.writeThisMethod(LogicFile.INCLUDE_THICK);
    }
    /**
     *  Write the TableDoc code to DoMakeViews.
     */
    public void writeFileMakeViews()
    {
        String maintClass, displayClass;
        Record recFileHdr = this.getRecord(FileHdr.kFileHdrFile);
        displayClass = recFileHdr.getField(FileHdr.kDisplayClass).getString();
        maintClass = recFileHdr.getField(FileHdr.kMaintClass).getString();
        if (this.readThisMethod("makeScreen"))
        {
            this.writeThisMethod(LogicFile.INCLUDE_THICK);
            return;     // if no views specified, use default methods
        }
        if ((maintClass.length() == 0) && (displayClass.length() == 0))
            return;
        this.writeMethodInterface(null, "makeScreen", "BaseScreen", "ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties", "", "Make a default screen.", null);
        String strDefaultCall = "\t\tscreen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);\n";
        m_StreamOut.writeit("\tBaseScreen screen = null;\n");
        m_StreamOut.writeit("\tif ((iDocMode & ScreenConstants.MAINT_MODE) != 0)\n");
        if ((maintClass.length() == 0))
            m_StreamOut.writeit(strDefaultCall);
        else
        {
            m_StreamOut.writeit("\t\tscreen = new " + maintClass + "(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);\n");
            m_IncludeNameList.addInclude(maintClass);
        }
    
        m_StreamOut.writeit("\telse if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)\n");
    
        if ((displayClass.length() == 0))
            m_StreamOut.writeit(strDefaultCall);
        else
        {
            m_StreamOut.writeit("\t\tscreen = new " + displayClass + "(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);\n");
            m_IncludeNameList.addInclude(displayClass);
        }
    
        m_StreamOut.writeit("\telse\n");
        m_StreamOut.writeit(strDefaultCall);

        m_StreamOut.writeit("\treturn screen;\n}\n");
    }
    /**
     *  WriteSetupField.
     *  WARNING: This method changes the Class Info File Position
     */
    public void writeSetupField()
    {
        FieldStuff fieldStuff = new FieldStuff();
        Record recClassInfo = this.getMainRecord();
    // Now, write all the record methods
        Record recFileHdr = this.getRecord(FileHdr.kFileHdrFile);          // Open the Agency File
        Record recFieldData = this.getRecord(FieldData.kFieldDataFile);          // Open the Agency File
        FieldIterator fieldIterator = new FieldIterator(recFileHdr, recClassInfo, recFieldData);
        if (!fieldIterator.hasNext())
            return;

    // Initialization method
        String strRecordClass = recClassInfo.getField(ClassInfo.kClassName).getString();

        this.writeMethodInterface(null, "setupField", "BaseField", "int iFieldSeq", "", "Add this field in the Record's field sequence.", null);
        m_StreamOut.setTabs(+1);
        m_StreamOut.writeit("BaseField field = null;\n");

        while (fieldIterator.hasNext())
        {
            recFieldData = (Record)fieldIterator.next();
            String strPre = DBConstants.BLANK;
            if (recFieldData.getField(FieldData.kID).isNull())
                strPre = "//";
            if (recFieldData.getField(FieldData.kFieldName).getString().equals(recFieldData.getField(FieldData.kBaseFieldName).getString()))
            if (recFieldData.getField(FieldData.kFieldClass).getLength() == 0)
            if (recFieldData.getField(FieldData.kMaximumLength).getLength() == 0)
            if (recFieldData.getField(FieldData.kFieldDescVertical).getLength() == 0)
            if (recFieldData.getField(FieldData.kDefaultValue).getLength() == 0)
            if (recFieldData.getField(FieldData.kInitialValue).getLength() == 0)
                strPre = "//";

            this.getFieldData(fieldStuff, false);

            if ((recFieldData.getField(FieldData.kFieldType).getString().equalsIgnoreCase("N")) || (fieldStuff.bNotNullField))
                strPre = DBConstants.BLANK;
            m_StreamOut.writeit(strPre + "if (iFieldSeq == k" + fieldStuff.strFieldName + ")\n");
            String strFieldType = recFieldData.getField(FieldData.kFieldType).getString();
            String strDefaultValue = recFieldData.getField(FieldData.kDefaultValue).getString();
            String strMinimumLength = recFieldData.getField(FieldData.kMinimumLength).getString();
            boolean bHidden = recFieldData.getField(FieldData.kHidden).getState();
            if (strMinimumLength.equals("0"))
                strMinimumLength = "";
            if ((strFieldType.equalsIgnoreCase("N")) || (fieldStuff.bNotNullField) || (strDefaultValue.equalsIgnoreCase("old")) || (strMinimumLength.length() > 0) || bHidden)   // Virtual BaseField?
                m_StreamOut.writeit(strPre + "{\n");
            if (!fieldStuff.strFieldDesc.equals("null"))
                fieldStuff.strFieldDesc = "\"" + fieldStuff.strFieldDesc + "\"";
            fieldStuff.strFieldDesc = "null";   // Use the resource file!
            m_StreamOut.writeit(strPre + "\tfield = new " + fieldStuff.strFieldClass + "(this, \"" + fieldStuff.strFileFieldName + "\", " + fieldStuff.strFieldLength + ", " + fieldStuff.strFieldDesc + ", " + fieldStuff.strDefaultField + ");\n");
            if (strFieldType.equalsIgnoreCase("N"))     // Virtual BaseField?
                m_StreamOut.writeit(strPre + "\tfield.setVirtual(true);\n");
            if (bHidden)     // Virtual BaseField?
                m_StreamOut.writeit(strPre + "\tfield.setHidden(true);\n");
            if (fieldStuff.bNotNullField)
                m_StreamOut.writeit(strPre + "\tfield.setNullable(false);\n");
            if (strDefaultValue.equalsIgnoreCase("old"))    // Virtual BaseField?
                m_StreamOut.writeit(strPre + "\tfield.addListener(new InitOnceFieldHandler(null));\n");
            if (strMinimumLength.length() > 0)  // Virtual BaseField?
                m_StreamOut.writeit(strPre + "\tfield.setMinimumLength(" + strMinimumLength + ");\n");
            if ((strFieldType.equalsIgnoreCase("N")) || (fieldStuff.bNotNullField) || (strDefaultValue.equalsIgnoreCase("old")) || (strMinimumLength.length() > 0) || bHidden)   // Virtual BaseField?
                m_StreamOut.writeit(strPre + "}\n");
        }
    //  m_MethodsOut.writeit("default:\n");
        m_StreamOut.writeit("if (field == null)\n");
        m_StreamOut.writeit("{\n");
        m_StreamOut.setTabs(+1);
        m_StreamOut.writeit("field = super.setupField(iFieldSeq);\n");
        m_StreamOut.writeit("if (field == null) if (iFieldSeq < k" + strRecordClass + "LastField)\n");
        m_StreamOut.writeit("\tfield = new EmptyField(this);\n");
        m_StreamOut.setTabs(-1);
    //  m_MethodsOut.SetTabs(-1);
        m_StreamOut.writeit("}\n");
        m_StreamOut.writeit("return field;\n");
        m_StreamOut.setTabs(-1);
        m_StreamOut.writeit("}\n");
        fieldIterator.free();
    }
    /**
     *  WriteSetupKey.
     */
    public void writeSetupKey()
    { // Now, write the key setup method
        Record recClassInfo = this.getMainRecord();
        try   {
            String strRecordClass, strDaoKeyName;
            String strKeyName = DBConstants.BLANK;
            strRecordClass = recClassInfo.getField(ClassInfo.kClassName).getString();
            
            Record recKeyInfo = this.getRecord(KeyInfo.kKeyInfoFile);
            recKeyInfo.close();
            if (!recKeyInfo.hasNext())
            {
                recKeyInfo.close();
                return;
            }
        
            this.writeMethodInterface(null, "setupKey", "KeyArea", "int iKeyArea", "", "Add this key area description to the Record.", null);
            m_StreamOut.setTabs(+1);
            m_StreamOut.writeit("KeyArea keyArea = null;\n");
        //  m_MethodsOut.writeit("switch(iKeyArea)\n{\n");
        //  m_MethodsOut.SetTabs(+1);
        
            int count = 0;
            recKeyInfo.close();
            while (recKeyInfo.hasNext())
            {
                recKeyInfo.next();
                count++;
                strDaoKeyName = "";
                strKeyName = recKeyInfo.getField(KeyInfo.kKeyName).getString();
                if (strKeyName.length() == 0)
                {
                    strKeyName = recKeyInfo.getField(KeyInfo.kKeyField1).getString();
                    if (count == 1)
                        strDaoKeyName = DBConstants.PRIMARY_KEY;
                    else
                    {   // No key name, use the field name!
                        FieldData recFieldData = (FieldData)this.getRecord(FieldData.kFieldDataFile);
                        recFieldData.initRecord(false);
                        String strFieldName, strBaseFieldName;
                        strFieldName = strKeyName;
                        strBaseFieldName = "";
                        m_BasedFieldHelper.getBasedField(recFieldData, strRecordClass, strFieldName, strBaseFieldName);  // Get the field this is based on
                        strDaoKeyName = recFieldData.getField(FieldData.kFieldName).getString();
                    }
                }
                if (strDaoKeyName.length() == 0)
                    strDaoKeyName = strKeyName;
                if (strKeyName.equalsIgnoreCase(DBConstants.PRIMARY_KEY))
                    strKeyName = strRecordClass + "Primary";    // To avoid re-defining kPrimaryKey
                strKeyName = strKeyName + "Key";
        //      m_MethodsOut.writeit("case k" + strKeyName + ":\n");
                m_StreamOut.writeit("if (iKeyArea == k" + strKeyName + ")\n");
                m_StreamOut.writeit("{\n");
                m_StreamOut.setTabs(+1);
                String strKeyType, strKeyFieldName;
                strKeyType = recKeyInfo.getField(KeyInfo.kKeyType).getString();
                strDaoKeyName = this.fixSQLName(strDaoKeyName);
                String strUnique = "UNIQUE";
                if (strKeyType.equalsIgnoreCase("U"))
                    strUnique = "UNIQUE";
                else if (strKeyType.equalsIgnoreCase("N"))
                    strUnique = "NOT_UNIQUE";
                else if (strKeyType.equalsIgnoreCase("S"))
                    strUnique = "SECONDARY_KEY";
                else
                {   // Not specified.
                    if (strKeyName.equalsIgnoreCase(DBConstants.PRIMARY_KEY))
                        strUnique = "UNIQUE";
                    else
                        strUnique = "NOT_UNIQUE";
                }
                m_StreamOut.writeit("keyArea = this.makeIndex(DBConstants." + strUnique + ", \"" + strDaoKeyName + "\");\n");
                boolean bAscending = true;
                if (recKeyInfo.getField(KeyInfo.kKeyField9).getString().equalsIgnoreCase("D"))
                    bAscending = false;     // *Fix this to allow Ascending/Descending on each key area!*
                for (int i = KeyInfo.kKeyField1; i <= KeyInfo.kKeyField9; i++)
                {
                    strKeyFieldName = recKeyInfo.getField(i).getString();
                    if (strKeyFieldName.length() == 0)
                        break;
                    String strAscending = "ASCENDING";
                    if (!bAscending)
                        strAscending = "DESCENDING";
                    m_StreamOut.writeit("keyArea.addKeyField(k" + strKeyFieldName + ", DBConstants." + strAscending + ");\n");
                }
        //      m_MethodsOut.writeit("break;\n");
                m_StreamOut.setTabs(-1);
                m_StreamOut.writeit("}\n");
            }
            recKeyInfo.close();
            m_StreamOut.writeit("if (keyArea == null) if (iKeyArea < k" + strRecordClass + "LastKey)\n");
        //  m_MethodsOut.writeit("default:\n");
            m_StreamOut.writeit("{\n");
            m_StreamOut.setTabs(+1);
            m_StreamOut.writeit("keyArea = super.setupKey(iKeyArea);\t\t\n");
            if (strKeyName.length() != 0)
            {
                m_StreamOut.writeit("if (keyArea == null) if (iKeyArea < k" + strRecordClass + "LastKey)\n");
                m_StreamOut.writeit("\tkeyArea = new EmptyKey(this);\n");
            }
            m_StreamOut.setTabs(-1);
            m_StreamOut.writeit("}\n");
        //  m_MethodsOut.writeit("break;\n");
        //  m_MethodsOut.SetTabs(-1);
        //  m_MethodsOut.writeit("}\n");
            m_StreamOut.writeit("return keyArea;\n");
            m_StreamOut.setTabs(-1);
            m_StreamOut.writeit("}\n");
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

            recClassFields.setKeyArea(ClassFields.kClassInfoClassNameKey);
            SubFileFilter fileBehavior2 = new SubFileFilter(recClassInfo.getField(ClassInfo.kClassName), ClassFields.kClassInfoClassName, null, -1, null, -1);
            recClassFields.addListener(fileBehavior2);   // Only read through the class fields

            recClassFields.close();
            recClassFields.moveFirst();
        
            this.writeMethodInterface(null, "free", "void", "", "", "Release the objects bound to this record.", null);
            m_StreamOut.setTabs(+1);
            m_StreamOut.writeit("super.free();\n");
        
            while (recClassFields.hasNext())
            {
                recClassFields.next();
                strFieldName = recClassFields.getField(ClassFields.kClassFieldName).getString();
                String strClassFieldType = recClassFields.getField(ClassFields.kClassFieldsType).toString();
                if ((strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.CLASS_FIELD))
                    || (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.NATIVE_FIELD)))
                        if (strFieldName.length() != 0)
                    if (!recClassFields.getField(ClassFields.kClassFieldProtect).getString().equalsIgnoreCase("S"))  // Not static
                {
                    String strReference = "";
                    if (strClassFieldType.equalsIgnoreCase(ClassFieldsTypeField.CLASS_FIELD))
                        strReference = "null";
                    else
                    {
                        strReference = recClassFields.getField(ClassFields.kClassFieldInitial).getString();
                        if (strReference.length() == 0)
                        {
                            strReference = "0";
                            strFieldClass = recClassFields.getField(ClassFields.kClassFieldClass).getString();
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
            strBaseClass = recClassInfo.getField(ClassInfo.kBaseClassName).getString();
            if (strBaseClass.equalsIgnoreCase("Record"))
                strBaseClass = "Record";
            if (strBaseClass.length() == 0)
                strBaseClass = "Record";
        }
        recClassInfo.getField(ClassInfo.kBaseClassName).setString(strBaseClass);
    }
    /**
     *  getFieldData.
     */
    public void getFieldData(FieldStuff fieldStuff, boolean bThin)
    {
        FieldData recFieldData = (FieldData)this.getRecord(FieldData.kFieldDataFile);
        Record recClassInfo2 = this.readFieldClass();      // Get the field and class this is based on
        if (recClassInfo2 == null)
            fieldStuff.strFieldClass = recFieldData.getField(FieldData.kFieldClass).getString();
        else
            fieldStuff.strFieldClass = recClassInfo2.getField(ClassInfo.kClassName).getString();
        fieldStuff.strFieldName = recFieldData.getField(FieldData.kFieldName).getString();
        fieldStuff.strFieldLength = recFieldData.getField(FieldData.kMaximumLength).getString();
        if ((fieldStuff.strFieldLength.length() == 0) || (fieldStuff.strFieldLength.equalsIgnoreCase("0")))
            fieldStuff.strFieldLength = "Constants.DEFAULT_FIELD_LENGTH";
        else
            fieldStuff.strFieldLength = recFieldData.getField(FieldData.kMaximumLength).stripNonNumeric(fieldStuff.strFieldLength);
        fieldStuff.strFileFieldName = recFieldData.getField(FieldData.kFieldName).getString();
//x     fieldStuff.strFileFieldName = recFieldData.getField(FieldData.kFieldNameDB).getString();
        if (fieldStuff.strFileFieldName.length() == 0)
            fieldStuff.strFileFieldName = fieldStuff.strFieldName;
        fieldStuff.bNotNullField = recFieldData.getField(FieldData.kFieldNotNull).getState();
        fieldStuff.strFieldDesc = recFieldData.getField(FieldData.kFieldDescVertical).getString();
        fieldStuff.strFieldTip = recFieldData.getField(FieldData.kFieldDescription).getString();
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
        fieldStuff.strDataClass = recFieldData.getField(FieldData.kDataClass).toString();
        fieldStuff.bHidden = recFieldData.getField(FieldData.kHidden).getState();
        this.getBaseDataClass(fieldStuff);

        fieldStuff.strDefaultField = this.getInitField(recFieldData, true, bThin);
        if ((fieldStuff.strDefaultField == null) || (fieldStuff.strDefaultField.length() == 0))
            fieldStuff.strDefaultField = "null";
        else
            if (fieldStuff.strDefaultField.charAt(0) != '\"')
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
        FieldData recFieldData = (FieldData)this.getRecord(FieldData.kFieldDataFile);
        
        String strRecordClass = recFieldData.getField(FieldData.kFieldClass).getString();
        fieldStuff.strBaseFieldClass = null;
        try   {
            while (true)
            {
                recClassInfo2.setKeyArea(ClassInfo.kClassNameKey);
                recClassInfo2.getField(ClassInfo.kClassName).setString(strRecordClass);   // Class of this record
                if ((!recClassInfo2.seek("=")) || (strRecordClass == null) || (strRecordClass.length() == 0))
                {
                    if (fieldStuff.strBaseFieldClass == null)
                        fieldStuff.strBaseFieldClass = recFieldData.getField(FieldData.kFieldClass).getString();   // Never
                    return;
                }
                if (fieldStuff.strBaseFieldClass == null)
                {
                	String packageName = ((ClassInfo)recClassInfo2).getPackageName();
                    if (packageName.endsWith(".base.field"))
                        fieldStuff.strBaseFieldClass = recClassInfo2.getField(ClassInfo.kClassName).toString();
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
                strRecordClass = recClassInfo2.getField(ClassInfo.kBaseClassName).getString();
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
