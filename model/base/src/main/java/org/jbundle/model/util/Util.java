/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.model.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jbundle.model.App;
import org.jbundle.util.osgi.webstart.util.UrlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Basic utilities.
 */
public class Util extends UrlUtil
{	
    /**
     * Add this param to this path.
     * @param basePath The original URL to add this param to.
     * @param fileName The parameter to add.
     * @return The new URL string.
     */
    public static String addToPath(String basePath, String fileName)
    {
        return Util.addToPath(basePath, fileName, '\0');
    }
    /**
     * Add this param to this path.
     * @param basePath The original URL to add this param to.
     * @param fileName The parameter to add.
     * @param chFileSeparator Add an empty param if the data is null?
     * @return The new URL string.
     */
    public static String addToPath(String basePath, String fileName, char chFileSeparator)
    {
        if (chFileSeparator == '\0')
        {
            String strFileSeparator = System.getProperty("file.separator");
            chFileSeparator = '/';
            if ((strFileSeparator != null) && (strFileSeparator.length() == 1))
                chFileSeparator = strFileSeparator.charAt(0);
        }
        StringBuilder sb = new StringBuilder();
        if (basePath != null)
            sb.append(basePath);
        if (sb.length() > 0)
            if (sb.charAt(sb.length() - 1) != chFileSeparator)
                sb.append(chFileSeparator);
        if (fileName != null)
        {
            if ((fileName.length() > 0) && (fileName.charAt(0) == chFileSeparator) && (sb.length() > 0))
                sb.append(fileName.substring(1));
            else
                sb.append(fileName);
        }
        return sb.toString();
    }
    /**
     * Change this to a straight path name without /../ references (Required for 'create directory').
     * @return The new path string.
     */
    public static String normalizePath(String path)
    {
        StringBuilder sb = new StringBuilder(path);
        int lastToken = -1;
        for (int i = 0; i < sb.length(); i++)
        {
            if ((sb.charAt(i) == File.separatorChar)
                || (sb.charAt(i) == '/'))
            {
                if (lastToken != -1)
                {
                    if (i < sb.length() - 2)
                        if (sb.charAt(i + 1) == '.')
                            if (sb.charAt(i + 2) == '.')
                    {   // Remove last directory name from path
                        sb.delete(lastToken, i + 3);
                        i = lastToken;
                    }
                }
                lastToken = i;
            }
        }
        return sb.toString();
    }
    /**
     * Is this string a valid number?
     * @param string The string to check.
     * @return true if this string is a valid number (all characters numeric).
     */
    public static boolean isNumeric(String string)
    {
    	return Util.isNumeric(string, false);
    }
    /**
     * Is this string a valid number?
     * @param string The string to check.
     * @return true if this string is a valid number (all characters numeric).
     */
    public static boolean isNumeric(String string, boolean bAllowFormatting)
    {
        if ((string == null) || (string.length() == 0))
            return false;
        boolean bIsNumeric = true;
        for (int i = 0; i < string.length(); i++)
        {
            if (!Character.isDigit(string.charAt(i)))
            {
            	if ((!bAllowFormatting)
            		|| (Character.isLetter(string.charAt(i))))
            			bIsNumeric = false;
            }
        }
        return bIsNumeric;
    }
    /**
     * Set the message data as a XML String.
     * @return
     */
    public static Document convertXMLToDOM(String strXML)
    {
        DocumentBuilder db = Util.getDocumentBuilder();
        // Parse the input file
        Document doc = null;
        try {
            synchronized (db)
            {   // db is not thread safe
                doc = db.parse(new InputSource(new StringReader(strXML)));
            }
        } catch (SAXException se) {
            System.out.println(se.getMessage());
            return null;
        } catch (IOException ioe) {
            System.out.println(ioe);
            return null;
        } catch (Exception ioe) {
            System.out.println(ioe);
            return null;
        }
        return doc;
    }
    /**
     * Send this document as text to this output stream.
     * @param doc
     * @return The converted XML
     */
    public static String convertDOMToXML(Node doc)
    {
        StringWriter out = new StringWriter();
        Util.convertDOMToXML(doc, out);
        return out.toString();
    }
    /**
     * Send this document as text to this output stream.
     * @param doc
     */
    public static void convertDOMToXML(Node doc, Writer out)
    {
        try {
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
        } catch (TransformerConfigurationException tce) {
            // Error generated by the parser
            tce.printStackTrace();
        } catch (TransformerException te) {
            // Error generated by the parser
            te.printStackTrace();
        }
    }
    /**
     * Get the document builder.
     * The document builder is the parsing XML engine.
     * NOTE: Since the document builder can't be considered thread safe, you MUST synchronize on db before doing any document commands
     * @return The document builder.
     */
    private static final ThreadLocal < DocumentBuilder > m_dbs = 
        new ThreadLocal < DocumentBuilder > () {
            @Override protected DocumentBuilder initialValue() {

            DocumentBuilderFactory dbf = null;
            try {
                dbf = DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", this.getClass().getClassLoader());
            } catch (Exception e) {
                dbf = null;
            }
            if (dbf == null)
                dbf = DocumentBuilderFactory.newInstance();
            // Optional: set various configuration options
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            dbf.setIgnoringElementContentWhitespace(false);
            
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            // Set an ErrorHandler before parsing
            db.setErrorHandler(new MyErrorHandler(null));

            return db;
        }
    };

