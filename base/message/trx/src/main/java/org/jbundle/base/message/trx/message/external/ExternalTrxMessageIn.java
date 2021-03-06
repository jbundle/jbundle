/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.trx.message.external;

import org.jbundle.base.model.DBConstants;
import org.jbundle.thin.base.message.BaseMessage;


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
public abstract class ExternalTrxMessageIn extends ExternalTrxMessage
{

    /**
      * Creates new ExternalTrxMessage.
     */
    public ExternalTrxMessageIn() 
    {
        super();
    }
    /**
      * Initialize new ExternalTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public ExternalTrxMessageIn(BaseMessage message, Object objRawMessage)
    {
        this();
        this.init(message, objRawMessage);
    }
    /**
      * Initialize new ExternalTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(BaseMessage message, Object objRawMessage)
    {
        super.init(message, objRawMessage);
    }
    /**
     * Free all the resources belonging to this class.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Convert this source message to the ECXML format.
     * Typically you do not override this method, you override the getTransformer method
     * to supply a XSLT document to do the conversion.
     * @param source The source XML document.
     */
    public int convertExternalToInternal(Object recordOwner)
    {
        if (this.getXSLTDocument() == null)
            return super.convertExternalToInternal(recordOwner);
        String strXML = this.getXML();
        strXML = this.transformMessage(strXML, null);    // Now use the XSLT document to convert this XSL document.
        boolean bSuccess = this.getMessage().setXML(strXML);
        return bSuccess ? DBConstants.NORMAL_RETURN : DBConstants.ERROR_RETURN; 
    }
}
