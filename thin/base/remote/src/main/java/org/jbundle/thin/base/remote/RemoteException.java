package org.jbundle.thin.base.remote;

import java.io.Serializable;

/**
 * Remote exception such as connection and transfer problems.
 * Modeled after java.rmi.RemoteException (which isn't allow in android).
 * @author don
 *
 */
public class RemoteException extends java.rmi.RemoteException
    implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a <code>RemoteTaskException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     */
    public RemoteException() {
        super();
    }

    /**
     * Constructs a <code>RemoteTaskException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     */
    public RemoteException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>RemoteException</code> with the specified detail
     * message and cause.  This constructor sets the {@link #detail}
     * field to the specified <code>Throwable</code>.
     *
     * @param s the detail message
     * @param cause the cause
     */
    public RemoteException(String s, Throwable cause) {
        super(s, cause);
    }
}
