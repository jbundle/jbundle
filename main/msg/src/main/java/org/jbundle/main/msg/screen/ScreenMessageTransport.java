/**
 * @(#)ScreenMessageTransport.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.screen;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.message.trx.transport.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.model.main.msg.db.*;

/**
 *  ScreenMessageTransport - This is the base class to process an external message..
 */
public class ScreenMessageTransport extends BaseMessageTransport
{
    public static final String SCREEN_SCREEN = "screen";
    /**
     * Default constructor.
     */
    public ScreenMessageTransport()
    {
        super();
    }
    /**
     * ScreenMessageTransport Method.
     */
    public ScreenMessageTransport(Task messageSenderReceiver)
    {
        this();
        this.init(messageSenderReceiver);
    }
    /**
     * Initialize class fields.
     */
    public void init(Task messageSenderReceiver)
    {
        super.init(messageSenderReceiver, null, null);
    }
    /**
     * Get the message type.
     * @return the type of message this concrete class processes.
     */
    public String getMessageTransportType()
    {
        return MessageTransportModel.SCREEN;
    }
    /**
     * Send the message and (optionally) get the reply.
     * @param nodeMessage The XML tree to send.
     * @param strDest The destination URL string.
     * @return The reply message (or null if none).
     */
    public BaseMessage sendMessageRequest(BaseMessage messageOut)
    {
        return null;    // You can't send via screen.
    }

}
