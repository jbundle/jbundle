package org.jbundle.base.screen.view.xml.html;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.html.DefaultHtmlScreen;
import org.jbundle.base.screen.view.xml.XBaseScreen;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.main.db.Menus;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.thread.PrivateTaskScheduler;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * XDefaultHtmlScreen - The default xhtml screen.
 */
public class XDefaultHtmlScreen extends XBaseScreen
{

    /**
     * Constructor.
     */
    public XDefaultHtmlScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XDefaultHtmlScreen(ScreenField model,boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     */
    public void init(ScreenField model, boolean bEditableControl)
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
     * Code to display a Menu.
     * @exception DBException File exception.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        String strParamRecord = this.getProperty(DBParams.RECORD);      // Display record
        String strParamScreen = this.getProperty(DBParams.SCREEN);      // Display screen
//?        String strParamLimit = this.getProperty("limit");   // Display record
        if (strParamRecord == null)
            strParamRecord = DBConstants.ROOT_PACKAGE;
        if (strParamScreen == null)
            strParamScreen = Constants.BLANK;
        String strHTML = reg.getString("Default");
        if ((strHTML == null) || (strHTML.length() == 0))
            strHTML = "<hr>" +
            "<form action=\"/" + HtmlConstants.SERVLET_PATH + "\" method=\"get\">" +
            "<pre>" +
            " Record : <input type=\"text\" length=\"50\" name=\"record\" value=\"" + HtmlConstants.RECORD_TAG + "\"/>\n" +
            " Screen : <input type=\"text\" length=\"50\" name=\"screen\" value=\"" + HtmlConstants.SCREEN_TAG + "\"/>\n" +
            "  Limit : <input type=\"text\" maxlength=\"7\" name=\"limit\" value=\"200\">\n" +
            "</pre>" +
            "<input type=submit\"submit\" name=\"" + DBParams.COMMAND + "\" VALUE=\"" + ThinMenuConstants.SUBMIT + "\"/>" +
            "</form>";
        strHTML = Utility.replace(strHTML, HtmlConstants.RECORD_TAG, strParamRecord);
        strHTML = Utility.replace(strHTML, HtmlConstants.SCREEN_TAG, strParamScreen);

        if (this.getProperty(DBParams.JOB) != null)
        {
            String strCommand = this.getProperty(DBParams.JOB);
            try   {
                strCommand = URLDecoder.decode(strCommand, DBConstants.URL_ENCODING);
            } catch (Exception ex)  {
                // Ignore
            }
            strHTML = "<p>Job: " + strCommand;
            if (this.getScreenField().handleCommand(strCommand, this.getScreenField(), ScreenConstants.USE_SAME_WINDOW | DBConstants.PUSH_TO_BROSWER))
                strHTML += " queued successfully</p>";
            else
                strHTML += " queued unsuccessfully</p>";
        }
        else if (this.getProperty("menutasks") != null)
        {
            String strCommand = this.getProperty("menutasks");
            Task task = this.getTask();
            if (task == null)
                task = BaseApplet.getSharedInstance();  // ??
//x         TaskScheduler js = task.getApplication().getTaskScheduler();
            PrivateTaskScheduler js = new PrivateTaskScheduler(task.getApplication(), 0, true);
            strHTML = "<p>Multiple Job queue: " + strCommand + "</p>";
            Menus recMenu = new Menus((DefaultHtmlScreen)this.getScreenField());
            recMenu.setKeyArea(Menus.kCodeKey);
            recMenu.getField(Menus.kCode).setString(strCommand);
            String strMenuObjectID = null;
            if (recMenu.seek("="))
                strMenuObjectID = recMenu.getField(Menus.kID).toString();
            if ((strMenuObjectID != null) && (strMenuObjectID.length() > 0))
            {
                recMenu.close();
                recMenu.setKeyArea(Menus.kParentFolderIDKey);
                StringSubFileFilter behMenu = new StringSubFileFilter(strMenuObjectID, Menus.kParentFolderID, null, -1, null, -1);
                recMenu.addListener(behMenu);
                while (recMenu.hasNext())
                {
                    recMenu.next();
                    String strLink = recMenu.getLink();
                    Map<String,Object> properties = new Hashtable<String,Object>();
                    Util.parseArgs(properties, strLink);
                    strCommand = (String)properties.get(DBParams.JOB);
                    if (strCommand != null)
                    {
                        try   {
                            strCommand = URLDecoder.decode(strCommand, DBConstants.URL_ENCODING);
                        } catch (java.io.UnsupportedEncodingException ex) {
                            ex.printStackTrace();
                        }
                        strHTML += "<p>Job: " + strCommand;
                        js.addTask(strCommand);
                        strHTML += " queued successfully</p>";
                    }
                    else
                        strHTML += "<p>Job: " + strCommand + " illegal job name</p>";
                }
            }
            recMenu.free();
            recMenu = null;
            js.endOfJobs();     // This thing will stop now when its done.
        }
        
        out.println(strHTML);
    }
}
