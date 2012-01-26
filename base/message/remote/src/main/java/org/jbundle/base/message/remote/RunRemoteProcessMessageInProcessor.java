/*
 *  @(#)PingRequestMessageInProcessor.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.remote;

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.message.trx.processor.BaseMessageInProcessor;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.util.osgi.finder.ClassServiceUtility;

/**
 *  PingRequestMessageInProcessor - .
 */
public class RunRemoteProcessMessageInProcessor extends BaseMessageInProcessor
{
    /**
     * Default constructor.
     */
    public RunRemoteProcessMessageInProcessor()
    {
        super();
    }
    /**
     * Constructor.
     */
    public RunRemoteProcessMessageInProcessor(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
     * Process this internal message.
     * @param internalMessage The message to process.
     * @return (optional) The return message if applicable.
     */
    public BaseMessage processMessage(BaseMessage internalMessage)
    {
        if (internalMessage.getMessageDataDesc(null) == null)
            internalMessage.addMessageDataDesc(new RunRemoteProcessMessageData(null, null));
        
        RunRemoteProcessMessageData runRemoteProcessMessageData = (RunRemoteProcessMessageData)internalMessage.getMessageDataDesc(null);
        String processClassName = (String)runRemoteProcessMessageData.get(RunRemoteProcessMessageData.PROCESS_CLASS_NAME);
        if (processClassName == null)
        	return null;
        
        BaseProcess process = null;
        Task task = this.getTask();
        Map<String,Object> properties = new HashMap<String,Object>();
        MapMessage.convertDOMtoMap(internalMessage.getDOM(), properties, true);
        process = (BaseProcess)ClassServiceUtility.getClassService().makeObjectFromClassName(processClassName);
        if (process == null)
        	return null;
        process.init(task, null, properties);
        
        BaseMessage messageReply = null;
        if (process instanceof BaseMessageProcess)
        	messageReply = ((BaseMessageProcess)process).processMessage(internalMessage);
        else
        	process.run();
      
        return messageReply;
    }

}
