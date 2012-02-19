/*
 * MessageConstants.java
 *
 * Created on June 29, 2000, 3:44 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.message;

/** 
 * Usefull constants for messaging.
 * @author  Administrator
 * @version 1.0.0
 */
public interface MessageConstants
{
    public static final String QUEUE_TYPE = "queueType";
    public static final String QUEUE_NAME = "queueName";
    public static final String RETURN_QUEUE_NAME = "returnQueueName";

    public static final String LOCAL_QUEUE = "local";         // Local JVM
    public static final String INTRANET_QUEUE = "intranet";   // Intranet
    public static final String INTERNET_QUEUE = "internet";   // Internet

    public static final String DEFAULT_QUEUE = INTRANET_QUEUE;   // Default queue = Internet

    public static final String RECORD_QUEUE_NAME = "recordQueue";   // Queue for record messages
    public static final String SESSION_QUEUE_NAME = "sessionQueue"; // A private Queue for client-server messages

    public static final String TRX_SEND_QUEUE = "trxSend";          // The generic queue for remote sent transaction messages.
    public static final String TRX_RECEIVE_QUEUE = "trxReceive";    // The generic queue for received remote transaction messages.
    public static final String TRX_RETURN_QUEUE = "trxReceive";     // A Queue for private remote transaction return messages.

    public static final String MESSAGE_TYPE_PARAM = "messageType";  // This is a common property to pass

    public static final String CLASS_NAME = "className";            // Param to pass class name
    
    public static final int NAME = 0;
    public static final int VALUE = 1;
    
    public static final String GRID_FILTER = "GridRecordMessageFilter";
    public static final String RECORD_FILTER = "RecordMessageFilter";
    /**
     * The type of message filter to create (Tree or regular [sequential]).
     */
    public static final String MESSAGE_FILTER = "messageFilter";
    /**
     * A remote type file contains live data that should always be read from the source.
     */
    public static final String TREE_FILTER = "tree";
}
