/**
 * @(#)XslRepositoryScanListener.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.scan;

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

/**
 *  XslRepositoryScanListener - .
 */
public class XslRepositoryScanListener extends BaseScanListener
{
    /**
     * Default constructor.
     */
    public XslRepositoryScanListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XslRepositoryScanListener(RecordOwnerParent parent, String strSourcePrefix)
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
     * Do any string conversion on the file text.
     */
    public String convertString(String string)
    {
        if (string.indexOf("<referral") != -1)
        {
            int iStart = string.indexOf("url=\"") + 5;
            int iEnd = string.indexOf('\"', iStart);
            if ((iStart != 4) && (iEnd != -1))
            {
                String filename = string.substring(iStart, iEnd);
                if (filename.indexOf(":/") == -1)
                {
                    String stringFile = readFile(filename);
                    
                    if (stringFile != null)
                        string = "<!-- " + "Repository inlined from: " + filename  + " -->"
                            + "\n" + stringFile + "\n";
                }        
            }
        }
        return string;
    }
    /**
     * ReadFile Method.
     */
    public String readFile(String filename)
    {
        String path = m_fileSource.getParent();
        String string = Utility.transferURLStream("file:" + path + '/' + filename, null, null, null);
        if (string != null)
        {
            int start = string.indexOf("<repository");
            if (start != -1)
                start = string.indexOf(">", start);
            int end = string.lastIndexOf("</repository>");
            if ((start != -1) && (end != -1))
                string = string.substring(start + 1, end);
        }
        return string;
    }

}
