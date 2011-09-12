/*
 * BlueTheme.java
 *
 * Created on August 23, 2005, 5:25 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.landf.theme;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 *
 * @author don
 */
public class CustomTheme extends DefaultMetalTheme 
{
    
    protected ColorUIResource primaryColor1 = null;
    protected ColorUIResource primaryColor2 = null;
    protected ColorUIResource primaryColor3 = null;

    protected ColorUIResource secondaryColor1 = null;
    protected ColorUIResource secondaryColor2 = null;
    protected ColorUIResource secondaryColor3 = null;

    protected ColorUIResource blackColor = null;
    protected ColorUIResource whiteColor = null;
    
    protected FontUIResource defaultFont = null;

    /**
     * Creates a new instance of CustomTheme
     * Note: MUST only have default constructor!
     */
    public CustomTheme()
    {
        super();
    }

    public String getName()
    {
        return "Custom";
    }

    protected ColorUIResource getPrimary1()
    {
        if (primaryColor1 == null)
            return super.getPrimary1();
        else
            return primaryColor1;
    }
    protected ColorUIResource getPrimary2()
    {
        if (primaryColor2 == null)
            return super.getPrimary2();
        else
            return primaryColor2;
    }
    protected ColorUIResource getPrimary3()
    {
        if (primaryColor3 == null)
            return super.getPrimary3();
        else
            return primaryColor3;
    }
    protected ColorUIResource getSecondary1()
    {
        if (secondaryColor1 == null)
            return super.getSecondary1();
        else
            return secondaryColor1;
    }
    protected ColorUIResource getSecondary2()
    {
        if (secondaryColor2 == null)
            return super.getSecondary2();
        else
            return secondaryColor2;
    }
    protected ColorUIResource getSecondary3()
    {
        if (secondaryColor3 == null)
            return super.getSecondary3();
        else
            return secondaryColor3;
    }

    protected ColorUIResource getBlack()
    {
        if (blackColor == null)
            return super.getBlack();
        else
            return blackColor;
    }
    protected ColorUIResource getWhite()
    {
        if (whiteColor == null)
            return super.getWhite();
        else
            return whiteColor;
    }
    public FontUIResource getControlTextFont()
    { 
        if (defaultFont == null)
            return super.getControlTextFont();
        else
            return defaultFont;
    }

    public FontUIResource getSystemTextFont()
    { 
        if (defaultFont == null)
            return super.getSystemTextFont();
        else
            return defaultFont;
    }

    public FontUIResource getUserTextFont()
    { 
        if (defaultFont == null)
            return super.getUserTextFont();
        else
            return defaultFont;
    }

    public FontUIResource getMenuTextFont()
    { 
        if (defaultFont == null)
            return super.getMenuTextFont();
        else
            return defaultFont;
    }

    public FontUIResource getWindowTitleFont()
    { 
        if (defaultFont == null)
            return super.getWindowTitleFont();
        else
            return defaultFont;
    }

    public FontUIResource getSubTextFont()
    { 
        if (defaultFont == null)
            return super.getSubTextFont();
        else
            return defaultFont;
    }
    /**
     * Set the primary colors based on this (primary1) color.
     */
    public void setPrimaryColor(ColorUIResource primary)
    {
        primaryColor3 = primary;
        primaryColor2 = CustomTheme.darken(primaryColor3);
        primaryColor1 = CustomTheme.darken(primaryColor2);
    }
    /**
     * Set the secondary colors based on this (secondary1) color.
     */
    public void setSecondaryColor(ColorUIResource secondary)
    {
        secondaryColor3 = secondary;
        secondaryColor2 = CustomTheme.darken(secondaryColor3);
        secondaryColor1 = CustomTheme.darken(secondaryColor2);
    }
    /**
     * Set the black color for this theme.
     */
    public void setBlack(ColorUIResource color)
    {
        blackColor = color;
    }
    /**
     * Set the white color for this theme.
     */
    public void setWhite(ColorUIResource color)
    {
        whiteColor = color;
    }
    /**
     *
     */
    public void setDefaultFont(FontUIResource font)
    {
        defaultFont = font;
    }
    /**
     * Lighten this color.
     */
    public static ColorUIResource darken(ColorUIResource color)
    {
        return new ColorUIResource(darkenRGB(color.getRed()), darkenRGB(color.getGreen()), darkenRGB(color.getBlue()));
    }
    /**
     * Lighten this red, green, or blue component.
     */
    public static int darkenRGB(int rgb)
    {
        return Math.max(rgb - 0x33, 0x00);
    }
}
