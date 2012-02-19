/*
 * RecordMessage.java
 *
 * Created on June 26, 2000, 3:19 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.message.session;

import java.io.Serializable;

import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.MessageConstants;

/** 
 * A Session message header will only be delivered to the header with the matching object.
 * This is used to simplify the task of passing a message from the session up to a thin client. All you need to
 * do in the session is send a message with a SessionMessageHeader and the client will get it. Be careful to
 * specify private if you don't want to receive other messages on this queue (or don't if you want all messages).
 * Listen carefully.... this is a little complicated:
 * Three classes: ClientSessionMessageFilter, ServerSessionMessageFilter, and SessionMessageHeader
 * work together to solve the following problem:
 * A Thin client can listen on any queue, but will fail if a thick message or messageHeader is delivered to it.
 * To solve this problem, the server handles all the messages, converts them to a version a thin client can
 * read and sends that version up.
 * First, the thin client creates a ClientSessionMessageFilter and adds it to a queue that will not get thick
 * messages (usually SERVER_QUEUE_NAME).
 * Then the remote session creates a ServerSessionMessageFilter on the same queue. The ServerSessionMessageFilter
 * will only respond to messages that originate from the server session.
 * Then the remote session registers to listen for the thick message that the client wants.
 * When the remote session receives the thick message, it converts it to a Thin (usually Base) message and sends it
 * to the client by setting the header to a SessionMessageHeader and sending it to the client's queue.
 *
 * NOTE: This object is sent over the wire, so make sure you only fill it with simple,
 * serializable objects!
 * @author  Administrator
 * @version 1.0.0
 */
public class SessionMessageHeader extends BaseMessageFilter
    implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
     * The name of the private key.
     */
    protected transient Object m_objKey = null;    // Don't send this over the wire

    /**
     * Creates new RecordMessage.
     */
    public SessionMessageHeader()
    {
        super();
    }
    /**
     * Constructor.
     */
    public SessionMessageHeader(String strQueueName, String strQueueType, Object source, Object objKey)
    {
        this();
        this.init(strQueueName, strQueueType, source, objKey);
    }
    /**
     * Constructor.
     */
    public SessionMessageHeader(Object source, Object objKey)
    {
        this();
        this.init(MessageConstants.SESSION_QUEUE_NAME, MessageConstants.INTRANET_QUEUE, source, objKey);
    }
    /**
     * Constructor.
     */
    public void init(String strQueueName, String strQueueType, Object source, Object objKey)
    {
        super.init(strQueueName, strQueueType, source, null);
        m_objKey = objKey;
    }
    /**
     * Free this object.
     */
    public void free()
    {
        m_objKey = null;
        super.free();
    }
    /**
     * Get the object key (usually the remote session).
     * @return The object key.
     */
    public Object getObjectKey()
    {
        return m_objKey;
    }
}
