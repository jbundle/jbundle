package org.jbundle.base.db.jdbc.service;

import java.util.Dictionary;
import java.util.Hashtable;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.jdbc.JdbcDatabase;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.jbundle.base.util.*;
import org.jbundle.thin.base.util.osgi.bootstrap.ClassAccess;
import org.jbundle.thin.base.util.osgi.service.ClassAccessImpl;
import org.jbundle.thin.base.util.Util;

/**
 * The bundle start activator. 
 * Create and register the class creation utility.
 * @author don
 *
 */
public class Activator implements BundleActivator {
    ServiceRegistration helloServiceRegistration;
    
    /**
     * Bundle starting up.
     */
    public void start(BundleContext context) throws Exception {
        System.out.println("Starting Jdbc bundle");
        
        Dictionary<String,String> properties = new Hashtable<String,String>();
        properties.put(ClassAccess.PACKAGE_NAME, Util.getPackageName(JdbcDatabase.class.getName()));
        properties.put(ClassAccess.INTERFACE, BaseDatabase.class.getName());
        properties.put(ClassAccess.TYPE, DBParams.JDBC);
        ClassAccess classService = new ClassAccessImpl(properties);
        helloServiceRegistration = context.registerService(ClassAccess.class.getName(), classService, properties);
    }    
    /**
     * Bundle stopping.
     */
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping Jdbc bundle");
//        Automatically unregistered.
    }

}
