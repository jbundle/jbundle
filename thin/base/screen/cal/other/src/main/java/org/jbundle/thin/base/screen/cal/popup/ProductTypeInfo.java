package org.jbundle.thin.base.screen.cal.popup;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.ImageIcon;


/**
 * Product type information. Resource key, icons, highlight colors, etc.
 */
public class ProductTypeInfo extends Object
{
    protected String m_strDescription = null;
    protected ImageIcon m_iconStart = null;
    protected ImageIcon m_iconEnd = null;
    protected Color m_colorHighlight = null;
    protected Color m_colorSelect = null;

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
    public ProductTypeInfo(String strKey, Color colorHighlight, Color colorSelect, boolean bSameEndIcon)
    {
        this();
        this.init(strKey, org.jbundle.thin.base.screen.BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/" + strKey + ".gif", "Start Icon"), (bSameEndIcon ? org.jbundle.thin.base.screen.BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/" + strKey, "End Icon") : null), colorHighlight, colorSelect);
    }
    /**
     * Constructor.
     */
    public ProductTypeInfo(String strKey, ImageIcon iconStart, ImageIcon iconEnd, Color colorHighlight, Color colorSelect)
    {
        this();
        this.init(strKey, iconStart, iconEnd, colorHighlight, colorSelect);
    }
    /**
     * Constructor.
     */
    public void init(String strKey, ImageIcon iconStart, ImageIcon iconEnd, Color colorHighlight, Color colorSelect)
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
    public Color getHighlightColor()
    {
        return m_colorHighlight;
    }
    /**
     * Highlight color (optional).
     */
    public Color getSelectColor()
    {
        return m_colorSelect;
    }

    static Hashtable<String,ProductTypeInfo> m_ht = new Hashtable<String,ProductTypeInfo>();
    static  {
        m_ht.put(ProductConstants.AIR, new ProductTypeInfo(ProductConstants.AIR, new Color(255, 192, 192), null, false));
        m_ht.put(ProductConstants.HOTEL, new ProductTypeInfo(ProductConstants.HOTEL, new Color(192, 192, 255), null, true));
        m_ht.put(ProductConstants.LAND, new ProductTypeInfo(ProductConstants.LAND, new Color(192, 255, 255), null, false));
        m_ht.put(ProductConstants.CAR, new ProductTypeInfo(ProductConstants.CAR, new Color(255, 192, 255), null, true));
        m_ht.put(ProductConstants.CRUISE, new ProductTypeInfo(ProductConstants.CRUISE, new Color(192, 255, 192), null, false));
        m_ht.put(ProductConstants.TRANSPORTATION, new ProductTypeInfo(ProductConstants.TRANSPORTATION, new Color(192, 192, 192), null, false));
        m_ht.put(ProductConstants.TOUR, new ProductTypeInfo(ProductConstants.TOUR, new Color(255, 255, 255), null, false));
        m_ht.put(ProductConstants.ITEM, new ProductTypeInfo(ProductConstants.ITEM, new Color(224, 224, 224), null, false));

        m_ht.put(ProductConstants.MEAL, new ProductTypeInfo(ProductConstants.MEAL, null, null, false));

        m_ht.put(ProductConstants.COST, new ProductTypeInfo(ProductConstants.COST, null, null, true));
        m_ht.put(ProductConstants.PRICE, new ProductTypeInfo(ProductConstants.PRICE, null, null, true));
        m_ht.put(ProductConstants.INVENTORY, new ProductTypeInfo(ProductConstants.INVENTORY, null, null, false));
        m_ht.put(ProductConstants.NO_INVENTORY, new ProductTypeInfo(ProductConstants.NO_INVENTORY, null, null, false));
        m_ht.put(ProductConstants.BOOKING, new ProductTypeInfo(ProductConstants.BOOKING, null, null, true));
    }
    public static ProductTypeInfo getProductType(String strProductType)
    {
        return m_ht.get(strProductType);
    }
}
