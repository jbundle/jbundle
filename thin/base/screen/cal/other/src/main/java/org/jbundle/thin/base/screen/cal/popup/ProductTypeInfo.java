/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.popup;

import java.util.Hashtable;

import javax.swing.ImageIcon;

import org.jbundle.model.util.Colors;


/**
 * Product type information. Resource key, icons, highlight colors, etc.
 */
public class ProductTypeInfo extends Object
{
    protected String m_strDescription = null;
    protected ImageIcon m_iconStart = null;
    protected ImageIcon m_iconEnd = null;
    protected int m_colorHighlight = 0;
    protected int m_colorSelect = 0;

    /**
     * Constructor.
     */
    public ProductTypeInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ProductTypeInfo(String strKey, int colorHighlight, int colorSelect, boolean bSameEndIcon)
    {
        this();
        this.init(strKey, org.jbundle.thin.base.screen.BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/" + strKey + ".gif", "Start Icon"), (bSameEndIcon ? org.jbundle.thin.base.screen.BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/" + strKey, "End Icon") : null), colorHighlight, colorSelect);
    }
    /**
     * Constructor.
     */
    public ProductTypeInfo(String strKey, ImageIcon iconStart, ImageIcon iconEnd, int colorHighlight, int colorSelect)
    {
        this();
        this.init(strKey, iconStart, iconEnd, colorHighlight, colorSelect);
    }
    /**
     * Constructor.
     */
    public void init(String strKey, ImageIcon iconStart, ImageIcon iconEnd, int colorHighlight, int colorSelect)
    {
        m_strDescription = org.jbundle.thin.base.screen.BaseApplet.getSharedInstance().getString(strKey);
        if ((m_strDescription == null) || (m_strDescription.length() == 0))
            m_strDescription = strKey;
        m_iconStart = iconStart;
        m_iconEnd = iconEnd;
        m_colorHighlight = colorHighlight;
        m_colorSelect = colorSelect;
    }
    /**
     * I'm done with this item, free the resources.
     */
    public void free()
    {
    }
    /**
     * Get the description.
     */
    public String getDescription()
    {
        return m_strDescription;
    }
    /**
     * Get the start icon (opt).
     */
    public ImageIcon getStartIcon()
    {
        return m_iconStart;
    }
    /**
     * Get the ending icon (optional).
     */
    public ImageIcon getEndIcon()
    {
        return m_iconEnd;
    }
    /**
     * Highlight color (optional).
     */
    public int getHighlightColor()
    {
        return m_colorHighlight;
    }
    /**
     * Highlight color (optional).
     */
    public int getSelectColor()
    {
        return m_colorSelect;
    }

    static Hashtable<String,ProductTypeInfo> m_ht = new Hashtable<String,ProductTypeInfo>();
    static  {
        m_ht.put(ProductConstants.AIR, new ProductTypeInfo(ProductConstants.AIR, 0x00ffc0c0, Colors.NULL, false));
        m_ht.put(ProductConstants.HOTEL, new ProductTypeInfo(ProductConstants.HOTEL, 0x00c0c0ff, Colors.NULL, true));
        m_ht.put(ProductConstants.LAND, new ProductTypeInfo(ProductConstants.LAND, 0x00c0ffff, Colors.NULL, false));
        m_ht.put(ProductConstants.CAR, new ProductTypeInfo(ProductConstants.CAR, 0x00ffc0ff, Colors.NULL, true));
        m_ht.put(ProductConstants.CRUISE, new ProductTypeInfo(ProductConstants.CRUISE, 0x00c0ffc0, Colors.NULL, false));
        m_ht.put(ProductConstants.TRANSPORTATION, new ProductTypeInfo(ProductConstants.TRANSPORTATION, 0x00c0c0c0, Colors.NULL, false));
        m_ht.put(ProductConstants.TOUR, new ProductTypeInfo(ProductConstants.TOUR, 0x00ffffff, Colors.NULL, false));
        m_ht.put(ProductConstants.ITEM, new ProductTypeInfo(ProductConstants.ITEM, 0xe0e0e0, Colors.NULL, false));

        m_ht.put(ProductConstants.MEAL, new ProductTypeInfo(ProductConstants.MEAL, Colors.NULL, Colors.NULL, false));

        m_ht.put(ProductConstants.COST, new ProductTypeInfo(ProductConstants.COST, Colors.NULL, Colors.NULL, true));
        m_ht.put(ProductConstants.PRICE, new ProductTypeInfo(ProductConstants.PRICE, Colors.NULL, Colors.NULL, true));
        m_ht.put(ProductConstants.INVENTORY, new ProductTypeInfo(ProductConstants.INVENTORY, Colors.NULL, Colors.NULL, false));
        m_ht.put(ProductConstants.NO_INVENTORY, new ProductTypeInfo(ProductConstants.NO_INVENTORY, Colors.NULL, Colors.NULL, false));
        m_ht.put(ProductConstants.BOOKING, new ProductTypeInfo(ProductConstants.BOOKING, Colors.NULL, Colors.NULL, true));
    }
    public static ProductTypeInfo getProductType(String strProductType)
    {
        return m_ht.get(strProductType);
    }
}
