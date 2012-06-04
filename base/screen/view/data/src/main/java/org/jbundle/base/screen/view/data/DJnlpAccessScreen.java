/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.data;

/**
 * @(#)DJnlpAccessScreen.java   0.00 12-Feb-97 Don Corley
 * 
 * Note: This class is obsolete. I now just use the osgi-webstart servlet
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.util.osgi.ClassFinder;
import org.jbundle.util.osgi.ClassService;
import org.jbundle.util.osgi.finder.ClassServiceUtility;
import org.jbundle.util.webapp.base.HttpServiceActivator;
import org.jbundle.util.webapp.base.HttpServiceTracker;


/**
 * DJnlpAccessScreen sends a JNLP file down the pipe.
 */
public class DJnlpAccessScreen extends DDataAccessScreen
{
    public static final boolean SPLASH_ENABLED = false;

    /**
     * Constructor.
     */
    public DJnlpAccessScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The DataAccessScreen model.
     * @param bEditableControl If true, this view is editable.
     */
    public DJnlpAccessScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The DataAccessScreen model.
     * @param bEditableControl If true, this view is editable.
     */
    public void init(ScreenComponent model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Process an HTML get or post.
     * @param req The servlet request.
     * @param res The servlet response object.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void sendData(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
//        res.setContentType("application/x-java-jnlp-file");
//x        PrintWriter out = new PrintWriter(res.getOutputStream());
//        PrintWriter out = res.getWriter();

//        this.printXML(req, out);
//        out.flush();
        
        ClassService classService = ClassServiceUtility.getClassService();
        if (classService == null)
            return;    // Never
        ClassFinder classFinder = classService.getClassFinder(null);
        if (classFinder == null)
            return;
        HttpServiceActivator service = (HttpServiceActivator)classFinder.getClassBundleService(null, WEB_START_ACTIVATOR_CLASS, null, null, -1);
        HttpServiceTracker tracker = service.getServiceTracker();
        if (tracker == null)
            return;
        Servlet servlet = tracker.getServlet();
        if (servlet == null)
            return;
        servlet.service((HttpServletRequest)req, (HttpServletResponse)res);
    }
    public static final String WEB_START_ACTIVATOR_CLASS = org.jbundle.util.osgi.webstart.HttpServiceActivator.class.getName();
    //public static final String THINBASE_JAR = "lib/jbundle-thin-base";
    /**
     * This hack sends the hard-coded JNLP file down the pipe.
     * @param req The servlet request.
     * @param res The servlet response object.
     */
    /*
    public void printXML(HttpServletRequest req, PrintWriter out)
    {
        ServletTask task = (ServletTask)this.getTask();
    	ResourceBundle reg = ((BaseApplication)task.getApplication()).getResources("Jnlp", false);
    	Map<String,Object> properties = task.getRequestProperties(task.getServletRequest(), true);
    	String strJnlp = null;
    	if (properties.get("webStart") != null)
    		strJnlp = reg.getString(properties.get("webStart").toString());
    	if ((strJnlp == null) || (strJnlp.length() == 0))
    		strJnlp = reg.getString("default");	// Default Jnlp template
    	StringBuilder sbJnlp = new StringBuilder(strJnlp);
    	
        String strMainClass = (String)properties.get(DBParams.APPLET);
        if ((strMainClass == null)
            || (strMainClass.length() == 0)
                || (strMainClass.indexOf("Applet") != -1))
        {
            strMainClass = ThinApplet.class.getName();
            properties.put(DBParams.APPLET, strMainClass);
        }
        if (strMainClass.charAt(0) == '.')
            strMainClass = DBConstants.ROOT_PACKAGE + strMainClass.substring(1);
        String splash = (String)properties.get(Splash.SPLASH);
        if (splash != null)
            if (SPLASH_ENABLED)
        {
            strMainClass = Splash.class.getName();      // If they specified a splash screen, then splash is the main class
            properties.put(HtmlConstants.JNLPJARS, THINBASE_JAR);
        }

        String strServer = req.getRequestURL().toString();
        int iIndex = strServer.indexOf("/" + Constants.DEFAULT_SERVLET, 7);   // End of the host name
        if (iIndex == -1)
            iIndex = strServer.indexOf('/', 7);   // End of the host name
        if (iIndex != -1)
            strServer = strServer.substring(0, iIndex);

        String strTitle = (String)properties.get("title");
        if (strTitle == null)
            strTitle = "Tourgeek Application";
        String strDesc = (String)properties.get("description");
        if (strDesc == null)
            strDesc = "Tourgeek Application";
        String strIcon = (String)properties.get("icon");
        if (strIcon == null)
            strIcon = "images/tour/icons/Tour.gif";
        String strVersion = (String)properties.get("version");
        if (strVersion == null)
            strVersion = "1.6+";

        Utility.replace(sbJnlp, "{codebase}", strServer);
        Utility.replace(sbJnlp, "{title}", strTitle);
        Utility.replace(sbJnlp, "{desc}", strDesc);
        Utility.replace(sbJnlp, "{icon}", strIcon);
        Utility.replace(sbJnlp, "{javaVersion}", strVersion);

        String appVersion = (String)properties.get("jbundleversion");		// App/jar version
        if (appVersion == null)
        	appVersion = "";
        else if (appVersion.length() == 0)
        {
        	if (reg.getString("jbundleversion") != null)
            	appVersion = "-" + reg.getString("jbundleversion");
        }
        else
        	appVersion = "-" + appVersion;
        String strListJars = (String)properties.get(HtmlConstants.JNLPJARS);
        if (strListJars == null)
            strListJars = THINBASE_JAR;
        StringTokenizer st = new StringTokenizer(strListJars, ",");
        String listParts = (String)properties.get("parts");
        StringTokenizer stParts = null;
        if (listParts != null)
        	stParts = new StringTokenizer(listParts, ",");
        boolean bFirstTime = true;
        StringBuffer sbJars = new StringBuffer();
        while (st.hasMoreElements())
        {
            String strExtension = st.nextToken();
            int iIndexDot = strExtension.lastIndexOf(".jar");
            if (iIndexDot != -1)
                strExtension = strExtension.substring(0, iIndexDot);
            int iIndexSlash = strExtension.lastIndexOf('/');
            String strName = null;
            if (stParts != null)
            	strName = stParts.nextToken();
            if (strName == null)
            {
            	strName = strExtension;
            	if (iIndexSlash != -1)
            		strName = strExtension.substring(iIndexSlash + 1);
            }
            sbJars.append("    <jar href=\"" + strExtension + appVersion + ".jar\" part=\"" + strName + "\" />\n");
            if (bFirstTime)
            	sbJars.append(" <package name=\"" + strMainClass + "\" part=\"" + strName + "\" />\n");
            sbJars.append("\n");
            bFirstTime = false;
        }
        Utility.replace(sbJnlp, "{" + HtmlConstants.JNLPJARS + "}", sbJars.toString());

        String strListJnlp = (String)properties.get(HtmlConstants.JNLPEXTENSIONS);
        if (strListJnlp == null)
            strListJnlp = "docs/jnlp/thin,docs/jnlp/thintest";
        st = new StringTokenizer(strListJnlp, ",");
        sbJars = new StringBuffer();
        while (st.hasMoreElements())
        {
            String strExtension = st.nextToken();
            int iIndexDot = strExtension.lastIndexOf('.');
            if (iIndexDot != -1)
                strExtension = strExtension.substring(0, iIndexDot);
            int iIndexSlash = strExtension.lastIndexOf('/');
            String strName = strExtension;
            if (iIndexDot != -1)
                strName = strExtension.substring(iIndexSlash + 1);
            sbJars.append(" <extension name=\"" + strName + "\"\n");
            sbJars.append("   href=\"" + strExtension + ".jnlp\">\n");
            sbJars.append(" </extension>\n");
            sbJars.append("\n");
        }
        Utility.replace(sbJnlp, "{" + HtmlConstants.JNLPEXTENSIONS + "}", sbJars.toString());

//        out.println(" <extension name=\"thin\"");
//        out.println("   href=\"docs/jnlp/thin.jnlp\">");
//        out.println(" </extension>");
//        out.println("");
//        out.println(" <extension name=\"thintest\"");
//        out.println("   href=\"docs/jnlp/thintest.jnlp\">");
//        out.println(" </extension>");
//        out.println("");

        if (DBParams.WEBSTART_APPLET_PARAM.equalsIgnoreCase((String)properties.get(DBParams.DATATYPE)))
        {
            String strWidth = (String)properties.get(HtmlConstants.WIDTH);
            if (strWidth == null)
                strWidth = "600";
            String strHeight = (String)properties.get(HtmlConstants.HEIGHT);
            if (strHeight == null)
                strHeight = "600";
            Utility.replace(sbJnlp, "{appStart}", "  <applet-desc name=\"tour\" width=\"" + strWidth + "\" height=\"" + strHeight + "\"\n");
        }
        else
        {
            sbJars.append("\n");
            Utility.replace(sbJnlp, "{appStart}", "  <application-desc\n");
            if (splash == null)
                properties.remove(DBParams.APPLET);
        }
        Utility.replace(sbJnlp, "{mainClass}", strMainClass);
        String strName = "tourapp";
        if ((this.getProperty("name") != null) && (this.getProperty("name").length() > 0))
        	strName = this.getProperty("name");
        if (sbJnlp.indexOf("{name}") != -1)
        	Utility.replace(sbJnlp, "{name}", strName);

        properties.remove(HtmlConstants.HEIGHT);
        properties.remove(HtmlConstants.ARCHIVE);
        properties.remove(HtmlConstants.WIDTH);
        properties.remove(HtmlConstants.ID);
        properties.remove(HtmlConstants.CODEBASE);
        properties.remove(HtmlConstants.JNLPEXTENSIONS);
        properties.remove(HtmlConstants.JNLPJARS);
        properties.remove("code");
        properties.remove(DBParams.APPLET);
        properties.remove("webStart");

        if (DBConstants.BLANK.equalsIgnoreCase((String)properties.get(DBParams.USER_ID)))
        {       // They want me to fill in the user name
            properties.put(DBParams.USER_ID, this.getTask().getApplication().getProperty(DBParams.USER_ID));
            if (this.getTask().getApplication().getProperty(DBParams.AUTH_TOKEN) != null)
                properties.put(DBParams.AUTH_TOKEN, this.getTask().getApplication().getProperty(DBParams.AUTH_TOKEN));
        }

        sbJars = new StringBuffer();
        for (Map.Entry<String, Object> entry : properties.entrySet())
        {
        	if (sbJars.length() > 0)
        		sbJars.append('\n');
            String strKey = (String)entry.getKey();
            if (DBParams.DATATYPE.equalsIgnoreCase(strKey))
            	continue;
            Object objValue = entry.getValue();
            String strValue = null;
            if (objValue != null)
                strValue = objValue.toString();
            if (strValue != null)
                if (strValue.length() > 0)
                {
                    if (DBParams.WEBSTART_APPLET_PARAM.equalsIgnoreCase((String)properties.get(DBParams.DATATYPE)))
                    	sbJars.append(" <param name=\""+ strKey + "\" value=\"" + strValue + "\"/>"); 
                    else
                    	sbJars.append(" <argument>" + strKey + "=" + strValue + "</argument>");
                }
        }
        Utility.replace(sbJnlp, "{args}", sbJars.toString());
        
        if (DBParams.WEBSTART_APPLET_PARAM.equalsIgnoreCase((String)properties.get(DBParams.DATATYPE)))
            Utility.replace(sbJnlp, "{appEnd}", "  </applet-desc>");
        else
            Utility.replace(sbJnlp, "{appEnd}", "  </application-desc>");

        out.println(sbJnlp);
    }
    */
}
