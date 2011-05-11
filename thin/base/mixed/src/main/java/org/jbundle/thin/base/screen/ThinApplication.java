package org.jbundle.thin.base.screen;

import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JApplet;

import org.jbundle.model.BaseAppletReference;
import org.jbundle.model.Freeable;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabaseParent;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.ThinUtil;
import org.jbundle.thin.base.util.ThinMenuConstants;

public class ThinApplication extends Application {
    /**
     * The (optional) DatabaseOwner (see PDatabaseManager).
     * Note: This is only used in thin, in the thick model, pDatabaseOwner is in Environment.
     */
    protected ThinPhysicalDatabaseParent m_PhysicalDatabaseParent = null;

    /**
     * Default constructor.
     */
    public ThinApplication()
    {
        super();
    }
    /**
     * Constructor.
     * Pass in the possible initial parameters.
     * @param env Environment is ignored in the thin context.
     * @param strURL The application parameters as a URL.
     * @param args The application parameters as an initial arg list.
     * @param applet The application parameters coming from an applet.
     */
    public ThinApplication(Object env, Map<String,Object> properties, JApplet applet)
    {
        this();
        this.init(env, properties, applet); // The one and only
    }
    /**
     * Initialize the Application.
     * @param env Environment is ignored in the thin context.
     * @param strURL The application parameters as a URL.
     * @param args The application parameters as an initial arg list.
     * @param applet The application parameters coming from an applet.
     */
    public void init(Object env, Map<String,Object> properties, JApplet applet)
    {
    	super.init(env, properties, applet);
    }
    /**
     * Free all the resources belonging to this application.
     */
    public void free()
    {
    	super.free();
        if (m_PhysicalDatabaseParent instanceof Freeable)
        {
            ((Freeable)m_PhysicalDatabaseParent).free();
            m_PhysicalDatabaseParent = null;
        }
    }
    /**
     * Get the (optional) raw data database manager.
     * Note: This is only used in thin, in the thick model, pDatabaseOwner is in Environment.
     * @return The pDatabaseOwner (returns an object, so this package isn't dependent on PDatabaseOwner).
     */
    public ThinPhysicalDatabaseParent getPDatabaseParent(Map<String,Object> properties, boolean bCreateIfNew)
    {
        if (m_PhysicalDatabaseParent == null)
            if (bCreateIfNew)
        {
            Map<String,Object> map = new Hashtable<String,Object>();
            if (properties != null)
                map.putAll(properties);
            if (map.get(PhysicalDatabaseParent.APP) == null)
                map.put(PhysicalDatabaseParent.APP, this); // Access to the server, etc.
            
            m_PhysicalDatabaseParent = (ThinPhysicalDatabaseParent)ThinUtil.getClassService().makeObjectFromClassName(Constants.ROOT_PACKAGE + "thin.base.db.mem.base.PhysicalDatabaseParent");
            if (m_PhysicalDatabaseParent != null)
            	m_PhysicalDatabaseParent.init(map);  // Init
        }
        if (properties != null)
        {
            for (String strKey : properties.keySet())
            {
                m_PhysicalDatabaseParent.setProperty(strKey, properties.get(strKey));
            }
        }
        return m_PhysicalDatabaseParent;
    }
    /**
     * Display this URL in a web browser.
     * Uses the applet or jnlp context.
     * @param strURL The local URL to display (not fully qualified).
     * @param iOptions ThinMenuConstants.HELP_WINDOW_CHANGE If help pane is already displayed, change to this content.
     * @param The applet (optional).
     * @return True if successfully displayed.
     */
    public boolean showTheDocument(String strURL, BaseAppletReference applet, int iOptions)
    {
		boolean bIsHelpScreen = false;
		if ((strURL.indexOf("JHelp") != -1) ||
			(strURL.indexOf(Params.HELP + "=")) != -1)
				bIsHelpScreen = true;
		if (bIsHelpScreen)
			if ((iOptions & ThinMenuConstants.HELP_WINDOW_CHANGE) != 0)
            {	// Special case - if help is currently displayed, display this new help screen
                if ((applet != null) && (((BaseApplet)applet).getHelpView().getHelpPane().isVisible()))
    				iOptions = ThinMenuConstants.HELP_PANE_OPTION | ThinMenuConstants.HELP_WINDOW_CHANGE;
            	else
            		return false;	// Don't refresh help if no current pane
            }
        boolean bSuccess = false;
        URL url = null;
        String strProtocol = null;
        if (strURL != null)
            if (strURL.indexOf(':') != -1)
        {
            strURL.substring(strURL.indexOf(':') + 1);
            strProtocol = strURL.substring(0, strURL.indexOf(':'));
        }
        if ((Params.FAX.equalsIgnoreCase(strProtocol)) || (Params.PHONE.equalsIgnoreCase(strProtocol)))
            url = null;
        else
        {
            strProtocol = null;
            url = this.getResourceURL(strURL, applet);
        }
        if ((url != null) || (strProtocol != null))
        {
            if ((applet != null)
                    && (((BaseApplet)applet).getHelpView() != null)
                    && (((iOptions & 1) == 0) && ((iOptions & Constants.USE_NEW_WINDOW) == 0) && ((iOptions & ThinMenuConstants.HELP_WEB_OPTION) == 0)	// Use same window
                	|| (bIsHelpScreen)))
            {
                if (((applet != null) && (((BaseApplet)applet).getHelpView().getHelpPane().isVisible()))
                	|| ((ThinMenuConstants.HELP_PANE_OPTION & iOptions) == ThinMenuConstants.HELP_PANE_OPTION))
            	{
            		((BaseApplet)applet).getHelpView().linkActivated(url, (BaseApplet)applet, Constants.DONT_PUSH_HISTORY);	// For now
            	}
                bSuccess = true;	// Even if the help didn't display
            }
            if (!bSuccess)
            	if ((applet != null) && (url != null) && (applet.getApplet() != null))
            {
                applet = applet.getApplet();    // Get the JApplet if not standalone.
                ((BaseApplet)applet).getAppletContext().showDocument(url, "_blank");
                bSuccess = true;
            }
            if (!bSuccess)
            {
                if ((this.getMuffinManager() != null) && (url != null))
                    if (this.getMuffinManager().isServiceAvailable())
                        bSuccess = this.getMuffinManager().showTheDocument(url);
                if (!bSuccess)
                {
                    Map<String,Object> properties = new Hashtable<String,Object>();
                    properties.put(Params.SCREEN, org.jbundle.thin.base.screen.util.html.JHelpView.class.getName());
                    if (url != null)
                        strProtocol = url.getProtocol();
                    if (MAIL_TO.equalsIgnoreCase(strProtocol))
                        properties.put(Params.SCREEN, org.jbundle.thin.base.screen.util.mail.JMailView.class.getName());
                    else if (Params.FAX.equalsIgnoreCase(strProtocol))
                        properties.put(Params.SCREEN, org.jbundle.thin.base.screen.util.mail.JFaxView.class.getName());
                    else if (Params.PHONE.equalsIgnoreCase(strProtocol))
                        properties.put(Params.SCREEN, org.jbundle.thin.base.screen.util.mail.JPhoneView.class.getName());
                    if (url != null)
                        properties.put(Params.URL, url.toString());
                    else
                        properties.put(Params.URL, strURL);
                    this.makeHelpScreen(this, applet, properties);
                    return true;    // Success
                }
            }
        }
        return false; // failure
    }
    /**
     * Set the help pane.
     * @return the help pane.
     */
    public void makeHelpScreen(Application app, BaseAppletReference applet, Map<String,Object> properties)
    {
        BaseApplet appletHTML = new BaseApplet();
        appletHTML.initTask(this, properties);
        new JBaseFrame(this.getString(Constants.HELP), appletHTML);
        if (applet != null)
        	((BaseApplet)applet).setHelpView(appletHTML.getHelpView());    	
    }
}
