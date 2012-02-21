/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    private void readObject(ObjectInputStream s) throws ClassNotFoundException,
            IOException {
        s.defaultReadObject();

        width = s.readInt();
        height = s.readInt();
        pixels = (int[]) (s.readObject());

    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        s.writeInt(width);
        s.writeInt(height);
        s.writeObject(pixels);
    }

    public int getImageWidth() {
        return width;
    }

    public int getImageHeight() {
        return height;
    }

    public Object getImage() {
        return null;
    }

    public void setImage(Object image) {
    }
    
}
