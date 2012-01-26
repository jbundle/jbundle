package org.jbundle.base.model;

import org.jbundle.model.App;
import org.jbundle.model.message.MessageManager;

public interface MessageApp extends App {

    /**
     * Set the environment (This is used primarily for testing, since env is static).
     * @param env The parent enviroment.
     */
    public void setEnvironment(Object env);
    /**
     * Get this Message Queue (or create one if this name doesn't exist).
     */
    public MessageManager getThickMessageManager();
}
