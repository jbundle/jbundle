/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing.report;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.jbundle.base.message.core.trx.TrxMessageHeader;
import org.jbundle.base.message.core.trx.internal.ManualMessage;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.MenuConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.report.ReportScreen;
import org.jbundle.model.DBException;
import org.jbundle.model.main.msg.db.MessageTransportModel;
import org.jbundle.model.main.user.db.UserInfoModel;
import org.jbundle.model.message.MessageManager;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.screen.print.ScreenPrinter;
import org.jbundle.thin.base.util.Application;


/**
 * This is the base screen for reports.
 * This class can simply output the record in report format.
 */
public class VReportScreen extends VDualReportScreen
{

    /**
     * Constructor.
     */
    public VReportScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VReportScreen(ScreenField model, boolean bEditableControl)
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
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     */
    public Component setupControl(boolean bEditableControl)   // Must o/r
    {
        JEditorPane control = new JEditorPane();
        control.setContentType("text/html");
        control.setEditable(false);
        JScrollPane scrollpane = new JScrollPane();
        JViewport vp = scrollpane.getViewport();
        vp.add(control);

        control.setOpaque(false);
        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);
        scrollpane.setPreferredSize(new Dimension(10,10));
        scrollpane.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(scrollpane);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        return control;
    }
    /**
     * Get one of the physical components associated with this SField.
     * @param int iLevel    CONTROL_TOP - Parent physical control; CONTROL_BOTTOM - Lowest child control
     * @return The control at this level.
     */
    public Component getControl(int iLevel)
    {
        if (iLevel == DBConstants.CONTROL_TOP)
        {
            Container parent = this.getControl().getParent();
            while (!(parent instanceof JScrollPane))
            {
                parent = parent.getParent();
            }
            if (parent != null)
                return parent.getParent();  // scrollpane->JPanel
        }
        if (iLevel == DBConstants.CONTROL_BOTTOM)
            return null;    // Make sure controls are not directly added to this JTable
        return super.getControl(iLevel);
    }
    /**
     * Process the command.
     * Step 1 - Process the command if possible and return true if processed.
     * Step 2 - If I can't process, pass to all children (with me as the source).
     * Step 3 - If children didn't process, pass to parent (with me as the source).
     * Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to execute.
     * @return True if the command was handled.
     */
    public boolean doCommand(String strCommand)
    {
        boolean bFlag = false;
        if (strCommand.equalsIgnoreCase(MenuConstants.PRINT))
            bFlag = this.onPrint();
        if (strCommand.equalsIgnoreCase(MenuConstants.DISPLAY))
            bFlag = this.onDisplay();
        if (bFlag == false)
            bFlag = super.doCommand(strCommand);    // This will send the command to my parent
        return bFlag;
    }
    /**
     * Process the "Write" toolbar command.
     * Write the response to this URL to a file.
     * @return True if handled.
     */
    public boolean onWrite(String strURL)
    {
        ReportScreen modelScreen = (ReportScreen)this.getScreenField();

        Map<String,Object> properties = new Hashtable<String,Object>();
        Util.parseArgs(properties, strURL);
        String strFilename = (String)properties.get(DBParams.FILEOUT);
        String strSendMessageBy = (String)properties.get(MessageTransportModel.SEND_MESSAGE_BY_PARAM);

        String strMessage = Utility.transferURLStream(strURL, strFilename);

        if ((strSendMessageBy != null) && (strSendMessageBy.length() > 0))
            if ((strMessage != null) && (strMessage.length() > 0))
        {   // Note: properties include SEND_BY and DESTINATION
            TrxMessageHeader messageHeader = new TrxMessageHeader(this, properties);

            BaseMessage message = new ManualMessage(messageHeader, strMessage);

            MessageManager messageManager = ((Application)modelScreen.getTask().getApplication()).getMessageManager();
            if (messageManager != null)
                messageManager.sendMessage(message);
        }

        return true;    // Handled
    }
    /**
     * Process the "Display" toolbar command.
     * @return True if handled.
     */
    public boolean onDisplay()
    {
//?        ReportScreen modelScreen = (ReportScreen)this.getScreenField();
        String strURL = this.getScreenURL();
        URL url = null;
        try {
            url = new URL(strURL);
            ((JEditorPane)this.getControl()).setPage(url);
        } catch (MalformedURLException ex)  {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return true;    // Handled
    }
    /**
     * Process the "Print" toolbar command.
     * @return true if handled.
     */
    public boolean onPrint()
    {
        if (this.onDisplay() == true)
        {
    //?        ReportScreen modelScreen = (ReportScreen)this.getScreenField();
            String strURL = this.getScreenURL();
            Map<String,Object> properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, strURL);
            if (((properties.get(DBParams.FILEOUT) != null) && (((String)properties.get(DBParams.FILEOUT)).length() > 0))
                || ((properties.get(MessageTransportModel.SEND_MESSAGE_BY_PARAM) != null) && (((String)properties.get(MessageTransportModel.SEND_MESSAGE_BY_PARAM)).length() > 0)))
                    return this.onWrite(strURL);  // Print to a file.

            PrinterJob job = PrinterJob.getPrinterJob();
            PageFormat pf = job.defaultPage();
    //?     PageFormat pf = job.pageDialog(job.defaultPage());
            int iWidth = (int)pf.getImageableWidth();
            int iHeight = (int)pf.getImageableHeight();
            this.getControl().setSize(iWidth, iHeight);   // This is a hack for 8.5 x 11 page size
            ScreenPrinter fapp = new ScreenPrinter(this.getControl());
            job.setPrintable(fapp);
            if (job.printDialog ())
            {
                fapp.printJob(job);
            }
        }
        return true;    // Handled
    }
    /**
     * display this screen in html input format.
     *  returns true if default params were found for this form.
     * @param out The html out stream.
     * @param iHtmlAttributes The Html attributes.
     * @return True if fields were found.
     * @exception DBException File exception.
     */
    public String getScreenURL()
    {
        ReportScreen modelScreen = (ReportScreen)this.getScreenField();
        String strURL = modelScreen.getScreenURL();
        strURL = Utility.addURLParam(strURL, HtmlConstants.FORMS, HtmlConstants.DISPLAY); // Don't need outside frame stuff in a window
        strURL = Utility.addURLParam(strURL, DBParams.COMMAND, DBConstants.SUBMIT);
        strURL = Utility.addURLParam(strURL, DBParams.MENUBARS, UserInfoModel.NO);
        strURL = Utility.addURLParam(strURL, DBParams.NAVMENUS, UserInfoModel.NO_ICONS);
        strURL = Utility.addURLParam(strURL, DBParams.LOGOS, UserInfoModel.NO);
        strURL = Utility.addURLParam(strURL, DBParams.TRAILERS, UserInfoModel.NO);
        strURL = ((ReportScreen)this.getScreenField()).addScreenParams(modelScreen, strURL);

        String strFilePrefix = "http://";
        Application app = null;
        if (modelScreen.getTask() != null)
            app = (Application)modelScreen.getTask().getApplication();
        if (app != null)
        	strURL = app.addUserParamsToURL(strURL);
        String strServlet = modelScreen.getServletPath(null);
        if ((strServlet == null) || (strServlet.length() == 0))
            strServlet = Constants.DEFAULT_SERVLET;
        if (app == null)
            strFilePrefix += "localhost/" + strServlet;   // Never
        else
            strFilePrefix = app.getBaseServletPath() + '/' + strServlet;
        strURL = strFilePrefix + strURL;

        return strURL;
    }
}
