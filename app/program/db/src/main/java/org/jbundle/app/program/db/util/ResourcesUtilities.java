/**
 * @(#)ResourcesUtilities.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db.util;

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
import org.jbundle.base.screen.model.*;
import java.io.*;

/**
 *  ResourcesUtilities - .
 */
public class ResourcesUtilities extends Object
{
    /**
     * Default constructor.
     */
    public ResourcesUtilities()
    {
        super();
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
                strBuff.append(ResourcesUtilities.encodeLine(strLine, bResourceListBundle));
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
