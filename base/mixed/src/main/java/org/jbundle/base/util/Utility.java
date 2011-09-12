package org.jbundle.base.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.BaseListener;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.ThinUtil;
import org.w3c.dom.Node;


/**
 * Static utility methods.
 */
public class Utility extends ThinUtil
{
    /**
     * SaveProductParam Method.
     */
    public static Map<String,Object> addFieldParam(Map<String,Object> map, BaseField field)
    {
        if (!field.isNull())
            map.put(field.getFieldName(), field.toString());
        return map;
    }
    /**
     * SaveProductParam Method.
     */
    public static String addFieldParam(String strCommand, BaseField field)
    {
        if (!field.isNull())
            strCommand = Utility.addURLParam(strCommand, field.getFieldName(), field.toString());
        return strCommand;
    }
    /**
     * RestoreProductParam Method.
     */
    public static void restoreFieldParam(PropertyOwner propertyOwner, BaseField field)
    {
        String strFieldName = field.getFieldName();
        if (propertyOwner.getProperty(strFieldName) != null)
            field.setString((String)propertyOwner.getProperty(strFieldName));
    }
    /**
     * Add to this URL using all the properties in this property list.
     * @param strURL The original URL string.
     * @param properties The properties to add (key/value pairs).
     * @return The new URL string.
     */
    public static String propertiesToURL(String strURL, Map<String,Object> properties)
    {
        if (properties != null)
        {
            for (String strKey : properties.keySet())
            {
                Object strValue = properties.get(strKey);
                strURL = Utility.addURLParam(strURL, strKey.toString(), strValue.toString());                
            }
        }
        return strURL;
    }
    /**
     * Create an argument list using all the properties in this property list.
     * @param properties The properties to convert.
     * @return A string array with entries of key=value.
     */
    public static String[] propertiesToArgs(Map<String,Object> properties)
    {
        if (properties == null)
            return null;
        String[] rgArgs = new String[properties.size()];
        int i = 0;
        for (String strKey : properties.keySet())
        {
            Object strValue = properties.get(strKey);
            rgArgs[i++] = strKey.toString() + '=' + strValue.toString();
        }
        return rgArgs;
    }
    /**
     * Replace every occurrence of this string with the new data.
     * @param string The original string.
     * @param strOldParam The string to find.
     * @param strNewData The data to replace the string with.
     * @return The new string.
     */
    public static String replace(String string, String strOldParam, String strNewData)
    {
        if (string == null)
            return null;
        StringBuilder sb = new StringBuilder(string);
        return Utility.replace(sb, strOldParam, strNewData).toString();
    }
    /**
     * Replace every occurrence of this string with the new data.
     * @param sb The original string.
     * @param strOldParam The string to find.
     * @param strNewData The data to replace the string with.
     * @return The new string.
     */
    public static StringBuilder replace(StringBuilder sb, String strOldParam, String strNewData)
    {
        int iIndex = 0;
        if (strNewData == null)
            strNewData = Constants.BLANK;
        while ((iIndex = sb.indexOf(strOldParam, iIndex)) != -1)
        {
            sb.replace(iIndex, iIndex + strOldParam.length(), strNewData);
            iIndex = iIndex + strNewData.length();
        }
        return sb;
    }
    /**
     * Replace every occurrence of the strings in the hashtable with the new data.
     * This method assumes that the hashtable contains the keys which must be replaced with the associated values.
     * NOTE: This method might be re-worked to search the string for the delimiter character rather than
     * calling replace for each param.
     * @param string The original string.
     * @param ht The table of old/new values.
     * @return The new string.
     */
    public static StringBuilder replace(StringBuilder sb, String[][] mxStrings)
    {
        for (int x = 0; x < mxStrings.length; x++)
        {
            if (mxStrings[x].length != 2)
                return sb;
            sb = Utility.replace(sb, mxStrings[x][0], mxStrings[x][1]);
        }
        return sb;
    }
    /**
     * Replace every occurrence of the strings in the map with the new data.
     * This method assumes that the map contains the keys which must be replaced with the associated values.
     * NOTE: This method might be re-worked to search the string for the delimiter character rather than
     * calling replace for each param.
     * @param string The original string.
     * @param ht The table of old/new values.
     * @return The new string.
     */
    public static String replace(String string, String[][] mxStrings)
    {
        return Utility.replace(new StringBuilder(string), mxStrings).toString();
    }
    /**
     * Replace every occurrence of the strings in the map with the new data.
     */
    public static StringBuilder replace(StringBuilder sb, Map<String,String> ht)
    {
        for (Object strKey : ht.keySet())
        {
            sb = Utility.replace(sb, (String)strKey, (String)ht.get(strKey));            
        }
        return sb;
    }
    /**
     * Replace every occurrence of the strings in the map with the new data.
     */
    public static String replace(String string, Map<String,String> ht)
    {
        return Utility.replace(new StringBuilder(string), ht).toString();
    }
    /**
     * Replace the {} resources in this string.
     * Typically either reg or map are non-null (the null one is ignored)
     * @param reg A resource bundle
     * @param map A map of key/values
     * @param strResource
     * @return
     */
    public static StringBuilder replaceResources(StringBuilder sb, ResourceBundle reg, Map<String, Object> map, PropertyOwner propertyOwner)
    {
    	boolean bDoubleBraces = false;
        int index = 0;
        while (index < sb.length())
        {
            int iStartBrace = sb.indexOf("{", index);
            if (iStartBrace == -1)
                break;
            if (iStartBrace + 1 < sb.length())
            	if (sb.charAt(iStartBrace + 1) == '{')
        	{
            	bDoubleBraces = true;
            	index = iStartBrace + 2;
            	continue;
        	}
            int iEndBrace = sb.indexOf("}", iStartBrace);
            if (iEndBrace == -1)
                break;
            String strKey = sb.substring(iStartBrace + 1, iEndBrace);
            if (iStartBrace > 0)
            	if (sb.charAt(iStartBrace - 1) == '$')
            		iStartBrace--;
            String string = null;
            if (map != null)
            	if (map.get(strKey) != null)
            		string = map.get(strKey).toString();
            if (reg != null)
                if (string == null)
                    string = reg.getString(strKey);
            if (propertyOwner != null)
                if (string == null)
                    string = propertyOwner.getProperty(strKey);
            if (string == null)
                string = strKey;    // Never
            sb.replace(iStartBrace, iEndBrace + 1, string);
            index = index + string.length();
        }
        if (bDoubleBraces)
        {
            index = 0;
            while (index < sb.length() - 1)
            {
            	index = sb.indexOf("{{", index);
            	if (index == -1)
            		break;
    			sb.replace(index, index + 2, "{");
            	index = sb.indexOf("}}", index);
            	if (index == -1)
            		break;
    			sb.replace(index, index + 2, "}");
            }
        }
        return sb;
    }
    /**
     * Replace the {} resources in this string.
     * @param reg
     * @param map A map of key/values
     * @param strResource
     * @return
     */
    public static String replaceResources(String string, ResourceBundle reg, Map<String, Object> map, PropertyOwner propertyOwner)
    {
        if (string != null)
            if (string.indexOf('{') == -1)
                return string;
        return Utility.replaceResources(new StringBuilder(string), reg, map, propertyOwner).toString();
    }
    /**
     * A utility method to get an UTF-8 Input stream from a string.
     * @param string The string to be converted to an InputStream.
     * @return The input stream.
     */
    public static InputStream getStringInputStream(String string)
    {
        InputStream is = null;
        try   {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(ba);
            os.writeUTF(string);
            os.flush();
            is = new ByteArrayInputStream(ba.toByteArray());
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
        return is;
    }
    /**
     * Transfer the data stream from this URL to a string or file.
     * @param strURL The URL to read.
     * @param strFilename If non-null, create this file and send the URL data here.
     * @param strFilename If null, return the stream as a string.
     * @return The stream as a string if filename is null.
     */
    public static String transferURLStream(String strURL, String strFilename)
    {
        return Utility.transferURLStream(strURL, strFilename, null);
    }
    /**
     * Transfer the data stream from this URL to a string or file.
     * @param strURL The URL to read.
     * @param strFilename If non-null, create this file and send the URL data here.
     * @param strFilename If null, return the stream as a string.
     * @param in If this is non-null, read from this input source.
     * @return The stream as a string if filename is null.
     */
    public static String transferURLStream(String strURL, String strFilename, Reader in)
    {
        return Utility.transferURLStream(strURL, strFilename, in, null);
    }
    /**
     * Transfer the data stream from this URL to a string or file.
     * @param strURL The URL to read.
     * @param strFilename If non-null, create this file and send the URL data here.
     * @param strFilename If null, return the stream as a string.
     * @param in If this is non-null, read from this input source.
     * @return The stream as a string if filename is null.
     */
    public static String transferURLStream(String strURL, String strFilename, Reader in, Writer out)
    {
        String strMessage = null;
        URL url = null;
        try {
            InputStream inStream = null;
            if (in == null)
            {
                url = new URL(strURL);
                inStream = url.openStream();
                in = new BufferedReader(new InputStreamReader(inStream));
            }
            Writer outTemp = out;
            if (outTemp == null)
            {
                if ((strFilename == null) || (strFilename.length() == 0))
                    outTemp = new StringWriter();
                else
                    outTemp = new FileWriter(strFilename);
            }
            Utility.transferStream(in, outTemp);
            if (out == null)
            {
                outTemp.flush();
                outTemp.close();
                if ((strFilename == null) || (strFilename.length() == 0))
                    strMessage = ((StringWriter)outTemp).getBuffer().toString();
            }
            in.close();
            if (inStream != null)
                inStream.close();
        } catch (MalformedURLException ex)  {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return strMessage;
    }
    /**
     * Change this string to a XML Text string.
     * @param string The tag to enclose as an xml tag.
     * @return The XML tag.
     */
    public static String startTag(String string)
    {
        return "<" + string + '>';
    }
    /**
     * Change this string to a XML end Text string.
     * @param string The tag to enclose as an xml tag.
     * @return The ending XML tag.
     */
    public static String endTag(String string)
    {
        if (string.indexOf(' ') != -1)
            string = string.substring(0, string.indexOf(' '));
        return "</" + string + '>';
    }
    /**
     * Change this string to a XML Text string.
     * This just replaces < with &lt; and > with &gt and & with &amp;.
     * @param string The string to fix so it is valid XML.
     * @return The new valid XML string.
     */
    public static String encodeXML(String string)
    {
        return Utility.replace(string, mxXML);
    }
    public static String[][] mxXML =
    {
        {"<", "&lt;"},
        {">", "&gt;"},
        {"&", "&amp;"}//,
//        {"\n", "<br/>"}
    };
    /**
     * Get this property from the map and convert it to the target class.
     * @param properties The map object to get the property from.
     * @param strKey The key of the property.
     * @param classData The target class to convert the property to.
     * @return The data in the correct class.
     */
    public static Object getAs(Map<String,Object> properties, String strKey, Class<?> classData)
    {
        return Utility.getAs(properties, strKey, classData, null);
    }
    /**
     * Get this property from the map and convert it to the target class.
     * @param properties The map object to get the property from.
     * @param strKey The key of the property.
     * @param classData The target class to convert the property to.
     * @param objDefault The default value.
     * @return The data in the correct class.
     */
    public static Object getAs(Map<String,Object> properties, String strKey, Class<?> classData, Object objDefault)
    {
        if (properties == null)
            return objDefault;
        Object objData = properties.get(strKey);
        try {
            return Converter.convertObjectToDatatype(objData, classData, objDefault);
        } catch (Exception ex) {
            return null;
        }
    }
    /**
     * Get this item from the map and convert it to the target class.
     * Convert this object to an formatted string.
     * @param map The map to pull the param from.
     * @param strKey The property key.
     * @param classData The java class to convert the data to.
     * @param objDefault The default value.
     * @return The propety value in the correct class.
     */
    public static String getAsFormattedString(Map<String,Object> map, String strKey, Class<?> classData, Object objDefault)
    {
        Object objData = map.get(strKey);
        try {
            return Converter.formatObjectToString(objData, classData, objDefault);
        } catch (Exception ex) {
            return null;
        }
    }
    /**
     * Convert this object to an formatted string.
     * @param obj In
     * @return String out.
     */
    public static String formatObjectToString(Object objData)
    {
        if (objData == null)
            return null;
        try {
            return Converter.formatObjectToString(objData, objData.getClass(), null);
        } catch (Exception ex) {
            return null;
        }
    }
    /**
     * Do a smart conversion of this object to an unfomatted string (ie., toString).
     * @param obj In
     * @return String out.
     */
    public static String convertObjectToString(Object objData)
    {
        if (objData == null)
            return null;
        try {
            return Utility.convertObjectToString(objData, objData.getClass(), null);
        } catch (Exception ex) {
            return null;
        }
    }
    /**
     * Convert this object to an unfomatted string (ie., toString).
     * @param properties The map object to get the property from.
     * @param strKey The key of the property.
     * @param classData The target class to convert the property to.
     * @param objDefault The default value.
     * @return The data in the correct class.
     */
    public static String convertObjectToString(Object objData, Class<?> classData, Object objDefault)
    {
        try {
            objData = Converter.convertObjectToDatatype(objData, classData, objDefault);
        } catch (Exception ex) {
            objData = null;
        }
        if (objData == null)
            return null;
        return objData.toString();
    }
    /**
     * Convert this properties object to a map.
     * @param properties
     * @return
     */
    static public Map<String,Object> propertiesToMap(Properties properties)
    {
        Map<String,Object> map = null;
        if (properties != null)
        {
	        map = new Hashtable<String,Object>();
	        for (Object key : properties.keySet())
	        {
                map.put((String)key, properties.get(key));	            
	        }
        }
        return map;
    }
    /**
     * Convert this properties object to a map.
     * @param properties
     * @return
     */
    static public Map<String,Object> propertiesToMap(Dictionary<?, ?> properties)
    {
        Map<String,Object> map = null;
        if (properties != null)
        {
	        map = new Hashtable<String,Object>();
	        Enumeration<?> keys = properties.keys();
	        while (keys.hasMoreElements())
	        {
	        	Object key = keys.nextElement();
                map.put((String)key, properties.get(key));	            
	        }
        }
        return map;
    }
    /**
     * Convert map to properties.
     * @param map
     * @return
     */
    public static Properties mapToProperties(Map<String,Object> map)
    {
        Properties properties = new Properties();
        Iterator<? extends Map.Entry<?, ?>> i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<?, ?> e = i.next();
            properties.setProperty((String)e.getKey(), (e.getValue() != null) ? e.getValue().toString() : DBConstants.BLANK);
        }
        return properties;
    }
    public static Map<String,Object> arrayToMap(String[][] data)
    {
        Map<String,Object> map = new Hashtable<String,Object>();
        if (data != null)
        {
            for (String[] row : data)
            {
                map.put(row[0], row[1]);
            }
        }
        return map;
    }
    /**
     * Same as putall, except if the property already exists in the dest map, don't move it.
     * TODO Convert this to generics.
     * @param dest
     * @param source
     * @return
     */
    public static Map<String, Object> putAllIfNew(Map<String, Object> dest, Map<String, Object> source)
    {
    	if (dest == null)
    		return source;
    	for (String key : source.keySet())
    	{
    		if (dest.get(key) == null)
    			dest.put(key, source.get(key));
    	}
    	return dest;
    }
    /**
     * Get the domain name from this URL.
     * @param strDomain
     * @return
     */
    public static String getDomainFromURL(String strURL, String strContextPathAtEnd)
    {
    	return Utility.getDomainFromURL(strURL, strContextPathAtEnd, false);
    }
    /**
     * Get the domain name from this URL.
     * @param strDomain
     * @return
     */
    public static String getDomainFromURL(String strURL, String strContextPathAtEnd, boolean includePortNumber)
    {
        String strDomain = strURL;
        
        if (strDomain.indexOf(':') < 8)
            strDomain = strDomain.substring(strDomain.indexOf(':') + 1);  // Get rid of protocol
        if (strDomain.indexOf("//") == 0)
            strDomain = strDomain.substring(2);  // Get rid of '//'
        if (strDomain.indexOf('?') != -1)
            strDomain = strDomain.substring(0, strDomain.indexOf('?'));   // Get rid of params
        int iEndDomain = strDomain.indexOf('/');
        if (iEndDomain == -1)
            iEndDomain = strDomain.length();
        if (strDomain.lastIndexOf(Constants.DEFAULT_SERVLET) >= iEndDomain)
            strDomain = strDomain.substring(0, strDomain.lastIndexOf(Constants.DEFAULT_SERVLET) - 1);   // Strip servlet name
        if ((strDomain.indexOf(':') != -1) && (includePortNumber == false))
            strDomain = strDomain.substring(0, strDomain.indexOf(':'));  // Get rid of port number
        else
            strDomain = strDomain.substring(0, iEndDomain);
        
        if (strContextPathAtEnd != null)
        {
            int iStartIndex = strURL.indexOf(strDomain);
            int iContextIndex = strURL.indexOf(strContextPathAtEnd, iStartIndex + strDomain.length());
            if (iContextIndex != -1)
            {   // Always
                iContextIndex = iContextIndex + strContextPathAtEnd.length();
                strDomain = strURL.substring(iStartIndex, iContextIndex);
                if (!strDomain.endsWith(System.getProperty("file.separator")))
                    strDomain = strDomain + System.getProperty("file.separator");
            }
        }
        return strDomain;
    }
    /**
     * Get a recordowner from this record.
     * This method does a deep search using the listeners and the database connections to find a recordowner.
     * @param record
     * @return
     */
    public static RecordOwner getRecordOwner(Record record)
    {
        RecordOwner recordOwner = record.getRecordOwner();
        if (recordOwner instanceof org.jbundle.base.db.shared.FakeRecordOwner)
            recordOwner = null; 
        BaseListener listener = record.getListener();
        while ((recordOwner == null) && (listener != null))
        {
            BaseListener listenerDep = listener.getDependentListener();
            if (listenerDep != null)
                if (listenerDep.getListenerOwner() instanceof RecordOwner)
                    recordOwner = (RecordOwner)listenerDep.getListenerOwner();
            listener = listener.getNextListener();
        }
        if (recordOwner == null)
            if (record.getTable() != null)
                if (record.getTable().getDatabase() != null)
                    if (record.getTable().getDatabase().getDatabaseOwner() instanceof Application)
                    {
                        Application app = (Application)record.getTable().getDatabase().getDatabaseOwner();
                        if (app.getSystemRecordOwner() == null) // This should be okay... get the system recordowner.
                            app = record.getTable().getDatabase().getDatabaseOwner().getEnvironment().getDefaultApplication();
                        if (app != null)
                            if (app.getSystemRecordOwner() instanceof RecordOwner)
                                recordOwner = (RecordOwner)app.getSystemRecordOwner();
                    }
        return recordOwner;
    }
    /**
     * Get a recordowner from this recordOwnerParent.
     * This method does a deep search using the listeners and the database connections to find a recordowner.
     * @param record
     * @return
     */
    public static RecordOwner getRecordOwner(RecordOwnerParent recordOwnerParent)
    {
        if (recordOwnerParent instanceof RecordOwner)
            return (RecordOwner)recordOwnerParent;  // Duh
        RecordOwner recordOwner = null;
        if (recordOwnerParent != null)
            if (recordOwnerParent.getTask() != null)
                if (recordOwnerParent.getTask().getApplication() != null)
                    recordOwner = (RecordOwner)recordOwnerParent.getTask().getApplication().getSystemRecordOwner();
        return recordOwner;
    }
    /**
     * Use XSLT to convert this source tree into a new tree.
     * @param result If this is specified, transform the message to this result (and return null).
     * @param source The source to convert.
     * @param streamTransformer The (optional) input stream that contains the XSLT document.
     * If you don't supply a streamTransformer, you should override getTransforerStream() method.
     * @return The new tree.
     */
    public static void transformMessage(String strXMLIn, String strXMLOut, String strTransformer)
    {
        try {
            Reader reader = new FileReader(strXMLIn);

            Writer stringWriter = new FileWriter(strXMLOut);
            
            Reader readerxsl = new FileReader(strTransformer);

            Utility.transformMessage(reader, stringWriter, readerxsl);
        } catch (IOException ex)    {
            ex.printStackTrace();
        }                    
    }
    /**
     * Use XSLT to convert this source tree into a new tree.
     * @param result If this is specified, transform the message to this result (and return null).
     * @param source The source to convert.
     * @param streamTransformer The (optional) input stream that contains the XSLT document.
     * If you don't supply a streamTransformer, you should override getTransforerStream() method.
     * @return The new tree.
     */
    public static void transformMessage(Reader reader, Writer stringWriter, Reader readerxsl)
    {
        try {
            StreamSource source = new StreamSource(reader);

            Result result = new StreamResult(stringWriter);

            TransformerFactory tFact = TransformerFactory.newInstance();
            StreamSource streamTransformer = new StreamSource(readerxsl);
            
            Transformer transformer = tFact.newTransformer(streamTransformer);

            transformer.transform(source, result);
        } catch (TransformerConfigurationException ex)    {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }                    
    }
    public static void main(String args[])
    {
        Utility.transformMessage(args[0], args[1], args[2]);
    }
    /**
     * Copy DOM tree to a SOAP tree.
     * @param tree
     * @param node
     * @return The parent of the new child node.
     */
    public static Node copyTreeToNode(Node tree, Node node)
    {
        DOMResult result = new DOMResult(node);
        if (Utility.copyTreeToResult(tree, result))
            return node.getLastChild();
        else
            return null;    // Failure
    }
    /**
     * Copy DOM tree to a SOAP tree.
     * @param tree
     * @param node
     * @return The parent of the new child node.
     */
    public static boolean copyTreeToResult(Node tree, Result result)
    {
        try {
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(tree);
            transformer.transform(source, result);
            return true;    // Success
        } catch (TransformerConfigurationException tce) {
            // Error generated by the parser
            tce.printStackTrace();
        } catch (TransformerException te) {
            // Error generated by the parser
            te.printStackTrace();
        }
        return false;   // Failure
    }
    /**
     * Grow this array to the size of count (plus a buffer).
     * @param rgobjEnable
     * @param iNewSize
     * @param iBufferSize Extra size to add to the array if it has to grow.
     * @return
     */
    public static Object[] growArray(Object[] rgobjEnable, int iNewSize, int iBufferSize)
    {
        if (iNewSize > rgobjEnable.length)
        {
            Object[] rgobjNew = new Object[iNewSize + iBufferSize];
            for (int i = 0; i < rgobjEnable.length; i++)
                rgobjNew[i] = rgobjEnable[i];
            for (int i = rgobjEnable.length; i < rgobjNew.length ;i++)
                rgobjNew[i] = null;
            rgobjEnable = rgobjNew;
        }
        return rgobjEnable;
    }
}
