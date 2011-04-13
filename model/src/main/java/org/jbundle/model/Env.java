package org.jbundle.model;


/**
 * The environment is a singleton that contains the system properties and all the running apps.
 */
public interface Env
    extends PropertyOwner
{
    /**
     * Get this Message Queue (or create one if this name doesn't exist).
     * @param application The parent application
     * @param bCreateIfNotFound Create the manager if not found?
     * @return The message manager.
     */
    public MessageManager getMessageManager(App application, boolean bCreateIfNotFound);

}
