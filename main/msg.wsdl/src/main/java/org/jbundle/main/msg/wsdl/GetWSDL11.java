/**
 * @(#)GetWSDL11.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.wsdl;

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
import org.xmlsoap.schemas.wsdl.*;
import org.w3c.dom.*;
import javax.xml.namespace.*;
import javax.xml.bind.*;

/**
 *  GetWSDL11 - .
 */
public class GetWSDL11 extends GetWSDLBase
{
    /**
     * Default constructor.
     */
    public GetWSDL11()
    {
        super();
    }
    /**
     * Constructor.
     */
    public GetWSDL11(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
        JAXBElement<TDefinitions> root = (JAXBElement)this.unmarshalThisMessage(strWSDL);
        String address = this.processInterfaceTypes((TDefinitions)root.getValue(), false);
        address = this.addAddressToTarget(address);
        if (address == null)
            this.processInterfaceTypes((TDefinitions)root.getValue(), true); // Set the individual addresses
    }
    /**
     * GetSOAPPackage Method.
     */
    public String getSOAPPackage()
    {
        String strPackage = super.getSOAPPackage();
        if (strPackage == null)
            strPackage = "org.w3._2001.xmlschema:org.w3.wsdl11";
        return strPackage;
    }
    /**
     * ProcessInterfaceTypes Method.
     */
    public String processInterfaceTypes(TDefinitions descriptionType, boolean addAddress)
    {
        String allAddress = DBConstants.BLANK;
        
        for (TDocumented nextElement : descriptionType.getAnyTopLevelOptionalElement())
        {
            // Create the service type
            if (nextElement instanceof TPortType)
            {
                String address = this.processPortType(descriptionType, (TPortType)nextElement, addAddress);
                if (allAddress != null)
                {
                    if (allAddress == DBConstants.BLANK)
                        allAddress = address;
                    else if (!allAddress.equalsIgnoreCase(address))
                        allAddress = null;  // null = not all address are the same
                }
            }
        }
        
        return allAddress;
    }
    /**
     * ProcessPortType Method.
     */
    public String processPortType(TDefinitions descriptionType, TPortType interfaceType, boolean addAddress)
    {
        String interfaceName = interfaceType.getName();
        String allAddress = DBConstants.BLANK;
        
        for (TOperation nextElement : interfaceType.getOperation())
        {
            {
                String address = this.processInterfaceOperationType(descriptionType, interfaceName, nextElement, addAddress);
                if (allAddress != null)
                {
                    if (allAddress == DBConstants.BLANK)
                        allAddress = address;
                    else if (!allAddress.equalsIgnoreCase(address))
                        allAddress = null;  // null = not all address are the same
                }
            }
        }
        
        return allAddress;
    }
    /**
     * ProcessInterfaceOperationType Method.
     */
    public String processInterfaceOperationType(TDefinitions descriptionType, String interfaceName, TOperation interfaceOperationType, boolean addAddress)
    {
        String name = interfaceOperationType.getName();
        TParam messageIn = null;
        TParam messageOut = null;
        TParam messageFault = null;
        
        for (Object nextElement : interfaceOperationType.getRest())
        {
            Object inputOrOutputType = null;
            if (nextElement instanceof JAXBElement)
                inputOrOutputType = ((JAXBElement)nextElement).getValue();
            if (inputOrOutputType instanceof TParam)
            {
                String strName = ((JAXBElement)nextElement).getName().getLocalPart();
                if ("Input".equalsIgnoreCase(strName))
                    messageIn = (TParam)inputOrOutputType;
                else if ("Output".equalsIgnoreCase(strName))
                    messageOut = (TParam)inputOrOutputType;
                else
                    messageFault = (TParam)inputOrOutputType;
            }
        }
        
        TBinding binding = this.getBindingFromInterfaceName(descriptionType, interfaceName);
        
        String address = this.getServiceEndpointFromInterfaceNBinding(descriptionType, name, binding);
        boolean bIsSafe = true;
        // Note: Here I should probably go through the types and find the type, but I just do the quick and dirty
        String element = this.getElementNameFromMessageName(descriptionType, messageIn);
        String elementOut = this.getElementNameFromMessageName(descriptionType, messageOut);
        
        this.updateMessageProcessInfo(element, elementOut, null, bIsSafe, addAddress ? address : null);
        
        return address;
    }
    /**
     * Search through the messages for this one and return the element name.
     */
    public String getElementNameFromMessageName(TDefinitions descriptionType, TParam message)
    {
        QName qName = message.getMessage();
        String name = qName.getLocalPart();
        for (TDocumented nextElement : descriptionType.getAnyTopLevelOptionalElement())
        {
            // Create the service type
            if (nextElement instanceof TMessage)
            {
                String msgName = ((TMessage)nextElement).getName();
                for (TPart part : ((TMessage)nextElement).getPart())
                {
                    if (name.equals(part.getName()))
                    {
                        return part.getElement().getLocalPart();
                    }
                }
            }
        }
        return null;
    }
    /**
     * GetBindingFromInterfaceName Method.
     */
    public TBinding getBindingFromInterfaceName(TDefinitions descriptionType, String interfaceName)
    {
        for (TDocumented nextElement : descriptionType.getAnyTopLevelOptionalElement())
        {
            // Create the service type
            if (nextElement instanceof TBinding)
            {
                QName qname = ((TBinding)nextElement).getType();
                if (interfaceName.equalsIgnoreCase(qname.getLocalPart()))
                    return (TBinding)nextElement;
            }
        }
        return null;
    }
    /**
     * GetServiceEndpointFromInterfaceNBinding Method.
     */
    public String getServiceEndpointFromInterfaceNBinding(TDefinitions descriptionType, String interfaceName, TBinding binding)
    {
        for (TBindingOperation nextElement : binding.getOperation())
        {
            String name = nextElement.getName();
            if (interfaceName.equalsIgnoreCase(name))
            {
                for (Object op : nextElement.getAny())
                {
                    if (op instanceof org.w3c.dom.Element)
                    {
                        String strName = ((org.w3c.dom.Element)op).getLocalName();
                        if ("operation".equalsIgnoreCase(strName))
                        {
                            String ops = ((org.w3c.dom.Element)op).getAttribute("soapAction");
                            if (ops != null)
                                return ops;
                        }
                    }                    
                }
            }
        }
        // Not found in there, return the default address
        for (TDocumented nextElement : descriptionType.getAnyTopLevelOptionalElement())
        {
            // Create the service type
            if (nextElement instanceof TService)
            {
                for (TPort port : ((TService)nextElement).getPort())
                {
                    String portBindingName = port.getBinding().getLocalPart();
                    String bindingName = binding.getName();
                    if (bindingName.equals(portBindingName))
                    {
                        for (Object op : port.getAny())
                        {
                            if (op instanceof org.w3c.dom.Element)
                            {
                                String strName = ((org.w3c.dom.Element)op).getLocalName();
                                if ("address".equalsIgnoreCase(strName))
                                {
                                    String ops = ((org.w3c.dom.Element)op).getAttribute("location");
                                    if (ops != null)
                                        return ops;
                                }
                            }                            
                        }
                    }
                }
            }
        }
        return null;
    }

}
