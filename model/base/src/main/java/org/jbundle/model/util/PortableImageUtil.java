/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.util;

import java.awt.Image;

import org.jbundle.model.util.PortableImage;

/**
 * Serialize an image.
 * @author Don Corley <don@donandann.com>
 *
 */
public interface PortableImageUtil
{
    public Image getImage(PortableImage portableImage);
    
    public void setImage(PortableImage portableImage, Object image);    
}
