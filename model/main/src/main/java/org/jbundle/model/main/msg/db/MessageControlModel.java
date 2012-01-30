/**
 * @(#)MessageControlModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.msg.db;

import org.jbundle.model.db.*;

public interface MessageControlModel extends Rec
{

    //public static final String ID = ID;
    public static final String PROPERTIES = "Properties";
    public static final String WEB_SERVICES_SERVER = "WebServicesServer";
    public static final String DEFAULT_VERSION_ID = "DefaultVersionID";
    public static final String BASE_NAMESPACE = "BaseNamespace";
    public static final String BASE_SCHEMA_LOCATION = "BaseSchemaLocation";
    public static final String WEB_MESSAGE_TRANSPORT_ID = "WebMessageTransportID";
    public static final String[][] DESCRIPTIONS = {
       {MessageControlModel.BASE_NAMESPACE_URI, "Base namespace"},
       {MessageControlModel.WSDL_NAMESPACE_URI, "WSDL namespace"},
    };
    public static final String BASE_NAMESPACE_URI = "base.uri";
    public static final String WSDL_NAMESPACE_URI = "uri.namespace";
    public static final String SERVICE_NAME = "service.name";
    public static final String INTERFACE_NAME = "interface.name";
    public static final String BINDING_NAME = "binding.name";
    public static final String ENDPOINT_NAME = "endpoint.name";
    public static final String MESSAGE_SUFFIX = "message.suffix";

    public static final String MESSAGE_CONTROL_FILE = "MessageControl";
    public static final String THIN_CLASS = "org.jbundle.thin.main.msg.db.MessageControl";
    public static final String THICK_CLASS = "org.jbundle.main.msg.db.MessageControl";
    /**
     * GetVersionFromSchemaLocation Method.
     */
    public String getVersionFromSchemaLocation(String schemaLocation);

}
