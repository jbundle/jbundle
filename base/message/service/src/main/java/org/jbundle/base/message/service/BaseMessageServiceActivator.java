/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.message.service;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.Utility;
import org.jbundle.base.util.BaseThickActivator;
import org.jbundle.base.util.Environment;
import org.jbundle.model.App;
import org.jbundle.model.Env;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.thread.AutoTask;
import org.osgi.framework.BundleContext;

/**
 * BaseMessageServiceActivator - OSGI Servicemix service bridge.
 * To use servicemix, you will need a SOAP (and maybe REST) implementation.
 * @see http://jibx.sourceforge.net/schema-library/webservices.html
 * Next, you will need to override this class and the BaseServiceMessageTransport class.
 * @see PingService2011BImpl
 * When you have servicemix started you will need start the SOAP and REST
 * servers (but not the service!) as detailed in the
 * JiBX example. Then, you will need to start the activator that you overrode this class with: 
 * # obr:addurl http://www.jibx.org/repository.xml
 * # obr:deploy org.jibx.schema.org.opentravel._2011B.ping.ws.service  (NO!)
 * obr:deploy org.jibx.schema.org.opentravel._2011B.ping.ws.rest
 * obr:deploy org.jibx.schema.org.opentravel._2011B.ping.ws.soap
 * # ctrl+d (stop servicemix) - You must restart due to a karaf auto-wiring problem
 * # bin/servicemix (restart servicemix)
 * Start the rest of jbundle as usual.
 * start com.tourapp.tour.message.service._2011B.ping
 * start org.jibx.schema.org.opentravel._2011B.ping.ws.rest
 * start org.jibx.schema.org.opentravel._2011B.ping.ws.soap * @author don
 */
public abstract class BaseMessageServiceActivator extends BaseThickActivator
{
	
    /**
     * Make sure the dependent services are up, then call startupService.
     * @param versionRange Bundle version
     * @param baseBundleServiceClassName
     * @return false if I'm waiting for the service to startup.
     */
    public boolean checkDependentServices(BundleContext bundleContext)
    {
        boolean success = this.addDependentService(context, Env.class.getName(), Environment.class.getName(), null, this.getProperties());
        success = success & super.checkDependentServices(bundleContext);
        return success;
    }
    /**
     * Get the activator properties to pass to the service.
     * @return The properties in String,Object format
     */
    public Map<String,Object> getServiceProperties()
    {
        Map<String,Object> properties = super.getServiceProperties();
        properties = addConfigProperty(properties, DBParams.LOCAL, DBParams.JDBC); // Remote Server ALWAYS uses Jdbc
        properties = addConfigProperty(properties, DBParams.REMOTE, DBParams.JDBC);
        properties = addConfigProperty(properties, DBParams.TABLE, DBParams.JDBC);
        properties = addConfigProperty(properties, Params.APP_NAME, Params.DEFAULT_REMOTE_APP);
        properties = addConfigProperty(properties, Params.MESSAGE_SERVER, DBConstants.TRUE);
        properties = addConfigProperty(properties, DBParams.FREEIFDONE, Constants.FALSE);   // Don't free when only the last app is running.
        properties = addConfigProperty(properties, MessageConstants.MESSAGE_FILTER, MessageConstants.TREE_FILTER);  // Default for a server
        return properties;
    }
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public Object startupService(BundleContext bundleContext)
    {
        Map<String,Object> props = this.getServiceProperties();
        Environment env = (Environment)this.getService(Env.class);
        props = Utility.putAllIfNew(props, env.getProperties());  // Use the same params as environment
        // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
        App app = env.getMessageApplication(true, props);
        Task task = new AutoTask(app, null, props);

        return this.createMessageService(task, null, props);
    }
    /**
     * Get the service app to handle these messages.
     * @param application
     * @param strParams
     * @param properties
     * @return
     */
    public abstract Object createMessageService(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties);

//        return new PingService2011BImpl(application, null, properties);

    /**
     * Stop this service.
     * Override this to do all the startup.
     * @param bundleService
     * @param context bundle context
     * @return true if successful.
     */
    public boolean shutdownService(Object service, BundleContext context)
    {
        if (service instanceof BaseServiceMessageTransport) // Always
            ((BaseServiceMessageTransport)service).free();
        return super.shutdownService(service, context);
    }

}
