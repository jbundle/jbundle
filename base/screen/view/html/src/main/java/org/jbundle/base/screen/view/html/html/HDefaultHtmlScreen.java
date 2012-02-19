/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.html.html;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.html.DefaultHtmlScreen;
import org.jbundle.base.screen.view.html.HBaseScreen;
import org.jbundle.model.main.db.MenusModel;
import org.jbundle.model.DBException;
import org.jbundle.model.Task;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.thread.PrivateTaskScheduler;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 *  <p> TODO:
 *  1. Scroll into view if focused
 *  2. Tab = tab, even for JTextAreas (Could be a swing bug)
 */
public class HDefaultHtmlScreen extends HBaseScreen
{

    /**
     * Constructor.
     */
    public HDefaultHtmlScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public HDefaultHtmlScreen(ScreenField model,boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
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
     * Code to display a Menu.
     * @exception DBException File exception.
     */
    public void printScreen(PrintWriter out, ResourceBundle reg)
        throws DBException
    {
        String strParamRecord = this.getProperty(DBParams.RECORD);      // Display record
        String strParamScreen = this.getProperty(DBParams.SCREEN);      // Display screen
//        String strParamLimit = this.getProperty("limit");   // Display record
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
            "<input type=\"submit\" name=\"" + DBParams.COMMAND + "\" VALUE=\"" + ThinMenuConstants.SUBMIT + "\"/>" +
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
            if (this.getScreenField().handleCommand(strCommand, this.getScreenField(), ScreenConstants.USE_SAME_WINDOW))
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
            Record recMenu = Record.makeRecordFromClassName(MenusModel.THICK_CLASS, ((DefaultHtmlScreen)this.getScreenField()));
            recMenu.setKeyArea(MenusModel.CODE_KEY);
            recMenu.getField(MenusModel.CODE).setString(strCommand);
            String strMenuObjectID = null;
            if (recMenu.seek("="))
                strMenuObjectID = recMenu.getField(MenusModel.ID).toString();
            if ((strMenuObjectID != null) && (strMenuObjectID.length() > 0))
            {
                recMenu.close();
                recMenu.setKeyArea(MenusModel.PARENT_FOLDER_ID_KEY);
                StringSubFileFilter behMenu = new StringSubFileFilter(strMenuObjectID, recMenu.getField(MenusModel.PARENT_FOLDER_ID), null, null, null, null);
                recMenu.addListener(behMenu);
                while (recMenu.hasNext())
                {
                    recMenu.next();
                    String strLink = ((MenusModel)recMenu).getLink();
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
