package org.jbundle.thin.base.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jbundle.model.Task;
import org.jbundle.model.util.Constant;
import org.jbundle.model.util.Util;


/**
 * Thin specific static utility methods.
 */
public class OsgiUtil extends Util
{
   /**
    * Get the Osgi class service.
    * NOTE: Don't import this package as the ClassService class may not be available until this service is started.
    * @return
    */
   public static boolean classServiceAvailable = true;
   public static org.jbundle.util.osgi.finder.ClassFinder getClassFinder()
   {
	   if (!classServiceAvailable)
		   return null;
	   try {
		   Class.forName("org.osgi.framework.BundleActivator");	// This tests to see if osgi exists
		   return (org.jbundle.util.osgi.finder.ClassFinder)org.jbundle.util.osgi.finder.ClassFinderUtility.getClassFinder(null, true);
       } catch (Exception ex) {
		   classServiceAvailable = false;
    	   return null;	// Osgi is not installed
       }
   }
   /**
    * Create this object given the class name.
 * @param className
    * @return
    */
   public static Object makeObjectFromClassName(String className)
   {
	   return OsgiUtil.makeObjectFromClassName(className, null, true);
   }
   /**
    * Create this object given the class name.
    * @param className
    * @return
    */
   public static Object makeObjectFromClassName(String className, Task task, boolean bErrorIfNotFound)
   {
	   if (className == null)
		   return null;
	   className = Util.getFullClassName(className);
      
	   Class<?> clazz = null;
       try {
			clazz = Class.forName(className);
       } catch (ClassNotFoundException e) {
		   if (OsgiUtil.getClassFinder() != null)
			   clazz = OsgiUtil.getClassFinder().findClassBundle(className);	// Try to find this class in the obr repos
    	   if (clazz == null)
    	       OsgiUtil.handleClassException(e, className, task, bErrorIfNotFound);
       }
       
	   Object object = null;
       try {
    	   if (clazz != null)
    		   object = clazz.newInstance();
	   } catch (InstantiationException e)   {
	       OsgiUtil.handleClassException(e, className, task, bErrorIfNotFound);
	   } catch (IllegalAccessException e)   {
	       OsgiUtil.handleClassException(e, className, task, bErrorIfNotFound);
	   } catch (Exception e) {
	       OsgiUtil.handleClassException(e, className, task, bErrorIfNotFound);
       }
       return object;
   }
   /**
    * Shutdown the bundle for this service.
    * @param service The service object
    */
   public static void shutdownService(Object service)
   {
	   if (OsgiUtil.getClassFinder() != null)
		   OsgiUtil.getClassFinder().shutdownService(service);	// Shutdown the bundle for this service
   }
   /**
    * Create this object given the class name.
    * @param filepath
    * @return
    */
   public static URL getResourceFromPathName(String filepath, Task task, boolean bErrorIfNotFound, URL urlCodeBase, ClassLoader classLoader)
   {
	   if (filepath == null)
		   return null;
      
       URL url = null;
       try {
           url = classLoader.getResource(filepath);
       } catch (Exception e) {
               // Keep trying
       }

       if (url == null)
       {
		   if (OsgiUtil.getClassFinder() != null)
			   url = OsgiUtil.getClassFinder().findBundleResource(filepath);	// Try to find this class in the obr repos
		   if (url == null)
		       OsgiUtil.handleClassException(null, filepath, task, bErrorIfNotFound);
       }

       if (url == null)
       {
           try
           {
               if (urlCodeBase != null)
                   url = new URL(urlCodeBase, filepath);
           } catch(MalformedURLException ex) {
               // Keep trying
           } catch (Exception e) {
               // Keep trying
           }
       }
	   return url;
   }
   /**
    * Gets a resource bundle using the specified base name and locale,
    * @param baseName the base name of the resource bundle, a fully qualified class name
    * @param locale the locale for which a resource bundle is desired
    * @exception NullPointerException if <code>baseName</code> or <code>locale</code> is <code>null</code>
    * @exception MissingResourceException if no resource bundle for the specified base name can be found
    * @return a resource bundle for the given base name and locale
    */
   public static final ResourceBundle getResourceBundle(String className, Locale locale, Task task, boolean bErrorIfNotFound, ClassLoader classLoader)
   {
	   MissingResourceException ex = null;
	   ResourceBundle resourceBundle = null;
	   try {
		   resourceBundle = ResourceBundle.getBundle(className, locale);
	   } catch (MissingResourceException e) {
		   ex = e;
	   }
	   
	   if (resourceBundle == null)
       {
		   try {
			   if (OsgiUtil.getClassFinder() != null)
				   resourceBundle = OsgiUtil.getClassFinder().findResourceBundle(className, locale);	// Try to find this class in the obr repos
		   } catch (MissingResourceException e) {
			   ex = e;
		   }
		   if (resourceBundle == null)
		       OsgiUtil.handleClassException(null, className, task, bErrorIfNotFound);	// May throw MissingResourceException
       }
	   
	   if (resourceBundle == null)
		   if (ex != null)
			   throw ex;
	   
       return resourceBundle;
   }
   /**
    * Convert this encoded string back to a Java Object.
    * TODO This is expensive, I need to synchronize and use a static writer.
    * @param string The string to convert.
    * @return The java object.
    */
   public static Object convertStringToObject(String string, Task task, boolean bErrorIfNotFound)
   {
	   if (string == null)
		   return null;
      
	   Object object  = null;
       try {
    	   object = OsgiUtil.convertStringToObject(string);
       } catch (ClassNotFoundException e) {
		   if (OsgiUtil.getClassFinder() != null)
		   {
			   String className = null;
			   int startClass = e.getMessage().indexOf('\'') + 1;
			   int endClass = e.getMessage().indexOf('\'', startClass);
			   if (endClass != -1)
				   className = e.getMessage().substring(startClass, endClass);
			   object = OsgiUtil.getClassFinder().findResourceConvertStringToObject(className, string);	// Try to find this class in the obr repos
		   }
    	   if (object == null)
    	       OsgiUtil.handleClassException(e, null, task, bErrorIfNotFound);
       }
       
       return object;
   }
   /**
    * Convert this encoded string back to a Java Object.
    * TODO This is expensive, I need to synchronize and use a static writer.
    * @param string The string to convert.
    * @return The java object.
    * @throws ClassNotFoundException 
    */
   public static Object convertStringToObject(String string)
   		throws ClassNotFoundException
   {
       if ((string == null) || (string.length() == 0))
           return null;
       try {
           InputStream reader = new ByteArrayInputStream(string.getBytes(Constant.OBJECT_ENCODING));//Constants.STRING_ENCODING));
           ObjectInputStream inStream = new ObjectInputStream(reader);
           Object obj = inStream.readObject();
           reader.close();
           inStream.close();
           return obj;
       } catch (IOException ex)    {
           ex.printStackTrace();   // Never
       }
       return null;
   }
   /**
    * 
    * @param ex
    * @param className
    * @param task
    * @param bErrorIfNotFound
    */
   public static void handleClassException(Exception ex, String className, Task task, boolean bErrorIfNotFound)
   {
       if (bErrorIfNotFound)
       {
           Util.getLogger().warning("Error on create class: " + className);
           if (ex != null)
        	   ex.printStackTrace();
           if (task != null)
               task.setLastError("Error on create class: " + className);
       }
       
   }
}