    public static DocumentBuilder getDocumentBuilder()
    {
        return m_dbs.get();
    }
    /**
     * MyErrorHandler receives error notifications from the DocumentBuilder.
     */
    static class MyErrorHandler extends Object
        implements ErrorHandler
    {
        /**
         * Constructor.
         */
        public MyErrorHandler(Object obj)
        {
        }
        /**
          * Receive notification of a recoverable error.
          */
        public void error(SAXParseException exception)
        {
            System.out.println(exception.getMessage());
        }
        /**
          * Receive notification of a non-recoverable error.
          */
        public void fatalError(SAXParseException exception)
        {
            System.out.println(exception.getMessage());
        }
        /**
          * Receive notification of a recoverable warning.
          */
        public void warning(SAXParseException exception)
        {
            System.out.println(exception.getMessage());
        }
    }
    public static StringBuffer addStartTag(StringBuffer sbXML, String strTag)
    {
        return sbXML.append('<').append(strTag).append('>');
    }
    public static StringBuffer addEndTag(StringBuffer sbXML, String strTag)
    {
        return sbXML.append("</").append(strTag).append('>');
    }
   /**
    * Add this map as an xml string.
    */
   public static StringBuffer addXMLMap(StringBuffer sbXML, Map<String,Object> mapMessage)
   {
       if (sbXML == null)
           sbXML = new StringBuffer();
       if (mapMessage != null)
       {
           for (String strParam : mapMessage.keySet())
           {
               Object objValue = mapMessage.get(strParam);
               if (objValue != null)
                   Util.getXML(sbXML, strParam, objValue);
           }
       }
       return sbXML;
   }
   /**
    * Convert this value to a XML string.
    * @param strParam This value's tag.
    * @param objValue The raw data value.
    * @return The XML string for this value.
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static StringBuffer getXML(StringBuffer sbXML, String strParam, Object objValue)
   {
       Util.addStartTag(sbXML, strParam);
       if (objValue instanceof Map)
       {
           Util.addXMLMap(sbXML, (Map)objValue);
       }
       else
       {
           String strValue = Constant.BLANK;
           if (objValue != null)
               strValue = objValue.toString();
           if (Util.isCData(strValue))
               strValue = CDATA_START + strValue + CDATA_END;
           sbXML.append(strValue);
       }
       return Util.addEndTag(sbXML, strParam).append(Constant.RETURN);
   }
   public static final String CDATA_START = "![CDATA[";
   public static final String CDATA_END = "]]";
   /**
    * Is this a CData string?
    * @param string The string to analyze.
    * @return true if this string must be a CDATA area (if it has carriage-returns).
    */
   public static boolean isCData(String string)
   {
       if (string != null)
       {
           for (int i = 0; i < string.length(); i++)
           {
               char chChar = string.charAt(i);
               if ((chChar == 0x9)
                   || (chChar == 0xD)
                   || (chChar == 0xA))
                       return true;        // Carriage return/Line feed/newline
           }
       }
       return false;
   }
   /**
    * Fix this string so it can be a DOM element name.
    */
   public static String fixDOMElementName(String string)
   {
       for (int i = 0; i < string.length(); i++)
       {
           if (!Character.isJavaIdentifierPart(string.charAt(i)))
               string = string.substring(0, i) + "." + string.substring(i + 1);
       }
       return string;
   }

