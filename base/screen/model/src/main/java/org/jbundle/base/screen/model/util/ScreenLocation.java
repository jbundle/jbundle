/*

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.util;

import org.jbundle.model.screen.ScreenLoc;

/**
 * @(#)STEView.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

/**
 * ScreenLocation - Describes the relative position and size of a control.<p>
 * For Example, position could be BelowLast, RightOfLast, etc.
 * This allows for the correct screen layout independent of Font Size and metrics.
 */
public class ScreenLocation extends Object
    implements ScreenLoc
{
    /**
     * The location (see the location constants in ScreenConstants).
     */
    protected short m_sLocation;
    /**
     * Set the anchor for this control?
     */
    protected short m_sSetAnchor;
    /**
     * Row (if location is not used).
     */
    protected int m_iRow;
    /**
     * Column (if location is not used).
     */
    protected int m_iColumn;

    /**
     * Constructor.
     * @param sSetAnchor Set the anchor for this control?
     * @param iRow (if location is not used).
     * @param iColumn (if location is not used).
     */
    public ScreenLocation(int iRow, int iColumn, short sSetAnchor)
    {
        m_sLocation = -1;
        m_sSetAnchor = sSetAnchor;
        m_iRow = iRow;
        m_iColumn = iColumn;
    }
    /**
     * Constructor.
     * @param sSetAnchor Set the anchor for this control?
     * @param sLocation The location (see the location constants in ScreenConstants).
     */
    public ScreenLocation(short sLocation, short sSetAnchor)
    {
        m_sLocation = sLocation;
        m_sSetAnchor = sSetAnchor;
        m_iRow = -1;
        m_iColumn = -1;
    }
    /**
     * Get the anchor constant.
     * @return Set the anchor for this control.
     */
    public short getAnchorConstant()
    {
        return m_sSetAnchor;
    }
    /**
     * Get the location constant.
     * @return The location (see the location constants in ScreenConstants).
     */
    public short getLocationConstant()
    {
        return m_sLocation;
    }
    /**
     * Get the column constant.
     * @return The column for this control.
     */
    public int getColumn()
    {
        return m_iColumn;
    }
    /**
     * Get the row constant.
     * @return The row for this control.
     */
    public int getRow()
    {
        return m_iRow;
    }
}
