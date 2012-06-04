/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model;


/**
 * Environment - Usually only used in thick implementations.
 */
public interface Env
    extends PropertyOwner, Freeable
{
        /**
         * Add an Application to this environment.
         * If there is no default application yet, this is set to the default application.
         * @param application The application to add.
         */
        public void addApplication(App application);
        /**
         * Remove this application from the Environment.
         * @param application The application to remove.
         * @return Number of remaining applications still active.
         */
        public int removeApplication(App application);
        /**
         * Get the application count.
         * @return Number of remaining application still active.
         */
        public int getApplicationCount();
        /**
         * Get the application count.
         * @return Number of remaining application still active.
         */
        public App getApplication(int i);
}
