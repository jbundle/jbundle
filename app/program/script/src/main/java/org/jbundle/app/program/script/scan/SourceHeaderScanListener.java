/**
 * @(#)SourceHeaderScanListener.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.scan;

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
import java.io.*;

/**
 *  SourceHeaderScanListener - Add source header to all java files.
 */
public class SourceHeaderScanListener extends BaseScanListener
{
    protected String beforePackage;
    protected boolean foundComment;
    protected boolean foundPackage;
    protected String lineSeparator;
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
        beforePackage = "";
        foundComment = false;
        foundPackage = false;
        lineSeparator = (String) java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));;
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

}
