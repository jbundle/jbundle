package org.jbundle.thin.base.screen.menu;

/**
 * JRemoteMenuScreen.java:  Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.thread.TaskScheduler;
import org.jbundle.util.osgi.finder.ClassServiceUtility;

/**
 * A Basic menu that is linked to a remote record.
 */
public class JRemoteMenuScreen extends JBaseMenuScreen
{
	private static final long serialVersionUID = 1L;

	/**
     * The initial command passed in as a property (menu=xxx).
     */
    protected String m_strInitParam = null;
    /**
     * The remote menu session (a RemoteMenuSession).
     */
    protected RemoteSession m_remoteSession = null;

    /**
     *  JRemoteMenuScreen Class Constructor.
     */
    public JRemoteMenuScreen()
    {
        super();
    }
    /**
     * JRemoteMenuScreen Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public JRemoteMenuScreen(Object parent, Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Initialize this class.
     * If there is a top-level "menu=" property, save it for later.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public void init(Object parent, Object record)
    {
        if (parent instanceof BaseApplet)
            m_strInitParam = ((BaseApplet)parent).getProperty(Params.MENU);
        super.init(parent, record);
    }
    /**
     * Process this action.
     * @param strAction The command to execute.
     * @return True if handled.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        if (strAction != null)
            if (strAction.indexOf('=') != -1)
        {
            Map<String,Object> properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, strAction);
            String strMenu = (String)properties.get(Params.MENU);
            if (strMenu != null)
            {
                try   {
                    Object bSuccess = m_remoteSession.doRemoteAction(strMenu, null);
                    if ((bSuccess instanceof Boolean)
                        && (((Boolean)bSuccess).booleanValue() == true))    // Success
                    {
                        this.removeAll();
                        this.addScreenControls(this);
                        this.validate();
                        this.repaint();
                        if (properties.get(Params.SCREEN) == null)
                            strAction = strAction + '&' + Params.SCREEN + '=' + this.getClass().getName();
                        if (properties.get(Params.APPLET) == null)
                            strAction = strAction + '&' + Params.APPLET + '=';
                        this.getBaseApplet().pushHistory(strAction, ((iOptions & Constants.DONT_PUSH_TO_BROSWER) == Constants.PUSH_TO_BROSWER));    // This is the command to get to this screen (for the history).
                        return ((Boolean)bSuccess).booleanValue();
                    }
                } catch (Exception ex)  {
                    ex.printStackTrace();
                }
            }
            String strApplet = (String)properties.get(Params.APPLET);
            if (strApplet != null)
                if ((strApplet.length() == 0)
                    || (strAction.indexOf("BaseApplet") != -1))
                        strApplet = null; // Keep in same screen.
            if ((properties.get(Params.TASK) != null)
                || (strApplet != null))
            { // Asking to start a job
                TaskScheduler js = BaseApplet.getSharedInstance().getApplication().getTaskScheduler();
                js.addTask(strAction);
                return true;
            }
        }
        return super.doAction(strAction, iOptions);
    }
    /**
     * Build the list of fields that make up the screen.
     * This method creates a new Menus record and links it to the remote MenusSession.
     * @return the field.
     */
    public FieldList buildFieldList()
    {
        FieldList record = (FieldList)ClassServiceUtility.getClassService().makeObjectFromClassName(Constants.ROOT_PACKAGE + "thin.main.db.Menus");
        if (record != null)
            record.init(this);
        else
        	return null;
        record.setOpenMode(Constants.OPEN_READ_ONLY);   // This will improve performance by enabling the cache for readnext.
        BaseApplet applet = BaseApplet.getSharedInstance();
        m_remoteSession = applet.makeRemoteSession(null, ".main.remote.MenusSession");
        applet.linkRemoteSessionTable(m_remoteSession, record, true);
        
        if (m_strInitParam != null)
        {
            try   {
                m_remoteSession.doRemoteAction(m_strInitParam, null);
            } catch (Exception ex)  {
                // Ignore error
            }
        }
        return record;
    }
    /**
     * Get the command string that can be used to create this screen.
     * @return The screen command (defaults to ?applet=&screen=xxx.xxx.xxxx).
     */
    public String getScreenCommand()
    {
        String strMenu = Constants.BLANK;
        if (m_strInitParam != null)
            strMenu = m_strInitParam;
        return super.getScreenCommand() + "&menu=" + strMenu;
    }
}
