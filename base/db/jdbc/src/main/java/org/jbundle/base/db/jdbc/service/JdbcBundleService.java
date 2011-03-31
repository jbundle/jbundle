package org.jbundle.base.db.jdbc.service;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.jdbc.JdbcDatabase;
import org.jbundle.base.util.DBParams;
import org.jbundle.thin.base.util.Util;
import org.jbundle.thin.base.util.osgi.bundle.BaseBundleService;
import org.jbundle.thin.base.util.osgi.bundle.BundleService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The bundle start activator. 
 * Create and register the class creation utility.
 * @author don
 *
 */
public class JdbcBundleService extends BaseBundleService
	implements BundleActivator
{    
    /**
     * Bundle starting up.
     */
    public void start(BundleContext context) throws Exception {
        System.out.println("Starting Jdbc bundle");
        
        this.setProperty(BundleService.PACKAGE_NAME, Util.getPackageName(JdbcDatabase.class.getName()));
        this.setProperty(BundleService.INTERFACE, BaseDatabase.class.getName());
        this.setProperty(BundleService.TYPE, DBParams.JDBC);
        
        super.start(context);
    }    
    /**
     * Bundle stopping.
     */
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping Jdbc bundle");
//        Automatically unregistered.
        super.stop(context);
    }

}
