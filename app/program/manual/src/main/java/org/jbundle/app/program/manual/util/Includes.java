/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util;

/**
 *  WriteJava
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.app.program.db.ClassProject;
import org.jbundle.app.program.db.ClassProject.CodeType;
import org.jbundle.app.program.manual.util.data.NameList;
import org.jbundle.app.program.manual.util.data.StreamOut;
import org.jbundle.model.DBException;


        //*******************************************************************
    //  Includes - List of all source files already included in this file
    //*******************************************************************
//  enum eIncludeType {kNormalInclude, kMethodInclude, kForwardInclude};
public class Includes extends NameList
{
    private static final long serialVersionUID = 1L;

    protected ClassInfo m_recClassInfo = null;
    protected StreamOut m_MethodOut = null;

    /**
     *
     */
    public Includes(RecordOwner recordOwner, StreamOut methodOut)
    {
        m_recClassInfo = new ClassInfo(recordOwner);      // Open the class file
        m_MethodOut = methodOut;
    }
    /**
     *  Include the source file for this class if it isn't in this source
     */
    public void addInclude(String strClassName)
    {
        try   {
            m_recClassInfo.getField(ClassInfo.kClassName).setString(strClassName);
            m_recClassInfo.setKeyArea(ClassInfo.kClassNameKey);
            if ((m_recClassInfo.seek("=")) && (strClassName != null) && (strClassName.length() > 0))
            {   // If the based record class is in another file, include it!
                String strPackage = m_recClassInfo.getPackageName();
                this.addPackage(strPackage);
            }
            else if ((strClassName.indexOf("java.") != -1) || (strClassName.indexOf("javax.") != -1)) // If a java class is explicitly included, add it!
                this.addPackage(strClassName);
            else if ((strClassName.indexOf("com.") == 0) || (strClassName.indexOf("org.") == 0)) // If a java class is explicitly included, add it!
                this.addPackage(strClassName);
        } catch (DBException ex)   {
            ex.printStackTrace();
        }
    }
    /**
     *  Include the source file for this class if it isn't in this source
     */
    public void addPackage(String strPackage)
    {
        if ((strPackage.length() == 0) || (strPackage.equals(DBConstants.ROOT_PACKAGE)))
            return;
        if (this.addName(strPackage) == false)
            return;     // This name was already included in the header
        m_MethodOut.writeit("import " + strPackage + ".*;\n");
    }
    /**
     *
     */
    public void free()
    {
        if (m_recClassInfo != null)
            m_recClassInfo.free();
        m_recClassInfo = null;
        super.free();
    }
}
