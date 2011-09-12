/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report.parser;

/**
* @(#)XMLParser.java    0.00 12-Feb-97 Don Corley
*
* Copyright (c) 2009 tourapp.com. All Rights Reserved.
*   don@tourgeek.com
*/
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.util.Utility;

/**
 * This is the base for any data maintenance screen.
 * pend(don) This class needs to be updated to use a real XML parser.
 */
public class XMLParser extends Object
{
    protected RecordOwner m_screen = null;

    /**
     * Constructor.
     */
    public XMLParser()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XMLParser(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * init.
     */
    public void init(RecordOwner screen)
    {
        m_screen = screen;
    }
    /**
     * Free.
     */
    public void free()
    {
//      super.free();
    }
    /**
     * Output this screen using HTML.
     * Override this to print your XML.
     */
    public void printHtmlData(PrintWriter out, InputStream streamIn)
    {
        String str = null;
        if (streamIn != null)
            str = Utility.transferURLStream(null, null, new InputStreamReader(streamIn));
        if ((str == null) || (str.length() == 0))
            str = this.getDefaultXML();
        this.parseHtmlData(out, str);
    }
    /**
     * Find the data between these XML tags.
     */
    public String getDefaultXML()
    {
        return "You must override XML Parser for this class to be of use.";
    }
    /**
     * Find the data between these XML tags.
     * @param strData The XML code to find the tags in.
     * @param strTag The tag to find.
     */
    public String getTagData(String strData, String strTag)
    {
        int iStartData = strData.indexOf('<' + strTag + '>');
        if (iStartData == -1)
            return null;
        iStartData = iStartData + strTag.length() + 2;
        int iEndData = strData.indexOf("</" + strTag + '>');
        if (iStartData == -1)
            return null;
        return strData.substring(iStartData, iEndData);
    }
    /**
     * Output this screen using HTML.
     */
    public void parseHtmlData(PrintWriter out, String str)
    {
        int iCurrentParsePos = 0;
        int iCurrentOutputPos = 0;
        int iStartTag, iStartVelocity;
        while (true)
        { // Done
        	iStartTag = str.indexOf('<', iCurrentParsePos);
        	iStartVelocity = str.indexOf("${", iCurrentParsePos);
        	if ((iStartTag == -1) && (iStartVelocity == -1))
    			break;	// Done
        	String strTag, strParams, strData;
        	int iNextParam, iEndCloseTag;
    		if ((iStartVelocity == -1) || (iStartVelocity > iStartTag))
    		{	// xml format param
	            iCurrentParsePos = iStartTag + 1;
	            if (iCurrentParsePos == str.length())
	                break;
	            if (str.charAt(iStartTag + 1) == '/') // Don't process end tags
	                continue;
	            int iEndTag = str.indexOf(' ', iStartTag);
	            int iEndParam = str.indexOf('>', iStartTag);
	            if (iEndParam == -1)
	                break;      // poorly formed XML
	            int iNextVelocity = str.indexOf("${", iCurrentParsePos);
	            if (iNextVelocity != -1) if (iNextVelocity < iEndParam)
	                continue;   // This handles the case: <IMG=${image}>   (used internally)
	            iNextParam = str.indexOf('<', iCurrentParsePos);
	            if (iNextParam != -1) if (iNextParam < iEndParam)
	                continue;   // This handles the case: <IMG=<image/>>   (used internally)
	            if (iNextVelocity != -1) if (iNextVelocity < iNextParam)
	            	iNextParam = iNextVelocity;
	            iEndCloseTag = iEndParam;
	            if (str.charAt(iEndParam - 1) == '/')
	                iEndParam = iEndParam - 1;
	            if ((iEndTag == -1) || (iEndTag > iEndParam))
	                iEndTag = iEndParam;
	            strTag = str.substring(iStartTag + 1, iEndTag);
	            strParams = null;
	            if (iEndTag != iEndParam)
	                strParams = str.substring(iEndTag + 1, iEndParam);
	            strData = null;
	            int iStartCloseTag = -1;
	            
	            if (iCurrentOutputPos != iStartTag)
	                out.print(str.substring(iCurrentOutputPos, iStartTag));     // Print everything up to the start of the tag.
	            iCurrentOutputPos = iStartTag;
	            
	            if (str.charAt(iEndCloseTag) != '/')
	            {   // Find the end tag (if start tag doesn't end in '/')
	                iStartCloseTag = str.indexOf("</" + strTag + '>', iEndTag);
	                if (iStartCloseTag > iEndTag)
	                {
	                    iEndCloseTag = iStartCloseTag + strTag.length() + 2;
	                    strData = str.substring(iEndParam + 1, iStartCloseTag);
	                }
	            }
        	}
    		else
    		{	// Velocity format param
    			strParams = null;
    			strData = null;
    			
	            iCurrentParsePos = iStartTag + 2;	// ${
	            if (iCurrentParsePos == str.length())
	                break;
	            iEndCloseTag = str.indexOf('}', iStartTag);
	            if (iEndCloseTag == -1)
	                break;      // poorly formed Velocity tag
    			strTag = str.substring(iStartTag + 2, iEndCloseTag);

	            int iNextVelocity = str.indexOf("${", iCurrentParsePos);
	            iNextParam = str.indexOf('<', iCurrentParsePos);
	            if (iNextVelocity != -1) if (iNextVelocity < iNextParam)
	            	iNextParam = iNextVelocity;
    		}

            // Now, I have the tag, params, and data for this tag
            boolean bProcessed = this.parseHtmlTag(out, strTag, strParams, strData);
            
            if (!bProcessed)
            {
                out.print(str.substring(iCurrentOutputPos, iNextParam));     // It wasn't processed, so output it.
                iCurrentOutputPos = iNextParam;
            }
            else
                iCurrentOutputPos = iEndCloseTag + 1;
            iCurrentParsePos = iCurrentOutputPos;
        }
        iStartTag = str.length();
        if (iCurrentOutputPos != iStartTag)
            out.println(str.substring(iCurrentOutputPos, iStartTag));
    }
    /**
     * Process this XML Tag.
     */
    public boolean parseHtmlTag(PrintWriter out, String strTag, String strParams, String strData)
    {
        return false;   // Tag not found
    }
    /**
     * Get the screen passed in with the constructor.
     */
    public RecordOwner getRecordOwner()
    {
        return m_screen;
    }
    /**
     * Get this property.
     * First, see if it's in the param tag, then work your way up the hierarchy.
     */
    public String getProperty(String strProperty, String strParams)
    {
        String strValue = null;
        strValue = this.parseArg(strProperty, strParams);
        if (strValue == null)
            strValue = this.getRecordOwner().getProperty(strProperty);
        return strValue;
    }
    /**
     * Parse this URL formatted string into properties.
     */
    public String parseArg(String strProperty, String strURL)
    {
        if ((strURL == null) || (strProperty == null))
            return null;
        int iStartIndex = strURL.toUpperCase().indexOf(strProperty.toUpperCase() + '=');    // Start of first param (0 if no ?)
        if (iStartIndex == -1)
            return null;
        iStartIndex = iStartIndex + strProperty.length() + 1;
        if (iStartIndex < strURL.length())
            if (strURL.charAt(iStartIndex) == '\"')
                iStartIndex++;
        int iEndIndex = strURL.indexOf(' ', iStartIndex);
        if (iEndIndex == -1)
            iEndIndex = strURL.length();
        if (strURL.charAt(iEndIndex - 1) == '\"')
            iEndIndex--;
        return strURL.substring(iStartIndex, iEndIndex);
    }
}
