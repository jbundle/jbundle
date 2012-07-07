/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.util;

/**
 * Serialize an image.
 * @author Don Corley <don@donandann.com>
 *
 */
public interface PortableImageUtil
{
	public static final String PNG = "png";
	public static final String DEFAULT_FORMAT = PNG;

    public Object getImage(PortableImage portableImage);
    
    public void setImage(PortableImage portableImage, Object image);    
}
