package org.jbundle.thin.base.util.osgi.bundle;

import org.jbundle.thin.base.util.osgi.finder.BaseClassFinder;
import org.jbundle.thin.base.util.osgi.finder.ClassFinderUtility;
import org.osgi.framework.BundleContext;

/**
 * This class starts the bundle that belongs to this class and then calls the classfinder
 * with the bundle object.
 * Note: This is a thread since is is usually takes a while to run.
 * @author don
 *
 */
class BunderStarter extends Thread
{
	BundleContext bundleContext = null;
	String dependentBaseBundleClassName = null;
	
	public BunderStarter(BaseBundleService bundleService, BundleContext bundleContext, String dependentBaseBundleClassName)
	{
		this.bundleContext = bundleContext;
		this.dependentBaseBundleClassName = dependentBaseBundleClassName;
	}
	public void run()
	{
		BaseClassFinder classFinder = (BaseClassFinder)ClassFinderUtility.getClassFinder();
		classFinder.startBaseBundle(bundleContext, dependentBaseBundleClassName);
	}
}