/**
 * @(#)SaxHtmlHandler.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.data.importfix.base;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.xml.sax.helpers.*;
import org.xml.sax.*;

/**
 *  SaxHtmlHandler - .
 */
public class SaxHtmlHandler extends DefaultHandler
{
    public static final String TABLE = "table";
    public static final String TD = "td";
    public static final String TR = "tr";
    protected Record m_record = null;
    protected boolean startTable = false;
    protected int col = 0;
    protected int row = 0;
    /**
     * Default constructor.
     */
    public SaxHtmlHandler()
    {
        super();
    }
    /**
     * SaxHtmlHandler Method.
     */
    public SaxHtmlHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Init Method.
     */
    public void init(Record record)
    {
        m_record = record;
    }
    /**
     * StartDocument Method.
     */
    public void startDocument(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException
    {
        startTable = false;
        col = 0;
        row = 0;
    }
    /**
     * EndDocument Method.
     */
    public void endDocument(String namespaceURI, String localName, String qName) throws SAXException
    {
        // No code
    }
    /**
     * StartElement Method.
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes attr)
    {
        if (localName.equalsIgnoreCase(TABLE))
        {
            String strClass = attr.getValue("", "class");
            if (strClass != null)
                if (strClass.equalsIgnoreCase("table-in"))  // NO NO NO
                {
                    startTable = true;
                    row = -1;
                }
        }
        else if (startTable)
        {
            if (localName.equalsIgnoreCase(TR))
            {
                row++;
                col = -1;
            }
            else if (localName.equalsIgnoreCase(TD))
            {
                col++;
            }
        }
    }
    /**
     * EndElement Method.
     */
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException
    {
        if (localName.equalsIgnoreCase(TABLE))
        {
            if (startTable)
                if (m_record.getEditMode() == DBConstants.EDIT_ADD)
                {
                    try {
                        m_record.add();
                    } catch (DBException e) {
                        e.printStackTrace();
                    }
                }
            startTable = false;
        }
    }
    /**
     * Characters Method.
     */
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        if (startTable)
        {
            try {
                String string = new String(ch, start, length);
                for (int i = string.length() - 1; i >= 0; i--)
                {   // Trim trailing spaces
                    int x = Character.getNumericValue(string.charAt(i));
                    if ((Character.isWhitespace(string.charAt(i))) || (x == -1))
                        string = string.substring(0, string.length() - 1);
                    else
                        break;
                }
                if (row == 0)
                {
                    new StringField(m_record, string, -1, string, null);
                }
                else
                {
                    if (col == 0)
                    {
                        if (m_record.getEditMode() == DBConstants.EDIT_ADD)
                            m_record.add();
                        m_record.addNew();
                    }
                    m_record.getField(col + 1).setString(string);
                }
            } catch (DBException e) {
                e.printStackTrace();
            }
        }
    }

}
