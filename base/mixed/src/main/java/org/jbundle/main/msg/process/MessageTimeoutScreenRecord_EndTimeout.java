/**
 *  @(#)MessageTimeoutScreenRecord_EndTimeout.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.main.msg.process;

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

/**
 *  MessageTimeoutScreenRecord_EndTimeout - .
 */
public class MessageTimeoutScreenRecord_EndTimeout extends DateTimeField
{
    /**
     * Default constructor.
     */
    public MessageTimeoutScreenRecord_EndTimeout()
    {
        super();
    }
    /**
     * MessageTimeoutScreenRecord_EndTimeout Method.
     */
    public MessageTimeoutScreenRecord_EndTimeout(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize the field.
     */
    public int initField(boolean displayOption)
    {
        return this.setValue(currentTime(), displayOption, DBConstants.INIT_MOVE);
    }

}
