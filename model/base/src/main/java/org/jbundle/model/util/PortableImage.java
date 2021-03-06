/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.util;

import java.io.Serializable;

/**
 * Platform independent image.
 * Serializable and android/java independent image.
 * Override this with your concrete implementation.
 * @author Don Corley <don@donandann.com>
 */
public class PortableImage extends Object
    implements Serializable {

    private static final long serialVersionUID = 1L;

    protected int width;
    protected int height;
    protected byte[] data;
	protected String format;

    /**
     * Creates an Image that can be serialized.
     */
    public PortableImage() {
    }

    public PortableImage(Object image) {
        this.setImage(image);
    }

    public PortableImage(byte[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public int getImageWidth() {
        return width;
    }

    public int getImageHeight() {
        return height;
    }

    public byte[] getData() {
        return data;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

    public Object getImage() {
        return Util.getPortableImageUtil().getImage(this);
    }

    public void setImage(Object image) {
        Util.getPortableImageUtil().setImage(this, image);
    }
    
}
