/**
 * @(#)ClassInfoModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import java.io.*;
import java.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;

public interface ClassInfoModel extends Rec
{

    public static final String CLASS_INFO_FILE = "ClassInfo";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.ClassInfo";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.ClassInfo";
    /**
     * Get the link that will run this class.
     */
    public String getLink();
    /**
     * Read the ClassInfoService record
     * @param recordOwner The record owner to use to create the this record AND to optionally get the classinfo.
     * @param className if non-null read this class name, if null, use the recordowner properties to figure out the class.
     * @param getRecord If true, read the record.
    .
     */
    public ClassInfoModel readClassInfo(PropertyOwner recordOwner, String className);
    /**
     * GetClassName Method.
     */
    public String getClassName();
    /**
     * GetFullClassName Method.
     */
    public String getFullClassName();
    /**
     * GetClassDesc Method.
     */
    public String getClassDesc();
    /**
     * GetClassExplain Method.
     */
    public String getClassExplain();
    /**
     * GetClassHelp Method.
     */
    public String getClassHelp();
    /**
     * GetClassType Method.
     */
    public String getClassType();
    /**
     * GetSeeAlso Method.
     */
    public String getSeeAlso();
    /**
     * GetTechnicalInfo Method.
     */
    public String getTechnicalInfo();
    /**
     * IsValidRecord Method.
     */
    public boolean isValidRecord();
    /**
     * PrintHtmlTechInfo Method.
     */
    public void printHtmlTechInfo(PrintWriter out, String strTag, String strParams, String strData);
    /**
     * PrintScreen Method.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg);

}
