/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.comp;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Tile this image on the background.
 */
public class JTiledImage extends JPanel
{
	private static final long serialVersionUID = 1L;

	/**
     * The image to tile.
     */
    protected ImageIcon m_imageBackground = null;
    /**
     *
     */
    protected Container m_compAnchor = null;
    /**
     *
     */
    protected Point m_offset = new Point(0, 0);

    /**
     * Constructor - Tile this image on the background, using this background color.
     */
    public JTiledImage()
    {
        super();
    }
    /**
     * Constructor - Tile this image on the background, using this background color.
     * @param imageBackground The image to tile as the background.
     * @param colorBackground The color to paint to background (If you don't want a background, set this to non-opaque).
     */
    public JTiledImage(ImageIcon imageBackground, Color colorBackground, Container compAnchor)
    {
        this();
        this.init(imageBackground, colorBackground, compAnchor);
    }
    /**
     * Constructor - Tile this image on the background, using this background color.
     * @param imageBackground The image to tile as the background.
     * @param colorBackground The color to paint to background (If you don't want a background, set this to non-opaque).
     */
    public JTiledImage(ImageIcon imageBackground, Color colorBackground)
    {
        this();
        this.init(imageBackground, colorBackground, null);
    }
    /**
     * Constructor - Tile this image on the background, using this background color.
     * @param imageBackground The image to tile as the background.
     */
    public JTiledImage(ImageIcon imageBackground)
    {
        this();
        this.init(imageBackground, null, null);
    }
    /**
     * Constructor - Tile this image on the background, using this background color.
     * @param colorBackground The color to paint to background (If you don't want a background, set this to non-opaque).
     */
    public JTiledImage(Color colorBackground)
    {
        this();
        this.init(null, colorBackground, null);
    }
    /**
     * Constructor - Initialize this background.
     * @param imageBackground The image to tile as the background.
     * @param colorBackground The color to paint to background (If you don't want a background, set this to non-opaque).
     */
    public void init(ImageIcon imageBackground, Color colorBackground, Container compAnchor)
    {
        m_imageBackground = imageBackground;
        m_compAnchor = compAnchor;
        if (colorBackground != null)
            this.setBackground(colorBackground);
    }
    /**
     * Tile this image on the background, using this background color.
     * @param g The graphics object.
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);        // Just in case the image is bad/not loaded

        Rectangle rectangle = this.getBounds();
        if (m_compAnchor != null)
           this.calcOffset(m_compAnchor, m_offset);

        if (m_imageBackground != null) if (m_imageBackground.getImageLoadStatus() == MediaTracker.COMPLETE)
        {
            for (int y = m_offset.y; y < rectangle.height; y = y + m_imageBackground.getIconHeight())
            {
                for (int x = m_offset.x; x < rectangle.width; x = x + m_imageBackground.getIconWidth())
                {
                    //+ if intersect()
                    m_imageBackground.paintIcon(this, g, x, y);
                }
            }
        }
    }
    /**
     * Calculate the offset from the component to this component.
     * @param compAnchor The component that would be the upper left hand of this screen.
     * @param offset The offset to set for return.
     */
    public void calcOffset(Container compAnchor, Point offset)
    {
        offset.x = 0;
        offset.y = 0;
        Container parent = this;
        while (parent != null)
        {
            offset.x -= parent.getLocation().x;
            offset.y -= parent.getLocation().y;
            parent = parent.getParent();
            if (parent == compAnchor)
                return; // Success
        }
        // Failure - comp not found.
        offset.x = 0;
        offset.y = 0;
    }

}
