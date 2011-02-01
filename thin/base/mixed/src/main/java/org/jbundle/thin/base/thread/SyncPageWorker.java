/*
 * @(#)HtmlDemo.java    1.4 99/07/23
 */
package org.jbundle.thin.base.thread;

import java.awt.EventQueue;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 * Schedule a compute-intensive task to occur only after a page has displayed.
 * This thread can also be used to stack display tasks in the (private) task queue
 * to make sure each one is displayed before the next. (ie., please wait, then the actual screen)
 */
public class SyncPageWorker extends Thread
{
    protected SyncPage m_syncPage = null;

    protected Runnable m_swingPageLoader = null;
    
    protected Map<String,Object> m_map = null;

    /**
     * Constructor.
     */
    public SyncPageWorker() {
        super();
    }
    /**
     * Constructor.
     */
    public SyncPageWorker(SyncPage p, Map<String,Object> map) {
        this();
        
        this.init(p, null, map);
    }
    /**
     * Constructor.
     */
    public SyncPageWorker(SyncPage p, Runnable swingPageLoader) {
        this();
        
        this.init(p, swingPageLoader, null);
    }
    /**
     * Constructor.
     */
    public void init(SyncPage p, Runnable swingPageLoader, Map<String,Object> map) 
    {
        m_syncPage = p;
        m_swingPageLoader = swingPageLoader;
        m_map = map;
    }
    /**
     * Make sure everything goes in the right order.
     */
    public void run()
    {
        // Note: To make sure the user gets a chance to see the
        // html text, we wait for a paint before returing.
        // Since these threads are stacked in a private thread queue, the next
        // thread is not executed until this one is finished.
            if (! EventQueue.isDispatchThread() 
)//            && Runtime.getRuntime().availableProcessors() == 1)
        {
            if (!m_syncPage.isPaintCalled())
            {   // Wait for the previous call to finish
                synchronized (m_syncPage) {
                    while (!m_syncPage.isPaintCalled()) {
                        try { m_syncPage.wait(); } catch (InterruptedException e) {}
                    }
                }
            }
            synchronized (m_syncPage) {
                m_syncPage.setPaintCalled(false);

                this.runPageLoader();

                while (!m_syncPage.isPaintCalled()) {
                    try { m_syncPage.wait(); } catch (InterruptedException e) {}
                }
                
                this.afterPageDisplay();
            }
        }
    }
    /**
     * Invoke the page loader that was passed into the constructor.
     * If you override this, remember to invoke your code in the awt thread.
     */
    public void runPageLoader()
    {
        if (m_swingPageLoader != null)
            SwingUtilities.invokeLater(m_swingPageLoader);
    }
    /**
     * Do this code after the page has displayed on the screen.
     * Override this method.
     */
    public void afterPageDisplay()
    {
        // Override this
    }
    /**
     * Get saved property.
     * @param strKey
     * @return
     */
    public Object get(String strKey)
    {
        if (m_map != null)
            return m_map.get(strKey);
        return null;
    }
}
