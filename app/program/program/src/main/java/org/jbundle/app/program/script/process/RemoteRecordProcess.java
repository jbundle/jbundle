/**
 * @(#)RemoteRecordProcess.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.process;

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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.thread.*;
import org.jbundle.thin.base.screen.*;
import org.jbundle.thin.base.remote.*;

/**
 *  RemoteRecordProcess - .
 */
public class RemoteRecordProcess extends BaseProcess
{
    /**
     * Default constructor.
     */
    public RemoteRecordProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public RemoteRecordProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
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
        String strClassName = this.getProperty(DBParams.RECORD);
        Record record = Record.makeRecordFromClassName(strClassName, this);
        String strCommand = this.getProperty(DBParams.COMMAND);
        Map<String,Object> properties = new HashMap<String,Object>();
        
        try {
            record.handleRemoteCommand(strCommand, properties);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (DBException e) {
            e.printStackTrace();
        }        
        record.free();
    }

}
