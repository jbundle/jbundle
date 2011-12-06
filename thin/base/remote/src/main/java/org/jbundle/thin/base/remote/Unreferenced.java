package org.jbundle.thin.base.remote;

/**
 * A remote object implementation should implement the
 * <code>Unreferenced</code> interface to receive notification when there are
 * no more clients that reference that remote object.
 */
public interface Unreferenced {
    /**
     * Called by the RMI runtime sometime after the runtime determines that
     * the reference list, the list of clients referencing the remote object,
     * becomes empty.
     * @since JDK1.1
     */
    public void unreferenced();
}
