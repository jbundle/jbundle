/**
 * @(#)RemoteRecordProcess.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.process;

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.DBParams;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.remote.RemoteException;

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
