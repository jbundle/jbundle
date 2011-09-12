/**
 * @(#)XMLScanListener.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.scan;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import java.io.*;
import org.jbundle.thin.base.screen.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.jbundle.app.program.manual.convert.*;
import java.net.*;

/**
 *  XMLScanListener - .
 */
public class XMLScanListener extends BaseScanListener
{
    protected Transformer m_transformer = null;
    public static final String XSL_CONVERT = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
    "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\"  >" +
    //" <xsl:template match=\"\">" +
    //"  <xsl:copy-of select=\".\" />" +
    //" </xsl:template>" +
    "<!-- This template copies the name and attributes and applies templates to child nodes -->" +
    "<xsl:template match=\"*\">" +
    "  <xsl:copy>" +
    "    <xsl:for-each select=\"@*\">" +
    "      <xsl:attribute name=\"{name()}\">" +
    "        <xsl:value-of select=\".\"/>" +
    "      </xsl:attribute>" +
    "    </xsl:for-each> " +
    "     <xsl:apply-templates />" +
    "  </xsl:copy>" +
    "</xsl:template>" +
    
    //"<xsl:template match=\"roomType\">" +
    //" <xsl:element name=\"ota:roomType\" namespace=\"http://www.ota.org/xyz\">" +
    //"  <xsl:apply-templates />" +
    //" </xsl:element>" +
    //"</xsl:template>" +
    
    "</xsl:stylesheet>";;
    /**
     * Default constructor.
     */
    public XMLScanListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XMLScanListener(RecordOwnerParent parent, String strSourcePrefix)
    {
        this();
        this.init(parent, strSourcePrefix);
    }
    /**
     * Init Method.
     */
    public void init(RecordOwnerParent parent, String strSourcePrefix)
    {
        super.init(parent, strSourcePrefix);
        
        try {
            StreamSource streamTransformer = null;
            TransformerFactory tFact = TransformerFactory.newInstance();
            if (strSourcePrefix == null)
                strSourcePrefix = this.getProperty(ConvertCode.CONVERTER_PATH);
            if (strSourcePrefix != null)
                streamTransformer = this.getTransformerStream(strSourcePrefix);
            if (streamTransformer == null)
                streamTransformer = this.getTransformerStream();
            m_transformer = tFact.newTransformer(streamTransformer);
         
        } catch (TransformerConfigurationException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Main Method.
     */
    public static void main(String[] args)
    {
        if ((args == null) || (args.length < 1))
            System.exit(0);
        String strDirIn = args[0];
        String strDirOut = null;
        if (args.length > 1)
            strDirOut = args[1];
        else
            strDirOut = strDirIn;
        String strConvert = null;
        if (args.length > 2)
            strConvert = args[2];
        Task taskParent = null;   // Fix this!
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(ConvertCode.DEST_DIR, strDirOut);
        properties.put(ConvertCode.SOURCE_DIR, strDirIn);
        ConvertCode convert = new ConvertCode(taskParent, null, properties);
        convert.setScanListener(new XMLScanListener(convert, strConvert));
        convert.run();
    }
    /**
     * GetTransformerStream Method.
     */
    public StreamSource getTransformerStream()
    {
        Reader reader = new StringReader(XSL_CONVERT);
        StreamSource stylesource = new StreamSource(reader);
        return stylesource;
    }
    /**
     * GetTransformerStream Method.
     */
    public StreamSource getTransformerStream(String strDocument)
    {
        StreamSource source = null;
        if (strDocument.indexOf(':') == -1)
        {   // See if it is a file name
            try {   // First try it as a filename
                FileReader reader = new FileReader(strDocument);
                if (reader != null)
                    source = new StreamSource(reader);
            } catch (IOException ex)    {
                source = null;
            }                    
        }
        if (source == null)
        {
            try {
                URL url = new URL(strDocument);
                InputStream is = url.openStream();
                source = new StreamSource(is);
            } catch (IOException ex)    {
                source = null;
            }
        }
        return source;
    }
    /**
     * Do whatever processing that needs to be done on this file.
     */
    public void moveThisFile(File fileSource, File fileDestDir, String strDestName)
    {
        try   {
            fileDestDir.mkdirs();
            FileInputStream fileIn = new FileInputStream(fileSource);
            InputStreamReader inStream = new InputStreamReader(fileIn);
            StreamSource source = new StreamSource(inStream);
        
            System.out.println(fileDestDir + " " + strDestName);
            File fileDest = new File(fileDestDir, strDestName);
            fileDest.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(fileDest);
            PrintWriter dataOut = new PrintWriter(fileOut);
            StreamResult dest = new StreamResult(dataOut);
        
            m_transformer.transform(source, dest);
            
            dataOut.close();
            fileOut.close();
        
            inStream.close();
            fileIn.close();
        } catch (TransformerException ex)  {
            ex.printStackTrace();
        } catch (FileNotFoundException ex)  {
            ex.printStackTrace();
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }

}
