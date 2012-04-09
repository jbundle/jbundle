/**
 * @(#)MessageDetailTarget.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.db;

import org.jbundle.base.message.core.trx.*;

/**
 *  MessageDetailTarget - .
 */
public interface MessageDetailTarget
{
    /**
     * Add general properties to this message.
     */
    public TrxMessageHeader addMessageProperties(TrxMessageHeader trxMessageHeader);
    /**
     * Get the message transport for this target.
     */
    public MessageTransport getMessageTransport(TrxMessageHeader trxMessageHeader);
    /**
     * Add the destination information to this message.
     */
    public TrxMessageHeader addDestInfo(TrxMessageHeader trxMessageHeader);
    /**
     * Get the next target in the chain.
     */
    public MessageDetailTarget getNextMessageDetailTarget();
    /**
     * Set this property in the MessageDetailTarget
     * @return false if this is not supported.
     */
    public boolean setProperty(String strKey, String strProperty);
    /**
     * Get this record property.
     */
    public String getProperty(String strKey);

}
