/**
 * @(#)WriteResources.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.resource.screen;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.thread.*;
import java.io.*;
import org.jbundle.app.program.resource.db.*;
import org.jbundle.app.program.manual.util.stream.*;
import org.jbundle.app.program.db.*;
import org.jbundle.thin.base.screen.*;

/**
 *  WriteResources - Write all the resources from the resource file.
 */
public class WriteResources extends BaseProcess
{
    /**
     * Default constructor.
     */
    public WriteResources()
    {
        super();
    }
    /**
     * WriteResources Method.
     */
    public void writeResources()
    {
        StreamOut out = null;
        Record registration = this.getMainRecord();
        Resource resource = (Resource)this.getRecord(Resource.RESOURCE_FILE);
        
        String strCurrentFileName = "none";
        String strCurrentLanguage = "none";
        String strCurrentLocale = "none";
        try   {
            registration.setKeyArea(Registration.RESOURCE_ID_KEY);
            registration.close();
            boolean bFirstTime = true;
            boolean bResourceListBundle = true;
            while (registration.hasNext())
            {
                registration.next();
        
                String strFileName = resource.getField(Resource.CODE).toString();
                String strLanguage = registration.getField(Registration.LANGUAGE).toString();
                String strLocale = registration.getField(Registration.LOCALE).toString();
                
                if ((!strCurrentFileName.equals(strFileName))
                    || (!strCurrentLanguage.equals(strLanguage))
                    || (!strCurrentLocale.equals(strLocale)))
                { // New file
                    if (out != null)
                        this.printEndFile(out, strCurrentFileName, bResourceListBundle);   // End file stuff
                    out = null;
        
                    if (ResourceTypeField.PROPERTIES.equals(resource.getField(Resource.TYPE).getString()))
                        bResourceListBundle = false;
                    else
                        bResourceListBundle = true;
        
                        // Start new file
                    strCurrentFileName = strFileName;
                    strCurrentLanguage = strLanguage;
                    strCurrentLocale = strLocale;
                    String packageName = resource.getField(Resource.LOCATION).toString();
                    strFileName += "Resources";
                    if (strLanguage != null)
                        if (strLanguage.length() > 0)
                            strFileName += "_" + strLanguage;
                    if (strLocale != null)
                        if (strLocale.length() > 0)
                            strFileName += "_" + strLocale;
                    ClassProject recClassProject = (ClassProject)((ReferenceField)resource.getField(Resource.CLASS_PROJECT_ID)).getReference();
                    strFileName = strFileName.replace('.', '/');
                    String basePackageName = packageName;
                    if (basePackageName.startsWith("." + DBConstants.RES_SUBPACKAGE.substring(0, DBConstants.RES_SUBPACKAGE.length() - 1)))
                        basePackageName = basePackageName.substring(DBConstants.RES_SUBPACKAGE.length()); // Get rid of ".res".. it will be added back later
                    String strFullFileName = recClassProject.getFileName(strFileName, basePackageName, bResourceListBundle ? ClassProject.CodeType.RESOURCE_CODE : ClassProject.CodeType.RESOURCE_PROPERTIES, true, true);
                    packageName = recClassProject.getFullPackage(bResourceListBundle ? ClassProject.CodeType.RESOURCE_CODE : ClassProject.CodeType.RESOURCE_PROPERTIES, packageName);
                    out = this.createFile(strFullFileName);
                    out.writeit(this.getStartSourceCode(packageName, strFileName, resource.getField(Resource.DESCRIPTION).toString(), bResourceListBundle));
                    if (bResourceListBundle)
                    {
                        out.writeit("   static final Object[][] contents = {\n");
                        out.writeit("     // LOCALIZE THIS\n");
                    }
                    bFirstTime = true;
                }
                if (!bFirstTime)
                {
                    if (bResourceListBundle)
                        out.writeit(",\n");
                    else
                        out.writeit("\n");
                }
                bFirstTime = false;
                this.writeDetailLine(registration, out, bResourceListBundle);
            }
            if (out != null)
                this.printEndFile(out, strCurrentFileName, bResourceListBundle);   // End file stuff
            out = null;
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Run Method.
     */
    public void run()
    {
        this.writeResources();
    }
    /**
     * Open the main file.
     */
    public Record openMainRecord()
    {
        return new Registration(this);
    }
    /**
     * Open the other files.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        new Resource(this);
        new ProgramControl(this);
    }
    /**
     * Add the behaviors.
     */
    public void addListeners()
    {
        super.addListeners();
        this.getMainRecord().getField(Registration.RESOURCE_ID).addListener(new ReadSecondaryHandler(this.getRecord(Resource.RESOURCE_FILE)));
    }
    /**
     * WriteDetailLine Method.
     */
    public void writeDetailLine(Record registration, StreamOut out, boolean bResourceListBundle)
    {
        String strKey = registration.getField(Registration.KEY_VALUE).toString();
        String strValue = registration.getField(Registration.OBJECT_VALUE).toString();
        strValue = WriteResources.fixPropertyValue(strValue, bResourceListBundle);
        if (bResourceListBundle)
            out.writeit("\t{\"" + strKey + "\", " + strValue + "}");
        else
            out.writeit(WriteResources.fixPropertyKey(strKey) + "=" + strValue);
    }
    /**
     * @param out
     * @param strFileName
     * @param bResourceListBundle.
     */
    public void printEndFile(StreamOut out, String strFileName, boolean bResourceListBundle)
    {
        if (strFileName != null)
        { // This is IND of lame, I write duplication resources for the base resource. I should have a resource chain.
            Resource recResource = new Resource(this);
            try {
                recResource.getField(Resource.CODE).setString(strFileName);
                recResource.setKeyArea(Resource.CODE_KEY);
                if (recResource.seek(DBConstants.EQUALS))
                {
                    if (!recResource.getField(Resource.BASE_RESOURCE_ID).isNull())
                    { // There is an base resource - add the base resources
                        Record recBaseResource = ((ReferenceField)recResource.getField(Resource.BASE_RESOURCE_ID)).getReference();
                        Registration recBaseRegistration = new Registration(this);
                        recBaseRegistration.addListener(new SubFileFilter(recBaseResource));
                        Registration recRegistration = new Registration(this);
                        while (recBaseRegistration.hasNext())
                        {
                            recBaseRegistration.next();
                            recRegistration.addNew();
                            recRegistration.setKeyArea(Registration.CODE_KEY);
                            recRegistration.getField(Registration.RESOURCE_ID).moveFieldToThis(recResource.getField(Resource.ID));
                            recRegistration.getField(Registration.CODE).moveFieldToThis(recResource.getField(Resource.CODE));
                            recRegistration.getField(Registration.LANGUAGE).moveFieldToThis(recBaseRegistration.getField(Registration.LANGUAGE));
                            recRegistration.getField(Registration.LOCALE).moveFieldToThis(recBaseRegistration.getField(Registration.LOCALE));
                            recRegistration.getField(Registration.KEY_VALUE).moveFieldToThis(recBaseRegistration.getField(Registration.KEY_VALUE));
                            if (!recRegistration.seek(DBConstants.EQUALS))
                            { // If this base registration doesn't exist in the main registration file, add it.
                                if (bResourceListBundle)
                                    out.writeit(",\n");
                                else
                                    out.writeit("\n");
                                this.writeDetailLine(recBaseRegistration, out, bResourceListBundle);
                            }
                        }
                        recBaseRegistration.free();
                        recRegistration.free();
                    }
                }
            } catch (DBException e) {
                e.printStackTrace();
            } finally {
                recResource.free();
            }
        }
        if (bResourceListBundle)
        {
            out.writeit("");
            out.writeit("\t\t// END OF MATERIAL TO LOCALIZE\n");
            out.writeit("\t};\n");
            out.writeit("}\n");
        }
        out.free();
    }
    /**
     * CreateFile Method.
     */
    public StreamOut createFile(String strFullFileName)
    {
        StreamOut streamOut = null;
        try   {
            File file = new File(strFullFileName);
            String pathName = file.getParent();
            File fileDir = new File(pathName);
            fileDir.mkdirs();
            streamOut = new StreamOut(strFullFileName);
        } catch (IOException ex)    {
            ex.printStackTrace();
            streamOut = null;
        }
        return streamOut;
    }
    /**
     * GetStartSourceCode Method.
     */
    public String getStartSourceCode(String packagePath, String strFileName, String strDescription, boolean bResourceListBundle)
    {
        String string;
        if (bResourceListBundle)
        {
            string =
                "package " + packagePath + ";\n"
                + "/**\n"
                + " * @(#)" + strFileName + ".java  0.00 1-Jan-10 Don Corley\n"
                + " *\n"
                + " * Copyright © 2010 tourgeek.com. All Rights Reserved.\n"
                + " *   don@donandann.com\n"
                + " */\n"
                + "import java.util.*;\n"
                + "\n"
                + "/**\n"
                + " * " + strFileName + " - Resources.\n"
                + strDescription + "\n"
                + " */\n"
                + "public class " + strFileName + " extends ListResourceBundle\n"
                + "{\n"
                + "\tpublic Object[][] getContents() {\n"
                + "\t\treturn contents;\n"
                + "\t}\n";
        }
        else
            string = "# " + strFileName + " property file" + "\n" + "# " + strDescription + "\n";
        return string;
    }
    /**
     * Fix the property key.
     */
    public static String fixPropertyKey(String strKey)
    {
        strKey = strKey.replace(" ", "\\ ");
        return strKey;
    }
    /**
     * Clean up this long string and convert it to a java quoted string.
     */
    public static String fixPropertyValue(String string, boolean bResourceListBundle)
    {
        if (string == null)
            string = Constants.BLANK;
        StringBuffer strBuff = new StringBuffer();
        StringReader stringReader = new StringReader(string);
        LineNumberReader lineReader = new LineNumberReader(stringReader);
        boolean bFirstTime = true;
        String strLine;
        try   {
            while ((strLine = lineReader.readLine()) != null)
            {
                if (!bFirstTime)
                {
                    if (bResourceListBundle)
                        strBuff.append(" + \"\\n\" +" + "\n\t\t");
                    else
                        strBuff.append("\\n\\\n");
                }
                if (bResourceListBundle)
                    strBuff.append('\"');
                if (!bFirstTime)
                    if (!bResourceListBundle)
                        if (strLine.startsWith(" "))
                            strBuff.append("\\"); // Escape out the first space for properties files
                strBuff.append(WriteResources.encodeLine(strLine, bResourceListBundle));
                if (bResourceListBundle)
                    strBuff.append("\"");
                bFirstTime = false;
            }
        } catch (IOException ex)    {
            ex.printStackTrace(); // Never
        }
        return strBuff.toString();
    }
    /**
     * Encode the utf-16 characters in this line to escaped java strings.
     */
    public static String encodeLine(String string, boolean bResourceListBundle)
    {
        if (string == null)
            return string;
        for (int i = 0; i < string.length(); i++)
        {
            if (((string.charAt(i) == '\"') || (string.charAt(i) == '\\'))
             || ((!bResourceListBundle) && (string.charAt(i) == ':')))
            {   // must preceed these special characters with a "\"
                string = string.substring(0, i) + "\\" + string.substring(i);
                i++;
            }
            else if (string.charAt(i) > 127)
            {
                String strHex = "0123456789ABCDEF";
                String strOut = "\\u";
                strOut += strHex.charAt((string.charAt(i) & 0xF000) >> 12);
                strOut += strHex.charAt((string.charAt(i) & 0xF00) >> 8);
                strOut += strHex.charAt((string.charAt(i) & 0xF0) >> 4);
                strOut += strHex.charAt(string.charAt(i) & 0xF);
                string = string.substring(0, i) + strOut + string.substring(i + 1);
                i = i + strOut.length() - 1;
            }
        }
        return string;
    }

}
