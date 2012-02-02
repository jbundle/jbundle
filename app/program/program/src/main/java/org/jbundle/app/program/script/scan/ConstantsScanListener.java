/*
 *  @(#)BaseScanListener.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.script.scan;

import org.jbundle.model.RecordOwnerParent;

/**
 *  BaseScanListener - .
 */
public class ConstantsScanListener extends BaseScanListener
{
    /**
     * Default constructor.
     */
    public ConstantsScanListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ConstantsScanListener(RecordOwnerParent parent, String strSourcePrefix)
    {
        this();
        this.init(parent, strSourcePrefix);
    }
    /**
     * Init Method.
     */
    public void init(RecordOwnerParent parent, String strSourcePrefix)
    {
        super.init(parent, strSourcePrefix);
    }
    /**
     * Free Method.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Do any string conversion on the file text.
     */
    public String convertString(String string)
    {        
        int start;
        int end = 0;
        while ((start = string.indexOf("k", end)) > 0)
        {
            end = start + 1;
            char ch = string.charAt(start - 1);
            if ((ch != '.') && (ch != ' '))
                continue;
            ch = string.charAt(start + 1);
            if (!Character.isUpperCase(ch))
                continue;
            boolean isUpper = false;
            for (; end < string.length(); end++)
            {
                 ch = string.charAt(end);
                if (!Character.isJavaIdentifierPart(ch))
                    break;
                if (!Character.isUpperCase(ch))
                    isUpper = true;
            }
            if (!isUpper) if ((end - start) >= 3)
                continue;
            if (end >= string.length())
                break;

            String newName = convertNameToConstant(string.substring(start+1, end));
            
            string = string.substring(0, start) + newName + string.substring(end);

        }
        return string;
    }

    /**
     * A utility name to convert a java name to a constant.
     * (ie., thisClassName -> THIS_CLASS_NAME)
     */
    public String convertNameToConstant(String strName)
    {
        String strConstants = "";
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
