package org.jbundle.thin.base.util;

import java.rmi.RemoteException;

import org.jbundle.thin.base.remote.RemoteTable;


/**
 * Thin specific static utility methods.
 */
public class ThinUtil extends Util
{
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
