package org.jbundle.base.screen.model.html;

/**
 *  AppletHTMLScreen.
 *  Copyright � 1997 jbundle.org. All rights reserved.
 */

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.control.servlet.ServletTask;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Converter;

/**
 *  AppletHtmlScreen - Display an applet in an HTML screen.
 */
public class AppletHtmlScreen extends BaseScreen
{

    /**
     * Constructor.
     */
    public AppletHtmlScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public AppletHtmlScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize the member fields.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Specify the fields to display.
     */
    public void setupSFields()
    {
    }
    /**
     * Get the screen title.
     * @return The screen title.
     */
    public String getTitle()
    {
        return "Java Window"; // ** HACK **
    }
    /**
     * 
     * @return
     */
    public Map<String,Object> getAppletProperties(Map<String,Object> propApplet)
    {
        String strAppletScreen = this.getProperty(DBParams.APPLET);   // Applet page
        if (strAppletScreen == null)
            strAppletScreen = "org.jbundle.Main"; //++Main.class.getName();
//x          strAppletScreen = DBConstants.ROOT_PACKAGE + "Main.class";
//x        if (strAppletScreen.indexOf("class") == -1)
//x            strAppletScreen += ".class";
        propApplet.put(DBParams.APPLET, strAppletScreen);
        String strWidth = this.getProperty(HtmlConstants.WIDTH);
        if (strWidth == null)
            strWidth = "100%";
        propApplet.put(HtmlConstants.WIDTH, strWidth);
        String strHeight = this.getProperty(HtmlConstants.HEIGHT);
        if (strHeight == null)
            strHeight = "98%";
        propApplet.put(HtmlConstants.HEIGHT, strHeight);
        String strArchive= this.getProperty(HtmlConstants.ARCHIVE);     // Display screen
        if (strArchive != null)
            propApplet.put(HtmlConstants.ARCHIVE, strArchive);
        String strID= this.getProperty(HtmlConstants.ID);   // Display screen
        if (strID != null)
            propApplet.put(HtmlConstants.ID, strID);
        String strCodebase= this.getProperty(HtmlConstants.CODEBASE);   // Display screen
        if (strCodebase == null)
            strCodebase = HtmlConstants.DEFAULT_CODEBASE;
        propApplet.put(HtmlConstants.CODEBASE, strCodebase);
        String strName= this.getProperty(HtmlConstants.NAME);   // Display screen
        if (strName == null)
            strName = HtmlConstants.DEFAULT_NAME;
        propApplet.put(HtmlConstants.NAME, strName);

        Task task = this.getTask();
        Map<String,Object> properties = null;
        if (task instanceof ServletTask)
            properties = ((ServletTask)task).getRequestProperties(((ServletTask)task).getServletRequest(), true);
        else
            properties = task.getProperties();
        properties.remove(DBParams.APPLET);
        properties.remove(HtmlConstants.HEIGHT);
        properties.remove(HtmlConstants.ARCHIVE);
        properties.remove(HtmlConstants.WIDTH);
        properties.remove(HtmlConstants.ID);
        properties.remove(HtmlConstants.CODEBASE);
        properties.remove(DBParams.USER_NAME);
        
        if (DBConstants.BLANK.equalsIgnoreCase((String)properties.get(DBParams.USER_ID)))
        {       // They want me to fill in the user name
            properties.put(DBParams.USER_ID, this.getTask().getApplication().getProperty(DBParams.USER_ID));
            if (this.getTask().getApplication().getProperty(DBParams.AUTH_TOKEN) != null)
                properties.put(DBParams.AUTH_TOKEN, this.getTask().getApplication().getProperty(DBParams.AUTH_TOKEN));
        }

        return properties;
    }
}
