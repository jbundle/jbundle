/**
 * @(#)CreateWSDL20.
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
import org.w3._2006._01.wsdl.*;
import org.jbundle.main.msg.db.*;
import java.io.*;
import javax.xml.bind.*;
import javax.xml.namespace.*;
import org.w3._2001.xmlschema.*;

/**
 *  CreateWSDL20 - .
 */
public class CreateWSDL20 extends CreateWSDL
{
    public static String[][] DEFAULTS_2_0 = {
        {MessageControl.WSDL_NAMESPACE_URI, "/wsdl"},
        {MessageControl.SERVICE_NAME, "openTravelService"},
        {MessageControl.INTERFACE_NAME, "openTravelInterface"},
        {MessageControl.BINDING_NAME, "openTravelBinding"},
        {MessageControl.ENDPOINT_NAME, "openTravelEndpoint"},
        {MessageControl.MESSAGE_SUFFIX, "Message"},
        {WSOAP_BINDING_URI,"http://www.w3.org/ns/wsdl/soap"},
        {SOAP_SENDING_URI, "http://www.w3.org/2003/05/soap-envelope"},
        {SCHEMA_BINDING_URI, "http://www.w3.org/2001/XMLSchema"},
        {MESSAGE_PATTERN_URI, "http://www.w3.org/ns/wsdl/in-out"},
        {MESSAGE_STYLE_URI, "http://www.w3.org/ns/wsdl/style/iri"},
        {SOAP_URI, "http://www.w3.org/2003/05/soap/bindings/HTTP/"},
        {SOAP_RESPONSE_URI, "http://www.w3.org/2003/05/soap/mep/soap-response"},
    };
    protected org.w3._2006._01.wsdl.ObjectFactory wsdlFactory = null;
    /**
     * Default constructor.
     */
    public CreateWSDL20()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CreateWSDL20(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
        wsdlFactory = null;
        DEFAULTS = DEFAULTS_2_0;
    }
    /**
     * GetSOAPPackage Method.
     */
    public String getSOAPPackage()
    {
        return "org.w3._2006._01.wsdl";//org.w3._2001.xmlschema: (String)((TrxMessageHeader)this.getMessage().getMessageHeader()).get(SOAPMessageTransport.SOAP_PACKAGE);
    }
    /**
     * CreateMarshallableObject Method.
     */
    public Object createMarshallableObject()
    {
        wsdlFactory = new org.w3._2006._01.wsdl.ObjectFactory();
        
        // create a wsdl object
        DescriptionType descriptionType = wsdlFactory.createDescriptionType();
        JAXBElement<DescriptionType> root = wsdlFactory.createDescription(descriptionType);
        String targetNamespace = this.getNamespace();  // Location of this document
        descriptionType.setTargetNamespace(targetNamespace);
        
        // Create the service type
        this.addServiceType(descriptionType);
        
        // Create the bindings type
        this.addBindingType(descriptionType);
        
        // Create the interface type
        this.addInterfaceType(descriptionType);
        
        // Create the types (import the OTA specs)
        this.addTypeTypes(descriptionType);
        
        return root;
    }
    /**
     * AddServiceType Method.
     */
    public void addServiceType(DescriptionType descriptionType)
    {
        String interfacens;
        String interfacename;
        QName qname;
        String name;
        
        ServiceType serviceType = wsdlFactory.createServiceType();
        descriptionType.getImportOrIncludeOrTypes().add(wsdlFactory.createService(serviceType));
        
        name = this.getControlProperty(MessageControl.SERVICE_NAME);
        serviceType.setName(name);
        interfacens = this.getNamespace();;
        interfacename = this.getControlProperty(MessageControl.INTERFACE_NAME);
        qname = new QName(interfacens, interfacename);
        serviceType.setInterface(qname);
        EndpointType endpointType = wsdlFactory.createEndpointType();
        serviceType.getEndpointOrFeatureOrProperty().add(wsdlFactory.createEndpoint(endpointType));
        String address = this.getURIValue(this.getRecord(MessageControl.MESSAGE_CONTROL_FILE).getField(MessageControl.WEB_SERVICES_SERVER).toString());  // Important - This is the web services URL
        endpointType.setAddress(address);
        interfacens = this.getNamespace();;
        interfacename = this.getControlProperty(MessageControl.BINDING_NAME);
        qname = new QName(interfacens, interfacename);
        endpointType.setBinding(qname);
        name = this.getControlProperty(MessageControl.ENDPOINT_NAME);
        endpointType.setName(name);
    }
    /**
     * AddBindingType Method.
     */
    public void addBindingType(DescriptionType descriptionType)
    {
        String interfacens;
        String interfacename;
        QName qname;
        String value;
        String name;
        
        // Create the bindings type
        BindingType bindingType = wsdlFactory.createBindingType();
        descriptionType.getImportOrIncludeOrTypes().add(wsdlFactory.createBinding(bindingType));
        name = this.getControlProperty(MessageControl.BINDING_NAME);
        bindingType.setName(name);
        interfacens = this.getNamespace();
        interfacename = this.getControlProperty(MessageControl.INTERFACE_NAME);
        qname = new QName(interfacens, interfacename);
        bindingType.setInterface(qname);
        bindingType.setType(this.getControlProperty(WSOAP_BINDING_URI));
        interfacens = this.getControlProperty(SOAP_SENDING_URI);
        interfacename = "protocol";
        qname = new QName(interfacens, interfacename);
        value = this.getControlProperty(SOAP_URI);
        bindingType.getOtherAttributes().put(qname, value);
        
        this.addBindingOperationTypes(bindingType);
    }
    /**
     * AddBindingOperationTypes Method.
     */
    public void addBindingOperationTypes(BindingType bindingType)
    {
        this.scanProcesses(bindingType, OperationType.BINDING_OPERATIONS);
    }
    /**
     * AddBindingOperationType Method.
     */
    public void addBindingOperationType(String strVersion, BindingType bindingType, MessageProcessInfo recMessageProcessInfo)
    {
        String interfacens;
        String interfacename;
        QName qname;
        String value;
        
        BindingOperationType bindingOperationType = wsdlFactory.createBindingOperationType();
        bindingType.getOperationOrFaultOrFeature().add(wsdlFactory.createBindingTypeOperation(bindingOperationType));
        interfacens = this.getNamespace();
        interfacename = this.fixName(recMessageProcessInfo.getField(MessageProcessInfo.DESCRIPTION).toString());
        qname = new QName(interfacens, interfacename);
        bindingOperationType.setRef(qname);
        interfacens = this.getURIProperty(WSOAP_BINDING_URI);
        interfacename = "code";
        qname = new QName(interfacens, interfacename);
        value = this.getURIProperty(SOAP_RESPONSE_URI);
        bindingOperationType.getOtherAttributes().put(qname, value);
        BindingOperationFaultType bindingOperationFaultType = wsdlFactory.createBindingOperationFaultType();
        bindingOperationFaultType.setRef(qname);
        interfacens = this.getURIProperty(SOAP_SENDING_URI);
        interfacename = "mep";
        qname = new QName(interfacens, interfacename);
        value = this.getURIProperty(SOAP_RESPONSE_URI);
        bindingOperationType.getOtherAttributes().put(qname, value);
    }
    /**
     * AddInterfaceType Method.
     */
    public void addInterfaceType(DescriptionType descriptionType)
    {
        // Create the interfaces
        InterfaceType interfaceType = wsdlFactory.createInterfaceType();
        descriptionType.getImportOrIncludeOrTypes().add(wsdlFactory.createInterface(interfaceType));
        String interfaceName = this.getControlProperty(MessageControl.INTERFACE_NAME);
        interfaceType.setName(interfaceName);
        
        this.addInterfaceOperationTypes(interfaceType);
    }
    /**
     * AddInterfaceOperationTypes Method.
     */
    public void addInterfaceOperationTypes(InterfaceType interfaceType)
    {
        this.scanProcesses(interfaceType, OperationType.INTERFACE_OPERATIONS);
    }
    /**
     * AddInterfaceOperationType Method.
     */
    public void addInterfaceOperationType(String strVersion, InterfaceType interfaceType, MessageProcessInfo recMessageProcessInfo)
    {
        InterfaceOperationType interfaceOperationType = wsdlFactory.createInterfaceOperationType();
        interfaceType.getOperationOrFaultOrFeature().add(wsdlFactory.createInterfaceTypeOperation(interfaceOperationType));
        String name = this.fixName(recMessageProcessInfo.getField(MessageProcessInfo.DESCRIPTION).toString());
        interfaceOperationType.setName(name);
        String pattern = this.getURIProperty(MESSAGE_PATTERN_URI);
        interfaceOperationType.setPattern(pattern);
        boolean safe = SAFE_DEFAULT;
        String safeValue = ((PropertiesField)recMessageProcessInfo.getField(MessageProcessInfo.PROPERTIES)).getProperty(MessageProcessInfo.SAFE);
        if (safeValue != null)
            safe = Boolean.parseBoolean(safeValue);
        interfaceOperationType.setSafe(safe);
        String style = ((PropertiesField)recMessageProcessInfo.getField(MessageProcessInfo.PROPERTIES)).getProperty(MESSAGE_STYLE_URI);
        if (style == null)
            style = this.getURIProperty(MESSAGE_STYLE_URI);
        interfaceOperationType.setStyle(style);
        MessageInfo recMessageInfo = this.getMessageIn(recMessageProcessInfo);
        if (recMessageInfo != null)
        {
            MessageRefType messageRefType = wsdlFactory.createMessageRefType();
            JAXBElement<MessageRefType> interfaceOperationTypeInput = wsdlFactory.createInterfaceOperationTypeInput(messageRefType);
            interfaceOperationType.getInputOrOutputOrInfault().add(interfaceOperationTypeInput);
            name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
            String code = recMessageInfo.getField(MessageInfo.CODE).toString();
            if (code == null)
                code = name;
            String element = ((PropertiesField)recMessageInfo.getField(MessageInfo.MESSAGE_PROPERTIES)).getProperty(MessageInfo.ELEMENT);
            if (element == null)
                if (code != null)
                    element = code;
            String messageLabel = "In";
            messageRefType.setElement(element);
            messageRefType.setMessageLabel(messageLabel);
            messageRefType = wsdlFactory.createMessageRefType();
        }
        recMessageInfo = this.getMessageOut(recMessageProcessInfo);
        if (recMessageInfo != null)
        {
            MessageRefType messageRefType = wsdlFactory.createMessageRefType();
            JAXBElement<MessageRefType> interfaceOperationTypeOutput = wsdlFactory.createInterfaceOperationTypeOutput(messageRefType);
            interfaceOperationType.getInputOrOutputOrInfault().add(interfaceOperationTypeOutput);
            name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
            String code = recMessageInfo.getField(MessageInfo.CODE).toString();
            if (code == null)
                code = name;
            String element = ((PropertiesField)recMessageInfo.getField(MessageInfo.MESSAGE_PROPERTIES)).getProperty(MessageInfo.ELEMENT);
            if (element == null)
                if (code != null)
                    element = code;
            String messageLabel = "Out";
            messageRefType.setElement(element);
            messageRefType.setMessageLabel(messageLabel);
        }
    }
    /**
     * AddTypeTypes Method.
     */
    public void addTypeTypes(DescriptionType descriptionType)
    {
        TypesType typesType = wsdlFactory.createTypesType();
        descriptionType.getImportOrIncludeOrTypes().add(wsdlFactory.createTypes(typesType));
        
        this.scanProcesses(typesType, OperationType.TYPES_OPERATIONS);
    }
    /**
     * AddTypeType Method.
     */
    public void addTypeType(String version, TypesType typesType, MessageProcessInfo recMessageProcessInfo)
    {
        if (schemaFactory == null)
            schemaFactory = new org.w3._2001.xmlschema.ObjectFactory();
        // Create the types (import the OTA specs)
        MessageInfo recMessageInfo = this.getMessageIn(recMessageProcessInfo);
        if (recMessageInfo != null)
        {
            String name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
            if (this.isNewType(name))
            {
                this.addSchema(version, typesType, recMessageInfo);
            }
        }
        recMessageInfo = this.getMessageOut(recMessageProcessInfo);
        if (recMessageInfo != null)
        {
            String name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
            if (this.isNewType(name))
            {
                this.addSchema(version, typesType, recMessageInfo);
            }
        }
    }
    /**
     * AddSchema Method.
     */
    public void addSchema(String version, TypesType typesType, MessageInfo recMessageInfo)
    {
        String name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
        
        Import importel = schemaFactory.createImport();
        typesType.getAny().add(importel);
        String code = recMessageInfo.getField(MessageInfo.CODE).toString();
        if (code == null)
            code = name;
        String namespace = ((PropertiesField)recMessageInfo.getField(MessageInfo.MESSAGE_PROPERTIES)).getProperty(MessageInfo.NAMESPACE);
        String element = ((PropertiesField)recMessageInfo.getField(MessageInfo.MESSAGE_PROPERTIES)).getProperty(MessageInfo.ELEMENT);
        String schemaLocation = ((PropertiesField)recMessageInfo.getField(MessageInfo.MESSAGE_PROPERTIES)).getProperty(MessageInfo.SCHEMA_LOCATION);
        
        if (namespace == null)
            namespace = this.getMessageControl().getNamespaceFromVersion(version);
        if (element == null)
            if (code != null)
                element = code;
        if (schemaLocation == null)
            if (code != null)
                schemaLocation = code + ".xsd";
        schemaLocation = this.getMessageControl().getSchemaLocation(version, schemaLocation);
        if (namespace != null)
            importel.setNamespace(namespace);
        importel.setSchemaLocation(schemaLocation);
    }
    /**
     * AddProcessForWSDL Method.
     */
    public void addProcessForWSDL(String strVersion, Object typeObject, MessageProcessInfo recMessageProcessInfo, OperationType type)
    {
        if (type == OperationType.BINDING_OPERATIONS)
            this.addBindingOperationType(strVersion, (BindingType)typeObject, recMessageProcessInfo);
        else if (type == OperationType.INTERFACE_OPERATIONS)
            this.addInterfaceOperationType(strVersion, (InterfaceType)typeObject, recMessageProcessInfo);
        else if (type == OperationType.TYPES_OPERATIONS)
            this.addTypeType(strVersion, (TypesType)typeObject, recMessageProcessInfo);
    }

}
