/**
 *  @(#)MessageDetailTarget.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.main.msg.db;

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
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.base.message.trx.message.*;

/**
 *  MessageDetailTarget - .
 */
public interface MessageDetailTarget
{
    /**
     * Add general properties to this message.
     */
    public TrxMessageHeader addMessageProperties(TrxMessageHeader trxMessageHeader);    /**
     * Get the message transport for this target.
     */
    public MessageTransport getMessageTransport(TrxMessageHeader trxMessageHeader);    /**
     * Add the destination information to this message.
     */
    public TrxMessageHeader addDestInfo(TrxMessageHeader trxMessageHeader);    /**
     * Get the next target in the chain.
     */
    public MessageDetailTarget getNextMessageDetailTarget();    /**
     * Set this property in the MessageDetailTarget
     * @return false if this is not supported.
     */
    public boolean setProperty(String strKey, String strProperty);    /**
     * Get this record property.
     */
    public String getProperty(String strKey);
}
