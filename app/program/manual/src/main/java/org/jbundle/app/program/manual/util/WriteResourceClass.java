/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util;

import java.util.Map;

import org.jbundle.app.program.db.ClassProject;
import org.jbundle.app.program.db.ClassResource;
import org.jbundle.app.program.db.LogicFile;
import org.jbundle.app.program.db.ProgramControl;
import org.jbundle.app.program.resource.db.ResourceTypeField;
import org.jbundle.app.program.resource.screen.WriteResources;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.app.program.db.ClassProjectModel.CodeType;


/**
 *  WriteJava - Constructor.
 */
public class WriteResourceClass extends WriteClass
{

    /**
     * Constructor.
     */
    public WriteResourceClass()
    {
        super();
    }
    /**
     * Constructor.
     */
    public WriteResourceClass(Task taskParent, Record recordMain, Map<String,Object> properties)
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
     * Get the file name.
     */
    public String getFileName(String strClassName, String strPackage, CodeType codeType, ClassProject recClassProject)
    {
    	boolean bResourceListBundle = ResourceTypeField.LIST_RESOURCE_BUNDLE.equals(this.getRecord(ProgramControl.PROGRAM_CONTROL_FILE).getField(ProgramControl.CLASS_RESOURCE_TYPE).toString());
    	codeType = bResourceListBundle ? CodeType.RESOURCE_CODE : CodeType.RESOURCE_PROPERTIES;
    	// For now, put this type of resource in the main code base
    	codeType = CodeType.THICK;
    	String strFileName = super.getFileName(strClassName, strPackage, codeType, recClassProject);
    	if (!bResourceListBundle)
    		strFileName.replace(".java", ".properties");
    	return strFileName;
    }
    /**
     *  Create the Class for this field
     */
    public void writeClass(String strClassName, CodeType codeType)
    {
        if (!this.readThisClass(strClassName))  // Get the field this is based on
            return;
    	boolean bResourceListBundle = ResourceTypeField.LIST_RESOURCE_BUNDLE.equals(this.getRecord(ProgramControl.PROGRAM_CONTROL_FILE).getField(ProgramControl.CLASS_RESOURCE_TYPE).toString());
    	String strFileName = strClassName;
    	if (!bResourceListBundle)
    		strFileName += ".properties";
        this.writeHeading(strFileName, this.getPackage(codeType), bResourceListBundle ? CodeType.RESOURCE_CODE : CodeType.RESOURCE_PROPERTIES);        // Write the first few lines of the files
    	if (bResourceListBundle)
    	{
	        this.writeIncludes(CodeType.THICK);
	        m_StreamOut.writeit("import java.util.*;\n");
	    
	        if (m_MethodNameList.size() != 0)
	            m_MethodNameList.removeAllElements();
	    
	        this.writeClassInterface();
	    
	        this.writeClassFields(CodeType.THICK);        // Write the C++ fields for this class
	
	        this.writeResources(strClassName);
	        this.writeMethodInterface(null, "getContents", "Object[][]", "", "", "Get the resource table", null);                   
	        m_StreamOut.writeit("\treturn contents;\n");
	        m_StreamOut.writeit("}\n");
	        this.writeClassMethods(CodeType.THICK);   // Write the remaining methods for this class
    	}
    	else
    	{
	        this.writeResources(strClassName);
    	}
        this.writeEndCode(bResourceListBundle);
    }
    /**
     * Write out all the data fields for this class (not file fields!)
     * Required: strClassName - Current Class
     */
    public void writeResources(String strClassName)
    {
    	boolean bResourceListBundle = ResourceTypeField.LIST_RESOURCE_BUNDLE.equals(this.getRecord(ProgramControl.PROGRAM_CONTROL_FILE).getField(ProgramControl.CLASS_RESOURCE_TYPE).toString());
	   	if (bResourceListBundle)
	   	{
	        m_StreamOut.writeit("protected Object[][] contents = {\n");
	        m_StreamOut.setTabs(+1);
	   	}
        ClassResource recClassResource = new ClassResource(this);
        recClassResource.setKeyArea(ClassResource.CLASS_NAME_KEY);
        recClassResource.addListener(new StringSubFileFilter(strClassName, ClassResource.CLASS_NAME, null, null, null, null));
        try   {
            recClassResource.close();
            while (recClassResource.hasNext())
            {
                recClassResource.next();
               	if (bResourceListBundle)
               		m_StreamOut.writeit("{\"" + recClassResource.getField(ClassResource.KEY_NAME).toString()
                            + "\"," + WriteResources.fixPropertyValue(recClassResource.getField(ClassResource.VALUE_NAME).toString(), bResourceListBundle) + "},\n");
               	else
                    m_StreamOut.writeit(recClassResource.getField(ClassResource.KEY_NAME).toString()
                        + "=" + WriteResources.fixPropertyValue(recClassResource.getField(ClassResource.VALUE_NAME).toString(), bResourceListBundle) + "\n");
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        recClassResource.free();
       	if (bResourceListBundle)
       	{
       		m_StreamOut.setTabs(-1);
       		m_StreamOut.writeit("};\n");
       	}
    }
}
