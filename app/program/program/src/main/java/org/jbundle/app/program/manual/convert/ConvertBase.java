package org.jbundle.app.program.manual.convert;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;


/**
 * Copyright (c) 1996 jbundle.org. All Rights Reserved.
 *  .
 *      don@tourgeek.com
 */

/**
 * Convert strings base code.
*/
public class ConvertBase extends BaseProcess
{   
    public static boolean COMPARE_ANY_STRING = true;    // Default = false;

    protected static String[][] m_strChange = {
//        {"((iDocMode & ScreenConstants.MAINT_MODE) == iDocMode & ScreenConstants.MAINT_MODE)", "((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)"},
//        {"BaseTrxMessage", "BaseMessage"},
//        {"InternalTrxMessage", "BaseMessage"},
    	{"${jcalendarbutton.groupId}", "net.sourceforge.jcalendarbutton"},
    	{"${ota.groupId}", "net.sourceforge.ota-tools"},
    	{"${jbundle.groupId}", "org.jbundle"},
    	{"${jbundle.artifactId}", "jbundle"},
    	{"${jbundle.config.groupId}", "org.jbundle.config"},
    	{"${jbundle.config.artifactId}", "jbundle-config"},
    	{"${jbundle.util.groupId}", "org.jbundle.util"},
    	{"${jbundle.util.artifactId}", "jbundle-util"},
    	{"${jbundle.util.webapp.groupId}", "org.jbundle.util.webapp"},
    	{"${jbundle.util.webapp.artifactId}", "jbundle-util-webapp"},
    	{"${jbundle.res.groupId}", "org.jbundle.res"},
    	{"${jbundle.thin.groupId}", "org.jbundle.thin"},
    	{"${jbundle.thin.base.groupId}", "org.jbundle.thin.base"},
    	{"${jbundle.thin.opt.groupId}", "org.jbundle.thin.opt"},
    	{"${jbundle.thin.app.groupId}", "org.jbundle.thin.app"},
    	{"${jbundle.base.groupId}", "org.jbundle.base"},
    	{"${jbundle.base.db.groupId}", "org.jbundle.base.db"},
    	{"${jbundle.base.screen.groupId}", "org.jbundle.base.screen"},
    	{"${jbundle.base.message.groupId}", "org.jbundle.base.message"},
    	{"${jbundle-app-groupId}", "org.jbundle.app"},
    	{"${jbundle.wsdl.groupId}", "org.jbundle.util.wsdl"},
    	{"${jbundle.wsdl.artifactId}", "jbundle-util-wsdl"},
    	{"${jbundle.config.groupId}", "org.jbundle.config"},
    	{"${tourapp.groupId}", "com.tourapp.config"},
    	{"${tourapp.thin.groupId}", "com.tourapp.thin"},
    	{"${tourapp.res.groupId}", "com.tourapp.res"},
    	{"${tourapp.tour.groupId}", "com.tourapp.tour"},
    	{"${tourapp.config.groupId}", "com.tourapp.config"},
    	{"${muffinmanager.groupId}", "jbundle.util"},
    };

    /**
     * Constructor.
     */
    public ConvertBase()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public ConvertBase(Task taskParent, Record recordMain, Map<String, Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Convert this line to the output format.
     */
    public String convertString(String string)
    {
    /**   if (string.indexOf("package") == 0)
            string = this.replaceString(string, m_strSourcePackage, m_strDestPackage);
        if (string.indexOf("import") == 0)
        {
            string = this.replaceString(string, m_strSourcePackage + ".", m_strDestPackage + ".");
            string = this.replaceString(string, "com.sun.java.swing.", "javax.swing.");
        }
    */
        if (m_strChange != null)
        {
            for (int i = 0; i < m_strChange.length; i++)
            {
                if (string.indexOf(m_strChange[i][0]) != -1)
                {
                    string = this.replaceString(string, m_strChange[i][0], m_strChange[i][1]);
                }
            }
        }
        return string;
//        return this.tabsToSpaces(string);
    }

    /**
     * Convert this line to the output format.
     */
    public String replaceString(String string, String strTarget, String strReplace)
    {
        int iIndex = 0;
        while ((iIndex = string.indexOf(strTarget, iIndex)) != -1)
        {
            char chNext = ' ';
            char chPrev = ' ';
            if (string.length() > iIndex + strTarget.length())
                chNext = string.charAt(iIndex + strTarget.length());
            if (iIndex > 1)
                chPrev = string.charAt(iIndex - 1);
            if (COMPARE_ANY_STRING)
            {   // Compare any string, not just names
                chNext = '.';
                chPrev = '.';
            }
            if (Character.isLetterOrDigit(chNext))
            { // Alphanumeric = longer than target.. don't change
                iIndex = iIndex + strTarget.length();
            }
            else if (Character.isLetterOrDigit(chPrev))
            { // Alphanumeric = longer than target.. don't change
                iIndex = iIndex + strTarget.length();
            }
            else
            { // space/other = end of char.. change
                string = string.substring(0, iIndex) + strReplace + string.substring(iIndex + strTarget.length());
                iIndex = iIndex + strReplace.length();
System.out.println("New String " + string);
            }
        }
        return string;
    }
    public static final String FOUR_SPACES = "    ";
    /**
     * Convert the tabs to spaces.
     */
    public String tabsToSpaces(String string)
    {
        int iOffset = 0;
        for (int i = 0; i < string.length(); i++)
        {
            if (string.charAt(i) == '\n')
                iOffset = i + 1;
            if (string.charAt(i) == '\t')
            {
                int iSpaces = (i - iOffset) % 4;
                if (iSpaces == 0)
                    iSpaces = 4;
                string = string.substring(0, i) + FOUR_SPACES.substring(0, iSpaces) + string.substring(i + 1);
            }
        }
        return string;
    }
    /**
     * Replace the file separators with the correct characters.
     */
    public static String fixFilePath(String source, String separator)
    {
    	if (source == null)
    		return null;
        StringBuilder dest = new StringBuilder(DBConstants.BLANK);
        for (int i = 0; i < source.length(); i++)
        {
            if (source.charAt(i) == '/')
            	dest.append(separator);
            else
            	dest.append(source.charAt(i));
        }
        return dest.toString();
    }
}
