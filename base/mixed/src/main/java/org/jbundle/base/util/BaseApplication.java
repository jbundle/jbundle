package org.jbundle.base.util;

import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JApplet;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.screen.model.util.Resources;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.ThinPhysicalDatabaseParent;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.screen.ThinApplication;


/**
 * A BaseApplication encompasses all of a single user's apps.
 * For example, a standalone app, an applet, an ongoing or stateless HTML BaseApplication,
 * a user's server BaseApplication, or a user's EJB app server BaseApplication.
 */
public class BaseApplication extends ThinApplication
    implements DatabaseOwner
{ 
    /**
     * The parent environment.
     */
    protected Environment m_env = null;
    /**
     * The list of databases.
     */
    protected DatabaseCollection m_databaseCollection = null;   // List of database lists (Each list is by DB name)

    /**
     * Default constructor.
     */
    public BaseApplication()
    {
        super();
    }
    /**
     * Constructor.
     * @param env The Environment.
     * @param strURL The application parameters as a URL.
     * @param args The application parameters as an initial arg list.
     * @param applet The application parameters coming from an applet.
     */
    public BaseApplication(Object env, Map<String,Object> properties, JApplet applet)
    {
        this();
        m_resources = null;
        this.init(env, properties, applet); // The one and only
    }
    /**
     * Initializes the BaseApplication.
     * Usually you pass the object that wants to use this sesssion.
     * For example, the applet or BaseApplication.
     * @param env The Environment.
     * @param strURL The application parameters as a URL.
     * @param args The application parameters as an initial arg list.
     * @param applet The application parameters coming from an applet.
     */
    public void init(Object env, Map<String,Object> properties, JApplet applet)
    {
        m_resources = null;
        super.init(env, properties, applet);
        if (m_env == null)
        {
            if (env != null)
                m_env = (Environment)env;
            else
                m_env = this.getEnvironment();
        }
        m_env.addApplication(this);
        
        m_databaseCollection = new DatabaseCollection(this);
    }
    /**
     * Free all the resources belonging to this applet. If all applet screens are closed, shut down the applet.
     */
    public void free()
    {
        if (m_env != null)
        {
            m_env.removeApplication(this);
            m_env.freeIfDone();
        }
        
        super.free();

        if (m_databaseCollection != null)
            m_databaseCollection.free();
        m_databaseCollection = null;

        m_env = null;

        m_resources = null;
    }
    /**
     * Get the base properties to pass to the server.
     * @return The base server properties
     */
    public Map<String,Object> getServerProperties()
    {
        Map<String,Object> properties = super.getServerProperties();
        return BaseDatabase.addDBProperties(properties, this);
    }
    /**
     * Get the User ID.
     * @return The userID as a string.
     */
    public String getUserID()
    {
        return this.getProperty(DBParams.USER_ID);
    }
    /**
     * Get the user name.
     * @return The user name as a string.
     */
    public String getUserName()
    {
        return this.getProperty(DBParams.USER_NAME);
    }
    /**
     * Get the environment.
     * @return The parent environment.
     */
    public Environment getEnvironment()
    {
        if (m_env != null)
            return m_env;
        return Environment.getEnvironment(null);    // Hopefully never
    }
    /**
     * Set the environment (This is used primarily for testing, since env is static).
     * @param env The parent enviroment.
     */
    public void setEnvironment(Environment env)
    {
        m_env = env;
    }
    /**
     * Given a class name in the program package, get this resource's class name in the res package.
     * @param strBasePackage A class name in the same program directory as the res class.
     * @param strResourceClass The base resource class name.
     * @return The full resource class name.
     */
    public String getResourceClassName(String strBasePackage, String strResourceClass)
    {
        if (strResourceClass == null)
            strResourceClass = strBasePackage.substring(strBasePackage.lastIndexOf('.') + 1);
        strBasePackage = strBasePackage.substring(Constants.ROOT_PACKAGE.length());
        strBasePackage = strBasePackage.substring(0, strBasePackage.lastIndexOf('.') + 1);
        strBasePackage = Constants.ROOT_PACKAGE + Constants.RES_SUBPACKAGE + strBasePackage + strResourceClass;
        return strBasePackage;
    }
    /**
     * Get this standard resource.
     * This is a utility method for loading and caching resources.
     * The returned Resources object can be called just like a resource object with some
     * extra functions.
     * @param strResourceName The class name of the resource file to load.
     * @param bReturnKeyOnMissing Return the string key if the resource is missing (typically, pass true).
     * @return The Resources object.
     */
    public ResourceBundle getResources(String strResourceName, boolean bReturnKeyOnMissing)
    {
        if (strResourceName != null)
        {
            if (m_resources instanceof Resources)   // Always
                if (strResourceName.equals(((Resources)m_resources).getResourceName()))
                {
                	((Resources)m_resources).setReturnKeyOnMissing(bReturnKeyOnMissing);
                    return m_resources;     // Already the current resource
                }
        }
        ResourceBundle resources = super.getResources(strResourceName, bReturnKeyOnMissing);
        if (resources != null)
            if (!(resources instanceof Resources))
                resources = new Resources(resources, strResourceName, bReturnKeyOnMissing);
        m_resources = resources;    // Last accessed
        if (m_resources == null)
        {
        	Utility.getLogger().warning("Resource not found: " + strResourceName);
        	if (bReturnKeyOnMissing)
        	    resources = m_resources = new Resources(null, null, true); // Return the key
        }
        return resources;
    }
    /**
     * Get the location of the default resources.
     * @return the default resources.
     */
    public String getResPackage()
    {
        return ".res.base.screen.";
    }
    /**
     * Given the name, either get the open database, or open a new one.
     * @param strDBName The name of the database.
     * @param iDatabaseType The type of database/table.
     * @return The database (new or current).
     */
    public BaseDatabase getDatabase(String strDBName, int iDatabaseType, Map<String, Object> properties)
    {
        return m_databaseCollection.getDatabase(strDBName, iDatabaseType, properties);
    }
    /**
     * Add this database to my database list.<br />
     * Do not call these directly, used in database init.
     * @param database The database to add.
     */
    public void addDatabase(BaseDatabase database)
    {
        m_databaseCollection.addDatabase(database);
    }
    /**
     * Remove this database from my database list.
     * Do not call these directly, used in database free.
     * @param database The database to free.
     * @return true if successful.
     */
    public boolean removeDatabase(BaseDatabase database)
    {
        return m_databaseCollection.removeDatabase(database);
    }
    /**
     * Get the (optional) raw data database manager.
     * @return The pDatabaseOwner (returns an object, so this package isn't dependent on PDatabaseOwner).
     */
    public ThinPhysicalDatabaseParent getPDatabaseParent(Map<String,Object> properties, boolean bCreateIfNew)
    {
        return this.getEnvironment().getPDatabaseParent(properties, bCreateIfNew);
    }
    /**
     * Get this Message Queue (or create one if this name doesn't exist).
     * @param bCreateIfNone
     * NOTE: This is overridden for anything but thin.
     */
    public BaseMessageManager getMessageManager(boolean bCreateIfNone)
    {
        return this.getEnvironment().getMessageManager(this, bCreateIfNone);
    }
    /**
     * Authenticate the token.
     * TODO This method should authenticate the token and return the encoded password so this user can sign-in.
     * @param strAuthentication The encoded authentication token.
     * @return The user's password or null if not authenticated.
     */
    public String authenticateToken(String strAuthentication)
    {
    	return strAuthentication;
    }
}
