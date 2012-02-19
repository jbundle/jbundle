/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.swing.util;

/**
 * @(#)ScreenInfo.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.control.swing.SApplet;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.model.PropertyOwner;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.landf.ScreenUtil;
import org.jbundle.thin.base.screen.landf.theme.CustomTheme;


/**
 * Layout manager for Screens.
 */
public class ScreenInfo extends Object
{
    public static final int FIRST_ROW_OFFSET = 4;    // start 5 pixels down on displays
    public static final int EXTRA_BOX_SPACING = 5; // Extra pixels needed for a box (Vertical)
    public static final int EXTRA_ROW_SPACING = 2;
    public static final int HORIZONTAL_EXTRA_CHARS = 2;  // Extra chars in horizontal position
    public static final int FIELD_VERT_OFFSET = EXTRA_BOX_SPACING / 2; // Offset for a description
    public static final int EXTRA_INTER_ROW_SPACING = EXTRA_BOX_SPACING + EXTRA_ROW_SPACING;    // Extra pixels needed per row
    public static final int INIT_GRID_ROW_INSET = 4;
    public static final int BUMP_ROW_HEIGHT_LIMIT = 15; // Bump the row height by one if the height is less than this
    public static final int FIRST_COL_OFFSET = 4;    // start 5 pixels down on displays
    public static final int FIELD_HORIZ_OFFSET = 1;  // Extra pixels needed in each character cell
    public static final int EXTRA_COL_BOX_SPACING = FIELD_HORIZ_OFFSET * 2 + 3;    // Extra pixels needed for a box
    public static final int EXTRA_COL_SPACING = EXTRA_COL_BOX_SPACING * 3;         // Extra space added to buttons and popups
    public static final int CELL_BORDER_WIDTH = 2;
    public static final int SHIFT_TEXT_DOWN = +1;
    public static final int SHIFT_TEXT_RIGHT = +1;

    public static final int HORIZ_BUFFER = 5;       // Buffer around view
    public static final int VERTICAL_BUFFER = 5;

    protected Color m_colorControl = null;
    protected Color m_colorText = null;
    protected int m_Ascent;
    protected int m_Descent;
    protected int m_InternalLeading;
    protected int m_ExternalLeading;
    protected int m_AveCharWidth;
    protected int m_MaxCharWidth;

    protected boolean m_bFirstTime = true;

    protected BaseApplet m_baseApplet = null;
    /**
     *
     */
    protected Font m_fontMenuButton = null;
    /**
     * The font to return to controls (return null if default).
     */
    protected Font m_font = null;
    protected boolean usingDefaultFont = true;
    /**
     * Am I using a custom theme?
     */
    protected boolean m_bCustomTheme = false;
    protected boolean m_bMetalLookAndFeel = true;

    protected Map<String,Object> m_cachedProperties = null;

