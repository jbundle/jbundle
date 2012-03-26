/**
 * @(#)GetWSDL.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.wsdl;

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
import org.jbundle.base.thread.*;
import java.io.*;
import org.jbundle.main.msg.db.*;
import org.jbundle.base.message.core.trx.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.main.db.base.*;

/**
 *  GetWSDL - .
 */
public class GetWSDL extends BaseProcess
{
    /**
     * Default constructor.
     */
    public GetWSDL()
    {
        super();
    }
    /**
     * Constructor.
     */
    public GetWSDL(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Open the main file.
     */
    public Record openMainRecord()
    {
        String strClassName = this.getProperty(MenuConstants.RECORD);
        Record record = Record.makeRecordFromClassName(strClassName, this);
        return record;
    }
    /**
     * Open the other files.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        new MessageInfo(this);
        new MessageProcessInfo(this);
        new MessageDetail(this);
        new ContactType(this);
        new MessageTransport(this);
    }
    /**
     * Add the behaviors.
     */
    public void addListeners()
    {
        super.addListeners();
        Record record = this.getMainRecord();
        record.setKeyArea(DBConstants.MAIN_KEY_AREA);
        record.getCounterField().setString(this.getProperty(DBConstants.OBJECT_ID));
        try {
            if (!record.seek(null))
                return; // Never;
        
            ContactType recContactType = (ContactType)this.getRecord(ContactType.CONTACT_TYPE_FILE);
            recContactType = (ContactType)recContactType.getContactType(record);
            Record recMessageDetail = this.getRecord(MessageDetail.MESSAGE_DETAIL_FILE);
            recMessageDetail.setKeyArea(MessageDetail.CONTACT_TYPE_ID_KEY);
            recMessageDetail.addListener(new SubFileFilter(recContactType.getField(ContactType.ID), MessageDetail.CONTACT_TYPE_ID, (BaseField)record.getCounterField(), MessageDetail.PERSON_ID, null, null));
        
        } catch (DBException e) {
            e.printStackTrace();
            return;
        }
        
        MessageTransport recMessageTransport = (MessageTransport)this.getRecord(MessageTransport.MESSAGE_TRANSPORT_FILE);
        recMessageTransport = recMessageTransport.getMessageTransport(MessageTransport.SOAP);   // For now - Only SOAP
    }
    /**
     * Run Method.
     */
    public void run()
    {
        Record record = this.getMainRecord();
        try {
            Writer out = new StringWriter();
            MessageDetailTarget messageDetailTarget = (MessageDetailTarget)this.getMainRecord();
            String strSite = messageDetailTarget.getProperty(TrxMessageHeader.DESTINATION_PARAM);
            String strWSDLPath = messageDetailTarget.getProperty(TrxMessageHeader.WSDL_PATH);
            strWSDLPath = this.getFullPath(strSite, strWSDLPath);
            //x strWSDLPath = "http://www.tourloco.com:8181/tour/tourapphtml?datatype=wsdl&version=b2007";
            Utility.transferURLStream(strWSDLPath, null, null, out);
            out.flush();
            out.close();
        
            record.edit();
            this.processWSDLXML(out.toString());
            record.set();
            
            if (this.getProperty(TrxMessageHeader.REGISTRY_ID) != null)    // The return Queue ID
            {
                Application app = (Application)this.getTask().getApplication();
            
                Integer intFilterID = new Integer(this.getProperty(TrxMessageHeader.REGISTRY_ID));
                TrxMessageHeader messageHeader = new TrxMessageHeader(MessageConstants.TRX_RECEIVE_QUEUE, MessageConstants.INTERNET_QUEUE, null);
                messageHeader.setRegistryIDMatch(intFilterID);
                Map<String,Object> map = new Hashtable<String,Object>();
                map.put(DBConstants.OBJECT_ID, this.getProperty(DBConstants.OBJECT_ID));
                BaseMessage message = new MapMessage(messageHeader, map);
                // Tell the sender that I've finished (not required)
                app.getMessageManager().sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DBException e) {
            e.printStackTrace();
            return;
        }
    }
    /**
     * ProcessWSDLXML Method.
     */
    public void processWSDLXML(String strWSDL)
    {
        GetWSDLBase getWSDLBase = null;
        boolean bVersion20 = true;
        if (strWSDL.lastIndexOf("definitions>") >= strWSDL.length() - 20)
            bVersion20 = false; // wsdl1.1
        if (bVersion20)
            getWSDLBase = new GetWSDL20(this, null, null);
        else
            getWSDLBase = new GetWSDL11(this, null, null);
        getWSDLBase.processWSDLXML(strWSDL);
    }
    /**
     * GetFullPath Method.
     */
    public String getFullPath(String strSite, String strPath)
    {
        if (strSite != null)
        {
            if (strPath != null)
                if (strPath.startsWith("/"))
                    strPath = strSite + strPath;
        }
        return strPath;
    }

}
