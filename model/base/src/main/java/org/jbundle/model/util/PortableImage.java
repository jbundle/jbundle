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
    protected int[] pixels;

    /**
     * Creates an Image that can be serialized.
     */
    public PortableImage() {
    }

    public PortableImage(Object image) {
        this.setImage(image);
    }

    public PortableImage(int[] pixels, int width, int height) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
    }

    public int getImageWidth() {
        return width;
    }

    public int getImageHeight() {
        return height;
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public Object getImage() {
        return Util.getPortableImageUtil().getImage(this);
    }

    public void setImage(Object image) {
        Util.getPortableImageUtil().setImage(this, image);
    }
    
}
