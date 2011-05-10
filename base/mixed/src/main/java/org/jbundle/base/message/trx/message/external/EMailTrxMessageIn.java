/*
 * BaseTrxMessage.java
 *
 * Created on October 12, 2001, 3:32 AM
 */

package org.jbundle.base.message.trx.message.external;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.message.BaseMessage;


/**
 * This is the base class for a transaction which is sent externally.
 * The two main sub-classes of this class are InternalTrxMessage and ExternalTrxMessage.
 * An InternalTrxMessage is the data I create internally to send to the destination. It
 * usually contains all the relative information needed to send to the destination.
 * An ExternalTrxMessage is the message converted to a format that the receiver can
 * understand (such as ebXML).
 * @author  don
 * @version 
 */
public class EMailTrxMessageIn extends ExternalTrxMessageIn
{
	private static final long serialVersionUID = 1L;

	/**
     * Fix this to call system.get(NEW_LINE).
     */
    public static final char JAVA_NEW_LINE = '\n';
    /**
     * End of key character.
     */
    public static final char END_OF_KEY_CHAR = ':';

    /**
      * Creates new BaseTrxMessage
     */
    public EMailTrxMessageIn() 
    {
        super();
    }
    /**
      * Initialize new BaseTrxMessage.
     * This is used for outgoing EC transactions where you have the jaxb message and you need to convert it.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public EMailTrxMessageIn(BaseMessage message, String strMessage)
    {
        this();
        this.init(message, strMessage);
    }
    /**
      * Initialize new BaseTrxMessage.
     * @param objRawMessage The (optional) raw data of the message.
     */
    public void init(BaseMessage message, Object strMessage)
    {
        super.init(message, strMessage);
    }
    /**
     * Convert the external form to the internal message form.
     * You must override this method.
     * @param externalMessage The received message to be converted to internal form.
     * @return The internal message.
     */
    public int convertExternalToInternal(Object recordOwner)
    {
        String strMessage = (String)this.getRawData();
        strMessage = this.stripHtmlTags((String)strMessage);
        Map<String,Object> map = this.getEMailParams(strMessage);
        this.moveHeaderParams(map, ((TrxMessageHeader)this.getMessage().getMessageHeader()).getMessageHeaderMap());
        
        String rootTag = BaseMessage.ROOT_TAG;
        if (this.getMessage() != null)
        	if (this.getMessage().getMessageDataDesc(null) != null)
        		if (this.getMessage().getMessageDataDesc(null).getKey() != null)
        			rootTag = this.getMessage().getMessageDataDesc(null).getKey();
        StringBuffer sb = new StringBuffer();
        Util.addStartTag(sb, rootTag);
        Util.addXMLMap(sb, map);
        Util.addEndTag(sb, rootTag);
        String strXML = sb.toString();
        if (this.getXSLTDocument() != null)
            strXML = this.transformMessage(strXML, null);    // Now use the XSLT document to convert this XSL document.
        boolean bSuccess = this.getMessage().setXML(strXML);
        return bSuccess ? DBConstants.NORMAL_RETURN : DBConstants.ERROR_RETURN;  
    }
    /**
     * Remove the spaces from this string and change the leading char to lowercase.
     * (To turn this description into a param).
     * @param string The source string.
     * @return The param string.
     */
    public String stripHtmlTags(String string)
    {
        for (int i = 1; i < string.length(); i++)
        {
            if (string.charAt(i) == '<')
            {
                int iEndTag = string.indexOf('>', i);
                if (iEndTag != -1)
                {
                    string = string.substring(0, i) + JAVA_NEW_LINE + string.substring(iEndTag + 1);
                    i--;
                }
            }
        }
        return string;
    }
    /**
     * Is this the first character of the property?
     */
    public boolean isLeadingWhitespace(char chChar)
    {
        if (Character.isWhitespace(chChar))
            return true;
        if (chChar == '>')
            return true;
        return false;
    }
    /**
     * Is this the ending character of the property?
     */
    public boolean isEndChar(char chChar)
    {
        if ((chChar == '\n')
            || (chChar == '\r')
            || (chChar == JAVA_NEW_LINE))
                return true;
        if (chChar == '<')
            return true;
        return false;
    }
    /**
     * Scan through this message and get the params to descriptions map.
     * @param externalMessage The received message to be converted to internal form.
     * @return The map of params to descriptions.
     */
    public Map<String,Object> getEMailParams(String strMessage)
    {
        Map<String,Object> properties = new Hashtable<String,Object>();
        int iIndex = this.getStartNextPropertyKey(-1, strMessage);
        while (iIndex != -1)
        {
            int iIndexEnd = strMessage.indexOf(END_OF_KEY_CHAR, iIndex);
            if (iIndexEnd == -1)
                break;
            String strKey = strMessage.substring(iIndex, iIndexEnd);
            if (strKey.indexOf(' ') != -1)
                strKey = strKey.substring(strKey.lastIndexOf(' ') + 1);
            iIndex = this.getStartNextPropertyKey(iIndexEnd + 1, strMessage);
            if (iIndex >= iIndexEnd)
            {
                for (iIndexEnd = iIndexEnd + 1; iIndexEnd < iIndex; iIndexEnd++)
                {
                    char chChar = strMessage.charAt(iIndexEnd);
                    if (!this.isLeadingWhitespace(chChar))
                        break;  // Start of value
                }
                if (iIndex == -1)
                    break;
                for (int i = iIndex - 1; i >= iIndexEnd; i--)
                {
                    char chChar = strMessage.charAt(i);
                    if (!this.isLeadingWhitespace(chChar))
                    {
                        String strValue = strMessage.substring(iIndexEnd, i + 1);
                        this.putProperty(properties, strKey, strValue);
                        break;
                    }
                }
            }
        }
        return properties;
    }
    public void putProperty(Map properties, String strKey, String strValue)
    {
        if (strKey.indexOf('/') == -1)
            properties.put(strKey, strValue);
        else
        {   // Multi-level map
            String strKey2 = strKey.substring(strKey.indexOf('/') + 1);
            strKey = strKey.substring(0, strKey.indexOf('/'));
            Map<String,Object> properties2 = null;
            if (properties.get(strKey) == null)
            {
                properties2 = new Hashtable<String,Object>();
                properties.put(strKey, properties2);
            }
            else if (properties.get(strKey) instanceof Map)
                properties2 = (Map)properties.get(strKey);
            else
            {   // Error - already not a map
                return;
            }
            this.putProperty(properties2, strKey2, strValue);
        }
    }
   /**
     * Get the next property key after this location.
     * @param iIndex start at this location +1.
     * @param strMessage The message to search through.
     * @return The position of this property key (or -1 if EOF).
     */
    public int getStartNextPropertyKey(int iIndex, String strMessage)
    {
        for (iIndex = iIndex + 1; iIndex < strMessage.length(); iIndex++)
        {
            char chChar = strMessage.charAt(iIndex);
            if (this.isEndChar(chChar))
                break;
        }
        if (iIndex >= strMessage.length())
            return -1;
        int iNextProperty = strMessage.indexOf(END_OF_KEY_CHAR, iIndex);
        if (iNextProperty == -1)
            return -1;  // Not found
        for (int i = iNextProperty - 1; i >= 0; i--)
        {
            char chChar = strMessage.charAt(i);
            if (this.isEndChar(chChar))
            {   // Key always starts on a new line.
                for (int k = i + 1; k < strMessage.length(); k++)
                {   // Now go forward to the first character.
                    chChar = strMessage.charAt(k);
                    if (!this.isLeadingWhitespace(chChar))
                    {
                        return k;
                    }
                }
            }
        }
        return -1;
    }
}
