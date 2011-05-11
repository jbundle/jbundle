package org.jbundle.thin.base.screen.print;

/**
 * @(#)AppletScreen.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.jbundle.thin.base.screen.print.thread.SyncPageWorker;


/**
 * Print this screen or component as it is displayed on the screen.
 * Note: This is rather inefficient as it re-paints the entire component list for each page.
 * pend(don) This needs to be reviewed.
 */
public class ScreenPrinter implements Printable
{
    protected Component m_component = null;
    private boolean firstTime = true;
    protected ComponentPrint m_componentPrint = null;
    protected PaperPrint m_paperPrint = null;
    protected PrintDialog dialog = null;
    protected boolean m_bPrintHeader = true;

    /**
     * Print the current screen.
     * @return true.
     */
    public static boolean onPrint(Component component)
    {
        return ScreenPrinter.onPrint(component, true);
    }
    /**
     * Print the current screen.
     * @return true.
     */
    public static boolean onPrint(Component component, boolean bPrintHeader)
    {
        PrinterJob job = PrinterJob.getPrinterJob();
        ScreenPrinter fapp = new ScreenPrinter(component, bPrintHeader);
        job.setPrintable(fapp);
        if (job.printDialog ())
            fapp.printJob(job);
        return true;
    }
    /**
     * Print this job.
     * @param job
     */
    public void printJob(PrinterJob job)
    {
        Frame frame = this.getFrame();
        dialog = new PrintDialog(frame, false);
        
        dialog.pack();
        if (frame != null)
            this.centerDialogInFrame(dialog, frame);

        
        Map<String,Object> map = new Hashtable<String,Object>();
        map.put("job", job);
        SyncPageWorker thread = new SyncPageWorker(dialog, map)
        {
            public void runPageLoader()
            {
                Thread swingPageLoader = new Thread("SwingPageLoader")
                {
                    public void run()
                    {
                        ((PrintDialog)m_syncPage).setVisible(true);
                    }
                };
                SwingUtilities.invokeLater(swingPageLoader);
            }
            public void afterPageDisplay()
            {
                Thread swingPageLoader = new Thread("SwingPageLoader")
                {
                    public void run()
                    {
                        try {
                            PrinterJob job = (PrinterJob)get("job");
                            job.print();
                        } catch (PrinterException ex) {
                            ex.printStackTrace ();
                        }
                        ((PrintDialog)m_syncPage).setVisible(false);
                    }
                };
                SwingUtilities.invokeLater(swingPageLoader);
            }
        };
        thread.start();
    }
    /**
     * Constructor.
     */
    public ScreenPrinter()
    {
        super();
    }
    /**
     * Constructor - print all the components on this screen (as is, no format changes).
     * @param component The component to print (including the component's children).
     */
    public ScreenPrinter(Component component)
    {
        this();
        this.init(component, true);
    }
    /**
     * Constructor - print all the components on this screen (as is, no format changes).
     * @param component The component to print (including the component's children).
     */
    public ScreenPrinter(Component component, boolean bPrintHeader)
    {
        this();
        this.init(component, bPrintHeader);
    }
    /**
     * Constructor - print this component and all the children after formatting for the screen size.
     * @param component The component to print (including the component's children).
     * @param gridTable The (optional) gridtable to read through to print each compnent.
     */
    public void init(Component component, boolean bPrintHeader)
    {
        m_component = component;
        m_bPrintHeader = bPrintHeader;
    }
    /**
     * Print this page.
     */
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) 
    {
        Graphics2D g2d = (Graphics2D)g;

        if (firstTime)
        {
            m_componentPrint = new ComponentPrint(null);
            m_componentPrint.surveyComponents(m_component);
            m_paperPrint = new PaperPrint(null);
            m_paperPrint.surveyPage(pageFormat, g2d, m_bPrintHeader);
            firstTime = false;
        }

        // Print this page
        int pageHeight = m_paperPrint.getPrintableHeight();
        double scale = Math.min(1.0, ((double)m_paperPrint.getPrintableWidth() / (double)m_componentPrint.getMaxComponentWidth()));

        m_paperPrint.setCurrentYLocation(0);
        m_componentPrint.setPageHeight((int)(pageHeight / scale));
        boolean pageDone = m_componentPrint.setCurrentYLocation(pageIndex, 0);
        
        while (!pageDone)
        {
            Component component = m_componentPrint.getCurrentComponent();
            int componentCurrentYLocation = m_componentPrint.getCurrentYLocation();
            int componentHeightOnPage = m_componentPrint.getComponentPageHeight();

            if (this.isCancelled())
                return NO_SUCH_PAGE;    // Done, No more pages
            if (component == null)
            {
                if (m_paperPrint.getCurrentYLocation() == 0)
                    return NO_SUCH_PAGE;    // No more pages
                else
                    break;  // End of components (last page)
            }

            if (m_paperPrint.getCurrentYLocation() == 0)
            {       // First time through
                if (m_bPrintHeader)
                {
                    m_paperPrint.printHeader(g2d, m_componentPrint.getTitle());
                    m_paperPrint.printFooter(g2d, "Page " + (pageIndex + 1));
                }
                this.setDialogMessage("Printing page " + (pageIndex + 1));
            }

            int xShift = m_paperPrint.getXOffset();
            int yShift = m_paperPrint.getYOffset() - (int)(componentCurrentYLocation * scale);

            g2d.setClip(m_paperPrint.getXOffset(), m_paperPrint.getYOffset(), m_paperPrint.getPrintableWidth(), (int)(componentHeightOnPage * scale));
            g2d.translate(xShift, yShift);
            g2d.scale(scale, scale);
            disableDoubleBuffering(component);
            component.paint(g2d);
            enableDoubleBuffering(component);
            g2d.scale(1 / scale, 1 / scale);
            g2d.translate(-xShift, -yShift);

            m_paperPrint.addCurrentYLocation((int)(componentHeightOnPage * scale));
            pageDone = m_componentPrint.addCurrentYLocation(componentHeightOnPage);
        }
        return PAGE_EXISTS;
    }
    /**
     * This shows the dialog.
     * NOTE: You are going to have to give this dialog it's own thread, so it can respond to button click.
     */
    public Frame getFrame()
    {
        Component frame = m_component;
        while (frame != null)
        {
            if (frame instanceof Frame)
                return (Frame)frame;
            frame = frame.getParent();
        }
        return null;
    }
    /**
     * Set the dialog's message.
     * @param newMessage The message to display.
     */
    public void setDialogMessage(String newMessage)
    {
        if (dialog == null)
            return;
        dialog.setMessage(newMessage);
    }
    /**
     * Was the dialog box cancelled?
     * @return True if cancelled.
     */
    public boolean isCancelled()
    {
        if (dialog == null)
            return true;
        return (dialog.getReturnStatus() == JOptionPane.CANCEL_OPTION);
    }
    /**
     * Turn the cache off, so printing will work.
     * @param c Component.
     */
    public static void disableDoubleBuffering(Component c)
    {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }
    /**
     * Turn the cache back on.
     * @param c Component.
     */
    public static void enableDoubleBuffering(Component c)
    {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
    /**
     * Center this dialog in the frame.
     */
    public void centerDialogInFrame(Dialog dialog, Frame frame)
    {
        dialog.setLocation(frame.getX() + (frame.getWidth() - dialog.getWidth()) / 2, frame.getY() + (frame.getHeight() - dialog.getHeight()) / 2);
    }    
}
