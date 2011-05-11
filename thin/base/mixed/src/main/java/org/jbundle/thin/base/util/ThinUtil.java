package org.jbundle.thin.base.util;

import java.rmi.RemoteException;

import org.jbundle.model.PropertyOwner;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.util.osgi.ClassService;
import org.jbundle.util.osgi.finder.ClassServiceImpl;


/**
 * Thin specific static utility methods.
 */
public class ThinUtil extends Util
{
   /**
    * Get this URL minus the nav bars
    * @param strURL
    * @param bHelp Add help param
    * @param bNoNav Add no nav bars params
    * @param bLanguage Add language param
    * @return
    */
   public static String fixDisplayURL(String strURL, boolean bHelp, boolean bNoNav, boolean bLanguage, PropertyOwner propertyOwner)
   {
       if ((strURL == null) || (strURL.length() == 0))
    	   return strURL;
       if ((strURL != null)
               && (strURL.length() > 0)
               && (strURL.charAt(0) != '?'))
    	   strURL = '?' + strURL;
       if (bHelp)
    	   strURL = Util.addURLParam(strURL, Params.HELP, Constants.BLANK);
       if (bNoNav)
       {
           strURL = Util.addURLParam(strURL, Params.MENUBARS, "No");
           strURL = Util.addURLParam(strURL, Params.NAVMENUS, "No");
           strURL = Util.addURLParam(strURL, Params.LOGOS, "No");
           strURL = Util.addURLParam(strURL, Params.TRAILERS, "No");  // Don't need outside frame stuff in a window
       }
       if (bLanguage)
       {
    	   String strLanguage = null;
    	   if (propertyOwner != null)
    		   strLanguage = propertyOwner.getProperty("helplanguage");
    	   if ((strLanguage == null) || (strLanguage.length() == 0))
    		   if (propertyOwner != null)
    			   strLanguage = propertyOwner.getProperty(Params.LANGUAGE);
    	   if ((strLanguage != null) && (strLanguage.length() > 0))
    		   strURL = Util.addURLParam(strURL, Params.LANGUAGE, strLanguage);
       }
       return strURL;
   }
   /**
    * Get the remote table reference.
    * If you want the remote table session, call this method with java.rmi.server.RemoteStub.class.
    * @classType The base class I'm looking for (If null, return the next table on the chain) 
    * @return The remote table reference.
    */
   public static RemoteTable getRemoteTableType(RemoteTable tableRemote, Class<?> classType)
   {
       if ((classType == null) || (tableRemote == null) || (classType.isAssignableFrom(tableRemote.getClass())))
           return tableRemote;
       RemoteTable remoteTable = null;
       if (!(tableRemote instanceof java.rmi.server.RemoteStub)) // No need to actually do the remote call
           if (!(tableRemote instanceof java.rmi.server.UnicastRemoteObject))
               if (!(tableRemote instanceof java.lang.reflect.Proxy)) // No need to actually do the remote call
       {
           try {
               remoteTable = tableRemote.getRemoteTableType(classType);
           } catch (RemoteException ex)    {
               // Never.
           }
       }
       if (classType == java.rmi.server.RemoteStub.class)  // Yeah I know this has already been done, but it is possible the EJB server may be forcing me to use a proxy
           if (remoteTable == null)
               return tableRemote;    // If you're asking for the last in the chain, that's me
       return remoteTable;
   }
}
