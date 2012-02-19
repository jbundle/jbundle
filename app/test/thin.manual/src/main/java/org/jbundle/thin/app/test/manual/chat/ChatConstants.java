/*
 * ChatConstants.java
 *
 * Created on September 18, 2001, 5:35 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.chat;

import org.jbundle.thin.base.message.MessageConstants;

/**
 *
 * @author  don
 * @version 
 */
public interface ChatConstants
{
    /**
     * The send button name/param.
     */
    public static final String SEND = "Send";
    /**
     * The remote queue name.
     */
    public static final String CHAT_QUEUE_NAME = "chat";
    /**
     * 
     */
    public static final String CHAT_QUEUE_TYPE = MessageConstants.INTERNET_QUEUE;
    /**
     * The message property param.
     */
    public static final String MESSAGE_PARAM = "message";
    /**
     * The channel name.
     */
    public static final String TREE_NAME = "tree";
    /**
     * The channel name.
     */
    public static final String DOOR_NAME = "door";
    /**
     * The room name.
     */
    public static final String FILTER_NAME = "filter";
}

