/*
 *  @(#)BaseSetupSiteProcess.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.remote;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.message.BaseMessage;

/**
 *  BaseMessageProcess - Process this message and supply a return message.
 */
public class BaseMessageProcess extends BaseProcess
{
    /**
     * Default constructor.
     */
    public BaseMessageProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public BaseMessageProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
     * Process this message and return a reply.
     */
    public BaseMessage processMessage(BaseMessage message)
    {
    	return null;	// Override this
    }
}