   /**
    * A utility method to get an Input stream from a filename or URL string.
    * @param strFilename The filename or url to open as an Input Stream.
    * @return The imput stream (or null if there was an error).
    */
   public static InputStream getInputStream(String strFilename, App app)
   {
       InputStream streamIn = null;
       if ((strFilename != null) && (strFilename.length() > 0))
       {
           try   {
               URL url = null;
               if (strFilename.indexOf(':') != -1)
                   url = new URL(strFilename);
               if (url == null)
                   if (app != null)
                       url = app.getResourceURL(strFilename, null);
               if (url != null)
                   streamIn = url.openStream();
           } catch (Exception ex)  {
               streamIn = null;
           }
           if (streamIn == null)
           {
               File file = new File(strFilename);
               try   {
                   streamIn = new FileInputStream(file);
               } catch (FileNotFoundException ex)  {
                   streamIn = null;
               }
           }
       }
       return streamIn;
   }
   /**
    * Transfer the data stream from in stream to out stream.
    * Note: This does not close the out stream.
    * @param in Stream in
    * @param out Stream out
    */
   public static void transferStream(InputStream in, OutputStream out)
   {
       try {
    	   byte[] cbuf = new byte[1000];
           int iLen = 0;
           while ((iLen = in.read(cbuf, 0, cbuf.length)) > 0)
           {   // Write the entire file to the output buffer
               out.write(cbuf, 0, iLen);
           }
           in.close();
       } catch (MalformedURLException ex)  {
           ex.printStackTrace();
       } catch (IOException ex) {
           ex.printStackTrace();
       }
   }
   /**
    * Transfer the data stream from in stream to out stream.
    * Note: This does not close the out stream.
    * @param in Stream in
    * @param out Stream out
    */
   public static void transferStream(Reader in, Writer out)
   {
       try {
           char[] cbuf = new char[1000];
           int iLen = 0;
           while ((iLen = in.read(cbuf, 0, cbuf.length)) > 0)
           {   // Write the entire file to the output buffer
               out.write(cbuf, 0, iLen);
           }
           in.close();
       } catch (MalformedURLException ex)  {
           ex.printStackTrace();
       } catch (IOException ex) {
           ex.printStackTrace();
       }
   }
   /**
    * Convert this class name by inserting this package after the domain.
    * ie., com.xyz.abc.ClassName -> com.xyz.newpackage.abc.ClassName.
    * @param className
    * @param stringToInsert
    * @return Converted string
    */
   public static String convertClassName(String className, String stringToInsert)
   {
       return Util.convertClassName(className, stringToInsert, 2);
   }
   /**
    * Convert this class name by inserting this package after the domain.
    * ie., if location = 2, com.xyz.abc.ClassName -> com.xyz.newpackage.abc.ClassName.
    * @param className
    * @param location location (positive = from left, negative = from right; 0 = before package, -1 = before class name, etc.)
    * @param stringToInsert
    * @return Converted string
    */
   public static String convertClassName(String className, String stringToInsert, int location)
   {
       int startSeq = 0;
       if (location >= 0)
       {
           for (int i = 0; i < location; i++)
           {
               startSeq = className.indexOf('.', startSeq + 1);
           }
       }
       else
       {
           startSeq = className.length();
           for (int i = location; i < 0; i++)
           {
               startSeq = className.lastIndexOf('.', startSeq - 1);
           }
       }
       if (startSeq == -1)
           return null;
       int domainSeq = startSeq;
       if (className.indexOf(Constant.THIN_SUBPACKAGE, startSeq) == startSeq + 1)
           startSeq = startSeq + Constant.THIN_SUBPACKAGE.length();
       className = className.substring(0, domainSeq + 1) + stringToInsert + className.substring(startSeq + 1);
       return className;
   }
   /**
    * Get the domain name from this package or class name
    * @param className
    * @return
    */
   public static String getDomainName(String className)
   {
	   int i = className.indexOf('.');
	   i = className.indexOf('.', i + 1);
	   if (i == -1)
		   return null;
	   return className.substring(0, i);
   }
   /**
    * Get the package name of this class name
    * @param className
    * @return
    */
   public static String getPackageName(String className)
   {
	   return Util.getPackageName(className, false);
   }
   /**
    * If class name starts with '.' append base package.
    * Note: This code is the same exact code as osgi ClassService. I just can't get to there
    * from here.
    */
   public static String getFullClassName(String domainName, String packageName, String className) {
       if (packageName != null)
           if (packageName.length() > 0) {
               if (packageName.charAt(packageName.length() - 1) != '.')
                   packageName = packageName + '.';
           }
       if (className != null)
           if (className.length() > 0) {
               if (className.indexOf('.') == -1)
                   if (packageName != null)
                       className = packageName + className;
               if (className.charAt(0) == '.')
               {
               	if (domainName != null)
               	{
               		if (domainName.endsWith("."))
               			className = domainName + className.substring(1);
               		else
               			className = domainName + className;
               	}
               	else
                       className = Constant.ROOT_PACKAGE + className.substring(1);
               }
           }
       return className;
   }	
   /**
    * Get the package name of this class name
    * @param className
    * @return
    */
   public static String getPackageName(String className, boolean resource)
   {
       String packageName = null;
       if (className != null)
       {
       	if (resource)
       		if (className.endsWith(PROPERTIES))
       			className = className.substring(0, className.length() - PROPERTIES.length());
           if (className.lastIndexOf('.') != -1)
               packageName = className.substring(0, className.lastIndexOf('.'));
       }
       return packageName;
   }
   public static final String PROPERTIES = ".properties";
   /**
    * Get this image's full filename.
    * @param filename The filename of this image (if no path, assumes images/buttons; if not ext assumes .gif).
    * @param strSubDirectory The sub-directory.
    * @return The full (relative) filename for this image.
    */
   public static String getImageFilename(String filename, String strSubDirectory)
   {
       if ((filename == null) || (filename.length() == 0))
           return null;    // A null will tell a JButton not to load an image
       if (filename.indexOf('.') == -1)
           filename += ".gif";
       filename = Util.getFullFilename(filename, strSubDirectory, Constant.IMAGE_LOCATION, true);
       return filename;
   }
   /**
    * Get this image's full filename.
 * @param strSubDirectory The sub-directory.
 * @param fixRelativePaths Fix the relative paths
 * @param filename The filename of this image (if no path, assumes images/buttons; if not ext assumes .gif).
    * @return The full (relative) filename for this image.
    */
   public static String getFullFilename(String filename, String strSubDirectory, String fileLocation, boolean fixRelativePaths)
   {
	   String localLocation = fileLocation.substring(fileLocation.lastIndexOf('/') + 1);
       if (((filename.indexOf(File.separator) == -1) && (filename.indexOf('/') == -1)) && (strSubDirectory != null))
           filename = fileLocation + File.separator + strSubDirectory + File.separator + filename;
       else if ((filename.indexOf(localLocation + File.separator) == -1) && (filename.indexOf(localLocation + "/") == -1))
           filename = fileLocation + File.separator + filename;
       else if ((filename.indexOf(localLocation + File.separator) == 0) || (filename.indexOf(localLocation + "/") == 0))
           filename = fileLocation + filename.substring(localLocation.length());
	   if (fixRelativePaths)
	   {	// Remove '/../'
		   while (filename.indexOf("/..") != -1)
		   {
			   int startPath = filename.indexOf("/..");
			   int startPrev = filename.lastIndexOf("/", startPath - 1);
			   if (startPrev == -1)
				   break;
			   filename = filename.substring(0, startPrev) + filename.substring(startPath + 3);
		   }
	   }
       return filename;
   }
   /**
    * Get the logger.
    * @return The logger.
    */
   public static Logger getLogger()
   {
       if (m_logger == null)
       {
           m_logger = Logger.getLogger(Constant.ROOT_PACKAGE.substring(0, Constant.ROOT_PACKAGE.length() - 1));
           try {
               LogManager logManager = LogManager.getLogManager();
               logManager.reset();//removeAllGlobalHandlers();
               ConsoleHandler fh = new ConsoleHandler();
               fh.setFormatter(new SimpleFormatter());
               // Send logger output to our FileHandler.
               m_logger.addHandler(fh);
           } catch (java.security.AccessControlException ex)    {
               // Access error if running in an applet.
        	   m_logger = Logger.getAnonymousLogger();
           } finally {
               try {
	               // Request that every detail gets logged.
//                 m_logger.setLevel(Level.OFF);
//                   m_logger.setLevel(Level.ALL);
	               m_logger.setLevel(Level.WARNING);        	   
               } catch (java.security.AccessControlException ex)    {
            	   ex.printStackTrace();	// Never
               }
           }
       }
       return m_logger;
   }
   /**
    * 
    */
   public static void setLogger(Logger logger)
   {
       m_logger = logger;
//     m_logger.setLevel(Level.ALL);
       if (m_logger != null)
    	   m_logger.setLevel(Level.WARNING);
   }
   private static Logger m_logger = null;
   
   protected static PortableImageUtil portableImageUtil = null;
   public static PortableImageUtil getPortableImageUtil()
   {
       return portableImageUtil;
   }
   public static void setPortableImageUtil(PortableImageUtil portableImageUtil)
   {
       Util.portableImageUtil = portableImageUtil;
   }
   /**
    * Is this a valid url?
    * @param url
    * @return
    */
   public static boolean isURL(String url)
   {
       if (url == null)
           return false;
       return url.matches(VALID_URL_REGEX);
   }
   public static final String VALID_URL_REGEX = "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[\\w]*))?)";
}
