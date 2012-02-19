/**
 * @(#)MessageInitialProcess.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.app;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.thread.*;
import org.jbundle.main.msg.db.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.base.message.process.*;

/**
 *  MessageInitialProcess - .
 */
public class MessageInitialProcess extends BaseProcess
{
    /**
     * Default constructor.
     */
    public MessageInitialProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public MessageInitialProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Init Method.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
        
        this.registerInitialProcesses();
    }
    /**
     * Free Method.
     */
    public void free()
    {
        // Note, it usually not neccessary to be removed from my parent, as this is a simple process.
        try   {
            super.free();
        } catch (Exception ex)  {
            // Never
        }
    }
    /**
     * RegisterInitialProcesses Method.
     */
    public void registerInitialProcesses()
    {
        MessageProcessInfo recMessageProcessInfo = new MessageProcessInfo(this);
        try   {
             // Always register this generic processing queue.
            this.registerProcessForMessage(new BaseMessageFilter(MessageConstants.TRX_SEND_QUEUE, MessageConstants.INTERNET_QUEUE, null, null), null, null);
            recMessageProcessInfo.close();
            while (recMessageProcessInfo.hasNext())
            {
                recMessageProcessInfo.next();
                String strQueueName = recMessageProcessInfo.getQueueName(true);
                String strQueueType = recMessageProcessInfo.getQueueType(true);
                String strProcessClass = recMessageProcessInfo.getField(MessageProcessInfo.PROCESSOR_CLASS).toString();
                Map<String,Object> properties = ((PropertiesField)recMessageProcessInfo.getField(MessageProcessInfo.PROPERTIES)).getProperties();
                Record recMessageType = ((ReferenceField)recMessageProcessInfo.getField(MessageProcessInfo.MESSAGE_TYPE_ID)).getReference();
                if (recMessageType != null)
                {   // Start all processes that handle INcoming REQUESTs.
                    String strMessageType = recMessageType.getField(MessageType.CODE).toString();
                    Record recMessageInfo = ((ReferenceField)recMessageProcessInfo.getField(MessageProcessInfo.MESSAGE_INFO_ID)).getReference();
                    if (recMessageInfo != null)
                    {
                        Record recMessageInfoType = ((ReferenceField)recMessageInfo.getField(MessageInfo.MESSAGE_INFO_TYPE_ID)).getReference();
                        if (recMessageInfoType != null)
                        {
                            String strMessageInfoType = recMessageInfoType.getField(MessageInfoType.CODE).toString();
                            if (MessageInfoType.REQUEST.equals(strMessageInfoType))
                                if (MessageType.MESSAGE_IN.equals(strMessageType))
                                    if ((strQueueName != null) && (strQueueName.length() > 0))
                                        this.registerProcessForMessage(new BaseMessageFilter(strQueueName, strQueueType, null, null), strProcessClass, properties);
                        }
                    }
                }
            }
            recMessageProcessInfo.close();
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            recMessageProcessInfo.free();
        }
    }
    /**
     * RegisterProcessForMessage Method.
     */
    public void registerProcessForMessage(BaseMessageFilter messageFilter, String strProcessClass, Map<String,Object> properties)
    {
        new TrxMessageListener(messageFilter, (Application)this.getTask().getApplication(), strProcessClass, properties);   // This listener was added to the filter
        ((MessageInfoApplication)this.getTask().getApplication()).getThickMessageManager().addMessageFilter(messageFilter);
        // Note: No need to worry about cleanup... Freeing the message manager will free all these listeners.
        if (properties != null)
            if (properties.get(MessageInfoApplication.AUTOSTART) != null)
                ((MessageInfoApplication)this.getTask().getApplication()).getThickMessageManager().sendMessage(new MapMessage(new BaseMessageHeader(messageFilter.getQueueName(), messageFilter.getQueueType(), this, null), properties));
    }

}
