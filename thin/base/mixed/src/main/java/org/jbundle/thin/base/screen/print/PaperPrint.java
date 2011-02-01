package org.jbundle.thin.base.screen.print;

/**
* @(#)AppletScreen.java    0.00 12-Feb-97 Don Corley
*
* Copyright (c) 2009 tourapp.com. All Rights Reserved.
*      don@tourgeek.com
*/
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;

/**
 * This class keeps track of where I am on the physical paper.
 */
public class PaperPrint extends Object
{
    public static final int HEADER_MARGIN = 12;
    public static final int BORDER = 2;

    protected int currentYLocation = 0;

    protected Rectangle pageRect;
    protected int headerHeight = 0;
    protected int footerHeight = 0;

    /**
     * Constructor.
     */
    public PaperPrint()
    {
        super();
    }
   /**
     * Constructor.
     */
    public PaperPrint(PageFormat pageFormat)
    {
        this();
        this.init(pageFormat);
    }
    /**
     * Constructor.
     */
    public void init(PageFormat pageFormat)
    {
    }
    /**
     * Save basic page information.
     * @param pageFormat The pageformat from the print call.
     * @param g2d The graphics environment from the print call.
     */
    public void surveyPage(PageFormat pageFormat, Graphics2D g2d, boolean bPrintHeader)
    {
        pageRect = new Rectangle((int)pageFormat.getImageableX(), (int)pageFormat.getImageableY(), (int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());

        FontMetrics fm = g2d.getFontMetrics();
        int textHeight = fm.getHeight();
        if (bPrintHeader)
        {
            headerHeight = textHeight + HEADER_MARGIN + BORDER;
            footerHeight = textHeight + HEADER_MARGIN + BORDER;
        }
    }
    /**
     * Get the height of the page that is available for imaging components.
     * (The height minus the header and footer).
     * @return The height.
     */
    public int getPrintableHeight()
    {
        return pageRect.height - headerHeight - footerHeight;
    }
    /**
     * Get the height of the page that is available for imaging components.
     * (The height minus the header and footer).
     * @return The height.
     */
    public int getPrintableWidth()
    {
        return pageRect.width;
    }
    /**
     * Set the Y location on the page.
     * @param The Y location on the page.
     */
    public void setCurrentYLocation(int locationY)
    {
        currentYLocation = locationY;
    }
    /**
     * Add to the Y location on the page.
     * @param The Y location to add to this page.
     */
    public void addCurrentYLocation(int length)
    {
        currentYLocation += length;
    }
    /**
     * Get the Y location on the page.
     * @return The Y location on the page.
     */
    public int getCurrentYLocation()
    {
        return currentYLocation;
    }
    /**
     * Get the next location to start imaging in this page.
     * @return The Y shift
     */
    public int getYOffset()
    {
        return headerHeight + pageRect.y + currentYLocation;
    }
    /**
     * Get the amount to shift this image on the page.
     * @return The X shift
    */
    public int getXOffset()
    {
        return pageRect.x;
    }
    /**
     * Print the page header.
     * @param g2d The graphics environment.
     * @param headerText The text to print for the header.
     */
    protected void printHeader(Graphics2D g2d, String headerText)
    {
        FontMetrics fm = g2d.getFontMetrics();
        int textHeight = fm.getHeight();
        int stringWidth = fm.stringWidth(headerText);
        int textX = (pageRect.width - stringWidth) / 2 + pageRect.x;
        int textY = pageRect.y + textHeight + BORDER;
        g2d.drawString(headerText , textX, textY);
    }
    /**
     * Print the page footer.
     * @param g2d The graphics environment.
     * @param footerText The text to print in the footer.
     */
    protected void printFooter(Graphics2D g2d, String footerText)
    {
        FontMetrics fm = g2d.getFontMetrics();
        int stringWidth = fm.stringWidth(footerText);
        int textX = (pageRect.width - stringWidth) / 2 + pageRect.x;
        int textY = pageRect.y + pageRect.height - BORDER;
        g2d.drawString(footerText , textX, textY);
    }
}
