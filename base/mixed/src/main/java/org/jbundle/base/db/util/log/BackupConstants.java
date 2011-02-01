/*
 * ChatConstants.java
 *
 * Created on September 18, 2001, 5:35 AM
 */

package org.jbundle.base.db.util.log;

import org.jbundle.thin.base.message.MessageConstants;

/**
 *
 * @author  don
 * @version 
 */
public interface BackupConstants
{
    /**
     * The send button name/param.
     */
    public static final String SEND = "Send";
    /**
     * The remote queue name.
     */
    public static final String BACKUP_QUEUE_NAME = "backup";
    /**
     * 
     */
    public static final String BACKUP_QUEUE_TYPE = MessageConstants.INTERNET_QUEUE;
    /**
     * The message property param.
     */
    public static final String MESSAGE_PARAM = "message";
    /**
     * No transaction.
     */
    public static final String NOTRX = "NOTRX";
    public static final String DEFAULT_PATTERN = "trxlog_{0}.txt";
    public static final String DEFAULT_REGEX = "trxlog.*txt";
}

