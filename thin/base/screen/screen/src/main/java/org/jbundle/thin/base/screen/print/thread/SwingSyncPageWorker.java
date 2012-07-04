/*
 * @(#)HtmlDemo.java    1.4 99/07/23
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.print.thread;

import java.awt.Component;
import java.awt.Cursor;
import java.util.Map;
import java.util.MissingResourceException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.JBasePanel;
import org.jbundle.thin.base.thread.SyncNotify;
import org.jbundle.thin.base.thread.SyncPage;
import org.jbundle.thin.base.thread.SyncWorker;
import org.jbundle.thin.base.util.ThinResourceConstants;

/**
 * This is a utility class that makes sure the wait message and cursor are displayed before starting
 * a compute intensive task in the awt thread (in done()) . It is preferable to use SwingWorker and do
 * the tasks in a separate thread, but if you want to lock the screen while doing a task and
 * GUARANTEE done() IS EXECUTED IN THE AWT THREAD, use this utility.
 */
public class SwingSyncPageWorker extends SyncPageWorker
    implements SyncWorker
{
    protected boolean m_bManageCursor = true;
    SyncNotify syncNotify = null;
    
    public static final String WAIT_MESSAGE = "Please wait...";

    /**
     * Constructor.
     */
    public SwingSyncPageWorker() {
        super();
    }
    /**
     * Constructor.
     */
    public SwingSyncPageWorker(SyncPage syncPage, boolean bManageCursor) {
        this();
        
        this.init(syncPage, null, null, null, bManageCursor);
    }
    /**
     * Constructor.
     */
    public SwingSyncPageWorker(SyncPage syncPage, Map<String,Object> map, boolean bManageCursor) {
        this();
        
        this.init(syncPage, null, null, map, bManageCursor);
    }
    /**
     * Constructor.
     */
    public SwingSyncPageWorker(SyncPage syncPage, Runnable swingPageLoader) {
        this();
        
        this.init(syncPage, null, swingPageLoader, null, true);
    }
    /**
     * Constructor.
     */
    public void init(SyncPage syncPage, SyncNotify syncNotify, Runnable swingPageLoader, Map<String,Object> map, boolean bManageCursor) 
    {
        m_bManageCursor = bManageCursor;
        this.syncNotify = syncNotify;
        if (syncNotify != null)
            syncNotify.setSyncWorker(this);
        super.init(syncPage, swingPageLoader, map);
    }
    /**
     * Invoke the page loader that was passed into the constructor.
     * If you override this, remember to invoke your code in the awt thread.
     */
    public void runPageLoader()
    {
            SwingUtilities.invokeLater(new Thread()
            {	// Utility class to change the status in the AWT thread, then run the m_swingPageLoader.
            	public void run()
            	{
                    if (m_syncPage instanceof Task)
                    {
                        String strWaitMessage = WAIT_MESSAGE;
                        try {
                            strWaitMessage = ((Task)m_syncPage).getApplication().getResources(ThinResourceConstants.ERROR_RESOURCE, true).getString(strWaitMessage);
                        } catch (MissingResourceException ex) {
                        }
                        ((Task)m_syncPage).setStatusText(strWaitMessage, Constants.WAIT);
                    }
                    else if (m_syncPage instanceof JBasePanel)
                    {
                        if (((JBasePanel)m_syncPage).getBaseApplet() != null)
                        {
                            String strWaitMessage = WAIT_MESSAGE;
                            try {
                                strWaitMessage = (((JBasePanel)m_syncPage).getBaseApplet()).getApplication().getResources(ThinResourceConstants.ERROR_RESOURCE, true).getString(strWaitMessage);
                            } catch (MissingResourceException ex) {
                            }
                            ((JBasePanel)m_syncPage).getBaseApplet().setStatusText(strWaitMessage, Constants.WAIT);
                        }
                    }
                    Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
                    if (m_syncPage instanceof Component)
                    {
                    	((Component)m_syncPage).setCursor(waitCursor);
                        ((Component)m_syncPage).repaint();	// Queue a repaint
                    }
                    
                    if (m_swingPageLoader != null)
                    	m_swingPageLoader.run();
            		
                    if (m_syncPage instanceof Component)
                        ((Component)m_syncPage).repaint();                    
            	}
            });
    }
    /**
     * Do this code after the page has displayed on the screen.
     * Override this method.
     */
    public void afterPageDisplay()
    {
        SwingWorker<String,String> worker = new SwingWorker<String,String>()
        {
            public String doInBackground()
            {
                return null;
            }
            public void done()
            {	// This should always be the awt thread
                SwingSyncPageWorker.this.done();
                if (m_syncPage instanceof Task)
                {
                    ((Task)m_syncPage).setStatusText(null, Constants.INFORMATION);
                }
                else if (m_syncPage instanceof JBasePanel)
                {
                    if (((JBasePanel)m_syncPage).getBaseApplet() != null)
                        ((JBasePanel)m_syncPage).getBaseApplet().setStatusText(null, Constants.INFORMATION);
                }
                Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
                if (m_syncPage instanceof Component)
                {
                	((Component)m_syncPage).setCursor(waitCursor);
                    ((Component)m_syncPage).repaint();	// Queue a repaint
                }
            }
        };
        worker.execute();
    }
    /*
     * 
     */
    public void done()
    {   // Override this
        if (syncNotify != null)
            syncNotify.done();
    }
}
