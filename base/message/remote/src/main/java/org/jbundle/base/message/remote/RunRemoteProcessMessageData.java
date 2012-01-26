/*
 *  @(#)CreateSiteMessageData.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.remote;

import org.jbundle.base.model.DBParams;
import org.jbundle.thin.base.message.MessageDataParent;
import org.jbundle.thin.base.message.MessageFieldDesc;
import org.jbundle.thin.base.message.MessageRecordDesc;

/**
 *  CreateSiteMessageData - .
 */
public class RunRemoteProcessMessageData extends MessageRecordDesc
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String RUN_REMOTE_PROCESS = "RunRemoteProcess";
	public static final String PROCESS_CLASS_NAME = "ProcessClassName";
    /**
     * Default constructor.
     */
    public RunRemoteProcessMessageData()
    {
        super();
    }
    /**
     * CreateSiteMessageData Method.
     */
    public RunRemoteProcessMessageData(MessageDataParent messageDataParent, String strKey)
    {
        this();
        this.init(messageDataParent, strKey);
    }
    /**
     * Init Method.
     */
    public void init(MessageDataParent messageDataParent, String strKey)
    {
        if (strKey == null)
            strKey = RUN_REMOTE_PROCESS;
        super.init(messageDataParent, strKey);
    }
    /**
     * Setup sub-Message Data.
     */
    public void setupMessageDataDesc()
    {
        super.setupMessageDataDesc();
        
        this.addMessageFieldDesc(PROCESS_CLASS_NAME, String.class, MessageFieldDesc.REQUIRED, null);
        this.addMessageFieldDesc(DBParams.USER_NAME, String.class, MessageFieldDesc.REQUIRED, null);
        this.addMessageFieldDesc(DBParams.AUTH_TOKEN, String.class, MessageFieldDesc.REQUIRED, null);
    }

}
