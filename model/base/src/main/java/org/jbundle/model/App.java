/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.jbundle.model.screen.BaseAppletReference;

/**
 * A Application contains all of a single user's apps.
 * For client apps, there is only one Application class, for server apps there
 * will be an Application class for each client.
 */
public interface App
    extends Service, Freeable, PropertyOwner
{
    /**
     * Initialize the Application.
     * @param env Environment is ignored in the thin context.
     * @param strURL The application parameters as a URL.
     * @param args The application parameters as an initial arg list.
     * @param applet The application parameters coming from an applet.
     */
    public void init(Object env, Map<String,Object> properties, Object applet);
    /**
     * Get the current language.
     * @param bCheckLocaleAlso If true, and language has not been set, return the system's language
     * @return The current language code.
     */
    public String getLanguage(boolean bCheckLocaleAlso);
    /**
     * Look this key up in the current resource file and return the string in the local language.
     * If no resource file is active, return the string.
     * @param string The key to lookup.
     * @return The local string.
     */
    public String getString(String string);
    /**
     * Get this standard resource.
     * This is a utility method for loading and caching resources.
     * The returned Resources object can be called just like a resource object with some
     * extra functions.
     * @param strResourceName The class name of the resource file to load.
     * @param bReturnKeyOnMissing Return the string key if the resource is missing (typically, pass true).
     * @return The Resources object.
     */
    public ResourceBundle getResources(String strResourceName, boolean bReturnKeyOnMissing);
    /**
     * Get the system record owner.
     * This is used to keep from indefinite recursion.
     * This is used in overriding apps
     */
     public PropertyOwner getSystemRecordOwner();
     /**
      * Set the current language.
      * Change the current resource bundle to the new language.
      * <p>In your overriding code you might convert the actual language names to the two letter code:
      * <code>
      * if (language != null) if (language.length() > 2) if (language.indexOf('_') == -1)
      *  language = this.findLocaleFromLanguage(language);
      * </code>
      * <p>In your code, you should update all the current display fields when the language is changed.
      * @param language java.lang.String The language code.
      */
     public void setLanguage(String language);
     /**
      * Convert this filename to a valid URL.
      * @param filename The filename to find.
      * @param applet The (optional) applet.
      * @return A URL to this filename (or null if not found).
      */
     public URL getResourceURL(String filename, BaseAppletReference applet);
     /**
      * Get the error text for this security error.
      */
     public String getSecurityErrorText(int iErrorCode);
     /**
      * Does the current user have permission to access this resource.
      * @param classResource The resource to check the permission on.
      * @return NORMAL_RETURN if access is allowed, ACCESS_DENIED or LOGIN_REQUIRED otherwise.
      */
     public int checkSecurity(String strClassResource);
     /**
      * Change the current user to this user and (optionally) validate password.
      * @param strPassword
      * @param strDomain The domain
      * @param strUser
      * @return normal_return if successful
      */
     public int login(Task task, String strUserName, String strPassword, String strDomain);
     /**
      * Add this user's params to the URL, so when I submit this to the server it can authenticate me.
      * @param strURL
      * @return
      */
     public String addUserParamsToURL(String strURL);
     /**
      * Get the connection to the server for this applet.
      * Optionally create the server connection.
      * @param localTaskOwner The task that will own this remote task (or application) server) [If null, get the app server].
      * @param strUserID The user id (or name) to initialize the server's application to.
      * @param bCreateIfNotFound If the server is null, initialize the server.
      * @return The server object (application defined).
      */
     public Object getRemoteTask(Task localTaskOwner, String strUserID, boolean bCreateIfNotFound);
     /**
      * Get a connection to the server for this applet.
      * @param localTaskOwner The task that will own this remote task (or application) server) [If null, get the app server].
      * @return The server object (application defined).
      */
     public Object getRemoteTask(Task localTaskOwner);
     /**
      * Add this session, screen, or task that belongs to this application.
      * @param objSession Session to remove.
      * @return Number of remaining sessions still active.
      */
     public int addTask(Task task, Object remoteTask);
     /**
      * Remove this session, screen, or task that belongs to this application.
      * @param objSession Session to remove.
      * @return True if all the potential main tasks have been removed.
      */
     public boolean removeTask(Task task);
}
