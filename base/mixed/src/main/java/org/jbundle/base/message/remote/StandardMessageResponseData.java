/**
 *  @(#)CreateSiteResponseData.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.base.message.remote;

import org.jbundle.thin.base.message.MessageDataParent;
import org.jbundle.thin.base.message.MessageFieldDesc;
import org.jbundle.thin.base.message.MessageRecordDesc;

/**
 *  CreateSiteResponseData - .
 */
public class StandardMessageResponseData extends MessageRecordDesc
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String STANDARD_MESSAGE_RESPONSE = "StandardMessageResponse";
    public static final String MESSAGE = "Message";
    /**
     * Default constructor.
     */
    public StandardMessageResponseData()
    {
        super();
    }
    /**
     * CreateSiteResponseData Method.
     */
    public StandardMessageResponseData(MessageDataParent messageDataParent, String strKey)
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
            strKey = STANDARD_MESSAGE_RESPONSE;
        super.init(messageDataParent, strKey);
    }
    /**
     * SetErrorMessage Method.
     */
    public void setMessage(String message)
    {
        this.put(MESSAGE, message);
    }
    /**
     * Setup sub-Message Data.
     */
    public void setupMessageDataDesc()
    {
        super.setupMessageDataDesc();
        
        this.addMessageFieldDesc(MESSAGE, String.class, MessageFieldDesc.REQUIRED, null);
    }

}
