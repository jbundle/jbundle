/**
 * @(#)GetWSDLBase.
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
import org.jbundle.main.msg.db.*;
import javax.xml.bind.*;
import javax.xml.namespace.*;
import java.io.*;
import org.jbundle.thin.base.screen.*;
import org.jbundle.base.message.core.trx.*;
import org.jbundle.base.message.trx.message.external.convert.jaxb.*;

/**
 *  GetWSDLBase - .
 */
public class GetWSDLBase extends BaseProcess
{
    /**
     * Default constructor.
     */
    public GetWSDLBase()
    {
        super();
    }
    /**
     * Constructor.
     */
    public GetWSDLBase(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
     * ProcessWSDLXML Method.
     */
    public void processWSDLXML(String strWSDL)
    {
        // Override this!
    }
    /**
     * UnmarshalThisMessage Method.
     */
    public Object unmarshalThisMessage(String strXMLBody)
    {
        try {
            Reader inStream = new StringReader(strXMLBody);
        
            Object msg = this.unmarshalRootElement(inStream);
        
            inStream.close();
            
            return msg;
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
        return null;    // Error
    }
    /**
     * UnmarshalRootElement Method.
     */
    public Object unmarshalRootElement(Reader inStream) throws UnmarshalException
    {
        try {
            // create a JAXBContext capable of handling classes generated into
            // the primer.po package
            String strSOAPPackage = this.getSOAPPackage();
            if (strSOAPPackage != null)
            {
                // create an Unmarshaller
                Unmarshaller u = JaxbContexts.getJAXBContexts().getUnmarshaller(strSOAPPackage);
                
                // unmarshal a po instance document into a tree of Java content
                // objects composed of classes from the primer.po package.
                Object obj = null;
                synchronized (u)
                {
                    obj = u.unmarshal( inStream );
                }
                return obj;
            }
        //+} catch (XMLStreamException ex)  {
        //+    ex.printStackTrace();
        } catch (JAXBException ex)  {
            ex.printStackTrace();
        }
        
        return null;
    }
    /**
     * GetSOAPPackage Method.
     */
    public String getSOAPPackage()
    {
        String strPackage = null;   // Override this!
        //strPackage = (String)((TrxMessageHeader)this.getMessage().getMessageHeader()).get(SOAPMessageTransport.SOAP_PACKAGE);
        return strPackage;
    }
    /**
     * UpdateMessageProcessInfo Method.
     */
    public void updateMessageProcessInfo(String elementIn, String elementOut, String elementFault, boolean bIsSafe, String address)
    {
        MessageInfo recMessageInfo = this.getMessageInfo(elementIn);   // Note: Message IN for them is Message Out for me
        if (recMessageInfo != null)
        {
            MessageProcessInfo recMessageProcessInfo = (MessageProcessInfo)this.getRecord(MessageProcessInfo.MESSAGE_PROCESS_INFO_FILE);
            FileListener listener = new SubFileFilter(recMessageInfo);
            recMessageProcessInfo.addListener(listener);
            recMessageProcessInfo.setKeyArea(MessageProcessInfo.MESSAGE_INFO_ID_KEY);
            recMessageProcessInfo.close();
            try {
                while (recMessageProcessInfo.hasNext())
                {
                    recMessageProcessInfo.next();
                    if (!MessageType.MESSAGE_OUT.equalsIgnoreCase(((ReferenceField)recMessageProcessInfo.getField(MessageProcessInfo.MESSAGE_TYPE_ID)).getReference().getField(MessageType.CODE).toString()))
                        continue;
                    boolean safe = CreateWSDL.SAFE_DEFAULT;
                    String safeValue = ((PropertiesField)recMessageProcessInfo.getField(MessageProcessInfo.PROPERTIES)).getProperty(MessageProcessInfo.SAFE);
                    if (safeValue != null)
                        safe = Boolean.parseBoolean(safeValue);
                    if (safeValue != null)
                        if (safe != bIsSafe)
                            continue;
                    
                    Record recMessageProcessInfo2 = ((ReferenceField)recMessageProcessInfo.getField(MessageProcessInfo.REPLY_MESSAGE_PROCESS_INFO_ID)).getReference();
                    if (recMessageProcessInfo2 != null)
                        if ((recMessageProcessInfo2.getEditMode() == DBConstants.EDIT_IN_PROGRESS) || (recMessageProcessInfo2.getEditMode() == DBConstants.EDIT_CURRENT))
                        {
                            Record recMessageInfo2 = ((ReferenceField)recMessageProcessInfo2.getField(MessageProcessInfo.MESSAGE_INFO_ID)).getReference();
                            if (recMessageInfo2 != null)
                            {
                                if (elementOut != null)
                                    if (elementOut.equalsIgnoreCase(recMessageInfo2.getField(MessageInfo.CODE).getString()))
                                    {
                                        Map<String,Object> map = null;
                                        if (address != null)
                                        {
                                            map = new Hashtable<String,Object>();
                                            String site = this.getSiteFromAddress(address, null);
                                            map.put(TrxMessageHeader.DESTINATION_PARAM, site);
                                            map.put(TrxMessageHeader.DESTINATION_MESSAGE_PARAM, this.getPathFromAddress(address, site));
                                        }
                                        this.updateMessageDetail(recMessageProcessInfo, map);   // Found
                                    }
                            }
                        }
                }
            } catch (DBException e) {
                e.printStackTrace();
            } finally {
                recMessageProcessInfo.removeListener(listener, true);
            }
        }
    }
    /**
     * GetMessageInfo Method.
     */
    public MessageInfo getMessageInfo(String element)
    {
        MessageInfo recMessageInfo = (MessageInfo)this.getRecord(MessageInfo.MESSAGE_INFO_FILE);
        recMessageInfo.setKeyArea(MessageInfo.CODE_KEY);
        recMessageInfo.getField(MessageInfo.CODE).setString(element);
        try {
            if (recMessageInfo.seek(null))
            {
                return recMessageInfo;
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * AddAddressToTarget Method.
     */
    public String addAddressToTarget(String address)
    {
        if (address != null)
        {
            MessageDetailTarget messageDetailTarget = (MessageDetailTarget)this.getMainRecord();
            String site = messageDetailTarget.getProperty(TrxMessageHeader.DESTINATION_PARAM);
            site = this.getSiteFromAddress(address, site);
            if (!messageDetailTarget.setProperty(TrxMessageHeader.DESTINATION_PARAM, site))
                address = null; // This type doesn't take properties (never)
            messageDetailTarget.setProperty(TrxMessageHeader.DESTINATION_MESSAGE_PARAM, this.getPathFromAddress(address, site));
            String wsdlPath = messageDetailTarget.getProperty(TrxMessageHeader.WSDL_PATH);
            wsdlPath = this.getPathFromAddress(wsdlPath, site);
            if (wsdlPath != null)
                messageDetailTarget.setProperty(TrxMessageHeader.WSDL_PATH, wsdlPath);
        }
        return address;
    }
    /**
     * UpdateMessageDetail Method.
     */
    public void updateMessageDetail(MessageProcessInfo recMessageProcessInfo, Map<String,Object> map)
    {
        MessageDetail recMessageDetail = (MessageDetail)this.getRecord(MessageDetail.MESSAGE_DETAIL_FILE);
        MessageTransport recMessageTransport = (MessageTransport)this.getRecord(MessageTransport.MESSAGE_TRANSPORT_FILE);
        
        try {
            recMessageDetail.addNew();
            
            recMessageDetail.getField(MessageDetail.MESSAGE_PROCESS_INFO_ID).moveFieldToThis((BaseField)recMessageProcessInfo.getCounterField());
            recMessageDetail.getField(MessageDetail.MESSAGE_TRANSPORT_ID).moveFieldToThis((BaseField)recMessageTransport.getCounterField());
            if (recMessageDetail.seek(null))
                recMessageDetail.edit();
            else
                recMessageDetail.addNew();
            
            recMessageDetail.getField(MessageDetail.MESSAGE_PROCESS_INFO_ID).moveFieldToThis((BaseField)recMessageProcessInfo.getCounterField());
            recMessageDetail.getField(MessageDetail.MESSAGE_TRANSPORT_ID).moveFieldToThis((BaseField)recMessageTransport.getCounterField());
            String site = (map == null) ? null : (String)map.get(TrxMessageHeader.DESTINATION_PARAM);
            ((PropertiesField)recMessageDetail.getField(MessageDetail.PROPERTIES)).setProperty(TrxMessageHeader.DESTINATION_PARAM, site);
            String wspath = (map == null) ? null : (String)map.get(TrxMessageHeader.DESTINATION_MESSAGE_PARAM);
            ((PropertiesField)recMessageDetail.getField(MessageDetail.PROPERTIES)).setProperty(TrxMessageHeader.DESTINATION_MESSAGE_PARAM, wspath);
            
            if (recMessageDetail.getEditMode() == DBConstants.EDIT_ADD)
                recMessageDetail.add();
            else
                recMessageDetail.set();
        } catch (DBException e) {
            e.printStackTrace();
        }
    }
    /**
     * GetSiteFromAddress Method.
     */
    public String getSiteFromAddress(String url, String site)
    {
        int iStart = url.indexOf("//") + 2;
        iStart = url.indexOf('/', iStart);
        if (iStart == -1)
            iStart = url.length();
        if ((site != null) && (site.length() > 0))
            if (!url.equalsIgnoreCase(site))
                return site; // Return this site if different from URL
        return url.substring(0, iStart);
    }
    /**
     * GetPathFromAddress Method.
     */
    public String getPathFromAddress(String url, String site)
    {
        int iStart = url.indexOf("//") + 2;
        iStart = url.indexOf('/', iStart);
        if (iStart == -1)
            iStart = url.length();
        if ((site != null) && (site.length() > 0))
            if (!url.substring(0, iStart).equalsIgnoreCase(site))
                return url; // Return full path if site is different
        return url.substring(iStart);
    }

}
