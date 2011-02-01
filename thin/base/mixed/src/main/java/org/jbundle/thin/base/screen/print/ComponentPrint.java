package org.jbundle.thin.base.screen.print;

/**
 * @(#)AppletScreen.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.JTableHeader;

/**
 * This class keeps track of where I am in the component's print space.
 */
public class ComponentPrint extends Object
{
    private int pageHeight = 0;
    protected String title = null;
    protected Component[] componentList = null;

    protected Component currentComponent = null;
    /**
     * Current position within component.
     */
    protected int componentStartYLocation = 0;
    /**
     * The height of this component to image on this page (from the current Y).
     */
    protected int componentPageHeight = 0;
    /**
     * Amount of space left until the end of the component.
     */
    protected int remainingComponentHeight = 0;
    protected int currentPageIndex = 0;
    protected int currentLocationOnPage = 0;
    protected int componentIndex = 0;
    protected int remainingPageHeight = 0;

    public static final float MAX_LOWER_BREAK = 0.2f;   // 20% of the page
    public static final String BLANK = "";

    /**
     * Constructor
     */
    public ComponentPrint()
    {
        super();
    }
    /**
     * Constructor
     */
    public ComponentPrint(Component component)
    {
        this();
        this.init(component);
    }
    /**
     * Constructor
     */
    public void init(Component component)
    {
        if (component != null)
            this.surveyComponents(component);
    }
    /**
     * Survey the components.
     * @param component The top-level parent.
     */
    public void surveyComponents(Component component)
    {
        // First, climb up and get the title from the frame.
        Component frame = component;
        while (frame != null)
        {
            if (frame instanceof Frame)
            {
                title = ((Frame)frame).getTitle();
                break;
            }
            frame = frame.getParent();
        }
        if (title == null)
            title = BLANK;
        // Now go through the components and line the printable ones up.
        Vector<Component> vector = new Vector<Component>();
        this.addComponents(((Container)component), vector);
        if (vector.size() == 0)
            vector.add(component);
        this.componentList = new Component[vector.size()];
        for (int i = 0; i < vector.size(); i++)
        {
            componentList[i] = (Component)vector.get(i);
        }
        
        this.resetAll();
    }
    /**
     * Go through all the components and order the ones that I need to print.
     * @param container The container to start at.
     * @param vector The vector to add the components to.
     */
    public void addComponents(Container container, Vector<Component> vector)
    {
        int components = container.getComponentCount();
        int[] componentOrder = this.getComponentOrder(container);
        for (int i = 0; i < components; i++)
        {
            Component component = container.getComponent(componentOrder[i]);
            if (component instanceof JMenuBar)
                continue;   // Never print this
            if (component instanceof JScrollPane)
            {
                component = ((JScrollPane)component).getViewport().getView();
                if (!(component instanceof JTable))
                {
                    vector.add(component);
                    component = null;
                }
            }
            if (component instanceof JTable)
            {
                JComponent header = ((JTable)component).getTableHeader();
                if (header != null)
                    vector.add(header);
                vector.add(component);
                component = null;
            }
            if (component instanceof JToolBar)
            {
                vector.add(component);
                component = null;
            }
            if (component instanceof Container)
                this.addComponents(((Container)component), vector);
        }
    }
    /**
     * Get the component order by how they are ordered vertically on the screen.
     * @param container The container to survey.
     * @return An array with the container index values by their y position on the screen.
     */
    public int[] getComponentOrder(Container container)
    {
        int components = container.getComponentCount();
        int[] componentOrder = new int[components];
        int[] rgY = new int[components];
        // First assume order is Y order
        for (int i = 0; i < components; i++)
        {
            componentOrder[i] = i;
            rgY[i] = container.getComponent(i).getY();
        }
        // Now bubble sort by Y
        for (int i = 0; i < components; i++)
        {
            for (int j = i + 1; j < components; j++)
            {
                if (rgY[j] < rgY[i])
                {   // Make component closer to top first
                    int temp = rgY[i];
                    rgY[i] = rgY[j];
                    rgY[j] = temp;
                    temp = componentOrder[i];
                    componentOrder[i] = componentOrder[j];
                    componentOrder[j] = temp;
                }
            }
        }
        return componentOrder;
    }
    /**
     * Reset to the first page.
     */
    public void resetAll()
    {
        currentLocationOnPage = 0;

        componentIndex = 0;
        currentComponent = this.getComponent(componentIndex);
        componentStartYLocation = 0;
        componentPageHeight = 0;

        currentPageIndex = 0;

        remainingComponentHeight = 0;
        if (currentComponent != null)
            remainingComponentHeight = currentComponent.getHeight();
        remainingPageHeight = pageHeight;
    }
    /**
     * Set the length of a page.
     * @param pageHeight The page height.
     */
    public void setPageHeight(int pageHeight)
    {
        this.pageHeight = pageHeight;
        this.resetAll();
    }
    /**
     * Set the current Y location and change the current component information to match.
     * @param targetPageIndex The page to position to.
     * @param targetLocationOnPage The location in the current page to position in.
     * @return True if this is the last component and it is finished on this page.
     */
    public boolean setCurrentYLocation(int targetPageIndex, int targetLocationOnPage)
    {
//?        if ((targetPageIndex != currentPageIndex) || (targetLocationOnPage != currentLocationOnPage))
            this.resetAll();    // If I'm not using the cached values, reset to start

        boolean pageDone = false;
        
        while (pageDone == false)
        {
            if (currentComponent == null)
                break;
            
            componentPageHeight = this.calcComponentPageHeight();
            
            if (currentPageIndex > targetPageIndex)
                break;
            if (currentPageIndex == targetPageIndex)
                if (currentLocationOnPage >= targetLocationOnPage)
                    break;  // Target location reached. Return this current component, stats

            remainingComponentHeight = remainingComponentHeight + this.checkComponentHeight();
            if (remainingComponentHeight < remainingPageHeight)
            {   // This component will fit on this page.
                currentLocationOnPage = currentLocationOnPage + remainingComponentHeight;
                remainingPageHeight = remainingPageHeight - remainingComponentHeight;

                componentIndex++;
                currentComponent = this.getComponent(componentIndex);
                componentStartYLocation = 0;
                if (currentComponent != null)
                    remainingComponentHeight = currentComponent.getHeight();
            }
            else
            {   // This component goes past the end of the page
                componentStartYLocation = componentStartYLocation + componentPageHeight;
                if (targetPageIndex == currentPageIndex)
                    pageDone = true;    // The target page is completely scanned

                currentPageIndex++;
                currentLocationOnPage = 0;
                remainingComponentHeight = remainingComponentHeight - componentPageHeight;
                remainingPageHeight = pageHeight;
           }
        }
        
        return pageDone;    // Page done
    }
    /**
     * Add this value to the current location.
     * @param y Amount to add.
     * @return True if this is the last component and it is finished on this page.
     */
    public boolean addCurrentYLocation(int y)
    {
        return this.setCurrentYLocation(currentPageIndex, currentLocationOnPage + y);
    }
    /**
     * Get the current component.
     * @return The current component.
     */
    public Component getCurrentComponent()
    {
        return currentComponent;
    }
    /**
     * Get the widest component.
     * To calculate the scale.
     * @return The width of the widest component.
     */
    public int getMaxComponentWidth()
    {
        int maxWidth = 0;
        for (int index = 0; ; index++)
        {
            Component component = this.getComponent(index);
            if (component == null)
                break;
            if (component instanceof JTableHeader)
                continue;   // Don't use this one
            if (component instanceof JPanel)
            {   // Use the rightmost child for the width
                for (int i = 0; i < ((JPanel)component).getComponentCount(); i++)
                {
                    Component childComponent = ((JPanel)component).getComponent(i);
                    int farthestWidth = childComponent.getX() + ((JPanel)component).getComponent(i).getWidth();
                    maxWidth = Math.max(maxWidth, Math.min(component.getWidth(), farthestWidth));
                }
            }
            else
                maxWidth = Math.max(maxWidth, component.getWidth());
        }
        return maxWidth;
    }
    /**
     * Get the component at this index.
     * @param componentIndex Component index.
     * @return The component at this index.
     */
    public Component getComponent(int componentIndex)
    {
        if (componentList != null)
            if (componentIndex < componentList.length)
                return componentList[componentIndex];
        return null;
    }
    /**
     * Current location of the start of this component.
     * @return The current start.
     */
    public int getCurrentYLocation()
    {
        return componentStartYLocation;
    }
    /**
     * Get the height of this component.
     * Typically, this is just the height of the component.
     * Except for JTables, where I need to query to this target height to make sure the
     * component is at least this correct height.
     * @param component The component to get the height of.
     * @return the component's height.
     */
    public int checkComponentHeight()
    {
        int maxHeightToCheck = componentStartYLocation - currentLocationOnPage + pageHeight;
        if (currentComponent == null)
            return 0;
        if (currentComponent instanceof JTable)
        {   // todo(don) There has to be a better way of doing this JTable code.
            int beforeHeight = currentComponent.getHeight();
            int rowHeight = ((JTable)currentComponent).getRowHeight();
            int rowCount = ((JTable)currentComponent).getRowCount();
            int maxRowCount = maxHeightToCheck / rowHeight;
            for (int i = rowCount - 1; i < rowCount ; i++)
            {
                if (i >= maxRowCount)
                    break;  // No need to go farther (yet)
                rowCount = ((JTable)currentComponent).getRowCount();
            }
            int newComponentHeight = rowHeight * rowCount;
            currentComponent.setSize(currentComponent.getWidth(), newComponentHeight);    // Change in the height.
            return newComponentHeight - beforeHeight;
        }
        return 0;
    }
    /**
     * Calculate the remaining height of this component on this page.
     * This is used to calculate a smart page break that does not split a control
     * between pages.
     * @return The remaining height of this component on this page.
     */
    public int calcComponentPageHeight()
    {
        remainingComponentHeight = remainingComponentHeight + this.checkComponentHeight();
        if (remainingComponentHeight <= remainingPageHeight)
            return remainingComponentHeight;
        if (currentComponent == null)
            return 0;   // Never
        if (!(currentComponent instanceof Container))
            return 0;   // Never
        if (currentComponent instanceof JTable)
        {
            int rowHeight = ((JTable)currentComponent).getRowHeight();
            int currentComponentPageBreak = componentStartYLocation + remainingPageHeight;
            int currentComponentPageRow = (int)(currentComponentPageBreak / rowHeight);
            return remainingPageHeight - (currentComponentPageBreak - currentComponentPageRow * rowHeight);
        }
        
        int lastComponentPageHeight = 0;
        int y = 0;
        int maxLowerBreak = (int)(pageHeight * MAX_LOWER_BREAK);
        Component component = null;
        for (y = remainingPageHeight; y > remainingPageHeight - maxLowerBreak; y--)
        {
            int x = 0;
            for (x = 0; x < currentComponent.getWidth(); x++)
            {
                component = ((Container)currentComponent).getComponentAt(x, componentStartYLocation + y);
                if (component != null)
                    if (component != currentComponent)
                        if (component.getY() + component.getHeight() - 1 != y)  // Don't count last line
                            break;  // This line contains a component, don't break here (continue looking)
            }
            if (x == currentComponent.getWidth())
                break;  // White space thru this whole line, use this as a break
            if (component != null)
                lastComponentPageHeight = Math.max(lastComponentPageHeight, component.getY() - 1);
        }
        if (y == remainingPageHeight - maxLowerBreak)
        {   // Did not find a break, try another way
            if ((lastComponentPageHeight == 0) || (lastComponentPageHeight < pageHeight - maxLowerBreak))
                y = pageHeight;    // No break found, break at the end of the page (in the middle of a component)
            else
                y = lastComponentPageHeight;  // No break found, use the start of the lowest component
        }
        
        return y;
    }
    /**
     * Get this component's page height.
     * @return The page height.
     */
    public int getComponentPageHeight()
    {
        return componentPageHeight;
    }
    /**
     * Get the title for this report.
     * Typically the survey sets this to the frame's title.
     * @return This report's title.
     */
    public String getTitle()
    {
        return title;
    }
}
