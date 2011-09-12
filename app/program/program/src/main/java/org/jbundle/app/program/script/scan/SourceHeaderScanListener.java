/**
* @(#)SourceHeaderScanListener.
* Copyright © 2011 tourapp.com. All rights reserved.
*/

package org.jbundle.app.program.script.scan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;

import org.jbundle.model.RecordOwnerParent;

/**
 *  SourceHeaderScanListener - .
 */
public class SourceHeaderScanListener extends BaseScanListener
{
    /**
     * Default constructor.
     */
    public SourceHeaderScanListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public SourceHeaderScanListener(RecordOwnerParent parent, String strSourcePrefix)
    {
        this();
        this.init(parent, strSourcePrefix);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent parent, String strSourcePrefix)
    {
        super.init(parent, strSourcePrefix);
    }
    /**
     * If this file should be processed, return true.
     */
    public boolean filterFile(File file)
    {
        String strName = file.getName();
        if (!strName.endsWith(".java"))
            return false;
        return super.filterFile(file);
    }
    /**
     * Do whatever processing that needs to be done on this file.
     */
    public void moveThisFile(File fileSource, File fileDestDir, String strDestName)
    {
    	foundComment = false;
    	foundPackage = false;
    	beforePackage = "";
    	super.moveThisFile(fileSource, fileDestDir, strDestName);
    }
    boolean foundComment = false;
    boolean foundPackage = false;
    String beforePackage = "";
	String lineSeparator = (String) java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));
    /**
     * MoveSourceToDest Method.
     */
    public void moveSourceToDest(LineNumberReader reader, PrintWriter dataOut)
    {
        try {
            String string;
            while ((string = reader.readLine()) != null)
            {
            	if (string.indexOf("/*") != -1)
            		foundComment = true;
            	if (!foundPackage)
            	{
            		if (string.indexOf("package") == -1)
	            	{
            			if (string.indexOf("/*") != -1)
            				continue;
            			if (string.indexOf("*/") != -1)
            				continue;
            			if (string.indexOf("opyright") != -1)
            				continue;
	            		beforePackage = beforePackage + this.convertString(string) + lineSeparator;
	            		continue;
	            	}
            		else
	            	{
	            		foundPackage = true;
	                    dataOut.write("/*" + lineSeparator);
	            		dataOut.write(beforePackage);
	                    dataOut.write(" * Copyright © 2011 jbundle.org. All rights reserved." + lineSeparator);
	                    dataOut.write(" */" + lineSeparator);
	            	}
            	}
                string = this.convertString(string);
                if (string != null)
                {
                    dataOut.write(string + lineSeparator);
//x                    dataOut.println();
                }
            }
        	if (!foundPackage)
        		dataOut.write(beforePackage);
        } catch (FileNotFoundException ex)  {
            ex.printStackTrace();
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Do any string conversion on the file text.
     */
    public String convertString(String string)
    {
        return string;
    }
}
