package org.jbundle.model;

import java.util.Map;

/** 
 * The <code>Remote</code> interface serves to identify interfaces whose
 * methods may be invoked from a non-local virtual machine.
 */
public interface RemoteTarget {

    /**
     * Do a remote action.
     * @param strCommand Command to perform remotely.
     * @return boolean success.
     * @throws RemoteException 
     */
    public Object doRemoteAction(String strCommand, Map<String, Object> properties) throws DBException, RemoteException;
}
