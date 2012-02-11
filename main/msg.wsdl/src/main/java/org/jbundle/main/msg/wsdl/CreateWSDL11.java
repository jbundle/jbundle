/**
 * @(#)CreateWSDL11.
 * Copyright © 2011 jbundle.org. All rights reserved.
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
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.xmlsoap.schemas.wsdl.*;
import javax.xml.bind.*;
import javax.xml.namespace.*;
import org.jbundle.main.msg.db.*;
import org.w3._2001.xmlschema.*;
import java.io.*;
import org.xmlsoap.schemas.wsdl.ObjectFactory.*;
import org.xmlsoap.schemas.wsdl.soap.ObjectFactory.*;

/**
 *  CreateWSDL11 - .
 */
public class CreateWSDL11 extends CreateWSDL
{
    public static String[][] DEFAULTS_1_1 = {
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
        {SOAP_URI, "http://schemas.xmlsoap.org/soap/http"},
        {SOAP_RESPONSE_URI, "http://www.w3.org/2003/05/soap/mep/soap-response"},
    };
    protected org.xmlsoap.schemas.wsdl.ObjectFactory wsdlFactory = null;
    protected org.xmlsoap.schemas.wsdl.soap.ObjectFactory soapFactory = null;
    public static final String WSDL_TRANSFORMER =     "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
        "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\"  >" +
        "<!-- This template copies the name and attributes and applies templates to child nodes -->" +
        "<xsl:template match=\"*\">" +
        "  <xsl:copy>" +
        "    <xsl:for-each select=\"@*\">" +
    // This is a lame hack to remove the 'part' attribute that is added in a JAXB bug
    "<xsl:if test=\"not((name()='parts') and (namespace-uri(..)='http://schemas.xmlsoap.org/wsdl/soap/') and (.=''))\">" +
        "      <xsl:attribute name=\"{name()}\">" +
        "        <xsl:value-of select=\".\"/>" +
        "      </xsl:attribute>" +
    "</xsl:if>" +
        "    </xsl:for-each> " +
        "   <xsl:apply-templates />" +
        "  </xsl:copy>" +
        "</xsl:template>" +
        "</xsl:stylesheet>";
    /**
     * Default constructor.
     */
    public CreateWSDL11()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CreateWSDL11(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
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
        DEFAULTS = DEFAULTS_1_1;
    }
    /**
     * GetSOAPPackage Method.
     */
    public String getSOAPPackage()
    {
        return "org.xmlsoap.schemas.wsdl:org.xmlsoap.schemas.wsdl.soap:org.w3._2001.xmlschema";//(String)((TrxMessageHeader)this.getMessage().getMessageHeader()).get(SOAPMessageTransport.SOAP_PACKAGE);
    }
    /**
     * CreateMarshallableObject Method.
     */
    public Object createMarshallableObject()
    {
        wsdlFactory = new org.xmlsoap.schemas.wsdl.ObjectFactory();
        
        // create a wsdl object
        TDefinitions TDefinitions = wsdlFactory.createTDefinitions();
        JAXBElement<TDefinitions> root = wsdlFactory.createDefinitions(TDefinitions);
        String targetNamespace = this.getNamespace();  // Location of this document
        TDefinitions.setTargetNamespace(targetNamespace);
        
        // Create the service type
            this.addService(TDefinitions);
            
          // Create the bindings type
        this.addBindingsType(TDefinitions);
        
        // Create the service type
        this.addPort(TDefinitions);
        
        // Create the service type
        this.addMessageTypes(TDefinitions);
        
        // Create the interface type
        this.addTypeTypes(TDefinitions);
        
        return root;
    }
    /**
     * AddService Method.
     */
    public void addService(TDefinitions descriptionType)
    {
        String interfacens;
        String interfacename;
        QName qname;
        String name;
        
        if (soapFactory == null)
            soapFactory = new org.xmlsoap.schemas.wsdl.soap.ObjectFactory();
        
        TService service = wsdlFactory.createTService();
        descriptionType.getAnyTopLevelOptionalElement().add(service);
        
        name = this.getControlProperty(MessageControl.SERVICE_NAME);
        service.setName(name);
        
        interfacens = this.getNamespace();
        interfacename = this.getControlProperty(MessageControl.BINDING_NAME);
        qname = new QName(interfacens, interfacename);
        
        TPort port = wsdlFactory.createTPort();
        service.getPort().add(port);
        port.setName(name + "Port");
        port.setBinding(qname);
        
        org.xmlsoap.schemas.wsdl.soap.TAddress tAddress = soapFactory.createTAddress();
        port.getAny().add(soapFactory.createAddress(tAddress));
        
        String address = this.getURIValue(this.getRecord(MessageControl.MESSAGE_CONTROL_FILE).getField(MessageControl.WEB_SERVICES_SERVER).toString());  // Important - This is the web services URL
        tAddress.setLocation(address);
    }
    /**
     * AddBindingsType Method.
     */
    public void addBindingsType(TDefinitions definitions)
    {
        String interfacens;
        String interfacename;
        QName qname;
        String value;
        String name;
        
        if (soapFactory == null)
            soapFactory = new org.xmlsoap.schemas.wsdl.soap.ObjectFactory();
        
        // Create the bindings type
        TBinding binding = wsdlFactory.createTBinding();
        definitions.getAnyTopLevelOptionalElement().add(binding);
        name = this.getControlProperty(MessageControl.BINDING_NAME);
        binding.setName(name);
        interfacens = this.getNamespace();
        interfacename = this.getControlProperty(MessageControl.INTERFACE_NAME);
        qname = new QName(interfacens, interfacename);
        binding.setType(qname);
        //?LocalElement localElement = schemaFactory.createLocalElement();
        //?TBinding.getAny().add(localElement);
        org.xmlsoap.schemas.wsdl.soap.TBinding tBinding = soapFactory.createTBinding();
        binding.getAny().add(soapFactory.createBinding(tBinding));
        interfacens = this.getControlProperty(SOAP_URI);
        tBinding.setTransport(interfacens);
        tBinding.setStyle(org.xmlsoap.schemas.wsdl.soap.TStyleChoice.DOCUMENT);
        
        this.addBindingOperationTypes(binding);
    }
    /**
     * AddBindingOperationTypes Method.
     */
    public void addBindingOperationTypes(TBinding bindingType)
    {
        this.scanProcesses(bindingType, OperationType.BINDING_OPERATIONS);
    }
    /**
     * AddBindingOperationType Method.
     */
    public void addBindingOperationType(String strVersion, TBinding binding, MessageProcessInfo recMessageProcessInfo)
    {
        String interfacename;
        
        TBindingOperation bindingOperationType = wsdlFactory.createTBindingOperation();
        binding.getOperation().add(bindingOperationType);
        interfacename = this.fixName(recMessageProcessInfo.getField(MessageProcessInfo.DESCRIPTION).toString());
        bindingOperationType.setName(interfacename);
        interfacename = this.fixName(recMessageProcessInfo.getField(MessageProcessInfo.CODE).toString());
        if (interfacename == null)
            interfacename = this.getURIValue(this.getRecord(MessageControl.MESSAGE_CONTROL_FILE).getField(MessageControl.WEB_SERVICES_SERVER).toString());  // Important - This is the web services URL
        
        org.xmlsoap.schemas.wsdl.soap.TOperation tBinding = soapFactory.createTOperation();
        bindingOperationType.getAny().add(soapFactory.createOperation(tBinding));
        tBinding.setSoapAction(interfacename);
        tBinding.setStyle(org.xmlsoap.schemas.wsdl.soap.TStyleChoice.DOCUMENT);
        
        TBindingOperationMessage message = wsdlFactory.createTBindingOperationMessage();
        bindingOperationType.setInput(message);
        org.xmlsoap.schemas.wsdl.soap.TBody body = soapFactory.createTBody();
        message.getAny().add(soapFactory.createBody(body));
        body.setUse(org.xmlsoap.schemas.wsdl.soap.UseChoice.LITERAL);
        
        message = wsdlFactory.createTBindingOperationMessage();
        bindingOperationType.setOutput(message);
        body = soapFactory.createTBody();
        message.getAny().add(soapFactory.createBody(body));
        body.setUse(org.xmlsoap.schemas.wsdl.soap.UseChoice.LITERAL);
    }
    /**
     * AddPort Method.
     */
    public void addPort(TDefinitions description)
    {
        // Create the interfaces
        TPortType interfaceType = wsdlFactory.createTPortType();
        description.getAnyTopLevelOptionalElement().add(interfaceType);
        String interfaceName = this.getControlProperty(MessageControl.INTERFACE_NAME);
        interfaceType.setName(interfaceName);
        
        this.addInterfaceOperationTypes(interfaceType);
    }
    /**
     * AddInterfaceOperationTypes Method.
     */
    public void addInterfaceOperationTypes(TPortType interfaceType)
    {
        this.scanProcesses(interfaceType, OperationType.INTERFACE_OPERATIONS);
    }
    /**
     * AddInterfaceOperationType Method.
     */
    public void addInterfaceOperationType(String strVersion, TPortType interfaceType, MessageProcessInfo recMessageProcessInfo)
    {
        TOperation interfaceOperationType = wsdlFactory.createTOperation();
        interfaceType.getOperation().add(interfaceOperationType);
        String name = this.fixName(recMessageProcessInfo.getField(MessageProcessInfo.DESCRIPTION).toString());
        interfaceOperationType.setName(name);
        String pattern = this.getURIProperty(MESSAGE_PATTERN_URI);
        //xinterfaceOperationType.setPattern(pattern);
        boolean safe = SAFE_DEFAULT;
        String safeValue = ((PropertiesField)recMessageProcessInfo.getField(MessageProcessInfo.PROPERTIES)).getProperty(MessageProcessInfo.SAFE);
        if (safeValue != null)
            safe = Boolean.parseBoolean(safeValue);
        //xinterfaceOperationType.setSafe(safe);
        String style = ((PropertiesField)recMessageProcessInfo.getField(MessageProcessInfo.PROPERTIES)).getProperty(MESSAGE_STYLE_URI);
        if (style == null)
            style = this.getURIProperty(MESSAGE_STYLE_URI);
        //xinterfaceOperationType.setStyle(style);
        MessageInfo recMessageInfo = this.getMessageIn(recMessageProcessInfo);
        if (recMessageInfo != null)
        {
            TParam messageRefType = wsdlFactory.createTParam();
            interfaceOperationType.getAny().add(wsdlFactory.createTOperationInput(messageRefType));
            name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
            String interfacens = this.getNamespace();
            QName qname = new QName(interfacens, name);
            String code = recMessageInfo.getField(MessageInfo.CODE).toString();
            if (code == null)
                code = name;
            String element = ((PropertiesField)recMessageInfo.getField(MessageInfo.MESSAGE_PROPERTIES)).getProperty(MessageInfo.ELEMENT);
            if (element == null)
                if (code != null)
                    element = code;
            String messageLabel = "In";
            messageRefType.setMessage(qname);
        }
        recMessageInfo = this.getMessageOut(recMessageProcessInfo);
        if (recMessageInfo != null)
        {
            TParam messageRefType = wsdlFactory.createTParam();
            interfaceOperationType.getAny().add(wsdlFactory.createTOperationOutput(messageRefType));
            name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
            String interfacens = this.getNamespace();
            QName qname = new QName(interfacens, name);
            String code = recMessageInfo.getField(MessageInfo.CODE).toString();
            if (code == null)
                code = name;
            String element = ((PropertiesField)recMessageInfo.getField(MessageInfo.MESSAGE_PROPERTIES)).getProperty(MessageInfo.ELEMENT);
            if (element == null)
                if (code != null)
                    element = code;
            String messageLabel = "Out";
            messageRefType.setMessage(qname);
        }
    }
    /**
     * AddMessageTypes Method.
     */
    public void addMessageTypes(TDefinitions descriptionType)
    {
        names.clear();
        this.scanProcesses(descriptionType, OperationType.MESSAGE_OPERATIONS);
    }
    /**
     * AddMessageType Method.
     */
    public void addMessageType(String version, TDefinitions descriptionType, MessageProcessInfo recMessageProcessInfo)
    {
        // Create the types (import the OTA specs)
        MessageInfo recMessageInfo = this.getMessageIn(recMessageProcessInfo);
        if (recMessageInfo != null)
        {
            String name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
            if (this.isNewType(name))
            {
                this.addMessage(version, descriptionType, recMessageInfo);
            }
        }
        recMessageInfo = this.getMessageOut(recMessageProcessInfo);
        if (recMessageInfo != null)
        {
            String name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
            if (this.isNewType(name))
            {
                this.addMessage(version, descriptionType, recMessageInfo);
            }
        }
    }
    /**
     * AddMessage Method.
     */
    public void addMessage(String version, TDefinitions descriptionType, MessageInfo recMessageInfo)
    {
        TMessage messageType = wsdlFactory.createTMessage();
        descriptionType.getAnyTopLevelOptionalElement().add(messageType);
        String strMessageName = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
        messageType.setName(strMessageName);
        TPart TPart = wsdlFactory.createTPart();        
        messageType.getPart().add(TPart);
        TPart.setName(strMessageName);
        String interfacens = this.getNamespace();
        String interfacename = this.fixName(recMessageInfo.getField(MessageInfo.CODE).toString());
        QName qname = new QName(interfacens, interfacename);
        TPart.setElement(qname);
    }
    /**
     * AddTypeTypes Method.
     */
    public void addTypeTypes(TDefinitions descriptionType)
    {
        if (schemaFactory == null)
            schemaFactory = new org.w3._2001.xmlschema.ObjectFactory();
        
        names.clear();
        TTypes types = wsdlFactory.createTTypes();
        descriptionType.getAnyTopLevelOptionalElement().add(types);
        
        // This code is LAME, but for wsdl1.1, import doesn't work
        org.w3._2001.xmlschema.Schema schema = schemaFactory.createSchema();
        types.getAny().add(schema);
        String strVersion = this.getProperty("version");
        if (strVersion == null)
            strVersion = this.getDefaultVersion();
        schema.setId(this.getMessageControl().getIdFromVersion(strVersion));
        schema.setVersion(this.getMessageControl().getNumericVersionFromVersion(strVersion));
        schema.setElementFormDefault(FormChoice.QUALIFIED);
        this.scanProcesses(types, OperationType.TYPES_OPERATIONS);
    }
    /**
     * AddTypeType Method.
     */
    public void addTypeType(String version, TTypes TTypes, MessageProcessInfo recMessageProcessInfo)
    {
        // Create the types (import the OTA specs)
        MessageInfo recMessageInfo = this.getMessageIn(recMessageProcessInfo);
        if (recMessageInfo != null)
        {
            String name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
            if (this.isNewType(name))
            {
                this.addSchema(version, TTypes, recMessageInfo);
            }
        }
        recMessageInfo = this.getMessageOut(recMessageProcessInfo);
        if (recMessageInfo != null)
        {
            String name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
            if (this.isNewType(name))
            {
                this.addSchema(version, TTypes, recMessageInfo);
            }
        }
    }
    /**
     * AddSchema Method.
     */
    public void addSchema(String version, TTypes types, MessageInfo recMessageInfo)
    {
        String name = this.fixName(recMessageInfo.getField(MessageInfo.DESCRIPTION).toString());
        
        Import importel = schemaFactory.createImport();
        org.w3._2001.xmlschema.Schema schema = (org.w3._2001.xmlschema.Schema)types.getAny().get(0);    // LAME
        schema.getIncludeOrImportOrRedefine().add(importel);
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
            this.addBindingOperationType(strVersion, (TBinding)typeObject, recMessageProcessInfo);
        else if (type == OperationType.INTERFACE_OPERATIONS)
            this.addInterfaceOperationType(strVersion, (TPortType)typeObject, recMessageProcessInfo);
        else if (type == OperationType.MESSAGE_OPERATIONS)
            this.addMessageType(strVersion, (TDefinitions)typeObject, recMessageProcessInfo);
        else if (type == OperationType.TYPES_OPERATIONS)
            this.addTypeType(strVersion, (TTypes)typeObject, recMessageProcessInfo);
    }
    /**
     * GetXML Method.
     */
    public String getXML(Object root)
    {
        String strXML = super.getXML(root);
        // NOTE: This is a lame hack to remove the 'part' attribute that is added in a JAXB bug
        Reader reader = new StringReader(strXML);
        
        Writer stringWriter = new StringWriter();
        
        String strTransformer = WSDL_TRANSFORMER;
        Reader readerxsl = new StringReader(strTransformer);
        
        Utility.transformMessage(reader, stringWriter, readerxsl);
        
        return stringWriter.toString();
    }

}
