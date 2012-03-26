/**
 * @(#)GetWSDL20.
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
import org.w3._2006._01.wsdl.*;
import javax.xml.namespace.*;
import javax.xml.bind.*;

/**
 *  GetWSDL20 - .
 */
public class GetWSDL20 extends GetWSDLBase
{
    /**
     * Default constructor.
     */
    public GetWSDL20()
    {
        super();
    }
    /**
     * Constructor.
     */
    public GetWSDL20(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
        JAXBElement<DescriptionType> root = (JAXBElement)this.unmarshalThisMessage(strWSDL);
        String address = this.processInterfaceTypes((DescriptionType)root.getValue(), false);
        address = this.addAddressToTarget(address);
        if (address == null)
            this.processInterfaceTypes((DescriptionType)root.getValue(), true); // Set the individual addresses
    }
    /**
     * GetSOAPPackage Method.
     */
    public String getSOAPPackage()
    {
        String strPackage = super.getSOAPPackage();
        if (strPackage == null)
            strPackage = "org.w3._2001.xmlschema:org.w3.wsdl20";
        return strPackage;
    }
    /**
     * ProcessInterfaceTypes Method.
     */
    public String processInterfaceTypes(DescriptionType descriptionType, boolean addAddress)
    {
        String allAddress = DBConstants.BLANK;
        
        for (Object nextElement : descriptionType.getImportOrIncludeOrTypes())
        {
            Object nextType = null;
            if (nextElement instanceof JAXBElement)
                nextType = ((JAXBElement)nextElement).getValue();
            // Create the service type
            if (nextType instanceof InterfaceType)
            {
                String address = this.processInterfaceType(descriptionType, (InterfaceType)nextType, addAddress);
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
     * ProcessInterfaceType Method.
     */
    public String processInterfaceType(DescriptionType descriptionType, InterfaceType interfaceType, boolean addAddress)
    {
        String interfaceName = interfaceType.getName();
        String allAddress = DBConstants.BLANK;
        
        for (Object nextElement : interfaceType.getOperationOrFaultOrFeature())
        {
            Object interfaceOperationType = null;
            if (nextElement instanceof JAXBElement)
                interfaceOperationType = ((JAXBElement)nextElement).getValue();
            if (interfaceOperationType instanceof InterfaceOperationType)
            {
                String address = this.processInterfaceOperationType(descriptionType, interfaceName, (InterfaceOperationType)interfaceOperationType, addAddress);
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
    public String processInterfaceOperationType(DescriptionType descriptionType, String interfaceName, InterfaceOperationType interfaceOperationType, boolean addAddress)
    {
        String name = interfaceOperationType.getName();
        String patern = interfaceOperationType.getPattern();
        String style = interfaceOperationType.getStyle();
        boolean bIsSafe = interfaceOperationType.isSafe();
        MessageRefType messageIn = null;
        MessageRefType messageOut = null;
        MessageRefType messageFault = null;
        
        for (Object nextElement : interfaceOperationType.getInputOrOutputOrInfault())
        {
            Object inputOrOutputType = null;
            if (nextElement instanceof JAXBElement)
                inputOrOutputType = ((JAXBElement)nextElement).getValue();
            if (inputOrOutputType instanceof MessageRefType)
            {
                if ("In".equalsIgnoreCase(((MessageRefType)inputOrOutputType).getMessageLabel()))
                    messageIn = (MessageRefType)inputOrOutputType;
                else if ("Out".equalsIgnoreCase(((MessageRefType)inputOrOutputType).getMessageLabel()))
                    messageOut = (MessageRefType)inputOrOutputType;
                else
                    messageFault = (MessageRefType)inputOrOutputType;
            }
        }
        
        BindingType binding = this.getBindingFromInterfaceName(descriptionType, interfaceName);
        
        EndpointType endpointType = this.getServiceEndpointFromInterfaceNBinding(descriptionType, interfaceName, binding);
        
        String address = endpointType.getAddress();
        
        // Note: Here I should probably go through the types and find the type, but I just do the quick and dirty
        String element = messageIn.getElement();
        String messageLabel = messageIn.getMessageLabel();
        String elementOut = messageOut.getElement();
        
        this.updateMessageProcessInfo(element, elementOut, null, bIsSafe, addAddress ? address : null);
        
        return address;
    }
    /**
     * GetBindingFromInterfaceName Method.
     */
    public BindingType getBindingFromInterfaceName(DescriptionType descriptionType, String interfaceName)
    {
        for (Object nextElement : descriptionType.getImportOrIncludeOrTypes())
        {
            Object nextType = null;
            if (nextElement instanceof JAXBElement)
                nextType = ((JAXBElement)nextElement).getValue();
            // Create the service type
            if (nextType instanceof BindingType)
            {
                QName qname = ((BindingType)nextType).getInterface();
                if (interfaceName.equalsIgnoreCase(qname.getLocalPart()))
                    return (BindingType)nextType;
            }
        }
        return null;
    }
    /**
     * GetServiceEndpointFromInterfaceNBinding Method.
     */
    public EndpointType getServiceEndpointFromInterfaceNBinding(DescriptionType descriptionType, String interfaceName, BindingType binding)
    {
        for (Object nextElement : descriptionType.getImportOrIncludeOrTypes())
        {
            Object nextType = null;
            if (nextElement instanceof JAXBElement)
                nextType = ((JAXBElement)nextElement).getValue();
            // Create the service type
            if (nextType instanceof ServiceType)
            {
                QName qname = ((ServiceType)nextType).getInterface();
                if (qname != null)
                    if (interfaceName.equalsIgnoreCase(qname.getLocalPart()))
                {
                    for (Object nextElement2 : ((ServiceType)nextType).getEndpointOrFeatureOrProperty())
                    {
                        Object endpoint = null;
                        if (nextElement2 instanceof JAXBElement)
                            endpoint = ((JAXBElement)nextElement2).getValue();
                        if (endpoint instanceof EndpointType)
                        {
                            EndpointType endpointType = (EndpointType)endpoint;
                            String address = endpointType.getAddress();
                            QName qname2 = endpointType.getBinding();
                            String ns = qname2.getNamespaceURI();
                            String name = qname2.getLocalPart();
                            if (name.equalsIgnoreCase(binding.getName()))
                                return endpointType;
                        }
                    }
                }
            }
        }
        return null;
    }

}
