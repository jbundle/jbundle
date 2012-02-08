/*
 * HotelRateRequestOut.java
 *
 * Created on September 26, 2003, 12:41 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.core.trx.internal;

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.message.core.trx.TrxMessageHeader;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.MapMessage;


/**
 * This is the base message for sending and receiving requests.
 * Data in this object are stored in the native java object type.
 * Data can either be extracted as the Raw object, an External object, or
 * a string:
 * Raw Object: Native java object.
 * External Object: Externally recognizable data (such as Hotel Name rather than HotelID).
 * String: External Object converted to ASCII (The conversion is specified in the DataDesc).
 * Typically, you override the rawToExternal and externlToRaw to do your conversion (none by default).
 * Also: XML: Typically External to String conversion (with tags) except for items such as dates.
 * @author  don
 */
public class ManualMessage extends MapMessage
{
    private static final long serialVersionUID = 1L;

    public static final String MANUAL_MESSAGE_CODE = "MANUAL";
    public static final String MESSAGE_PARAM = "message";
    
    /**
     * Creates a new instance of HotelRateRequestOut
     */
    public ManualMessage()
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public ManualMessage(TrxMessageHeader messageHeader, Map<String,Object> objRawMessage, String strMessage)
    {
        this();
        this.init(messageHeader, objRawMessage, strMessage);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public ManualMessage(TrxMessageHeader messageHeader, String strMessage)
    {
        this();
        this.init(messageHeader, null, strMessage);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(BaseMessageHeader messageHeader, Map<String,Object> mapRawMessage, String strMessage)
    {
        if (mapRawMessage == null)
            mapRawMessage = new HashMap<String,Object>();
        mapRawMessage.put(TrxMessageHeader.MESSAGE_CODE, ManualMessage.MANUAL_MESSAGE_CODE);
        mapRawMessage.put(ManualMessage.MESSAGE_PARAM, strMessage);  // The physical message
        super.init(messageHeader, mapRawMessage);
    }
    /**
     * Get the message data as a XML String.
     * @return The XML String.
     */
    public String getXML(boolean bIncludeHeader)
    {
        if (bIncludeHeader == false)
        {
            if (ManualMessage.MANUAL_MESSAGE_CODE.equals(this.get(TrxMessageHeader.MESSAGE_CODE)));
            {
                String strMessage = this.getString(ManualMessage.MESSAGE_PARAM);
                if (strMessage.startsWith("<"))
                    return strMessage;
            }
        }
        return super.getXML(bIncludeHeader);
    }
}
