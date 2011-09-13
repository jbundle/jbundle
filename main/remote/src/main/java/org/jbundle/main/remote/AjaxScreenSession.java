/**
 * @(#)AjaxScreenSession.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.remote;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.base.remote.db.*;
import org.jbundle.base.remote.*;
import org.jbundle.base.db.xmlutil.*;
import java.rmi.*;
import java.io.*;
import org.jbundle.base.screen.control.servlet.xml.*;
import org.jbundle.thin.base.screen.*;

/**
 *  AjaxScreenSession - Main session for Ajax remote screens.
 */
public class AjaxScreenSession extends Session
{
    public static final String CREATE_SCREEN = "createScreen";
    protected TopScreen m_topScreen = null;
    /**
     * Default constructor.
     */
    public AjaxScreenSession() throws RemoteException
    {
        super();
    }
    /**
     * AjaxScreenSession Method.
     */
    public AjaxScreenSession(BaseSession parentSessionObject, Record record, Object objectID) throws RemoteException
    {
        this();
        this.init(parentSessionObject, record, objectID);
    }
    /**
     * Initialize class fields.
     */
    public void init(BaseSession parentSessionObject, Record record, Object objectID)
    {
        m_topScreen = null;
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * GetScreen Method.
     */
    public BaseScreen getScreen(Map<String,Object> properties)
    {
        if (m_topScreen == null)
            m_topScreen = this.createTopScreen(this, null, null);
        BaseScreen screen = null;
        if (m_topScreen.getSFieldCount() > 0)
            screen = (BaseScreen)m_topScreen.getSField(0);
        this.setProperties(properties);
        BaseScreen newScreen = m_topScreen.getScreen(screen, this);
        return newScreen;
    }
    /**
     * Override this to do an action sent from the client.
     * @param strCommand The command to execute
     * @param properties The properties for the command
     * @returns Object Return a Boolean.TRUE for success, Boolean.FALSE for failure.
     */
    public Object doRemoteCommand(String strCommand, Map<String,Object> properties) throws RemoteException, DBException
    {
        if (properties == null)
            properties = new Hashtable<String,Object>();
        this.setProperties(properties);
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
        
        if (CREATE_SCREEN.equals(strCommand))
        {
            BaseScreen screen = this.getScreen(properties);
            int iErrorCode = screen.checkSecurity();
            if ((iErrorCode != DBConstants.NORMAL_RETURN) && (iErrorCode != Constants.READ_ACCESS))
            {
                String strError = this.getTask().getLastError(iErrorCode);
                if ((strError == null)
                    || (strError.length() == 0)
                    || ((iErrorCode >= DBConstants.ACCESS_DENIED) && (iErrorCode <= DBConstants.CREATE_USER_REQUIRED)))
                        strError = Integer.toString(iErrorCode);
                RemoteException ex = new RemoteException(strError);
                throw ex;
            }
            this.printScreen(screen, out);
        }
        else if (strCommand != null)
        {
            if (strCommand.indexOf('=') != -1)
                Utility.parseArgs(properties, strCommand);
            else
                properties.put(DBParams.COMMAND, strCommand);
            
            BaseScreen screen = this.getScreen(properties);
            if (screen != null)
            {
                screen = screen.doServletCommand(m_topScreen);  // Move the input params to the record fields
                if (screen != null)
                {
                    screen.processInputData(out);
                    if (stringWriter.getBuffer().length() == 0)
                        if (HtmlConstants.DISPLAY.equalsIgnoreCase((String)properties.get(HtmlConstants.FORMS)))
                            this.printScreen(screen, out);    // This is a report
                }
                else
                {   // Special case - command success, client needs to reset (to main menu)
                    String strMessage = this.getTask().getStatusText(DBConstants.INFORMATION_MESSAGE);
                    out.println(Utility.startTag(XMLTags.STATUS_TEXT));
                    if (strMessage != null)
                        out.println(Utility.startTag(XMLTags.TEXT) + strMessage + Utility.endTag(XMLTags.TEXT));
                    out.println(Utility.startTag(XMLTags.ERROR) + "information" + Utility.endTag(XMLTags.ERROR));
                    String strReturnCommand = Utility.addURLParam(null, XMLTags.MENU, DBConstants.BLANK);   // User's main menu
                    strReturnCommand = Utility.addURLParam(strReturnCommand, DBParams.USER_NAME, this.getProperty(DBParams.USER_NAME));
                    strReturnCommand = Utility.addURLParam(strReturnCommand, DBParams.AUTH_TOKEN, this.getProperty(DBParams.AUTH_TOKEN));
                    out.println(Utility.startTag(DBParams.COMMAND) + Utility.encodeXML(strReturnCommand) + Utility.endTag(DBParams.COMMAND));
                    out.println(Utility.endTag(XMLTags.STATUS_TEXT));
                }
            }
        }
        
        out.flush();
        out.close();
        
        return stringWriter.toString();
    }
    /**
     * PrintScreen Method.
     */
    public void printScreen(BaseScreen screen, PrintWriter out)
    {
        try {
            ResourceBundle reg = ((BaseApplication)this.getTask().getApplication()).getResources(HtmlConstants.XML_RESOURCE, false);
            String string = XmlUtilities.XML_LEAD_LINE;
            out.println(string);
        
            String strStylesheetPath = screen.getScreenFieldView().getStylesheetPath();
            if (strStylesheetPath != null)
            {
                string = "<?xml-stylesheet type=\"text/xsl\" href=\"" + strStylesheetPath + "\" ?>";
                out.println(string);
            }
            out.println(Utility.startTag(XMLTags.CONTENT_AREA));
        
            screen.printScreen(out, reg);
        
            out.println(Utility.endTag(XMLTags.CONTENT_AREA));
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * CreateTopScreen Method.
     */
    public TopScreen createTopScreen(RecordOwnerParent parent, FieldList recordMain, Object properties)
    {
        return new XmlScreen(parent, recordMain, properties);
    }

}
