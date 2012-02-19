/**
 * @(#)ScanListener.
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
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import java.io.*;

/**
 *  ScanListener - .
 */
public interface ScanListener extends RecordOwnerParent, Freeable
{
    /**
     * If this file should be processed, return true.
     */
    public boolean filterFile(File file);
    /**
     * Do whatever processing that needs to be done on this file.
     */
    public void moveThisFile(File file, File fileDestDirectory, String strDestFilename);
    /**
     * If this file should be processed, return true.
     */
    public boolean filterDirectory(File file);
    /**
     * Do whatever processing that needs to be done on this directory.
     * @param objDirID The parent object id
     * @return The new parent object id.
     */
    public Object processThisDirectory(File file, Object objDirID);
    /**
     * Do whatever processing that needs to be done on this directory after processing the files.
     * @param objDirID The parent object id
     * @return The new parent object id.
     */
    public void postProcessThisDirectory(File fileDir, Object objDirID);

}