    /**
     * Constructor.
     */
    public ScreenInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ScreenInfo(BaseApplet baseApplet)
    {
        this();
        this.init(baseApplet);
    }
    /**
     * Initialize the variables.
     */
    public void init(BaseApplet baseApplet)
    {
        this.setControl(baseApplet);

        m_bFirstTime = true;
    }
    /**
     * Set the applet control for this screen.
     * @param baseApplet The applet.
     */
    public void setControl(BaseApplet baseApplet)
    {
        m_baseApplet = baseApplet;
    }
    /**
     * Set the property owner to get params from.
     * @param propertyOwner
     */
    public void setScreenProperties(PropertyOwner propertyOwner, Map<String,Object> properties)
    {
        m_font = ScreenUtil.getFont(propertyOwner, properties);
        if (m_font != null)
            usingDefaultFont = false;
        m_colorControl = ScreenUtil.getColor(ScreenUtil.CONTROL_COLOR, propertyOwner, properties);
        m_colorText = ScreenUtil.getColor(ScreenUtil.TEXT_COLOR, propertyOwner, properties);

        m_bFirstTime = true;
        m_cachedProperties = null;
    }
    /**
     * Get the font params.
     */
    public void calcFontMetrics(Component control)
    {
        if (!m_bFirstTime)
            return;
        m_bFirstTime = false;

        if (m_font == null)
        {
            Component parent = control;
            if (parent == null)
                parent = m_baseApplet;
            if (parent != null)
            { // Sorry, but the default applet font is too ugly!
                m_font = parent.getFont();
                if (m_font == null)
                {
                    while ((m_font == null) && (parent != null) && ((parent = parent.getParent()) != null))
                        m_font = parent.getFont();
                }
            }
            if (m_font == null)
            {
                m_font = new Font("SansSerif", Font.PLAIN, 12);
                usingDefaultFont = false;
            }
        }
        FontMetrics tm = null;
        if (control == null)
            control = m_baseApplet;
        if (control == null) if (m_baseApplet.getParent() != null)
            control = m_baseApplet.getParent();
        if (control == null)
            control = SApplet.getSharedInstance();
        tm = control.getFontMetrics(m_font);
        m_Ascent = tm.getAscent();
        m_Descent = tm.getDescent();
        m_InternalLeading = tm.getMaxDescent(); //** + EXTRA_ROW_PIXELS;
        m_ExternalLeading = tm.getLeading(); //** + EXTRA_INTER_ROW_SPACING;  //*theFontInfo.leading + 3;     // 1 pixel for each box adorner, plus one inbetween
        m_AveCharWidth = this.getCharWidth(tm);   //** dc.getCharWidth('0', '0', width);
        m_MaxCharWidth = tm.charWidth('W');
        
        // Now see if I'm using a custom theme...
        m_bCustomTheme = false;
        m_bMetalLookAndFeel = false;
        if (UIManager.getLookAndFeel() != null)
            if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel)
        {
            m_bMetalLookAndFeel = true;
            if (MetalLookAndFeel.getCurrentTheme() instanceof CustomTheme)
                m_bCustomTheme = true;
        }
    }
    /**
     * Free this object.
     */
    public void free()
    {
    }
    /**
     * Get the box height given this number of rows.
     */
    public int getBoxHeight(BasePanel pBasePanel, int rows)
    {
        int rowHeight = m_Ascent + m_Descent + m_InternalLeading + m_ExternalLeading; //** + EXTRA_ROW_PIXELS;
        rowHeight = rowHeight * rows;
        if (rowHeight < BUMP_ROW_HEIGHT_LIMIT)
            rowHeight++;
        if (((pBasePanel.getScreenType() & ScreenConstants.INPUT_TYPE) != 0) || (pBasePanel.getEditing() == true))
            rowHeight += EXTRA_BOX_SPACING;
        return rowHeight;
    }
    /**
     * Get the box width given this char width.
     */
    public int getBoxWidth(int charWidth)
    {   // Width of a box (weighted, so short boxes with wide chars won't cut them off)
        int oneCharWidth = this.getColumnWidth(1);
        if (charWidth <= 10)        // Add extra space for wide characters in short cells
            oneCharWidth += ((m_MaxCharWidth - m_AveCharWidth) * (11 - charWidth)) / 10;
        return charWidth * oneCharWidth;
    }
    public static final String m_strAlpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * Get the avaerage character width of this font.
     */
    public int getCharWidth(FontMetrics m)
    {
        int iWidth = m.stringWidth(m_strAlpha) / m_strAlpha.length();

        if (iWidth <= 0)
            iWidth = 1;
        return iWidth;
    }
    /**
     * Get the x value of this column.
     */
    public int getColumnLocation(int column)
    {
        return this.getColumnWidth(column - 1) + FIRST_COL_OFFSET;
    }
    /**
     * Get the x width of this many columns.
     */
    public int getColumnWidth(int columns)
    {
        return columns * (m_AveCharWidth + FIELD_HORIZ_OFFSET);
    }
    /**
     * Get the current font metrics.
     */
    public FontMetrics getFontMetrics()
    {
        Component parent = m_baseApplet;
        if (parent == null)
            return null;
        if (m_bFirstTime)
            this.calcFontMetrics(null);
        FontMetrics tm = parent.getFontMetrics(m_font);
        return tm;
    }
    /**
     * Is there a custom theme in use?
     * @return true If there is a custome theme set (so I don't need to set fonts, colors).
     */
    public boolean isCustomTheme()
    {
        if (m_bFirstTime)
            this.calcFontMetrics(null);
        return m_bCustomTheme;
    }
    /**
     *
     */
    public boolean isMetalLookAndFeel()
    {
        if (m_bFirstTime)
            this.calcFontMetrics(null);
        return m_bMetalLookAndFeel;
    }
    /**
     * Get the current font.
     * @return The current font (or null if none).
     */
    public Font getFont()
    {
        if (m_bFirstTime)
            this.calcFontMetrics(null);
        if (usingDefaultFont)
            return null;
        return m_font;
    }
    /**
     * Get the current row height.
     */
    public Color getControlColor()
    {
        return m_colorControl;
    }
    /**
     * Get the current row height.
     */
    public Color getTextColor()
    {
        return m_colorText;
    }
    /**
     * A convience method to get the background color.
     */
    public Color getBackgroundColor()
    {
        if (m_baseApplet != null)
            return m_baseApplet.getBackgroundColor();
        else
            return null;
    }
    /**
     * Set the current font.
     */
    public void setFont(Font font)
    {
        m_font = font;
        m_bFirstTime = true;
    }
    /**
     * Set the current row height.
     */
    public void setControlColor(Color colorControl)
    {
        m_colorControl = colorControl;
    }
    /**
     * Set the current row height.
     */
    public void setTextColor(Color colorText)
    {
        m_colorText = colorText;
    }
    /**
     * Get the current row height.
     */
    public int getRowHeight(BasePanel pBasePanel)
    {
        int rowHeight = m_Ascent + m_Descent + m_InternalLeading + m_ExternalLeading; //** + EXTRA_ROW_PIXELS;
        if (rowHeight < BUMP_ROW_HEIGHT_LIMIT)
            rowHeight++;
        if (((pBasePanel.getScreenType() & ScreenConstants.INPUT_TYPE) != 0) || (pBasePanel.getEditing() == true))
            rowHeight += EXTRA_INTER_ROW_SPACING;
        return rowHeight;
    }
    /**
     * Get the y location of this row.
     */
    public int getRowLocation(BasePanel pBasePanel, int row)
    {
        int location = (row - 1) * this.getRowHeight(pBasePanel);
        if (((pBasePanel.getScreenType() & ScreenConstants.INPUT_TYPE) != 0) || (pBasePanel.getEditing() == true))
            location += FIRST_ROW_OFFSET;
        return location;
    }
    /**
     * Get the UI font property.
     * @param property
     * @return
     */
    public Font getFont(String property)
    {
        Font f = null;
        if (m_cachedProperties != null)
            f = (Font)m_cachedProperties.get(property);
        if (f != null)
            return f;
        f = UIManager.getFont(property);
        if (f != null)
        {
	        if (m_cachedProperties == null)
    	        m_cachedProperties = new HashMap<String,Object>();
        	m_cachedProperties.put(property, f);
        }
        return f;
    }
    /**
     * Return true if this l&f should have a text box holding descriptions on menus
     * @return
     */
    public boolean isTextBoxStyle()
    {
        return true;
    }
}
