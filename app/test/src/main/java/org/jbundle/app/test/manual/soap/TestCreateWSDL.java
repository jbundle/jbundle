/**
 *  @(#)HotelRateRequestSOAPMessageOutA2006.
 *  Copyright © 2007 tourapp.com. All rights reserved.
 */
package org.jbundle.app.test.manual.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.bind.*;
import javax.xml.namespace.QName;

import org.jbundle.base.db.Record;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.message.trx.transport.soap.SOAPMessageTransport;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;
import org.w3._2006._01.wsdl.*;
import org.w3._2001.xmlschema.*;
import org.w3c.dom.*;


/**
 *  HotelRateRequestSOAPMessageOutA2006 - .
 */
public class TestCreateWSDL extends BaseProcess
{
    /**
     * Initialization.
     */
    public TestCreateWSDL()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public TestCreateWSDL(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        m_properties = properties;

        super.init(taskParent, recordMain, properties);
    }
    public static void main(String[] args)
    {
        TestCreateWSDL test = new TestCreateWSDL();
        Object data = test.convertInternalToMarshallableObject(null);
        String xml = test.getXML(data);
        System.out.println(xml);
    }
    /**
     * Convert this tree to a XML string.
     * Override this.
     * @param root The room jaxb item.
     * @return The dom tree.
     */
    public String getXML(Object root)
    {
        if (root != null)
        {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                String strSOAPPackage = "org.w3._2001.xmlschema:org.w3.wsdl20";//(String)((TrxMessageHeader)this.getMessage().getMessageHeader()).get(SOAPMessageTransport.SOAP_PACKAGE);
                JAXBContext context = JAXBContext.newInstance(strSOAPPackage);

                Marshaller m = context.createMarshaller();
    
    //            Validator validator = context.createValidator();
    //            validator.validate(root);
    
                m.marshal( root, out );
                String strXML = out.toString(Constants.STRING_ENCODING);
                return strXML;
            } catch (IOException ex)  {
                ex.printStackTrace();   // Never
            } catch (JAXBException ex)   {
                ex.printStackTrace();
            } catch (java.lang.IllegalArgumentException ex)   {
                ex.printStackTrace();
            }
        }
        return null;
    }
    /**
     * Convert this source message to the ECXML format.
     * Typically you do not override this method, you override the getTransformer method
     * to supply a XSLT document to do the conversion.
     * @return The XML tree that conforms to the ECXML format.
     */
    public Object convertInternalToMarshallableObject(Document doc)
    {
        String interfacens;
        String interfacename;
        QName qname;
        String value;
        String namespace;

        JAXBElement<DescriptionType> root = null;
        // create an empty wsdl object
        String targetNamespace = "http://www.tourloco.com:8080/tour/receiver/wsdl";  // Location of this document
        org.w3._2006._01.wsdl.ObjectFactory wsdlFactory = new org.w3._2006._01.wsdl.ObjectFactory();
        org.w3._2001.xmlschema.ObjectFactory schemaFactory = new org.w3._2001.xmlschema.ObjectFactory();
        DescriptionType descriptionType = wsdlFactory.createDescriptionType();
        root = wsdlFactory.createDescription(descriptionType);
        descriptionType.setTargetNamespace(targetNamespace);
        
        // Create the types (import the OTA specs)
        TypesType typesType = wsdlFactory.createTypesType();
        final String WSOAP_BINDING_TYPE = "http://www.w3.org/ns/wsdl/soap";
        final String TNS_BINDING_TYPE = "http://www.w3.org/ns/wsdl/tns";
        final String GHNS_BINDING_TYPE = "http://www.w3.org/ns/wsdl/ghns";
        final String SOAP_BINDING_TYPE = "http://www.w3.org/2003/05/soap-envelope";
        final String SCHEMA_BINDING_TYPE = "http://www.w3.org/2001/XMLSchema";
        JAXBElement<TypesType> types = wsdlFactory.createTypes(typesType);
        descriptionType.getImportOrIncludeOrTypes().add(types);

        Schema schema = schemaFactory.createSchema();
        typesType.getAny().add(schema);
        boolean importSchema = false;
        if (importSchema)
        {
            namespace="xs:anyURI";
            schema.setTargetNamespace(namespace);
            Import importel = schemaFactory.createImport();
            typesType.getAny().add(importel);
            namespace = "xs:anyURI";
            importel.setNamespace(namespace);
            String schemaLocation="xs:anyURI";
            importel.setSchemaLocation(schemaLocation);
        }
        else
        {
            TopLevelElement topLevelElement = schemaFactory.createTopLevelElement();
            interfacens = SCHEMA_BINDING_TYPE;
            interfacename = "xyz";
            qname = new QName(interfacens, interfacename);
            value = "value";
            topLevelElement.setType(qname);
            topLevelElement.setRef(qname);
            schema.getSimpleTypeOrComplexTypeOrGroup().add(topLevelElement);
        }
        //                <types>
        //                [ <xs:import namespace="xs:anyURI" schemaLocation="xs:anyURI"? /> |
        //                  <xs:schema targetNamespace="xs:anyURI" /> |
        //                  other extension elements ]*
        //              </types>
        
        // Create the interfaces
        String interfaceName = "reservationsInterface";
        InterfaceType interfaceType = wsdlFactory.createInterfaceType();
        interfaceType.setName(interfaceName);
        descriptionType.getImportOrIncludeOrTypes().add(wsdlFactory.createInterface(interfaceType));
        InterfaceOperationType interfaceOperationType = wsdlFactory.createInterfaceOperationType();
        interfaceType.getOperationOrFaultOrFeature().add(wsdlFactory.createInterfaceTypeOperation(interfaceOperationType));
        String name = "opCheckAvailability";
        final String PATTERN = "http://www.w3.org/ns/wsdl/in-out";
        final String STYLE = "http://www.w3.org/ns/wsdl/style/iri";
        final boolean SAFE = true;
        interfaceOperationType.setName(name);
        interfaceOperationType.setPattern(PATTERN);
        interfaceOperationType.setSafe(SAFE);
        interfaceOperationType.setStyle(STYLE);
        MessageRefType messageRefType = wsdlFactory.createMessageRefType();
        String elementName = "ghns:checkAvailability";
        String messageLabel = "In";
        messageRefType.setElement(elementName);
        messageRefType.setMessageLabel(messageLabel);
        JAXBElement<MessageRefType> interfaceOperationTypeInput = wsdlFactory.createInterfaceOperationTypeInput(messageRefType);
        interfaceOperationType.getInputOrOutputOrInfault().add(interfaceOperationTypeInput);
        messageRefType = wsdlFactory.createMessageRefType();
        elementName = "ghns:checkAvailabilityResponse";
        messageLabel = "Out";
        messageRefType.setElement(elementName);
        messageRefType.setMessageLabel(messageLabel);
        JAXBElement<MessageRefType> interfaceOperationTypeOutput = wsdlFactory.createInterfaceOperationTypeOutput(messageRefType);
        interfaceOperationType.getInputOrOutputOrInfault().add(interfaceOperationTypeOutput);
        InterfaceFaultType interfaceFaultType = wsdlFactory.createInterfaceFaultType();
        interfaceType.getOperationOrFaultOrFeature().add(wsdlFactory.createInterfaceTypeFault(interfaceFaultType));
        interfacens = GHNS_BINDING_TYPE;
        interfacename = "invalidDataError";
        qname = new QName(interfacens, interfacename);
        interfaceFaultType.setElement(qname);
        name = "invalidDataFault";
        interfaceFaultType.setName(name);

        
        MessageRefFaultType messageRefFaultType = wsdlFactory.createMessageRefFaultType();
        interfacens = TNS_BINDING_TYPE;
        interfacename = "invalidDataFault";
        qname = new QName(interfacens, interfacename);
        messageLabel = "Out";
        messageRefFaultType.setRef(qname);
        messageRefFaultType.setMessageLabel(messageLabel);
        JAXBElement<MessageRefFaultType> interfaceOperationFaultType = wsdlFactory.createInterfaceOperationTypeOutfault(messageRefFaultType);
        interfaceOperationType.getInputOrOutputOrInfault().add(interfaceOperationFaultType);
        
        // Create the bindings type
        BindingType bindingType = wsdlFactory.createBindingType();
        descriptionType.getImportOrIncludeOrTypes().add(wsdlFactory.createBinding(bindingType));
        name = "reservationSOAPBinding";
        bindingType.setName(name);
        interfacens = TNS_BINDING_TYPE;
        interfacename = "reservationInterface";
        qname = new QName(interfacens, interfacename);
        bindingType.setInterface(qname);
        bindingType.setType(WSOAP_BINDING_TYPE);
        interfacens = SOAP_BINDING_TYPE;
        interfacename = "protocol";
        qname = new QName(interfacens, interfacename);
        value = "http://www.w3.org/2003/05/soap/bindings/HTTP/";
        bindingType.getOtherAttributes().put(qname, value);
        BindingOperationType bindingOperationType = wsdlFactory.createBindingOperationType();
        interfacens = TNS_BINDING_TYPE;
        interfacename = "opCheckAvailability";
        qname = new QName(interfacens, interfacename);
        bindingOperationType.setRef(qname);
        interfacens = WSOAP_BINDING_TYPE;
        interfacename = "code";
        qname = new QName(interfacens, interfacename);
        value = "http://www.w3.org/2003/05/soap/mep/soap-response";
        bindingOperationType.getOtherAttributes().put(qname, value);
        bindingType.getOperationOrFaultOrFeature().add(wsdlFactory.createBindingTypeOperation(bindingOperationType));
        BindingOperationFaultType bindingOperationFaultType = wsdlFactory.createBindingOperationFaultType();
        bindingOperationFaultType.setRef(qname);
        interfacens = SOAP_BINDING_TYPE;
        interfacename = "mep";
        qname = new QName(interfacens, interfacename);
        value = "http://www.w3.org/2003/05/soap/mep/soap-response";
        bindingType.getOtherAttributes().put(qname, value);
        BindingFaultType bindingFaultType = wsdlFactory.createBindingFaultType();
        bindingType.getOperationOrFaultOrFeature().add(wsdlFactory.createBindingTypeFault(bindingFaultType));
        interfacens = TNS_BINDING_TYPE;
        interfacename = "invalidDataFault";
        qname = new QName(interfacens, interfacename);
        bindingFaultType.setRef(qname);
        interfacens = SOAP_BINDING_TYPE;
        interfacename = "code";
        qname = new QName(interfacens, interfacename);
        value = "soap:Sender";
        bindingFaultType.getOtherAttributes().put(qname, value);

        // Create the service type
        ServiceType serviceType = wsdlFactory.createServiceType();
        descriptionType.getImportOrIncludeOrTypes().add(wsdlFactory.createService(serviceType));
        name = "reservationService";
        serviceType.setName(name);
        interfacens = TNS_BINDING_TYPE;
        interfacename = "reservationInterface";
        qname = new QName(interfacens, interfacename);
        serviceType.setInterface(qname);
        EndpointType endpointType = wsdlFactory.createEndpointType();
        serviceType.getEndpointOrFeatureOrProperty().add(wsdlFactory.createEndpoint(endpointType));
        String address = "http://greath.example.com/2004/reservation";
        endpointType.setAddress(address);
        interfacens = TNS_BINDING_TYPE;
        interfacename = "reservationSOAPBinding";
        qname = new QName(interfacens, interfacename);
        endpointType.setBinding(qname);
        name = "reservationEndpoint";
        endpointType.setName(name);

        return root;
    }

}
