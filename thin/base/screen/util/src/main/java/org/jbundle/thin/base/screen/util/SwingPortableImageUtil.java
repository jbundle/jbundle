/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jbundle.model.util.PortableImage;
import org.jbundle.model.util.PortableImageUtil;

/**
 * Serialize an image.
 * @author Don Corley <don@donandann.com>
 *
 */
public class SwingPortableImageUtil extends Object
    implements PortableImageUtil
{

    /**
     * Creates an Image that can be serialized.
     */
    public SwingPortableImageUtil() {
    }

    public Image getImage(PortableImage portableImage) {
    	
        Image image = null;

        byte[] data = portableImage.getData();
        ByteArrayInputStream baIn = new ByteArrayInputStream(data);
        try {
			image = ImageIO.read(baIn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
/*        if (portableImage.getPixels() != null) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            ColorModel cm = ColorModel.getRGBdefault();
            image = tk.createImage(new MemoryImageSource(portableImage.getImageWidth(), portableImage.getImageHeight(), cm, portableImage.getPixels(), 0, portableImage.getImageWidth()));
        }
*/      return image;
    }

    public void setImage(PortableImage portableImage, Object image) {
        
        loadImage((Image)image);

    	int height = ((Image)image).getHeight(observer);
        int width = ((Image)image).getWidth(observer);

        BufferedImage bufferedImage = null;
        if (image instanceof BufferedImage)
        	bufferedImage = (BufferedImage)image;
        else
        {
	        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	        bufferedImage.getGraphics().drawImage((Image)image, 0 , 0, observer);
	        /* or?
        int[] pixels = image != null ? new int[width * height] : null;

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

        DataBufferInt db = new DataBufferInt(pixels, pixels.length);
        SampleModel sm = new SampleModel();
        WritableRaster wr = Raster.createWritableRaster(sm, db, null);
        BufferedImage bi = BufferedImage.

  //      * Create a BufferedImage using the WritableRaster	         
	         */
        }
        ByteArrayOutputStream baOut = new ByteArrayOutputStream();
        try {
			ImageIO.write(bufferedImage, DEFAULT_FORMAT, baOut);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

        portableImage.setHeight(height);
        portableImage.setWidth(width);
        portableImage.setFormat(DEFAULT_FORMAT);
        portableImage.setData(baOut.toByteArray());
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

    protected ImageObserver observer = new ImageAdapter();
    
    protected static class ImageAdapter implements ImageObserver
    {

        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y,
                int width, int height) {
            // Since I wait for an image to load, I know I have the image.
            return true;
        }
        
    }
}
