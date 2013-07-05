/**
 * @(#)ClassInfoModel.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import java.io.*;
import java.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;

public interface ClassInfoModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String CLASS_NAME = "ClassName";
    public static final String BASE_CLASS_NAME = "BaseClassName";
    public static final String CLASS_DESC = "ClassDesc";
    public static final String CLASS_PROJECT_ID = "ClassProjectID";
    public static final String CLASS_PACKAGE = "ClassPackage";
    public static final String CLASS_SOURCE_FILE = "ClassSourceFile";
    public static final String CLASS_TYPE = "ClassType";
    public static final String CLASS_EXPLAIN = "ClassExplain";
    public static final String CLASS_HELP = "ClassHelp";
    public static final String CLASS_IMPLEMENTS = "ClassImplements";
    public static final String SEE_ALSO = "SeeAlso";
    public static final String TECHNICAL_INFO = "TechnicalInfo";
    public static final String COPY_DESC_FROM = "CopyDescFrom";

    public static final String CLASS_NAME_KEY = "ClassName";

    public static final String CLASS_SOURCE_FILE_KEY = "ClassSourceFile";

    public static final String BASE_CLASS_NAME_KEY = "BaseClassName";

    public static final String CLASS_PROJECT_ID_KEY = "ClassProjectID";
    public static final String CLASS_INFO_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassInfoScreen";
    public static final String CLASS_INFO_GRID_SCREEN_CLASS = "org.jbundle.app.program.screen.ClassInfoGridScreen";

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
