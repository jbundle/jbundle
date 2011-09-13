/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.data;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ImageField;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.html.DataAccessScreen;
import org.jbundle.thin.base.screen.util.SerializableImage;
import java.awt.Image;

/**
 * DObjectAccessScreen converts image data to JPG and sends it down the pipe.
 */
public class DObjectAccessScreen extends DDataAccessScreen
{
    public static final String JPEG_TYPE = "jpg";   // This must be in the jdk?
    public static final String GIF_TYPE = "gif";   // This must be in the jdk?
    public static final String JPEG_MIME_TYPE = "image/jpeg";   // This must be in the jdk?
    public static final String GIF_MIME_TYPE = "image/gif";   // This must be in the jdk?

    /**
     * Constructor.
     */
    public DObjectAccessScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The DataAccessScreen model.
     * @param bEditableControl If true, this view is editable.
     */
    public DObjectAccessScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The DataAccessScreen model.
     * @param bEditableControl If true, this view is editable.
     */
    public void init(ScreenField model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Process an HTML get or post.
     * @param req The servlet request.
     * @param res The servlet response object.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void sendData(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        String dataType = GIF_TYPE;
        res.setContentType(GIF_TYPE.equalsIgnoreCase(dataType) ? "image/gif" : "image/jpeg");
        OutputStream out = res.getOutputStream();
        String strParamField = this.getProperty("field");   // Display record
        ImageIcon imageIcon = null;
        Image image = null;
        int scaledW = 0;
        int scaledH = 0;
        if (strParamField != null)
        {
            Record record = ((DataAccessScreen)this.getScreenField()).getMainRecord();
            if (record != null)
            {
                BaseField field = record.getField(strParamField);
                if (field != null) if (!field.isNull())
                {
                    if (field instanceof ImageField)
                    {
                        if (field.getData() instanceof ImageIcon)
                            imageIcon = (ImageIcon)field.getData();
                        else if (field.getData() instanceof SerializableImage)
                        {
                            image = ((SerializableImage)field.getData()).getImage();
                            scaledW = ((SerializableImage)field.getData()).getImageWidth();
                            scaledH = ((SerializableImage)field.getData()).getImageHeight();
                        }
                    }
                }                
            }
        }
        if ((imageIcon == null) && (image == null))
            imageIcon = new ImageIcon("images/icons/Noimage.gif", "Next button");

        if (imageIcon != null)
        {
            scaledW = imageIcon.getIconWidth();
            scaledH = imageIcon.getIconHeight();
            image = imageIcon.getImage();
        }

        BufferedImage outImage = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_INT_RGB);
        // Paint image.
        Graphics2D g2d = outImage.createGraphics();
        g2d.drawImage(image, null, null);
        g2d.dispose();

        ImageIO.write(outImage, dataType, out);
/** This is the code (that works fine) when using the jai image encoder
        // This is the code to use with com.sun encoder:
        // JPEG-encode the image and write to file.
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);

        encoder.encode(outImage);
/*
/** This is the code (that works fine) when using the JAI toolkit
        ParameterBlock pb = new ParameterBlock();
        pb.add(image.getImage());
        
        // Create the AWTImage operation.
        PlanarImage im = (PlanarImage)JAI.create("awtImage", pb); // Store the image in the JPEG format.
        JPEGEncodeParam encodeParam = new JPEGEncodeParam();
        float fQuality = 1.0f;
        String strDestination = "test.gif";
        encodeParam.setQuality(fQuality);   // Medium quality
        JAI.create("encode", im, out, "JPEG", encodeParam);
*/
    }
}
