/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.swing.util;

/**
 * @(#)ScreenLayout.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JLabel;

import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.screen.view.swing.VBasePanel;
import org.jbundle.base.screen.view.swing.VScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ScreenConstants;


/**
 * ScreenLayout - Layout manager for Screens.
 */
public class ScreenLayout extends Object
    implements LayoutManager2
{
    protected VBasePanel m_screen = null;
    protected Rectangle m_rectAnchor = null;        // Used to add a field to the current screen
    protected Rectangle m_rectLastField = null;
    protected ScreenInfo m_screenInfo = null;
    protected Dimension m_dimFarthest = null;   // Farthest control (used for survey).
    protected Dimension m_dimMinimum = null;        // Minimum screen size.

    /**
     * Constructor.
     */
    public ScreenLayout()
    {
        super();
    }
    /**
     * Constructor.
     * @param screen The screen to layout.
     */
    public ScreenLayout(VBasePanel screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize the variables.
     * @param screen The screen to layout.
     */
    public void init(VBasePanel screen)
    {
        m_screen = screen;
        screen.setScreenLayout(this);   // Link it back to me

        m_rectAnchor = new Rectangle(0, 0, 0, 0);
        m_rectLastField = new Rectangle(0, 0, 0, 0);

        m_screenInfo = screen.getScreenInfo();
        m_dimFarthest = new Dimension(0, 0);
        m_dimMinimum = new Dimension(0, 0);
    }
    /**
     * Free this object.
     * @param screen The screen to free.
     */
    public void free(VBasePanel screen)
    {
        if (m_screen != null) if (m_screen != screen)
            m_screen.setScreenLayout(null);     // Link it back to me
        m_screen = null;
        m_screenInfo = null;

        m_rectAnchor = null;        // Used to add a field to the current screen
        m_rectLastField = null;
    }
    // LayoutManager Methods
    /**
     * Adds the specified component to the layout, using the specified
     * constraint object.  For border layouts, the constraint must be
     * one of the following strings:  <code>"North"</code>,
     * <code>"South"</code>, <code>"East"</code>,
     * <code>"West"</code>, or <code>"Center"</code>.  
     * <p>
     * Most applications do not call this method directly. This method 
     * is called when a component is added to a container using the 
     * <code>Container.add</code> method with the same argument types.
     * @param   comp         the component to be added.
     * @param   constraints  an object that specifies how and where 
     *                       the component is added to the layout.
     * @see     java.awt.Container#add(java.awt.Component, java.lang.Object)
     * @exception   IllegalArgumentException  if the constraint object is not
     *                 a string, or if it not one of the five specified strings.
     * @since   JDK1.1
     */
    public void addLayoutComponent(Component comp, Object constraints)
    {
    }
    public void addLayoutComponent(String name, Component comp)
    {
    }
    /**
     * Lay out the screen.
     * @param parent The parent container.
     */
    public void layoutContainer(Container parent)
    {
        if (m_screenInfo == null)
            m_screenInfo = m_screen.getScreenInfo();
        m_screenInfo.calcFontMetrics(parent);
        Insets insets = parent.getInsets();
        
        int count = ((BasePanel)m_screen.getScreenField()).getSFieldCount();
        Rectangle rect = new Rectangle();

        this.resetFarthestField();
        ScreenLocation screenLocation = ((BasePanel)m_screen.getScreenField()).getNextLocation(ScreenConstants.FIRST_LOCATION, ScreenConstants.SET_ANCHOR);
        this.calcLocation(screenLocation, null);
        for (int i = 0; i < count; i++)
        {
            ScreenField sField = ((BasePanel)m_screen.getScreenField()).getSField(i);
            if (sField instanceof ToolScreen)
                continue;

            String strFieldDesc = null;
            if (sField.getConverter() != null)
                strFieldDesc = sField.getConverter().getFieldDesc();
            Point ptLocation = this.calcLocation(sField.getScreenLocation(), strFieldDesc);

            Rectangle r = ((VScreenField)sField.getScreenFieldView()).calcBoxShape(ptLocation);
            rect.setBounds(r);      // I do this, because r is a reference and I don't want to change it.
            if (sField.getScreenLocation().getAnchorConstant() == ScreenConstants.FILL_REMAINDER)
            {
                rect.height = Math.min(rect.height, 100); // If this screen fills the remainder, min height = 100 pixels
                rect.width = Math.min(rect.width, 100);   // If this screen fills the remainder, min height = 100 pixels
            }
            this.surveyBox(rect, sField.getScreenLocation());
            ((VScreenField)sField.getScreenFieldView()).setControlExtent(r);

            Component c = (Component)sField.getScreenFieldView().getControl(DBConstants.CONTROL_TOP);
            if (c != null)
            {
                if (c instanceof JLabel)
                {
                    // Adjusts for space at left of Java labels.
                    rect.x     -= 12;
                    rect.width += 12;
                }
                if (sField instanceof BaseScreen)
                    rect.width += 15; // To keep horiz scrollers from appearing
                if (sField.getScreenLocation().getAnchorConstant() == ScreenConstants.FILL_REMAINDER)
                {   // Fill the remainder of the screen area
                    rect.width = parent.getBounds().width - rect.x;     // Only stretch width for edit fields
                    rect.height = parent.getBounds().height - rect.y;
                }

                rect.x += insets.left;
                rect.y += insets.top;
                c.setBounds(rect);
            }
        }
        screenLocation = ((BasePanel)m_screen.getScreenField()).getNextLocation(ScreenConstants.ADD_SCREEN_VIEW_BUFFER, ScreenConstants.SET_ANCHOR);
        this.calcLocation(screenLocation, null);
        m_dimFarthest.width += insets.left + insets.right;      // Add the insets to the screen size
        m_dimFarthest.height += insets.top + insets.bottom;
    }
    /**
     * Return the minimum size of this screen.
     * Since this is a fixed size screen, the minimum size is the preferred size.
     * @param parent The parent container.
     */
    public Dimension preferredLayoutSize(Container parent)
    {
        if (m_dimFarthest.width == 0) if (m_dimFarthest.height == 0)
        {   // Container has not been laid out yet, lay it out now
            this.layoutContainer(parent); // This should be okay (be careful of endless calls)
        }
        return m_dimFarthest;
    }
    /**
     * Return the minimum size of this screen.
     * Since this is a fixed size screen, the minimum size is the preferred size.
     * @param parent The parent container.
     */
    public Dimension minimumLayoutSize(Container parent)
    {
        return this.preferredLayoutSize(parent);
    }
    /**
     * Returns the maximum dimensions for this layout given the components
     * in the specified target container.
     * @param target the component which needs to be laid out
     * @see Container
     * @see #minimumLayoutSize
     * @see #preferredLayoutSize
     */
    public Dimension maximumLayoutSize(Container target)
    {
        return this.preferredLayoutSize(target);
    }
    /**
     * Remote this component (ignored).
     * @param comp The component to remove.
     */
    public void removeLayoutComponent(Component comp)
    {
    }
    /**
     * Set the next anchor location.
     * @param newAnchorLocation The new anchor location.
     */
    public void setAnchor(Rectangle newAnchorLocation)
    {
        m_rectAnchor = newAnchorLocation;
    }
    /**
     * Survey this control's boundaries to resize the screen.
     * @param box The box to survey.
     */
    public void surveyBox(Rectangle box, ScreenLocation screenLocation)
    {
        if (screenLocation.getLocationConstant() == ScreenConstants.FIELD_DESC)
            return;     // Don't survey these
        if (screenLocation.getAnchorConstant() != ScreenConstants.DONT_SET_ANCHOR)
        {
            m_rectAnchor.width = box.width;
            m_rectAnchor.height = box.height;
        }
        m_rectLastField.x = box.x;
        m_rectLastField.y = box.y;
        m_rectLastField.height = box.height;
        m_rectLastField.width = box.width;
        this.checkFarthestField(new Point(box.x + box.width, box.y + box.height)); // Bottom x
    }
    /**
     * Given this position and anchor flag, figure out the logical Point for this control.
     * @param location The location to calculate.
     * @return The target position.
     */
    public Point calcLocation(ScreenLocation location, String strFieldDesc)
    {
        short setNewAnchor = location.getAnchorConstant();
        short m_sLocation = location.getLocationConstant();
        int m_iColumn = location.getColumn();
        int m_iRow = location.getRow();

        if (m_sLocation == -1)
        {   // Duuuh, Don't do this... Sub-class ScreenLayout to get this listener
            Point screenPosition = new Point(0, 0);
            screenPosition.x = m_screenInfo.getColumnLocation(m_iColumn);
            screenPosition.y = m_screenInfo.getRowLocation((BasePanel)m_screen.getScreenField(), m_iRow);
            if (setNewAnchor == ScreenConstants.SET_ANCHOR)
            {
                m_rectAnchor.x = screenPosition.x;      // New reference for more fields
                m_rectAnchor.y = screenPosition.y;      // New reference for more fields
            }
            m_rectLastField.x = screenPosition.x;
            m_rectLastField.y = screenPosition.y;   // In any case, set this position
            return screenPosition;
        }
        else
        {
            Point nextPosition = new Point(0, 0);
            switch (m_sLocation)
            {
                case ScreenConstants.RIGHT_OF_LAST:
                case ScreenConstants.RIGHT_OF_LAST_BUTTON:
                case ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP:
                case ScreenConstants.RIGHT_OF_LAST_CHECKBOX:
                    nextPosition.y = m_rectLastField.y;
                    nextPosition.x = m_rectLastField.x + m_rectLastField.width;
                    if (m_sLocation == ScreenConstants.RIGHT_OF_LAST)
                        nextPosition.x += m_screenInfo.getColumnWidth(1); // x of last char + gap between columns
                    if (m_sLocation == ScreenConstants.RIGHT_OF_LAST_CHECKBOX)
                        if (strFieldDesc != null)
                            nextPosition.x += m_screenInfo.getColumnWidth(strFieldDesc.length() + 1); // x of last char + gap between columns
                    if (m_sLocation == ScreenConstants.RIGHT_OF_LAST_BUTTON_WITH_GAP)
                        nextPosition.x += 5;    // Add a gap between the buttons
                    if (location.getAnchorConstant() == ScreenConstants.ANCHOR_DEFAULT)
                        setNewAnchor = ScreenConstants.DONT_SET_ANCHOR;
                    break;
                case ScreenConstants.BELOW_LAST:
                    nextPosition.x = m_rectLastField.x;
                    nextPosition.y = m_rectLastField.y + m_screenInfo.getRowHeight((BasePanel)m_screen.getScreenField()); // Bottom of last char + gap between lines
                    if (setNewAnchor == ScreenConstants.ANCHOR_DEFAULT)
                        setNewAnchor = ScreenConstants.SET_ANCHOR;
                    break;
                case ScreenConstants.BELOW_LAST_CONTROL:
                case ScreenConstants.BELOW_LAST_DESC:
                    nextPosition.x = m_rectLastField.x;
                    if (m_sLocation == ScreenConstants.BELOW_LAST_DESC)
                        nextPosition.x = m_rectAnchor.x - m_screenInfo.getColumnWidth(ScreenConstants.kMaxDescLength + 1);
                    if (nextPosition.x < 0)
                        nextPosition.x = 5;
                    nextPosition.y = m_rectLastField.y + m_rectLastField.height;    // Bottom of last char + gap between lines
                    if (setNewAnchor == ScreenConstants.ANCHOR_DEFAULT)
                        setNewAnchor = ScreenConstants.SET_ANCHOR;
                    break;
                case ScreenConstants.TOP_NEXT:
                    int iDescCharWidth = 1;   // Buffer space
                    if (m_sLocation == ScreenConstants.TOP_NEXT)
                        iDescCharWidth += ScreenConstants.kMaxDescLength;
                    nextPosition = new Point(this.getFarthestField().width + m_screenInfo.getColumnWidth(iDescCharWidth), m_screenInfo.getRowLocation((BasePanel)m_screen.getScreenField(), 1));
                    if (setNewAnchor == ScreenConstants.ANCHOR_DEFAULT)
                        setNewAnchor = ScreenConstants.SET_ANCHOR;
                    break;
                case ScreenConstants.POPUP_DESC:
                case ScreenConstants.CHECK_BOX_DESC:
                case ScreenConstants.FIELD_DESC:
                    nextPosition = new Point(m_rectLastField.x, m_rectLastField.y); // m_anchorLocation;
                    nextPosition.x -= m_screenInfo.getColumnWidth(ScreenConstants.kMaxDescLength + 1);
                    if (m_sLocation == ScreenConstants.FIELD_DESC)
                        nextPosition.y += ScreenInfo.kFieldVertOffset;
                    if (setNewAnchor == ScreenConstants.ANCHOR_DEFAULT)
                        setNewAnchor = ScreenConstants.DONT_SET_ANCHOR;
                    break;
                case ScreenConstants.AT_ANCHOR:     // This may be obsolete
                    nextPosition = new Point(m_rectAnchor.x, m_rectAnchor.y);   // New reference for more fields
                    break;
                case ScreenConstants.ADD_VIEW_BUFFER: // Add no buffer around the current view
                case ScreenConstants.ADD_SCREEN_VIEW_BUFFER:    // Add a small buffer around the current view
                case ScreenConstants.ADD_GRID_SCREEN_BUFFER:    // Add a small buffer around the current grid cell (enough to still show the 3dRect)
                    Dimension viewSize = this.getFarthestField();
                    nextPosition = new Point(viewSize.width, viewSize.height);
                    if (m_sLocation == ScreenConstants.ADD_SCREEN_VIEW_BUFFER)
                    {
                        nextPosition.x += ScreenInfo.kHorizBuffer;
                        nextPosition.y += ScreenInfo.kVerticalBuffer;
                    }
                    if (m_sLocation == ScreenConstants.ADD_GRID_SCREEN_BUFFER)
                    {
                        nextPosition.x += 3;
                        nextPosition.y += 3;
                    }
                    if (setNewAnchor == ScreenConstants.ANCHOR_DEFAULT)
                        setNewAnchor = ScreenConstants.SET_ANCHOR;
                    break;
                case ScreenConstants.FIRST_FIELD_BUTTON_LOCATION:   // BaseField buttons displayed
                case ScreenConstants.FIRST_LOCATION:
                case ScreenConstants.FIRST_INPUT_LOCATION:
                case ScreenConstants.FIRST_DISPLAY_LOCATION:
                case ScreenConstants.FIRST_FRAME_LOCATION:
                case ScreenConstants.FIRST_SCREEN_LOCATION:
                    nextPosition = new Point(0, 0);     // Default
                    m_rectLastField.setBounds(0, 0, 0, 0);  // Reset
                    m_rectAnchor.setBounds(0, 0, 0, 0);     // Reset
                    if (setNewAnchor == ScreenConstants.ANCHOR_DEFAULT)
                        setNewAnchor = ScreenConstants.SET_ANCHOR;
                    if ((m_sLocation == ScreenConstants.FIRST_LOCATION) || (m_sLocation == ScreenConstants.FIRST_INPUT_LOCATION))
                        nextPosition = new Point(m_screenInfo.getColumnLocation(ScreenConstants.kMaxDescLength + ScreenInfo.kHorizontalExtraChars), m_screenInfo.getRowLocation((BasePanel)m_screen.getScreenField(), 0));
                    if (m_sLocation == ScreenConstants.FIRST_DISPLAY_LOCATION)
                        nextPosition = new Point(3, 3);   // Enough to display 3DRect //xthis.GetColumnLocation(0) + ScreenConstants.kGridButtonOffset, this.getRowLocation(m_screen, 1));  //***GET RID OF THIS**
                    break;
                case ScreenConstants.NEXT_INPUT_LOCATION:
                    nextPosition.x = m_screenInfo.getColumnLocation(ScreenConstants.kMaxDescLength + ScreenInfo.kHorizontalExtraChars);
                    nextPosition.y = this.getFarthestField().height + ScreenInfo.kExtraInterRowSpacing;   // Bottom of last char + gap between lines
                    break;
                case ScreenConstants.FLUSH_RIGHT:
                case ScreenConstants.CENTER:
                case ScreenConstants.FLUSH_LEFT:
                    nextPosition.x = 0;
                    nextPosition.y = this.getFarthestField().height + ScreenInfo.kExtraInterRowSpacing;   // Bottom of last char + gap between lines
                    if (setNewAnchor == ScreenConstants.ANCHOR_DEFAULT)
                        setNewAnchor = ScreenConstants.SET_ANCHOR;
                    break;
                case ScreenConstants.RIGHT_WITH_DESC: // Right of this position (so desc starts at leftmost position!)
                    nextPosition.y = m_rectLastField.y;
                    nextPosition.x = m_rectLastField.x + m_rectLastField.width + m_screenInfo.getColumnWidth(14); // x of last char + gap between columns
                    if (setNewAnchor == ScreenConstants.ANCHOR_DEFAULT)
                        setNewAnchor = ScreenConstants.DONT_SET_ANCHOR;
                    break;
                case ScreenConstants.NEXT_LOGICAL:
                case ScreenConstants.BELOW_LAST_ANCHOR:
                default:
                    nextPosition = new Point(m_rectAnchor.x, m_rectAnchor.y);
                    if (((m_rectAnchor.x == m_rectLastField.x) && (m_rectAnchor.y == m_rectLastField.y)) && (m_rectLastField.height > 0))
                        nextPosition.y += m_rectLastField.height + ScreenInfo.kExtraInterRowSpacing - ScreenInfo.kExtraBoxSpacing;  // Bottom of last char + gap between lines
                    else if (m_rectAnchor.height > 0)
                        nextPosition.y += m_rectAnchor.height + ScreenInfo.kExtraInterRowSpacing - ScreenInfo.kExtraBoxSpacing;  // Bottom of last char + gap between lines
                    else
                        nextPosition.y += m_screenInfo.getRowHeight((BasePanel)m_screen.getScreenField());  // Bottom of last char + gap between lines
                    if (setNewAnchor == ScreenConstants.ANCHOR_DEFAULT)
                        setNewAnchor = ScreenConstants.SET_ANCHOR;
                    break;
            }
            this.checkFarthestField(nextPosition);
            if (setNewAnchor == ScreenConstants.SET_ANCHOR)
            {
                m_rectAnchor.x = nextPosition.x;        // New reference for more fields
                m_rectAnchor.y = nextPosition.y;        // New reference for more fields
            }
            if (m_sLocation != ScreenConstants.FIELD_DESC)
            {
                m_rectLastField.x = nextPosition.x;
                m_rectLastField.y = nextPosition.y;     // In any case, set this position
            }
            return nextPosition;
        }
    } 
    /**
     * Reset the farthest point survey variable.
     */
    public void resetFarthestField()
    {
        m_dimFarthest.width = 0;
        m_dimFarthest.height = 0;
    }
    /**
     * Enlarge the View size if this control is larger than the current view.
     * @param itsRightBottom The right bottom of the control to survey.
     */
    public void checkFarthestField(Point itsRightBottom)
    {
        m_dimFarthest.width = Math.max(m_dimFarthest.width, itsRightBottom.x);
        m_dimFarthest.height = Math.max(m_dimFarthest.height, itsRightBottom.y);
    }
    /**
     * Size of the physical view.
     * @return The dimension of the farthest field.
     */
    public Dimension getFarthestField()
    {
        return m_dimFarthest;
    }
    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     * @param parent The parent container.
     * @return The layout alignment.
     */
    public float getLayoutAlignmentX(Container parent)
    {
        return 0.0f;
    }
    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     * @param parent The parent container.
     * @return The layout alignment.
     */
    public float getLayoutAlignmentY(Container parent)
    {
        return 0.0f;
    }
    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     * @param target The target container.
     */
    public void invalidateLayout(Container target)
    {
    }              
}
