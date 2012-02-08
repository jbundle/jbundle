/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.Serializable;

/**
 * Serialize an image.
 * @author Don Corley <don@donandann.com>
 *
 */
public class SerializableImage extends PortableImage
    implements Serializable, ImageObserver {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an Image that can be serialized.
     */
    public SerializableImage() {
    }

    public SerializableImage(Image image) {
        super(image);
    }

    public Image getImage() {
        Image image = null;

        if (pixels != null) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            ColorModel cm = ColorModel.getRGBdefault();
            image = tk.createImage(new MemoryImageSource(width, height, cm, pixels, 0, width));
        }
        return image;
    }

    public void setImage(Object image) {
        
        loadImage((Image)image);

        width = ((Image)image).getWidth(this);
        height = ((Image)image).getHeight(this);
        pixels = image != null ? new int[width * height] : null;

        if (image != null) {
            try {
                PixelGrabber pg = new PixelGrabber((Image)image, 0, 0, width, height, pixels, 0, width);
                pg.grabPixels();
                if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
                    throw new RuntimeException("failed to load image contents");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("image load interrupted");
            }
        }
    }
    
    static MediaTracker tracker = null;
    static int mediaTrackerID = 0;
    
    /**
     * Loads the image, returning only when the image is loaded.
     * Note: This USUSALLY is a bad idea (to hang this thread until the image loads),
     * but in this case, images are ONLY loaded here when they are initially set which
     * is when they are selected by the user.
     * @param image the image
     */
    protected void loadImage(Image image) {
        MediaTracker mTracker = getTracker();
        synchronized(mTracker) {
            int id = getNextID();

            mTracker.addImage(image, id);
        try {
                mTracker.waitForID(id, 0);
        } catch (InterruptedException e) {
        System.out.println("INTERRUPTED while loading Image");
        }
            //?int loadStatus = mTracker.statusID(id, false);
            mTracker.removeImage(image, id);
    }
    }

    /**
     * Returns an ID to use with the MediaTracker in loading an image.
     */
    private int getNextID() {
        synchronized(getTracker()) {
            return ++mediaTrackerID;
        }
    }

    /**
     * Returns the MediaTracker for the current AppContext, creating a new
     * MediaTracker if necessary.
     */
    private MediaTracker getTracker() {
        // Opt: Only synchronize if trackerObj comes back null?
        // If null, synchronize, re-check for null, and put new tracker
        synchronized(this) {
            if (tracker == null) {
                @SuppressWarnings("serial")
                Component comp = new Component() {};
                tracker = new MediaTracker(comp);
            }
        }
        return (MediaTracker) tracker;
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y,
            int width, int height) {
        // Since I wait for an image to load, I know I have the image.
        return true;
    }
}
