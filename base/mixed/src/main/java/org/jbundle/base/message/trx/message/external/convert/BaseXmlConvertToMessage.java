/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external.convert;

import org.jbundle.base.message.trx.message.external.ExternalTrxMessageIn;

/**
 * This is the base class for a transaction which is sent externally.
 * The two main sub-classes of this class are InternalTrxMessage and ExternalTrxMessage.
 * An InternalTrxMessage is the data I create internally to send to the destination. It
 * usually contains all the relative information needed to send to the destination.
 * An ExternalTrxMessage is the message converted to a format that the receiver can
 * understand (such as ebXML).
 * @author  don
 * @version 
 */
public class BaseXmlConvertToMessage extends BaseConvertToMessage
{

    /**
      * Creates new BaseTrxMessage
     */
    public BaseXmlConvertToMessage() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public BaseXmlConvertToMessage(ExternalTrxMessageIn message)
    {
        this();
        this.init(message);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(ExternalTrxMessageIn message)
    {
        super.init(message);
    }
}
