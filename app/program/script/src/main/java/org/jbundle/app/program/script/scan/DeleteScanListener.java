/**
 * @(#)DeleteScanListener.
 * Copyright © 2013 jbundle.org. All rights reserved.
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
import java.io.*;

/**
 *  DeleteScanListener - .
 */
public class DeleteScanListener extends BaseScanListener
{
    /**
     * Default constructor.
     */
    public DeleteScanListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public DeleteScanListener(RecordOwnerParent parent, String strSourcePrefix)
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
     * Do whatever processing that needs to be done on this file.
     */
    public void moveThisFile(File fileSource, File fileDestDir, String strDestName)
    {
        fileSource.delete();
    }
    /**
     * Do whatever processing that needs to be done on this directory.
     * @return caller specific information about this directory.
     */
    public void postProcessThisDirectory(File fileDir, Object objDirID)
    {
        if (DBConstants.TRUE.equalsIgnoreCase(this.getProperty("deleteDir")))
            fileDir.delete();
    }

}
